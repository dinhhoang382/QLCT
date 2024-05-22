package com.example.bottomproject.ui.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomproject.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<IncomeItem> incomeItems;
    private static final int REQUEST_EDIT_INCOME = 1;

    public IncomeAdapter(List<IncomeItem> incomeItems) {
        this.incomeItems = incomeItems;
    }

    @NonNull
    @Override
    public IncomeAdapter.IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_adapter_layout, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeAdapter.IncomeViewHolder holder, int position) {
        IncomeItem incomeItem = incomeItems.get(position);
        holder.name.setText(String.format("Tên: %s", incomeItem.getName()));
        holder.money.setText(String.format("+ %s VNĐ", incomeItem.getMoney()));
        holder.date.setText(incomeItem.getDate());
        holder.category.setText(incomeItem.getCategory());
        //OnClickListener to RecyclerView Items
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setTitle("Chọn hành động");
                alertDialogBuilder.setItems(new CharSequence[]{"Sửa","Xoá"},(dialog, which) -> {
                    switch (which){
                        case 0:
                            break;
                        case 1:
                            break;
                    }
                });
                alertDialogBuilder.show();
            }
        });*/
        //SET ONCLICK EDIT
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditIncome.class);
                intent.putExtra("editincomeID", incomeItem.getId());

                ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_EDIT_INCOME);
            }
        });
        //SET ONCLICK DELETE
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdeleteIncome(v.getContext(), incomeItem.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return incomeItems.size();
    }

    public void updateDataIncome(List<IncomeItem> incomeItemList) {
        this.incomeItems = incomeItemList;
        notifyDataSetChanged();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView name, money, date, category;
        ImageButton btn_edit;
        ImageButton btn_delete;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adt_name_income);
            money = itemView.findViewById(R.id.adt_money_income);
            date = itemView.findViewById(R.id.adt_date_income);
            category = itemView.findViewById(R.id.adt_category_income);
            btn_edit = itemView.findViewById(R.id.btn_edit_income);
            btn_delete = itemView.findViewById(R.id.btn_delete_income);
        }
    }

    public void deleteIncomeItem(String id) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("income").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int position = -1;
                    for (int i = 0; i < incomeItems.size(); i++) {
                        if (incomeItems.get(i).getId().equals(id)) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        incomeItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, incomeItems.size() - position);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Adapter", "Lỗi xoá dữ liệu");
                });
    }

    //SHOW NOTIFICATION DELETE
    private void showdeleteIncome(Context context, String id) {
        new AlertDialog.Builder()
                .setTitle("Xác nhận xoá!")
                .setMessage("Bạn có chắc chắn xoá khoản thu này?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteIncomeItem(id);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_question_mark)
                .show();
    }
}