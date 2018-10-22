import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import spark.Request;
import spark.Response;
import spark.Route;

public class LampAPI {
	private static Connection connect;
	static{
		sqlconnect();
	}
	private static void sqlconnect(){
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/styrning?"
							+ "user=jakob&password=furugatan10&serverTimezone=UTC");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static Route lampstatus =new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			try {
				ResultSet resultSet = connect.createStatement()
						.executeQuery("select * from Data WHERE Data='Lyser'");
				resultSet.next();
				response.body((resultSet.getInt("Value")==0?false:true)+"");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sqlconnect();
				return handle(request, response);
			}
			return response.body();
		}
	};
	static Route setstatus=(request, response) -> {
		if(request.body().startsWith("lampa")){
			if(request.body().endsWith("true")){
				System.err.println("tänd!");
				response.body("tänder");
			}
			else if(request.body().endsWith("false")){
				System.err.println("släck!");
				response.body("släcker");
			}

			connect.createStatement().executeUpdate("UPDATE Data SET Value='1' WHERE Data='Switch'");
		}
		return response.body();
	};
	static Route postDarkTimes=(request, response) -> {
		try {
			String body = request.body();
			System.out.println(body);
			Statement statement=connect.createStatement();
			statement.executeUpdate("TRUNCATE LampaMorkTid");
			if (!body.equals("")) {
				String[] tider=body.substring(0,body.length()-1).split(";");
				for (int i = 0; i < tider.length; i++) {
					String[] tid= tider[i].split("-");
					statement.executeUpdate("INSERT INTO LampaMorkTid (Start,Slut) VALUES ("+tid[0]+","+tid[1]+");");
				}
			}
			response.body("OK");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sqlconnect();
			response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
		return response.body();
	};
	static Route getDarkTimes=new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			try {
				ResultSet resultSet = connect.createStatement()
						.executeQuery("select * from LampaMorkTid");
				String data="";
				while(resultSet.next()){
					data+=resultSet.getString(1)+"-"+resultSet.getString(2)+";";
				}
				response.body(data);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sqlconnect();
				return handle(request, response);
			}

			return response.body();
		}
	};
}
