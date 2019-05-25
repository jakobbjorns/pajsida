console.log('window - onload');
//4th
window.onload = function load() {
	console.log("load");
	SL();
	setdata("/F56/a","°C",0,"inne_temp")
	setdata("/F56/b","%",0,"inne_fukt")
	setdata("/F56/c","°C",1,"ute_temp")
	setdata("/spark/givare/read/c","°C",1,"gu_ute_temp")
	setdata("/spark/givare/read/d","%",0,"gu_ute_fukt")
	setdata("/spark/givare/read/a","°C",1,"gu_inne_temp")
	setdata("/spark/givare/read/b","%",0,"gu_inne_fukt")
	setTimeout(load, 10000);
}
function setdata(id,sign,decimals,elementid){
	var req = new XMLHttpRequest();
	req.open("GET", id, true);
	req.onload = function() {
		document.getElementById(elementid).innerHTML  = parseFloat(this.responseText).toFixed(decimals)+sign;
	};
	req.send(null);
	navigator.vibrate(1000);
}
function example2() {
  // For a single value you can pass in a Number rather than an Array
  var x = document.getElementById("alarm");
  x.play();
  navigator.vibrate([500,100,500,100,500,100,500,100,500,100,500,100,500,100,500,100,500,100]);
}
function SL(){
	console.log("sl");

	var req = new XMLHttpRequest();
	req.open("GET", "/SL&siteid=9183", true);
	req.onload = function() {
		json = JSON.parse(this.responseText);
		document.getElementById("depaturesGUN").innerHTML=""
		document.getElementById("depaturesGUS").innerHTML=""
		document.getElementById("depaturesGUI").innerHTML=""
		for (i=0;i<6;i++){
			metro=json.ResponseData.Metros[i]
			if (metro.JourneyDirection==1){
				appendDep("depaturesGUN",metro)
			}
			else {
				appendDep("depaturesGUS",metro)
			}
		}
		info=json.ResponseData.StopPointDeviations
		for (var i = 0; i < info.length; i++) {
			var rad = document.createElement("tr");
			var d1 = document.createElement("td");
			rad.appendChild(d1);
			d1.append(info[i].Deviation.Text)
			document.getElementById("depaturesGUI").append(rad)
		}
		console.log(json.ResponseData.Metros);
	};
	req.send(null);
}
function appendDep(tableid,depature){
	var rad = document.createElement("tr");
	var d1 = document.createElement("td");
	var d2 = document.createElement("td");
	d1.append(depature.LineNumber+" "+depature.Destination)
	d2.append(depature.DisplayTime)
	rad.appendChild(d1);rad.appendChild(d2);
	document.getElementById(tableid).appendChild(rad)
}
load()
