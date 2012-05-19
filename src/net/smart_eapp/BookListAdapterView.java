package net.smart_eapp;

import java.util.ArrayList;

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

import net.smart_eapp.bookreader.BookReader;
import net.smart_eapp.bookreader.ZipBookReader;

public class BookListAdapterView extends BaseAdapter {
	private final Context mContext;
	private final LayoutInflater mInflater;
	private ArrayList<BookFile> books;

	public BookListAdapterView(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		books = BookFile.getBookFiles(mContext);
	}

	@Override
	public int getCount() {
		return books.size();
	}

	@Override
	public Object getItem(int position) {
		return books.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.book_list_adapter_view, parent, false);
			TextView text = (TextView) v.findViewById(R.id_book_list_adapter_view.text);
			ImageView image = (ImageView) v.findViewById(R.id_book_list_adapter_view.image);
			try {
				BookReader book = new ZipBookReader(mContext, ((BookFile) (getItem(position))));
				text.setText(book.getBookTitle());
				Bitmap bm = BitmapFactory.decodeStream(book.getBookIcon());
				image.setImageBitmap(bm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			v = convertView;
		}
		return (v);
	}
}