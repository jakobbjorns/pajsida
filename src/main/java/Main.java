import static spark.Spark.*;

import java.util.ArrayList;
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
		LoginAPI loginAPI=new LoginAPI();
		HueAPI hueAPI=new HueAPI();
		LampAPI lampAPI=new LampAPI();
		ManageAPI manageAPI= new ManageAPI();
		webSocket("/spark/chat", ChatAPI.class);
		before("/*",manageAPI.beforeAll);
		after("/*",manageAPI.afterAll);
		path("/spark", ()->{
			path("/login", ()->{
				before("/*",loginAPI.validated);
				post("", loginAPI.login);
				post("/F56/*",hueAPI.send);
				get("/F56/*", hueAPI.send);
				put("/F56/*", hueAPI.send);
				get("/lampstatus",lampAPI.lampstatus);
				get("/dark", lampAPI.getDarkTimes);
				post("/dark", lampAPI.postDarkTimes);
				post("/set", lampAPI.setstatus);});
			
			path("/manage", ()->{
				get("/stop",manageAPI.stop);
				get("/restart", manageAPI.restart);
				get("/git", manageAPI.git);});
		});
		init();
	}
}
