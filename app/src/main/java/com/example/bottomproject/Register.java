package com.example.bottomproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends Activity {
    EditText edtemail, edtpassword, edtpasswordR;
    Button btnRegister, btnGoogle;
    TextView btnLogin;
    private CheckBox chkpass;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        edtemail = (EditText) findViewById(R.id.edtemail);
        edtpassword = (EditText) findViewById(R.id.edtpassword);
        edtpasswordR = (EditText) findViewById(R.id.edtpasswordR);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnGoogle = (Button) findViewById(R.id.btnGoogleR);
        chkpass = (CheckBox) findViewById(R.id.chkPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginButton();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterButton();
            }
        });
        chkpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edtpasswordR.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    edtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtpasswordR.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }

    public void handleLoginButton() {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    public void handleRegisterButton() {
        progressBar.setVisibility(View.VISIBLE);
        String email, password, passwordR;
        email = edtemail.getText().toString().trim();
        password = edtpassword.getText().toString();
        passwordR = edtpasswordR.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "email không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordR)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || passwordR.length() < 6) {
            Toast.makeText(getApplicationContext(), "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordR)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu đăng ký không trùng khớp!", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //set notification alertdialog
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(Register.this);
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setTitle("Thông báo!");
                    alertDialogBuilder.setMessage("Đăng ký thành công");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    //set progressbar gone
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "Đăng ký không thành công!" + "Vui lòng thử lại", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}