package com.example.nishant.libarayapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminLoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView viewstudents,viewbooks,viewissuehistory;
    TextView viewstudentstext,viewbookstext,viewissuehistorytext;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        this.setTitle("Welcome Admin");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewstudents=findViewById(R.id.viewstudents);
        viewbooks=findViewById(R.id.viewbooks);
        viewissuehistory=findViewById(R.id.viewissuehistory);
        viewstudentstext=findViewById(R.id.viewstudentstext);
        viewbookstext=findViewById(R.id.viewbookstext);
        viewissuehistorytext=findViewById(R.id.viewissuehistorytext);
        viewstudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent studentsintent=new Intent(AdminLoginActivity.this,AdminProfileActivity.class);
                startActivity(studentsintent);
            }
        });
        viewstudentstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent studentsintent=new Intent(AdminLoginActivity.this,AdminProfileActivity.class);
                startActivity(studentsintent);
            }
        });
        viewbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent booksintent=new Intent(AdminLoginActivity.this,AdminBookActivity.class);
                startActivity(booksintent);
            }
        });
        viewbookstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent booksintent=new Intent(AdminLoginActivity.this,AdminBookActivity.class);
                startActivity(booksintent);
            }
        });
        viewissuehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent issueintent=new Intent(AdminLoginActivity.this,IssueHistory.class);
                startActivity(issueintent);
            }
        });
        viewissuehistorytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent issueintent=new Intent(AdminLoginActivity.this,IssueHistory.class);
                startActivity(issueintent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog adddialog=new Dialog(AdminLoginActivity.this);
                adddialog.setContentView(R.layout.pickimagedialoglayout);
                ImageView stu,book;
                stu=adddialog.findViewById(R.id.cameraicon);
                book=adddialog.findViewById(R.id.galleryicon);
                stu.setImageResource(R.drawable.studenticon);
                adddialog.show();
                stu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signup=new Intent(AdminLoginActivity.this,SignupActivity.class);
                        startActivity(signup);
                        adddialog.dismiss();
                    }
                });
                book.setImageResource(R.drawable.bookicon);
                book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addbook=new Intent(AdminLoginActivity.this,AddBook.class);
                        startActivity(addbook);
                        adddialog.dismiss();
                    }
                });

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            AdminLoginActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent studentsintent=new Intent(AdminLoginActivity.this,AdminProfileActivity.class);
            startActivity(studentsintent);
        } else if (id == R.id.nav_gallery) {
            Intent booksintent=new Intent(AdminLoginActivity.this,AdminBookActivity.class);
            startActivity(booksintent);
        } else if (id == R.id.nav_manage) {
            Intent settings=new Intent(AdminLoginActivity.this,MyPreferenceActivity.class);
            startActivity(settings);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
