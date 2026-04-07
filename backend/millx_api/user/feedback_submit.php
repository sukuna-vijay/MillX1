<?php
include "../config/db.php"; header("Content-Type: application/json");

$headers=getallheaders();
$token=$headers['Authorization']??'';
$q=mysqli_query($conn,"SELECT user_id FROM user_tokens WHERE token='$token'");
if(mysqli_num_rows($q)==0){ echo json_encode(["status"=>"error"]); exit; }
$user_id=mysqli_fetch_assoc($q)['user_id'];

$data=json_decode(file_get_contents("php://input"),true);
mysqli_query($conn,"INSERT INTO feedback(user_id,rating,message)
VALUES($user_id,{$data['rating']},'{$data['message']}')");

echo json_encode(["status"=>"success"]);
?>
