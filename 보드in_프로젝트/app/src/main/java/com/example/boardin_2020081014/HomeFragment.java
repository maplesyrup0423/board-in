package com.example.boardin_2020081014;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

//홈(기본) 프래그먼트 처리
public class HomeFragment extends Fragment {

    FragViewModel fragViewModel;
    GameData recGameData;
    RequestQueue queue;
    Context context;
    Button btnForSearch;
    TextView txtRecommend, txtPlayer, txtTime, txtGenre, txtDifficulty;
    //랜덤 추천 게임 레이아웃 클릭 시 발생하는 JsonObjectRequest에 들어갈 php의 url
    final static private String URL = "http://10.0.2.2/BOIN/boin_ForRecommend.php";

    // 프래그먼트 전환 시 MainActivity.java의 replaceFragment(Fragment fragment)에 파라미터로 쓰이는 객체를 생성하기 위한 메소드
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String[] genreAttibute = {"genre_couple", "genre_detective", "genre_puzzle", "genre_strategy", "genre_coop", "genre_card", "genre_mafia", "genre_economy", "genre_lie", "genre_handskill", "genre_quickness", "genre_trpg"};
        String[] genres = getResources().getStringArray(R.array.genres);

        View view = inflater.inflate(R.layout.fragment_home, null);
        context = container.getContext();
        btnForSearch = view.findViewById(R.id.btnForSearch);
        txtRecommend = view.findViewById(R.id.txtRecommend);
        txtDifficulty = view.findViewById(R.id.txtDifficulty);
        txtPlayer = view.findViewById(R.id.txtPlayer);
        txtTime = view.findViewById(R.id.txtTime);
        txtGenre = view.findViewById(R.id.txtGenre);

        ConstraintLayout viewRecommend = view.findViewById(R.id.viewRecommend);

        //boardgame 테이블에서 랜덤한 레코드 하나를 JsonObject로 받아오는 Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //response의 값으로 랜덤 추천 게임 영역 값을 set
                    
                    String name, player, time, difficulty = "", youtube = "";
                    String genre[];
                    int minPlayer, maxPlayer, playtime, diff;
                    Log.d("태그", "response : " + response);
                    //보드게임 테이블의 장르 속성의 값을 체크하기 위한 배열
                    //0 -> false, 1 -> true
                    int[] gameGenre = new int[genreAttibute.length];

                    name = response.getString("game_name");
                    minPlayer = response.getInt("min_player");
                    maxPlayer = response.getInt("max_player");
                    playtime = response.getInt("play_time");
                    diff = response.getInt("difficulty");
                    youtube = response.getString("youtube_link");

                    if (minPlayer == maxPlayer) {
                        player = minPlayer + "명";
                    } else {
                        player = minPlayer + " - " + maxPlayer + "명";
                    }

                    switch (diff) {
                        case 1:
                            difficulty = "easy";
                            break;
                        case 2:
                            difficulty = "normal";
                            break;
                        case 3:
                            difficulty = "hard";
                    }

                    //분 단위로 저장된 시간을 "*시 *분" 형식으로 교체
                    if (playtime < 60) {
                        time = playtime + "분";
                    } else {
                        time = (int) (playtime / 60) + "시간 ";
                        if (playtime % 60 != 0)
                            time = time + (playtime % 60) + "분";
                    }

                    //해당하는 장르의 이름을 infoGenre에 넣어 배열로 변환한 뒤 recGameData로 넣음
                    ArrayList<String> infoGenre = new ArrayList<>();
                    for (int j = 0; j < gameGenre.length; j++) {
                        gameGenre[j] = response.getInt(genreAttibute[j]);
                        if (gameGenre[j] == 1) {
                            infoGenre.add(genres[j]);
                        }
                    }

                    genre = infoGenre.toArray(new String[infoGenre.size()]);
                    txtRecommend.setText(response.getString("game_name"));
                    
                    //랜덤 추천게임 영역 클릭 시 이동하는 GameInfoFragment에 값을 전달하기 위한 변수
                    recGameData = new GameData(name, player, time, genre, difficulty, youtube);

                } catch (Exception e) {
                    Log.d("태그", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test", error.toString());
            }
        });
        queue = Volley.newRequestQueue(context);
        queue.add((jsonObjectRequest));

        //클릭 시 GameInfoFragment로 전환
        //ViewModel을 이용하여 recGameData을 전환할 프래그먼트로 전달 
        viewRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragViewModel.setLiveData(recGameData);

                ((MainActivity) getActivity()).replaceFragment(GameInfoFragment.newInstance());
            }
        });
        
        //클릭 시 SearchFragment로 전환
        btnForSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(SearchFragment.newInstance());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragViewModel = new ViewModelProvider(requireActivity()).get(FragViewModel.class);

    }
}