import java.util.ArrayList;

import spark.*;
public class SnakeServer {
	int i=0;
	ArrayList<Player> players = new ArrayList<SnakeServer.Player>();
	public SnakeServer() {
		
		Spark.get("/login/snake", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				// TODO Auto-generated method stub
				players.add(new Player(i));
				response.body(i+++"");
				return null;
			}
		});
		Spark.post("/login/snake", new Route() {

			@Override
			public Object handle(Request request, Response response) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
	class Player{
		int id;
		public Player(int id) {
			this.id=id;
			// TODO Auto-generated constructor stub
		}
	}
}

