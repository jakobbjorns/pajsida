package gojb.jbhttp.API;

import java.io.InputStream;
import java.util.Scanner;

import spark.Route;

public class TempAPI {
	static Route F56 = (request, response) ->{
		ProcessBuilder pb = new ProcessBuilder("ssh", "glenn","'curl -s 192.168.1.10/a'");
		pb.inheritIO();
		Process process=pb.start();
		process.waitFor();
		response.body(convertStreamToString(process.getInputStream()));
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
