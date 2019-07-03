package com.example.nishant.libarayapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    Button booksearchbutton,viewissuehistorystudent;
    ImageView profilepic;
    String student_id;
    TextView studentnamebox,studentidbox,studentemailbox,studentphonebox;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.setTitle("Profile");
        ConstraintLayout your_Layout = findViewById(R.id.linearLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) your_Layout.getBackground();
        animationDrawable.setEnterFadeDuration(4000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        studentnamebox=findViewById(R.id.studentnamebox);
        studentemailbox=findViewById(R.id.studentemailbox);
        studentphonebox=findViewById(R.id.studentphonebox);
        studentidbox=findViewById(R.id.studentidbox);
        profilepic=findViewById(R.id.profilepic);
        student_id=getIntent().getExtras().getString("student_id");
        try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from students where student_id=?",new String[]{student_id});
            if(myresult.moveToNext())
            {
                studentnamebox.setText(myresult.getString(myresult.getColumnIndex("student_name")));
                studentidbox.setText(myresult.getString(myresult.getColumnIndex("student_id")));
                studentemailbox.setText(myresult.getString(myresult.getColumnIndex("student_email")));
                studentphonebox.setText(myresult.getString(myresult.getColumnIndex("student_phone")));
                profilepic.setImageURI(Uri.parse(myresult.getString(myresult.getColumnIndex("student_image"))));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        booksearchbutton=findViewById(R.id.booksearchbutton);
        viewissuehistorystudent=findViewById(R.id.viewissuehistorystudent);

        booksearchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookissue=new Intent(ProfileActivity.this,BookSearchActivity.class);
                bookissue.putExtra("studentid",student_id);
                startActivity(bookissue);
            }
        });
        viewissuehistorystudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent issuehistory=new Intent(ProfileActivity.this,IssueHistory.class);
                issuehistory.putExtra("studentid",student_id);
                startActivity(issuehistory);
            }
        });
    }
    protected void onResume() {
        try
        {
            SQLiteDatabase mydb=openOrCreateDatabase("librarydatabase",MODE_PRIVATE,null);
            Cursor myresult=mydb.rawQuery("select * from students where student_id=?",new String[]{student_id});
            if(myresult.moveToNext())
            {
                studentnamebox.setText(myresult.getString(myresult.getColumnIndex("student_name")));
                studentidbox.setText(myresult.getString(myresult.getColumnIndex("student_id")));
                studentemailbox.setText(myresult.getString(myresult.getColumnIndex("student_email")));
                studentphonebox.setText(myresult.getString(myresult.getColumnIndex("student_phone")));
                profilepic.setImageURI(Uri.parse(myresult.getString(myresult.getColumnIndex("student_image"))));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"Update Details");
        menu.add(1,2,2,"Logout");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1)
        {
            Intent updatedetails=new Intent(ProfileActivity.this,SignupActivity.class);
            updatedetails.putExtra("student_id",student_id);
            startActivity(updatedetails);
        }
        else if(item.getItemId()==2)
        {
            ProfileActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
