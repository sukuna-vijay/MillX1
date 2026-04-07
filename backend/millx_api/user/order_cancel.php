<?php
include "../config/db.php";
header("Content-Type: application/json");

/**
 * ✅ Allow only POST
 */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
    exit;
}

/**
 * ✅ Auth check (Strip Bearer)
 */
$headers = getallheaders();
$authHeader = $headers['Authorization'] ?? '';
$token = (strpos($authHeader, 'Bearer ') === 0) ? substr($authHeader, 7) : trim($authHeader);

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

/**
 * ✅ Read Input
 */
$data = json_decode(file_get_contents("php://input"), true);
$order_id = intval($data['order_id'] ?? 0);

if ($order_id <= 0) {
    echo json_encode(["status" => "error", "message" => "order_id is required"]);
    exit;
}

/**
 * ✅ Verify order ownership & current status
 */
$verifyQuery = mysqli_query($conn, "SELECT order_status FROM orders WHERE id = $order_id AND user_id = $user_id");
if (mysqli_num_rows($verifyQuery) === 0) {
    echo json_encode(["status" => "error", "message" => "Order not found or access denied"]);
    exit;
}

$order = mysqli_fetch_assoc($verifyQuery);
$currentStatus = strtolower($order['order_status']);

// Only allow cancellation for confirmed or processing status
if ($currentStatus !== 'confirmed' && $currentStatus !== 'processing') {
    echo json_encode(["status" => "error", "message" => "Order cannot be cancelled in status: $currentStatus"]);
    exit;
}

/**
 * ✅ Update status
 */
$update = mysqli_query($conn, "UPDATE orders SET order_status = 'cancelled' WHERE id = $order_id");

if ($update) {
    echo json_encode(["status" => "success", "message" => "Order cancelled successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Failed to cancel order: " . mysqli_error($conn)]);
}
exit;
?>
