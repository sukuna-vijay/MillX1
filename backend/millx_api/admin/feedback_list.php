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
 * ✅ Read Authorization token from headers
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
 * ✅ Verify token (NO ROLE CHECK)
 */
$tokenQuery = mysqli_query(
    $conn,
    "SELECT u.id
     FROM user_tokens t
     JOIN users u ON t.user_id = u.id
     WHERE t.token = '$token'"
);

if (!$tokenQuery || mysqli_num_rows($tokenQuery) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid or expired token"
    ]);
    exit;
}

/**
 * ✅ Fetch feedback list
 */
$feedbackQuery = mysqli_query(
    $conn,
    "SELECT 
        f.id,
        f.rating,
        f.message,
        f.created_at,
        u.name AS user_name
     FROM feedback f
     JOIN users u ON f.user_id = u.id
     ORDER BY f.created_at DESC"
);

$feedbackList = [];

if ($feedbackQuery) {
    while ($row = mysqli_fetch_assoc($feedbackQuery)) {
        $feedbackList[] = $row;
    }
}

/**
 * ✅ Success response
 */
echo json_encode([
    "status" => "success",
    "feedback" => $feedbackList
]);
exit;
?>
