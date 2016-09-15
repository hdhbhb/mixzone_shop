package Login_Logout_tools;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.example.mixzone2.UriAPI;

import android.content.Context;
import android.widget.Toast;

public class Register_do {
	private Context context;
	private String username;
	private String password;
	private boolean Register_success = false;
	public String failed_result;
	public Register_do(Context context,String username,String password) {
		this.context = context;
		this.username = username;
		this.password = password;
	}
	public boolean register_success(){
		try {
			URL url = new URL(UriAPI.RegisterUri);
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
				if (baos.toString().equals("USER_HAVE_BEEN_CREATE")) {
					failed_result = "用户名已注册";
					return Register_success;
				}
				if (baos.toString().equals("CREATE_SUCCESS")) {
					Register_success = true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		failed_result = "未知错误";
		return Register_success;
}
}
