import static spark.Spark.*;

import com.google.gson.*;

import spark.Request;
import spark.Response;
import spark.Route;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}
	public Main() {
		// TODO Auto-generated constructor stub
		port(8181);

		get("/post", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				System.out.println("GET-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
				if(request.ip().equals("127.0.0.1")){
					response.body("hej");
				}
				else{
					response.body("forbidden");
					response.status(403);
				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});
		post("/post", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				//				for (String string : request.headers()) {
				//				System.out.println(string+"  "+request.headers(string));
				//			}
				System.out.println("POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");

				if(request.ip().equals("127.0.0.1")){

					System.out.println(request.body());
					response.body("HEJSAN");


				}
				else{
					response.body("forbidden");
					response.status(403);
				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});
	}

}
class JSON {
	/**
	 * Parses an input string as a JSON object
	 * @param theString the json to parse
	 * @return the object of the string
	 */
	public static JsonObject parseStringToJSON(String theString) throws JsonSyntaxException {
		JsonElement jsonElement = new JsonParser().parse(theString.trim());
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		return jsonObject;
	}
}

