<?php
$chatJSON		=	'inChat.json';
$sessionFile	=	"chat_sessions";
$timeZone		=	"America/New_York";

$session		=	!empty($_POST['session']) ? $_POST['session'] : false;
$message		=	!empty($_POST['message']) ? $_POST['message'] : false;
date_default_timezone_set($timeZone);
if($session && $message){
	$fh			=	fopen($sessionFile,"r");
	$data		=	array();
	while( ($row=fgetcsv($fh)) !== false){
		$data[]	=	$row;
	}
	fclose($fh);
	for($i=0;$i<count($data);$i++){
		$row	=	$data[$i];
		if($row[2]==$session){
			$player	=	$row[1];
			$json	=	file_get_contents($chatJSON);
			$data	=	json_decode($json,true);
			$data[]	=	array('message'=>  $message,'time'=>date('D M j G:i:s T Y'),'source'=>'out','player'=>  $player);
			$json	=	json_encode($data);
			$fh		=	fopen($chatJSON,'w+');
			fwrite($fh, $json);
			fclose($fh);
			?>[{"result":true,"response":"Message sent"}]<?PHP
			exit();
		}
	}
	?>[{"result":false,"response":"Not authenticated"}]<?PHP
}else{
	?>[{"result":false,"response":"Improper chat structure"}]<?PHP
}
?>