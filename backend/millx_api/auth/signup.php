<?php
ob_start();
error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once "../config/db.php";
require_once '../config/mail.php';
require_once '../vendor/PHPMailer/src/Exception.php';
require_once '../vendor/PHPMailer/src/PHPMailer.php';
require_once '../vendor/PHPMailer/src/SMTP.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

header("Content-Type: application/json");

/* Read RAW JSON */
$raw  = file_get_contents("php://input");
$data = json_decode($raw, true);

if (!is_array($data)) {
    ob_end_clean();
    echo json_encode(["status" => "error", "message" => "Invalid JSON format"]);
    exit;
}

/* Inputs */
$name  = trim($data['name'] ?? '');
$email = trim($data['email'] ?? '');
$phone = trim($data['phone'] ?? '');
$pass  = trim($data['password'] ?? '');

/* Validation */
if ($name === '' || $email === '' || $phone === '' || $pass === '') {
    ob_end_clean();
    echo json_encode(["status" => "error", "message" => "All fields required"]);
    exit;
}

/* Email exists check */
$check = mysqli_query($conn, "SELECT id, status FROM users WHERE email = '$email'");
if (mysqli_num_rows($check) > 0) {
    $row = mysqli_fetch_assoc($check);
    if ($row['status'] == 1) {
        ob_end_clean();
        echo json_encode(["status" => "error", "message" => "Email already registered"]);
        exit;
    } else {
        mysqli_query($conn, "DELETE FROM users WHERE id = {$row['id']}");
    }
}

/* Hash password */
$hash = password_hash($pass, PASSWORD_BCRYPT);
/* Generate OTP */
$otp = sprintf("%04d", mt_rand(1000, 9999));

/* Insert user (unverified) */
$sql = "INSERT INTO users (name, email, phone, password, role, status, reset_otp, reset_otp_expires_at)
        VALUES ('$name', '$email', '$phone', '$hash', 'user', 0, '$otp', DATE_ADD(NOW(), INTERVAL 15 MINUTE))";

if (!mysqli_query($conn, $sql)) {
    ob_end_clean();
    echo json_encode(["status"  => "error", "message" => mysqli_error($conn)]);
    exit;
}

/* Send Mail */
$mail = new PHPMailer(true);
try {
    $mail->isSMTP();
    $mail->Host       = SMTP_HOST;
    $mail->SMTPAuth   = true;
    $mail->Username   = SMTP_USER;
    $mail->Password   = SMTP_PASS;
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = SMTP_PORT;
    $mail->SMTPDebug  = 0;

    $mail->SMTPOptions = [
        'ssl' => [
            'verify_peer' => false,
            'verify_peer_name' => false,
            'allow_self_signed' => true
        ]
    ];

    $mail->setFrom(SMTP_FROM_EMAIL, SMTP_FROM_NAME);
    $mail->addAddress($email, $name);
    $mail->isHTML(true);
    $mail->Subject = 'Verify Your Account - Your OTP';
    $mail->Body    = "Hello {$name},<br><br>Thank you for signing up. Your verification OTP is: <b>{$otp}</b>";
    $mail->AltBody = "Hello {$name},\n\nThank you for signing up. Your verification OTP is: {$otp}";

    $mail->send();
    
    ob_end_clean();
    echo json_encode([
        "status"  => "success",
        "message" => "Account created! OTP sent to your email."
    ]);
} catch (Exception $e) {
    ob_end_clean();
    echo json_encode([
        "status"  => "error",
        "message" => "Account created but failed to send OTP: " . $mail->ErrorInfo
    ]);
}
?>
