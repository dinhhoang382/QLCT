package com.example.bottomproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddKhoanChi extends Activity {
    private Spinner spinner_khoanchi;
    private TextView txtdate_khoanchi;
    private Button btndate_khoanchi, btnsave_khoanchi;
    private EditText edtname_khoanchi, edtmoney_khoanchi, edtnote_khoanchi;
    private FirebaseFirestore firestore;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_khoan_chi);
        txtdate_khoanchi = (TextView) findViewById(R.id.txtdate_khoanchi);
        btndate_khoanchi = (Button) findViewById(R.id.date_khoanchi);
        btnsave_khoanchi = (Button) findViewById(R.id.btn_add_khoanchi);
        edtname_khoanchi = (EditText) findViewById(R.id.name_khoanchi);
        edtmoney_khoanchi = (EditText) findViewById(R.id.money_khoanchi);
        edtnote_khoanchi = (EditText) findViewById(R.id.note_khoanchi);
        //initialization database from fire_store
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //
        btndate_khoanchi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                //
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddKhoanChi.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtdate_khoanchi.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        spinner_khoanchi = (Spinner) findViewById(R.id.spinner_nhom_khoanchi);
        //khoi tao db
        DBHelper dbHelper = new DBHelper(this);
        List<String> listKhoanchi = dbHelper.getDBLoaiChi();
        //
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listKhoanchi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_khoanchi.setAdapter(adapter);
        //initialize Money Edittext
        //initializeMoney();
//        edtmoney_khoanchi.addTextChangedListener(new NumberTextWatcherForThousand(edtmoney_khoanchi));
//        NumberTextWatcherForThousand.trimCommaOfString(edtmoney_khoanchi.getText().toString());

        //button save data
        btnsave_khoanchi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDatatoFirebase();
            }
        });
    }

    private void saveDatatoFirebase() {
        String name = edtname_khoanchi.getText().toString();
        Integer money = Integer.valueOf(edtmoney_khoanchi.getText().toString());
        String note = edtnote_khoanchi.getText().toString();
        String date = txtdate_khoanchi.getText().toString();
        String category = spinner_khoanchi.getSelectedItem().toString();
        //
        String userId = user.getUid();
        //check - kiem tra
        if (name.isEmpty()) {
            edtname_khoanchi.setError("Vui lòng nhập tên!");
            return;
        }
        if (money == 0) {
            edtmoney_khoanchi.setError("Nhập số tiền?");
            return;
        }

        if (money <= 0) {
            edtmoney_khoanchi.setError("Số tiền nhập > 0!");
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày!", Toast.LENGTH_SHORT).show();
            return;
        }
        //new document with data
        Map<String, Object> khoanchi = new HashMap<>();
        khoanchi.put("name", name);
        khoanchi.put("money", money);
        khoanchi.put("category", category);
        khoanchi.put("date", date);
        khoanchi.put("note", note);
        //TODO: add data khoanchi to firebase
        firestore.collection("users").document(userId).collection("expense").add(khoanchi)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Thêm khoản chi ' " + name + " 'thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("expenseToMain", true);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Lỗi! thêm khoản chi không thành công!", Toast.LENGTH_SHORT).show();
                });
    }

}