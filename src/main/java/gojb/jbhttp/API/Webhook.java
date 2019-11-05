package gojb.jbhttp.API;

import spark.Route;
import spark.RouteGroup;
import static spark.Spark.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;

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
	
		String sign = "sha1="+toHexString(hmac(request.bodyAsBytes()));
		String xhub = request.headers("X-Hub-Signature");
		System.out.println(request.body());
		if (sign.equals(xhub)) {
			ManageAPI.autogit();
			JSONObject obj = new JSONObject(request.body().substring(8));
			JSONObject headcommit=obj.getJSONObject("head_commit");
			JSONArray modified=headcommit.getJSONArray("modified");
			for (Object object : modified) {
				String s = (String)object;
				System.out.println(s);
			}
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
	static byte[] hmac(byte[] body) {
		try {
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(new SecretKeySpec(properties.getProperty("secret").getBytes(), "HmacSHA1"));
		return mac.doFinal(body);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String s = formatter.toString();
		formatter.close();
		return s;
	}
}
