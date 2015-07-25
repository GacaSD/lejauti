package com.example.lejauti.galery;

import com.example.lejauti.R;

import android.app.Activity;
import android.os.Bundle;

public class Galerija extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.galerija);
		((GaleryFragment) getFragmentManager().findFragmentById(R.id.frag_galery)).load();
	}
}
