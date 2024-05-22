package com.example.bottomproject.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomproject.AddKhoanThu;
import com.example.bottomproject.R;
import com.example.bottomproject.databinding.FragmentKhoanthuBinding;
import com.example.bottomproject.ui.notifications.ExpenseAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class KhoanThuFragment extends Fragment {

    private FragmentKhoanthuBinding binding;
    FloatingActionButton fab_add_khoanthu;
    private CalendarView calendar_income;
    private RecyclerView recyclerView_income;
    private TextView txt_sumIncome;
    private ListenerRegistration incomeTotalListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        KhoanThuViewModel khoanThuViewModel =
                new ViewModelProvider(this).get(KhoanThuViewModel.class);

        binding = FragmentKhoanthuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //INTENT
        fab_add_khoanthu = (FloatingActionButton) root.findViewById(R.id.fab_add_khoanthu);
        fab_add_khoanthu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddKhoanThu.class);
                startActivity(intent);
            }
        });

        //FIND CALENDAR
        calendar_income = (CalendarView) root.findViewById(R.id.calenarView_khoanthu);
        recyclerView_income = (RecyclerView) root.findViewById(R.id.expenseRecyclerView_khoanthu);

        //update date to click
        Calendar today = Calendar.getInstance();
        String currentMonthYear = (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR);
        //String currentDate = today.get(Calendar.DAY_OF_MONTH) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR);
        String now = today.get(Calendar.DAY_OF_MONTH) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR);
        updateTotalIncome(khoanThuViewModel, currentMonthYear);
        khoanThuViewModel.queryDataIncome(this, now);

        //get date from firestore
        txt_sumIncome = (TextView) root.findViewById(R.id.txt_sum_income);
        calendar_income.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            String selectedMonthYear = (month + 1) + "/" + year;
            khoanThuViewModel.queryDataIncome(this, selectedDate);
            //SUM INCOME ---MONTH--- FROM MODELVIEW [CODE 97]
            updateTotalIncome(khoanThuViewModel, selectedMonthYear);
        });

        //SET ADAPTER
        IncomeAdapter adapter = new IncomeAdapter(new ArrayList<>());
        recyclerView_income.setAdapter(adapter);
        recyclerView_income.setLayoutManager(new LinearLayoutManager(getContext()));

        //recycler adapter
        khoanThuViewModel.getIncomeItem().observe(getViewLifecycleOwner(), new Observer<List<IncomeItem>>() {
            @Override
            public void onChanged(List<IncomeItem> incomeItems) {
                adapter.updateDataIncome(incomeItems);
            }
        });


        //SET RECYCLERVIEW DISPLAY DATA
        //HIDE ACTIONBAR
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
        }
        return root;
    }

    private void updateTotalIncome(KhoanThuViewModel viewModel, String monthYear) {
        viewModel.queryTotalIncomeMonth(monthYear).observe(getViewLifecycleOwner(), total -> {
            txt_sumIncome.setText(String.format("Tổng thu: %d VNĐ", total));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Calendar today = Calendar.getInstance();
        String currentMonthYear = (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR);
        updateTotalIncome1(currentMonthYear);
    }

    //SET NO ACTION BAR
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (incomeTotalListener != null) {
            incomeTotalListener.remove();
        }
    }

    private void updateTotalIncome1(String monthYear) {
        if (incomeTotalListener != null) {
            incomeTotalListener.remove();
        }

        incomeTotalListener = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("income")
                .whereEqualTo("monthYear", monthYear)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("SnapshotListener", "Listen failed.", e);
                        return;
                    }
                    int total = 0;
                    for (DocumentSnapshot doc : snapshots) {
                        Integer money = doc.getLong("money").intValue();
                        total += money;
                    }
                    txt_sumIncome.setText(String.format("Tổng thu: %d VNĐ", total));
                });
    }
}