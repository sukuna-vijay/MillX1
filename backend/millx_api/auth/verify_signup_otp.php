<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once '../config/db.php';

$data = json_decode(file_get_contents("php://input"));

if (!empty($data->email) && !empty($data->otp)) {
    $email = trim($data->email);
    $otp = trim($data->otp);

    // Verify OTP and check expiration
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ? AND reset_otp = ? AND status = 0 AND reset_otp_expires_at > NOW()");
    $stmt->bind_param("ss", $email, $otp);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        $user_id = $user['id'];

        // Update status to 1 and clear the OTP
        $update_stmt = $conn->prepare("UPDATE users SET status = 1, reset_otp = NULL, reset_otp_expires_at = NULL WHERE id = ?");
        $update_stmt->bind_param("i", $user_id);
        
        if ($update_stmt->execute()) {
            http_response_code(200);
            echo json_encode(array("success" => true, "message" => "Account successfully verified. You can now login."));
        } else {
            http_response_code(500);
            echo json_encode(array("success" => false, "message" => "Failed to update account status."));
        }
    } else {
        http_response_code(400);
        echo json_encode(array("success" => false, "message" => "Invalid or expired OTP."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Email and OTP are required."));
}
?>
