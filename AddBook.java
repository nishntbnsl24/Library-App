package com.example.nishant.libarayapp;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class AddBook extends AppCompatActivity {

    Button addbook;
    EditText booknamebox, bookauthorbox, booksubjectbox, bookvaliditybox, bookquantitybox;
    ImageView bookpic, cameraicon, galleryicon;
    Uri bookuri;
    Uri imagepath;
    File myfolder, myfile, imageFolder;
    String picuri;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        this.setTitle("Add Book");
        booknamebox = findViewById(R.id.booknamebox);
        bookauthorbox = findViewById(R.id.bookauthorbox);
        booksubjectbox = findViewById(R.id.booksubjectbox);
        bookvaliditybox = findViewById(R.id.bookvaliditybox);
        bookquantitybox = findViewById(R.id.bookquantitybox);
        bookpic = findViewById(R.id.bookicon);
        addbook = findViewById(R.id.addbookbutton);
        picuri = bookpic.toString();
        if (booknamebox.getText().toString().isEmpty()) {
            booknamebox.setError("Can't be empty");
        }
        bookpic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog mydialog = new Dialog(AddBook.this);
                mydialog.setContentView(R.layout.pickimagedialoglayout);
                mydialog.show();
                cameraicon = mydialog.findViewById(R.id.cameraicon);
                galleryicon = mydialog.findViewById(R.id.galleryicon);
                galleryicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 102);
                        mydialog.dismiss();
                    }
                });
                cameraicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkAndRequestPermissions()) {
                            myfolder = new File(Environment.getExternalStorageDirectory(), "libraryapp");
                            if (!myfolder.exists()) {
                                myfolder.mkdir();
                            }
                            myfile = new File(myfolder, new java.util.Date().getTime() + ".jpg");
                            imagepath = Uri.fromFile(myfile);
                            Intent myintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String authorities = BuildConfig.APPLICATION_ID + ".provider";
                            Uri imageUri = FileProvider.getUriForFile(AddBook.this, authorities, myfile);
                            myintent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(myintent, 100);
                        } else {
                            Toast.makeText(AddBook.this, "First Give permissions for Camera to work ", Toast.LENGTH_SHORT).show();
                            checkAndRequestPermissions();
                        }
                        mydialog.dismiss();
                    }
                });
            }
        });
        addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookname, bookauthor, booksubject, bookpicuri;
                int bookvalidity, bookquantity;
                try {
                    bookname = booknamebox.getText().toString();
                    bookauthor = bookauthorbox.getText().toString();
                    booksubject = booksubjectbox.getText().toString();
                    bookvalidity = Integer.parseInt(bookvaliditybox.getText().toString());
                    bookquantity = Integer.parseInt(bookquantitybox.getText().toString());
                    SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                    mydb.execSQL("insert into books(book_name,book_author,book_subject,book_validity,book_quantity,book_image) values(?,?,?,?,?,?)", new Object[]{bookname, bookauthor, booksubject, bookvalidity, bookquantity, picuri});
                    Toast.makeText(AddBook.this, "Book added", Toast.LENGTH_SHORT).show();
                    AddBook.this.finish();
                } catch (Exception e) {
                    Toast.makeText(AddBook.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                bookpic.setImageURI(imagepath);
                picuri = imagepath.toString();
            }
        } else if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                bookpic.setImageURI(selectedImage);
                picuri = selectedImage.toString();
                imageFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "libraryapp");
                if (!imageFolder.exists()) {
                    imageFolder.mkdirs();
                }
                if (imageFolder.exists()) {
                    String filename = "img" + new java.util.Date().getTime() + ".jpg";
                    File myimage = new File(imageFolder, filename);
                    picuri = (Uri.fromFile(new File(myimage.getPath()))).toString();
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

        String[] proj = {MediaStore.Video.Media.DATA};
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
        if (requestCode == 1) {
            boolean flag = false;
            if (grantResults.length > 0) {
                if (grantResults.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        flag = true;
                    }
                } else if (grantResults.length == 2) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        flag = true;
                    }
                } else if (grantResults.length == 3) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        flag = true;
                    }
                }
                if (flag) {
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
                } else {
                    Toast.makeText(this, "Please give all permissions", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please give all permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkAndRequestPermissions() {
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
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }
}
