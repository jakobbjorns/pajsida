package gojb.jbhttp.API;

import spark.Route;
import spark.RouteGroup;

import static spark.Spark.*;

public class Webhook {
	static Route github = (request, response) ->{
		System.out.println("Webhook! Refreshing git");
		ManageAPI.autogit();
		return response.body();
	};
	//Hash: Zk7itmP+rC0UQrtprvKWkzrlIzo=
	static RouteGroup routeGroup = () -> {
		post("/github/", github);
	};
}
