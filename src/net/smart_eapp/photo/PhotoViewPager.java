package net.smart_eapp.photo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PhotoViewPager extends ViewPager {
	private boolean mIsLeft;
	private boolean mIsRight;
	private boolean isPinch;
	private int lastX;

	public boolean isLeft() {
		return mIsLeft;
	}

	public void setLeft(boolean mIsLeft) {
		this.mIsLeft = mIsLeft;
	}

	public boolean isRight() {
		return mIsRight;
	}

	public void setRight(boolean mIsRight) {
		this.mIsRight = mIsRight;
	}

	public PhotoViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int touchCount = e.getPointerCount();
		switch (e.getAction()) {
		case MotionEvent.ACTION_UP:
			isPinch = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (isPinch) {
				// ピンチインアウト作業中はflipさせない
				return false;
			}
			if (e.getX() > lastX) {
				// 右へのフリックで左へ移動しようとしている場合
				if (!mIsLeft) {
					return false;
				}
			}
			if (e.getX() < lastX) {
				// 左へのフリックで右へ移動しようとしている場合
				if (!mIsRight) {
					return false;
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_1_DOWN:
		case MotionEvent.ACTION_POINTER_2_DOWN:
			if (touchCount >= 2) {
				isPinch = true;
			} else {
				isPinch = false;
			}
			lastX = (int) e.getX();
			break;
		}
		return super.onInterceptTouchEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onTouchEvent(e);
	}
}
