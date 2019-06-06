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
}

function SL(){
	console.log("sl");

	var req = new XMLHttpRequest();
	var sel = document.getElementById("station");
	var stnid = sel.value;
	req.open("GET", "/SL&siteid="+stnid, true);
	req.onload = function() {
		json = JSON.parse(this.responseText);
		document.getElementById("depaturesGUN").innerHTML=""
		document.getElementById("depaturesGUS").innerHTML=""
		document.getElementById("depaturesGUI").innerHTML=""
		for (i=0;i<json.ResponseData.Metros.length;i++){
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
	dev=""
	for (i=0;depature.Deviations!=null&&i<depature.Deviations.length;i++){
		dev+=" " + depature.Deviations[i].Text +" (" + depature.Deviations[i].Consequence + ")";
	}
	d2.append(depature.DisplayTime + " " + dev)
	rad.appendChild(d1);rad.appendChild(d2);
	document.getElementById(tableid).appendChild(rad)
}
load()
