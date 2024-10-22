package com.example.boardin_2020081014;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyinfActivity extends AppCompatActivity {
    private AlertDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinf);

        final EditText etName = findViewById(R.id.etName);
        EditText etPasswd = findViewById(R.id.etPasswd);
        EditText etNewPasswd = findViewById(R.id.etNewPasswd);
        EditText etNewPasswd2 = findViewById(R.id.etNewPasswd2);
        Button btnOK = findViewById(R.id.btnOK);
        Toolbar tb = findViewById(R.id.tb);

        //툴바에 뒤로가기 버튼 생성
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //유저의 키값인 "userEmail"을 받는다.
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");

        //받아온 유저의 키값인 "userEmail"을 UserdataRequest로 넘긴다.
        //UserdataRequest 받은 닉네임을 etName에 넣어 보여준다.
        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
            @Override
            public void onResponse(String row) {
                try {
                    row = row.replaceFirst("<br *?", "");

                    JSONObject jsonResponse = new JSONObject(row);
                    String Name = jsonResponse.getString("userName");

                    etName.setText(Name);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        UserdataRequest userdataRequest = new UserdataRequest(userEmail, responseListener2);
        RequestQueue queue2 = Volley.newRequestQueue(MyinfActivity.this);
        queue2.add(userdataRequest);

        //확인 버튼을 눌러 유저의 정보를 변경한다.
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //각 EditText에 입력된 값을 받아온다.
                final String NewName = etName.getText().toString();
                final String userPassword = etPasswd.getText().toString();
                final String userNewPassword = etNewPasswd.getText().toString();
                final String userNewPassword2 = etNewPasswd2.getText().toString();

                //UserdataRequest를 통해 "userPassword"를 받아와 pw에 저장.
                //NewName은 공백일 수 없다.
                //userPassword, userNewPassword, userNewPassword2이 공백인 경우 유저 닉네임만 변경하는 UserNameChangeRequest 사용

                //pw 값과 userPassword이 일치하는지 확인
                //userNewPassword, userNewPassword2 값이 서로 일치하는 지 확인
                //userNewPassword 값이 비밀번호 조건에 해당하는지 확인
                //확인 경과 모두 일치한다면 유저 닉네임, 비밀번호를 변경하는 php와 연결된 UserNamePwChangeRequest 사용
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String row) {

                        try {
                            row = row.replaceFirst("<br *?", "");

                            JSONObject jsonResponse = new JSONObject(row);
                            String pw = jsonResponse.getString("userPassword");

                            //닉네임 == null
                            if (NewName.equals("")) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(MyinfActivity.this);
                                dialog = builder.setMessage("닉네임은 빈 칸일 수 없습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                return;
                            } else {
                                //비밀번호 입력란이 모두 공백이라면 닉네임만 변경
                                if (userPassword.equals("") && userNewPassword.equals("") && userNewPassword2.equals("")) {
                                    // userEmail를 보내 유저값을 확인 한 후 같이보낸 NewName 값으로 유저 닉네임 변경
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                response = response.replaceFirst("<br *?", "");

                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean success = jsonResponse.getBoolean("success");
                                                if (success) {
                                                    Toast.makeText(MyinfActivity.this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(MyinfActivity.this, MainActivity.class);
                                                    intent.putExtra("userEmail", userEmail);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    UserNameChangeRequest userNameChangeRequest = new UserNameChangeRequest(userEmail, NewName, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(MyinfActivity.this);
                                    queue.add(userNameChangeRequest);
                                } else {
                                    // 입력받은 기존비밀번호와 DB에 있는 실제 비밀번호 일치 검사
                                    if (pw.equals(userPassword)) {
                                        // 새비밀번호와 새비밀번호 확인 일치 검사
                                        if (userNewPassword.equals(userNewPassword2)) {
                                            //비밀번호 조건 8~16자 영어 대/소문자, 숫자, 특수문자로 구성
                                            String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
                                            String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
                                            Pattern pattern_symbol = Pattern.compile(val_symbol);
                                            Pattern pattern_alpha = Pattern.compile(val_alpha);
                                            Matcher matcher_symbol = pattern_symbol.matcher(userNewPassword);
                                            Matcher matcher_alpha = pattern_alpha.matcher(userNewPassword);
                                            //비밀번호 조건 일치 검사
                                            if (!matcher_symbol.find() && !matcher_alpha.find() || userNewPassword.length() > 16 || userNewPassword.length() < 8) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MyinfActivity.this);
                                                dialog = builder.setMessage("비밀번호는 8~16자 사이의 영어 대/소문자, 숫자, 특수문자로 구성되어야 합니다.")
                                                        .setPositiveButton("확인", null)
                                                        .create();
                                                dialog.show();

                                            }
                                            //닉네임 & 비밀번호 변경 php
                                            //닉네임 != null && 입력받은 기존 비밀번호와 실 비밀번호와 일치 && 새비밀번호와 새비밀번호 확인 일치 && 비밀번호 생겅조건 통과
                                            else {
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            response = response.replaceFirst("<br *?", "");


                                                            JSONObject jsonResponse = new JSONObject(response);
                                                            boolean success = jsonResponse.getBoolean("success");
                                                            if (success) {
                                                                Toast.makeText(MyinfActivity.this, "개인정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(MyinfActivity.this, MainActivity.class);
                                                                intent.putExtra("userEmail", userEmail);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };
                                                UserNamePwChangeRequest userNamePwChangeRequest = new UserNamePwChangeRequest(userEmail, NewName, userNewPassword, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(MyinfActivity.this);
                                                queue.add(userNamePwChangeRequest);

                                            }
                                        } else {
                                            AlertDialog.Builder builder =
                                                    new AlertDialog.Builder(MyinfActivity.this);
                                            dialog = builder.setMessage("새비밀번호와 새비밀번호 확인이 일치하지 않습니다.")
                                                    .setPositiveButton("확인", null)
                                                    .create();
                                            dialog.show();
                                        }
                                    } else {
                                        AlertDialog.Builder builder =
                                                new AlertDialog.Builder(MyinfActivity.this);
                                        dialog = builder.setMessage("기존 비밀번호를 틀렸습니다.")
                                                .setPositiveButton("확인", null)
                                                .create();
                                        dialog.show();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                UserdataRequest userdataRequest = new UserdataRequest(userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MyinfActivity.this);
                queue.add(userdataRequest);
            }
        });
    }


    //툴바에 뒤로가기 버튼 클릭시 이전 액티비티로 이동
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                // 액티비티 이동
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}