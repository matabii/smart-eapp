package net.smart_eapp;

import java.util.ArrayList;

import net.smart_eapp.Util.BookFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class LaunchActivity extends Activity {
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch_activity);
		this.mContext = this;
		Handler handler = new Handler();
		handler.postDelayed(new StartHandler(), 500);
	}

	class StartHandler implements Runnable {

		@Override
		public void run() {
			Util.copyFile(mContext);
			ArrayList<BookFile> books = Util.getBooks(mContext);
			if (books.size() <= 0) {
				Toast.makeText(mContext, "not found book file.", Toast.LENGTH_LONG).show();
			} else if (books.size() == 1) {
				BookStarter.start(mContext, books.get(0));
				finish();
			} else {
				Intent intent = new Intent(mContext, BookListActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.splash_open, R.anim.splash_close);
			}
		}
	}
}
