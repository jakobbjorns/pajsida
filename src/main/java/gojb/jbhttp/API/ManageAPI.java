package gojb.jbhttp.API;
import static spark.Spark.stop;

import java.io.File;

import spark.Filter;
import spark.Route;


public class ManageAPI {
//	private static Process ssh;
	static{
//		try {
//			ProcessBuilder pb = new ProcessBuilder("ssh", "glenn","-N");
//			pb.inheritIO();
//			ssh=pb.start();
//			System.out.println("ssh mot glenn");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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
	static Filter beforeAll=(request,response)->{
		System.out.println();
		System.out.println(request.requestMethod()+"-request (" +request.uri() +" "+ request.protocol()+") från: "+request.headers("X-Real-IP")+" ("+request.ip()+")");
		System.out.println("hejsan");
	};
	static Filter afterAll=(request,response)->{
		System.out.println("Responding with: " + response.status() + ", " + response.body());
	};
	static Route stop=(request, response) -> {
		System.out.println("Avslutar");
		stop();
//		ssh.destroy();
		System.out.println("Avslutad");
		System.exit(0);
		return response.body();
	};
	static Route restart=(request, response) -> {
		System.out.println("Startar om");
		ProcessBuilder pb = new ProcessBuilder("/etc/init.d/bjorns", "restart");
		pb.inheritIO();
		pb.start();
		return response.body();
	};
	static Route git=(request, response) -> {
		System.out.println("Git refresh");
		ProcessBuilder pb = new ProcessBuilder("su","pi","-c","'sh autogit'");
		File homedir = new File(System.getProperty("user.home"));
		File file = new File(homedir, "git/pajsida");
		pb.directory(file);
		pb.inheritIO();
		pb.start();
		response.body("OK");
		return response.body();
	};

}
