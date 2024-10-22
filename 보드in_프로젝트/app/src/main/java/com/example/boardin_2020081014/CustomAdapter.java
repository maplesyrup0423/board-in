package com.example.boardin_2020081014;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

//리스트뷰에 넣을 커스텀 어댑터 클래스
public class CustomAdapter extends BaseAdapter {

    ArrayList<GameData> items;

    public CustomAdapter(ArrayList<GameData> gameData) {
        items = gameData;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            final Context context = viewGroup.getContext();
            view = View.inflate(context, R.layout.adpater_listview, null);
        }

        TextView txtNameAdapter = view.findViewById(R.id.txtNameAdapter);
        TextView txtPlayerAdapter = view.findViewById(R.id.txtPlayerAdapter);
        TextView txtTimeAdapter = view.findViewById(R.id.txtTimeAdapter);
        TextView txtGenreAdapter = view.findViewById(R.id.txtGenreAdapter);
        TextView txtDifficultyAdapter = view.findViewById(R.id.txtDifficultyAdapter);

        txtNameAdapter.setText(items.get(i).getName());
        txtPlayerAdapter.setText("# " + items.get(i).getPlayer());
        txtTimeAdapter.setText("# " + items.get(i).getTime());
        txtGenreAdapter.setText("# " + items.get(i).getGenre()[0]);
        txtDifficultyAdapter.setText("# " + items.get(i).getDifficulty());

        return view;
    }
}
