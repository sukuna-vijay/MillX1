<?php
include "../config/db.php";
header("Content-Type: application/json");

// Capture ALL PHP errors into a file for debugging
ini_set('display_errors', 0);
error_reporting(E_ALL);

function catch_error($errno, $errstr, $errfile, $errline) {
    file_put_contents("../error_log.txt", "ERROR: $errstr in $errfile line $errline \n", FILE_APPEND);
}
set_error_handler("catch_error");

$authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? "";
if (empty($authHeader) && function_exists('apache_request_headers')) {
    $h = apache_request_headers();
    $authHeader = $h['Authorization'] ?? $h['authorization'] ?? "";
}

$token = (strpos($authHeader, 'Bearer ') === 0) ? trim(substr($authHeader, 7)) : trim($authHeader);
$token = mysqli_real_escape_string($conn, $token);

if ($token === '') {
    echo json_encode(["status" => "error", "message" => "Authorization token required"]);
    exit;
}

$tokenQuery = mysqli_query($conn, "SELECT user_id FROM user_tokens WHERE token = '$token'");
if (!$tokenQuery || mysqli_num_rows($tokenQuery) === 0) {
    echo json_encode(["status" => "error", "message" => "Invalid token"]);
    exit;
}
$user_id = mysqli_fetch_assoc($tokenQuery)['user_id'];

// Try the query. If it fails, log the SQL error.
$sql = "SELECT 
            o.id AS order_id,
            o.quantity,
            o.order_status,
            o.created_at,
            p.product_name,
            p.image,
            p.price,
            p.unit
         FROM orders o
         LEFT JOIN prices p ON o.product_id = p.product_id
         WHERE o.user_id = $user_id
         ORDER BY o.created_at DESC";

$q = mysqli_query($conn, $sql);
if (!$q) {
    file_put_contents("../error_log.txt", "SQL Error: " . mysqli_error($conn) . " Query: " . $sql . "\n", FILE_APPEND);
    echo json_encode(["status" => "error", "message" => "SQL Error occurred. Check logs."]);
    exit;
}

$orders = [];
while ($row = mysqli_fetch_assoc($q)) {
    $orders[] = $row;
}
echo json_encode(["status" => "success", "orders" => $orders]);
exit;
?>
