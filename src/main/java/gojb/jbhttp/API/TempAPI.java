package gojb.jbhttp.API;

import java.io.InputStream;
import java.util.Scanner;

import spark.Route;

public class TempAPI {
	static Route F56 = (request, response) ->{
//		ProcessBuilder pb = new ProcessBuilder("ssh", "glenn","<<","EOF","curl","-s 192.168.1.10/a\n",
//				"curl","-s 192.168.1.10/a","EOF");
//		pb.inheritIO();
//		Process process=pb.start();
//		process.waitFor();
//		response.body(convertStreamToString(process.getInputStream()));
		Process curl = Runtime.getRuntime().exec("ssh glenn 'curl -s 192.168.1.10/a ; curl -s 192.168.1.10/b'");
		byte[] buffer = new byte[1024];
		curl.getInputStream().read(buffer);
		String s = new String(buffer);
		System.out.println(s);
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
