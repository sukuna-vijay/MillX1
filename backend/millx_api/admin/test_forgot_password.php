<?php
// Test Script for Forgot Password Flow
echo "Starting Automated Test for Forgot Password Flow...\n\n";

$base_url = "http://localhost/millx_api/auth/";
$email = "vijay@gmail.com"; // Testing with real db user

// Helper function to send POST request
function sendPostRequest($url, $data) {
    $options = array(
        'http' => array(
            'header'  => "Content-Type: application/json\r\n",
            'method'  => 'POST',
            'content' => json_encode($data),
            'ignore_errors' => true
        )
    );
    $context  = stream_context_create($options);
    $result = file_get_contents($url, false, $context);
    return json_decode($result, true);
}

// 1. Test Forgot Password
echo "1. Testing forgot_password.php for {$email}...\n";
$forgot_data = array("email" => $email);
$forgot_res = sendPostRequest($base_url . "forgot_password.php", $forgot_data);

if (isset($forgot_res['success']) && $forgot_res['success'] == true) {
    echo "   [SUCCESS] OTP initiated and email sending complete.\n";
} else {
    echo "   [FAILED] Could not send OTP. Response: " . json_encode($forgot_res) . "\n";
    echo "   (Note: If this failed with a Mailer Error, it means config/mail.php lacks real SMTP credentials!)\n";
}

echo "\n2. Extracting OTP from Database (for testing purposes without opening email)...\n";
require_once __DIR__ . '/../config/db.php';
$stmt = $conn->prepare("SELECT reset_otp FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();
$otp = "";
if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    $otp = $user['reset_otp'];
    echo "   [SUCCESS] Found OTP in DB: {$otp}\n";
} else {
    die("   [FAILED] Could not find user or OTP.\n");
}

if (!empty($otp)) {
    echo "\n3. Testing verify_otp.php...\n";
    $verify_data = array("email" => $email, "otp" => $otp);
    $verify_res = sendPostRequest($base_url . "verify_otp.php", $verify_data);
    
    if (isset($verify_res['success']) && $verify_res['success'] == true) {
        echo "   [SUCCESS] OTP verified successfully.\n";
    } else {
        echo "   [FAILED] OTP verification failed. Response: " . json_encode($verify_res) . "\n";
    }

    echo "\n4. Testing reset_password.php...\n";
    $new_pass = "admin123";
    $reset_data = array("email" => $email, "otp" => $otp, "new_password" => $new_pass);
    $reset_res = sendPostRequest($base_url . "reset_password.php", $reset_data);
    
    if (isset($reset_res['success']) && $reset_res['success'] == true) {
        echo "   [SUCCESS] Password reset successfully to '{$new_pass}'.\n";
    } else {
        echo "   [FAILED] Password reset failed. Response: " . json_encode($reset_res) . "\n";
    }
}

echo "\n--- End of Automated Test ---\n";
?>
