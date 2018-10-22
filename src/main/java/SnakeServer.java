import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import spark.Spark;

class SnakeServer{

	public static Random random = new Random();
	public static final int height = 50;
	public static final int width = 50;
	public static boolean pause;
	public static int pluppX,pluppY;
	public static boolean highscoreBool;
	public static JSONArray arrayBuilder=new JSONArray();
	static String message;	

	static final Object LOCK = new Object();
	static final Object INACTIVE = new Object();
	private static Map<Session, Snake> sessions = SnakeAPI.sessions;
	public static void main(String[] args) {
		Spark.port(1000);
		Spark.webSocket("/snake", SnakeAPI.class);
		Spark.init();
	}
	public static Thread gameloop=new Thread(){
		@Override
		public void run() {
			while (true) {
				try {
					if (sessions.isEmpty()) {
						System.out.println("Snakeserver inaktiv");
						synchronized (INACTIVE) {
							INACTIVE.wait();
						}
						System.out.println("Snakeserver aktiv");
					}
					long i = System.currentTimeMillis();
					if (!pause) {
						update();
					}
					try {
						sleep(i+100-System.currentTimeMillis());
					} 
					catch (IllegalArgumentException e) {
						e.printStackTrace();
					}catch (InterruptedException e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	};
	static Comparator<Snake> comparator = new Comparator<Snake>() {
		@Override
		public int compare(Snake o1, Snake o2) {
			int 
			poäng2=o2.length-3,
			poäng1=o1.length-3;
			if (poäng1<0) {
				poäng1=0;
			}
			if (poäng2<0) {
				poäng2=0;
			}
			int one = (poäng2-poäng1);

			return one == 0 ? (o2.highscore-o1.highscore) : one;
		}
	};
	static{
		//		timer.start();
		plupp();
		gameloop.start();;
		System.out.println("Running");
	}
	static void highscore(){
		ArrayList<Snake> snakes=new ArrayList<>(sessions.values());
		snakes.sort(comparator);
		JSONArray array=new JSONArray();
		for (Snake snake : snakes) {
			array.put(snake.highscoreObject());
		}
		arrayBuilder.put(new JSONObject()
				.put("type", "highscore")
				.put("highscore", array));
		highscoreBool=false;
	}
	public static void update() {
		long date = System.currentTimeMillis(),date2,date3 = 0,date4=0,date5=0,date6=0,date7 = 0,date8=0;
		date2 = System.currentTimeMillis() ;
		try{
			//Gör alla förflyttningar
			for (Snake snake : sessions.values()) {
				snake.move();
			}
			//Förlustkontroll
			date3 = System.currentTimeMillis();
			for (Snake snake : sessions.values()) {
				dennasnake:if(snake.fördröjning<0){
					//Kolla om munnen åker ur bild
					//					if (snake.x[0]<0||snake.y[0]<0||snake.x[0]>=width||snake.y[0]>=height) {
					if (snake.urbild(width, height)) {
						snake.gameover("urBild");
						break dennasnake;
					}

					//Kolla om munnen nuddar egna kroppen
					for (int i = 1; i < snake.length; i++) {
						//						if((snake.x[0]==snake.x[i]&&snake.y[0]==snake.y[i])) {
						if(snake.käkar(snake, i)) {
							snake.gameover("nuddaKropp");
							break dennasnake;
						}
					}

					//Kolla om munnen nuddar annans kropp eller mun
					for (Snake snake2 : sessions.values()) {
						if (snake2!=snake) {
							//							if (snake.x[0]==snake2.x[0]&&snake.y[0]==snake2.y[0]) {
							if(snake.käkar(snake2, 0)) {
								if (snake2.fördröjning<0) {
									snake.gameover("krock");
								}
								snake2.gameover("krock");
							}
							//							else if (snake.x[0]==snake2.x[1]&&snake.y[0]==snake2.y[1]&&
							//									snake.x[1]==snake2.x[0]&&snake.y[1]==snake2.y[0]) {
							else if (snake.käkar(snake2, 1)&&
									snake2.käkar(snake, 0)) {
								snake.gameover("dubbelkrock");
								snake2.gameover("dubbelkrock");
							}
							else{
								for (int i = 1; i < snake2.length; i++) {
									//									if (snake.x[0]==snake2.x[i]&&snake.y[0]==snake2.y[i]){
									if (snake.käkar(snake2, i)){
										snake.gameover("nuddaAnnanKropp");
										break dennasnake;
									}
								} 
							}
						}
					}
				}
			}
			date4 = System.currentTimeMillis();
			//Poängkontroll
			for (Snake snake : sessions.values()) {
				//				if (snake.x[0]==pluppX&&snake.y[0]==pluppY) {
				if (snake.käkar(pluppX, pluppY)) {
					snake.poäng();
					plupp();
				}
			}
			date5 = System.currentTimeMillis();
			date6 = databuild();
			date7 = System.currentTimeMillis();
			sendAll();

		}
		catch(Exception e){
			sendAll("SERVERUPDATEEXEPTION");
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			sendAll("E "+errors.toString());
		}
		date8 = System.currentTimeMillis();
		long diff=date8-date;
		if (diff>4) {
			JSONObject object=new JSONObject();
			object.put("type", "delay");
			object.put("delay", "Total:"+diff+
					" Rem:"+(date2-date)+
					" Move:"+(date3-date2)+
					" Förl:"+(date4-date3)+
					" Poäng:"+(date5-date4)+
					" BuildLoad:"+(date6-date5)+
					" Build:"+(date7-date6)+
					" Send:"+(date8-date7));
			arrayBuilder.put(object);
		}
	}
	private static long databuild() {
		JSONArray array=new JSONArray();
		for (Snake snake : sessions.values()) {
			array.put(snake.getData());
		}
		arrayBuilder.put(new JSONObject()
				.put("type", "players")
				.put("players", array));


		if (highscoreBool) {
			highscore();
		}
		long b=System.currentTimeMillis();

		message=new JSONObject().put("data", arrayBuilder).toString();
		arrayBuilder=new JSONArray();
		return b;
	}
	public static void sendAll(String message){
		for (Snake snake : sessions.values()) {
			snake.send(message);
		}
	}
	public static void sendAll(){
		synchronized (LOCK) {
			LOCK.notifyAll();
		}

		//				sendAll(message);

	}
	static void plupp(){
		pluppX = random.nextInt(width);
		pluppY = random.nextInt(height);
		JSONObject object = new JSONObject();
		object.put("type", "plupp");
		object.put("X", pluppX);
		object.put("Y", pluppY);
		arrayBuilder.put(object);
		//		arrayBuilder.add(Json.createObjectBuilder()
		//				.add("type", "plupp")
		//				.add("X", pluppX)
		//				.add("Y", pluppY));
		highscoreBool=true;
	}




	public static void resetAll(){
		for (Snake snake : sessions.values()) {
			snake.reset();
			snake.starta();
		}
		plupp();
		pause=false;
	}
	static void message(Scanner scanner,Session user) {
		String string=scanner.next();
		if (string.equals("INIT")) {
			Snake newsnake=new Snake(user,scanner);
			sessions.put(user, newsnake);
			synchronized (INACTIVE) {
				INACTIVE.notify();
			}
			if (pause) {
				newsnake.send(new JSONObject()
						.put("data", new JSONArray()
								.put(new JSONObject()
										.put("type", "pause")))
						.toString());
				databuild();
				sendAll();
			}
		}
		else if (string.equals("R")) {
			Snake snake=sessions.get(user);
			if (!snake.setRiktning(scanner.next())) {
				if (scanner.hasNext()) {
					snake.setRiktning(scanner.next());
				}
			}

		}
		else if (string.equals("RES")) {
			if (pause) {
				update();
			}
			else {
				resetAll();
			}

		}
		else if(string.equals("PAUSE")){
			pause=!pause;
			if (pause) {
				arrayBuilder.put(new JSONObject().put("type", "pause"));
				databuild();
				sendAll();
			}
			else {
				arrayBuilder.put(new JSONObject().put("type", "unpause"));
				databuild();
				sendAll();
			}
		}
		scanner.close();
	}

}