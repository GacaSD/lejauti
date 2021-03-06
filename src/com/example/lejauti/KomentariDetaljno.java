package com.example.lejauti;

import java.util.ArrayList;

import com.example.lejauti.galery.Galerija;

import Modeli.Komentar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class KomentariDetaljno extends Activity

{
	ArrayList<Komentar> listaKomentara;
	ArrayList<String> listaSlika;
	String komentarID, maps = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.komentari_detaljno);
		Intent intent = getIntent();

		ImageView mImage = (ImageView) findViewById(R.id.ivSlika);
		TextView txtKom = (TextView) findViewById(R.id.txtKomentar);
		TextView txtDatum = (TextView) findViewById(R.id.txtDatum);
		TextView txtOcena = (TextView) findViewById(R.id.txtOcena);
		TextView txtUser = (TextView) findViewById(R.id.txtUser);
		TextView txtgrad = (TextView) findViewById(R.id.tvMainCity);
		TextView txtUserLokacija = (TextView) findViewById(R.id.txtUserLokacija);

		txtKom.setText(intent.getStringExtra("TekstKom"));
		txtDatum.setText(intent.getStringExtra("Datum"));
		txtOcena.setText(intent.getStringExtra("Ocena"));
		txtUser.setText(intent.getStringExtra("User"));
		txtgrad.setText(intent.getStringExtra("city"));
		
		// Set image
		Bitmap map = JSONParser.decodeImage(intent.getStringExtra("image"));
		if (map != null) {
			mImage.setImageBitmap(map);
		} else {
			mImage.setImageResource(R.drawable.download);
		}
		
		if (intent.getStringExtra("drzava") != null) {
			txtUserLokacija.setText(intent.getStringExtra("drzava") + ", " + intent.getStringExtra("grad"));
		} else
			txtUserLokacija.setText("No location avalible");
		komentarID = intent.getStringExtra("KomentarID");
		maps = intent.getStringExtra("maps");
	}

	public void openGalery(View view) {
		Intent intent = new Intent(this, Galerija.class);
		intent.putExtra("komentarID", komentarID);
		startActivity(intent);
	}

	public void openMap(View view) {
		if (maps.equals("")) {
			Toast.makeText(this, "There is no pins on map", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("maps", maps);
			intent.putExtra("from_details", true);
			startActivity(intent);
		}
	}

}
