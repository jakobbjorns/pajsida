import static spark.Spark.*;
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
		path("/spark", ()->{
			path("/login", ()->{
				before("/*",LoginAPI.validated);
				post("", LoginAPI.login);
				post("/F56/*",HueAPI.send);
				get("/F56/*", HueAPI.send);
				put("/F56/*", HueAPI.send);
				get("/lampstatus",LampAPI.lampstatus);
				get("/dark", LampAPI.getDarkTimes);
				post("/dark", LampAPI.postDarkTimes);
				post("/set", LampAPI.setstatus);});
			
			path("/manage", ()->{
				get("/stop",ManageAPI.stop);
				get("/restart", ManageAPI.restart);
				get("/git", ManageAPI.git);});
		});
		
	}
}
