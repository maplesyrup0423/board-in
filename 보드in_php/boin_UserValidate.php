<?php
//이메일 중복검사
//회원가입창에서 입력받은 이메일이 DB에 있는지 검사
$con = mysqli_connect("localhost","root","","보드in");

    $userEmail = $_POST["userEmail"];
    $statement=mysqli_prepare($con,"SELECT userEmail FROM users WHERE userEmail	= ?");
    
    mysqli_stmt_bind_param($statement,"s",$userEmail);
	mysqli_stmt_execute($statement);

    $response=array();
	$response["newEmail"]=true;

	while(mysqli_stmt_fetch($statement)){
		$response["newEmail"]=false;
		//$response["userEmail"]=$userEmail;
	}
	echo json_encode($response);
?>