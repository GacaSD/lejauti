package com.example.lejauti;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class LoadingFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getLayout(), container);
	}

	public View findViewById(int id) {
		return getView().findViewById(id);
	};

	public void load() {
		new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				return doBackground();
			}

			protected void onPostExecute(JSONObject result) {
				if (result != null) {
					findViewById(R.id.preloader).setVisibility(View.GONE);
					findViewById(R.id.content).setVisibility(View.VISIBLE);
					onSuccess(result);
				} else {
					Toast.makeText(getActivity(), R.string.network_error_occurred, Toast.LENGTH_SHORT).show();
				}
			};

		}.execute();
	}

	public abstract JSONObject doBackground();

	public abstract int getLayout();

	public abstract void onSuccess(JSONObject result);
}
