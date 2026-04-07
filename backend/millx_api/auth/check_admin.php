<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include "../config/db.php";
header("Content-Type: application/json");

/* Get headers */
$headers = function_exists('getallheaders') ? getallheaders() : [];

$auth = '';
if (isset($headers['Authorization'])) {
    $auth = $headers['Authorization'];
} elseif (isset($headers['authorization'])) {
    $auth = $headers['authorization'];
}

if ($auth === '') {
    echo json_encode(array(
        "status" => "error",
        "message" => "Authorization token missing"
    ));
    exit;
}

/* Remove Bearer */
$token = trim(str_replace("Bearer", "", $auth));

/* Check token */
$q = mysqli_query($conn,
    "SELECT user_id FROM user_tokens WHERE token='$token'"
);

if (!$q || mysqli_num_rows($q) === 0) {
    echo json_encode(array(
        "status" => "error",
        "message" => "Unauthorized admin"
    ));
    exit;
}

/* ✅ Admin authenticated */
$res = mysqli_fetch_assoc($q);
$admin_id = $res['user_id'];
?>
