console.log('window - onload');
//4th
function load() {
	console.log("load");
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET", "/spark/temp", true);
	xmlHttp.onload = function() {
		// do something to response
		inne_temp = document.getElementById("inne_temp");
		inne_fukt = document.getElementById("inne_fukt");
		ute_temp = document.getElementById("ute_temp");

		console.log(this.responseText);
		siffror=this.responseText.split('\n')
		inne_temp.innerHTML  = siffror[0].toFixed(0)+"°C";
		inne_fukt.innerHTML  = siffror[1].toFixed(0)+"%";
		ute_temp.innerHTML  = siffror[2].toFixed(1)+"°C";


	};

    xmlHttp.send(null);
	setTimeout(load, 15000);
}
load();