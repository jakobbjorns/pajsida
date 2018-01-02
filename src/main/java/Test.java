//
//
//import spark.*;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.json.simple.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//public class Test {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		Socket socket = new Socket();
//		Response response = socket.POST("http://localhost:8181/new", "hejsan");
//		
//		System.out.println(response.getResponseCode() + ": " + response.getResponseString());
//	}
//
//}
//
//
//class Socket {
//	
//	private final String USER_AGENT = "Mozilla/5.0";
//	private long lastMove = 0;
//
//	
//	public Response GET(String URL){
//		try{
//			
//			//GET
//			//Setting a timeout in milliseconds
//			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(4 * 1000).build();
//			HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
//			HttpGet request = new HttpGet(URL);
//			
//			// add request header
//			request.addHeader("User-Agent", USER_AGENT);
//			HttpResponse response = client.execute(request);
//			
//			BufferedReader bufferedReader = new BufferedReader(
//					new InputStreamReader(response.getEntity().getContent()));
//			
//			StringBuffer resultString = new StringBuffer();
//			String line = "";
//			while ((line = bufferedReader.readLine()) != null) {
//				resultString.append(line);
//			}
//			
//			Response result = new Response(new String(resultString), response.getStatusLine().getStatusCode());
//			
//			return result;
//			
//		}
//		catch (Exception e){
////			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	public Response POST(String URL, JSONObject json) {
//		try {
//			//POST
//			String url = URL;
//			
//			HttpClient client = HttpClientBuilder.create().build();
//			HttpPost post = new HttpPost(url);
//			
//			// add header
//			post.addHeader("Accept","application/json");
//			
//			
//			StringEntity jsonRequest = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
//			post.setEntity(jsonRequest);
//			
//			//See if last move was more than 100ms ago
//			while (System.currentTimeMillis() - lastMove < 110){
//			
//			}
//			
//			
//			HttpResponse response = client.execute(post);
//			
//			BufferedReader bufferedReader = new BufferedReader(
//					new InputStreamReader(response.getEntity().getContent()));
//			
//			StringBuffer resultString = new StringBuffer();
//			String line = "";
//			while ((line = bufferedReader.readLine()) != null) {
//				resultString.append(line);
//			}
//			
//			Response result = new Response(new String(resultString), response.getStatusLine().getStatusCode());
//			
//			return result;
//			
//		}
//		catch (Exception e){
//			e.printStackTrace();
//			return null;
//		}
//	}
//}