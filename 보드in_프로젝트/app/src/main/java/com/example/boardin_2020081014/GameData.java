package com.example.boardin_2020081014;

//데이터 전달용 클래스(DTO) - 커스텀 어댑터 및 게임 정보 필요 영역에서 사용
public class GameData {
    private String name, player, time, genre[], difficulty, youtube;

    GameData(String _name, String _player, String _time, String[] _genre, String _difficulty, String _youtube) {
        name = _name;
        player = _player;
        time = _time;
        genre = _genre;
        difficulty = _difficulty;
        youtube = _youtube;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String name) {
        this.youtube = youtube;
    }
}
