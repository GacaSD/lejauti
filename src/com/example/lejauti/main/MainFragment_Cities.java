package com.example.lejauti.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lejauti.JSONParser;
import com.example.lejauti.ListaGradova;
import com.example.lejauti.ListaKomentara;
import com.example.lejauti.LoadingFragment;
import com.example.lejauti.R;

import Modeli.CurrentParameters;
import Modeli.Grad;
import Modeli.Helper;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainFragment_Cities extends LoadingFragment {

	private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

	// Views
	ImageAdapter imageAdapter;
	TextView txtGrad, txtTime, txtWhether, txtCountry, tvAllCities;
	ImageView ivWhetherIcon;
	private ImageView imGlavniGrad;
	ListView mList;
	private Grad current = null, capital = null;
	private ViewGroup mCapitalHeader;

	// Data
	ArrayList<HashMap<String, Grad>> listaGradova = new ArrayList<HashMap<String, Grad>>();
	String country;

	@Override
	public int getLayout() {
		return R.layout.main_frag_cities;
	}

	@Override
	public JSONObject doBackground() {
		Intent intent = getActivity().getIntent();
		country = intent.getStringExtra("country");
		final String strRegUrl = country.replaceAll(" ", "%20");
		return JSONParser.getJSON(
				"http://medinapartments.com/apartmani/getTownByCountryName.php?nazivDrzava=%27" + strRegUrl + "%27");
	}

	@Override
	public void onSuccess(JSONObject jsonCities) {

		// Add comment action
		findViewById(R.id.btnComments).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ListaGradova.class);
				intent.putExtra("country", country);
				intent.putExtra("IsList", 1);
				startActivity(intent);
			}
		});

		// Get views
		mList = (ListView) findViewById(R.id.list);

		// Get Header
		mCapitalHeader = (ViewGroup) View.inflate(getActivity(), R.layout.main_header, null);
		txtGrad = (TextView) mCapitalHeader.findViewById(R.id.txtCiyMain);
		txtTime = (TextView) mCapitalHeader.findViewById(R.id.txtTime);
		txtWhether = (TextView) mCapitalHeader.findViewById(R.id.txtWhether);
		ivWhetherIcon = (ImageView) mCapitalHeader.findViewById(R.id.ivWhetherIcon);
		imGlavniGrad = (ImageView) mCapitalHeader.findViewById(R.id.imageToShow);
		txtCountry = (TextView) mCapitalHeader.findViewById(R.id.tvMainCity);

		// Get cities
		try {
			JSONArray cities = jsonCities.getJSONArray("slikemesta");

			// Show empty
			if (cities.length() == 0) {
				findViewById(R.id.empty).setVisibility(View.VISIBLE);
				findViewById(R.id.content).setVisibility(View.GONE);
				capital = null;
				return;
			} else {
				HashMap<String, Grad> item = null;
				for (int i = 0; i < cities.length(); i++) {
					JSONObject city = cities.getJSONObject(i);

					// Create city object
					final Grad objGrad = new Grad();
					objGrad.setMestoID(city.getInt("mestoID"));
					objGrad.setNaziv(city.getString("naziv"));
					objGrad.setSlika(JSONParser.decodeImage(city.getString("slika")));
					objGrad.setGlavni(city.getInt("isCapital"));

					// Check if current city
					if (objGrad.getNaziv().equals(CurrentParameters.getCurrentCity())) {
						current = objGrad;
					}

					// Split capital
					if (objGrad.getGlavni() == 0) {
						if (item == null) {
							item = new HashMap<String, Grad>();
							item.put("left", objGrad);
							listaGradova.add(item);
						} else {
							item.put("right", objGrad);
							item = null;
						}
					} else {

						// Fill header/capital
						capital = objGrad;
						imGlavniGrad.setImageBitmap(capital.getSlika());
						txtGrad.setText(capital.getNaziv());
						JSONObject json = Helper.getJSON(String.format(OPEN_WEATHER_MAP_API, capital.getNaziv()));
						if (json != null) {
							setWeter(json);
						}
						mCapitalHeader.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(getActivity(), ListaKomentara.class);
								intent.putExtra("MestoID", String.valueOf(capital.getMestoID()));
								intent.putExtra("Naziv", capital.getNaziv());
								startActivity(intent);
							}
						});
					}
				}
			}

			// Load current city comments
			if (current != null) {
				CurrentParameters.setMestoID(String.valueOf(current.getMestoID()));
				loadCurrent(current);
			} else if (capital != null) {
				CurrentParameters.setMestoID(String.valueOf(capital.getMestoID()));
				loadCurrent(capital);
			} else {
				((MainActivity) getActivity()).loadCurrent();
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		setTime();
		mList.addHeaderView(mCapitalHeader);

		// Small header
		ViewGroup headerSmall = (ViewGroup) View.inflate(getActivity(), R.layout.main_header_small, null);
		mList.addHeaderView(headerSmall);
		tvAllCities = (TextView) findViewById(R.id.tvAllCities);
		tvAllCities.setText(country);

		// Fill list
		mList.setAdapter(new ImageAdapter(getActivity(), listaGradova));
		mList.setDivider(null);
		mList.setDividerHeight(0);
	}

	public void loadCurrent(Grad objGrad) {
		Intent intent = new Intent(getActivity(), ListaKomentara.class);
		intent.putExtra("isCurent", "1");
		intent.putExtra("MestoID", CurrentParameters.getMestoID());
		intent.putExtra("country", country);
		intent.putExtra("Naziv", objGrad.getNaziv());
		((MainActivity) getActivity()).loadCurrent(intent);
	}

	private void setTime() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		txtTime.setText(today.format("%k:%M"));
	}

	private void setWeter(JSONObject json) {
		try {
			JSONObject details = json.getJSONArray("weather").getJSONObject(0);
			JSONObject main = json.getJSONObject("main");
			txtWhether.setText(String.format("%.2f", main.getDouble("temp")) + " ℃");
			setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000,
					json.getJSONObject("sys").getLong("sunset") * 1000);
		} catch (Exception e) {
			Log.e("SimpleWeather", "One or more fields not found in the JSON data");
		}
	}

	private void setWeatherIcon(int actualId, long sunrise, long sunset) {
		int id = actualId / 100;
		int icon = R.drawable.oblacno;
		if (actualId == 800) {
			long currentTime = new Date().getTime();
			if (currentTime >= sunrise && currentTime < sunset) {
				icon = R.drawable.sunce;// R.drawable.sun
			} else {
				icon = R.drawable.vedra_noc;
			}
		} else {
			switch (id) {
			case 2:
				icon = R.drawable.grmljevina;
				break;
			case 3:
				icon = R.drawable.kisica;
				break;
			case 7:
				icon = R.drawable.magla;
				break;
			case 8:
				icon = R.drawable.oblacno;// R.drawable.oblacno
				break;
			case 6:
				icon = R.drawable.sneg;
				break;
			case 5:
				icon = R.drawable.kisa;
				break;
			}
		}
		ImageView ivWhetherIcon = (ImageView) mCapitalHeader.findViewById(R.id.ivWhetherIcon);
		ivWhetherIcon.setImageResource(icon);
	}

	public class ImageAdapter extends BaseAdapter {

		ArrayList<HashMap<String, Grad>> mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray mSparseBooleanArray;

		public ImageAdapter(Context context, ArrayList<HashMap<String, Grad>> listGrad) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			this.mList = listGrad;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Inflate item
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.main_item, null);
			}

			// Get city
			HashMap<String, Grad> item = mList.get(position);
			fillItem(item.get("left"), convertView.findViewById(R.id.left));
			View right = convertView.findViewById(R.id.right);
			if (item.containsKey("right")) {
				fillItem(item.get("right"), right);
			} else {
				right.setVisibility(View.INVISIBLE);
				right.setOnClickListener(null);
			}
			return convertView;
		}

		private void fillItem(final Grad g, View item) {

			// Set image
			DisplayMetrics metrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
			((ImageView) item.findViewById(R.id.imageView1)).setImageBitmap(g.getSlika());

			// Set city name
			TextView ttxtGrad = (TextView) item.findViewById(R.id.txtNazivGrada);
			ttxtGrad.setText(g.getNaziv());
			item.setTag(g);
			item.setVisibility(View.VISIBLE);
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ListaKomentara.class);
					intent.putExtra("MestoID", String.valueOf(g.getMestoID()));
					intent.putExtra("Naziv", g.getNaziv());
					startActivity(intent);
				}
			});
		}
	}
}
