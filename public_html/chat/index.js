
window.setTimeout(setup,5000);
var next = 0
function setup() {
	document.getElementById('chat').onkeydown = function(event) {
		if (event.keyCode == 13) {
			sendMessage();
		}
	};
}
function getMessages(){
	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/login/chat/read', true);
	xhr.onload = function() {
		var console = document.getElementById('console');
		var p = document.createElement('p');

		p.style.wordWrap = 'break-word';
		p.innerHTML = this.responseText;
		next = this.getResponseHeader("NextMessage");
				
		console.appendChild(p);
		while (console.childNodes.length > 40) {
			console.removeChild(console.firstChild);
		}
		console.scrollTop = console.scrollHeight;
		var title = document.title;
		document.title = "Nytt meddelande";
		//playSound();
		setTimeout(function(){
			document.title = "Chat";
		},3000);
		
		window.setTimeout(getMessages,5000);
	}
	;
	xhr.send(last+"");
	
	
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
