package com.example.bottomproject.ui.notifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomproject.ui.dashboard.IncomeItem;
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

public class KhoanChiViewModel extends ViewModel {
    private MutableLiveData<List<ExpenseItem>> expenseItem;
    private FirebaseAuth mAuth;

    public KhoanChiViewModel() {
        expenseItem = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
    }

    private ListenerRegistration expenseUpdate;

    public LiveData<List<ExpenseItem>> getExpenseItem() {
        return expenseItem;
    }

    public void queryDatafromFirestore(LifecycleOwner lifecycleOwner, String selectedDate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user==null){return;}
        String userId = user.getUid();
        expenseUpdate = db.collection("users").document(userId).collection("expense").whereEqualTo("date", selectedDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        List<ExpenseItem> items = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            ExpenseItem item = doc.toObject(ExpenseItem.class);
                            item.setId(doc.getId());
                            items.add(item);
                        }
                        expenseItem.setValue(items);
                    }
                });
        lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (expenseUpdate != null) {
                        expenseUpdate.remove();
                    }
                }
            }
        });
    }
    //FETCHING DATA FROM FIRESTORE TO SUM EXPENSE
    public LiveData<Integer> queryTotalIncomeMonth(String monthYear) {
        MutableLiveData<Integer> totalExpense = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        String startMonth = "01/" + monthYear;
        String endMonth = "31/" + monthYear;
        firestore.collection("users").document(userId).collection("expense")
                .whereGreaterThanOrEqualTo("date", startMonth)
                .whereLessThanOrEqualTo("date", endMonth)
                .get()
                .addOnCompleteListener(task -> {
                    int sum = 0;
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            ExpenseItem income = document.toObject(ExpenseItem.class);
                            sum += Integer.parseInt(String.valueOf(income.getMoney()));
                        }
                        totalExpense.setValue(sum);
                    } else {
                        totalExpense.setValue(0); // Error or no data
                    }
                });
        return totalExpense;
    }
}
//        .get().addOnSuccessListener(queryDocumentSnapshots -> {
//        List<ExpenseItem> items = new ArrayList<>();
//        for (QueryDocumentSnapshot documentsnapshot: queryDocumentSnapshots){
//        ExpenseItem item = documentsnapshot.toObject(ExpenseItem.class);
//        items.add(item);
//        }
//        expenseItem.setValue(items);
//        }).addOnFailureListener(e->{});