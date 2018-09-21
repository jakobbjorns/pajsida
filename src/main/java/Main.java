import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Base64;

public class Main {
	private SecureRandom secureRandom;
	private static String session;
	private Connection connect = null;
	//	private Statement statement = null;
	//	private ResultSet resultSet = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Välkommen");
		System.out.println(args);
		try {
			if (args.length>0) {
				new Main(Integer.parseInt(args[0]));
			}
			else {
				new Main(8181);

			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println("Ange endast portnummer som argument");
		}
	}
	public Main(int port) throws SQLException, ClassNotFoundException {
		// TODO Auto-generated constructor stub
		try {
			secureRandom= SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		port(8181);
		port(port);
		sqlconnect();
		openHTTP();
		while(true){
			try {
				Process p = Runtime.getRuntime().exec("curl -k https://freedns.afraid.org/dynamic/update.php?SWZodlZ4Y3dRdlFramFoVDZEMVdlUlZDOjE3MjYwNzM2");
				p.waitFor();

				BufferedReader reader =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line = reader.readLine())!= null) {
					sb.append(line);
				}
				String string = sb.toString();

				if (!string.endsWith("has not changed.")) {
					System.out.println(string);
				}
				Thread.sleep(30000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void openHTTP(){
		get("/login", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				System.out.println("GET-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
				validated(request, response,false);
				response.redirect("https://"+request.headers("remote-host"));
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});
		post("/login", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				for (String string : request.headers()) {
					System.out.println(string+"  "+request.headers(string));
				}
				System.out.println("POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
				System.out.println(request.headers("Origin"));
				String password=request.queryParams("password");
				System.out.println(request.body());
				System.out.println(password);
				password=new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-1").digest(password.getBytes())));
				System.out.println(password);
				if(password.equals("LH2WA9HlvN5+QxR+P+idBq9x3OE=")){
					// lösenhash LH2WA9HlvN5+QxR+P+idBq9x3OE=
					response.body("Inloggad");
					String id=createSessionID();
					response.cookie("", "", "sessionID", id, 60*60*24, true, true);
					response.redirect(request.headers("Origin")+"/admin");
					session=id;
				}
				else{
					String remotehost=request.headers("Origin");
					forbiddenaccess(request, response);
					response.redirect(remotehost);
				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});
		get("/login/lampstatus", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				if(validated(request, response,true)){
					try {

						ResultSet resultSet = connect.createStatement()
								.executeQuery("select * from Data WHERE Data='Lyser'");
						resultSet.next();
						response.body((resultSet.getInt("Value")==0?false:true)+"");
						System.out.println(response.body());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sqlconnect();
						return handle(request, response);
					}
				}
				return response.body();
			}
		});
		get("/login/dark", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				if(validated(request, response,false)){
					try {
						ResultSet resultSet = connect.createStatement()
								.executeQuery("select * from LampaMorkTid");
						String data="";
						while(resultSet.next()){
							data+=resultSet.getString(1)+"-"+resultSet.getString(2)+";";
						}

						response.body(data);
						System.out.println(response.body());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sqlconnect();
						return handle(request, response);
					}
				}
				return response.body();
			}
		});
		post("/login/dark", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				if(validated(request, response,false)){
					try {
						String body = request.body();
						System.out.println(body);
						Statement statement=connect.createStatement();
						statement.executeUpdate("TRUNCATE LampaMorkTid");
						if (!body.equals("")) {
							String[] tider=body.substring(0,body.length()-1).split(";");
							for (int i = 0; i < tider.length; i++) {
								String[] tid= tider[i].split("-");
								statement.executeUpdate("INSERT INTO LampaMorkTid (Start,Slut) VALUES ("+tid[0]+","+tid[1]+");");
							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sqlconnect();
					}
				}
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
				if(validated(request, response, true)&&
						request.body().startsWith("lampa")){
					if(request.body().endsWith("true")){
						System.err.println("tänd!");
						response.body("tänder");
					}
					else if(request.body().endsWith("false")){
						System.err.println("släck!");
						response.body("släcker");
					}

					connect.createStatement().executeUpdate("UPDATE Data SET Value='1' WHERE Data='Switch'");

				}
				System.out.println("Responding with: " + response.status() + ", " + response.body());
				System.out.println();
				return response.body();
			}
		});


	}
	private boolean validated(Request request, Response response,boolean requireLogin){
		//Kolla om anslutningen kommer från den lokala nginx-servern och
		// om requireLogin är true, kolla så att sessionID i cookie är sparad session
		if(request.ip().equals("127.0.0.1")&&
				requireLogin ? 
						request.cookie("sessionID")!=null&&
						request.cookie("sessionID").equals(session)
						:true){
			response.body("OK");
			return true;
		}
		else{
			forbiddenaccess(request, response);
			return false;
		}

	}
	private void forbiddenaccess(Request request, Response response){
		System.err.println("Forbidden");
		response.body("forbidden");
		response.status(403);
	}
	private void sqlconnect() throws SQLException {
		connect = DriverManager
				.getConnection("jdbc:mysql://localhost/styrning?"
						+ "user=jakob&password=furugatan10&serverTimezone=UTC");
	}
	public  String createSessionID () {
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
