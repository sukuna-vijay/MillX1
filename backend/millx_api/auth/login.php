<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include "../config/db.php";
header("Content-Type: application/json");

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status"=>"error","message"=>"Invalid request"]);
    exit;
}

/* Read JSON */
$raw = file_get_contents("php://input");
$data = json_decode($raw, true);

if (!is_array($data)) {
    echo json_encode(["status"=>"error","message"=>"Invalid JSON format"]);
    exit;
}

$email = trim($data['email'] ?? '');
$pass  = trim($data['password'] ?? '');

if ($email === '' || $pass === '') {
    echo json_encode(["status"=>"error","message"=>"Email & password required"]);
    exit;
}

/* Fetch user safely */
$stmt = mysqli_prepare($conn, "SELECT * FROM users WHERE email = ?");
mysqli_stmt_bind_param($stmt, "s", $email);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if (mysqli_num_rows($result) === 0) {
    echo json_encode(["status"=>"error","message"=>"Invalid login"]);
    exit;
}

$user = mysqli_fetch_assoc($result);

/* Password verify */
if (!password_verify($pass, $user['password'])) {
    echo json_encode(["status"=>"error","message"=>"Wrong password"]);
    exit;
}

if ($user['status'] == 0) {
    echo json_encode(["status"=>"error","message"=>"Account not verified. Please verify your email first."]);
    exit;
}

/* Generate token */
$token = bin2hex(random_bytes(32));
mysqli_query($conn,
    "INSERT INTO user_tokens (user_id, token)
     VALUES ({$user['id']}, '$token')"
);

/* Response */
echo json_encode([
    "status" => "success",
    "token"  => $token,
    "user"   => [
        "id"            => $user['id'],
        "name"          => $user['name'],
        "email"         => $user['email'],
        "role"          => $user['role'],
        "phone"         => $user['phone'],
        "profile_image" => !empty($user['profile_image']) ? 'uploads/profiles/' . $user['profile_image'] : ""
    ]
]);
