<?php
$DB_HOST = "localhost";
$DB_USER = "windsekirun";
$DB_PASSWORD = "";
$DB_NAME = "windsekirun";

$conn = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME);
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$name = $_POST ['name'];
$sql = "CREATE TABLE ".$name;
$sql = $sql." (id int(255) AUTO_INCREMENT PRIMARY KEY, ";
$sql = $sql."studentNum varchar(1000) not null, ";
$sql = $sql."checked varchar(1000) not null)";
echo $sql;
echo "";

if (mysqli_query($conn, $sql)) {
    echo "Table created successfully";
} else {
    echo "Error creating table: " . mysqli_error($conn);
}

mysqli_close($conn);
?>