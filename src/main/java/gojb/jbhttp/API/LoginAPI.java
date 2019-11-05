package gojb.jbhttp.API;
import static spark.Spark.halt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginAPI {
	private static ArrayList<Kaka> kakor= new ArrayList<Kaka>();
	static Filter validated=(request, response) -> {
		String remotehost=request.headers("Origin");
		System.out.println("Remotehost: "+remotehost);
		String cookie=request.cookie("sessionID");
		System.out.println(cookie);
		
		if (request.ip().equals("127.0.0.1")&&inloggad(cookie)) {
			System.out.println("Validated");
		}
		else {
			System.err.println("Forbidden");
			halt(403);
		}

	};
	static Route login=new Route() {
		@Override
		public Object handle(Request request, Response response){
			String remotehost=request.headers("Origin");
			String user=request.queryParams("user");
			String password=request.queryParams("password");
			if(correctPassword(user,password)){
				response.body("Inloggad!!!!");
				String id=createSessionID();
				int age=60*60*24;
				response.cookie("", "", "sessionID", id, age, true, true);
				kakor.add(new Kaka(id, age));
				if (remotehost==null||remotehost=="null") {
					remotehost="https://bjorns.tk";
				}
				response.redirect(remotehost+"/admin");
			}
			else{
				response.redirect(remotehost);
			}
			return response.body();
		}
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
	}
	public static boolean correctPassword(String user,String password) {
		// lösenhash LH2WA9HlvN5+QxR+P+idBq9x3OE=
		String passwordHash=getHash(password);
		boolean correct=passwordHash.equals("LH2WA9HlvN5+QxR+P+idBq9x3OE=");
		System.out.println("Checking password: "+password+" ("+passwordHash+") Correct:"+correct);
		return correct;
	}
	/**Returns the hash of the input string
	 * @param string Input String
	 * @return Hash of input string
	 * @author Jakob*/
	private static String getHash(String string) {
		MessageDigest sha;
		Encoder encoder=Base64.getEncoder();
		try {
			sha=MessageDigest.getInstance("SHA-1");
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		byte[]bytes=string.getBytes();
		bytes=sha.digest(bytes);
		bytes=encoder.encode(bytes);
		return new String(bytes);
	}
	private static boolean inloggad(String sessionID){
		if (sessionID!=null) {
			ArrayList<Kaka>kakorna=new ArrayList<>(kakor);
			for (Kaka kaka : kakorna) {
				if (kaka.isValid()) {
					if (kaka.gettext().equals(sessionID)) {
						return true;
					}
				}
				else {
					kakor.remove(kaka);
				}
			}
		}
		return false;
	}
}
class Kaka{
	private String text;
	private long created=System.currentTimeMillis();
	private long TTL;
    /**
     * Skapar en kaka
     *
     * @param text Kakans text
     * @param TTL Livslängd i sekunder
     * @author Jakob

     */
	public Kaka(String text,int TTL) {
		this.text=text;
		this.TTL=TTL*1000;
	}
	public String gettext() {
		return text;
	}
	public boolean isValid() {
		return created+TTL>System.currentTimeMillis();
	}
}
