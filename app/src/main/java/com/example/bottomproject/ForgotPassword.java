package com.example.bottomproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends Activity {
    EditText edtmailforgot;
    Button btnforgot;
    FirebaseAuth mAuth;
    ProgressBar progressBar_forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        edtmailforgot = (EditText) findViewById(R.id.edtemailforgot);
        btnforgot = (Button) findViewById(R.id.btnforgot);
        progressBar_forgot = (ProgressBar) findViewById(R.id.progressbar_forgot);

//        //for a determinate color
//        progressBar_forgot.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
//        //indeterminate color
//        progressBar_forgot.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        btnforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                progressBar_forgot.setVisibility(View.VISIBLE);
                String forgotpass = edtmailforgot.getText().toString().trim();
                if (TextUtils.isEmpty(forgotpass)) {
                    Toast.makeText(getApplicationContext(), "Email không được để trống", Toast.LENGTH_SHORT).show();
                }
                mAuth.sendPasswordResetEmail(forgotpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPassword.this);
                            alertDialogBuilder.setIcon(R.drawable.v_tick);
                            alertDialogBuilder.setTitle("Thông báo!");
                            alertDialogBuilder.setMessage("Xác nhận email thành công!\nVui lòng kiếm tra trong email");
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ForgotPassword.this, Login.class);
                                    startActivity(intent);
                                    progressBar_forgot.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}