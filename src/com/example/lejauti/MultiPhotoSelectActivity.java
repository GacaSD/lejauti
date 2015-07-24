package com.example.lejauti;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/**
 * @author Paresh Mayani (@pareshmayani)
 */
public class MultiPhotoSelectActivity extends AppActivity {

	private ArrayList<String> imageUrls, mSelectedImages = new ArrayList<String>();
	private DisplayImageOptions options;
	private LruCache<String, Bitmap> mCache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);

		// Load images from gallery
		new AsyncTask<Void, Void, ArrayList<String>>() {

			@Override
			protected ArrayList<String> doInBackground(Void... params) {
				final String[] columns = { MediaStore.Images.Media.DATA };
				Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
						MediaStore.Images.Media.DATE_TAKEN + " DESC");
				ArrayList<String> imageUrls = new ArrayList<String>();
				for (int i = 0; i < imagecursor.getCount(); i++) {
					imagecursor.moveToPosition(i);
					int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
					imageUrls.add(imagecursor.getString(dataColumnIndex));
				}
				imagecursor.close();
				return imageUrls;
			}

			@Override
			protected void onPostExecute(ArrayList<String> result) {

				// Show content
				findViewById(R.id.content).setVisibility(View.VISIBLE);
				findViewById(R.id.preloader).setVisibility(View.GONE);

				// Load list
				mCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024) / 5);
				GridView gridView = (GridView) findViewById(R.id.gridview);
				gridView.setAdapter(new ImageAdapter(MultiPhotoSelectActivity.this, imageUrls = result));
			}
		}.execute();
	}

	public void btnChoosePhotosClick(View v) {
		Intent intent = new Intent(this, AddComment.class);
		intent.putExtra("selectedPhotos", mSelectedImages);
		Log.d("", "onActivityResult: " + mSelectedImages.size());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public class ImageAdapter extends BaseAdapter {

		ArrayList<String> mList;
		LayoutInflater mInflater;
		Context mContext;

		public ImageAdapter(Context context, ArrayList<String> imageList) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mList = new ArrayList<String>();
			this.mList = imageList;
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
				convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
			}

			// Get Items
			final CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			ImageView mImage = (ImageView) convertView.findViewById(R.id.imageView1);

			// your image file path
			final String url = mList.get(position);
			File file = new File(url);
			int dimen = (int) (getScreenWidth(MultiPhotoSelectActivity.this) * 0.33);
			mImage.setTag(url);
			mImage.setLayoutParams(new RelativeLayout.LayoutParams(dimen, dimen));
			mImage.setImageResource(R.drawable.ic_launcher);
			mImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View image) {
					if (mCheckBox.isChecked()) {
						mSelectedImages.remove(url);
					} else {
						mSelectedImages.add(url);
					}
					mCheckBox.setChecked(mSelectedImages.contains(url));
				}
			});
			mCheckBox.setChecked(mSelectedImages.contains(url));
			loadImage(mImage);
			return convertView;
		}

		public void loadImage(final ImageView image) {

			// Check cache
			String url = image.getTag().toString();
			Bitmap bitmap = mCache.get(url);
			if (bitmap != null) {
				image.setImageBitmap(bitmap);
			} else {
				new AsyncTask<String, Void, Object[]>() {

					@Override
					protected Object[] doInBackground(String... params) {
						int dimen = getScreenWidth(MultiPhotoSelectActivity.this) / 3;
						Bitmap map = decodeScaledFile(params[0], dimen, dimen);
						return new Object[] { map, params[0] };
					}

					protected void onPostExecute(Object[] result) {
						if (image.getTag().toString().equals(result[1].toString())) {
							mCache.put(result[1].toString(), (Bitmap) result[0]);
							image.setImageBitmap((Bitmap) result[0]);
						}
					};
				}.execute(url);
			}
		}
	}

	public static String getCachePath(Context context) {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&& context.getExternalCacheDir() != null ? context.getExternalCacheDir().getPath()
						: context.getCacheDir().getPath();
	}

	/**
	 * Decodes a scaled bitmap from the file system.
	 *
	 * @param file
	 *            The file to decode.
	 * @param maxWidth
	 *            The maximum width of the resulting bitmap.
	 * @param maxHeight
	 *            The maximum height of the resulting bitmap.
	 * @return The scaled bitmap, with preserved aspect ratio.
	 */
	public static Bitmap decodeScaledFile(final String file, final int maxWidth, final int maxHeight) {

		// Get the information about the original bitmap
		final BitmapFactory.Options bitmapOptions = getImageOriginalDimension(file);
		final int orignalWidth = bitmapOptions.outWidth;
		final int originalHeight = bitmapOptions.outHeight;

		// Get the scale
		if (originalHeight > maxWidth || orignalWidth > maxHeight) {

			// Calculate aspect ratios of height and width
			final int widthRatio = Math.round((float) orignalWidth / (float) maxWidth);
			final int heightRatio = Math.round((float) originalHeight / (float) maxHeight);

			// Choose the smallest ratio as inSampleSize value
			bitmapOptions.inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		bitmapOptions.inJustDecodeBounds = false;
		Bitmap resizedBitmap = BitmapFactory.decodeFile(file, bitmapOptions);

		// Rotate image manually, just for SAMSUNG and SONY
		final Matrix matrix = new Matrix();
		try {
			final ExifInterface exif = new ExifInterface(file);
			final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.postRotate(90);
				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.postRotate(180);
				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.postRotate(270);
				break;
			}
			resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(),
					resizedBitmap.getHeight(), matrix, true);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return resizedBitmap;
	}

	/**
	 * Get image original dimensions
	 *
	 * @param file
	 *            Image path
	 * @return
	 */
	public static BitmapFactory.Options getImageOriginalDimension(final String file) {
		final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 1;
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, bitmapOptions);
		return bitmapOptions;
	}
}