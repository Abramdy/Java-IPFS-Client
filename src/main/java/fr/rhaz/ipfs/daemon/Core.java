package fr.rhaz.ipfs.daemon;

import java.awt.SplashScreen;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;

import fr.rhaz.ipfs.daemon.Wget.WgetStatus;
import io.ipfs.api.IPFS;

public class Core{
	public static Core i;
	public static IPFS ipfs;
	private OS os;
	
	public static void main(String[] args){
		i = new Core();
	}

	public Core(){
		getOS();
		if(os == null || (os.equals(OS.FREEBSD) && !is64bits())) {
			System.out.println("System not supported");
			System.exit(1);
		}
		
		getBin();
		
		run(() -> {
			try {
				Process init = process("init");
				gobble(init);
				init.waitFor();
				Process daemon = process("daemon");
				gobble(daemon);
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		            daemon.destroy();
				}));
				daemon.waitFor();
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
		
		try {
			Thread.sleep(5000);
			SplashScreen.getSplashScreen().close();
			attach();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getOS() {
		if(SystemUtils.IS_OS_WINDOWS)
			os = OS.WINDOWS;
        if(SystemUtils.IS_OS_LINUX)
        	os = OS.LINUX;
        if(SystemUtils.IS_OS_MAC)
        	os = OS.MAC;
        if(SystemUtils.IS_OS_FREE_BSD)
        	os = OS.FREEBSD;
	}
	
	public boolean is64bits() {
		return System.getProperty("os.arch").contains("64");
	}
	
	public File getBin() {
		switch(os) {
			case WINDOWS:{
				File bin = new File("bin.exe");
				if(!bin.exists()) {
					try {
						downloadBin();
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
				return bin;
			}
			case MAC:{
				File bin = new File("bin");
				if(!bin.exists()) {
					try {
						downloadBin();
					} catch (Exception e) {
						return null;
					}
				}
				return bin;
			}
			case LINUX:{
				File bin = new File("bin");
				if(!bin.exists()) {
					try {
						downloadBin();
					} catch (Exception e) {
						return null;
					}
				}
				return bin;
			}
			case FREEBSD:{
				File bin = new File("bin");
				if(!bin.exists()) {
					try {
						downloadBin();
					} catch (Exception e) {
						return null;
					}
				}
				return bin;
			}
		} return null;
	}
	
	public void getFileFromZip(String path, File zip, File destination) throws IOException{
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
		ZipEntry ze = zis.getNextEntry();
		while(ze!=null) {
			if(ze.getName().equals(path)) {
				FileUtils.copyInputStreamToFile(zis, destination);
				break;
			}
			ze = zis.getNextEntry();
		}
		zis.close();
	}
	
	public void downloadBin() throws Exception {
		String url = "https://dist.ipfs.io/go-ipfs/v0.4.13/go-ipfs_v0.4.13_";
		switch(os) {
			case WINDOWS:{
				url = url + "windows-amd64.zip";
				System.out.println(url);
				FileUtils.copyURLToFile(new URL(url), new File("go.zip"));
				System.out.println("Bin downloaded");
				getFileFromZip("go-ipfs/ipfs.exe", new File("go.zip"), new File("bin.exe"));
				break;
			}
			case MAC:{
				url = url + "/mac" + (is64bits()?"64":"32") + "/ipfs";
				WgetStatus wget = Wget.wGet("ipfs", url);
				if(!wget.equals(WgetStatus.Success)) throw new Exception();
				break;
			}
			case LINUX:{
				url = url + "/linux" + (is64bits()?"64":"32") + "/ipfs";
				WgetStatus wget = Wget.wGet("ipfs", url);
				if(!wget.equals(WgetStatus.Success)) throw new Exception();
				break;
			}
			case FREEBSD:{
				url = url + "/freebsd" + "64" + "/ipfs";
				WgetStatus wget = Wget.wGet("ipfs", url);
				if(!wget.equals(WgetStatus.Success)) throw new Exception();
				break;
			}
		}
	}
	
	public Thread run(Runnable r) {
		Thread t = new Thread(r);
		t.start();
		return t;
	}
	
	public Thread run(boolean gobble, String... args) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Process p = process(args);
					if(gobble) gobble(p);
					p.waitFor();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		t.start();
		return t;
	}
	
	public void gobble(Process p) {
		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream());
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream());
		errorGobbler.start();
		outputGobbler.start();
	}
	
	public Process process(String... args) throws IOException{
		File bin = getBin();
		String[] cmd = ArrayUtils.insert(0, args, bin.getPath());
		return Runtime.getRuntime().exec(cmd);
	}

	public void attach() {
		ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
		try {
			Thread.sleep(1000);
			ipfs.refs.local();
			new Window();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
