import static spark.Spark.*;

import spark.Request;
import spark.Response;
import spark.Route;
import java.util.ArrayList;
public class Main {
	private ArrayList<String> meddelanden=new ArrayList<>();

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
			webSocket("chat", ChatAPI.class);
			post("/login/chat/read", new Route() {
				@Override
				public Object handle(Request request, Response response) throws Exception {
					//				for (String string : request.headers()) {
					//					System.out.println(string+"  "+request.headers(string));
					//				}
					//				System.out.println("(SET) POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
					String body=request.body();
					//				System.out.println(body);

					int i=0;
					try {
						i=Integer.parseInt(body);
					} catch (Exception e) {
						System.err.println("default");
					}
					String svar="";
					int size=meddelanden.size();
					for (int j = i; j < size; j++) {
						svar+=meddelanden.get(j)+"\n";
					}
					response.body(svar);
					response.header("NextMessage", (size)+"");
					return response.body();
				}
			});
			post("/login/chat/post", new Route() {
				@Override
				public Object handle(Request request, Response response) throws Exception {
					for (String string : request.headers()) {
						System.out.println(string+"  "+request.headers(string));
					}
					System.out.println("(SET) POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
					String body=request.body();
					System.out.println(body);
					meddelanden.add(body);
					response.body("OK");
					return response.body();
				}
			});
		});
	}
}
