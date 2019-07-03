package com.example.nishant.libarayapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AdminActivity extends AppCompatActivity {

    ImageView viewstudents,viewbooks,viewissuehistory;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        LinearLayout your_Layout = findViewById(R.id.linearLayout2);
        AnimationDrawable animationDrawable = (AnimationDrawable) your_Layout.getBackground();
        animationDrawable.setEnterFadeDuration(4000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        viewstudents=findViewById(R.id.viewstudents);
        viewbooks=findViewById(R.id.viewbooks);
        viewissuehistory=findViewById(R.id.viewissuehistory);
        viewstudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent studentsintent=new Intent(AdminActivity.this,AdminProfileActivity.class);
                startActivity(studentsintent);
            }
        });
        viewbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent booksintent=new Intent(AdminActivity.this,AdminBookActivity.class);
                startActivity(booksintent);
            }
        });
        viewissuehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent issueintent=new Intent(AdminActivity.this,IssueHistory.class);
                startActivity(issueintent);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"Update Details");
        menu.add(1,2,2,"Logout");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1)
        {
            Intent updatedetails=new Intent(AdminActivity.this,SignupActivity.class);
            updatedetails.putExtra("updatetoken",1);
            startActivity(updatedetails);
        }
        else if(item.getItemId()==2)
        {
           AdminActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
