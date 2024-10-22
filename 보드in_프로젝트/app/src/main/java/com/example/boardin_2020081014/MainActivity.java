package com.example.boardin_2020081014;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

//Fragment를 담을 inLayout과 하단 네비게이션 바의 탭을 처리
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FragmentContainerView inLayout;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //유저의 키값인 "userEmail"을 받는다.
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        inLayout = findViewById(R.id.inLayout);

        //부분 화면 전환 프래그먼트 트랜젝션 선언 & 초기 부분 화면(프래그먼트) 설정
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.inLayout, HomeFragment.newInstance()).commit();

        //하단 네비게이션 리스너 지정
        bottomNavigationView.setOnItemSelectedListener(new TabSelectedListener());
    }

    //하단 네비게이션 바 탭 메뉴 선택 시 리스너 정의
    class TabSelectedListener implements BottomNavigationView.OnItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    replaceFragment(new HomeFragment());
                    return true;
                }
                case R.id.tab_search: {
                    replaceFragment(new SearchFragment());
                    return true;
                }
                case R.id.tab_mypage: {
                    Bundle bundle = new Bundle();
                    bundle.putString("email",userEmail);
                    MypageFragment mypageFragment = new MypageFragment();
                    replaceFragment(mypageFragment);
                    mypageFragment.setArguments(bundle);
                    return true;
                }
            }
            return false;
        }
    }

    //프래그먼트 간의 화면 전환을 담당하는 메소드
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.inLayout, fragment).addToBackStack(null).commit();
    }

}