package com.cundong.listviewinsidescrollview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.emilsjolander.components.stickyscrollviewitems.StickyScrollView;
import com.emilsjolander.components.stickyscrollviewitems.StickyScrollView.FirstStickyViewTopListener;
import com.linearlistview.internal.LinearListView;
import com.linearlistview.internal.LinearListView.OnItemClickListener;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

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

		findViewById(R.id.mybutton1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "mybutton1", Toast.LENGTH_SHORT).show();
				vertical.setVisibility(View.VISIBLE);
				mViewGroup2.setVisibility(View.GONE);
			}
		});

		findViewById(R.id.mybutton2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "mybutton2", Toast.LENGTH_SHORT).show();
				vertical.setVisibility(View.GONE);
				mViewGroup2.setVisibility(View.VISIBLE);
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