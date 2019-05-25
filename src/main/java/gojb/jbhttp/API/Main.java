package gojb.jbhttp.API;
import static spark.Spark.*;

import gojb.jbhttp.API.aktier.AktieAPI;
import gojb.jbhttp.API.snake.SnakeAPI;

public class Main {
	public static void main(String[] args) {
		System.out.println("Välkommen");
		System.out.println(args);
		try {
			if (args.length>0) {
				new Main(Integer.parseInt(args[0]));
			}
			else {
				new Main(8181);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println("Ange endast portnummer som argument");
		}
	}
	public Main(int port) throws ClassNotFoundException {
		port(port);
		openHTTP();
	}

	private void openHTTP(){		
		webSocket("/ws/chat", ChatAPI.class);
		webSocket("/ws/snake", SnakeAPI.class);
		init();

		before("/*",ManageAPI.beforeAll);
		after("/*",ManageAPI.afterAll);
		get("/mailauth",MailAPI.auth);
		path("/spark", ()->{
			path("/login", ()->{
				post("", LoginAPI.login);
				before("/*",LoginAPI.validated); //Kontrollerar så att användaren är inloggad
				//				get("/dark", LampAPI.getDarkTimes);
				//				post("/dark", LampAPI.postDarkTimes);
				//				post("/set", LampAPI.setstatus);
				//				get("/lampstatus",LampAPI.lampstatus);
				post("/F56/*",HueAPI.send);
				get("/F56/*", HueAPI.send);
				put("/F56/*", HueAPI.send);});
			path("/manage", ()->{
				get("/stop",ManageAPI.stop);
				get("/restart", ManageAPI.restart);
				get("/git", ManageAPI.git);});
			path("/aktier/", AktieAPI.aktier);
			path("/givare/",GivareAPI.givare);
			get("/temp", TempAPI.F56);
		});
	}
}
