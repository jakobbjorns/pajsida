import static spark.Spark.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Base64;


public class Main {

	/**
	 * @param args
	 */
	SecureRandom secureRandom;
	String session;
	boolean lampstatus;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}
	public Main() {
		// TODO Auto-generated constructor stub
		try {
			secureRandom= SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port(8181);

		get("/login", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				System.out.println("GET-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
				if(request.ip().equals("127.0.0.1")){
					response.body("hej");
				}
				else{
					response.body("forbidden");
					response.status(403);
				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});
		post("/login", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				//				for (String string : request.headers()) {
				//				System.out.println(string+"  "+request.headers(string));
				//			}
				System.out.println("POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");

				if(request.ip().equals("127.0.0.1")){
					System.out.println(request.body());
					response.body("HEJSAN");
					String id=SessionID();
					response.cookie("", "", "sessionID", id, 3600, true, true);
					response.redirect("https://bjorns.tk/admin");
					session=id;
				}
				else{
					response.body("forbidden");
					response.status(403);
				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});
		get("/login/lampstatus", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				response.body(lampstatus+"");
				System.out.println(response.body());
				return response.body();
			}
		});
		post("/login/set", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				for (String string : request.headers()) {
					System.out.println(string+"  "+request.headers(string));
				}
				System.out.println("(SET) POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
				System.out.println(request.body());
				if(request.ip().equals("127.0.0.1")){
					//anslutning från webserver
					System.err.println(session);
					System.out.println(request.cookie("sessionID"));
					if(request.cookie("sessionID").equals(session)){
						//verified
						if(request.body().startsWith("lampa")){
							if(request.body().endsWith("true")){
								System.err.println("tänd!");
								response.body("tänder");
								lampstatus=true;
							}
							else if(request.body().endsWith("false")){
								System.err.println("släck!");
								response.body("släcker");
								lampstatus=false;
							}
						}
					}
					else {
						response.body("forbidden");
						response.status(403);
					}
				}
				else{
					response.body("forbidden");
					response.status(403);
				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});

	}
	public  String SessionID () {
		String id="";

		try {
			id=new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-1").digest((secureRandom.nextInt()+"").getBytes())));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SessionID: " + id);

		return id;
	}

}
