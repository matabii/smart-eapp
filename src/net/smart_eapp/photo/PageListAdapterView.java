package net.smart_eapp.photo;

import java.io.InputStream;

import net.smart_eapp.BookStarter;
import net.smart_eapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PageListAdapterView extends BaseAdapter {
	private final LayoutInflater mInflater;

	public PageListAdapterView(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return BookStarter.getBook().getPageCount();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.photo_page_list_adapter, parent, false);
		} else {
			v = convertView;
		}
		ImageView image = (ImageView) v.findViewById(R.id_photo_page_list_adapter.image);

		try {
			InputStream is2 = BookStarter.getBook().getPageThumb(position + 1);
			Bitmap bm = BitmapFactory.decodeStream(is2);
			image.setImageBitmap(bm);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return (v);
	}
}