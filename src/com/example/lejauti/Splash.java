package com.example.lejauti;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.example.lejauti.main.MainActivity;

import Modeli.CurrentParameters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class Splash extends Activity {

	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Location loc;
	String city;
	String country;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Thread timer = new Thread() {

			public void run() {

				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {

					if (hasActiveInternetConnection()) {
						setLocation();
						if (city != null && country != null) {
							Intent openStartingPoint = new Intent(Splash.this, MainActivity.class);
							openStartingPoint.putExtra("country", CurrentParameters.getCurrentCountry());
							startActivity(openStartingPoint);
						} else {
							Intent openStartingPoint = new Intent(Splash.this, ListaDrzava.class);
							openStartingPoint.putExtra("IsList", "1");
							startActivity(openStartingPoint);
						}

					} else {
						Intent openStartingPoint = new Intent(Splash.this, NoConnection.class);
						startActivity(openStartingPoint);
					}
				}
			}

		};
		timer.start();
	}

	private void setLocation() {

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		city = null;
		country = null;
		if (loc != null) {

			Geocoder geocoder = new Geocoder(this);
			List<Address> addressList = null;
			try {
				addressList = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 2);
			} catch (IOException e) {
			}
			if (addressList != null) {
				Address address = addressList.get(0);
				if (address != null) {
					city = address.getAddressLine(1);
					country = address.getCountryName();
					CurrentParameters.setCurrentCity(address.getAddressLine(1));
					CurrentParameters.setCurrentCountry(address.getCountryName());

				} else {
					// .setText("Unable to determine the city.");
				}

			}

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	public boolean hasActiveInternetConnection() {
		if (isNetworkAvailable()) {
			try {
				HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				showToast("Error checking internet connection");
			}
		} else {
			showToast("No network available");
		}
		return false;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

}
