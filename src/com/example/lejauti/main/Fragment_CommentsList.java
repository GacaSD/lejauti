package com.example.lejauti.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lejauti.AddComment;
import com.example.lejauti.JSONParser;
import com.example.lejauti.KomentariDetaljno;
import com.example.lejauti.LoadingFragment;
import com.example.lejauti.R;

import Modeli.CurrentParameters;
import Modeli.Komentar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment_CommentsList extends LoadingFragment implements OnItemClickListener {

	public static final int REQUEST_CODE_ADD_COMMENT = 1;

	// Views
	ImageView ivSlika;
	ListView mList;
	ImageAdapter imageAdapter;

	// Data
	ArrayList<String> imageList;
	String cityID;
	String country;
	String city;
	ArrayList<Komentar> listaKomentara;
	Intent intent;

	@Override
	public int getLayout() {
		return R.layout.lista_komentara_frag;
	}

	Intent mIntent;

	public void setData(Intent intent) {
		mIntent = intent;
	}

	@Override
	public JSONObject doBackground() {
		if (mIntent == null) {
			mIntent = getActivity().getIntent();
		}
		cityID = mIntent.getStringExtra("MestoID");
		city = mIntent.getStringExtra("Naziv");
		return JSONParser.getJSON("http://medinapartments.com/apartmani/getAllCommentsByTown.php?mestoID=" + cityID);
	}

	@Override
	public void onSuccess(JSONObject jsonComments) {
		findViewById(R.id.btnComments).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addComment();
			}
		});

		// Get views
		mList = (ListView) findViewById(R.id.list);

		if (jsonComments != null) {
			try {
				JSONArray main = jsonComments.getJSONArray("komentarimesta");

				// Check empty
				if (main.length() == 0) {
					findViewById(R.id.empty).setVisibility(View.VISIBLE);
					return;
				}

				listaKomentara = new ArrayList<Komentar>();
				if (main != null) {
					Log.i("main", "json nije prazan");
					for (int i = 0; i < main.length(); i++) {
						JSONObject c = main.getJSONObject(i);
						Komentar k = new Komentar();
						// gets the content of each tag
						k.setDatum(c.getString("datum"));
						k.setKomentarID(c.getInt("komentarID"));
						k.setMestoID(c.getInt("mestoID"));
						k.setDrzava(c.getString("drzava"));
						k.setGrad(c.getString("mestoID"));
						k.setMaps(c.getString("maps"));
						k.setSlika(c.getString("image"));
						k.setOcena(c.getDouble("Ocena"));
						k.setTekstKomentara(c.getString("tekstKomentara"));
						k.setUser(c.getString("user"));
						listaKomentara.add(k);
						Log.i("num", String.valueOf(listaKomentara.size()));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Set adapter
			mList.setAdapter(new ImageAdapter(getActivity(), listaKomentara));
			mList.setOnItemClickListener(this);
		} else {
			city = CurrentParameters.getCurrentCity();
			cityID = CurrentParameters.getMestoID();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Komentar k = (Komentar) view.getTag();
		Intent intent = new Intent(getActivity(), KomentariDetaljno.class);
		intent.putExtra("KomentarID", String.valueOf(k.getKomentarID()));
		intent.putExtra("Datum", k.getDatum());
		intent.putExtra("Mesto", String.valueOf(k.getMestoID()));
		intent.putExtra("Ocena", String.valueOf(k.getOcena()));
		intent.putExtra("TekstKom", k.getTekstKomentara());
		intent.putExtra("User", k.getUser());
		intent.putExtra("drzava", k.getDrzava());
		intent.putExtra("grad", k.getGrad());
		intent.putExtra("city", city);
		intent.putExtra("maps", k.getMaps());
		intent.putExtra("image", k.getSlika());
		startActivity(intent);
	}

	public void addComment() {
		Intent intent = new Intent(getActivity(), AddComment.class);
		intent.putExtra("MestoID", cityID);
		intent.putExtra("Naziv", city);
		startActivityForResult(intent, REQUEST_CODE_ADD_COMMENT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Refresh activity
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ADD_COMMENT) {
			startActivity(getActivity().getIntent());
			getActivity().finish();
		}
	}

	private class ImageAdapter extends BaseAdapter {

		ArrayList<Komentar> mList;
		LayoutInflater mInflater;
		Context mContext;

		public ImageAdapter(Context context, ArrayList<Komentar> listKom) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			this.mList = listKom;
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

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.lista_komentara_item, null);
			}

			// TextView txtKom, txtDatum, txtOcena, txtUser;

			ImageView mImage = (ImageView) convertView.findViewById(R.id.ivSlika);
			TextView txtKom = (TextView) convertView.findViewById(R.id.txtKomentar);
			TextView txtDatum = (TextView) convertView.findViewById(R.id.txtDatum);
			TextView txtOcena = (TextView) convertView.findViewById(R.id.txtOcena);
			TextView txtUser = (TextView) convertView.findViewById(R.id.txtUser);

			Komentar k = mList.get(position);
			if (!k.getSlika().equals("null")) {
				JSONParser.saveToFile(mContext, k.getSlika());
				mImage.setImageBitmap(JSONParser.decodeImage(k.getSlika()));
			} else {
				mImage.setImageResource(R.drawable.download);
			}

			txtKom.setText(k.getTekstKomentara());
			txtDatum.setText(k.getDatum());
			txtOcena.setText(String.valueOf(k.getOcena()));
			txtUser.setText(k.getUser());
			convertView.setTag(k);

			return convertView;
		}

	}
}
