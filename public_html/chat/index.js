document.getElementById('chat').onkeydown = function(event) {
	if (event.keyCode == 13) {
		sendMessage();
	}
};
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
