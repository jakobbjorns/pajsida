import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Ssh {
	public static void main(String[] args) throws IOException, InterruptedException {
		//
		//		ProcessBuilder builder = new ProcessBuilder("/bin/bash");
		//		builder.redirectErrorStream(true);
		//		Process pr = builder.start();

		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec("ssh glenn");
		new Thread(new Runnable() {
			public void run() {
				BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = null; 

				while (pr.isAlive()) {
					
					try {
						Thread.sleep(1000);
						while ((line = input.readLine()) != null)
							System.out.println(line);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			}
		}).start();
		OutputStream os=pr.getOutputStream();
		os.write("exit\n".getBytes());
		os.flush();
		os.close();

		System.out.println(pr.waitFor());
		System.out.println(pr.exitValue());
	}
}
