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
		get("/hello", new Route() {
			@Override
			public Object handle(Request req, Response res) throws Exception {
				System.out.println(req.ip());
				System.out.println(req.port());
				System.out.println(req.protocol());
				System.out.println(req.body());
				return "<html>" +
						"<head>" +
						"<title>Test</title>" +
						"<script>" +
						"        function loadXMLDoc() {" +
						"            var xhr = new XMLHttpRequest();" +
						"            xhr.open('POST', 'hello', true);" +
						"            xhr.setRequestHeader('Content-type', 'new');" +
						"            xhr.onload = function() {" +
						"                console.log(this.responseText);" +
						"            };" +
						"            xhr.send('hej');" +
						"        }" +
						"    </script>" +
						"</head>" +
						"<body>" +
						"<h1>Detta fungerar</h1>" +
						"<p>Din ip-adress är: "+req.ip()+"</p>"+
						"    <h2>Using the XMLHttpRequest Object</h2>" +
						"    <div id=\"demo\">" +
								"        <button type=\"button\" onclick='loadXMLDoc()'>Change Content</button>" +
										"    </div>" +
										
										"</body>";
							}
		});
		post("/new", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				System.out.println("/new : " + request.body());

				response.body("HEJSAN");

				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();

				return response.body();
			}
		});
		post("/hello", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				System.out.println(request.ip());
				System.out.println(request.port());
				System.out.println(request.ip());
				System.out.println(request.protocol());
				System.out.println(request.body());

				response.body("HEJSAN");

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

