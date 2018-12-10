package gojb.jbhttp.API.aktier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import spark.Request;
import spark.Response;
import spark.Route;

public class Stockholmsbörsen {
	public static Route aktielista = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			response.body(getAktielista(handleFrom(request),handleTo(request)));
			return response;
		}
	};
	public static void main(String[] args) {
		getAktielista("2018-01-01", "2018-01-01");
	}
	public static Route omxs30 = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			// TODO Auto-generated method stub
			response.body(datapoints("", 19002, handleFrom(request), handleTo(request), "DAY"));
			return response;
		}
	};
	public static String handleFrom(Request request) {
		String[] splats=request.splat();
		String date="2018-01-01";
		if (splats.length>0) {
			date=splats[0];
		}
		return date;
	}	
	public static String handleTo(Request request) {
		String[] splats=request.splat();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		if (splats.length>1) {
			date=splats[1];
		}
		return date;
	}
	static String getAktielista(String fromdate,String todate) {
		try {
			URL url=new URL("https://www.avanza.se/"
					+ "frontend/template.html/marketing/advanced-filter/advanced-filter-template?"
					+ "widgets.stockLists.filter.list%5B0%5D=SE.LargeCap.SE&"
//					+ "widgets.stockLists.filter.list%5B1%5D=SE.MidCap.SE&"
//					+ "widgets.stockLists.filter.list%5B2%5D=SE.SmallCap.SE&"
					+ "widgets.stockLists.active=true&"
					+ "parameters.startIndex=0&"
					+ "parameters.maxResults=1000&"
					+ "parameters.selectedFields%5B0%5D=PRICE_PER_EARNINGS&"
					+ "parameters.selectedFields%5B1%5D=PRICE_PER_SALES&"
					+ "parameters.selectedFields%5B2%5D=DIRECT_YIELD&"
					+ "parameters.selectedFields%5B3%5D=MARKET_CAPITAL_IN_SEK"
					);


			System.out.println(url.toString());
			Document document = Jsoup.parse(url, 1000);

			Elements elements2 = document.getElementsByClass("u-standardTable");
			System.out.println(elements2.size());

			Element bolagsnamn_tabell=elements2.get(0);
			Elements bolagsnamn_lista=bolagsnamn_tabell.getElementsByClass("ellipsis");

			Element bolagsdata_tabell=elements2.get(1);
			Elements bolagsdata_lista=bolagsdata_tabell.getElementsByTag("tr");

			String result="";

			for (int i = 0; i < bolagsnamn_lista.size(); i++) {
				Element element = bolagsnamn_lista.get(i);
				String namn=element.text();
				String href=element.attr("href");
				String id = href;
				id=id.split("/")[3];
				Elements bolagsdata = bolagsdata_lista.get(i+1).children();
				String string = namn;
				for (Element element2 : bolagsdata) {
					string+="\t"+element2.getElementsByTag("span").text();
				}
				Document bolagsfakta = Jsoup.parse(new URL("https://avanza.se"+href.replace("om-aktien.html","om-bolaget.html")), 10000);
				Elements el = bolagsfakta.getElementsContainingOwnText("Soliditet %");
				string+="\t"+el.get(0).parent().nextElementSibling().text();
				System.out.println(string);
				result+=string+"\n";
				result+=datapoints("06bf11f1-54bf-4419-8e20-54cfa59c62c3", Integer.parseInt(id), fromdate,todate,"DAY");
				result+="\n";
			}
			return result;
		} 

		//		catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}
		catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	static String datapoints(String token,int id,String start,String end,String resolution) throws IOException {
		HttpURLConnection connection=openConnection("https://www.avanza.se/ab/component/highstockchart/getchart/orderbook", "POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("AZA-usertoken", token);
		connection.setRequestProperty("Content-Type", "application/json");

		JSONObject objectBuilder = new JSONObject();
		objectBuilder.put("navigator", true);
		objectBuilder.put("percentage", false);
		objectBuilder.put("volume", false);
		objectBuilder.put("owners", false);
		objectBuilder.put("chartType", "AREA");
		objectBuilder.put("widthOfPlotContainer", 640);

		objectBuilder.put("orderbookId", id);
		objectBuilder.put("chartResolution", resolution);
		//		objectBuilder.put("timePeriod",  "today");
		objectBuilder.put("start",  start+"T00:00:00.000Z");
		objectBuilder.put("end",  end+"T23:59:00.000Z");

		//		JSONArray arrayBuilder = new JSONArray();
		//		arrayBuilder.put(new JSONObject().put("type", "sma").put("timeFrame", 10));
		//		arrayBuilder.put(new JSONObject().put("type", "sma").put("timeFrame", 20));
		//
		//		//		objectBuilder2.put("type", "rsi");
		//		//		objectBuilder2.put("timeFrame", 14);
		//		//		arrayBuilder.put(objectBuilder2.build());
		//
		//		objectBuilder.put("ta",arrayBuilder);
		connection.getOutputStream().write(objectBuilder.toString().getBytes());
		String s =connect(connection);
		JSONObject object = new JSONObject(s);
		JSONArray data=object.getJSONArray("dataPoints");
		String result="";
		String d = null;
		for (Object object2 : data) {
			JSONArray point=new JSONArray(object2.toString());
			long l=point.getLong(0);
			Object obj=point.get(1);
			if (!obj.equals(null)) {
				d=obj.toString();
			}
			Date date = new Date(l);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			if (resolution.equals("DAY")) {
				dateFormat= new SimpleDateFormat("yy-MM-dd");
			}

			result+=dateFormat.format(date)+"\t"+d+"\n";
		}
		return result;
	}
	static String connect(HttpURLConnection connection) throws IOException  {
		connection.connect();
		if (connection.getResponseCode()!=200) {
			System.out.println(connection.getResponseMessage());
			System.out.println(connection.getResponseCode());
		}

		InputStreamReader reader = new InputStreamReader(connection.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(reader);
		String string=bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		return string;
	}
	static HttpURLConnection openConnection(String URL,String metod) throws IOException {
		URL url = new URL(URL);
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(metod);
		return connection;
	}
}
