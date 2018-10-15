import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import spark.Route;

public class HueAPI {
	Route send = (request2, response2) -> {
		System.out.println("HUE");
		System.out.println(request2.body());
		try {
			String[] splats=request2.splat();
			String splat=String.join("/", splats);
			System.out.println(splat);
			URL url = new URL("http://localhost:10000/api/1Ct9oM4V40HVsMkaWFq76MFchV3yygkBCTDl7SaH/"+splat);
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
	private String connect(HttpURLConnection connection) throws IOException, InterruptedException {
		while (connection.getResponseCode()!=200) {
			Thread.sleep(100);
			System.out.println(connection.getResponseCode());
			System.out.println(connection.getResponseMessage());
			if (connection.getResponseCode()==400) {
				break;
			}
		}
		InputStreamReader reader = new InputStreamReader(connection.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(reader);
		String string=bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
		System.out.println(string);
		return string;
	}
}