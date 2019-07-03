package com.example.nishant.libarayapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText id,password;
    Button signin;
    TextView registerbutton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Admin Login");
        ConstraintLayout your_Layout = (ConstraintLayout) findViewById(R.id.conslayout2);
        AnimationDrawable animationDrawable = (AnimationDrawable) your_Layout.getBackground();
        animationDrawable.setEnterFadeDuration(4000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        id=findViewById(R.id.loginid);
        password=findViewById(R.id.password);
        signin=findViewById(R.id.loginbutton);
        registerbutton=findViewById(R.id.registerbutton);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idlogin,passwordlogin;
                idlogin=id.getText().toString();
                passwordlogin=password.getText().toString();
                if(idlogin.equals("1234")&&passwordlogin.equals("admin"))
                {
                    Intent adminlogin=new Intent(LoginActivity.this,AdminLoginActivity.class);
                    startActivity(adminlogin);
                    LoginActivity.this.finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Invalid ID or password",Toast.LENGTH_SHORT).show();
                }
            }
        });
//        registerbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signup=new Intent(LoginActivity.this,SignupActivity.class);
//                startActivity(signup);
//            }
//        });
    }
}
