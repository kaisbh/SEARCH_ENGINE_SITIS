package isped.sitis.se.util;

import java.io.File;
import java.util.ArrayList;

public class FileUtil {
	private ArrayList<File> queue = new ArrayList<File>();

	public ArrayList<File> addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			// ===================================================
			// Only index text files
			// ===================================================
			if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml")
					|| filename.endsWith(".txt")) {
				queue.add(file);
				// System.out.println("Added " + filename);
			} else {
				// System.out.println("Skipped " + filename);
			}
		}
		return queue;
	}

	static public void createDir(String dir) {

	}

	static public void deleteFiles(String emplacement) {
		File path = new File(emplacement);
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteFiles(path + "\\" + files[i]);
				}
				files[i].delete();
			}
		}
	}
}
