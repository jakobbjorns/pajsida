function change() {
	console.log("hej");
};
function lampa(object) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/spark/login/set', true);
	xhr.onload = function() {
		// do something to response
		console.log(this.responseText);
		if (this.responseText == "forbidden") {
			window.location.replace("/");
		}
	}
	;
	xhr.send("lampa=" + object.checked);
}
function lampa(object) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/spark/login/lampa2', true);
	xhr.onload = function() {
		// do something to response
		console.log(this.responseText);
		var status=this.status
		console.log(status);
		if (status == 403) {
			window.location.replace("/");
		}
	}
	;
	xhr.send("lampa=" + object.checked);
}
console.log('window - onload');
//4th

var xmlHttp = new XMLHttpRequest();
xmlHttp.open("GET", "/spark/login/lampstatus", true);
xmlHttp.onload = function() {
	// do something to response
	element = document.getElementById("brytare");
	console.log(this.responseText);
	var status=this.status
	console.log(status);
	if (status == 403) {
		window.location.replace("/");
	}
	element.checked = (this.responseText == 'true');

}
;

xmlHttp.send(null);

var servertider = new XMLHttpRequest();

servertider.open("GET", "/spark/login/dark", true);
servertider.onload = function() {
	// do something to response
	element = document.getElementById("tider");
	console.log(this.responseText);
	var status=this.status
	console.log(status);
	if (status == 403) {
		window.location.replace("/");
	}
	var times = this.responseText.slice(0, -1).split(";");
	times.forEach(createline);

}
;
servertider.send(null);
function createline(entry) {
	entry = entry.split("-");
	console.log(entry);
	var div = document.createElement("div");
	for (i = 0; i < 2; i++) {
		var line = document.createElement("input");
		line.className = "tid";
		line.type = "time";
		var x = entry[i].split(".");
		x[0] = ("0" + x[0]).slice(-2);
		x[1] = (x[1] + "0").slice(0, 2);
		line.value = x.join(":");
		line.onchange = function() {
			var empty=-1;
			var tider = document.getElementsByClassName("tid");
			console.log(tider);
			var result = "";
			for (var k = 0; k < tider.length; k++) {
				var a=tider[k].value.split(":").join(".") + "-" + tider[++k].value.split(":").join(".") + ";";
				console.log(a.length);
				if(a.length==12){
					result+=a;
				}
				else{
					empty=k;
				}
			}
			if(empty>0){
				tider[empty].parentElement.outerHTML = "";
			}
			tider[empty]
			console.log(result);
			var skicka = new XMLHttpRequest();
			skicka.open("POST", "/spark/login/dark", true);
			skicka.onload = function() {
				// do something to response
				console.log(this.responseText);
				var status=this.status
				console.log(status);
				if (status == 403) {
					window.location.replace("/");
				}
			}
			;

			skicka.send(result);
		}
		;
		div.appendChild(line);
	}
	element.appendChild(div);
}