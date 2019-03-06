console.log('window - onload');
//4th
window.onload = function load() {
	console.log("load");
	
	setdata("/F56/a","°C",0,"inne_temp")
	setdata("/F56/b","%",0,"inne_fukt")
	setdata("/F56/c","°C",1,"ute_temp")
	setdata("/GU/","°C",1,"gu_ute_temp")
	setTimeout(load, 15000);
}
function setdata(id,sign,decimals,elementid){
	var req = new XMLHttpRequest();
	req.open("GET", id, true);
	req.onload = function() {
		document.getElementById(elementid).innerHTML  = parseFloat(this.responseText).toFixed(decimals)+sign;
	};
	req.send(null);
}