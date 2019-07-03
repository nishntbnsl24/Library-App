package com.example.nishant.libarayapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookSearchActivity extends AppCompatActivity {

    Spinner spinner;
    String[] booksearchbyoptions={"Search by","Name","Author","Subject","Validity"};
    Button searchbutton;
    ListView books;
    EditText searchbox;
    String search;
    String student_id;
    ArrayList<String> booknames=new ArrayList<String>();
    ArrayList<Integer> ids=new ArrayList<Integer>();
    ArrayList<String> picuris=new ArrayList<String>();
    ArrayList<String> subjectnames=new ArrayList<String>();
    ArrayList<String> authornames=new ArrayList<String>();
    ArrayList<Integer> quantities=new ArrayList<Integer>();
    ArrayList<Integer> validities=new ArrayList<Integer>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        if(getIntent().getExtras()!=null)
        {
            student_id=getIntent().getExtras().getString("studentid");
        }
        searchbox=findViewById(R.id.searchbox);
        books=findViewById(R.id.books);
        try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
//            mydb.execSQL("drop table students");
//            mydb.execSQL("drop table books");
//            mydb.execSQL("drop table issuetable");
            mydb.execSQL("create table if not exists students(student_id integer primary key,student_name varchar(1000),student_password varchar(1000)," +
                    "student_email varchar(1000),student_phone varchar(1000),student_dob date,student_image varchar(2000))");
            mydb.execSQL("create table if not exists books(book_name varchar(1000) primary key,book_author varchar(1000)," +
                    "book_subject varchar(1000),book_validity integer,book_quantity integer,book_image varchar(2000))");
            mydb.execSQL("create table if not exists issuetable(issue_id integer primary key,student_id integer,student_name varchar(1000),book_sr_no integer,issue_date date,due_date date,return_date date,fine integer)");
        }
        catch(Exception e)
        {
            Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        spinner=findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,booksearchbyoptions));
        searchbutton=findViewById(R.id.searchbutton);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                booknames.clear();
                authornames.clear();
                picuris.clear();
                quantities.clear();
                books=findViewById(R.id.books);
                search=searchbox.getText().toString();
                fetchvalues(search,spinner.getSelectedItem().toString());
                BookSearchActivity.myhelperclass obj=new BookSearchActivity.myhelperclass(BookSearchActivity.this,android.R.layout.simple_list_item_1,booknames.toArray(new String[booknames.size()]));
                books.setAdapter(obj);
            }
        });
        books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    Intent book=new Intent(BookSearchActivity.this,BookActivity.class);
                    if(getIntent().getExtras()!=null)
                    {
                        book.putExtra("bookname",(booknames.get(position)+""));
                        book.putExtra("studentid",student_id);
                    }
                    else
                    {
                        book.putExtra("bookname",(booknames.get(position)+""));
                    }
                    startActivity(book);
                }
                catch (Exception e)
                {
                    Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"Admin Login");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1)
        {
            Intent adminlogin=new Intent(BookSearchActivity.this,LoginActivity.class);
            startActivity(adminlogin);
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchvalues(String search,String searchby)
    {
        booknames.clear();
        authornames.clear();
        quantities.clear();
        picuris.clear();
        if(searchby.equals("Name"))
        {try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books where book_name like '%"+search+"%'", null);
            if(myresult.moveToNext())
            {
                do
                {
                    booknames.add(myresult.getString(myresult.getColumnIndex("book_name")));
                    authornames.add(myresult.getString(myresult.getColumnIndex("book_author")));
                    quantities.add(myresult.getInt(myresult.getColumnIndex("book_quantity")));
                    picuris.add(myresult.getString(myresult.getColumnIndex("book_image")));
                }
                while(myresult.moveToNext());
            }
            else
            {
                Toast.makeText(BookSearchActivity.this,"No results Found",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }}
        else if(searchby.equals("Author"))
        {try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books where book_author like '%"+search+"%'", null);
            if(myresult.moveToNext())
            {
                do
                {
                booknames.add(myresult.getString(myresult.getColumnIndex("book_name")));
                authornames.add(myresult.getString(myresult.getColumnIndex("book_author")));
                quantities.add(myresult.getInt(myresult.getColumnIndex("book_quantity")));
                picuris.add(myresult.getString(myresult.getColumnIndex("book_image")));
                }
                while(myresult.moveToNext());
            }
            else
            {
                Toast.makeText(BookSearchActivity.this,"No results Found",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }}
        else if(searchby.equals("Subject"))
        {try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books where book_subject like '%"+search+"%'", null);
            if(myresult.moveToNext())
            {
                do
                {
                    booknames.add(myresult.getString(myresult.getColumnIndex("book_name")));
                    authornames.add(myresult.getString(myresult.getColumnIndex("book_author")));
                    quantities.add(myresult.getInt(myresult.getColumnIndex("book_quantity")));
                    picuris.add(myresult.getString(myresult.getColumnIndex("book_image")));
                }
                while(myresult.moveToNext());
            }
            else
            {
                Toast.makeText(BookSearchActivity.this,"No results Found",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }}
        else if(searchby.equals("Validity"))
        {try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books where book_validity like '%"+search+"%'", null);
            if(myresult.moveToNext())
            {
                do
                {
                    booknames.add(myresult.getString(myresult.getColumnIndex("book_name")));
                    authornames.add(myresult.getString(myresult.getColumnIndex("book_author")));
                    quantities.add(myresult.getInt(myresult.getColumnIndex("book_quantity")));
                    picuris.add(myresult.getString(myresult.getColumnIndex("book_image")));
                }
                while(myresult.moveToNext());
            }
            else
            {
                Toast.makeText(BookSearchActivity.this,"No results Found",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }}
        else if(searchby.equals("Search by"))
        {try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from books where book_name like '%"+search+"%' or book_author like '%"+search+"%' or book_subject like '%"+search+"%' or book_validity like '%"+search+"%'",null);
            if(myresult.moveToNext())
            {
                do
                {
                    booknames.add(myresult.getString(myresult.getColumnIndex("book_name")));
                    authornames.add(myresult.getString(myresult.getColumnIndex("book_author")));
                    quantities.add(myresult.getInt(myresult.getColumnIndex("book_quantity")));
                    picuris.add(myresult.getString(myresult.getColumnIndex("book_image")));
                }
                while(myresult.moveToNext());
            }
            else
            {
                Toast.makeText(BookSearchActivity.this,"No results Found",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }}
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
            try
            {
                booklistimagebox.setImageURI(Uri.parse(picuris.get(positon)));
            }
            catch (Exception e)
            {
                Toast.makeText(BookSearchActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            return myrow;
        }
    }
}
