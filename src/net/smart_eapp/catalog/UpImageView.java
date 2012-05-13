package net.smart_eapp.catalog;

import java.io.InputStream;

import net.smart_eapp.BookStarter;
import net.smart_eapp.R;
import net.smart_eapp.ScaleImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class UpImageView extends Activity {
	int mPosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.up_image_view);
		mPosition = getIntent().getExtras().getInt(BookStarter.INTENT_PAGE);
		try {
			InputStream is = BookStarter.getBook().getPageImage(mPosition + 1);
			ScaleImageView image = (ScaleImageView) findViewById(R.id_up_image_view.image);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			image.setImageBitmap(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}