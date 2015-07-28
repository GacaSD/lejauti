package com.example.lejauti;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MapActivity extends FragmentActivity {

	GoogleMap googleMap;
	ArrayList<String> mPoints;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.map__index);

		// Get Places
		if (getIntent() != null && getIntent().getExtras() != null) {
			Bundle extras = getIntent().getExtras();
			if (extras.containsKey("places")) {
				mPoints = getIntent().getStringArrayListExtra("places");
			} else if (extras.containsKey("maps")) {
				String maps = extras.getString("maps");
				if (maps != null && !maps.equals("")) {
					String[] split = maps.split(",");
					mPoints = new ArrayList<String>();
					for (String latLong : split) {
						mPoints.add(latLong);
					}
				}
			}
		}
		if (mPoints == null) {
			mPoints = new ArrayList<String>();
		}

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) {

			// Google Play Services are not available
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		} else {

			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

			// Load places
			if (mPoints != null && mPoints.size() > 0) {
				final LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (String place : mPoints) {
					String[] split = place.split(":");
					LatLng location = new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
					drawMarker(location);
					builder.include(location);
				}
				googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

				    @Override
				    public void onCameraChange(CameraPosition arg0) {
				    	
				        // Move camera.
				    	googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
				        // Remove listener to prevent position reset on camera move.
				    	googleMap.setOnCameraChangeListener(null);
				    }
				});
			}
		}

		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				// Drawing marker on the map
				drawMarker(point);
				mPoints.add(point.latitude + ":" + point.longitude);
			}
		});

		// Don't change markers if details
		if (!getIntent().getExtras().containsKey("from_details")) {
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					marker.setVisible(false);
					String locationToRemove = null;
					for (String item : mPoints) {
						if (item.equals(marker.getPosition().latitude + ":" + marker.getPosition().longitude)) {
							locationToRemove = item;
						}
					}
					mPoints.remove(locationToRemove);
					return false;
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Moving CameraPosition to last clicked position
		Location location = googleMap.getMyLocation();
		if (location != null) {
			LatLng myPostion = new LatLng(location.getLatitude(), location.getLongitude());
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPostion));

			// Setting the zoom level in the map on last position is clicked
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(10f));
		}
	}

	private void drawMarker(LatLng point) {
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude and longitude for the marker
		markerOptions.position(point);

		// Adding marker on the Google Map
		googleMap.addMarker(markerOptions);
	}

	@Override
	public void finish() {

		// Return data
		setResult(RESULT_OK, getIntent().putExtra("data", mPoints));
		super.finish();
	}

}
