package gojb.jbhttp.API.snake;
import java.util.Random;
import java.util.Scanner;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONObject;

public class Snake {
	private Session session;
	private int[] x=new int[1000];
	private int[] y=new int[1000];
	int length;
	private String riktning,senasteriktning;
	private String färg;
	private String namn;
	int highscore;
	int fördröjning;
	public Random random = new Random();
	private Thread sendloop=new Thread(){
		public void run() {
			while(session.isOpen()){
				try {
					synchronized(SnakeServer.LOCK){
						SnakeServer.LOCK.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				send(SnakeServer.message);
			}
		};
	};
	//	int clear;
	public Snake(Session session, Scanner scanner) {
		this.session=session;
		System.out.println("nytt objekt");
		
		try {
			sendloop.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		färg = scanner.next();
		scanner.useDelimiter("\\z"); 
		namn = scanner.next().substring(1);
		x=new int[1000];
		y=new int[1000];
		reset();
		//				send(Json.createObjectBuilder()
		//						.add("data",Json.createArrayBuilder().add(Json.createObjectBuilder()
		//								.add("type", "plupp")
		//								.add("X", pluppX)
		//								.add("Y", pluppY)
		//								)).build().toString());
		send(new JSONObject()
				.put("data",new JSONArray()
						.put(new JSONObject()
								.put("type", "plupp")
								.put("X", SnakeServer.pluppX)
								.put("Y", SnakeServer.pluppY))).toString());
		send("START");
		fördröjning=-1;
		starta();
	}
	int errtimes;
	public void send(String string) {
		try {
			session.getRemote().sendString(string);
			errtimes=0;
		} catch (Exception e) {
			e.printStackTrace();
			if (errtimes++>100) {
				SnakeAPI.sessions.remove(session);
			}

		}
	}
	public void starta(){
		length=3;
		x[1]=-1;
		y[1]=-1;
		x[2]=-1;
		y[2]=-1;
	}
	public void reset(){
		int posx = random.nextInt(SnakeServer.width);
		int posy = random.nextInt(SnakeServer.height);

		if (posx>SnakeServer.width*0.8||posx<SnakeServer.width*0.2||posy>SnakeServer.height*0.8||posy<SnakeServer.height*0.2) {
			reset();
			return;
		}
		else{		
			String [] arr = {"up", "down", "right", "left"};
			riktning=arr[random.nextInt(arr.length)];
			length = 1;
			x[0]=posx;
			y[0]=posy;
		}
		SnakeServer.highscoreBool=true;
		fördröjning=10;

	}
	void gameover(String orsak){
				SnakeServer.arrayBuilder.put(new JSONObject()
						.put("type", "gameover")
						.put("namn", namn)
						.put("orsak", orsak)
						);
		reset();
	}
	boolean setRiktning(String nyRiktning) {
		if (!((senasteriktning.equals("up")||senasteriktning.equals("down"))&&(nyRiktning.equals("up")||nyRiktning.equals("down"))||
				(senasteriktning.equals("left")||senasteriktning.equals("right"))&&(nyRiktning.equals("left")||nyRiktning.equals("right")))) {
			riktning=nyRiktning;
			return true;
		}
		else {
			return false;
		}
	}
	JSONArray getData() {
		JSONArray data=new JSONArray();
		data.put(färg);
		for (int i = 0; i < length; i++) {
			data.put(x[i]).put(y[i]);
		}
		return data;
	}
	void move() {
		if (fördröjning<0) {
			for (int i = length-1 ; i > 0; i--) {
				x[i]=x[i-1];
				y[i]=y[i-1];
			}
			if (riktning.equals("down")) 
				y[0]+=1;
			else if (riktning.equals("up"))
				y[0]-=1;
			else if (riktning.equals("right"))
				x[0]+=1;
			else if (riktning.equals("left"))
				x[0]-=1;
		}
		else{
			if (--fördröjning<=0) {
				starta();
			}
		}
		senasteriktning=riktning;
	}
	JSONObject highscoreObject() {
		int poäng=length-3;
		if(poäng<0)
			poäng=0;
		if (poäng>highscore) {
			highscore=poäng;
		}
		JSONObject object=new JSONObject();
		object.put("färg",färg);     
		object.put("namn", namn);            
		object.put("poäng", poäng);          
		object.put("highscore", highscore);
		return object;
	};
	boolean käkar(Snake snake2,int atpos) {
		return käkar(snake2.x[atpos], snake2.y[atpos]);
//		return (x[0]==snake2.x[atpos]&&y[0]==snake2.y[atpos]);
	};
	boolean käkar(int posx,int posy) {
		return x[0]==posx&&y[0]==posy;
	}
	void poäng() {
		x[length]=-1;
		y[length]=-1;
		length++;
	}
	boolean urbild(int width,int height) {
		return	x[0]<0||
				y[0]<0||
				x[0]>=width||
				y[0]>=height;
	}
}
