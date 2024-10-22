package com.example.boardin_2020081014;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchRequest extends StringRequest {
    final static private String URL = "http://10.0.2.2/BOIN/boin_ForSearch.php";
    private Map<String, String> parameters;

    public SearchRequest(String name, String player, String minTime, String maxTime, String genres, String difficulties, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("minTime", minTime);
        parameters.put("maxTime", maxTime);
        parameters.put("genres", genres);

        if(player.equals("상관없음")){
            parameters.put("player", "");
        } else {
            parameters.put("player", player);
        }

        if (difficulties.equals("0,1,2,")) {
            parameters.put("difficulties", "");
        } else {
            parameters.put("difficulties", difficulties);
        }
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError{
        return  parameters;
    }
}
