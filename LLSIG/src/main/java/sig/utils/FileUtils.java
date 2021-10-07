package main.java.sig.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileUtils {
	public static String[] readFromFile(String filename) throws IOException {
		File file = new File(filename);
		//System.out.println(file.getAbsolutePath());
		List<String> contents= new ArrayList<String>();
		if (file.exists()) {
					FileInputStream in = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(in,Charset.forName("UTF-8"));
					BufferedReader br = new BufferedReader(isr);
					String readline = br.readLine();
					do {
						if (readline!=null) {
							//System.out.println(readline);
							contents.add(readline);
							readline = br.readLine();
						}} while (readline!=null);
					br.close();
					isr.close();
					in.close();
		}
		return contents.toArray(new String[contents.size()]);
	}
	public static void writeToFile(String[] data, String filename) {
		  writeToFile(data,filename,false);
	  }
	  
	  public static void writeToFile(String[] data, String filename, boolean append) {
		  File file = new File(filename);
			try {

				if (!file.exists()) {
					file.createNewFile();
				}

				OutputStream out = new FileOutputStream(file,append);
			    Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				PrintWriter pw = new PrintWriter(writer);
				//pw.print('\uFEFF');
				for (String s : data) {
					pw.println(s);
				}
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	  }
}
