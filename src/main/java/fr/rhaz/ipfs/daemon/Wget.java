package fr.rhaz.ipfs.daemon;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Wget {
	
	static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
	
	public enum WgetStatus {
		Success, MalformedUrl, IoException, UnableToCloseOutputStream;
	}
	
	public static void download(String file, String url) throws Exception {
		URL website = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
	public static WgetStatus wGet(String saveAsFile, String urlOfFile) {
		InputStream in = null;
		OutputStream out = null;
	    try {
	      // check the http connection before we do anything to the fs
	      URLConnection con = new URL(urlOfFile).openConnection();
	      con.setRequestProperty("User-Agent", USER_AGENT);
	      
	      int contentLength = con.getContentLength();
	        System.out.println("File contentLength = " + contentLength + " bytes");
	      
	      in = con.getInputStream();
	      // prep saving the file
	      out = new FileOutputStream(saveAsFile);
	      byte[] buffer = new byte[2048];

	        // Increments file size
	        int length;
	        int downloaded = 0; 

	        // Looping until server finishes
	        while ((length = in.read(buffer)) != -1) 
	        {
	            // Writing data
	            out.write(buffer, 0, length);
	            downloaded+=length;
	            System.out.println("Download status: "+ (downloaded * 100) / (contentLength * 1.0) + "%");
	        }
	    } catch (MalformedURLException e) {
	      return WgetStatus.MalformedUrl;
	    } catch (IOException e) {
	      return WgetStatus.IoException;
	    } finally {
	      try {
	        in.close();
	        out.close();
	      } catch (IOException e) {
	        return WgetStatus.UnableToCloseOutputStream;
	      }
	    }
	    return WgetStatus.Success;
	}
}
