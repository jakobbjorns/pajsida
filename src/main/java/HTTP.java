import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HTTP {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws Exception {
		new HTTP();
	}
	public HTTP() throws IOException, InterruptedException {
		CookieManager cookieManager = new CookieManager();
		login(cookieManager);
	}
	private void login(CookieManager manager) throws IOException, InterruptedException {
		HttpURLConnection connection=openConnection("https://www.avanza.se/ab/bankid/authenticate", "POST");
		//		String cookiesHeader = connection.getHeaderField("Set-Cookie");
		//		List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
		//		cookies.forEach(cookie -> manager.getCookieStore().add(null, cookie));
		Map<String, String> parameters = new HashMap<>();
		parameters.put("personnummer", "9901225095");
		add_parameters(connection, parameters);
		while (connection.getResponseCode()!=200) {
			Thread.sleep(100);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		System.out.println(content);
	}
	HttpURLConnection openConnection(String URL,String metod) throws IOException {
		URL url = new URL(URL);
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(metod);
		return connection;
	}
	void add_parameters(HttpURLConnection  connection,Map<String, String> parameters) throws UnsupportedEncodingException, IOException {
		connection.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes(getParamsString(parameters));
		out.flush();
		out.close();
	}
	String getParamsString(Map<String, String> params) throws UnsupportedEncodingException{
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}
		String resultString = result.toString();
		return resultString.length() > 0
				? resultString.substring(0, resultString.length() - 1)
						: resultString;
	}

}

