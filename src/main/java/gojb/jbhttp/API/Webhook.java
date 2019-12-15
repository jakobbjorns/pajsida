package gojb.jbhttp.API;

import spark.Route;
import spark.RouteGroup;
import static spark.Spark.*;

import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;

public class Webhook {
	static Route github = (request, response) ->{
		System.out.println("Webhook! Refreshing git");
	
		String sign = "sha1="+toHexString(hmac(request.bodyAsBytes()));
		String xhub = request.headers("X-Hub-Signature");
		//System.out.println(request.body());
		if (sign.equals(xhub)) {
			ManageAPI.autogit();
			JSONObject obj = new JSONObject(request.body());
			
			JSONObject headcommit=obj.getJSONObject("head_commit");
			JSONArray modified=headcommit.getJSONArray("modified");
			for (Object object : modified) {
				String s = (String)object;
				System.out.println(s);
				if (s.startsWith("src/main/java/")) {
					System.err.println("Java");
				}
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
		mac.init(new SecretKeySpec(Main.properties.getProperty("secret").getBytes(), "HmacSHA1"));
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
