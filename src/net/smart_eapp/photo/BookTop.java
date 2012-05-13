package net.smart_eapp.photo;

import java.io.InputStream;

import net.smart_eapp.BookStarter;
import net.smart_eapp.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BookTop extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_top);

		TextView titleText = (TextView) findViewById(R.id_book_top.title_bar);
		titleText.setText(BookStarter.getBook().getBookTitle());

		ImageView topImage = (ImageView) findViewById(R.id_book_top.top);

		InputStream is;
		try {
			is = BookStarter.getBook().getTopFileStream();
			Bitmap bm = BitmapFactory.decodeStream(is);
			topImage.setImageBitmap(bm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		topImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = null;
				intent = new Intent(BookTop.this, PageActivity.class);
				intent.putExtra(BookStarter.INTENT_PAGE, 0);
				startActivity(intent);
			}
		});
	}
}
