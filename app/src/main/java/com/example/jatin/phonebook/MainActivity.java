package com.example.jatin.phonebook;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.util.List;

public class MainActivity extends AppCompatActivity {
SQLiteDatabase sqLiteDatabase;
TableLayout tableLayout;
EditText editText;
Cursor c;
Intent intent;
String call;
int toastcount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int numContacts=0;
        setContentView(R.layout.activity_main);

        //Permission
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        //Id's
        tableLayout = (TableLayout) findViewById(R.id.tl);
        editText=(EditText)findViewById(R.id.srchbar);

        //Long Press Message
        if(toastcount==0) {
            Toast.makeText(this, "Use Long-Press to make an instant call", Toast.LENGTH_SHORT).show();
            toastcount++;
        }

        //SQLLite
        sqLiteDatabase = openOrCreateDatabase("contacts", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists usercontactsdetail(name varchar,company varchar,title varchar,mobiletag varchar,mobile varchar,emailtag varchar,email varchar,nickname varchar,website varchar,notes varchar)");

        //Cursor
        c = sqLiteDatabase.rawQuery("select name,mobile from usercontactsdetail order by name COLLATE NOCASE;", null);

        intent=new Intent(this,AddContact.class);
        if(c.moveToFirst()) {
            do {
                final String temp = c.getString(0);
                TableRow tableRow1 = new TableRow(this);
                TableRow tableRow2 = new TableRow(this);
                final Button button = new Button(this);


                if (temp.isEmpty()) {
                    button.setText(c.getString(1));
                } else {
                    button.setText(c.getString(0));
                }

                //Button attributes
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 150);
                lp.setMargins(20, 20, 0, 20);
                button.setLayoutParams(lp);
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setAllCaps(false);
                button.setTextSize(15);
                button.setPadding(5, 5, 0, 0);
                button.setBackgroundResource(R.drawable.mainborder);
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.contactdrawable, 0,0, 0);
                button.setCompoundDrawablePadding(40);
                call=c.getString(1);

                //On long press to call
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        try{
                            Intent intent=new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:"+call));
                            startActivity(intent);
                        }
                        catch (SecurityException e){}
                        return true;
                    }
                });

                //Thin line
                View view=new View(this);
                TableRow.LayoutParams lp4 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 3);
                lp4.setMargins(0,0,0,30);
                view.setLayoutParams(lp4);
                view.setBackgroundColor(Color.parseColor("#E5E5E5"));

                //Adding views
                tableRow1.addView(button);
                tableRow2.addView(view);

                //Jump to call activity
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, Call.class);
                        intent.putExtra("s", temp);
                        startActivity(intent);
                    }
                });

                tableLayout.addView(tableRow1);
                tableLayout.addView(tableRow2);
                numContacts++;
            } while (c.moveToNext());
        }

        else{
            final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
            alertDialog.setTitle("No Contacts");
            alertDialog.setMessage("Looks like u are clean,Press below buttons to take actions");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Contact", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(intent);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        //Search bar
        editText.setHint(String.valueOf(numContacts)+" Contacts");
        editText.setHintTextColor(Color.parseColor("#000000"));

    }

    public void addbtn(View v){
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
    public void searchBarClick(View v){
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    public void searchClick(View view){
        String s=editText.getText().toString();
        Intent intent=new Intent(this,SearchResult.class);
        intent.putExtra("srch",s);
        startActivity(intent);
    }
}
