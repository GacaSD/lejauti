package com.example.lejauti;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import Modeli.CurrentParameters;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddComment extends AppActivity {

	// Map request code
	public static final int REQUEEST_CODE_MAP = 1;
	public static final int REQUEEST_CODE_CAMERA = 2;
	public static final int REQUEEST_CODE_NEW_CITY = 3;

	EditText currentCity, userName, text;
	RatingBar ratingBar;
	Button submit;
	String mestoID;
	int odobren = 0;
	JSONParser jsonParser = new JSONParser();
	ImageView send;
	TextView numCom, txtMape;
	private static final String POST_COMMENT_URL = "http://medinapartments.com/apartmani/addComment.php";
	private static final String UPLOAD_PHOTOS = "http://medinapartments.com/apartmani/upload.php";
	ArrayList<String> selectedImages = new ArrayList<String>(), selectedPlaces = new ArrayList<String>();
	TextView txtCurrent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_comment);
		ratingBar = (RatingBar) findViewById(R.id.rtbProductRating);
		userName = (EditText) findViewById(R.id.etCountries);
		text = (EditText) findViewById(R.id.etCountrie1s);
		txtCurrent = (TextView) findViewById(R.id.current);
		send = (ImageView) findViewById(R.id.ivSend);
		// txtKomentar = (TextView) findViewById(R.id.txtKomentar);
		numCom = (TextView) findViewById(R.id.numCom);
		numCom.setText("(0)");
		txtMape = (TextView) findViewById(R.id.txtMape);
		txtMape.setText("(0)");
		send.setVisibility(View.VISIBLE);

		selectedImages.add("/storage/emulated/0/DCIM/Camera/20150724_095749.jpg");

		showData(getIntent());
	}

	private void showData(Intent intent) {
		mestoID = intent.getStringExtra("MestoID");
		txtCurrent.setText("Your experience for " + intent.getStringExtra("Naziv"));
	}

	public void openImageGalery(View view) {
		selectedImages = null;
		Intent intent = new Intent(this, MultiPhotoSelectActivity.class);
		startActivityForResult(intent, REQUEEST_CODE_CAMERA);
	}

	/**
	 * Open map to place a pin
	 * 
	 * @param view
	 */
	public void openMap(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra("places", selectedPlaces);
		startActivityForResult(intent, REQUEEST_CODE_MAP);
	}

	public void changeCity(View view) {
		Intent intent = new Intent(this, ListaDrzava.class);
		intent.putExtra("IsList", "0");
		intent.putExtra("changeCity", true);
		startActivityForResult(intent, REQUEEST_CODE_NEW_CITY);
	}

	public void goNext(View view) {
		final float rating = ratingBar.getRating();
		final String user = userName.getText().toString();
		final String textComment = text.getText().toString();

		// Check name
		if (user.length() == 0) {
			send.setEnabled(true);
			Toast.makeText(AddComment.this, "Please enter your name.", Toast.LENGTH_LONG).show();
			return;
		}

		// Check comment
		if (textComment.length() < 50) {
			Toast.makeText(AddComment.this, "Please add at least 50 character.", Toast.LENGTH_LONG).show();
			return;
		}

		new AsyncTask<Void, Void, Boolean>() {
			ProgressDialog showCancelableDialog;

			protected void onPreExecute() {
				showCancelableDialog = showCancelableDialog(this, "Creating comment...");
			};

			@Override
			protected Boolean doInBackground(Void... params) {

				HashMap<String, Object> comment = new HashMap<String, Object>();
				comment.put("mestoID", Integer.parseInt(mestoID));
				comment.put("user", user);
				comment.put("tekstKomentara", textComment);
				comment.put("ocena", String.valueOf(rating));
				comment.put("drzava", CurrentParameters.getCurrentCountry());
				comment.put("grad", CurrentParameters.getCurrentCity());
				comment.put("maps", selectedPlaces.toString().replace("[", "").replace("]", ""));

				try {
					JSONObject jsonComment = JSONParser.makeHttpPostRequest(AddComment.this, POST_COMMENT_URL, comment);
					final String id = jsonComment.getString("commentID");

					// Image upload
					if (selectedImages != null && selectedImages.size() != 0) {
						for (int i = 0; i < selectedImages.size(); i++) {
							HashMap<String, Object> image = new HashMap<String, Object>();
							Log.d("", "UPLOAD_PHOTOS: " + selectedImages.get(i));
							image.put("image", new File(selectedImages.get(i)));
							image.put("isDefault", i == 0 ? "1" : "0");
							image.put("komentarID", id);
							jsonComment = JSONParser.makeHttpPostRequest(AddComment.this, UPLOAD_PHOTOS, image);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			protected void onPostExecute(Boolean result) {
				showCancelableDialog.dismiss();
				if (result) {
					Toast.makeText(AddComment.this, "Successufuly added your expirience!", Toast.LENGTH_SHORT).show();
					setResult(RESULT_OK);
					finish();
				} else {
					Toast.makeText(AddComment.this, "Network error occurred, try again!", Toast.LENGTH_SHORT).show();
				}
			};
		}.execute();
	}

	/**
	 * Create a progress dialog for AsyncTask that can be cancelled by user.
	 *
	 * @param task
	 *            Task that should be cancelled.
	 * @param content
	 *            Content inside the progress dialog.
	 * @return Fully built progress dialog with the ability to cancel.
	 */
	public ProgressDialog showCancelableDialog(final AsyncTask<?, ?, ?> task, final String content) {
		try {
			final ProgressDialog dialog = ProgressDialog.show(this, null, content);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(final DialogInterface dialog) {
					task.cancel(true);
				}
			});

			return dialog;
		} catch (final Exception e) {
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Check data
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEEST_CODE_CAMERA:
				selectedImages = data.getStringArrayListExtra("selectedPhotos");
				numCom.setText("(" + String.valueOf(selectedImages.size()) + ")");
				break;
			case REQUEEST_CODE_MAP:
				selectedPlaces = data.getStringArrayListExtra("data");
				txtMape.setText("(" + String.valueOf(selectedPlaces.size()) + ")");
				break;
			case REQUEEST_CODE_NEW_CITY:
				showData(data);
				break;
			}
		}

	}

}
