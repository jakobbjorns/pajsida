console.log('window - onload');
//4th
function load() {
	console.log("load");
	
	setdata("a","°C",0,document.getElementById("inne_temp"))
	setdata("b","%",0,document.getElementById("inne_fukt"))
	setdata("c","°C",1,document.getElementById("ute_temp"))
	setTimeout(load, 15000);
}
function setdata(id,sign,decimals,element){
	var req = new XMLHttpRequest();
	req.open("GET", "/F56/"+id, true);
	req.onload = function() {
		element.innerHTML  = parseFloat(this.responseText).toFixed(decimals)+sign;
	};
	req.send(null);
}
load();