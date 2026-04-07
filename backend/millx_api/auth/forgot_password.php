<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once '../config/db.php';
require_once '../config/mail.php';

// Include PHPMailer files
require_once '../vendor/PHPMailer/src/Exception.php';
require_once '../vendor/PHPMailer/src/PHPMailer.php';
require_once '../vendor/PHPMailer/src/SMTP.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

$data = json_decode(file_get_contents("php://input"));

if (!empty($data->email)) {
    $email = trim($data->email);

    // Check if user exists
    $stmt = $conn->prepare("SELECT id, name FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        $name = $user['name'];

        // Generate a 4-digit OTP
        $otp = sprintf("%04d", mt_rand(1000, 9999));

        // Update the reset_otp and expiration time (15 mins from now)
        $update_stmt = $conn->prepare("UPDATE users SET reset_otp = ?, reset_otp_expires_at = DATE_ADD(NOW(), INTERVAL 15 MINUTE) WHERE email = ?");
        $update_stmt->bind_param("ss", $otp, $email);
        $update_stmt->execute();

        // Send Email with OTP
        $mail = new PHPMailer(true);

        try {
            // Server settings
            $mail->isSMTP();
            $mail->Host       = SMTP_HOST;
            $mail->SMTPAuth   = true;
            $mail->Username   = SMTP_USER;
            $mail->Password   = SMTP_PASS;
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
            $mail->Port       = SMTP_PORT;

            // SSL options for XAMPP / Local development
            $mail->SMTPOptions = array(
                'ssl' => array(
                    'verify_peer' => false,
                    'verify_peer_name' => false,
                    'allow_self_signed' => true
                )
            );

            // Recipients
            $mail->setFrom(SMTP_FROM_EMAIL, SMTP_FROM_NAME);
            $mail->addAddress($email, $name);

            // Content
            $mail->isHTML(true);
            $mail->Subject = 'Your Password Reset OTP';
            $mail->Body    = "Hello {$name},<br><br>You requested a password reset. Here is your 4-digit OTP: <b>{$otp}</b><br><br>This OTP is valid for 15 minutes.<br><br>If you did not request this, please ignore this email.";
            $mail->AltBody = "Hello {$name},\n\nYou requested a password reset. Here is your 4-digit OTP: {$otp}\n\nThis OTP is valid for 15 minutes.\n\nIf you did not request this, please ignore this email.";

            $mail->send();
            ob_end_clean();
            http_response_code(200);
            echo json_encode(array("success" => true, "message" => "OTP sent to email successfully."));
        } catch (Exception $e) {
            ob_end_clean();
            http_response_code(500);
            echo json_encode(array("success" => false, "message" => "Message could not be sent. Mailer Error: {$mail->ErrorInfo}"));
        }
    } else {
        http_response_code(404);
        echo json_encode(array("success" => false, "message" => "Email address not found."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Email is required."));
}
?>
