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
$token = trim($headers['Authorization'] ?? '');

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
 * ✅ Verify token & ADMIN role
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

$admin = mysqli_fetch_assoc($tokenQuery);

if ($admin['role'] !== 'admin') {
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

$userId    = intval($data['user_id'] ?? 0);
$machineId = intval($data['machine_id'] ?? 0);
$quantity  = intval($data['quantity'] ?? 0);

/**
 * ✅ Input validation
 */
if ($userId <= 0 || $machineId <= 0 || $quantity <= 0) {
    echo json_encode([
        "status" => "error",
        "message" => "user_id, machine_id and quantity are required"
    ]);
    exit;
}

/**
 * ✅ Check USER exists
 */
$userCheck = mysqli_query($conn, "SELECT id FROM users WHERE id = $userId");
if (mysqli_num_rows($userCheck) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "User not found"
    ]);
    exit;
}

/**
 * ✅ Check MACHINE exists
 */
$machineCheck = mysqli_query($conn, "SELECT id FROM machines WHERE id = $machineId");
if (mysqli_num_rows($machineCheck) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Machine not found"
    ]);
    exit;
}

/**
 * ✅ Insert order (default status = confirmed)
 */
$insert = mysqli_query(
    $conn,
    "INSERT INTO orders (user_id, machine_id, quantity, order_status)
     VALUES ($userId, $machineId, $quantity, 'confirmed')"
);

if (!$insert) {
    echo json_encode([
        "status" => "error",
        "message" => "Failed to add order"
    ]);
    exit;
}

/**
 * ✅ Success response
 */
echo json_encode([
    "status" => "success",
    "message" => "Order added successfully"
]);
exit;
?>
