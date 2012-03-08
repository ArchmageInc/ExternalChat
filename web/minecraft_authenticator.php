<?PHP
$sessionFile	=	"chat_sessions";

$loginURL		=	"https://login.minecraft.net";
$version		=	12;
$user			=	!empty($_POST['user']) ? $_POST['user'] : false;
$pass			=	!empty($_POST['password']) ? $_POST['password'] : false;
$domain			=	$_SERVER['HTTP_HOST'];

header("Access-Control-Allow-Origin: http://{$domain}");
if($user && $pass){
	$ch	=	curl_init($loginURL);
	curl_setopt($ch,CURLOPT_POST,true);
	curl_setopt($ch,CURLOPT_POSTFIELDS,"user={$user}&password={$pass}&version={$version}");
	curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
	$result	=	curl_exec($ch);
	if($result=='Bad login'){
		?>[{"response":"Bad Login","result":false}]<?PHP
	}elseif($result=="User not premium"){
		$username	=	$user;
		$session	=	md5(uniqid());
		$date		=	time();
		$fh			=	fopen($sessionFile,"r");
		$fh			=	fopen($sessionFile,"r");
		if(!$fh){
			?>[{"response":"Successful login, but unable to read session data","result":false,"user":"<?=$username?>","session":"<?=$session?>"}]<?
			exit();
		}
		$data		=	array();
		while( ($row=fgetcsv($fh)) !==false){
			$data[]	=	$row;
		}
		fclose($fh);
		for($i=0;$i<count($data);$i++){
			$row	=	$data[$i];
			if($row[1]==$username){
				unset($data[$i]);
			}
		}
		$data[]		=	array($date,$username,$session);
		$fh			=	fopen($sessionFile,"w+");
		if(!$fh){
			?>[{"response":"Successful login, but unable to write session data","result":false,"user":"<?=$username?>","session":"<?=$session?>"}]<?
			exit();
		}
		foreach($data as $row){
			fputcsv($fh,$row);
		}
		fclose($fh);
		?>[{"response":"Successful login, but user is not premium","result":true,"user":"<?=$username?>","session":"<?=$session?>"}]<?
	}elseif(preg_match('/[0-9]*:deprecated:.*:[A-z0-9]*/',$result)){
		$result		=	explode(":",$result);
		$username	=	$result[2];
		$session	=	$result[3];
		$date		=	time();
		$fh			=	fopen($sessionFile,"r");
		if(!$fh){
			?>[{"response":"Successful login, but unable to read session data","result":false,"user":"<?=$username?>","session":"<?=$session?>"}]<?
			exit();
		}
		$data		=	array();
		while( ($row=fgetcsv($fh)) !==false){
			$data[]	=	$row;
		}
		fclose($fh);
		for($i=0;$i<count($data);$i++){
			$row	=	$data[$i];
			if($row[1]==$username){
				unset($data[$i]);
			}
		}
		$data[]		=	array($date,$username,$session);
		$fh			=	fopen($sessionFile,"w+");
		if(!$fh){
			?>[{"response":"Successful login, but unable to write session data","result":false,"user":"<?=$username?>","session":"<?=$session?>"}]<?
			exit();
		}
		foreach($data as $row){
			fputcsv($fh,$row);
		}
		fclose($fh);
		?>[{"response":"Successful login","result":true,"user":"<?=$username?>","session":"<?=$session?>"}]<?
	}else{
		?>[{"response": "I don't know what happened, but it didn't work","result":false}]<?
	}
}else{
	?>[{"response": "Invalid login structure","result":false}]<?
}
?>