package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterationActivity extends AppCompatActivity {

    FirebaseAuth mauth;
    ProgressDialog mdialog;

    EditText email_ed , password_ed;
    Button register_btn;
    TextView login_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        mauth = FirebaseAuth.getInstance();
        mdialog = new ProgressDialog(this);
        //findviewbyids
        email_ed = findViewById(R.id.ed_email);
        password_ed = findViewById(R.id.ed_pass);
        register_btn = findViewById(R.id.btn_register);
        login_tv = findViewById(R.id.txt_login);

        //on click listeners
        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.setMessage("Registering..");
                mdialog.show();
                String email , password;
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
                mauth.createUserWithEmailAndPassword(email , password)
                        .addOnCompleteListener(RegisterationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mdialog.dismiss();
                                    Toast.makeText(getApplicationContext() , "registeration successful" , Toast.LENGTH_SHORT)
                                            .show();
                                    finish();

                                } else {
                                    mdialog.dismiss();
                                    Toast.makeText(getApplicationContext() , "unsuccessful" , Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
            }
        });
    }
}
