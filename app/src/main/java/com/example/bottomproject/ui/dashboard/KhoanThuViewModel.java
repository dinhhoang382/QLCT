package com.example.bottomproject.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class KhoanThuViewModel extends ViewModel {
    public MutableLiveData<List<IncomeItem>> incomeItem;
    private FirebaseAuth mAuth;

    public KhoanThuViewModel() {
        incomeItem = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
    }

    private ListenerRegistration incomeUpdate;

    public LiveData<List<IncomeItem>> getIncomeItem() {
        return incomeItem;
    }

    public void queryDataIncome(LifecycleOwner lifecycleOwner, String selectedDate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        String userId = user.getUid();
        incomeUpdate = db.collection("users").document(userId).collection("income").whereEqualTo("date", selectedDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        List<IncomeItem> items = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            IncomeItem item = doc.toObject(IncomeItem.class);
                            item.setId(doc.getId());
                            items.add(item);
                        }
                        incomeItem.setValue(items);
                    }
                });
        lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (incomeUpdate != null) {
                        incomeUpdate.remove();
                    }
                }
            }
        });
    }

    //FETCHING DATA FROM FIRESTORE TO SUM INCOME
    public LiveData<Integer> queryTotalIncomeMonth(String monthYear) {
        MutableLiveData<Integer> totalIncome = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        String startMonth = "01/" + monthYear;
        String endMonth = "31/" + monthYear;
        firestore.collection("users").document(userId).collection("income")
                .whereGreaterThanOrEqualTo("date", startMonth)
                .whereLessThanOrEqualTo("date", endMonth)
                .get()
                .addOnCompleteListener(task -> {
                    int sum = 0;
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            IncomeItem income = document.toObject(IncomeItem.class);
                            sum += Integer.parseInt(String.valueOf(income.getMoney()));
                        }
                        totalIncome.setValue(sum);
                    } else {
                        totalIncome.setValue(0); // Error or no data
                    }
                });
        return totalIncome;
    }
    public LiveData<Integer> getMonthlyIncomeTotal() {
        MutableLiveData<Integer> totalIncomeLiveData = new MutableLiveData<>();
        return totalIncomeLiveData;
    }
}