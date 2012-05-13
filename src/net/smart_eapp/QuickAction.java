package net.smart_eapp;

import android.content.Context;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;

import java.util.ArrayList;

import net.smart_eapp.R;

public class QuickAction {
	private View mRootView;
	private final View mView;
	private final ImageView mArrowUp;
	private final LayoutInflater inflater;
	private final Context context;
	private final PopupWindow window;
	private final WindowManager windowManager;
	private Drawable background = null;
	private ViewGroup mTrack;
	private ScrollView scroller;
	private ArrayList<ActionItem> actionList;

	public QuickAction(View view) {
		this.mView = view;
		this.window = new PopupWindow(mView.getContext());
		window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					window.dismiss();

					return true;
				}
				return false;
			}
		});

		windowManager = (WindowManager) mView.getContext().getSystemService(Context.WINDOW_SERVICE);
		actionList = new ArrayList<ActionItem>();
		context = mView.getContext();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = (ViewGroup) inflater.inflate(R.layout.popup, null);
		mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
		setContentView(mRootView);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
		scroller = (ScrollView) mRootView.findViewById(R.id.scroller);
	}

	public void setContentView(View root) {
		this.mRootView = root;

		window.setContentView(root);
	}

	public void addActionItem(ActionItem action) {
		actionList.add(action);
	}

	protected void preShow() {
		if (mRootView == null) {
			throw new IllegalStateException("setContentView was not called with a view to display.");
		}

		if (background == null) {
			window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			window.setBackgroundDrawable(background);
		}
		window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setTouchable(true);
		window.setFocusable(true);
		window.setOutsideTouchable(true);
		window.setContentView(mRootView);
	}

	public void close() {
		if (window != null && window.isShowing()) {
			window.dismiss();
		}
	}

	public void show() {
		preShow();
		int xPos, yPos;
		int[] location = new int[2];
		mView.getLocationOnScreen(location);
		Rect anchorRect = new Rect(location[0], location[1], location[0] + mView.getWidth(), location[1]
				+ mView.getHeight());
		createActionList();
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int rootHeight = mRootView.getMeasuredHeight();
		int rootWidth = mRootView.getMeasuredWidth();
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - mView.getWidth());
		} else {
			if (mView.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left;
			}
		}

		int dyBottom = screenHeight - anchorRect.bottom;
		yPos = anchorRect.bottom;
		if (rootHeight > dyBottom) {
			LayoutParams l = scroller.getLayoutParams();
			l.height = dyBottom;
		}

		showArrow(anchorRect.centerX() - xPos);
		setAnimationStyle(screenWidth, anchorRect.centerX());
		window.showAtLocation(mView, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void setAnimationStyle(int screenWidth, int requestedX) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;
		if (arrowPos <= screenWidth / 4) {
			window.setAnimationStyle(R.style.Animations_PopUpMenu_Left);
		} else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
			window.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
		} else {
			window.setAnimationStyle(R.style.Animations_PopUpMenu_Right);
		}
	}

	private void createActionList() {
		View view;
		String title;
		Drawable icon;
		OnClickListener listener;

		for (int i = 0; i < actionList.size(); i++) {
			title = actionList.get(i).getTitle();
			icon = actionList.get(i).getIcon();
			listener = actionList.get(i).getListener();
			view = getActionItem(title, icon, listener);
			view.setFocusable(true);
			view.setClickable(true);
			mTrack.addView(view);
		}
	}

	private View getActionItem(String title, Drawable icon, OnClickListener listener) {
		LinearLayout container = (LinearLayout) inflater.inflate(R.layout.action_item, null);

		ImageView img = (ImageView) container.findViewById(R.id.icon);
		TextView text = (TextView) container.findViewById(R.id.title);

		if (icon != null) {
			img.setImageDrawable(icon);
		}

		if (title != null) {
			text.setText(title);
		}

		if (listener != null) {
			container.setOnClickListener(listener);
		}

		return container;
	}

	private void showArrow(int requestedX) {
		final View showArrow = mArrowUp;
		final int arrowWidth = mArrowUp.getMeasuredWidth();
		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();
		param.leftMargin = requestedX - arrowWidth / 2;
	}
}