<?php
     //이메일, 새로운 이름/비밀번호를 받아 이름/비밀번호 변경
     $con = mysqli_connect("localhost","root","","보드in");

    $userEmail = $_POST["userEmail"];
    $NewName = $_POST["NewName"];
    $userNewPassword= $_POST["userNewPassword"];
$sql="UPDATE users SET userName ='".$NewName."', userPassword= '".$userNewPassword."' WHERE userEmail= '".$userEmail."'";
    $statement=mysqli_query($con,$sql);

   $response=array();
	$response["success"]=true;
    $response["sql"]=$sql;
  // $response["NewName"]=$NewName;
echo json_encode($response);
?>