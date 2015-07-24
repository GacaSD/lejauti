package com.example.lejauti;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

public class AppActivity extends Activity {

	/**
	 * Sets the background for a view while preserving its current padding.
	 *
	 * @param view
	 *            View to receive the new background.
	 * @param backgroundResource
	 *            Resource to set as new background.
	 */
	public void setBackgroundResourceWithPadding(final View view, final int backgroundResource) {
		setBackgroundDrawableWithPadding(view, getResources().getDrawable(backgroundResource));
	}

	/**
	 * Sets the background for a view while preserving its current padding.
	 *
	 * @param view
	 *            View to receive the new background.
	 * @param backgroundDrawable
	 *            Resource to set as new background.
	 */
	@SuppressWarnings("deprecation")
	public void setBackgroundDrawableWithPadding(final View view, final Drawable backgroundDrawable) {
		if (view != null) {
			final int top = view.getPaddingTop();
			final int left = view.getPaddingLeft();
			final int right = view.getPaddingRight();
			final int bottom = view.getPaddingBottom();

			if (android.os.Build.VERSION.SDK_INT < 16) {
				view.setBackgroundDrawable(backgroundDrawable);
			} else {
				view.setBackgroundDrawable(backgroundDrawable);
			}
			view.setPadding(left, top, right, bottom);
		}
	}
	
	/**
	 * Get the width of the screen.
	 *
	 * @param context
	 *            The application context.
	 * @return Width of the screen in pixels.
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(final Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			return getScreenDimension(context).x;
		} else {
			return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		}
	}

	/**
	 * Get the height of the screen.
	 *
	 * @param context
	 *            The application context.
	 * @return Height of the screen in pixels.
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenHeight(final Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			return getScreenDimension(context).y;
		} else {
			return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		}
	}

	/**
	 * Get the dimension of the screen.
	 *
	 * @param context
	 *            The application context.
	 */
	@SuppressLint("NewApi")
	private static Point getScreenDimension(final Context context) {
		final Point size = new Point();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
		return size;
	}


}
