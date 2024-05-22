package com.example.bottomproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.bottomproject.ui.dashboard.KhoanThuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bottomproject.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> addKhoanThuLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_khoanthu, R.id.navigation_khoanchi, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //TODO: Navigation Fragment between Main To Khoanthu/chi
        addKhoanThuLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getBooleanExtra("transactionComplete", false)) {
                    // navigation KhoanThuFragment
                    NavController nav = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
                    nav.navigate(R.id.navigation_khoanthu);
                }
                if (data != null && data.getBooleanExtra("expenseToMain", false)) {
                    //navigation KhoanChiFragment
                    NavController nav = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
                    nav.navigate(R.id.navigation_khoanchi);
                }
            }
        });
    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        }
    };
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


}