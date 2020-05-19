package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mauth;
    ProgressDialog mdialog;
    EditText email_ed , password_ed;
    Button login_btn;
    TextView register_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mauth = FirebaseAuth.getInstance();
        mdialog = new ProgressDialog(this);
        email_ed = findViewById(R.id.ed_email);
        password_ed = findViewById(R.id.ed_pass);
        login_btn = findViewById(R.id.btn_login);
        register_tv = findViewById(R.id.txt_register);

        //on click listeners
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.setMessage("Checking credentials..");
                mdialog.show();

                String email, password;
                email = email_ed.getText().toString().trim();
                password = password_ed.getText().toString();
                if(TextUtils.isEmpty(email)){
                    email_ed.setError("field can't be empty");
                    mdialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    password_ed.setError("field can't be empty");
                    mdialog.dismiss();
                    return;
                }
                mauth.signInWithEmailAndPassword(email , password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mdialog.dismiss();
                                    Toast.makeText(MainActivity.this , "login successful",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext() , HomeActivity.class));
                                    finish();

                                } else {
                                    mdialog.dismiss();
                                    Toast.makeText(MainActivity.this , "login failed", Toast.LENGTH_SHORT)
                                            .show();
                                }

                            }
                        });
            }
        });

        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , RegisterationActivity.class));
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mauth.getCurrentUser();
        if(currentuser != null){
            startActivity(new Intent(getApplicationContext() , HomeActivity.class));
            finish();
        }
    }
}
