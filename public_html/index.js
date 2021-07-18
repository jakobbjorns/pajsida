console.log('window - onload');
//4th
window.onload = function load() {
	console.log("load");
	fu();
	SL();
	setdata("/F56/a", "°C", 0, "inne_temp")
	// setdata("/F56/b","%",0,"inne_fukt")
	setdata("/F56/c", "°C", 1, "ute_temp")
	// setdata("/F56/d","%",0,"ute_fukt")
	setdata("https://sthlm." + window.location.host + "/termometer/0", "°C", 1, "la_ute_temp")
	// setdata("https://sthlm."+window.location.host+"/termometer/d","%",0,"la_ute_fukt")
	setdata("https://sthlm." + window.location.host + "/termometer/1", "°C", 1, "la_inne_temp")
	// setdata("https://sthlm."+window.location.host+"/termometer/b","%",0,"la_inne_fukt")
	setdata("/FUi/a", "°C", 1, "fu_inne_temp")
	// setdata("/FUi/b","%",0,"fu_inne_fukt")
	setTimeout(load, 10000);
}
function setdata(id, sign, decimals, elementid) {
	var req = new XMLHttpRequest();
	req.timeout = 10000; // time in milliseconds
	req.open("GET", id, true);
	req.onload = function () {
		document.getElementById(elementid).innerHTML = parseFloat(this.responseText).toFixed(decimals) + sign;
	};
	req.send(null);
}
function fu() {
	var req = new XMLHttpRequest();
	req.timeout = 2000; // time in milliseconds
	req.open("GET", "/FU", true);

	req.onload = function () {
		var json = JSON.parse(this.responseText);
		var temp = json.value;
		document.getElementById("fu_ute_temp").innerHTML = parseFloat(temp).toFixed(1) + "°C";
	};
	req.send(null);
}
function SL() {
	console.log("sl");

	var req = new XMLHttpRequest();
	var sel = document.getElementById("station");
	var stnid = sel.value;
	req.open("GET", "/SL&siteid=" + stnid, true);
	req.onload = function () {
		json = JSON.parse(this.responseText);
		document.getElementById("depaturesGUN").innerHTML = ""
		document.getElementById("depaturesGUS").innerHTML = ""
		document.getElementById("depaturesGUI").innerHTML = ""
		for (i = 0; i < json.ResponseData.Metros.length; i++) {
			metro = json.ResponseData.Metros[i]
			if (metro.JourneyDirection == 1) {
				appendDep("depaturesGUN", metro)
			}
			else {
				appendDep("depaturesGUS", metro)
			}
		}
		appendtext("depaturesGUN","<b>Bussar</b>")

		for (i = 0; i < json.ResponseData.Buses.length; i++) {
			metro = json.ResponseData.Buses[i]
			if (metro.JourneyDirection == 1) {
				appendDep("depaturesGUN", metro)
			}
			else {
				appendDep("depaturesGUS", metro)
			}
		}
		info = json.ResponseData.StopPointDeviations
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
function appendDep(tableid, depature) {
	var rad = document.createElement("tr");
	var d1 = document.createElement("td");
	var d2 = document.createElement("td");
	d1.append(depature.LineNumber + " " + depature.Destination + "")
	dev = ""
	for (i = 0; depature.Deviations != null && i < depature.Deviations.length; i++) {
		dev += " " + depature.Deviations[i].Text + " (" + depature.Deviations[i].Consequence + ")";
	}
	d2.append(depature.DisplayTime + " " + dev)
	rad.appendChild(d1); rad.appendChild(d2);
	document.getElementById(tableid).appendChild(rad)
}

function appendtext(tableid, text) {
	var rad = document.createElement("tr");
	var d1 = document.createElement("td");
	var b = document.createElement("b");
	b.append(text)
	d1.append(b)
	rad.appendChild(d1);
	document.getElementById(tableid).appendChild(rad)
}

function getTid() {
	var day = new Date();
	var hh = ("0" + day.getHours()).slice(-2);
	var mm = ("0" + day.getMinutes()).slice(-2);
	var ss = ("0" + day.getSeconds()).slice(-2);

	return hh + ":" + mm + ":" + ss;
}
function getDatum() {
	daglista = ["Söndag", "Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag"]
	månadslista = ["Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Augusti", "September", "Oktober", "November", "December"]
	var day = new Date();
	return daglista[day.getDay()] + " " +
		day.getDate() + " " +
		månadslista[day.getMonth()] + " " +
		day.getFullYear();
}
function klocka() {
	document.getElementById("klocka").innerHTML = getTid();
	document.getElementById("datum").innerHTML = getDatum();
	setTimeout(klocka, 200);
}
