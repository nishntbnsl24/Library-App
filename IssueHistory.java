package com.example.nishant.libarayapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;

public class IssueHistory extends AppCompatActivity {

    ListView issuelist;
    ArrayList<String> booknames = new ArrayList<String>();
    ArrayList<String> returnstatuses = new ArrayList<String>();
    ArrayList<String> issueids = new ArrayList<String>();
    ArrayList<String> studentids = new ArrayList<String>();
    ArrayList<String> duedates = new ArrayList<String>();
    ArrayList<String> studentnames = new ArrayList<String>();
    ArrayList<String> issuedates = new ArrayList<String>();
    String student_id;
    String number,bookname;
    long fine=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Issue History");
        setContentView(R.layout.activity_issue_history);
        issuelist = findViewById(R.id.issuelist);
        this.registerForContextMenu(issuelist);
        if(getIntent().getExtras()!=null)
        {
            student_id=getIntent().getExtras().getString("studentid");
            booknames.clear();
            returnstatuses.clear();
            issueids.clear();
            studentids.clear();
            duedates.clear();
            issuedates.clear();
            studentnames.clear();
            fetchvalues(student_id);
            IssueHistory.myhelperclass obj=new IssueHistory.myhelperclass(IssueHistory.this,android.R.layout.simple_list_item_1,booknames.toArray(new String[booknames.size()]));
            issuelist.setAdapter(obj);
        }
    }

    public void fetchvalues() {
        try {
            SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
            Cursor myresult = mydb.rawQuery("select * from issuetable", null);
            if (myresult.moveToNext()) {
                do {
                    issueids.add(myresult.getString(myresult.getColumnIndex("issue_id")));
                    returnstatuses.add(myresult.getString(myresult.getColumnIndex("return_date")));
                    booknames.add(myresult.getString(myresult.getColumnIndex("book_sr_no")));
                    studentids.add(myresult.getString(myresult.getColumnIndex("student_id")));
                    duedates.add(myresult.getString(myresult.getColumnIndex("due_date")));
                    studentnames.add(myresult.getString(myresult.getColumnIndex("student_name")));
                    issuedates.add(myresult.getString(myresult.getColumnIndex("issue_date")));
                }
                while (myresult.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(IssueHistory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(1,1,1,"Return Book");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        java.util.Date cdate = new java.util.Date();
        SimpleDateFormat myformat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat normalformat = new SimpleDateFormat("E,dd-MM-yyyy");
        if (item.getItemId() == 1) {
            try {
                Date duedate=null,todaydate=null;
                try
                {
                    duedate=new SimpleDateFormat("dd-MM-yyyy").parse(duedates.get(position));
                    todaydate=new Date();
                    if(((duedate.getTime()-todaydate.getTime())/1000/60/60/24)>0)
                    {
                        fine=0;
                    }
                    else
                    {
                        fine=-((duedate.getTime()-todaydate.getTime())/1000/60/60/24);
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(IssueHistory.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                mydb.execSQL("update issuetable set return_date='"+myformat.format(cdate)+"', fine='"+(int)fine+"' where issue_id=" + issueids.get(position));
                mydb.execSQL("update books set book_quantity=book_quantity+1 where book_name='"+booknames.get(position)+"'");
                Cursor myresult = mydb.rawQuery("select * from students where student_id="+studentids.get(position), null);
                if (myresult.moveToNext())
                {
                    number=myresult.getString(myresult.getColumnIndex("student_phone"));
                    bookname=booknames.get(position);
                }
                    SmsManager smsmanager=SmsManager.getDefault();
                    if(ActivityCompat.checkSelfPermission(IssueHistory.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(IssueHistory.this,new String[]{Manifest.permission.SEND_SMS},101);
                        return super.onContextItemSelected(item);
                    }
                    else
                    {
                        smsmanager.sendTextMessage("tel:"+number,null,bookname+" returned on "+normalformat.format(cdate)+". Fine: Rs."+fine,null,null);
                        //Toast.makeText(SignupActivity.this,"Message Sent",Toast.LENGTH_LONG).show();
                    }

                Toast.makeText(IssueHistory.this, "Book returned", Toast.LENGTH_SHORT).show();
                onResume();
            } catch (Exception e) {
                Toast.makeText(IssueHistory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }
    public void fetchvalues(String stuid) {
        try {
            SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
            Cursor myresult = mydb.rawQuery("select * from issuetable where student_id="+Integer.parseInt(stuid), null);
            if (myresult.moveToNext()) {
                do {
                    issueids.add(myresult.getString(myresult.getColumnIndex("issue_id")));
                    returnstatuses.add(myresult.getString(myresult.getColumnIndex("return_date")));
                    booknames.add(myresult.getString(myresult.getColumnIndex("book_sr_no")));
                    studentids.add(myresult.getString(myresult.getColumnIndex("student_id")));
                    duedates.add(myresult.getString(myresult.getColumnIndex("due_date")));
                    studentnames.add(myresult.getString(myresult.getColumnIndex("student_name")));
                    issuedates.add(myresult.getString(myresult.getColumnIndex("issue_date")));
                }
                while (myresult.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(IssueHistory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    protected void onResume() {
        booknames.clear();
        returnstatuses.clear();
        issueids.clear();
        studentids.clear();
        duedates.clear();
        issuedates.clear();
        studentnames.clear();
        if(getIntent().getExtras()!=null)
        {
            student_id=getIntent().getExtras().getString("studentid");
            fetchvalues(student_id);
        }
        else
        {
            fetchvalues();
        }
        IssueHistory.myhelperclass obj=new IssueHistory.myhelperclass(IssueHistory.this,android.R.layout.simple_list_item_1,booknames.toArray(new String[booknames.size()]));
        issuelist.setAdapter(obj);
        super.onResume();
    }
    public class myhelperclass extends ArrayAdapter<String>
    {
        public myhelperclass(Context context, int resource, String[] objects)
        {
            super(context,resource,objects);
        }
        public View getView(int positon, View convertview, ViewGroup parent) {
            View myrow = getLayoutInflater().inflate(R.layout.issuehistorylistdesign, parent, false);
            TextView issueidlist,returnstatuslist,bookissuedlist,studentnamelist,duedatelist,issuedatelist,studentidlist;
            issueidlist=myrow.findViewById(R.id.issueidlist);
            returnstatuslist=myrow.findViewById(R.id.returnstatuslist);
            bookissuedlist=myrow.findViewById(R.id.bookissuedlist);
            studentnamelist=myrow.findViewById(R.id.studentnamelist);
            duedatelist=myrow.findViewById(R.id.duedatelist);
            issuedatelist=myrow.findViewById(R.id.issuedatelist);
            studentidlist=myrow.findViewById(R.id.studentidlist);
            issueidlist.setText(issueids.get(positon));
            if(returnstatuses.get(positon)==null)
            {
                returnstatuslist.setText("Not returned");
                returnstatuslist.setTextColor(Color.RED);
            }
            else
            {
                returnstatuslist.setText("returned on "+returnstatuses.get(positon));
                returnstatuslist.setTextColor(Color.GREEN);
            }
            bookissuedlist.setText(booknames.get(positon));
            studentnamelist.setText(studentnames.get(positon));
            Date duedate=null,todaydate=null;
            try
            {
                duedate=new SimpleDateFormat("dd-MM-yyyy").parse(duedates.get(positon));
                todaydate=new Date();
            }
            catch (Exception e)
            {
                Toast.makeText(IssueHistory.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            if(duedate.after(todaydate))
            {
                duedatelist.setTextColor(Color.GREEN);
            }
            else
            {
                duedatelist.setTextColor(Color.RED);
            }
            duedatelist.setText(duedates.get(positon));
            issuedatelist.setText(issuedates.get(positon));
            studentidlist.setText(studentids.get(positon));
            return myrow;
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 102)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                java.util.Date cdate = new java.util.Date();

                SimpleDateFormat normalformat = new SimpleDateFormat("E,dd-MM-yyyy");
                SmsManager smsmanager = SmsManager.getDefault();
                smsmanager.sendTextMessage("tel:"+number,null,bookname+" returned on "+normalformat.format(cdate)+". Fine: Rs."+fine,null,null);
            } else {
                Toast.makeText(IssueHistory.this, "Message Permission not given", Toast.LENGTH_SHORT).show();
            }

    }
}
