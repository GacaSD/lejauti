package com.example.lejauti;

import com.example.lejauti.main.Fragment_CommentsList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ListaKomentara extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_komentara);
		Fragment_CommentsList fragCurrent = (Fragment_CommentsList) getFragmentManager().findFragmentById(R.id.frag_current);
		fragCurrent.load();
	}
}
