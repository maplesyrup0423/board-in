package com.example.boardin_2020081014;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 게임 검색 프래그먼트 처리
public class SearchFragment extends Fragment {
    // 프래그먼트 간 데이터 전달을 위해 FragViewModel 선언
    FragViewModel fragViewModel;
    
    Context context;

    EditText editSearch;
    TextView txtPlayerFilter, txtTimeFilter, txtGenreFilter, txtDifficultyFilter, txtSeachCount;
    TextView txtFilterForPlayer, txtFilterForTime, txtFilterForGenre, txtFilterForDiff;
    // 커스텀 다이얼로그 뷰
    View dialogDifficulty, dialogTime;
    RangeSlider rangeSlider_dialog, rangeSlider_diff;
    Button btnForSearch;

    // 선택된 장르를 저장하는 변수
    List<String> selectGenre;
    // 리스트뷰에 들어갈 어댑터 객체들에 들어갈 데이터 어레이리스트
    ArrayList<GameData> gameDataList;
    String nameSearchReq, playerSearchReq, minTimeSearchReq, maxTimeSearchReq, genreSearchReq, difficultySearchReq;
    int minTimeNumber, maxTimeNumber, minDiffNumber, maxDiffNumber;

    // 프래그먼트 전환 시 MainActivity.java의 replaceFragment(Fragment fragment)에 파라미터로 쓰이는 객체를 생성하기 위한 메소드
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_search, null);

        // 클릭 시 각 조건을 지정하는 다이얼로그를 실행시킬 텍스트 뷰를 인플레이팅
        txtPlayerFilter = view.findViewById(R.id.txtPlayerFilter);
        txtTimeFilter = view.findViewById(R.id.txtTimeFilter);
        txtGenreFilter = view.findViewById(R.id.txtGenreFilter);
        txtDifficultyFilter = view.findViewById(R.id.txtDifficultyFilter);

        // 조건 다이얼로그로 지정된 조건을 보여주는 텍스트 뷰를 인플레이팅
        editSearch = view.findViewById(R.id.editSearch);
        btnForSearch = view.findViewById(R.id.btnForSearch);
        txtFilterForPlayer = view.findViewById(R.id.txtFilterForPlayer);
        txtFilterForTime = view.findViewById(R.id.txtFilterForTime);
        txtFilterForGenre = view.findViewById(R.id.txtFilterForGenre);
        txtFilterForDiff = view.findViewById(R.id.txtFilterForDiff);

        // 커스텀 어댑터를 요소로 가질 리스트뷰
        ListView lv = view.findViewById(R.id.list_view);

        // 장르 선택 시에 값 비교와 상태 저장에 쓰이는 변수들
        String[] genres = getResources().getStringArray(R.array.genres);
        final boolean[] checkGenre = new boolean[genres.length];
        selectGenre = new ArrayList<>();

        //서치 리퀘스트에 넣을 변수를 초기화
        nameSearchReq=""; playerSearchReq = "상관없음"; minTimeSearchReq = "0"; maxTimeSearchReq = "5"; genreSearchReq = ""; difficultySearchReq = "";
        //레인지 슬라이더의 값을 받는 변수를 초기화
        minTimeNumber = 0; maxTimeNumber = 5; minDiffNumber = 0; maxDiffNumber = 2;
        //데이터베이스 보드게임 테이블의 장르 속성들이 들어간 배열
        final String[] genreAttibute = {"genre_couple", "genre_detective", "genre_puzzle", "genre_strategy", "genre_coop", "genre_card", "genre_mafia", "genre_economy", "genre_lie", "genre_handskill", "genre_quickness", "genre_trpg"};

        //검색 개수 들어가는 textView
        txtSeachCount = view.findViewById(R.id.txtSeachCount);


        // 검색 버튼 클릭 리스너
        btnForSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력한 검색어 넣음("" or 문자열)
                nameSearchReq = editSearch.getText().toString();

                //SearchRequest에 들어갈 Response 리스너 정의
                Response.Listener responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        gameDataList = new ArrayList<>();
                        CustomAdapter customAdapter = new CustomAdapter(gameDataList);
                        lv.setAdapter(customAdapter);

                        try {
                            response = response.replaceFirst("<br *?", "");

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            // SQL 실행 결과로 받아온 JsonArray에서 JsonObject 하나씩 꺼내서
                            // GameData를 하나씩 생성 후 gameDataList에 넣음
                            for(int i = 0; i < jsonArray.length(); i++) {

                                String name, player, time, difficulty = "", youtube = "";
                                String[] genre;
                                int minPlayer, maxPlayer, playtime, diff;

                                //보드게임 테이블의 장르 속성의 값을 체크하기 위한 배열
                                //0 -> false, 1 -> true
                                int[] gameGenre = new int[genres.length];

                                JSONObject js = jsonArray.getJSONObject(i);

                                //커스텀어댑터 뷰에 들어갈 값들을 추출
                                name = js.getString("game_name");
                                minPlayer = js.getInt("min_player");
                                maxPlayer = js.getInt("max_player");
                                playtime = js.getInt("play_time");
                                diff = js.getInt("difficulty");
                                youtube = js.getString("youtube_link");

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

                                if (playtime < 60) {
                                    time = playtime + "분";
                                } else {
                                    time = (int) (playtime / 60) + "시간 ";
                                    if (playtime % 60 != 0)
                                        time = time + (playtime % 60) + "분";
                                }

                                // i번째 레코드가 해당하는 장르를 저장하는 infoGenre
                                // boardgame 테이블의 장르 속성명들이 저장되어 있는 배열
                                ArrayList<String> infoGenre = new ArrayList<>();
                                for (int j = 0; j < gameGenre.length; j++) {
                                    gameGenre[j] = js.getInt(genreAttibute[j]);
                                    if (gameGenre[j] == 1) {
                                        infoGenre.add(genres[j]);
                                    }
                                }

                                genre = infoGenre.toArray(new String[infoGenre.size()]);

                                GameData gameData = new GameData(name, player, time, genre, difficulty, youtube);
                                gameDataList.add(gameData);
                            }

                            // 검색 결과 레코드가 몇개 있는지 표시
                            txtSeachCount.setText("총 " + jsonArray.length() +"개");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                // SearchRequest를 선언 및 생성
                // 리스너가 아닌 각 파라미터는 검색 SQL문에 들어갈 조건을 나타냄
                SearchRequest sRequest = new SearchRequest(nameSearchReq, playerSearchReq,
                        minTimeSearchReq, maxTimeSearchReq, genreSearchReq, difficultySearchReq, responseListener);

                RequestQueue queue = Volley.newRequestQueue(context);

                if(sRequest!=null){
                    queue.add(sRequest);
                }
            }
        });

        // 리스트뷰의 아이템 클릭 시 그 아이템에 해당하는 게임의 상세 정보 프래그먼트로 전환환
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragViewModel.setLiveData(gameDataList.get(position));

                ((MainActivity) getActivity()).replaceFragment(GameInfoFragment.newInstance());
            }
        });

        //인원 수 조건을 처리하는 리스너
        txtPlayerFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("플레이 인원");
                dlg.setSingleChoiceItems(R.array.players, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] players = getResources().getStringArray(R.array.players);
                        String psr = players[i];
                        txtFilterForPlayer.setText("# " + psr);

                        switch (psr) {
                            case "상관없음":
                                playerSearchReq = "";
                                break;
                            case "1명":
                                playerSearchReq = "1";
                                break;
                            case "2명":
                                playerSearchReq = "2";
                                break;
                            case "3명":
                                playerSearchReq = "3";
                                break;
                            case "4명":
                                playerSearchReq = "4";
                                break;
                            case "5명":
                                playerSearchReq = "5";
                                break;
                            case "6명":
                                playerSearchReq = "6";
                                break;
                            case "6명 이상":
                                playerSearchReq = "7";
                                break;

                        }
                    }
                });
                dlg.setPositiveButton("확인", null);
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        //장르 조건을 처리하는 리스너
        txtGenreFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("장르");
                dlg.setMultiChoiceItems(genres, checkGenre, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            selectGenre.add(genres[i]);
                        } else if (selectGenre.contains(genres[i])) {
                            selectGenre.remove(genres[i]);
                        }
                    }
                });
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        genreSearchReq = "";
                        String str = "";

                        for (int j = 0; j < genres.length; j++) {
                            if (selectGenre.contains(genres[j])) {
                                genreSearchReq += "1,";
                                str += "# " + genres[j] + " ";
                            } else {
                                genreSearchReq += "0,";
                            }

                        }

                        txtFilterForGenre.setText(str);
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        //난이도 조건을 처리하는 리스너
        txtDifficultyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDifficulty = View.inflate(getActivity(), R.layout.dialog_difficulty, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("난이도");
                dlg.setView(dialogDifficulty);

                rangeSlider_diff = dialogDifficulty.findViewById(R.id.rangeSlider_diff);
                rangeSlider_diff.addOnSliderTouchListener(rsDiffTouchListener);
                rangeSlider_diff.setLabelBehavior(LabelFormatter.LABEL_GONE);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        difficultySearchReq = "";
                        String diff[] = getResources().getStringArray(R.array.diff);

                        // 0 - easy, 1 - normal, 2 - hard
                        // max - min : 범위가 0이면 같은 값을 지정한 것
                        //           : 범위가 1이면 min, max 레인지 값이 서로 다른 것
                        //           : 범위가 2이면 모든 난이도를 선택한 것
                        switch (maxDiffNumber-minDiffNumber) {
                            case 0: difficultySearchReq = minDiffNumber + ","; break;
                            case 1: difficultySearchReq = minDiffNumber + "," + maxDiffNumber + ","; break;
                            case 2: difficultySearchReq += "0,1,2," ;
                        }

                        if(minDiffNumber == maxDiffNumber) {
                            txtFilterForDiff.setText("# " + diff[minDiffNumber]);
                        } else {
                            txtFilterForDiff.setText("# " + diff[minDiffNumber] + " - " + diff[maxDiffNumber]);
                        }

                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        //소요 시간 조건을 처리하는 리스너
        txtTimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogTime = View.inflate(getActivity(), R.layout.dialog_time, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("소요 시간");
                dlg.setView(dialogTime);
                String[] times = getResources().getStringArray(R.array.times);

                rangeSlider_dialog = dialogTime.findViewById(R.id.rangeSlider_dialog);
                rangeSlider_dialog.addOnSliderTouchListener(rsTimeTouchListener);
                rangeSlider_dialog.setLabelBehavior(LabelFormatter.LABEL_GONE);

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        minTimeSearchReq = Integer.toString(minTimeNumber);
                        maxTimeSearchReq = Integer.toString(maxTimeNumber);
                        if(minTimeNumber == maxTimeNumber) {
                            txtFilterForTime.setText("# " + times[minTimeNumber]);
                        } else {
                            txtFilterForTime.setText("# " + times[minTimeNumber] + " - " +  times[maxTimeNumber]);
                        }

                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        return view;
        //return inflater.inflate(R.layout.fragment_search, container, false); -> 새롭게 생성해서 보내는 것이기 때문에 리스너 상호작용이 안됨
    }

    //FragViewModel 객체 생성
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragViewModel = new ViewModelProvider(requireActivity()).get(FragViewModel.class);

    }

    //소요 시간 조건의 레인지 슬라이더 리스너 정의
    private final RangeSlider.OnSliderTouchListener rsTimeTouchListener =
            new RangeSlider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(RangeSlider slider) {
                    //레인지 슬라이더 바가 시작했을 때
                }

                @Override
                public void onStopTrackingTouch(RangeSlider slider) {
                    //레인지 슬라이더 바에서 손을 땠을 때
                    //0 ~ 5
                    minTimeNumber = Math.round(slider.getValues().get(0));
                    maxTimeNumber = Math.round(slider.getValues().get(1));
                }
            };

    //난이도 조건의 레인지 슬라이더 리스너 정의
    private final RangeSlider.OnSliderTouchListener rsDiffTouchListener =
            new RangeSlider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(RangeSlider slider) {
                    //레인지 슬라이더 바가 시작했을 때
                }

                @Override
                public void onStopTrackingTouch(RangeSlider slider) {
                    //레인지 슬라이더 바에서 손을 땠을 때
                    //0 ~ 2
                    minDiffNumber = Math.round(slider.getValues().get(0));
                    maxDiffNumber = Math.round(slider.getValues().get(1));
                }
            };

}