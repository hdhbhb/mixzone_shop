package Login_Logout_tools;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.example.mixzone2.UriAPI;

public class Logout_do {
	private String username;
	public Logout_do(String username) {
		this.username = username;
	}
	public boolean logout_success(){
		try {
			URL url = new URL(UriAPI.logoutUri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			String data = "username="+URLEncoder.encode(username,"UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(data.getBytes());
			os.flush();
			os.close();
			if (conn.getResponseCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
