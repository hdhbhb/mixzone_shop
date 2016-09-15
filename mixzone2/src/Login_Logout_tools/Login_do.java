package Login_Logout_tools;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.example.mixzone2.ShareString;
import com.example.mixzone2.UriAPI;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class Login_do {
	private String username;
	private String password;
	private boolean Login_FLAG = false;
	private SharedPreferences preferences;
	private Editor editor;
	private String login_name;
	private String login_pwd;
	/**自动登陆时使用*/
	public Login_do(Context context){
		preferences = context.getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
	}
	/**手动登录时使用*/
	public Login_do(String username,String password) {
		this.username = username;
		this.password = password;
	}
	/**联网耗时操作，需要在线程中使用*/
	public boolean login_success(){
			try {
				URL url = new URL(UriAPI.loginUri);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setReadTimeout(5000);
				conn.setConnectTimeout(5000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				String data = "username="+URLEncoder.encode(username,"UTF-8")+"&password="+URLEncoder.encode(password,"UTF-8");
				OutputStream os = conn.getOutputStream();
				os.write(data.getBytes());
				os.flush();
				os.close();
					
				if (conn.getResponseCode() == 200) {
					InputStream is = conn.getInputStream();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int len = 0;
					byte[] buffer = new byte[1024];
					while ((len = is.read(buffer)) != -1) {
						baos.write(buffer,0,len);
					}
					is.close();
					baos.close();
					if (baos.toString().equals("YES")) {
						Login_FLAG = true;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Login_FLAG;
	}
	
	public void check_login_save(){
		if (preferences.getBoolean(ShareString.SaveLogin, false)) {
			login_name = preferences.getString(ShareString.Username, "");
			login_pwd = preferences.getString(ShareString.Password, "");

			Thread thread = new Thread(new login());
			thread.start();
		}
	}
	
	private class login implements Runnable {
		public void run() {
			Login_do login_do = new Login_do(login_name, login_pwd);
			if (login_do.login_success()) {
				editor = preferences.edit();
				editor.putBoolean(ShareString.isLogin, true);
				editor.commit();
			}
		}
	}
}
