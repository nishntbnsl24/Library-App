package com.example.nishant.libarayapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminBookActivity extends AppCompatActivity {


    ArrayList<String> booknames=new ArrayList<String>();
    ArrayList<Integer> ids=new ArrayList<Integer>();
    ArrayList<String> picuris=new ArrayList<String>();
    ArrayList<String> subjectnames=new ArrayList<String>();
    ArrayList<String> authornames=new ArrayList<String>();
    ArrayList<Integer> quantities=new ArrayList<Integer>();
    ArrayList<Integer> validities=new ArrayList<Integer>();
    ListView books;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_book);
        this.setTitle("Books");
        books=findViewById(R.id.books);
        this.registerForContextMenu(books);
        books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    //Toast.makeText(AdminBookActivity.this,ids.get(position)+"",Toast.LENGTH_SHORT).show();
                    Intent book=new Intent(AdminBookActivity.this,BookActivity.class);
                    book.putExtra("bookname",(booknames.get(position)+""));
                    startActivity(book);
                }
                catch (Exception e)
                {
                    Toast.makeText(AdminBookActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent addbook=new Intent(AdminBookActivity.this,AddBook.class);
                startActivity(addbook);
            }
        });
    }
    public void fetchvalues()
    {
        try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books",null);
            if(myresult.moveToNext())
            {
                do
                {
                    booknames.add(myresult.getString(myresult.getColumnIndex("book_name")));
                    authornames.add(myresult.getString(myresult.getColumnIndex("book_author")));
                    quantities.add(myresult.getInt(myresult.getColumnIndex("book_quantity")));
                    picuris.add(myresult.getString(myresult.getColumnIndex("book_image")));
                    //ids.add(Integer.parseInt(myresult.getString(myresult.getColumnIndex("book_sr_no"))));
                }
                while(myresult.moveToNext());
            }
        }
        catch (Exception e)
        {
            Toast.makeText(AdminBookActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(1,1,1,"Delete Book");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        if (item.getItemId() == 1) {
            AlertDialog.Builder mybuilder=new AlertDialog.Builder(AdminBookActivity.this);
            mybuilder.setMessage("Do you really want to delete book?");
            mybuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                        mydb.execSQL("delete from books where book_name='" + booknames.get(position)+"'");
                        Toast.makeText(AdminBookActivity.this, "Book deleted", Toast.LENGTH_SHORT).show();
                        onResume();
                    } catch (Exception e) {
                        Toast.makeText(AdminBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog mydialog=mybuilder.create();
            mydialog.show();
            }
        return super.onContextItemSelected(item);
    }
    protected void onResume() {
        booknames.clear();
        authornames.clear();
        picuris.clear();
        quantities.clear();
        fetchvalues();
        AdminBookActivity.myhelperclass obj=new AdminBookActivity.myhelperclass(AdminBookActivity.this,android.R.layout.simple_list_item_1,booknames.toArray(new String[booknames.size()]));
        books.setAdapter(obj);
        super.onResume();
    }
    public class myhelperclass extends ArrayAdapter<String>
    {
        public myhelperclass(Context context, int resource, String[] objects)
        {
            super(context,resource,objects);
        }
        public View getView(int positon, View convertview, ViewGroup parent) {
            View myrow = getLayoutInflater().inflate(R.layout.booklistdesign, parent, false);
            ImageView booklistimagebox;
            TextView booknamelisttextbox,bookauthorlisttextbox,bookavailabilitylisttextbox;
            booklistimagebox = myrow.findViewById(R.id.booklistimagebox);
            booknamelisttextbox = myrow.findViewById(R.id.booknamelisttextbox);
            bookauthorlisttextbox=myrow.findViewById(R.id.bookauthorlisttextbox);
            bookavailabilitylisttextbox=myrow.findViewById(R.id.bookavailabilitylisttextbox);
            booknamelisttextbox.setText(booknames.get(positon));
            bookauthorlisttextbox.setText(authornames.get(positon));
            booklistimagebox.setImageURI(Uri.parse(picuris.get(positon)));
            if(quantities.get(positon)>0)
            {
                bookavailabilitylisttextbox.setText("Available");
                bookavailabilitylisttextbox.setTextColor(Color.GREEN);
            }
            else
            {
                bookavailabilitylisttextbox.setText("Not Available");
                bookavailabilitylisttextbox.setTextColor(Color.RED);
            }
            return myrow;
        }
    }
}
