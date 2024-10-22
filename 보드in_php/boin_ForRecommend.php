<?php
    $con = mysqli_connect("localhost","root","","보드in");

    //에러 예외처리
    if(mysqli_connect_error()){
        echo "MySQL에 연결 실패 : " . mysqli_connect_error();
    }

    $sql = "SELECT * FROM boardgame;";

    $res = mysqli_query($con, $sql);
    $response = array();

    while($row = mysqli_fetch_array($res)) {
        array_push($response, $row);
    }

    echo json_encode($response[array_rand($response)]);

    mysqli_close($con);

?>