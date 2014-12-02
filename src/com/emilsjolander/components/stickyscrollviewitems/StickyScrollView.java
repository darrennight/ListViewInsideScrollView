package com.emilsjolander.components.stickyscrollviewitems;

import java.util.ArrayList;

import net.appkraft.parallax.BackAnimimation;
import net.appkraft.parallax.OnOverScrollByListener;
import net.appkraft.parallax.OnTouchEventListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.cundong.listviewinsidescrollview.R;

@SuppressLint("NewApi")
public class StickyScrollView extends ScrollView {

	/**
	 * Tag for views that should stick and have constant drawing. e.g.
	 * TextViews, ImageViews etc
	 */
	public static final String STICKY_TAG = "sticky";

	/**
	 * Flag for views that should stick and have non-constant drawing. e.g.
	 * Buttons, ProgressBars etc
	 */
	public static final String FLAG_NONCONSTANT = "-nonconstant";

	/**
	 * Flag for views that have aren't fully opaque
	 */
	public static final String FLAG_HASTRANSPARANCY = "-hastransparancy";

	/**
	 * Default height of the shadow peeking out below the stuck view.
	 */
	private static final int DEFAULT_SHADOW_HEIGHT = 10; // dp;

	private ArrayList<View> stickyViews;
	private View currentlyStickingView;
	private float stickyViewTopOffset;
	private int stickyViewLeftOffset;
	private boolean redirectTouchesToStickyView;
	private boolean clippingToPadding;
	private boolean clipToPaddingHasBeenSet;

	private int mShadowHeight;
	private Drawable mShadowDrawable;

	private final Runnable invalidateRunnable = new Runnable() {

		@Override
		public void run() {
			if (currentlyStickingView != null) {
				int l = getLeftForViewRelativeOnlyChild(currentlyStickingView);
				int t = getBottomForViewRelativeOnlyChild(currentlyStickingView);
				int r = getRightForViewRelativeOnlyChild(currentlyStickingView);
				int b = (int) (getScrollY() + (currentlyStickingView.getHeight() + stickyViewTopOffset));
				invalidate(l, t, r, b);
			}
			postDelayed(this, 16);
		}
	};

	public StickyScrollView(Context context) {
		this(context, null);
		init(context, null);
	}

