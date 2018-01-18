import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

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
	static String session;
	static boolean lampstatus;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new Main();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Main() throws SQLException, ClassNotFoundException {
		// TODO Auto-generated constructor stub
		try {
			secureRandom= SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port(8181);
		// Setup the connection with the DB
		connect = DriverManager
				.getConnection("jdbc:mysql://localhost/styrning?"
						+ "user=jakob&password=furugatan10");

		// Statements allow to issue SQL queries to the database
		statement = connect.createStatement();

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
				response.redirect("https://bjorns.tk/");
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
				String password=request.queryParams("password");
				System.out.println(request.body());
				System.out.println(password);
				password=new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-1").digest(password.getBytes())));
				System.out.println(password);
				if(request.ip().equals("127.0.0.1")&&password.equals("LH2WA9HlvN5+QxR+P+idBq9x3OE=")){

					// lösenhash LH2WA9HlvN5+QxR+P+idBq9x3OE=
					response.body("HEJSAN");
					String id=SessionID();
					response.cookie("", "", "sessionID", id, 3600, true, true);
					response.redirect("https://bjorns.tk/admin");
					session=id;
				}
				else{
					response.body("forbidden");
					response.status(403);
					response.redirect("https://bjorns.tk/");
				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});
		get("/login/lampstatus", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				// Result set get the result of the SQL query
				try {
					resultSet = statement
							.executeQuery("select * from Data WHERE Data='Lyser'");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					statement = connect.createStatement();
					return handle(request, response);

				}
				int status;
				resultSet.next();
				status=resultSet.getInt("Value");
				if (status==0){
					lampstatus=false;
				}
				else {
					lampstatus=true;
				}
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
					if(request.cookie("sessionID")!=null&&request.cookie("sessionID").equals(session)){
						//verified
						if(request.body().startsWith("lampa")){

							if(request.body().endsWith("true")){
								System.err.println("tänd!");
								response.body("tänder");
								//								lampstatus=true;
							}
							else if(request.body().endsWith("false")){
								System.err.println("släck!");
								response.body("släcker");
								//								lampstatus=false;
							}

							statement.executeUpdate("UPDATE Data SET Value='1' WHERE Data='Switch'");
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
		while(true){
			try {
				//				System.out.println(Runtime.getRuntime().exec("curl -k https://freedns.afraid.org/dynamic/update.php?SWZodlZ4Y3dRdlFramFoVDZEMVdlUlZDOjE3MjYwNzM2"));
				Process p = Runtime.getRuntime().exec("curl -k https://freedns.afraid.org/dynamic/update.php?SWZodlZ4Y3dRdlFramFoVDZEMVdlUlZDOjE3MjYwNzM2");
				p.waitFor();

				BufferedReader reader =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line = reader.readLine())!= null) {
					sb.append(line);
				}
				Thread.sleep(30000);
				System.out.println(sb);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
