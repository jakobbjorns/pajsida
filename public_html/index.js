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
        
        inne_fukt.innerHTML  = "100%"


    }
    ;
    xmlHttp.send(null);
    //Hämta info från hue -server
    var hue = new XMLHttpRequest();
    hue.open("GET", "/spark/login/F56/lights/2", true);
    hue.onload = function() {
        // do something to response
        var element = document.getElementById("brytare2");
        var slider = document.getElementById("ljusstyrka");
        console.log(this.responseText);

        var status = this.status
        console.log(status);
        if (status == 403) {
            window.location.replace("/");
        }
        var json = JSON.parse(this.responseText);
        element.checked = json.state.on;
        slider.value = json.state.bri;

    }
    ;
    hue.send(null);

    setTimeout(load, 60000);
}
load();