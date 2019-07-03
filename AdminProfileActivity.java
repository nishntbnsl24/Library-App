package com.example.nishant.libarayapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminProfileActivity extends AppCompatActivity {

    ArrayList<String> names=new ArrayList<String>();
    ArrayList<Integer> ids=new ArrayList<Integer>();
    ArrayList<String> picuris=new ArrayList<String>();
    ListView students;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        this.setTitle("Students");
        students=findViewById(R.id.studentslist);
        this.registerForContextMenu(students);
        students.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    Intent profile=new Intent(AdminProfileActivity.this,ProfileActivity.class);
                    profile.putExtra("student_id",(ids.get(position)+""));
                    startActivity(profile);
                }
                catch (Exception e)
                {
                    Toast.makeText(AdminProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup=new Intent(AdminProfileActivity.this,SignupActivity.class);
                startActivity(signup);
            }
        });
    }
    public void fetchvalues()
    {
        try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from students",null);
            if(myresult.moveToNext())
            {
                do
                {
                    names.add(myresult.getString(myresult.getColumnIndex("student_name")));
                    ids.add(myresult.getInt(myresult.getColumnIndex("student_id")));
                    picuris.add(myresult.getString(myresult.getColumnIndex("student_image")));
                }
                while(myresult.moveToNext());
            }
        }
        catch (Exception e)
        {
            Toast.makeText(AdminProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(1,1,1,"Delete Student");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        if (item.getItemId() == 1) {
            AlertDialog.Builder mybuilder=new AlertDialog.Builder(AdminProfileActivity.this);
            mybuilder.setMessage("Do you really want to delete student?");
            mybuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        SQLiteDatabase mydb = openOrCreateDatabase("librarydatabase", MODE_PRIVATE, null);
                        mydb.execSQL("delete from students where student_id=" + ids.get(position));
                        Toast.makeText(AdminProfileActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                        onResume();
                    } catch (Exception e) {
                        Toast.makeText(AdminProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog mydialog=mybuilder.create();
            mydialog.show();
        }
            return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        names.clear();
        ids.clear();
        picuris.clear();
        fetchvalues();
        myhelperclass obj=new myhelperclass(AdminProfileActivity.this,android.R.layout.simple_list_item_1,names.toArray(new String[names.size()]));
        students.setAdapter(obj);
        super.onResume();
    }

    public class myhelperclass extends ArrayAdapter<String>
    {
        public myhelperclass(Context context, int resource, String[] objects)
        {
            super(context,resource,objects);
        }
        public View getView(int positon, View convertview, ViewGroup parent) {
            View myrow = getLayoutInflater().inflate(R.layout.profilelistdesign, parent, false);
            ImageView img;
            TextView nameheading,idheading;
            img = myrow.findViewById(R.id.studentlistimagebox);
            nameheading = myrow.findViewById(R.id.studentnamelisttextbox);
            idheading=myrow.findViewById(R.id.studentidlisttextbox);
            nameheading.setText(names.get(positon));
            idheading.setText(ids.get(positon)+"");
            img.setImageURI(Uri.parse(picuris.get(positon)));
            return myrow;
        }
    }
}
