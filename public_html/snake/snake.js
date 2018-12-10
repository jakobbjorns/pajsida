$(window).load(function(){
	var ctx = $('.snakeruta')[0].getContext("2d");
	var pixelstorlek = 9;
	var socket;
	var vem;
	var gameover=false;
	var paused=false;
	var pluppX=0,pluppY=0;
	var pixels = [];
	var highscores = [];
	var riktning;


//	Kolla vart klicket är. Om riktning är horizontell, kolla om
//	klickHeight-snakeHeight>0, då åker den uppålt, annars nedåt.
//	Kolla om JS swipe är snabbare än JGesture
	document.addEventListener('touchstart', function(e) {
		console.log("touch");
		if(e.touches[0].pageY>(pixelstorlek*25 + $('.snakeruta').offset().top)){
			if(e.touches[0].pageX>(pixelstorlek*25 + $('.snakeruta').offset().left)){
				socket.send("R down right");
			}
			else{ 
				socket.send("R down left");
			}
		}
		else if(e.touches[0].pageY<=(pixelstorlek*25 + $('.snakeruta').offset().top)){
			if(e.touches[0].pageX>(pixelstorlek*25 + $('.snakeruta').offset().left)){
				socket.send("R up right");
			}
			else{ 
				socket.send("R up left");
			}
		}
	}, false);
	document.body.addEventListener('touchmove', function(e){ e.preventDefault(); });
	res();
	if ('WebSocket' in window) {
		socket = new WebSocket("wss://bjorns.tk/ws/snake");
	}  else {
		console.log('Error: WebSocket stöds inte.');
	}
	socket.onopen = function () {

		var namn = prompt("Vad heter du?", "");
		console.log("Öppnar");
		if (namn==null||namn=="") {
			namn="Okänd";
		}
		var letters = '0123456789ABCDEF'.split('');
		var color = '';
		for (var i = 0; i < 6; i++ ) {
			color += letters[Math.floor(Math.random() * 16)];
		}
		socket.send("INIT "+color+" "+namn);
	}
	socket.onclose = function () {
		socket = new WebSocket("wss://bjorns.tk/ws/snake");

	};

	socket.onmessage = function (message) {
		var obj;
		try {
			obj = JSON.parse(message.data);
		} 
		catch (e) {
			if (message.data=="START"||message.data=="OPEN") {
				console.log(message.data);
			}
			else{
				console.error(message.data);
			}
			return;
		}
		var datas=obj.data;
		for (var int = 0; int < datas.length; int++) {
			var data=datas[int];
			var type=data.type;
			if(type=="plupp"){
				pluppX=data.X;
				pluppY=data.Y;
			}
			else if (type=="players") {
				pixels=[];
				var players=data.players;
				for (var int2 = 0; int2 < players.length; int2++) {
					var player=players[int2];
//					var X=player.X;
//					var Y=player.Y;
					var färg = "#"+player[0];
					for (var int3 = 1; int3 < player.length; int3++) {
						pixels.push(new Pixel(player[int3], player[++int3], färg));
					}
				}
			}
			else if(type=="highscore"){
				$('.highscore').empty();
				$('.highscore').append(
						'<tr>'+
						'<th>Spelare</th>'+
						'<th>Poäng</th>'+
						'<th>Highscore</th>'+
						'</tr>'
				);
				var highscores=data.highscore;
				for (var int3 = 0; int3 < highscores.length; int3++) {
					var highscore=highscores[int3];
//					var highscore=new Highscore(scanner);
					$('.highscore').append(
							'<tr style="color:#'+highscore.färg+';">'+
							'<td><script type="text/plain">'+highscore.namn+'</script></td>'+
							'<td>'+highscore.poäng+'</td>'+
							'<td>'+highscore.highscore+'</td>'+
							'</div>'
					);
				}
			}
			else if(type=="gameover"){
				console.log(data);
				vem=data.namn;
				gameover = true;

			}
			else if(type=="delay"){
				console.log(data.delay)
			}
			else if(type=="cleangameover"){
				gameover=false;
			}
			else if(type=="pause"){
				paused=true;
			}
			else if(type=="unpause"){
				paused=false;
			}
		}
		paint();
	};

	$(window).keydown(function (e) {
		if(e.which == 37){
			socket.send("R left");
		}
		else if(e.which == 39){
			socket.send("R right");
		}
		else if(e.which == 38){
			socket.send("R up");
		}
		else if(e.which == 40){
			socket.send("R down");
		}
		else if (e.which == 82||e.keyCode == 113){
			socket.send("RES");
		}
		else if(e.which == 32)
			socket.send("PAUSE");
	});
	function paint(){
		ctx.fillStyle="#ffffff";
		ctx.fillRect(0, 0, $('.snakeruta')[0].width, $('.snakeruta')[0].height);
		ctx.beginPath();
		ctx.moveTo(0, 0);
		ctx.lineTo(0, $('.snakeruta')[0].height);
		ctx.lineTo($('.snakeruta')[0].width,$('.snakeruta')[0].height);
		ctx.lineTo($('.snakeruta')[0].width,0);
		ctx.lineTo(0,0);
		ctx.strokeStyle = '#000000';
		ctx.stroke();
		if($("body").hasClass("smal")){
			ctx.beginPath();
			ctx.moveTo(pixelstorlek*25,0);
			ctx.lineTo(pixelstorlek*25,pixelstorlek*50);
			ctx.moveTo(0,pixelstorlek*25);
			ctx.lineTo(pixelstorlek*50,pixelstorlek*25);
			ctx.strokeStyle = '#DDDDDD';
			ctx.stroke();
		}

		ctx.beginPath();
		ctx.arc(pluppX*pixelstorlek+pixelstorlek/2+1, pluppY*pixelstorlek+pixelstorlek/2+1, pixelstorlek/2-1, 0, 2 * Math.PI, false);
		ctx.fillStyle = '#ff0000';
		ctx.fill();

		var arrayList = pixels;
		for (var i = 0; i < arrayList.length; i++) {
			var pixel = arrayList[i];
			ctx.fillStyle=pixel.color;
			ctx.fillRect(pixel.x*pixelstorlek+2, pixel.y*pixelstorlek+2, pixelstorlek-2, pixelstorlek-2);
		}
		if(paused){
			ctx.fillStyle="#0000FF";
			ctx.font=pixelstorlek*3+"px Bitter";
			ctx.fillText("Spelet pausat.", 10, $('.snakeruta')[0].height/2);
			ctx.fillText("Tryck på mellanslag för att fortsätta.", 10, $('.snakeruta')[0].height/2+pixelstorlek*3);
		}
		if (gameover) {
			ctx.fillStyle="#FF0000";
			ctx.font=pixelstorlek*3+"px Bitter";
			ctx.fillText(vem+" förlorade!",25 , $('.snakeruta')[0].height/2-25);
		}
	}
	class Pixel{
		constructor(x,y,color) {
			this.x=x;
			this.y=y;
			this.color=color;
		}
	}
	$(window).resize(function r() {
		res();
	});
	function res() {
		$('.snakeruta')[0].height=0;
		$('.snakeruta')[0].width=0;
		pixelstorlek=Math.min(Math.floor(($('.middle').height()-2)/50),Math.floor(($('.middle').width()-2)/50));
		console.log("pixel"+pixelstorlek);
		$('.snakeruta')[0].height=pixelstorlek*50+2;
		$('.snakeruta')[0].width=pixelstorlek*50+2;
	}
	function clone(obj) {
		// Handle the 3 simple types, and null or undefined
		if (null == obj || "object" != typeof obj) return obj;

		// Handle Date
		if (obj instanceof Date) {
			var copy = new Date();
			copy.setTime(obj.getTime());
			return copy;
		}

		// Handle Array
		if (obj instanceof Array) {
			var copy = [];
			for (var i = 0, len = obj.length; i < len; i++) {
				copy[i] = clone(obj[i]);
			}
			return copy;
		}

		// Handle Object
		if (obj instanceof Object) {
			var copy = {};
			for (var attr in obj) {
				if (obj.hasOwnProperty(attr)) copy[attr] = clone(obj[attr]);
			}
			return copy;
		}

		throw new Error("Unable to copy obj! Its type isn't supported.");
	}
});