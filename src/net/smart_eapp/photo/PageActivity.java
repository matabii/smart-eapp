package net.smart_eapp.photo;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.smart_eapp.ActionItem;
import net.smart_eapp.BookStarter;
import net.smart_eapp.QuickAction;
import net.smart_eapp.R;
import net.smart_eapp.ScaleImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PageActivity extends Activity {
	private final static int REQUEST_SHOW_INDEX = 10;
	int mPosition = 0;
	int mCount;
	PhotoViewPager mDragable;
	RelativeLayout mTopMenu;
	View mActionMenu;
	QuickAction mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_page_activity);

		mActionMenu = findViewById(R.id_photo_page_fragment.title);
		mDragable = (PhotoViewPager) findViewById(R.id_photo_page_fragment.dragable);
		mPosition = getIntent().getExtras().getInt(BookStarter.INTENT_PAGE);
		mCount = BookStarter.getBook().getPageCount();

		PhotoAdapter adapter = new PhotoAdapter(this);
		mDragable.setAdapter(adapter);
		mDragable.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mPosition = position;
				settingMenu(position);
				// ２ページ前、後を優先的に削除する
				for (int i = 0; i < mDragable.getChildCount(); i++) {
					View v = mDragable.getChildAt(i);
					int viewPosition = (Integer) v.getTag();
					ScaleImageView image = (ScaleImageView) v.findViewById(R.id_photo_page_adapter.image);
					if (viewPosition <= mPosition - 2 || viewPosition >= mPosition + 2) {
						destroyView(v);
					} else {
						if (image.getDrawable() == null) {
							mDragable.removeViewAt(i);
							View view = createView(v, position);
							mDragable.addView(view, i);
						}
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		mDragable.setCurrentItem(mPosition);
		settingMenu(mPosition);
		mDragable.getAdapter().notifyDataSetChanged();
		mTopMenu = (RelativeLayout) findViewById(R.id_photo_page_fragment.top);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mActionBar != null) {
			mActionBar.close();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_SHOW_INDEX:
			if (resultCode == RESULT_OK) {
				int page = data.getIntExtra(BookStarter.INTENT_PAGE, 0);
				moveToScreen(page);
			}
		}
	}

	@Override
	protected void onDestroy() {
		for (int i = 0; i < mDragable.getChildCount(); i++) {
			View v = mDragable.getChildAt(i);
			destroyView(v);
		}
		super.onDestroy();
	}

	class PhotoAdapter extends PagerAdapter {

		public PhotoAdapter(Context context) {

		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			destroyView((View) view);
			((PhotoViewPager) collection).removeView((FrameLayout) view);
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			View v = createView(collection, position);
			((PhotoViewPager) collection).addView(v);
			return v;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((FrameLayout) arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

	public void settingMenu(int position) {
		TextView title = (TextView) findViewById(R.id_photo_page_fragment.title);
		title.setText(String.valueOf(position + 1) + "/" + String.valueOf(mCount) + "▼");
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openActionBar();
			}
		});
		ImageView left = (ImageView) findViewById(R.id_photo_page_fragment.left_image);
		if (position == 0) {
			left.setImageResource(R.drawable.left_disable);
		} else {
			left.setImageResource(R.drawable.left_selector);
			left.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isPending = false;
					try {
						Field field = mDragable.getClass().getSuperclass().getDeclaredField("mPopulatePending");
						field.setAccessible(true);
						isPending = field.getBoolean(mDragable);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!isPending) {
						snapToScreen(mPosition - 1);
					}
				}
			});
		}
		ImageView right = (ImageView) findViewById(R.id_photo_page_fragment.right_image);
		if (position >= mCount - 1) {
			right.setImageResource(R.drawable.right_disable);
		} else {
			right.setImageResource(R.drawable.right_selector);
			right.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isPending = false;
					try {
						Field field = mDragable.getClass().getSuperclass().getDeclaredField("mPopulatePending");
						field.setAccessible(true);
						isPending = field.getBoolean(mDragable);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!isPending) {
						snapToScreen(mPosition + 1);
					}
				}
			});
		}
	}

	public View createView(View collection, int position) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.photo_page_adapter, null);
		final ScaleImageView image = (ScaleImageView) view.findViewById(R.id_photo_page_adapter.image);
		image.setBackgroundColor(Color.parseColor(BookStarter.getBook().getPageBackground()));
		image.addSingleTapConfirmedListener(new ScaleImageView.SingleTapEvent() {
			@Override
			public void doEvent() {
				if (mTopMenu.getVisibility() == View.VISIBLE) {
					mTopMenu.setVisibility(View.GONE);
				} else {
					mTopMenu.setVisibility(View.VISIBLE);
				}
			}
		});

		image.addEdgeEventListener(new ScaleImageView.EdgeEvent() {
			@Override
			public void onRelease() {
				mDragable.setLeft(false);
			}

			@Override
			public void onEdge() {
				mDragable.setLeft(true);
			}
		}, new ScaleImageView.EdgeEvent() {
			@Override
			public void onRelease() {
				mDragable.setRight(false);
			}

			@Override
			public void onEdge() {
				mDragable.setRight(true);
			}
		});

		try {

			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display disp = wm.getDefaultDisplay();
			int width = disp.getWidth();

			InputStream is = BookStarter.getBook().getPageImage(position + 1);
			Bitmap bm = BitmapFactory.decodeStream(is);
			float scale = ((float) width / (float) bm.getWidth());

			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);

			Bitmap resultBitmap = Bitmap.createBitmap((int) (bm.getWidth() * scale), (int) (bm.getHeight() * scale),
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(resultBitmap);
			BitmapDrawable drawable = new BitmapDrawable(bm);
			drawable.setBounds(0, 0, (int) (bm.getWidth() * scale), (int) (bm.getHeight() * scale));
			drawable.draw(canvas);
			bm.recycle();
			image.setImageBitmap(resultBitmap);

		} catch (Exception e) {
			e.printStackTrace();
		}

		view.setTag(position);
		return view;
	}

	public void destroyView(View view) {
		ScaleImageView image = (ScaleImageView) ((FrameLayout) view).findViewById(R.id_photo_page_adapter.image);
		Drawable d = image.getDrawable();
		if (d instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			bitmap.recycle();
		}
		image.setImageDrawable(null);
	}

	public void moveToScreen(int position) {
		try {
			Field field = mDragable.getClass().getSuperclass().getDeclaredField("mPopulatePending");
			field.setAccessible(true);
			field.setBoolean(mDragable, true);
			Method method = mDragable
					.getClass()
					.getSuperclass()
					.getDeclaredMethod("setCurrentItemInternal",
							new Class[] { int.class, boolean.class, boolean.class });
			method.setAccessible(true);
			method.invoke(mDragable, new Object[] { position, false, true });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void snapToScreen(int position) {
		try {
			Field field = mDragable.getClass().getSuperclass().getDeclaredField("mPopulatePending");
			field.setAccessible(true);
			field.setBoolean(mDragable, true);
			Method method = mDragable
					.getClass()
					.getSuperclass()
					.getDeclaredMethod("setCurrentItemInternal",
							new Class[] { int.class, boolean.class, boolean.class });
			method.setAccessible(true);
			method.invoke(mDragable, new Object[] { position, true, true });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openActionBar() {
		if (mTopMenu.getVisibility() == View.GONE) {
			mTopMenu.setVisibility(View.VISIBLE);
		}

		mActionBar = new QuickAction(mActionMenu);
		ActionItem item1 = new ActionItem();
		item1.setTitle(getString(R.string.photo_show_index));
		item1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PageActivity.this, PageListActivity.class);
				startActivityForResult(intent, REQUEST_SHOW_INDEX);
				mActionBar.close();
			}
		});
		ActionItem item2 = new ActionItem();
		item2.setTitle(getString(R.string.photo_move_top));
		item2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				moveToScreen(0);
				mActionBar.close();
			}
		});
		mActionBar.addActionItem(item1);
		mActionBar.addActionItem(item2);
		mActionBar.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			openActionBar();
		}
		return super.onKeyDown(keyCode, event);
	}
}