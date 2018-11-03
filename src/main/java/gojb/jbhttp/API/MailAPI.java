package gojb.jbhttp.API;

import spark.Request;
import spark.Response;
import spark.Route;

public class MailAPI {
	public static Route auth=new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			String user = request.headers("Auth-User");
			String password = request.headers("Auth-Pass");
			String protocol=request.headers("Auth-Protocol");
			String port="110";
			if (protocol.equals("imap")) {
				port="143";
			}
			if (protocol.equals("smtp")) {
				port="25";
			}
			if (LoginAPI.correctPassword(user,password)) {
				response.header("Auth-Status","OK");
				response.header("Auth-Server","localhost");
				response.header("Auth-Port",port);
			}
			else {
				response.header("Auth-Status", "Invalid login or password");
			}
			return response;
		}
	};
}
