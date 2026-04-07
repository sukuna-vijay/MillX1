<?php
include "../config/db.php";
header("Content-Type: application/json");

$headers = getallheaders();
$token = $headers['Authorization'] ?? '';

if($token==''){
    echo json_encode(["status"=>"error","message"=>"Token required"]);
    exit;
}

$q = mysqli_query($conn,"SELECT user_id FROM user_tokens WHERE token='$token'");
if(mysqli_num_rows($q)==0){
    echo json_encode(["status"=>"error","message"=>"Invalid token"]);
    exit;
}

$row = mysqli_fetch_assoc($q);
$user_id = $row['user_id'];
?>
