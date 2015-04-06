package es.sidelab.pascaline.cdtinterface.launch;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class FpcPathDiscoverer {

	public static IPath getFPCPath() {
		IPath subPath = new Path("fpc");
		
		// 1. Try the FPC directory in the platform installation directory
		IPath installPath = new Path(Platform.getInstallLocation().getURL().getFile());
		IPath binPath = installPath.append(subPath);
		if(binPath.toFile().isDirectory()) {
			return getFpcBinDir(binPath);
		}
		
		// 2. Try the directory above the install dir
		binPath = installPath.removeLastSegments(1).append(subPath);
		if(binPath.toFile().isDirectory()) { 
			return getFpcBinDir(binPath); 
		}
		
		// 3. Try the default Windows FPC install dir
		binPath = new Path("C:\\fpc");
		if(binPath.toFile().isDirectory()) {
			return getFpcBinDir(binPath);
		}
		
		// 4. Try the /usr/bin path
		binPath = new Path("/usr/bin/fpc");
		if(binPath.toFile().exists() && binPath.toFile().isFile()) {
			return new Path("/usr/bin");
		}
		
		// 5. Try the /usr/local/bin path
		binPath = new Path("/usr/local/bin/fpc");
		if(binPath.toFile().exists() && binPath.toFile().isFile()) {
			return new Path("/usr/local/bin");
		}
		
		// 6. Try the bin dir in user home
		binPath = new Path(System.getProperty("user.home")).append("bin");
		if(binPath.toFile().exists() && binPath.toFile().isFile()) {
			return binPath;
		}
		
		// Oooops!, not found
		return null;
	}
	
	private static IPath getFpcBinDir(IPath binPath) {
		File[] fpcVersions = binPath.toFile().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		int higher = 0;
		int medium = 0;
		int lower = 0;
		IPath binDir = null;
		for(File dir : fpcVersions) {
			StringTokenizer st = new StringTokenizer(dir.getName(), ".");
			int highVersionNumber = Integer.parseInt(st.nextToken());
			int mediumVersionNumber = Integer.parseInt(st.nextToken());
			int lowerVersionNumber = Integer.parseInt(st.nextToken());
			
			if(highVersionNumber > higher) {
				higher = highVersionNumber;
				medium = mediumVersionNumber;
				lower = lowerVersionNumber;
				binDir = new Path(dir.getAbsolutePath()); 
			} else if(highVersionNumber == higher) {
				if(mediumVersionNumber > medium){
					higher = highVersionNumber;
					medium = mediumVersionNumber;
					lower = lowerVersionNumber;
					binDir = new Path(dir.getAbsolutePath()); 
				} else if(mediumVersionNumber == medium) {
					if(lowerVersionNumber > lower) {
						higher = highVersionNumber;
						medium = mediumVersionNumber;
						lower = lowerVersionNumber;
						binDir = new Path(dir.getAbsolutePath()); 
					}
				}
			}
		}
		
		return binDir.append("bin").append("i386-win32");
	}	
	
}
