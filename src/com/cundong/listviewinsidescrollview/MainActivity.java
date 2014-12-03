package com.cundong.listviewinsidescrollview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cundong.listviewinsidescrollview.weight.StickyScrollView;
import com.cundong.listviewinsidescrollview.weight.StickyScrollView.FirstStickyViewTopListener;
import com.linearlistview.internal.LinearListView;
import com.linearlistview.internal.LinearListView.OnItemClickListener;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	public static final String[] TABSTRIP_VALUE = { "简介", "微博", "数据", "文章" };

	private int mCount = 20;
	private ViewGroup mTitleBar;

	LinearListView vertical = null;
	ViewGroup mViewGroup2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitleBar = (ViewGroup) findViewById(R.id.title_bar);
		StickyScrollView parallax = (StickyScrollView) findViewById(R.id.ScrollView);
		parallax.setFirstStickyViewTopListener(new FirstStickyViewTopListener() {

			@Override
			public void onComplete(int firstStickyViewTop) {
				if (firstStickyViewTop <= 0) {
					mTitleBar.setVisibility(View.GONE);
				} else {
					mTitleBar.setVisibility(View.VISIBLE);
				}
			}
		});

		ImageView image = (ImageView) findViewById(R.id.imageView1);
		parallax.setImageViewToParallax(image);

		final TextView text1, text2, text3, text4;
		text1 = (TextView) findViewById(R.id.mybutton1);
		text2 = (TextView) findViewById(R.id.mybutton2);
		text3 = (TextView) findViewById(R.id.mybutton3);
		text4 = (TextView) findViewById(R.id.mybutton4);

		final Drawable greenBottom = getResources().getDrawable(R.drawable.tabstrip_split);
		final Drawable whiteBottom = getResources().getDrawable(R.drawable.tabstrip_split_white);

		text1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, greenBottom);

		text1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				text1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, greenBottom);
				text2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				Toast.makeText(getApplicationContext(), "mybutton1", Toast.LENGTH_SHORT).show();
			}
		});

		text2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, greenBottom);
				text3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				Toast.makeText(getApplicationContext(), "mybutton2", Toast.LENGTH_SHORT).show();
			}
		});

		text3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, greenBottom);
				text4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				Toast.makeText(getApplicationContext(), "mybutton3", Toast.LENGTH_SHORT).show();
			}
		});

		text4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, whiteBottom);
				text4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, greenBottom);
				Toast.makeText(getApplicationContext(), "mybutton4", Toast.LENGTH_SHORT).show();
			}
		});

		/**
		 * Below shows setting the scroll view shadow properties
		 * programatically.
		 */
		// StickyScrollView scrollView = (StickyScrollView)
		// findViewById(R.id.ScrollView);
		// scrollView.setShadowDrawable(getResources().getDrawable(
		// R.drawable.sticky_shadow_default));
		// scrollView.setShadowHeight(height);

		mViewGroup2 = (ViewGroup) findViewById(R.id.view2);
		vertical = (LinearListView) findViewById(R.id.vertical_list);
		vertical.setDividerDrawable(new ColorDrawable(Color.CYAN));
		vertical.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_BEGINNING);

		vertical.setDividerThickness(getResources().getDimensionPixelSize(R.dimen.padding_small));

		vertical.setAdapter(mAdapter);

		vertical.setOnItemClickListener(mListener);
	}

	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
			}

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return mCount;
		}
	};

	OnItemClickListener mListener = new OnItemClickListener() {

		@Override
		public void onItemClick(LinearListView parent, View view, int position, long id) {
			Toast.makeText(getApplicationContext(), "Tapped position " + position, Toast.LENGTH_SHORT).show();

		}
	};
}