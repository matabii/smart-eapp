package net.smart_eapp.bookreader;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.os.Environment;

public abstract class BookReader {
	public Properties mProperties;
	public String name;
	private Context mContext;

	public BookReader(Context context, String name) {
		this.mContext = context;
		this.name = name;
		this.init(context, name);
	}

	public void init(Context context, String name) {
		this.mContext = context;
		this.name = name;
		this.mProperties = this.loadProperties();
	}

	public int getPageCount() {
		return Integer.parseInt(mProperties.getProperty("page_count"));
	}

	public String getBookTitle() {
		return mProperties.getProperty("book_title");
	}

	public String getPageBackground() {
		return mProperties.getProperty("page_background");
	}

	public String getTemplate() {
		return mProperties.getProperty("template");
	}

	public File getBookDir() {
		File rootDir = Environment.getExternalStorageDirectory();
		File bookDir = new File(rootDir, "/Android/data/" + mContext.getPackageName() + "/files/");
		if (!bookDir.exists()) {
			bookDir.mkdirs();
		}
		return bookDir;
	}

	abstract Properties loadProperties();

	abstract public InputStream getTopFileStream();

	abstract public InputStream getBookIcon();

	abstract public InputStream getPageImage(int page);

	abstract public InputStream getPageThumb(int page);

	abstract public String getPageTitle(int page);

	abstract public String getPageSentences(int page);
}
