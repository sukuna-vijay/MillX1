<?php
require '../config/db.php';
$res = $conn->query('SELECT email FROM users');
while($r = $res->fetch_assoc()) {
    echo $r['email'] . "\n";
}
?>
