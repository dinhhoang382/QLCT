package com.example.bottomproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomproject.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends Activity {
    private EditText edtemail, edtpassword;
    private TextView btnRegister, btnforgotpass;
    private Button btnLogin, btnGoogleL;
    private ProgressBar progressbar;
    private CheckBox chkpassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        edtemail = (EditText) findViewById(R.id.edtemail);
        edtpassword = (EditText) findViewById(R.id.edtpassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnGoogleL = (Button) findViewById(R.id.btnGoogleL);
        btnRegister = (TextView) findViewById(R.id.btnRegister);
        btnforgotpass = (TextView) findViewById(R.id.btnforgotpass);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        chkpassword = (CheckBox) findViewById(R.id.chkPassword);


        //login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginButton();
            }
        });
        //intent dang ky
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterButton();
            }
        });
        //show password
        chkpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    edtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        //forgot password
        btnforgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }
    //login one time Firebase
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if(firebaseUser != null){
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    private void handleLoginButton() {
        //set progressbar visible
        progressbar.setVisibility(View.VISIBLE);
        //convert email password to String
        String email, password;
        email = edtemail.getText().toString().trim();
        password = edtpassword.getText().toString();
        //condition()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tài khoản email!", Toast.LENGTH_SHORT).show();
            progressbar.setVisibility(View.INVISIBLE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập password!", Toast.LENGTH_SHORT).show();
            progressbar.setVisibility(View.INVISIBLE);
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "mật khẩu đăng nhập cần 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
            progressbar.setVisibility(View.INVISIBLE);
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("userId", uid);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(Login.this, "Lỗi! Đăng nhập thất bại!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void handleRegisterButton() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
}