package com.example.lejauti;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

public class JSONParser {

	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	@SuppressLint("NewApi")
	public static JSONObject getJSON(String adresa) {
		Log.d("", "Reading from address: " + adresa);
		String tmp = "";
		URL url = null;
		try {
			url = new URL(adresa);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.addRequestProperty("x-api-key", "11111");

			StrictMode.enableDefaults();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			tmp = "konekcija";
			StringBuffer json = new StringBuffer(1024);

			while ((tmp = reader.readLine()) != null)
				json.append(tmp).append("\n");
			reader.close();

			// Parse response
			String response = json.toString();
			Log.d("", "response: " + response);
			JSONObject data = new JSONObject(response);
			return data;
		} catch (Exception e) {
			Log.e("json", e.toString());
			return null;
		}
	}

	public static String encodeImage(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap bm = BitmapFactory.decodeFile(file.getPath(), options);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String encodedString = Base64.encodeToString(b, Base64.DEFAULT);
		return encodedString;
	}

	public static Bitmap decodeImage(String encodedString) {
		byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = null;
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if (bitmapDrawable.getBitmap() != null) {
				bitmap = bitmapDrawable.getBitmap();
			}
		}
		return bitmap;
	}

	public JSONObject getJSONFromUrl(final String url) {

		// Making HTTP request
		InputStream inputStream = null;
		try {
			// Construct the client and the HTTP request.
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			// Execute the POST request and store the response locally.
			HttpResponse httpResponse = httpClient.execute(httpPost);
			// Extract data from the response.
			HttpEntity httpEntity = httpResponse.getEntity();
			// Open an inputStream with the data content.
			inputStream = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// Create a BufferedReader to parse through the inputStream.
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
			// Declare a string builder to help with the parsing.
			StringBuilder sb = new StringBuilder();
			// Declare a string to store the JSON object data in string form.
			String line = null;

			// Build the string until null.
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			// Close the input stream.
			inputStream.close();
			// Convert the string builder data to an actual string.
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// Try to parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// Return the JSON Object.
		return jObj;

	}

	// function get json from url
	// by making HTTP POST method
	public static JSONObject makeHttpPostRequest(AppActivity activity, String url, HashMap<String, Object> params)
			throws Exception {

		// Making HTTP request
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(createPostEntity(activity, params));

		InputStream inputStream = httpClient.execute(httpPost).getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
		final StringBuilder jsonData = new StringBuilder();

		// Parse BOM marks
		String line;
		while ((line = reader.readLine()) != null) {

			// Handle BOM marks
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			jsonData.append(line);
		}

		inputStream.close();
		json = jsonData.toString();
		// Log.d("", "Response:" + json);

		// try parse the string to a JSON object
		jObj = new JSONObject(json);

		// return JSON String
		return jObj;
	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

		// Making HTTP request
		InputStream inputStream = null;
		try {

			// check for request method
			if (method == "POST") {
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				inputStream = httpClient.execute(httpPost).getEntity().getContent();

			} else if (method == "GET") {
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				inputStream = httpEntity.getContent();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;
	}

	/**
	 * Creates the HTTP entity from the supplied data.
	 *
	 * @param data
	 *            The data to convert to HTTP entity.
	 * @param salt
	 *            The SALT to use for security.
	 * @param ip
	 *            The current IP of the device.
	 * @return The HTTP entity ready to be sent.
	 */
	private static HttpEntity createPostEntity(AppActivity activity, final HashMap<String, Object> data) {
		HttpEntity entity = null;

		// Check if there is a file to upload
		final HashMap<String, File> files = new HashMap<String, File>();
		final List<NameValuePair> postData = new ArrayList<NameValuePair>();
		if (data != null) {
			for (final String entry : data.keySet()) {
				final Object value = data.get(entry);

				// Add files to the list
				if (value instanceof File) {
					files.put(entry, (File) value);
				} else {
					postData.add(new BasicNameValuePair(entry, value != null ? value.toString() : ""));
				}
			}
		}

		// Check for binary content
		try {
			for (final String entry : files.keySet()) {
				String encoded = getEncodedDrawable(
						MultiPhotoSelectActivity.decodeScaledFile(files.get(entry).getAbsolutePath(),
								activity.getScreenWidth(activity) / 8, activity.getScreenHeight(activity) / 8));
				postData.add(new BasicNameValuePair(entry, encoded));
			}
			entity = new UrlEncodedFormEntity(postData, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return entity;
	}

	public static void saveToFile(Context mContext, String text) {
		try {
			final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					&& mContext.getExternalCacheDir() != null ? mContext.getExternalCacheDir().getPath()
							: mContext.getCacheDir().getPath();
			// Create file
			FileWriter fstream = new FileWriter(cachePath + "/fileName.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static String getEncodedDrawable(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
	}
}
