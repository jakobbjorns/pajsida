package gojb.jbhttp.API;

import static spark.Spark.*;

import java.util.HashMap;

import spark.Route;
import spark.RouteGroup;

public class GivareAPI {
	static HashMap<String, GivarObjekt> givardata = new HashMap<String, GivarObjekt>();

	static Route read = (request2, response2) -> {
		String[] splats=request2.splat();
		String id=String.join("/",splats);
		GivarObjekt objekt = givardata.get(id);
		response2.body(objekt.data);
		return response2.body();
	};
	static Route write = (request2, response2) -> {
		String[] splats=request2.splat();
		String id=String.join("/",splats);
		String data=request2.body();
		givardata.put(id,new GivarObjekt(data));
		response2.body("OK");
		return response2.body();
	};
	public static RouteGroup givare = ()->{
		get("/read/*", read);
		post("/write/*",write);
	};
}
class GivarObjekt{
	public String data;
	public long tid = System.currentTimeMillis();
	public GivarObjekt(String data) {
		this.data=data;
	}
}