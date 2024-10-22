package com.example.boardin_2020081014;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
public class LoginActivity extends AppCompatActivity {
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnMembership = findViewById(R.id.btnMembership); // 회원가입으로 이동하는 버튼
        final EditText etEmail = findViewById(R.id.etEmail); //이메일
        final EditText etPasswd = findViewById(R.id.etPasswd);//비밀번호
        final Button btnLogin = findViewById(R.id.btnLogin);//로그인버튼

        //버튼 클릭시 회원가입 창으로 이동
        btnMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });



        //로그인 버튼 클릭시 EditText에 입력된 이메일과 비밀번호를 LoginRequest로 보낸다.
        //LoginRequest에서 받아온 success 값이 true일 시 로그인 성공이라는 토스트 메세지를 보여준다.
        //메인 화면으로 이동하며 유저의 키값인 "userEmail"을 같이 보내준다.
        //LoginRequest에서 받아온 success 값이 false 일 경우 "계정을 다시 확인하세요." 라는 다이얼로그를 보여준다.
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userEmail = etEmail.getText().toString();
                final String userPassword = etPasswd.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            response = response.replaceFirst("<br *?", "");
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            //로그인 성공 시 메인화면으로 이동
                            if (success) {
                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userEmail", userEmail); // userEmail 값 전송
                                startActivity(intent);
                                finish();
                            }
                            //로그인 실패
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요.")
                                        .setNegativeButton("다시 시도", null)
                                        .create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userEmail, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }
        });
    }

}
