<?php
include "../config/db.php";
header("Content-Type: application/json");

$headers=getallheaders();
$token=$headers['Authorization']??'';

mysqli_query($conn,"DELETE FROM user_tokens WHERE token='$token'");
echo json_encode(["status"=>"success","message"=>"Logged out"]);
?>
