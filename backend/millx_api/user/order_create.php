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
 * ✅ Read Authorization token (Strip Bearer if present)
 */
$headers = getallheaders();
$authHeader = $headers['Authorization'] ?? '';
$token = (strpos($authHeader, 'Bearer ') === 0) ? substr($authHeader, 7) : trim($authHeader);

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
 * ✅ Verify token & get user_id
 */
$tokenQuery = mysqli_query(
    $conn,
    "SELECT user_id FROM user_tokens WHERE token = '$token'"
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
 * ✅ Read JSON input
 */
$data = json_decode(file_get_contents("php://input"), true);

// Changed from machine_id to product_id
$product_id = intval($data['product_id'] ?? 0);
$quantity   = intval($data['quantity'] ?? 0);
$total_price = floatval($data['total_price'] ?? 0); // Optional: Store price snapshot if needed

/**
 * ✅ Input validation
 */
if ($product_id <= 0 || $quantity <= 0) {
    echo json_encode([
        "status" => "error",
        "message" => "product_id and quantity are required"
    ]);
    exit;
}

/**
 * ✅ Check product exists (using prices table)
 */
$productCheck = mysqli_query(
    $conn,
    "SELECT product_id FROM prices WHERE product_id = $product_id"
);

if (mysqli_num_rows($productCheck) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid product"
    ]);
    exit;
}

/**
 * ✅ Insert order
 * Note: machine_id is passed as NULL or 0
 */
$insert = mysqli_query(
    $conn,
    "INSERT INTO orders (user_id, product_id, quantity, order_status, created_at)
     VALUES ($user_id, $product_id, $quantity, 'confirmed', NOW())"
);

if (!$insert) {
    echo json_encode([
        "status" => "error",
        "message" => "Failed to place order: " . mysqli_error($conn)
    ]);
    exit;
}

/**
 * ✅ Success response
 */
echo json_encode([
    "status" => "success",
    "message" => "Order placed successfully"
]);
exit;
?>
