package gojb.jbhttp.API;

import java.io.InputStream;
import java.util.Scanner;

import spark.Route;

public class TempAPI {
	static Route F56 = (request, response) ->{
		ProcessBuilder pb = new ProcessBuilder("ssh", "glenn",
				"curl -s 192.168.1.10/a; curl -s 192.168.1.10/b; curl -s 192.168.1.10/c");
		pb.redirectErrorStream();
		Process process=pb.start();
		String s = convertStreamToString(process.getInputStream());
		System.out.println(s);
		
//		process.waitFor();
//		response.body(convertStreamToString(process.getInputStream()));
//		Process curl = Runtime.getRuntime().exec("ssh glenn \"curl -s 192.168.1.10/a ; curl -s 192.168.1.10/b\"");
//		byte[] buffer = new byte[1024];
//		curl.getInputStream().read(buffer);
//		String s = new String(buffer);
//		System.out.println(s);
//		
//		byte[] buffer2 = new byte[1024];
//		curl.getErrorStream().read(buffer2);
//		String s2 = new String(buffer2);
//		System.out.println(s2);
		
		response.body(s);
		return response;
	};
	static String convertStreamToString(InputStream is) {
		Scanner sc = new Scanner(is);
		Scanner s=sc.useDelimiter("\\A");
		String string=s.hasNext() ? s.next() : "";
		sc.close();
		return string ;
	}
}
