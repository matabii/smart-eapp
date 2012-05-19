package net.smart_eapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import android.content.Context;
import android.os.Environment;

public class BookFile {
	public String fileId;
	public String category1;
	public String category2;
	public String date;
	public String fileName;
	public File realFile;

	static public File getBookDir(Context context) {
		File rootDir = Environment.getExternalStorageDirectory();
		File bookDir = new File(rootDir, "/Android/data/" + context.getPackageName() + "/files/");
		if (!bookDir.exists()) {
			bookDir.mkdirs();
		}
		return bookDir;
	}

	static public BookFile getBookFile(Context context, String fileId) {
		ArrayList<BookFile> books = getBookFiles(context);
		Iterator<BookFile> it = books.iterator();
		while (it.hasNext()) {
			BookFile book = it.next();
			if (book.fileId.equals(fileId)) {
				return book;
			}
		}
		return null;
	}

	static public ArrayList<BookFile> getBookFiles(Context context) {
		ArrayList<BookFile> array = new ArrayList<BookFile>();
		File sdDir = getBookDir(context);
		String[] files = sdDir.list();
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(".") || files[i].equals("..")) {
				continue;
			}
			BookFile file = new BookFile();
			String fileNames[] = files[i].split("_");
			file.fileId = fileNames[0];
			file.category1 = fileNames[1];
			file.category2 = fileNames[2];
			file.date = fileNames[3];
			file.fileName = files[i];
			file.realFile = new File(getBookDir(context), files[i]);
			array.add(file);
		}
		return array;
	}

	static private void saveBookFile(Context context, InputStream is, String fileId, String fileName) {
		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			BookFile existBookFile = getBookFile(context, fileId);
			if (existBookFile != null) {
				existBookFile.realFile.delete();
			}
			File newBookFile = new File(getBookDir(context), fileName);
			FileOutputStream fos = new FileOutputStream(newBookFile);
			byte[] buf = new byte[1024];
			while ((bis.read(buf)) != -1) {
				fos.write(buf);
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static private void deleteBookFile(Context context, String fileId) {
		BookFile deleteFile = getBookFile(context, fileId);
		File file = new File(getBookDir(context), deleteFile.fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * assets/books から sdカードへzipファイルをコピーする
	 * ファイル名はid_yyyymmddhhmmssとなっていて、sdカードよりも新しい場合のみコピーする
	 * 
	 * @param context
	 */
	static public void copyBookFile(Context context) {
		try {
			ArrayList<BookFile> bookList = getBookFiles(context);
			String[] files = context.getAssets().list("books");
			for (int i = 0; i < files.length; i++) {
				// id_yyyymmddhhmmss
				String zipNames[] = files[i].split("_");
				Iterator<BookFile> it = bookList.iterator();
				boolean isMatch = false;
				while (it.hasNext()) {
					BookFile book = it.next();
					if (book.fileId.equals(zipNames[0])) {
						isMatch = true;
						if (book.date.compareTo(zipNames[3]) < 0) {
							// 上書きする
							deleteBookFile(context, book.fileId);
							InputStream is = context.getAssets().open("books/" + files[i]);
							saveBookFile(context, is, zipNames[0], files[i]);
						}
						break;
					}
				}
				if (isMatch == false) {
					InputStream is = context.getAssets().open("books/" + files[i]);
					saveBookFile(context, is, zipNames[0], files[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
