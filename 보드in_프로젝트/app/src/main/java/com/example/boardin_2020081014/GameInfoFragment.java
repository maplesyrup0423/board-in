package com.example.boardin_2020081014;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//게임 세부정보 프래그먼트 처리
public class GameInfoFragment extends Fragment {

    FragViewModel fragViewModel;
    TextView txtGameName, txtGamePlayer, txtGameTime, txtGameGenre, txtGameDifficulty, txtGoYoutube;
    ImageView imgGoYoutube;
    Uri uri;

    // 프래그먼트 전환 시 MainActivity.java의 replaceFragment(Fragment fragment)에 파라미터로 쓰이는 객체를 생성하기 위한 메소드
    public static GameInfoFragment newInstance() {
        return new GameInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_info, null);
        txtGameName = view.findViewById(R.id.txtGameName);
        txtGamePlayer = view.findViewById(R.id.txtGamePlayer);
        txtGameTime = view.findViewById(R.id.txtGameTime);
        txtGameGenre = view.findViewById(R.id.txtGameGenre);
        txtGameDifficulty = view.findViewById(R.id.txtGameDifficulty);
        txtGoYoutube = view.findViewById(R.id.txtGoYoutube);
        imgGoYoutube = view.findViewById(R.id.imgGoYoutube);

        // 클릭 시 유튜브의 게임 룰 설명 영상으로 이동
        txtGoYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(youtubeIntent);
            }
        });

        // 클릭 시 유튜브의 게임 룰 설명 영상으로 이동
        imgGoYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(youtubeIntent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Android 아키텍처 구성요소 : 뷰-모델로  수명 주기를 고려하여 UI 관련 데이터를 저장하고 관리
        //프래그먼트 간의 데이터 전달을 위해 생성 후 변화 시 값을 받아옴
        fragViewModel = new ViewModelProvider(requireActivity()).get(FragViewModel.class);
        fragViewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<GameData>() {
            @Override
            public void onChanged(GameData gameData) {
                txtGameName.setText(gameData.getName());
                txtGamePlayer.setText(gameData.getPlayer());
                txtGameTime.setText(gameData.getTime());

                //해당하는 장르를 모두 꺼내서 # a # b ... 형식으로 교체
                String str = "";
                for(String s:gameData.getGenre()) {
                    str += "# " + s + " ";
                }
                txtGameGenre.setText(str);
                txtGameDifficulty.setText(gameData.getDifficulty());

                uri = Uri.parse(gameData.getYoutube());
            }

        });
    }
}