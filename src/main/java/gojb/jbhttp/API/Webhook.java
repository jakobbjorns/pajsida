package gojb.jbhttp.API;

import spark.HaltException;
import spark.Route;
import spark.RouteGroup;
import spark.http.matching.Halt;

import static spark.Spark.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Webhook {
	static String propfile="/home/pi/.spark.prop";
	static Properties properties = new Properties();
	static {
		try {
			properties.load(new FileInputStream(propfile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			properties.store(new FileOutputStream(propfile), "Prop-file for pajsida spark server");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static Route github = (request, response) ->{
		System.out.println("Webhook! Refreshing git");
		String sign = properties.getProperty("sign");
		String xhub = request.headers("X-Hub-Signature");
		System.out.println(sign);
		System.out.println(xhub);
		if (sign.equals(xhub)) {
			ManageAPI.autogit();
		}
		else {
			halt(403);
		}
		
		response.body("OK");
		return response.body();
	};
	//Hash: Zk7itmP+rC0UQrtprvKWkzrlIzo=
	static RouteGroup routeGroup = () -> {
		post("/github/", github);
	};
}
