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
	d1.append(depature.LineNumber+" "+depature.Destination + "")
	dev=""
	for (i=0;depature.Deviations!=null&&i<depature.Deviations.length;i++){
		dev+=" " + depature.Deviations[i].Text +" (" + depature.Deviations[i].Consequence + ")";
	}
	var tabelltid = depature.TimeTabledDateTime.split("T")[1]
	var bertid = depature.ExpectedDateTime.split("T")[1]
	if (tabelltid.localeCompare(bertid)!=0){
		tabelltid=tabelltid+ " NY TID: "+bertid
	}
	d2.append(depature.DisplayTime + " ("+tabelltid+") " + dev)
	rad.appendChild(d1);rad.appendChild(d2);
	document.getElementById(tableid).appendChild(rad)
}
function getTid(){
	var day = new Date();
  	var hh = ("0"+day.getHours()).slice(-2);
  	var mm = ("0"+day.getMinutes()).slice(-2);
  	var ss = ("0"+day.getSeconds()).slice(-2);

	return hh+":"+mm+":"+ss;
}
function getDatum(){
	daglista=["Söndag","Måndag","Tisdag","Onsdag","Torsdag","Fredag","Lördag"]
	månadslista=["Januari","Februari","Mars","April","Maj","Juni","Juli","Augusti","September","Oktober","November","December"]
	var day = new Date();
	return daglista[day.getDay()]+" " + 
		day.getDate() + " "+ 
		månadslista[day.getMonth()]+" "+
		day.getFullYear();
}
function klocka(){
	document.getElementById("klocka").innerHTML= getTid();
	document.getElementById("datum").innerHTML= getDatum();
	setTimeout(klocka, 200);
}