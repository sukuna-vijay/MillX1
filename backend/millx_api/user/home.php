<?php
include "../config/db.php";
header("Content-Type: application/json");

/**
 * ✅ Allow only GET or POST
 */
if ($_SERVER['REQUEST_METHOD'] !== 'GET' && $_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request method"
    ]);
    exit;
}

/**
 * ✅ Read Authorization token
 */
$headers = getallheaders();
$token   = trim($headers['Authorization'] ?? '');

/**
 * ✅ Token validation
 */
if ($token === '') {
    echo json_encode([
        "status" => "error",
        "message" => "Authorization token required"
    ]);
    exit;
}

/**
 * ✅ Verify token and get user_id
 */
$tokenQuery = mysqli_query(
    $conn,
    "SELECT user_id 
     FROM user_tokens 
     WHERE token = '$token'"
);

if (!$tokenQuery || mysqli_num_rows($tokenQuery) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid or expired token"
    ]);
    exit;
}

$user_id = mysqli_fetch_assoc($tokenQuery)['user_id'];

/**
 * ✅ Fetch active machines (User Home data)
 */
$machineQuery = mysqli_query(
    $conn,
    "SELECT id, machine_name 
     FROM machines 
     WHERE machine_status = 'active'"
);

$machines = [];

if ($machineQuery) {
    while ($row = mysqli_fetch_assoc($machineQuery)) {
        $machines[] = $row;
    }
}

/**
 * ✅ Success response
 */
echo json_encode([
    "status" => "success",
    "machines" => $machines
]);
exit;
?>
