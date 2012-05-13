package net.smart_eapp.photo;

import net.smart_eapp.BookStarter;
import net.smart_eapp.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class PageListActivity extends Activity {
	private GridView mGridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_page_list_activity);

		TextView titleText = (TextView) findViewById(R.id_photo_page_list_fragment.title_bar);
		titleText.setText(BookStarter.getBook().getBookTitle());
		final PageListAdapterView adapter = new PageListAdapterView(this);
		mGridView = (GridView) findViewById(R.id_photo_page_list_fragment.grid);
		mGridView.setAdapter(adapter);
		mGridView.setSelector(R.drawable.selector);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(PageListActivity.this, PageActivity.class);
				intent.putExtra(BookStarter.INTENT_PAGE, position);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		setGridViewColumns();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setGridViewColumns();
	}

	private void setGridViewColumns() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int numColumns = (int) (metrics.widthPixels / metrics.density) / 100;
		mGridView.setNumColumns(numColumns);
	}
}