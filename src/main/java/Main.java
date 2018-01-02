import static spark.Spark.get;
import static spark.Spark.port;
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
				return "Hello World";
			}
		});
	}

}
