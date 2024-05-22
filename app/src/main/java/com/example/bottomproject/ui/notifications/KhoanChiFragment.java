package com.example.bottomproject.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomproject.AddKhoanChi;
import com.example.bottomproject.R;
import com.example.bottomproject.databinding.FragmentKhoanchiBinding;
import com.example.bottomproject.databinding.FragmentKhoanthuBinding;
import com.example.bottomproject.ui.dashboard.KhoanThuViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class KhoanChiFragment extends Fragment {
    FloatingActionButton fab_add;
    private CalendarView calendar_expense;
    private RecyclerView recyclerView_expense;
    private TextView txt_sumExpense;

    private FragmentKhoanchiBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        KhoanChiViewModel khoanChiViewModel =
                new ViewModelProvider(this).get(KhoanChiViewModel.class);

        binding = FragmentKhoanchiBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //INTENT FAB ADD
        fab_add = (FloatingActionButton) root.findViewById(R.id.fab_add_khoanchi);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddKhoanChi.class);
                startActivity(intent);
            }
        });
        //FIND VIEW ID
        calendar_expense = (CalendarView) root.findViewById(R.id.calenarView_khoanchi);
        recyclerView_expense = (RecyclerView) root.findViewById(R.id.recyclerView_khoanchi);
        //funtion
        //GET DATE FROM NOW
        Calendar today = Calendar.getInstance();
        String currentMonthYear = (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR);
        String now = today.get(Calendar.DAY_OF_MONTH) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR);
        khoanChiViewModel.queryDatafromFirestore(this, now);
        updateTotalExpense(khoanChiViewModel, currentMonthYear);

        //GET DATE FROM FIRESTORE
        txt_sumExpense = (TextView) root.findViewById(R.id.txt_sum_expense);
        calendar_expense.setOnDateChangeListener(((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            khoanChiViewModel.queryDatafromFirestore(this, selectedDate);
            //SUM INCOME ---MONTH--- FROM MODELVIEW
            String selectedMonth = (month + 1) + "/" + year;
            khoanChiViewModel.queryTotalIncomeMonth(selectedMonth).observe(getViewLifecycleOwner(), total -> {
                txt_sumExpense.setText(String.format("Tổng chi: %d VNĐ", total));
            });
        }));
        //SET ADAPTER
        ExpenseAdapter adapter = new ExpenseAdapter(new ArrayList<>()); // Initialize with an empty list
        recyclerView_expense.setAdapter(adapter);
        recyclerView_expense.setLayoutManager(new LinearLayoutManager(getContext()));
        khoanChiViewModel.getExpenseItem().observe(getViewLifecycleOwner(), new Observer<List<ExpenseItem>>() {
            @Override
            public void onChanged(List<ExpenseItem> expenseItems) {
                adapter.UpdateData(expenseItems);
            }
        });


        //HIDE ACTIONBAR
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
        }
        return root;
    }
    private void updateTotalExpense(KhoanChiViewModel viewModel, String monthYear) {
        viewModel.queryTotalIncomeMonth(monthYear).observe(getViewLifecycleOwner(), total -> {
            txt_sumExpense.setText(String.format("Tổng thu: %d VNĐ", total));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void updateRecyclerView(List<ExpenseItem> expenseItems) {
        /* TODO: set ExpenseAdapter */
        ExpenseAdapter adapter = (ExpenseAdapter) recyclerView_expense.getAdapter();
        if (adapter == null) {
            adapter = new ExpenseAdapter(expenseItems);
            recyclerView_expense.setAdapter(adapter);
            recyclerView_expense.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            adapter.UpdateData(expenseItems);
        }
    }
}