package com.example.boardin_2020081014;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.ResponseCache;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {


    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar tb =  findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText etName = findViewById(R.id.etName);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etPasswd = findViewById(R.id.etPasswd);
        final Button btnValidate = findViewById(R.id.btnValidate);
        TextView Terms_and_Conditions = findViewById(R.id.Terms_and_Conditions);
        TextView Personal_information_collection = findViewById(R.id.Personal_information_collection);
        CheckBox cb = findViewById(R.id.cb);

        //이메일 중복확인 버튼 클릭시 이메일이 null일 경우와 이메일 형식에 맞지 않는 경우 예외처리
        //위의 경우가 아닐 시 RegisterRequest의 조회결과와 비교하여 중복인 경우 예외처리를 한다.
        //중복이 아닌경우 수정이 불가하게 막는다.
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = etEmail.getText().toString();


                //이메일 null 예외처리하기
                if (userEmail.equals("")) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이메일은 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //이메일 형식 검사 및 예외처리
                String val_Email = ("^[a-zA-Z0-9]+@[a-zA-Z0-9]");
                Pattern pattern_Email = Pattern.compile(val_Email);
                Matcher matcher_Email = pattern_Email.matcher(userEmail);
                if (!matcher_Email.find()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이메일 형식을 확인해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (validate) return;

                //이메일 중복확인
                Response.Listener responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            response = response.replaceFirst("<br *?", "");

                            JSONObject jsonRespone = new JSONObject(response);
                            boolean newID = jsonRespone.getBoolean("newEmail");


                            if (newID) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 이메일입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                etEmail.setEnabled(false);
                                etEmail.setBackgroundColor(Color.GRAY);
                                btnValidate.setBackgroundColor(Color.GRAY);
                                validate = true;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 이메일입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });
        // 이용약관 및 개인정보취급 동의 클릭시 각 화면으로 이동
        Terms_and_Conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });
        Personal_information_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, PersonalInformationCollectionActivity.class);
                startActivity(intent);
            }
        });
        //회원가입 버튼 클릭시 이메일 중복확인 여부와 이용약관 동의 여부
        //모든 정보 입력 여부, 비밀번호 조건 해당여부를 검사하여 DB에 저장한다.
        Button btnMembership = findViewById(R.id.btnMembership);
        btnMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = etName.getText().toString();
                String userEmail = etEmail.getText().toString();
                String userPassword = etPasswd.getText().toString();

                //중복확인 여부
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("중복확인을 해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                //미입력 정보 확인
                if (userName.equals("") || userEmail.equals("") || userPassword.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("모든 정보를 입력해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // 체크박스 체크 확인
                if (cb.isChecked() == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이용약관 및 개인정보 수집 미동의 가입이 불가합니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //비밀번호 조건 8~16자 영어 대/소문자, 숫자, 특수문자로 구성
                String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
                String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
                Pattern pattern_symbol = Pattern.compile(val_symbol);
                Pattern pattern_alpha = Pattern.compile(val_alpha);
                Matcher matcher_symbol = pattern_symbol.matcher(userPassword);
                Matcher matcher_alpha = pattern_alpha.matcher(userPassword);

                //비밀번호 조건 미해당
                if (!matcher_symbol.find() && !matcher_alpha.find() || userPassword.length() > 16 || userPassword.length() < 8) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호는 8~16자 사이의 영어 대/소문자, 숫자, 특수문자로 구성되어야 합니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;

                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            response = response.replaceFirst("<br *?", "");
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원가입이 완료되었습니다.")
                                        .setPositiveButton("로그인 하러가기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .create();
                                dialog.show();
                                return;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원등록에 실패했습니다..")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userName, userEmail, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                // 액티비티 이동
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

