console.log('window - onload');
//4th
function load() {
	console.log("load");
	
	setdata("a","°C",0,"inne_temp")
	setdata("b","%",0,"inne_fukt")
	setdata("c","°C",1,"ute_temp")
	setTimeout(load, 15000);
}
function setdata(id,sign,decimals,elementid){
	var req = new XMLHttpRequest();
	req.open("GET", "/F56/"+id, true);
	req.onload = function() {
		document.getElementById(elementid).innerHTML  = parseFloat(this.responseText).toFixed(decimals)+sign;
	};
	req.send(null);
}
load();