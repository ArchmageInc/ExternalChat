var chatRead		=	"/inChat.json";
var chatWrite		=	"/outChat.php";
var authenticator	=	"/minecraft_authenticator.php";
var expireDays		=	365;
var chatDelay		=	3000;

var chat_session	=	"";
var domain			=	window.location.hostname;
var chatCheck		=	0;

function login(user,password){
	$.ajax({
		url:"https://"+domain+authenticator,
		dataType: 'json',
		cache: false,
		type: 'post',
		data: {
			'user':user,
			'password':password
		},
		success: function(data){
			var response	=	data[0];
			if(response.result){
				chat_session	=	response.session;
				setSessionCookie(chat_session);
				$('#loginDialog').dialog('close');
				$('#text').focus();
			}else{
				alert('Invalid Login');
			}
		},
		error: function(jqXHR,status,error){
			alert("Failed to authenticate... sorry.\n"+error);
		}
	})
}

function setSessionCookie(sessionId){
	var expire		=	new Date();
	expire.setDate(expire.getDate()+expireDays);
	document.cookie	=	"sid="+sessionId+"; expires="+expire.toUTCString();
}

function getSessionCookie(){
	var cookies	=	document.cookie.split(";");
	for(var i=0;i<cookies.length;i++){
		var x	=	cookies[i].substr(0,cookies[i].indexOf("="));
		var y	=	cookies[i].substr(cookies[i].indexOf("=")+1);
		x		=	x.replace(/^\s+|\s+$/g,"");
		if(x=="sid"){
			return unescape(y);
		}
	}
	return false;
}

function getChat(){
	$.ajax({
		url:chatRead,
		dataType: 'json',
		cache: false,
		success: function(data){
			for (var i in data) {
				var item			=	data[i];
				var name			=	item.player;
				var source			=	item.source;
				var time			=	Date.parse(item.time);
				var message			=	item.message;
				if(time>chatCheck){
					chatCheck=time;
					$('#chatArea').append('<span class="chatName">['+name+']</span> <span class="chatText">'+message+'</span><br/>');
				}
			}
			$('#chatArea').scrollTop(9000);
		},
		error: function(jqXHR,status,error){
			alert("Failed to get messages... sorry.");
		}
	})
}
function sendMessage(message){
	$.ajax({
		url: chatWrite,
		dataType: 'json',
		cache: false,
		data: {
			'session':chat_session,
			'message':message
		},
		type: 'POST',
		success: function(data,status,jqXHR){
			var response =	data[0];
			if(!response.result){
				alert(response.response);
			}
		},
		error: function(jqXHR,status,error){
			alert('Failed to send message... sorry.');
		}
	});
}
setInterval(getChat, chatDelay);