package com.example.jatin.phonebook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class SearchResult extends AppCompatActivity {
    TableLayout tableLayout;
    SQLiteDatabase sqLiteDatabase;
    Cursor c;
    String call;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        //id's
        tableLayout = (TableLayout) findViewById(R.id.tll);

        String s = getIntent().getStringExtra("srch");


        //SQLLite
        sqLiteDatabase = openOrCreateDatabase("contacts", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists usercontactsdetail(name varchar,company varchar,title varchar,mobiletag varchar,mobile varchar,emailtag varchar,email varchar,nickname varchar,website varchar,notes varchar)");

        //Cursor
        c = sqLiteDatabase.rawQuery("select name,mobile from usercontactsdetail where name='"+s+"' order by name COLLATE NOCASE", null);

        if (c.moveToFirst()) {
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
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.contactdrawable, 0, 0, 0);
                button.setCompoundDrawablePadding(40);
                call = c.getString(1);

                //On long press to call
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + call));
                            startActivity(intent);
                        } catch (SecurityException e) {
                        }
                        return true;
                    }
                });

                //Thin line
                View view = new View(this);
                TableRow.LayoutParams lp4 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 3);
                lp4.setMargins(0, 0, 0, 30);
                view.setLayoutParams(lp4);
                view.setBackgroundColor(Color.parseColor("#E5E5E5"));

                //Adding views
                tableRow1.addView(button);
                tableRow2.addView(view);

                //Jump to call activity
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SearchResult.this, Call.class);
                        intent.putExtra("s", temp);
                        startActivity(intent);
                    }
                });

                tableLayout.addView(tableRow1);
                tableLayout.addView(tableRow2);
            } while (c.moveToNext());
        }
    }
        @Override
        public void onBackPressed () {
            super.onBackPressed();
            startActivity(new Intent(this, MainActivity.class));
        }
}
