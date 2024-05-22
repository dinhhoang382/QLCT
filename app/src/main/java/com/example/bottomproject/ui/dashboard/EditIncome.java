package com.example.bottomproject.ui.dashboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bottomproject.DBHelper;
import com.example.bottomproject.R;
import com.example.bottomproject.ui.notifications.EditExpense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditIncome extends Activity {
    private EditText edit_name, edit_money, edit_note;
    private Button btn_Edit, btn_date;
    private Spinner edit_category;
    private TextView txtget_date;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_income);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edit_name = (EditText) findViewById(R.id.edit_name_khoanthu);
        edit_money = (EditText) findViewById(R.id.edit_money_khoanthu);
        edit_note = (EditText) findViewById(R.id.edit_note_khoanthu);
        btn_Edit = (Button) findViewById(R.id.btn_edit_khoanthu);
        btn_date = (Button) findViewById(R.id.edit_date_khoanthu);
        edit_category = (Spinner) findViewById(R.id.edit_spinner_nhom_khoanthu);
        txtget_date = (TextView) findViewById(R.id.txt_edit_date_khoanthu);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        //ADAPTER
        DBHelper dbHelper = new DBHelper(this);
        List<String> getlistIncome = dbHelper.getDBLoaiThu();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getlistIncome);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_category.setAdapter(adapter);
        //FETCH DATA INCOME ITEM FROM FIREBASE
        String incomeItemID = getIntent().getStringExtra("editincomeID");
        if (incomeItemID != null) {
            FirebaseFirestore.getInstance().collection("users").document(userID).collection("income")
                    .document(incomeItemID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        IncomeItem item = documentSnapshot.toObject(IncomeItem.class);
                        edit_name.setText(item.getName());
                        //--> warning the value of money is int
                        edit_money.setText(String.valueOf(item.getMoney()));
                        edit_note.setText(item.getNote());
                        txtget_date.setText(item.getDate());
                        //SET SPINNER
                        int category = getlistIncome.indexOf(item.getCategory());
                        if (category >= 0) {
                            edit_category.setSelection(category);
                        }
                    });
        }
        //DIALOG CALENDAR PICKER
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                //
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditIncome.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //UPDATE CODE STRING FORMAT
                        String formatdate = getString(R.string.formatted_date, dayOfMonth, month + 1, year);
                        txtget_date.setText(formatdate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //EDIT DATA AND UPDATE TO FIRESTORE
        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString().trim();
                int money = Integer.valueOf(edit_money.getText().toString());
                String note = edit_note.getText().toString().trim();
                String category = edit_category.getSelectedItem().toString();
                String date = txtget_date.getText().toString();
                //map
                Map<String, Object> update = new HashMap<>();
                update.put("name", name);
                update.put("money", money);
                update.put("note", note);
                update.put("category", category);
                update.put("date", date);
                //firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference reference = db.collection("users").document(userID).collection("income").document(incomeItemID);
                //
                reference.update(update)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getApplicationContext(), "Cập nhật " + name + " thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("transactionComplete", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Lỗi! Cập nhật khoản thu không thành công!", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}