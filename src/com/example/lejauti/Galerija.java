package com.example.lejauti;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Galerija extends Activity {

	ImageAdapter imageAdapter;
	String komentarID;
	ArrayList<String> listaSlika;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.galerija);
		Intent intent = getIntent();

		JSONObject jsonCities = JSONParser
				.getJSON("http://medinapartments.com/apartmani/getTownPictures.php?komentarID="
						+ intent.getStringExtra("komentarID"));
		listaSlika = new ArrayList<String>();
		if (jsonCities != null) {
			try {
				JSONArray main = jsonCities.getJSONArray("slikemesta");

				if (main != null) {
					for (int i = 0; i < main.length(); i++) {
						JSONObject c = main.getJSONObject(i);
						listaSlika.add(c.getString("image"));
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			imageAdapter = new ImageAdapter(this, listaSlika);

			GridView gridView = (GridView) findViewById(R.id.gridview);
			gridView.setAdapter(imageAdapter);

		}

	}

	public class ImageAdapter extends BaseAdapter {

		ArrayList<String> mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray mSparseBooleanArray;

		public ImageAdapter(Context context, ArrayList<String> imageList) {
			// TODO Auto-generated constructor stub
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			mList = new ArrayList<String>();
			this.mList = imageList;

		}

		@Override
		public int getCount() {
			return listaSlika.size();
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
				convertView = mInflater.inflate(R.layout.galerija_item, null);
				ImageView mImage = (ImageView) convertView.findViewById(R.id.ivGalerija);
				mImage.setImageBitmap(JSONParser.decodeImage(listaSlika.get(position)));
			}
			return convertView;
		}
	}
}
