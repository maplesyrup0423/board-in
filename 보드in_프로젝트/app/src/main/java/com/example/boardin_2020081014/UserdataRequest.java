package com.example.boardin_2020081014;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserdataRequest extends StringRequest {
    //final static private String URL = "http://192.168.35.10/BOIN/boin_UserData.php";
    final static private String URL = "http://10.0.2.2/BOIN/boin_UserData.php";
    private Map<String, String> parameters;

    public UserdataRequest(String userEmail, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userEmail", userEmail);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
