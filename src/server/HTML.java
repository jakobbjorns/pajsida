package server;
import static spark.Spark.*;

public class HTML {
	public HTML() {
		// TODO Auto-generated constructor stub
		port(8080);
		get("/hello", (req, res) -> "Hello World");
	}
}
