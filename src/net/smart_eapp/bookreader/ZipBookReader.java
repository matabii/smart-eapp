package net.smart_eapp.bookreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.smart_eapp.BookFile;
import android.content.Context;

public class ZipBookReader extends BookReader {

	public ZipFile mBookZipFile;

	public void init(Context context, BookFile book) {
		try {
			mBookZipFile = new ZipFile(new File(BookFile.getBookDir(context), book.fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.init(context, book);
	}

	public ZipBookReader(Context context, BookFile book) {
		super(context, book);
	}

	@Override
	public InputStream getTopFileStream() {
		ZipEntry topFileEntry = mBookZipFile.getEntry("top");
		if( topFileEntry == null ) {
			return null;
		}
		InputStream is = null;
		try {
			is = mBookZipFile.getInputStream(topFileEntry);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

	@Override
	Properties loadProperties() {
		ZipEntry entry = this.mBookZipFile.getEntry("setting.xml");
		Properties properties = new Properties();
		InputStream is;
		try {
			is = mBookZipFile.getInputStream(entry);
			properties.loadFromXML(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	@Override
	public InputStream getPageImage(int page) {
		ZipEntry entry = mBookZipFile.getEntry(page + "/image");
		InputStream is = null;
		try {
			is = mBookZipFile.getInputStream(entry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	@Override
	public InputStream getPageThumb(int page) {
		ZipEntry entry = mBookZipFile.getEntry(page + "/thumb");
		InputStream is = null;
		try {
			is = mBookZipFile.getInputStream(entry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	@Override
	public String getPageTitle(int page) {
		InputStream is;
		try {
			is = mBookZipFile.getInputStream(mBookZipFile.getEntry((page) + "/title"));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			return br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getPageSentences(int page) {
		InputStream is;
		String sentence = "";
		try {
			is = mBookZipFile.getInputStream(mBookZipFile.getEntry((page) + "/sentence"));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				sentence += line;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sentence;

	}

	@Override
	public InputStream getBookIcon() {
		ZipEntry entry = mBookZipFile.getEntry("icon");
		InputStream is = null;
		try {
			is = mBookZipFile.getInputStream(entry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}
}
