package com.example.lejauti;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lejauti.main.MainActivity;

import Modeli.CurrentParameters;
import Modeli.Drzava;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class ListaDrzava extends Activity {

	public static final int NEW_CITY = 10;
	TextView tv;
	GridView gb;
	ArrayList<Drzava> listaDrzava;
	String IsList;
	TextView txtCurrent;
	private Boolean mChangeCity = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_drzava);
		Intent intent = getIntent();
		JSONObject jsonCountries = JSONParser.getJSON("http://medinapartments.com/apartmani/vratiSveDrzave.php");

		IsList = intent.getStringExtra("IsList");
		mChangeCity = intent.getBooleanExtra("changeCity", false);
		txtCurrent = (TextView) findViewById(R.id.current);
		if (CurrentParameters.getCurrentCountry() != null) {
			txtCurrent.setText("My current country: " + CurrentParameters.getCurrentCountry());
		} else {
			txtCurrent.setText("Cant find current country. Find country...");
		}
		if (jsonCountries != null) {
			try {
				JSONArray main = jsonCountries.getJSONArray("drzave");
				listaDrzava = new ArrayList<Drzava>();
				if (main != null) {
					for (int i = 0; i < main.length(); i++) {
						JSONObject c = main.getJSONObject(i);
						Drzava d = new Drzava();
						// gets the content of each tag
						d.setId(c.getInt("drzavaID"));
						d.setName(c.getString("nazivDrzava"));
						listaDrzava.add(d);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ListView list = (ListView) findViewById(R.id.list);
			list.setVisibility(View.VISIBLE);
			list.setAdapter(new ImageAdapter(this, listaDrzava));
		}
	}

	public void getCurrent(View view) {
		if (CurrentParameters.getCurrentCountry() != null) {
			if (IsList.equals("1")) {
				Intent intent = new Intent(this, MainActivity.class);
				intent.putExtra("country", CurrentParameters.getCurrentCountry());
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, ListaGradova.class);
				intent.putExtra("country", CurrentParameters.getCurrentCountry());
				intent.putExtra("IsList", IsList);

				startActivity(intent);
			}
		}
	}

	private class ImageAdapter extends BaseAdapter {

		ArrayList<Drzava> mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray mSparseBooleanArray;

		public ImageAdapter(Context context, ArrayList<Drzava> listaDrzava) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			mList = new ArrayList<Drzava>();
			this.mList = listaDrzava;

		}

		@Override
		public int getCount() {
			return listaDrzava.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.simple_item, null);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onItemClick(v, position);
				}
			});

			// TextView txtKom, txtDatum, txtOcena, txtUser;
			TextView txtNaziv = (TextView) convertView;

			Drzava d = listaDrzava.get(position);
			txtNaziv.setText(d.getName());
			convertView.setTag(d);
			return convertView;
		}
	}

	public void onItemClick(View view, int position) {
		Drzava d = (Drzava) view.getTag();

		Intent intent;
		if (IsList.equals("1")) {
			intent = new Intent(this, MainActivity.class);
			intent.putExtra("country", d.getName());
			startActivity(intent);
		} else {
			intent = new Intent(this, ListaGradova.class);
			intent.putExtra("country", d.getName());
			intent.putExtra("IsList", IsList);
			intent.putExtra("changeCity", mChangeCity);
			startActivity(intent);
		}
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK == resultCode && requestCode == NEW_CITY) {
			setResult(RESULT_OK, data);
			finish();
		}
	}

}
