package com.example.lejauti;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Modeli.CurrentParameters;
import Modeli.Grad;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ListaGradova extends Activity implements OnItemClickListener {

	TextView tv;
	GridView gb;
	ImageAdapter imageAdapter;
	ArrayList<Grad> listaGradova;
	String IsList;
	String country;
	TextView txtCurrent;
	TextView drzava;
	private Boolean mChangeCity = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_gradova);

		Intent intent = getIntent();
		IsList = intent.getStringExtra("IsList");
		country = intent.getStringExtra("country");
		txtCurrent = (TextView) findViewById(R.id.current);
		drzava = (TextView) findViewById(R.id.drzava);
		mChangeCity = intent.getBooleanExtra("changeCity", false);
		if (CurrentParameters.getCurrentCity() != null) {
			txtCurrent.setText("My current city : " + CurrentParameters.getCurrentCity() + ", " + CurrentParameters.getCurrentCountry());
		} else {
			txtCurrent.setText("Cant find your current city.Searh for Cities ");
		}

		drzava.setText("Cities of " + country);


		JSONObject jsonTowns = null;

		String strRegUrl = country.replaceAll(" ", "%20");
		jsonTowns = JSONParser.getJSON("http://medinapartments.com/apartmani/getTownByCountryName.php?nazivDrzava=%27" + strRegUrl + "%27");

		listaGradova = new ArrayList<Grad>();
		if (jsonTowns != null) {
			try {
				JSONArray main = jsonTowns.getJSONArray("slikemesta");
				if (main != null) {
					for (int i = 0; i < main.length(); i++) {
						JSONObject c = main.getJSONObject(i);
						Grad g = new Grad();
						// gets the content of each tag
						g.setMestoID(c.getInt("mestoID"));
						g.setNaziv(c.getString("naziv"));
						listaGradova.add(g);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			imageAdapter = new ImageAdapter(this, listaGradova);

			GridView gridView = (GridView) findViewById(R.id.gridview);
			gridView.setAdapter(imageAdapter);
			gridView.setOnItemClickListener(this);
		}
	}

	public void getCurrent(View view) {
		if (CurrentParameters.getCurrentCity() != null) {
			int id = -1;
			for (int i = 0; i < listaGradova.size(); i++) {
				if (listaGradova.get(i).getNaziv().equals(CurrentParameters.getCurrentCity())) {
					id = listaGradova.get(i).getMestoID();
					break;
				}
			}

			// if (IsList.equals("1")) {
			//
			// Intent intent = new Intent(this, ListaKomentara.class);
			// intent.putExtra("MestoID", String.valueOf(id));
			// intent.putExtra("Naziv", CurrentParameters.getCurrentCity());
			// startActivity(intent);
			// } else {
			Intent intent = new Intent(this, AddComment.class);
			intent.putExtra("MestoID", String.valueOf(id));
			intent.putExtra("Naziv", CurrentParameters.getCurrentCity());
			startActivity(intent);
			// }


		}

	}

	private class ImageAdapter extends BaseAdapter {

		ArrayList<Grad> mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray mSparseBooleanArray;

		public ImageAdapter(Context context, ArrayList<Grad> listaGradova) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			mList = new ArrayList<Grad>();
			this.mList = listaGradova;
		}

		@Override
		public int getCount() {
			return listaGradova.size();
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
				convertView = mInflater.inflate(R.layout.gradovi_item, null);
			}

			// TextView txtKom, txtDatum, txtOcena, txtUser;
			TextView txtNaziv = (TextView) convertView.findViewById(R.id.txtGrad);

			Grad g = listaGradova.get(position);

			txtNaziv.setText(g.getNaziv());
			convertView.setTag(g);

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Grad g = (Grad) view.getTag();
		if (!mChangeCity) {
			Intent intent = new Intent(this, AddComment.class);
			intent.putExtra("MestoID", String.valueOf(g.getMestoID()));
			intent.putExtra("Naziv", g.getNaziv());
			startActivity(intent);
		} else {
			Intent intent = getIntent();
			intent.putExtra("MestoID", String.valueOf(g.getMestoID()));
			intent.putExtra("Naziv", g.getNaziv());
			intent.putExtra("country", country);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

}
