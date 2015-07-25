package com.example.lejauti.main;

import com.example.lejauti.AppActivity;
import com.example.lejauti.ListaDrzava;
import com.example.lejauti.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppActivity {

	ViewGroup mTabContainer;
	Fragment_CommentsList fragCurrent;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mTabContainer = (ViewGroup) findViewById(R.id.tab_container);

		// Load Fragments
		fragCurrent = (Fragment_CommentsList) getFragmentManager().findFragmentById(R.id.frag_current);
		MainFragment_Cities fragCities = (MainFragment_Cities) getFragmentManager().findFragmentById(R.id.frag_cities);
		fragCities.load();

		ImageView search = (ImageView) findViewById(R.id.ivSearchH);
		search.setVisibility(View.VISIBLE);

		// Pager
		final ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return mPager.getChildCount();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public Object instantiateItem(final ViewGroup container, final int position) {
				return mPager.getChildAt(position);
			}

			@Override
			public void destroyItem(final ViewGroup container, final int position, final Object object) {
			}
		});
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					setBackgroundResourceWithPadding(mTabContainer.getChildAt(0), R.drawable.bg_tab);
					setBackgroundResourceWithPadding(mTabContainer.getChildAt(1), R.drawable.empty);
					break;
				case 1:
					setBackgroundResourceWithPadding(mTabContainer.getChildAt(0), R.drawable.empty);
					setBackgroundResourceWithPadding(mTabContainer.getChildAt(1), R.drawable.bg_tab);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		setBackgroundResourceWithPadding(mTabContainer.getChildAt(0), R.drawable.bg_tab);

		// Tabs actions
		mTabContainer.getChildAt(0).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(0, true);
			}
		});
		mTabContainer.getChildAt(1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1, true);
			}
		});
	}

	public void loadCurrent(Intent intent) {
		fragCurrent.setData(intent);
		fragCurrent.load();
	}

	public void search(View view) {
		Intent intent = new Intent(this, ListaDrzava.class);
		intent.putExtra("IsList", "1");
		startActivity(intent);
	}
}
