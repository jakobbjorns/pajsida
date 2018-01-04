import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Test {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		// TODO Auto-generated method stub
        System.out.print("Enter Password");
		System.out.println(new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-1").digest(new BufferedReader(new InputStreamReader(System.in)).readLine().getBytes()))));
	}

}
