<?php
//회원가입
//회원가입 창에서 받은 닉네임, 이메일, 비밀번호를 보드in DB users 테이블에 INSERT 
$con = mysqli_connect("localhost","root","","보드in");
    $userName = $_POST["userName"];
    $userEmail = $_POST["userEmail"];
    $userPassword = $_POST["userPassword"];
   

    $statement=mysqli_prepare($con,"INSERT 
     into users VALUES(?,?,?)");

    mysqli_stmt_bind_param($statement,"sss",$userName,$userEmail,$userPassword);
	mysqli_stmt_execute($statement);

    $response=array();
	$response["success"]=true;


    echo json_encode($response);
?>