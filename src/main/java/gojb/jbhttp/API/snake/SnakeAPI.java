package gojb.jbhttp.API.snake;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class SnakeAPI {
	static Map<Session, Snake> sessions = new ConcurrentHashMap<Session, Snake>();
	@OnWebSocketConnect
	public void open(Session session){
		try {
			session.getRemote().sendString("OPEN");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnWebSocketMessage
	public void in(Session user, String message){
		Scanner scanner;
		try {
			scanner = new Scanner(message);
			SnakeServer.message(scanner, user);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@OnWebSocketClose 
	public void close(Session user, int statusCode, String reason){
		//		removeList.add(sessions.get(user));
		sessions.remove(user);
	}

	
	
}
