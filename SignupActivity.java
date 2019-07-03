package com.example.nishant.libarayapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    Button signupbutton;
    TextView systemgeneratedid,dobbox;
    ImageView profilepic,cameraicon,galleryicon,dateicon;
    File myfolder,myfile,imageFolder;
    Uri imagepath;
    int id=(int)(1000000*Math.random());
    int picchangedstatus=0;
    EditText namebox,passwordbox,emailbox,phonebox;
    String name,password,email,phone,dob,picuri,stuid;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.setTitle("Signup");
        signupbutton=findViewById(R.id.signupbutton);
        systemgeneratedid=findViewById(R.id.systemgeneratedid);
        dateicon=findViewById(R.id.dateicon);
        dobbox=findViewById(R.id.dobbox);
        namebox=findViewById(R.id.name);
        passwordbox=findViewById(R.id.password);
        emailbox=findViewById(R.id.email);
        phonebox=findViewById(R.id.phone);
        profilepic=findViewById(R.id.profilepic);
        picuri=profilepic.toString();
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mydialog=new Dialog(SignupActivity.this);
                mydialog.setContentView(R.layout.pickimagedialoglayout);
                mydialog.show();
                cameraicon=mydialog.findViewById(R.id.cameraicon);
                galleryicon=mydialog.findViewById(R.id.galleryicon);
                galleryicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 102);
                        mydialog.dismiss();
                        picchangedstatus=1;
                    }
                });
                cameraicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkAndRequestPermissions()) {
                            myfolder = new File(Environment.getExternalStorageDirectory(), "libraryapp");
                            if (!myfolder.exists()) {
                                myfolder.mkdir();
                            }
                            myfile = new File(myfolder, new java.util.Date().getTime() + ".jpg");
                            imagepath = Uri.fromFile(myfile);
                            Intent myintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String authorities = BuildConfig.APPLICATION_ID + ".provider";
                            Uri imageUri = FileProvider.getUriForFile(SignupActivity.this, authorities, myfile);
                            myintent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(myintent, 100);
                            picchangedstatus=1;
                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this, "First Give permissions for Camera to work ", Toast.LENGTH_SHORT).show();
                            checkAndRequestPermissions();
                        }
                        mydialog.dismiss();
                    }
                });
            }
        });
        dateicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mydialog = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        String month = (selectedMonth+1)+"",day=selectedDay+"";
                        if (selectedMonth < 10) {
                            month = "0" + (selectedMonth+1);
                        }
                        if(selectedDay < 10)
                        {
                            day="0"+selectedDay;
                        }
                        dobbox.setText(day + "-" + month + "-" + selectedYear);
                    }
                }, 2018, 07, 7);
                mydialog.getDatePicker().setMaxDate(new java.util.Date().getTime());
                mydialog.show();
            }
        });
        dobbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mydialog = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        String month = (selectedMonth+1)+"",day=selectedDay+"";
                        if (selectedMonth < 10) {
                            month = "0" + (selectedMonth+1);
                        }
                        if(selectedDay < 10)
                        {
                            day="0"+selectedDay;
                        }
                        dobbox.setText(day + "-" + month + "-" + selectedYear);
                    }
                }, 2018, 07, 7);
                mydialog.getDatePicker().setMaxDate(new java.util.Date().getTime());
                mydialog.show();
            }
        });
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(getIntent().getExtras()==null) {
                    SignupActivity.this.setTitle("Signup");
                    name = namebox.getText().toString();
                    password = passwordbox.getText().toString();
                    email = emailbox.getText().toString();
                    phone = phonebox.getText().toString();
                    dob = dobbox.getText().toString();
                    stuid=id+"";
                    try {
                        SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                        mydb.execSQL("insert into students(student_id,student_name,student_password,student_email,student_phone,student_dob,student_image) values (?,?,?,?,?,?,?)",
                                new Object[]{id, name, password, email, phone, dob, picuri});
                        String number=phone;
                        SmsManager smsmanager=SmsManager.getDefault();
                        if(ActivityCompat.checkSelfPermission(SignupActivity.this,Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(SignupActivity.this,new String[]{Manifest.permission.SEND_SMS},101);
                            return;
                        }
                        else
                        {
                            smsmanager.sendTextMessage("tel:"+number,null,"Library Account created with id: "+stuid,null,null);
                            //Toast.makeText(SignupActivity.this,"Message Sent",Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    SignupActivity.this.finish();
                    Toast.makeText(SignupActivity.this, "Student Record Created", Toast.LENGTH_SHORT).show();
                }
                else
               {
                   try
                   {
                       SignupActivity.this.setTitle("Update");
                       signupbutton.setText("Update");
                       SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
                       Cursor myresult=mydb.rawQuery("select * from students where student_id=?",new String[]{getIntent().getExtras().getString("student_id")});
                       if(myresult.moveToNext())
                       {
                           name = namebox.getText().toString();
                           password = passwordbox.getText().toString();
                           email = emailbox.getText().toString();
                           phone = phonebox.getText().toString();
                           dob = dobbox.getText().toString();
                           stuid=myresult.getString(myresult.getColumnIndex("student_id"));
                           if(picchangedstatus==0)
                           picuri=myresult.getString(myresult.getColumnIndex("student_image"));
                           try {
                               mydb.execSQL("update students set student_name=?,student_password=?,student_email=?,student_phone=?,student_dob=?,student_image=? where student_id=?",
                                       new Object[]{name, password, email, phone, dob, picuri,myresult.getString(myresult.getColumnIndex("student_id"))});
                           } catch (Exception e) {
                               Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                           SignupActivity.this.finish();
                           Toast.makeText(SignupActivity.this, "Student Record Updated", Toast.LENGTH_SHORT).show();
                       }
                   }
                   catch (Exception e)
                   {
                       Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                   }

               }
            }
        });
        if(getIntent().getExtras()!=null)
        {
                try
                {
                    SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
                    Cursor myresult=mydb.rawQuery("select * from students where student_id=?",new String[]{getIntent().getExtras().getString("student_id")});
                    if(myresult.moveToNext())
                    {
                        stuid=getIntent().getExtras().getString("student_id");
                        namebox.setText(myresult.getString(myresult.getColumnIndex("student_name")));
                        systemgeneratedid.setText(myresult.getString(myresult.getColumnIndex("student_id")));
                        emailbox.setText(myresult.getString(myresult.getColumnIndex("student_email")));
                        passwordbox.setText(myresult.getString(myresult.getColumnIndex("student_password")));
                        dobbox.setText(myresult.getString(myresult.getColumnIndex("student_dob")));
                        phonebox.setText(myresult.getString(myresult.getColumnIndex("student_phone")));
                        profilepic.setImageURI(Uri.parse(myresult.getString(myresult.getColumnIndex("student_image"))));
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                signupbutton.setText("Update");
        }
        else
        {
            systemgeneratedid.setText(id+"");
            signupbutton.setText("Signup");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                profilepic.setImageURI(imagepath);
                picuri=imagepath.toString();
            }
        } else if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                profilepic.setImageURI(selectedImage);
                picuri=selectedImage.toString();
                imageFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "libraryapp");
                if(!imageFolder.exists())
                {
                    imageFolder.mkdirs();
                }
                if(imageFolder.exists())
                {
                    String filename="img" + new java.util.Date().getTime() + ".jpg";
                    File myimage =new File(imageFolder,filename);
                    picuri= (Uri.fromFile(new File(myimage.getPath()))).toString();
                    //myuri = Uri.fromFile(new File(myimage.getPath()));
                    try {
                        myimage.createNewFile();
                        copyFile(new File(getRealPathFromURI(data.getData())), myimage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
         if(requestCode==1) {
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
         else if (requestCode == 102)
             if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 String number = phonebox.getText().toString();
                 SmsManager smsmanager = SmsManager.getDefault();
                 smsmanager.sendTextMessage("tel:" + number, null, "Library Account created with id: "+stuid, null, null);
                 Toast.makeText(SignupActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
             } else {
                 Toast.makeText(SignupActivity.this, "Message Permission not given", Toast.LENGTH_SHORT).show();
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
