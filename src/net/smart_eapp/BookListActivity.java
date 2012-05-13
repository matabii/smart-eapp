package net.smart_eapp;

import net.smart_eapp.R;
import net.smart_eapp.Util.BookFile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class BookListActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list_activity);

		TextView titleText = (TextView) findViewById(R.id_book_list_view.title_bar);
		titleText.setText(R.string.app_name);
		final BookListAdapterView adapter = new BookListAdapterView(this);
		GridView gridView = (GridView) findViewById(R.id_book_list_view.list);
		gridView.setAdapter(adapter);
		gridView.setSelector(R.drawable.selector);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BookFile selectFile = (BookFile) adapter.getItem(position);
				BookStarter.start(BookListActivity.this, selectFile.fileName);
			}
		});
		View bottom = findViewById(R.id_book_list_view.bottom);
		bottom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("http://smart-eapp.net");
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
			}
		});

	}
}