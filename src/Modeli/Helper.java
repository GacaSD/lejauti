package Modeli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

public class Helper {

	@SuppressLint("NewApi")
	public static JSONObject getJSON(String adresa) {
		String tmp = "";
		URL url = null;
		try {
			url = new URL(adresa);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.addRequestProperty("x-api-key", "11111");

			StrictMode.enableDefaults();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			tmp = "konekcija";
			StringBuffer json = new StringBuffer(1024);

			while ((tmp = reader.readLine()) != null)
				json.append(tmp).append("\n");
			reader.close();

			JSONObject data = new JSONObject(json.toString());

			return data;
		} catch (Exception e) {

			Log.e("json", e.toString());
			return null;
		}
	}

}
