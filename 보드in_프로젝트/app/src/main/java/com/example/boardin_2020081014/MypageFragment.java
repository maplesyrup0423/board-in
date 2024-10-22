package com.example.boardin_2020081014;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

//마이페이지 프래그먼트 처리
public class MypageFragment extends Fragment {
    String userEmail;
    Context context;

    // 프래그먼트 전환 시 MainActivity.java의 replaceFragment(Fragment fragment)에 파라미터로 쓰이는 객체를 생성하기 위한 메소드
    public static MypageFragment newInstance() {
        return new MypageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, null);
        context = container.getContext();
        setHasOptionsMenu(true);

        TextView tvId = view.findViewById(R.id.tvName);
        TextView tvIEm = view.findViewById(R.id.tvEmail);
        Toolbar tb = view.findViewById(R.id.tb);
        Bundle bundle = getArguments();
        Button btn1= view.findViewById(R.id.btn1);
        Button btn2= view.findViewById(R.id.btn2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //개인정보 수정 페이지로 이동하며 유저의 키값인 "userEmail"을 같이 보내준다.t
                Toast.makeText(getActivity(), "개인정보 수정 페이지로 이동", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MyinfActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃 버튼 클릭시 로그인창으로 이동시킨다.
                //이때 뒤로가기버튼 클릭시 로그인된 상태의 창이 뜨는 경우를 방지하기 위해
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); 을 추가해 이전 기록을 지워준다.
                Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
            }
        });
        //번들 안의 텍스트 불러오기
        userEmail = bundle.getString("email");

        //이메일 출력
        tvIEm.setText(userEmail);
        //이메일을 보내 DB를 조회해서 닉네임을 받아와 출력
        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
            @Override
            public void onResponse(String row) {
                try {
                    row = row.replaceFirst("<br *?", "");
                    JSONObject jsonResponse = new JSONObject(row);
                    String Name = jsonResponse.getString("userName");
                    tvId.setText(Name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        UserdataRequest userdataRequest = new UserdataRequest(userEmail, responseListener2);
        RequestQueue queue2 = Volley.newRequestQueue(context);
        queue2.add(userdataRequest);

        return view;
    }

    //개인정보 수정, 로그아웃이 들어있는 메뉴
   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mypage,menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.myinf:
                //개인정보 수정 페이지로 이동하며 유저의 키값인 "userEmail"을 같이 보내준다.
                Toast.makeText(getActivity(), "개인정보 수정 페이지로 이동", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MyinfActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
                break;
            case R.id.logout:
                //로그아웃 버튼 클릭시 로그인창으로 이동시킨다.
                //이때 뒤로가기버튼 클릭시 로그인된 상태의 창이 뜨는 경우를 방지하기 위해
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); 을 추가해 이전 기록을 지워준다.
                Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                break;
        }
        return false;
    }*/
}