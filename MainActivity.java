package com.example.nishant.libarayapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    TextView splashtext1,splashtext2,splashtext3;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try
//        {
//            copyDatabase();
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//        }
        splashtext1=findViewById(R.id.splashtext1);
        splashtext2=findViewById(R.id.splashtext2);
        splashtext3=findViewById(R.id.splashtext3);
        SharedPreferences mysettings= PreferenceManager.getDefaultSharedPreferences(this);
        boolean splash=mysettings.getBoolean("splashbox",true);
        if(splash)
        {
            ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.splashcons);
            AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
            animationDrawable.setEnterFadeDuration(1000);
            animationDrawable.setExitFadeDuration(1500);
            animationDrawable.start();
            final Typeface mynewface=Typeface.createFromAsset(getAssets(),"myfont1.ttf");
            splashtext3.setTypeface(mynewface);
            splashtext1.animate().scaleYBy(2).setDuration(2000);
            splashtext1.animate().scaleXBy(2).setDuration(2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    splashtext2.setText("Created By");
                    splashtext2.animate().scaleYBy((float)1.5).setDuration(1000);
                    splashtext2.animate().scaleXBy((float)1.5).setDuration(1000);
                }
            },2500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    splashtext3.setText("Nishant Bansal");
                    splashtext3.animate().scaleXBy((float)2.5).setDuration(2000);
                    splashtext3.animate().scaleYBy((float)2.5).setDuration(2000);
                }
            },3500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent myin=new Intent(MainActivity.this,BookSearchActivity.class);
                    startActivity(myin);
                    MainActivity.this.finish();
                }
            }, 7000);
        }
        else
        {
            Intent myin=new Intent(MainActivity.this,BookSearchActivity.class);
            startActivity(myin);
            MainActivity.this.finish();
        }
    }
    void copyDatabase() throws IOException
    {
        String DBPATH="/data/data/com.example.nishant.libarayapp/databases/";
        InputStream myinput=MainActivity.this.getAssets().open("librarydatabase");
//        File existingdb=new File("/data/data/com.example.nishant.libarayapp/databases/librarydatabse");
//        if(existingdb.exists())
//        {
//            Toast.makeText(MainActivity.this,"HELLO",Toast.LENGTH_SHORT).show();
//        }
        //else {
            String outfile = DBPATH + "librarydatabase";
            OutputStream myoutput = new FileOutputStream(outfile);
            int a;
            byte[] buffer = new byte[1024];
            while ((a = myinput.read(buffer)) > 0) {
                myoutput.write(buffer, 0, a);
            }
            myoutput.close();
            myinput.close();
        //}
    }
}
