package net.smart_eapp.catalog;

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
import android.widget.TextView;

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
			v = mInflater.inflate(R.layout.catalog_page_list_adapter, parent, false);
		} else {
			v = convertView;
		}
		TextView text = (TextView) v.findViewById(R.id_page_list_adapter_view.text);
		ImageView image = (ImageView) v.findViewById(R.id_page_list_adapter_view.image);

		text.setText(BookStarter.getBook().getPageTitle(position + 1));
		Bitmap bm = BitmapFactory.decodeStream(BookStarter.getBook().getPageThumb(position + 1));
		image.setImageBitmap(bm);

		return (v);
	}
}