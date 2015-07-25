package com.example.lejauti.galery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lejauti.JSONParser;
import com.example.lejauti.LoadingFragment;
import com.example.lejauti.R;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class GaleryFragment extends LoadingFragment {

	ImageAdapter imageAdapter;
	String komentarID;

	@Override
	public JSONObject doBackground() {
		return JSONParser.getJSON("http://medinapartments.com/apartmani/getTownPictures.php?komentarID="
				+ getActivity().getIntent().getStringExtra("komentarID"));
	}

	@Override
	public int getLayout() {
		return R.layout.galery_frag;
	}

	@Override
	public void onSuccess(JSONObject jsonGalery) {
		try {
			JSONArray main = jsonGalery.getJSONArray("slikemesta");
			imageAdapter = new ImageAdapter(getActivity(), main);
			((ListView) findViewById(R.id.list)).setAdapter(imageAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class ImageAdapter extends BaseAdapter {

		JSONArray mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray mSparseBooleanArray;

		public ImageAdapter(Context context, JSONArray list) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			this.mList = list;
		}

		@Override
		public int getCount() {
			return mList.length();
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
				convertView = mInflater.inflate(R.layout.galerija_item, null);
			}

			// Show image
			ImageView mImage = (ImageView) convertView.findViewById(R.id.ivGalerija);
			try {
				mImage.setImageBitmap(JSONParser.decodeImage(mList.getJSONObject(position).getString("image")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
}