	public StickyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.scrollViewStyle);
		init(context, attrs);
	}

	public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
		setup();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickyScrollView, defStyle, 0);

		final float density = context.getResources().getDisplayMetrics().density;
		int defaultShadowHeightInPix = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);

		mShadowHeight = a.getDimensionPixelSize(R.styleable.StickyScrollView_stuckShadowHeight, defaultShadowHeightInPix);

		int shadowDrawableRes = a.getResourceId(R.styleable.StickyScrollView_stuckShadowDrawable, -1);

		if (shadowDrawableRes != -1) {
			mShadowDrawable = context.getResources().getDrawable(shadowDrawableRes);
		}

		a.recycle();

	}

	/**
	 * Sets the height of the shadow drawable in pixels.
	 * 
	 * @param height
	 */
	public void setShadowHeight(int height) {
		mShadowHeight = height;
	}

	public void setup() {
		stickyViews = new ArrayList<View>();
	}

	private int getLeftForViewRelativeOnlyChild(View v) {
		int left = v.getLeft();
		while (v.getParent() != getChildAt(0)) {
			v = (View) v.getParent();
			left += v.getLeft();
		}
		return left;
	}

	private int getTopForViewRelativeOnlyChild(View v) {
		int top = v.getTop();
		while (v.getParent() != getChildAt(0)) {
			v = (View) v.getParent();
			top += v.getTop();
		}
		return top;
	}

	private int getRightForViewRelativeOnlyChild(View v) {
		int right = v.getRight();
		while (v.getParent() != getChildAt(0)) {
			v = (View) v.getParent();
			right += v.getRight();
		}
		return right;
	}

	private int getBottomForViewRelativeOnlyChild(View v) {
		int bottom = v.getBottom();
		while (v.getParent() != getChildAt(0)) {
			v = (View) v.getParent();
			bottom += v.getBottom();
		}
		return bottom;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!clipToPaddingHasBeenSet) {
			clippingToPadding = true;
		}

		notifyHierarchyChanged();
	}

	@Override
	public void setClipToPadding(boolean clipToPadding) {
		super.setClipToPadding(clipToPadding);
		clippingToPadding = clipToPadding;
		clipToPaddingHasBeenSet = true;
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		findStickyViews(child);
	}

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
		findStickyViews(child);
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		findStickyViews(child);
	}

	@Override
	public void addView(View child, int width, int height) {
		super.addView(child, width, height);
		findStickyViews(child);
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, params);
		findStickyViews(child);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (currentlyStickingView != null) {
			canvas.save();
			canvas.translate(getPaddingLeft() + stickyViewLeftOffset, getScrollY() + stickyViewTopOffset + (clippingToPadding ? getPaddingTop() : 0));

			canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth() - stickyViewLeftOffset, currentlyStickingView.getHeight() + mShadowHeight + 1);

			if (mShadowDrawable != null) {
				int left = 0;
				int right = currentlyStickingView.getWidth();
				int top = currentlyStickingView.getHeight();
				int bottom = currentlyStickingView.getHeight() + mShadowHeight;
				mShadowDrawable.setBounds(left, top, right, bottom);
				mShadowDrawable.draw(canvas);
			}

			canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth(), currentlyStickingView.getHeight());
			if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)) {
				showView(currentlyStickingView);
				currentlyStickingView.draw(canvas);
				hideView(currentlyStickingView);
			} else {
				currentlyStickingView.draw(canvas);
			}
			canvas.restore();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			redirectTouchesToStickyView = true;
		}

		if (redirectTouchesToStickyView) {
			redirectTouchesToStickyView = currentlyStickingView != null;
			if (redirectTouchesToStickyView) {
				redirectTouchesToStickyView = ev.getY() <= (currentlyStickingView.getHeight() + stickyViewTopOffset) && ev.getX() >= getLeftForViewRelativeOnlyChild(currentlyStickingView) && ev.getX() <= getRightForViewRelativeOnlyChild(currentlyStickingView);
			}
		} else if (currentlyStickingView == null) {
			redirectTouchesToStickyView = false;
		}
		if (redirectTouchesToStickyView) {
			ev.offsetLocation(0, -1 * ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
		}
		return super.dispatchTouchEvent(ev);
	}

	private boolean hasNotDoneActionDown = true;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (redirectTouchesToStickyView) {
			ev.offsetLocation(0, ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
		}

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			hasNotDoneActionDown = false;
		}

		if (hasNotDoneActionDown) {
			MotionEvent down = MotionEvent.obtain(ev);
			down.setAction(MotionEvent.ACTION_DOWN);
			super.onTouchEvent(down);
			hasNotDoneActionDown = false;
		}

		if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
			hasNotDoneActionDown = true;
		}

		for (int i = 0; i < mOnTouchEventList.size(); i++) {
			mOnTouchEventList.get(i).onTouchEvent(ev);
		}

		return super.onTouchEvent(ev);
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

		boolean isCollapseAnimation = false;

		for (int i = 0; i < mOnOverScrollByList.size(); i++) {

			isCollapseAnimation = mOnOverScrollByList.get(i).overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent) || isCollapseAnimation;

		}

		return isCollapseAnimation ? true : super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		doTheStickyThing();
	}

	FirstStickyViewTopListener mFirstStickyViewTopListener = null;

	public void setFirstStickyViewTopListener(FirstStickyViewTopListener listener) {
		this.mFirstStickyViewTopListener = listener;
	}

	public interface FirstStickyViewTopListener {
		public void onComplete(int firstStickyViewTop);
	}

	private void doTheStickyThing() {
		View viewThatShouldStick = null;
		View approachingView = null;
		for (int i = 0; i < stickyViews.size(); i++) {
			View v = stickyViews.get(i);

			int viewTop = getTopForViewRelativeOnlyChild(v) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop());

			if (viewTop <= 0) {
				if (viewThatShouldStick == null || viewTop > (getTopForViewRelativeOnlyChild(viewThatShouldStick) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))) {
					viewThatShouldStick = v;
				}
			} else {
				if (approachingView == null || viewTop < (getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))) {
					approachingView = v;
				}
			}

			if (i == 0) {
				if (mFirstStickyViewTopListener != null) {
					mFirstStickyViewTopListener.onComplete(viewTop);
				}
			}
		}

		if (viewThatShouldStick != null) {
			stickyViewTopOffset = approachingView == null ? 0 : Math.min(0, getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()) - viewThatShouldStick.getHeight());
			if (viewThatShouldStick != currentlyStickingView) {
				if (currentlyStickingView != null) {
					stopStickingCurrentlyStickingView();
				}
				// only compute the left offset when we start sticking.
				stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(viewThatShouldStick);
				startStickingView(viewThatShouldStick);
			}
		} else if (currentlyStickingView != null) {
			stopStickingCurrentlyStickingView();
		}
	}

	private void startStickingView(View viewThatShouldStick) {
		currentlyStickingView = viewThatShouldStick;
		if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)) {
			hideView(currentlyStickingView);
		}
		if (((String) currentlyStickingView.getTag()).contains(FLAG_NONCONSTANT)) {
			post(invalidateRunnable);
		}
	}

	private void stopStickingCurrentlyStickingView() {
		if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)) {
			showView(currentlyStickingView);
		}
		currentlyStickingView = null;
		removeCallbacks(invalidateRunnable);
	}

	/**
	 * Notify that the sticky attribute has been added or removed from one or
	 * more views in the View hierarchy
	 */
	public void notifyStickyAttributeChanged() {
		notifyHierarchyChanged();
	}

	private void notifyHierarchyChanged() {
		if (currentlyStickingView != null) {
			stopStickingCurrentlyStickingView();
		}
		stickyViews.clear();
		findStickyViews(getChildAt(0));
		doTheStickyThing();
		invalidate();
	}

	private void findStickyViews(View v) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				String tag = getStringTagForView(vg.getChildAt(i));
				if (tag != null && tag.contains(STICKY_TAG)) {
					stickyViews.add(vg.getChildAt(i));
				} else if (vg.getChildAt(i) instanceof ViewGroup) {
					findStickyViews(vg.getChildAt(i));
				}
			}
		} else {
			String tag = (String) v.getTag();
			if (tag != null && tag.contains(STICKY_TAG)) {
				stickyViews.add(v);
			}
		}
	}

	private String getStringTagForView(View v) {
		Object tagObject = v.getTag();
		return String.valueOf(tagObject);
	}

	@SuppressLint("NewApi")
	private void hideView(View v) {
		if (Build.VERSION.SDK_INT >= 11) {
			v.setAlpha(0);
		} else {
			AlphaAnimation anim = new AlphaAnimation(1, 0);
			anim.setDuration(0);
			anim.setFillAfter(true);
			v.startAnimation(anim);
		}
	}

	@SuppressLint("NewApi")
	private void showView(View v) {
		if (Build.VERSION.SDK_INT >= 11) {
			v.setAlpha(1);
		} else {
			AlphaAnimation anim = new AlphaAnimation(0, 1);
			anim.setDuration(0);
			anim.setFillAfter(true);
			v.startAnimation(anim);
		}
	}

	public final static double NO_ZOOM = 1;
	private ArrayList<OnOverScrollByListener> mOnOverScrollByList = new ArrayList<OnOverScrollByListener>();
	private ArrayList<OnTouchEventListener> mOnTouchEventList = new ArrayList<OnTouchEventListener>();
	private ImageView mImageView;
	private int mDrawableMaxHeight = -1;
	private int mImageViewHeight = -1;
	
	private OnOverScrollByListener onScroll = new OnOverScrollByListener() {

		@Override
		public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			if (mImageView.getHeight() <= mDrawableMaxHeight && isTouchEvent) {

				if (deltaY < 0) {

					if (mImageView.getHeight() - deltaY / 2 >= mImageViewHeight) {

						mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY / 2 < mDrawableMaxHeight ? mImageView.getHeight() - deltaY / 2 : mDrawableMaxHeight;

						mImageView.requestLayout();

					}

				} else {

					if (mImageView.getHeight() > mImageViewHeight) {

						mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY > mImageViewHeight ? mImageView.getHeight() - deltaY : mImageViewHeight;

						mImageView.requestLayout();

						return true;

					}
				}
			}

			return false;
		}
	};

	private OnTouchEventListener onTouched = new OnTouchEventListener() {

		@Override
		public void onTouchEvent(MotionEvent ev) {

			if (ev.getAction() == MotionEvent.ACTION_UP) {

				if (mImageViewHeight - 1 < mImageView.getHeight()) {
					BackAnimimation animation = new BackAnimimation(mImageView, mImageViewHeight, false);
					animation.setDuration(300);
					mImageView.startAnimation(animation);
				}
			}
		}
	};
	private double mZoomRatio = 1;

	private void init(Context context, AttributeSet attrs) {

		if (attrs != null) {

			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ParallaxScrollView, 0, 0);

			mZoomRatio = a.getFloat(R.styleable.ParallaxScrollView_zoomRatio, 1);
		}

		post(new Runnable() {

			@Override
			public void run() {

				setViewsBounds(mZoomRatio);
			}
		});
	}

	/**
	 * Set the ImageView that will be used in the parallax changing his
	 * {@link ImageView.ScaleType} to CENTER_CROP.
	 * 
	 * @param view
	 *            - An {@link ImageView} that will have the parallax effect.
	 * 
	 */

	public void setImageViewToParallax(ImageView imageView) {

		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		mImageView = imageView;

		addOnScrolledListener(onScroll);
		addOnTouchListener(onTouched);

	}

	private void addOnScrolledListener(OnOverScrollByListener onScrolled) {

		mOnOverScrollByList.add(onScrolled);
	}

	private void addOnTouchListener(OnTouchEventListener onTouched) {

		mOnTouchEventList.add(onTouched);
	}

	/**
	 * Set the bounds of the views and set the zoom of the view.
	 * <p>
	 * Necessary to get the size of the Views.
	 * <p>
	 * Have to put in the {@link #onWindowFocusChanged(boolean)} of the
	 * activity.
	 * 
	 * @param zoomRatio
	 *            Double - How many times is the max zoom of the image, minimum
	 *            1.
	 * 
	 */

	public void setViewsBounds(double zoomRatio) {

		if (mImageViewHeight == -1) {

			mImageViewHeight = mImageView.getHeight();

			double imageRatio = ((double) mImageView.getDrawable().getIntrinsicWidth()) / ((double) mImageView.getWidth());

			mDrawableMaxHeight = (int) ((mImageView.getDrawable().getIntrinsicHeight() / imageRatio) * (zoomRatio > 1 ? zoomRatio : 1));
		}
	}
}