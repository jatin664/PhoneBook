package com.example.jatin.phonebook;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Call extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabase;
    TextView textView;
    Button b1,b2,b3,b4;
    Cursor c,cc;
    EditText editText;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        //getExtra
        String s=getIntent().getStringExtra("s");

        //SQL
        sqLiteDatabase = openOrCreateDatabase("contacts", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists usercontactsdetail(name varchar,company varchar,title varchar,mobiletag varchar,mobile varchar,emailtag varchar,email varchar,nickname varchar,website varchar,notes varchar)");
        c=sqLiteDatabase.rawQuery("select name,mobiletag,mobile,emailtag,email,website,notes from usercontactsdetail where name='"+s+"'",null);
        c.moveToFirst();

        //Id's
        textView=(TextView)findViewById(R.id.nametxt);
        b1=(Button)findViewById(R.id.btn1);
        b2=(Button)findViewById(R.id.btn2);
        b3=(Button)findViewById(R.id.btn3);
        b4=(Button)findViewById(R.id.btn4);
        editText=(EditText)findViewById(R.id.notesend);
        linearLayout=(LinearLayout)findViewById(R.id.ll);

        textView.setText(c.getString(0));
        String temp=c.getString(0);
        b4.setTag(temp);

        //Phone number conditions
        if(c.getString(1).equals("Mobile")){
            b1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mobiletag,0,0,0);
        }
        if(c.getString(1).equals("Work")){
            b1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.worktag,0,0,0);
        }
        if(c.getString(1).equals("Home")){
            b1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hometag,0,0,0);
        }
        b1.setText(c.getString(2));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        //Long press to copy to clipboard
        b1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData=ClipData.newPlainText("number",c.getString(2));
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(Call.this,"Copied to clipboard",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //Email Conditions
        if(c.getString(3).equals("Work")){
            b2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.worktag,0,0,0);
        }
        if(c.getString(3).equals("Home")){
            b2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hometag,0,0,0);
        }
        if(c.getString(3).equals("Other")){
            b2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personaltag,0,0,0);
        }

        if(c.getString(4).isEmpty()){
            b2.setEnabled(false);
            b2.setText("<Empty>");
        }

        if(c.getString(4).isEmpty()==false){
            b2.setText(c.getString(4));
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s[]={c.getString(4)};
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("message/rfc822");
                    email.putExtra(Intent.EXTRA_EMAIL,s);
                    startActivity(Intent.createChooser(email,"Only choose an email client"));
                }
            });
            b2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("number",c.getString(4));
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(Call.this,"Copied to clipboard",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        //Website
        if(c.getString(5).isEmpty()){
            b3.setEnabled(false);
            b3.setText("<Empty>");
        }

        if(c.getString(5).isEmpty()==false){
            b3.setText(c.getString(5));
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i2=new Intent(Intent.ACTION_VIEW,Uri.parse("http://"+b3.getText().toString()));
                    startActivity(i2);
                }
            });
            b3.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("number",c.getString(5));
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(Call.this,"Copied to clipboard",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        //Notes
        if(c.getString(6).isEmpty()==false){
            editText.setText(c.getString(6));
        }
        if(c.getString(6).isEmpty()){
            editText.setHint("Notes : <Empty>");
        }
    }
    public void delClick(View view){
            sqLiteDatabase.execSQL("DELETE FROM usercontactsdetail" + " WHERE name = '"+c.getString(0)+ "'" + " AND mobile" + " ='" + c.getString(2) + "'");
            Toast.makeText(this, "Contact Deleted", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Call.this, MainActivity.class);
                    startActivity(intent);
                }
            }, 500);
    }
    public void editClick(View view){
        String m=view.getTag().toString();
        Intent intent=new Intent(this,AddContact.class);
        intent.putExtra("ss",m);
        startActivity(intent);
    }

    public void callClick(View v){
         try {
                    Intent callIntent = new Intent((Intent.ACTION_CALL));
                    callIntent.setData(Uri.parse("tel:" + b1.getText().toString()));
                    startActivity(callIntent);
         } catch (SecurityException e){}
    }

    public void cancelClick(View v){
        linearLayout.setVisibility(View.GONE);
    }

    public void messageClick(View view){
        final EditText editText=new EditText(this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50,5,50,5);
        editText.setLayoutParams(layoutParams);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Message");
        alertDialog.setIcon(R.drawable.message);
        alertDialog.setView(editText);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SmsManager.getDefault().sendTextMessage(c.getString(2),null,editText.getText().toString(),null,null);
                Toast.makeText(Call.this,"Sending message...",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
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
}
