
window.setTimeout(setup,2000);
var next = 0
function setup() {
	document.getElementById('chat').onkeydown = function(event) {
		if (event.keyCode == 13) {
			sendMessage();
		}
	};
	getMessages()
}
function getMessages(){
	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/login/chat/read', true);
	xhr.onload = function() {
		var text=this.responseText;
		if (text!="") {
			var console = document.getElementById('console');
			var p = document.createElement('p');

			p.style.wordWrap = 'break-word';
			p.innerHTML = text
			next = this.getResponseHeader("NextMessage");
					
			console.appendChild(p);
			//while (console.childNodes.length > 40) {
			//	console.removeChild(console.firstChild);
			//}
			console.scrollTop = console.scrollHeight;
			var title = document.title;
			document.title = "Nytt meddelande";
			//playSound();
			setTimeout(function(){
				document.title = "Chat";
			},3000);
		}

		
		window.setTimeout(getMessages,2000);
	}
	;
	xhr.send(next+"");
}
function sendMessage() {
	var message = document.getElementById('chat').value;
	if (message !== '') {

		var xhr = new XMLHttpRequest();
		xhr.open('POST', '/login/chat/post', true);
		xhr.onload = function() {
			// do something to response
			console.log(this.responseText);
		}
		;
		xhr.send(message);
		document.getElementById('chat').value = '';
	}
}
