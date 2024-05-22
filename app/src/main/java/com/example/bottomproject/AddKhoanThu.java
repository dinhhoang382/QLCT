package com.example.bottomproject;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.bottomproject.ui.dashboard.IncomeItem;
import com.example.bottomproject.ui.dashboard.KhoanThuFragment;
import com.example.bottomproject.ui.dashboard.KhoanThuViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddKhoanThu extends Activity {
    private Spinner spinner_khoanthu;
    private TextView txtdate_khoanthu;
    private Button btndate_khoanthu, btnsave_khoanthu;
    private EditText edtname_khoanthu, edtmoney_khoanthu, edtnote_khoanthu;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_khoan_thu);
        spinner_khoanthu = (Spinner) findViewById(R.id.spinner_nhom_khoanthu);
        txtdate_khoanthu = (TextView) findViewById(R.id.txtdate_khoanthu);
        btndate_khoanthu = (Button) findViewById(R.id.date_khoanthu);
        btnsave_khoanthu = (Button) findViewById(R.id.btn_add_khoanthu);
        edtname_khoanthu = (EditText) findViewById(R.id.name_khoanthu);
        edtmoney_khoanthu = (EditText) findViewById(R.id.money_khoanthu);
        edtnote_khoanthu = (EditText) findViewById(R.id.note_khoanthu);
        //TODO: Generate database from fire_store
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //TODO: Edittext money
//        edtmoney_khoanthu.addTextChangedListener(new NumberTextWatcherForThousand(edtmoney_khoanthu));
//        NumberTextWatcherForThousand.trimCommaOfString(edtmoney_khoanthu.getText().toString());

        //TODO: Get spinner dropdown item
        DBHelper dbHelper;
        dbHelper = new DBHelper(this);
        List<String> listKhoanthu = dbHelper.getDBLoaiThu();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listKhoanthu);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_khoanthu.setAdapter(adapter);
        //TODO: Button get date
        btndate_khoanthu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                //get day, month, year
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //create day picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddKhoanThu.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtdate_khoanthu.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //TODO: Button save data
        btnsave_khoanthu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDatatoFirebase();
            }
        });

    }

    private void saveDatatoFirebase() {
        String name = edtname_khoanthu.getText().toString();
        Integer money = Integer.valueOf(edtmoney_khoanthu.getText().toString());
        String note = edtnote_khoanthu.getText().toString();
        String date = txtdate_khoanthu.getText().toString();
        String category = spinner_khoanthu.getSelectedItem().toString();
        //
        String userId = user.getUid();
        //condition
        if (name.isEmpty()) {
            edtname_khoanthu.setError("Vui lòng nhập tên!");
            return;
        }
        if (money.equals(0)) {
            edtmoney_khoanthu.setError("Nhập số tiền?");
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày!", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> khoanthu = new HashMap<>();
        khoanthu.put("name", name);
        khoanthu.put("money", money);
        khoanthu.put("category", category);
        khoanthu.put("date", date);
        khoanthu.put("note", note);
        //TODO: save data to firestore - firebase
        firestore.collection("users").document(userId).collection("income")
                .add(khoanthu)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Thêm khoản thu " + name + " thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("transactionComplete", true);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi! Thêm khoản thu không thành công!", Toast.LENGTH_SHORT).show();
                });
    }
}