package net.smart_eapp.catalog;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.smart_eapp.ActionItem;
import net.smart_eapp.BookStarter;
import net.smart_eapp.QuickAction;
import net.smart_eapp.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PageActivity extends FragmentActivity {
	int mPosition = 0;
	ViewPager mDragable;
	ImageView mLeftAllow;
	ImageView mRightAllow;
	QuickAction mActionBar;
	View mActionMenu;
	boolean mIsVisibleAllow = true;
	boolean mIsScroll = false;
	int lastX;
	int lastY;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.catalog_page_activity);

		mPosition = getIntent().getExtras().getInt(BookStarter.INTENT_PAGE);
		mDragable = (ViewPager) findViewById(R.id_catalog_page_fragment.dragable);

		CatalogAdapter adapter = new CatalogAdapter(this);
		mDragable.setAdapter(adapter);
		mDragable.setCurrentItem(mPosition);
		mLeftAllow = (ImageView) findViewById(R.id_page_view.left_allow);
		mLeftAllow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isPending = false;
				try {
					Field field = mDragable.getClass().getDeclaredField("mPopulatePending");
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
		mRightAllow = (ImageView) findViewById(R.id_page_view.right_allow);
		mRightAllow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isPending = false;
				try {
					Field field = mDragable.getClass().getDeclaredField("mPopulatePending");
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

		mDragable.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mPosition = position;
				showAllow();
				// ２ページ前、後を優先的に削除する
				for (int i = 0; i < mDragable.getChildCount(); i++) {
					View v = mDragable.getChildAt(i);
					int viewPosition = (Integer) v.getTag();
					ImageView image = (ImageView) v.findViewById(R.id_catalog_page_adapter.image);
					if (viewPosition <= mPosition - 2 || viewPosition >= mPosition + 2) {
						destroyView(v);
					} else {
						if (image.getDrawable() == null) {
							View view = createView(v, position);
							mDragable.removeViewAt(i);
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
		showAllow();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mActionBar != null) {
			mActionBar.close();
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

	public void showAllow() {
		if (mIsVisibleAllow) {
			if (mPosition > 0) {
				mLeftAllow.setVisibility(View.VISIBLE);
			} else {
				mLeftAllow.setVisibility(View.GONE);
			}
			if (mPosition < BookStarter.getBook().getPageCount() - 1) {
				mRightAllow.setVisibility(View.VISIBLE);
			} else {
				mRightAllow.setVisibility(View.GONE);
			}
		} else {
			mLeftAllow.setVisibility(View.GONE);
			mRightAllow.setVisibility(View.GONE);
		}
	}

	class CatalogAdapter extends PagerAdapter {
		public CatalogAdapter(Context context) {

		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			destroyView((View) view);
			((ViewPager) collection).removeView((RelativeLayout) view);
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return BookStarter.getBook().getPageCount();
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			View view = createView(collection, position);
			((ViewPager) collection).addView(view);
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View view) {

		}
	}

	public View createView(View collection, int position) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.catalog_page_adapter, null);
		WebView web = (WebView) view.findViewById(R.id_catalog_page_adapter.web);
		web.setFocusable(false);
		TextView text = (TextView) view.findViewById(R.id_page_adapter_view.text);
		ImageView image = (ImageView) view.findViewById(R.id_catalog_page_adapter.image);
		mActionMenu = view.findViewById(R.id_page_adapter_view.plus_image);

		try {
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display disp = wm.getDefaultDisplay();
			int width = disp.getWidth();

			InputStream is = BookStarter.getBook().getPageImage(position + 1);
			Bitmap bm = BitmapFactory.decodeStream(is);
			float scale = ((float) width / (float) bm.getWidth());

			if (scale > 1) {
				scale = 1;
			}
			Bitmap resultBitmap = Bitmap.createBitmap((int) (bm.getWidth() * scale), (int) (bm.getHeight() * scale),
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(resultBitmap);
			BitmapDrawable drawable = new BitmapDrawable(bm);
			drawable.setBounds(0, 0, (int) (bm.getWidth() * scale), (int) (bm.getHeight() * scale));
			drawable.draw(canvas);
			bm.recycle();

			image.setImageBitmap(resultBitmap);
			text.setText(BookStarter.getBook().getPageTitle(position + 1));
			is = getResources().openRawResource(R.raw.template);
			byte[] readBytes = new byte[is.available()];
			is.read(readBytes);
			String readString = new String(readBytes);
			String _webString = String.format(readString, BookStarter.getBook().getPageSentences(position + 1));

			web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			web.loadData(_webString, "text/html", "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
		}

		mActionMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openActionBar();
			}
		});

		image.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mIsScroll = false;
					lastX = x;
					lastY = y;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (Math.abs(lastX - x) > 10 || Math.abs(lastY - y) > 10) {
						mIsScroll = true;
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!mIsScroll) {
						// スクロールしていなければ左右の矢印の表示非表示を切り替える
						mIsVisibleAllow = !mIsVisibleAllow;
						showAllow();
					}
					mIsScroll = false;
				}
				return true;
			}
		});
		view.setTag(position);
		return view;
	}

	public void destroyView(View view) {
		ImageView image = (ImageView) ((RelativeLayout) view).findViewById(R.id_catalog_page_adapter.image);
		Drawable d = image.getDrawable();
		if (d instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			bitmap.recycle();
		}
		image.setImageDrawable(null);
	}

	public void snapToScreen(int position) {
		try {
			Field field = mDragable.getClass().getDeclaredField("mPopulatePending");
			field.setAccessible(true);
			field.setBoolean(mDragable, true);
			Method method = mDragable.getClass().getDeclaredMethod("setCurrentItemInternal",
					new Class[] { int.class, boolean.class, boolean.class });
			method.setAccessible(true);
			method.invoke(mDragable, new Object[] { position, true, true });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openActionBar() {
		ActionItem item1 = new ActionItem();
		item1.setTitle(getString(R.string.catalog_zoom));
		item1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PageActivity.this, UpImageView.class);
				intent.putExtra(BookStarter.INTENT_PAGE, mPosition);
				startActivity(intent);
				mActionBar.close();
			}
		});
		mActionBar = new QuickAction(mActionMenu);
		mActionBar.addActionItem(item1);
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