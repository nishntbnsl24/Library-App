package com.example.nishant.libarayapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.Calendar.DATE;

public class BookActivity extends AppCompatActivity {

    Button issuebutton,idissuebuton;
    EditText idinput;
    String id,book_id,bookauthor;
    ImageView bookimage,cameraicon,galleryicon;
    File myfolder,myfile;
    Uri imagepath;
    Uri shareuri;
    int bookavailstatus=1;
    int idstatus=0;
    TextView booknamebox,bookauthorbox,bookvaliditybox,bookavailabilitybox;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        ConstraintLayout your_Layout = (ConstraintLayout) findViewById(R.id.conslayout1);
        AnimationDrawable animationDrawable = (AnimationDrawable) your_Layout.getBackground();
        animationDrawable.setEnterFadeDuration(4000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        //Toast.makeText(BookActivity.this,getIntent().getExtras().getString("studentid"),Toast.LENGTH_SHORT).show();
        if(getIntent().getExtras().getString("studentid")==null)
        {
            idstatus=0;
        }
        else
        {
            idstatus=1;
        }
        bookimage=findViewById(R.id.bookimage);
        booknamebox=findViewById(R.id.booknamebox);
        bookauthorbox=findViewById(R.id.bookauthorbox);
        bookavailabilitybox=findViewById(R.id.bookavailabilitybox);
        bookvaliditybox=findViewById(R.id.bookvaliditybox);
        book_id=getIntent().getExtras().getString("bookname");
        try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books where book_name=?",new String[]{book_id});
            if(myresult.moveToNext())
            {
                bookauthor=myresult.getString(myresult.getColumnIndex("book_author"));
                booknamebox.setText(myresult.getString(myresult.getColumnIndex("book_name")));
                BookActivity.this.setTitle(myresult.getString(myresult.getColumnIndex("book_name")));
                bookauthorbox.setText(myresult.getString(myresult.getColumnIndex("book_author")));
                bookvaliditybox.setText(myresult.getString(myresult.getColumnIndex("book_validity")));
                if(myresult.getInt(myresult.getColumnIndex("book_quantity"))>0)
                {
                    bookavailabilitybox.setText("Available");
                    bookavailabilitybox.setTextColor(Color.GREEN);
                    bookavailstatus=1;
                }
                else
                {
                    bookavailabilitybox.setText("Not Available");
                    bookavailabilitybox.setTextColor(Color.RED);
                    bookavailstatus=0;
                }
                bookimage.setImageURI(Uri.parse(myresult.getString(myresult.getColumnIndex("book_image"))));
                shareuri=Uri.parse(myresult.getString(myresult.getColumnIndex("book_image")));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(BookActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        issuebutton=findViewById(R.id.issuebutton);
        if(bookavailstatus==0)
        {
            issuebutton.setEnabled(false);
        }
        issuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idstatus == 1) {
                    if (ActivityCompat.checkSelfPermission(BookActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BookActivity.this, new String[]{Manifest.permission.SEND_SMS}, 101);
                        return;
                    } else {
                    id = getIntent().getExtras().getString("studentid");
                    try {
                        SmsManager smsmanager = SmsManager.getDefault();
                        SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                        Cursor myresult = mydb.rawQuery("select * from books where book_name='" + book_id + "'", null);
                        if (myresult.moveToNext()) {
                            String bookid = myresult.getString(myresult.getColumnIndex("book_name"));
                            String retudate = myresult.getString(myresult.getColumnIndex("book_validity"));
                            Cursor mystudentresult = mydb.rawQuery("select * from students where student_id=" + id, null);
                            if (mystudentresult.moveToNext()) {
                                String phone = mystudentresult.getString(mystudentresult.getColumnIndex("student_phone"));
                                String stuname = mystudentresult.getString(mystudentresult.getColumnIndex("student_name"));
                                java.util.Date cdate = new java.util.Date();
                                SimpleDateFormat myformat = new SimpleDateFormat("dd-MM-yyyy");
                                SimpleDateFormat normalformat = new SimpleDateFormat("E,dd-MM-yyyy");
                                Date returnDate = addDays(cdate, Integer.parseInt(retudate));
                                mydb.execSQL("update books set book_quantity=book_quantity-1 where book_name ='"+book_id+"'");
                                mydb.execSQL("insert into issuetable (issue_id,student_id,student_name,book_sr_no,issue_date,due_date) values (?,?,?,?,?,?)", new Object[]{(int) (1000000 * Math.random()), id, stuname, book_id, myformat.format(cdate), myformat.format(returnDate)});
                                smsmanager.sendTextMessage("tel:" + phone, null, "Dear " + stuname + ", " + bookid + " has been issued. Return date is " + normalformat.format(returnDate), null, null);
                                Toast.makeText(BookActivity.this, "Book issued", Toast.LENGTH_SHORT).show();
                                BookActivity.this.finish();
                            } else {
                                Toast.makeText(BookActivity.this, "No student found with this id", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(BookActivity.this, "No book found with this name", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(BookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                }
                else
                {
                final Dialog askforstudentid = new Dialog(BookActivity.this);
                askforstudentid.setContentView(R.layout.inputidlayout);
                askforstudentid.show();
                idissuebuton = askforstudentid.findViewById(R.id.idissuebutton);
                idinput = askforstudentid.findViewById(R.id.idissueinput);
                idissuebuton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SmsManager smsmanager = SmsManager.getDefault();
                        if (ActivityCompat.checkSelfPermission(BookActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(BookActivity.this, new String[]{Manifest.permission.SEND_SMS}, 101);
                            return;
                        } else {
                            id = idinput.getText().toString();
                            try {
                                SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                                Cursor myresult = mydb.rawQuery("select * from books where book_name='" + book_id + "'", null);
                                if (myresult.moveToNext()) {
                                    String bookid = myresult.getString(myresult.getColumnIndex("book_name"));
                                    String retudate = myresult.getString(myresult.getColumnIndex("book_validity"));
                                    Cursor mystudentresult = mydb.rawQuery("select * from students where student_id=" + id, null);
                                    if (mystudentresult.moveToNext()) {
                                        String phone = mystudentresult.getString(mystudentresult.getColumnIndex("student_phone"));
                                        String stuname = mystudentresult.getString(mystudentresult.getColumnIndex("student_name"));
                                        java.util.Date cdate = new java.util.Date();
                                        SimpleDateFormat myformat = new SimpleDateFormat("dd-MM-yyyy");
                                        SimpleDateFormat normalformat = new SimpleDateFormat("E,dd-MM-yyyy");
                                        Date returnDate = addDays(cdate, Integer.parseInt(retudate));
                                        mydb.execSQL("update books set book_quantity=book_quantity-1 where book_name ='"+book_id+"'");
                                        mydb.execSQL("insert into issuetable (issue_id,student_id,student_name,book_sr_no,issue_date,due_date) values (?,?,?,?,?,?)", new Object[]{(int) (1000000 * Math.random()), id, stuname, book_id, myformat.format(cdate), myformat.format(returnDate)});
                                        smsmanager.sendTextMessage("tel:" + phone, null, "Dear " + stuname + ", " + bookid + " has been issued. Due date is " + normalformat.format(returnDate), null, null);
                                        Toast.makeText(BookActivity.this, "Book issued", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(BookActivity.this, "No student found with this id", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(BookActivity.this, "No book found with this name", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(BookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        askforstudentid.dismiss();
                        BookActivity.this.finish();
                    }
                });
            }
                try
                {
                    SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
                    Cursor myresult=mydb.rawQuery("select * from books where book_name=?",new String[]{book_id});
                    if(myresult.moveToNext())
                    {
                        booknamebox.setText(myresult.getString(myresult.getColumnIndex("book_name")));
                        bookauthorbox.setText(myresult.getString(myresult.getColumnIndex("book_author")));
                        bookvaliditybox.setText(myresult.getString(myresult.getColumnIndex("book_validity")));
                        if(myresult.getInt(myresult.getColumnIndex("book_quantity"))>0)
                        {
                            bookavailabilitybox.setText("Available");
                            bookavailabilitybox.setTextColor(Color.GREEN);
                            bookavailstatus=1;
                        }
                        else
                        {
                            bookavailabilitybox.setText("Not Available");
                            bookavailabilitybox.setTextColor(Color.RED);
                            bookavailstatus=0;
                            issuebutton.setEnabled(false);
                        }
                        bookimage.setImageURI(Uri.parse(myresult.getString(myresult.getColumnIndex("book_image"))));
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(BookActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
        }
        });
    }
    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"Share");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1)
        {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, book_id);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, book_id+" by "+bookauthor);
            sharingIntent.putExtra(Intent.EXTRA_STREAM,shareuri);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onResume() {
        try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books where book_name=?",new String[]{book_id});
            if(myresult.moveToNext())
            {
                booknamebox.setText(myresult.getString(myresult.getColumnIndex("book_name")));
                bookauthorbox.setText(myresult.getString(myresult.getColumnIndex("book_author")));
                bookvaliditybox.setText(myresult.getString(myresult.getColumnIndex("book_validity")));
                if(myresult.getInt(myresult.getColumnIndex("book_quantity"))>0)
                {
                    bookavailabilitybox.setText("Available");
                    bookavailabilitybox.setTextColor(Color.GREEN);
                    bookavailstatus=1;
                }
                else
                {
                    bookavailabilitybox.setText("Not Available");
                    bookavailabilitybox.setTextColor(Color.RED);
                    bookavailstatus=0;
                    issuebutton.setEnabled(false);
                }
                bookimage.setImageURI(Uri.parse(myresult.getString(myresult.getColumnIndex("book_image"))));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(BookActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                bookimage.setImageURI(imagepath);

            }
        } else if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    bookimage.setImageURI(selectedImage);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == 101)
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(idstatus==1)
                    {
                        id=getIntent().getExtras().getString("studentid");
                    }
                    else
                    {
                        id=idinput.getText().toString();
                    }
                    SmsManager smsmanager=SmsManager.getDefault();
                    try {
                        SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                        Cursor myresult = mydb.rawQuery("select * from books where book_name='" + book_id+"'", null);
                        if (myresult.moveToNext()) {
                            String bookid=myresult.getString(myresult.getColumnIndex("book_name"));
                            String retudate=myresult.getString(myresult.getColumnIndex("book_validity"));
                            Cursor mystudentresult = mydb.rawQuery("select * from students where student_id="+id , null);
                            if (mystudentresult.moveToNext()) {
                                String phone=mystudentresult.getString(mystudentresult.getColumnIndex("student_phone"));
                                String stuname=mystudentresult.getString(mystudentresult.getColumnIndex("student_name"));
                                java.util.Date cdate=new java.util.Date();
                                SimpleDateFormat myformat=new SimpleDateFormat("dd-MM-yyyy");
                                SimpleDateFormat normalformat=new SimpleDateFormat("E,dd-MM-yyyy");
                                Date returnDate = addDays(cdate, Integer.parseInt(retudate));
                                mydb.execSQL("update books set book_quantity=book_quantity-1 where book_name ='"+book_id+"'");
                                mydb.execSQL("insert into issuetable (issue_id,student_id,student_name,book_sr_no,issue_date,due_date) values (?,?,?,?,?)",new Object[]{(int)(1000000*Math.random()),id,stuname,book_id,myformat.format(cdate),myformat.format(returnDate)});
                                smsmanager.sendTextMessage("tel:"+phone,null,"Dear "+stuname+", "+bookid+" has been issued. Due date is "+normalformat.format(returnDate),null,null);
                                Toast.makeText(BookActivity.this, "Book issued", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(BookActivity.this, "No student found with this id", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(BookActivity.this, "No book found with this name", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(BookActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BookActivity.this, "Permission not given", Toast.LENGTH_SHORT).show();
                }
            }
            else if(requestCode==1) {

                boolean flag=false;
                if (grantResults.length > 0)
                {
                    if(grantResults.length==1)
                    {
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        {
                            flag=true;
                        }
                    }
                    else if(grantResults.length==2)
                    {
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
                        {
                            flag=true;
                        }
                    }
                    else if(grantResults.length==3)
                    {
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED)
                        {
                            flag=true;
                        }
                    }
                    if(flag) {
                        Intent myintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        myfolder = new File(Environment.getExternalStorageDirectory(), "gtb");
                        if (!myfolder.exists()) {
                            myfolder.mkdir();
                        }
                        myfile = new File(myfolder, new java.util.Date().getTime() + ".jpg");
                        imagepath = Uri.fromFile(myfile);
                        String authorities = BuildConfig.APPLICATION_ID + ".provider";
                        Uri imageUri = FileProvider.getUriForFile(this, authorities, myfile);
                        myintent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(myintent, 100);
                    }
                    else
                    {
                        Toast.makeText(this, "Please give all permissions", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Please give all permissions", Toast.LENGTH_SHORT).show();

                }

            }

        }
        private boolean checkAndRequestPermissions()
        {
            ArrayList<String> listPermissionsNeeded = new ArrayList<>();
            int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int writestorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readstorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (writestorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (readstorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
                return false;
            }
            return true;
        }
    }
