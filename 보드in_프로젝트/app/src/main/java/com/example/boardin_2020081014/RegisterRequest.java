package com.example.boardin_2020081014;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    //final static private String URL = "http://192.168.35.10/BOIN/boin_UserRegister.php";
    final static private String URL = "http://10.0.2.2/BOIN/boin_UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userName, String userEmail, String userPassword, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userName", userName);
        parameters.put("userEmail", userEmail);
        parameters.put("userPassword", userPassword);


    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
