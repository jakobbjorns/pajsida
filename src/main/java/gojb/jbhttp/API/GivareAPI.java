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
		response2.body(objekt.readData());
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
	private String data;
	private long tid = System.currentTimeMillis();
	public GivarObjekt(String data) {
		this.data=data;
	}
	public String readData() {
		if (System.currentTimeMillis()<tid+1000*60*5) {//Newer than 5 minutes
			return data;
		}
		else {
			return "GAMMALT MÄTVÄRDE: "+data;
		}
	}
}