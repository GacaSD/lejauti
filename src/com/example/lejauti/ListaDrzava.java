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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ListaDrzava extends Activity implements OnItemClickListener {

	public static final int NEW_CITY = 10;
	TextView tv;
	GridView gb;
	ImageAdapter imageAdapter;
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
			txtCurrent.setText("My current countru :" + CurrentParameters.getCurrentCountry());
		} else {
			txtCurrent.setText("Cant find current country. Find country...");
		}
		if (jsonCountries != null) {
			try {
				JSONArray main = jsonCountries.getJSONArray("drzave");
				Log.i("main", main.toString());
				listaDrzava = new ArrayList<Drzava>();
				if (main != null) {
					Log.i("main", "json nije prazan");
					for (int i = 0; i < main.length(); i++) {
						JSONObject c = main.getJSONObject(i);
						Drzava d = new Drzava();
						// gets the content of each tag
						d.setId(c.getInt("drzavaID"));
						d.setName(c.getString("nazivDrzava"));
						listaDrzava.add(d);
						Log.i("num", String.valueOf(listaDrzava.size()));
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			imageAdapter = new ImageAdapter(this, listaDrzava);

			GridView gridView = (GridView) findViewById(R.id.gridview);
			gridView.setAdapter(imageAdapter);
			gridView.setOnItemClickListener(this);
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
			// TODO Auto-generated constructor stub
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
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.drzave_item, null);
			}

			// TextView txtKom, txtDatum, txtOcena, txtUser;
			TextView txtNaziv = (TextView) convertView.findViewById(R.id.txtDrzava);

			Drzava d = listaDrzava.get(position);

			txtNaziv.setText(d.getName());
			convertView.setTag(d);

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Log.i("listaDrzava", IsList);
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
		}
		startActivityForResult(intent, NEW_CITY);
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
