package gojb.jbhttp.API;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import spark.Route;

public class HueAPI {
	static Route send = (request2, response2) -> {
		System.out.println("HUE");
		System.out.println(request2.body());
		try {
			String[] splats=request2.splat();
			String splat=String.join("/", splats);
			URL url = new URL("http://glennolsson.se:2017/api/1Ct9oM4V40HVsMkaWFq76MFchV3yygkBCTDl7SaH/"+splat);
			HttpURLConnection connection= (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(request2.requestMethod());
			if (!request2.requestMethod().equals("GET")) {
				connection.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(request2.body());
				writer.close();
			}
			response2.body(connect(connection));
			response2.status(connection.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response2.body();
	};
	private static String connect(HttpURLConnection connection) throws IOException, InterruptedException {
		connection.connect();
		InputStreamReader reader = new InputStreamReader(connection.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(reader);
		String string=bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
		return string;
	}
}
