<?php
    $con = mysqli_connect("localhost","root","","보드in");

    //에러 예외처리
    if(mysqli_connect_error()){
        echo "MySQL에 연결 실패 : " . mysqli_connect_error();
    }

    $allgenres = array("genre_couple", "genre_detective", "genre_puzzle", "genre_strategy", "genre_coop", "genre_card", "genre_mafia", "genre_economy", "genre_lie", "genre_handskill", "genre_quickness", "genre_trpg");

    $name = $_POST["name"];
    $player = $_POST["player"];
    $minTime = $_POST["minTime"];
    $maxTime = $_POST["maxTime"];
    $genres =  $_POST["genres"];
    $difficulties = $_POST["difficulties"];

    $genre = explode(",",$genres);
    $diff = explode(",", $difficulties);
    $hasGenre = false;
    $diffes = array();

    for ($i = 0; $i < count($genre)-1; $i++) {
        if ($genre[$i] == "1") {
            $hasGenre = true;
            break;
        }
    }

    for ($i = 0; $i < count($diff)-1; $i++) {
        switch ($diff[$i]) {
            case "0" : array_push($diffes, 1); break;
            case "1" : array_push($diffes, 2); break;
            case "2" : array_push($diffes, 3); break;
        }
    }

    $sql = "SELECT * FROM boardgame";

    function hasWhere($sr) {
        if (strpos($sr, "WHERE")) {
            $sr = $sr." AND ";
        } else {
            $sr = $sr." WHERE ";
        }
        return $sr;
    }
    
    //name 속성 검색
     if ($name != "") {
            $sql = hasWhere($sql);
            $sql = $sql."game_name LIKE '%" . $name . "%' ";
    }

    //player 속성 검색
    if ($player == "") {
        // 상관없음 -> 처리 x

    } else if($player == "7") {
        // 6명 이상
        $sql = hasWhere($sql);
        $sql = $sql."max_player >= 6 ";

    } else {
        // 1명 ~ 6명
        $sql = hasWhere($sql);
        $sql = $sql."min_player <= ".$player. " AND max_player >= ".$player;
    }

    //paly_time 속성 검색
    $min = -1;
    $max = -1;

    switch($minTime) {
        case "0" : $min = 10; break;
        case "1" : $min = 20; break;
        case "2" : $min = 30; break;
        case "3" : $min = 60; break;
        case "4" : $min = 120; break;
        case "5" : $min = 121; break;
    }

    switch($maxTime) {
        case "0" : $max = 10; break;
        case "1" : $max = 20; break;
        case "2" : $max = 30; break;
        case "3" : $max = 60; break;
        case "4" : $max = 120; break;
        case "5" : $max = 121; break;
    }

    if($min == 10 && $max == 121) {
        //모든 소요시간 -> 처리 x
    }else {
        $sql = hasWhere($sql);
        $sql = $sql."play_time BETWEEN ". $min ." AND ". $max;
    }

    //difficulty 속성 검색
    if (count($diffes) == 1) {
        // 범위 1개 -> 1 / 2 / 3
        $sql = hasWhere($sql);
        $sql = $sql."difficulty = ". $diffes[0];

    } else if (count($diffes) == 2) {
        // 범위 2개 -> 1,2 / 1,3 / 2,3
        $sql = hasWhere($sql);
        $sql = $sql."difficulty = ". $diffes[0] . " OR difficulty = ". $diffes[1];
    }

    //genre 속성 검색
    if ($hasGenre) {

        for ($i = 0; $i < count($allgenres); $i++) {
            if ($genre[$i] == "1") {
                $sql = hasWhere($sql);
                $sql = $sql . " " .$allgenres[$i]. " = true ";
            }
        }
    }
    
    $sql = $sql.";";

    $res = mysqli_query($con, $sql);
    $response = array();

    while($row = mysqli_fetch_array($res)) {
        array_push($response, $row);
    }

    echo json_encode(array("result" => $response));

    mysqli_close($con);

?>