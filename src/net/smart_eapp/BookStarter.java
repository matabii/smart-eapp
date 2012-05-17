package net.smart_eapp;

import net.smart_eapp.bookreader.BookReader;
import net.smart_eapp.bookreader.ZipBookReader;
import net.smart_eapp.catalog.BookTop;
import net.smart_eapp.catalog.PageListActivity;
import net.smart_eapp.photo.PageActivity;

import android.content.Context;
import android.content.Intent;

/**
 * ブックを開く際に適切なIntentを発行するための処理を提供する
 */
public class BookStarter {
	public static final String TEMPLATE_PHOTO = "photo";
	public static final String TEMPLATE_CATALOG = "catalog";

	public static final String INTENT_PAGE = "intent_page";

	// singleton
	private static BookReader bookReader;

	public static BookReader getBook() {
		return bookReader;
	}

	public static void start(Context context, BookFile book) {
		bookReader = new ZipBookReader(context, book);
		try {
			Intent intent = null;
			if (bookReader.getTemplate().equals(TEMPLATE_PHOTO)) {
				// topfile無しの場合
				if (bookReader.getTopFileStream() == null) {
					intent = new Intent(context, PageActivity.class);
				} else {
					intent = new Intent(context, net.smart_eapp.photo.BookTop.class);
				}
				intent.putExtra(INTENT_PAGE, 0);
			} else if (bookReader.getTemplate().equals(TEMPLATE_CATALOG)) {
				// topfile無しの場合
				if (bookReader.getTopFileStream() == null) {
					intent = new Intent(context, PageListActivity.class);
				} else {
					intent = new Intent(context, BookTop.class);
				}
			} else {
				throw new Exception("not found template " + bookReader.getTemplate());
			}
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
