package net.smart_eapp.catalog;

import net.smart_eapp.BookStarter;
import net.smart_eapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class PageListActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.catalog_page_list_activity);

		TextView titleText = (TextView) findViewById(R.id_catalog_page_list_fragment.title_bar);
		titleText.setText(BookStarter.getBook().getBookTitle());
		final PageListAdapterView adapter = new PageListAdapterView(this);
		ListView listView = (ListView) findViewById(R.id_catalog_page_list_fragment.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(PageListActivity.this, PageActivity.class);
				intent.putExtra(BookStarter.INTENT_PAGE, position);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}