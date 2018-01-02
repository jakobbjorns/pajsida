package server;
import static spark.Spark.*;

import spark.Request;
import spark.Response;
import spark.Route;

public class HTML {
	public HTML() {
		// TODO Auto-generated constructor stub
		port(8080);
		get("/hello", new Route() {
			@Override
			public Object handle(Request req, Response res) throws Exception {
				return "Hello World";
			}
		});
	}
}
