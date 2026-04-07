<?php
include "../config/db.php"; header("Content-Type: application/json");

$headers=getallheaders();
$token=$headers['Authorization']??'';
$q=mysqli_query($conn,"SELECT user_id FROM user_tokens WHERE token='$token'");
if(mysqli_num_rows($q)==0){ echo json_encode(["status"=>"error"]); exit; }
$user_id=mysqli_fetch_assoc($q)['user_id'];

$q=mysqli_query($conn,"SELECT * FROM notifications WHERE user_id=$user_id");
$res=[]; while($r=mysqli_fetch_assoc($q)) $res[]=$r;
echo json_encode(["status"=>"success","notifications"=>$res]);
?>
