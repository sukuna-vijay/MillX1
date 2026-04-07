<?php
include "../config/db.php";
header("Content-Type: application/json");

/**
 * ✅ Allow only POST
 */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
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
 * ✅ Verify token & admin role
 */
$tokenQuery = mysqli_query(
    $conn,
    "SELECT u.id, u.role
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

$user = mysqli_fetch_assoc($tokenQuery);

if ($user['role'] !== 'admin') {
    echo json_encode([
        "status" => "error",
        "message" => "Access denied. Admin only"
    ]);
    exit;
}

/**
 * ✅ Read JSON input
 */
$data = json_decode(file_get_contents("php://input"), true);

$orderId = intval($data['order_id'] ?? 0);

/**
 * 🔥 FIX APPLIED HERE
 * Convert status to lowercase to avoid case errors
 */
$orderStatus = strtolower(trim($data['status'] ?? ''));

/**
 * ✅ Input validation
 */
if ($orderId <= 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Valid order_id is required"
    ]);
    exit;
}

/**
 * ✅ Allowed statuses
 */
$allowedStatus = ['confirmed', 'milling', 'quality', 'ready'];

if (!in_array($orderStatus, $allowedStatus)) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid order status"
    ]);
    exit;
}

/**
 * ✅ Check order exists
 */
$checkOrder = mysqli_query(
    $conn,
    "SELECT id FROM orders WHERE id = $orderId"
);

if (mysqli_num_rows($checkOrder) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Order not found"
    ]);
    exit;
}

/**
 * ✅ Update order status
 */
$update = mysqli_query(
    $conn,
    "UPDATE orders 
     SET order_status = '$orderStatus'
     WHERE id = $orderId"
);

if (!$update) {
    echo json_encode([
        "status" => "error",
        "message" => "Failed to update order status"
    ]);
    exit;
}

/**
 * ✅ Success response
 */
echo json_encode([
    "status" => "success",
    "message" => "Order status updated successfully"
]);
exit;
?>
