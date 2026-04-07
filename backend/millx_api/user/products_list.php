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
 * ✅ Verify token (user/admin allowed)
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

/**
 * ✅ Fetch active products (machine + price + stock)
 */
$productQuery = mysqli_query(
    $conn,
    "SELECT 
        m.id AS machine_id,
        m.machine_name,
        IFNULL(p.price, 0) AS price,
        IFNULL(s.available_stock, 0) AS available_stock
     FROM machines m
     LEFT JOIN prices p ON m.id = p.machine_id
     LEFT JOIN stock s ON m.id = s.machine_id
     WHERE m.machine_status = 'active'"
);

$products = [];

if ($productQuery) {
    while ($row = mysqli_fetch_assoc($productQuery)) {
        $products[] = $row;
    }
}

/**
 * ✅ Success response
 */
echo json_encode([
    "status" => "success",
    "products" => $products
]);
exit;
?>
