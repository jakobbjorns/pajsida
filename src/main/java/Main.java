import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;


public class Main {
	private SecureRandom secureRandom;
	private static String session;
	private Connection connect = null;
	private ArrayList<String> meddelanden=new ArrayList<>();
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
		new SnakeServer();
		//		while(true){
		//			try {
		//				Process p = Runtime.getRuntime().exec("curl -k https://freedns.afraid.org/dynamic/update.php?SWZodlZ4Y3dRdlFramFoVDZEMVdlUlZDOjE3MjYwNzM2");
		//				p.waitFor();
		//
		//				BufferedReader reader =
		//						new BufferedReader(new InputStreamReader(p.getInputStream()));
		//				StringBuffer sb = new StringBuffer();
		//				String line = "";
		//				while ((line = reader.readLine())!= null) {
		//					sb.append(line);
		//				}
		//				String string = sb.toString();
		//
		//				if (!string.endsWith("has not changed.")) {
		//					System.out.println(string);
		//				}
		//				Thread.sleep(30000);
		//			} catch (Exception e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		}
	}
	static void connect(HttpURLConnection connection) throws IOException, InterruptedException {
		while (connection.getResponseCode()!=200) {
			Thread.sleep(100);
			System.out.println(connection.getResponseCode());
			System.out.println(connection.getResponseMessage());
		}
		InputStreamReader reader = new InputStreamReader(connection.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(reader);
		System.out.println(bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())));
	}
	private void openHTTP(){
		before("/*",(request,response)->{
			System.out.println();
			System.out.println(request.requestMethod()+"-request (" +request.uri() +" "+ request.protocol()+") från: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
			System.out.println("hejsan");
		});
		after("/*",(request,response)->{
			System.out.println("Responding with: " + response.status() + ", " + response.body());
		});
		//		before("/*",(request,response)->{
		//			response.redirect("http://bjorns.tk/",302);	
		//		});
		path("/spark", ()->{
			path("/test", ()->{
				before((request,response)->{
					System.out.println("before");
				});
				before("*",(request,response)->{
					System.out.println("before2");
				});
				before("",(request,response)->{
					System.out.println("before3");
				});
				get("",(request,response)->{
					System.out.println("hej");
					response.body("hejsan");
					return response.body();
				});
			});
			path("/manage", ()->{
				get("", (request, response) -> {
					return response.body();
				});
				get("/stop", (request, response) -> {
					System.out.println("Avslutar");
					stop();
					System.out.println("Avslutad");
					System.exit(0);
					return response.body();
				});
				get("/restart", (request, response) -> {
					System.out.println("Startar om");
					ProcessBuilder pb = new ProcessBuilder("/etc/init.d/bjorns", "start");
					pb.inheritIO();
					pb.start();
					return response.body();
				});
			});
			path("/login", () -> {
				before("/*",(request, response) -> {
					String remotehost=request.headers("Origin");
					System.out.println("Remotehost: "+remotehost);
					validated(request, response, true,remotehost);
				});
				get("", new Route() {
					@Override
					public Object handle(Request request, Response response) throws Exception {
						response.redirect("https://"+request.headers("remote-host"));
						System.out.println("Responding with: " + response.status() + ", " + response.body());
						System.out.println();
						return response.body();
					}
				});
				post("", new Route() {
					@Override
					public Object handle(Request request, Response response) throws Exception {
						//						for (String string : request.headers()) {
						//							System.out.println(string+"  "+request.headers(string));
						//						}
						String password=request.queryParams("password");
						System.out.println(password);
						password=new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-1").digest(password.getBytes())));
						System.out.println(password);
						if(password.equals("LH2WA9HlvN5+QxR+P+idBq9x3OE=")){
							// lösenhash LH2WA9HlvN5+QxR+P+idBq9x3OE=
							response.body("Inloggad!!!!");
							String id=createSessionID();
							response.cookie("", "", "sessionID", id, 60*60*24, true, true);
							response.redirect(request.headers("Origin")+"/admin");
							session=id;
						}
						else{
							String remotehost=request.headers("Origin");
							response.redirect(remotehost);
						}
						return response.body();
					}
				});
				post("/hueF56/*", (request, response) -> {
					try {
						String[] splats=request.splat();
						System.out.println(splats);;
						int id=request.attribute("id");
						URL url = new URL("http://localhost:10000/api/1Ct9oM4V40HVsMkaWFq76MFchV3yygkBCTDl7SaH/"+splats);
						HttpURLConnection connection= (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("PUT");
						connection.setDoOutput(true);
						OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
						writer.write(request.body());
						writer.close();
						connect(connection);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return response.body();
				});
				get("/lampstatus", new Route() {
					@Override
					public Object handle(Request request, Response response) throws Exception {
						try {
							ResultSet resultSet = connect.createStatement()
									.executeQuery("select * from Data WHERE Data='Lyser'");
							resultSet.next();
							response.body((resultSet.getInt("Value")==0?false:true)+"");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							sqlconnect();
							return handle(request, response);
						}
						return response.body();
					}
				});

				get("/dark", new Route() {
					@Override
					public Object handle(Request request, Response response) throws Exception {
						try {
							ResultSet resultSet = connect.createStatement()
									.executeQuery("select * from LampaMorkTid");
							String data="";
							while(resultSet.next()){
								data+=resultSet.getString(1)+"-"+resultSet.getString(2)+";";
							}
							response.body(data);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							sqlconnect();
							return handle(request, response);
						}

						return response.body();
					}
				});
				post("/dark", new Route() {
					@Override
					public Object handle(Request request, Response response) throws Exception {
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

						return response.body();
					}
				});
				post("/set", new Route() {
					@Override
					public Object handle(Request request, Response response) throws Exception {
						if(request.body().startsWith("lampa")){
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
						return response.body();
					}
				});
			});

			post("/login/chat/read", new Route() {
				@Override
				public Object handle(Request request, Response response) throws Exception {
					//				for (String string : request.headers()) {
					//					System.out.println(string+"  "+request.headers(string));
					//				}
					//				System.out.println("(SET) POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
					String body=request.body();
					//				System.out.println(body);

					int i=0;
					try {
						i=Integer.parseInt(body);
					} catch (Exception e) {
						System.err.println("default");
					}
					String svar="";
					int size=meddelanden.size();
					for (int j = i; j < size; j++) {
						svar+=meddelanden.get(j)+"\n";
					}
					response.body(svar);
					response.header("NextMessage", (size)+"");
					return response.body();
				}
			});
			post("/login/chat/post", new Route() {
				@Override
				public Object handle(Request request, Response response) throws Exception {
					for (String string : request.headers()) {
						System.out.println(string+"  "+request.headers(string));
					}
					System.out.println("(SET) POST-request " + request.protocol()+" from: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
					String body=request.body();
					System.out.println(body);
					meddelanden.add(body);
					response.body("OK");
					return response.body();
				}
			});
		});

	}
	private boolean validated(Request request, Response response,boolean requireLogin,String remotehost){
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
			forbiddenaccess(request, response,remotehost);
			return false;
		}

	}
	private void forbiddenaccess(Request request, Response response,String remotehost){
		System.err.println("Forbidden");
		response.body("forbidden");
		response.status(403);
		try {
			response.redirect(remotehost,403);
		} catch (IllegalArgumentException e) {
			response.redirect("https://bjorns.tk/",403);
		}
	}
	private void sqlconnect(){
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/styrning?"
							+ "user=jakob&password=furugatan10&serverTimezone=UTC");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
