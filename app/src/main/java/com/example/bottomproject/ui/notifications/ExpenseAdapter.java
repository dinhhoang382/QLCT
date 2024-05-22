package com.example.bottomproject.ui.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bottomproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<ExpenseItem> expenseItems;
    private int REQUEST_EDIT_EXPENSE = 1;

    public ExpenseAdapter(List<ExpenseItem> expenseItems) {
        this.expenseItems = expenseItems;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_adapter_layout, parent, false);
        return new ExpenseViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        ExpenseItem expenseItem = expenseItems.get(position);
        holder.nameTextView.setText(String.format("Tên: %s", expenseItem.getName()));
        holder.moneyTextView.setText(String.format("- %s VNĐ", expenseItem.getMoney()));
        holder.dateTextView.setText(expenseItem.getDate());
        holder.categoryTextView.setText(expenseItem.getCategory());
        //
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditExpense.class);
                intent.putExtra("editexpenseID", expenseItem.getId());
                ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_EDIT_EXPENSE);
            }
        });
        //
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelete(v.getContext(), expenseItem.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseItems.size();
    }

    //update code
    public void UpdateData(List<ExpenseItem> newItems) {
        this.expenseItems = newItems;
        notifyDataSetChanged();

    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, categoryTextView, moneyTextView;
        ImageButton btn_edit, btn_delete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.adt_name_expense);
            dateTextView = itemView.findViewById(R.id.adt_date_expense);
            categoryTextView = itemView.findViewById(R.id.adt_category_expense);
            moneyTextView = itemView.findViewById(R.id.adt_money_expense);
            btn_edit = itemView.findViewById(R.id.btn_edit_expense);
            btn_delete = itemView.findViewById(R.id.btn_delete_expense);
        }
    }

    private void deleteDataFirestore(String id) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        firestore.collection("users").document(userId).collection("expense")
                .document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    int position = -1;
                    for (int i = 0; i < expenseItems.size(); i++) {
                        if (expenseItems.get(i).getId().equals(id)) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        expenseItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, expenseItems.size() - position);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Adapter", "Lỗi xoá dữ liệu");
                });
    }

    //NOTIFICATION DELETE
    private void showDelete(Context context, String id) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xoá!")
                .setMessage("Bạn có chắc chắn xoá khoản thu?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDataFirestore(id);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_question_mark)
                .show();
    }
}