<?php
include "config/db.php";
$res = mysqli_query($conn, "DESCRIBE admin_tokens");
while($row = mysqli_fetch_assoc($res)) {
    echo $row['Field'] . " - " . $row['Type'] . "\n";
}
?>
