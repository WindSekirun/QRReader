<?php
 
$response = array();

require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$result = mysql_query("SELECT *FROM data_default_form") or die(mysql_error());
 
if (mysql_num_rows($result) > 0) {
    $response["data"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        $data = array();
        $data["studentNum"] = $row["studentNum"];
        array_push($response["data"], $data);
    }

    $response["success"] = 1;
    echo json_encode($response);
} else {
    $response["success"] = 0;
    echo json_encode($response);
}
?>
