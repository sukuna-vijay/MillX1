<?php
include "../config/db.php";
$result = $conn->query("SELECT COUNT(*) as c FROM prices");
if ($result) {
    $row = $result->fetch_assoc();
    echo "Count: " . $row['c'] . "\n";
    
    $res2 = $conn->query("SELECT * FROM prices LIMIT 1");
    if ($res2 && $r = $res2->fetch_assoc()) {
        echo "First Item: " . json_encode($r) . "\n";
    }
} else {
    echo "Error: " . $conn->error . "\n";
}
?>
