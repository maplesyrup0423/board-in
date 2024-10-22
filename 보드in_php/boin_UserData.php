<?php
    //이메일을 받아 이름,비밀번호 리턴
    $con = mysqli_connect("localhost","root","","보드in");

    $userEmail = $_POST["userEmail"];
$sql="SELECT * FROM users WHERE userEmail='".$userEmail."'";
    $statement=mysqli_query($con,$sql);


    $response=array();

$row = mysqli_fetch_array($statement);
        $response["userName"]= $row["userName"];
        $response["userPassword"]=$row["userPassword"]; 
echo json_encode($response);
?>