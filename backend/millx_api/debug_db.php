<?php
include "config/db.php";
$result = mysqli_query($conn, "DESCRIBE orders");
$cols = [];
while ($row = mysqli_fetch_assoc($result)) {
    $cols[] = $row['Field'];
}
echo json_encode($cols);
?>
