package gojb.jbhttp.API;
import static spark.Spark.halt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import spark.Filter;
import spark.Route;

public class LoginAPI {
	private static String session;
	static Filter validated=(request, response) -> {
		String remotehost=request.headers("Origin");
		System.out.println("Remotehost: "+remotehost);
		String cookie=request.cookie("sessionID");
		System.out.println(cookie);
		if(!(request.ip().equals("127.0.0.1")&&
				cookie!=null&&
				cookie.equals(session))){
			System.err.println("Forbidden");
			halt(403);
		}
	};
	static Route login=(request, response) -> {
		//						for (String string : request.headers()) {
		//							System.out.println(string+"  "+request.headers(string));
		//						}
		String remotehost=request.headers("Origin");
		String password=request.queryParams("password");
		System.out.println(password);
		password=new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-1").digest(password.getBytes())));
		System.out.println(password);
		if(password.equals("LH2WA9HlvN5+QxR+P+idBq9x3OE=")){
			// lösenhash LH2WA9HlvN5+QxR+P+idBq9x3OE=
			response.body("Inloggad!!!!");
			String id=createSessionID();
			response.cookie("", "", "sessionID", id, 60*60*24, true, true);
			if (remotehost==null||remotehost=="null") {
				remotehost="https://bjorns.tk";
			}
			response.redirect(remotehost+"/admin");
			session=id;
		}
		else{
			response.redirect(remotehost);
		}
		return response.body();
	};

	private static String createSessionID () {
		String id="";
		try {
			SecureRandom secureRandom= SecureRandom.getInstance("SHA1PRNG");
			id=new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-1").digest((secureRandom.nextInt()+"").getBytes())));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SessionID: " + id);

		return id;
	}}
