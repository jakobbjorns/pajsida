package gojb.jbhttp.API.aktier;

import spark.RouteGroup;

import static spark.Spark.*;

public class AktieAPI {
	public static RouteGroup aktier = ()->{
		get("/omxs30/*", Stockholmsbörsen.omxs30);
		get("/lista/*",Stockholmsbörsen.aktielista);
	};
}
