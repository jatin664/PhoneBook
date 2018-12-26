package com.example.jatin.phonebook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddContact extends AppCompatActivity {
    int clickCount=0;
    EditText editText1,editText2,editText3;
    View view1,view2,view3;
    EditText editText4,editText5,editText6,editText7,editText8;
    SQLiteDatabase sqLiteDatabase;
    Spinner spinner1,spinner2;
    Button button;
    String m=null;
    Cursor c,c1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        //extra fields
        editText1=(EditText)findViewById(R.id.nicknameed);
        editText2=(EditText)findViewById(R.id.websiteed);
        editText3=(EditText)findViewById(R.id.notesed);

        //Top Fields

        editText4=(EditText)findViewById(R.id.nameed);
        editText5=(EditText)findViewById(R.id.companyed);
        editText6=(EditText)findViewById(R.id.titleed);
        editText7=(EditText)findViewById(R.id.phonenumed);
        editText8=(EditText)findViewById(R.id.emailed);

        //Spinner fields

        spinner1=(Spinner)findViewById(R.id.sp1);
        spinner2=(Spinner)findViewById(R.id.sp2);

        view1=(View)findViewById(R.id.nicknameline);
        view2=(View)findViewById(R.id.websiteline);
        view3=(View)findViewById(R.id.noetsline);

        button=(Button)findViewById(R.id.btn1);

        sqLiteDatabase=openOrCreateDatabase("contacts",MODE_PRIVATE,null);

        //Edit an contact
        try{
            Intent intent=getIntent();
            m=intent.getStringExtra("ss");
            if(m!=null) {
                editText1.setVisibility(View.VISIBLE);
                editText2.setVisibility(View.VISIBLE);
                editText3.setVisibility(View.VISIBLE);
            }
            //Set values to edit text boxes
            c=sqLiteDatabase.rawQuery("select name,company,title,mobile,email,nickname,website,notes from usercontactsdetail where name='"+m+"'",null);
            c.moveToFirst();
            editText4.setText(c.getString(0));
            editText5.setText(c.getString(1));
            editText6.setText(c.getString(2));
            editText7.setText(c.getString(3));
            editText8.setText(c.getString(4));

            editText1.setText(c.getString(5));
            editText2.setText(c.getString(6));
            editText3.setText(c.getString(7));

        }catch(Exception e){}
        sqLiteDatabase.execSQL("create table if not exists usercontactsdetail(name varchar,company varchar,title varchar,mobiletag varchar,mobile varchar,emailtag varchar,email varchar,nickname varchar,website varchar,notes varchar)");

        //Cursor c1 for cbecking if contact already exist

        c1 = sqLiteDatabase.rawQuery("select name,mobile from usercontactsdetail order by name asc ", null);
    }
    public void extraFields(View v) {
        clickCount++;
        if(clickCount <= 3){
            if (clickCount == 1) {
                editText1.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
            }
            else if (clickCount == 2) {
                editText2.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
            }
            else if (clickCount == 3) {
                editText3.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
            }
            else{
                clickCount=3;
                button.setClickable(true);
            }
        }
    }
    public void saveContact(View view) {
        String name = editText4.getText().toString();
        String company = editText5.getText().toString();
        String title = editText6.getText().toString();
        String mobiletag = spinner1.getSelectedItem().toString();
        String mobile = editText7.getText().toString();

        String emailtag = spinner2.getSelectedItem().toString();
        String email = editText8.getText().toString();
        String nickname = editText1.getText().toString();
        String website = editText2.getText().toString();
        String notes = editText3.getText().toString();

        //edit contact is not pressed
        if (m == null) {

            //if cursor has data then...
            if(c1.moveToFirst()) {
                //Check if contact already exist or not
                do {
                    if (name.equals(c1.getString(0)) && mobile.equals(c1.getString(1))) {
                        editText4.setError("Name with same phone number already existed");
                        editText7.setError("Name with same phone number already existed");
                        return;
                    }
                } while (c1.moveToNext());
            }

            //Errors
            if (TextUtils.isEmpty(mobile)) {
                editText7.setError("This field can't be left blanked");
                return;
            }

            sqLiteDatabase.execSQL("insert into usercontactsdetail values('" + name + "','" + company + "','" + title + "','" + mobiletag + "','" + mobile + "','" + emailtag + "','" + email + "','" + nickname + "','" + website + "','" + notes + "')");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(AddContact.this, MainActivity.class);
                    startActivity(intent);
                }
            }, 500);
        }
        //if edit contact is pressed
         else {
            if (TextUtils.isEmpty(mobile)) {
                editText7.setError("This field can't be left blanked");
                return;
            }
            sqLiteDatabase.execSQL("update usercontactsdetail set name='"+name+"',company='"+company+"',title='"+title+"',mobiletag='"+mobiletag+"',mobile='"+mobile+"',emailtag='"+emailtag+"',email='"+email+"',nickname='"+nickname+"',website='"+website+"',notes='"+notes+"' where name='"+m+"'");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(AddContact.this, MainActivity.class);
                    startActivity(intent);
                }
            }, 500);
        }
        Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
    }
}
