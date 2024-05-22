package com.example.bottomproject.ui.notifications;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditExpense extends Activity {
    private TextView txt_getdate;
    private EditText edtname, edtmoney, edtnote;
    private Spinner spncategory;
    private Button btnsave, btndate;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txt_getdate = (TextView) findViewById(R.id.txt_edit_date_khoanchi);
        edtname = (EditText) findViewById(R.id.edit_name_khoanchi);
        edtmoney = (EditText) findViewById(R.id.edit_money_khoanchi);
        edtnote = (EditText) findViewById(R.id.edit_note_khoanchi);
        spncategory = (Spinner) findViewById(R.id.edit_spinner_nhom_khoanchi);
        btnsave = (Button) findViewById(R.id.btn_edit_khoanchi);
        btndate = (Button) findViewById(R.id.edit_date_khoanchi);
        //FIREBASE USER
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        //ARRAY ADAPTER
        DBHelper dbHelper = new DBHelper(this);
        List<String> listeditExpense = dbHelper.getDBLoaiChi();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listeditExpense);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spncategory.setAdapter(adapter);
        //FETCH DATA INCOME ITEM FROM FIREBASE
        String expenseItemID = getIntent().getStringExtra("editexpenseID");
        if(expenseItemID != null){
           FirebaseFirestore.getInstance().collection("users").document(userID)
                   .collection("expense").document(expenseItemID)
                   .get()
                   .addOnSuccessListener(documentSnapshot -> {
                       ExpenseItem item = documentSnapshot.toObject(ExpenseItem.class);
                       edtname.setText(item.getName());
                       //--> warning the value of money is int
                       edtmoney.setText(String.valueOf(item.getMoney()));
                       edtnote.setText(item.getNote());
                       txt_getdate.setText(item.getDate());
                       //SET SPINNER
                       int category = listeditExpense.indexOf(item.getCategory());
                       if (category >= 0) {
                           spncategory.setSelection(category);
                       }
                   });
        }
        //DIALOG CALENDAR PICKER
        btndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                //
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditExpense.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //UPDATE CODE STRING FORMAT
                        String formatdate = getString(R.string.formatted_date, dayOfMonth, month+1, year);
                        txt_getdate.setText(formatdate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //BTN UPDATE
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UPDATEDATAFIRESTORE();
            }
        });
    }
    private void UPDATEDATAFIRESTORE() {
        String expenseItemID = getIntent().getStringExtra("editexpenseID");
        String name = edtname.getText().toString().trim();
        int money = Integer.parseInt(edtmoney.getText().toString().trim());
        String note = edtnote.getText().toString().trim();
        String category = spncategory.getSelectedItem().toString();
        String date = txt_getdate.getText().toString();
        //check
        if (name.isEmpty()) {
            edtname.setError("Vui lòng nhập tên!");
            return;
        }
        if (money == 0) {
            edtmoney.setError("Nhập số tiền?");
            return;
        }

        if (money <= 0) {
            edtmoney.setError("Số tiền nhập > 0!");
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày!", Toast.LENGTH_SHORT).show();
            return;
        }
        //map
        Map<String, Object> update = new HashMap<>();
        update.put("name",name);
        update.put("money",money);
        update.put("category", category);
        update.put("date", date);
        update.put("note",note);
        //FirebaseUser
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        //firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("users").document(userID).collection("expense").document(expenseItemID);

        reference.update(update)
                .addOnSuccessListener(documentReference ->{
                    Toast.makeText(getApplicationContext(), "Cập nhật ' " + name + " 'thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("expenseToMain", true);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .addOnFailureListener(e->{
                    Toast.makeText(getApplicationContext(), "Lỗi! Cập nhật khoản chi không thành công!", Toast.LENGTH_SHORT).show();
                });



    }
}