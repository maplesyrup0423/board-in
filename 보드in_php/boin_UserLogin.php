<?php
    //로그인
    //로그인창에서 이메일과 비밀번호를 받아와서 DB에 일치하는 데이터가 있는지 검사
    $con = mysqli_connect("localhost","root","","보드in");

    $userEmail = $_POST["userEmail"];
    $userPassword = $_POST["userPassword"];
    $statement=mysqli_prepare($con,"SELECT userEmail FROM users WHERE userEmail=? and userPassword=?");


    mysqli_stmt_bind_param($statement,"ss",$userEmail,  $userPassword);
	mysqli_stmt_execute($statement);


    $response=array();
	$response["success"]=false;
    
    while(mysqli_stmt_fetch($statement)){

		$response["success"]=true;
		//$response["userEmail"]=$userEmail;
       // $response["userPassword"]=$userPassword;
	}
    echo json_encode($response);
?>