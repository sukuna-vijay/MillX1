<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once '../config/db.php';

$data = json_decode(file_get_contents("php://input"));

if (!empty($data->email) && !empty($data->otp) && !empty($data->new_password)) {
    $email = trim($data->email);
    $otp = trim($data->otp);
    $new_password = $data->new_password;

    // Verify OTP again just to be safe
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ? AND reset_otp = ? AND reset_otp_expires_at > NOW()");
    $stmt->bind_param("ss", $email, $otp);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        $user_id = $user['id'];

        // Hash the new password using BCRYPT
        $hashed_password = password_hash($new_password, PASSWORD_BCRYPT);

        // Update password and clear OTP
        $update_stmt = $conn->prepare("UPDATE users SET password = ?, reset_otp = NULL, reset_otp_expires_at = NULL WHERE id = ?");
        $update_stmt->bind_param("si", $hashed_password, $user_id);
        
        if ($update_stmt->execute()) {
            http_response_code(200);
            echo json_encode(array("success" => true, "message" => "Password reset successfully."));
        } else {
            http_response_code(500);
            echo json_encode(array("success" => false, "message" => "Unable to reset password. Please try again."));
        }

    } else {
        http_response_code(400);
        echo json_encode(array("success" => false, "message" => "Invalid or expired OTP."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Email, OTP and new password are required."));
}
?>
