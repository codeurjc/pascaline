/*******************************************************************************
 * This file is part of Pascaline.
 * 
 * Copyright (c) Pascaline Team.
 * http://pascaline.sourceforge.net/
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package es.sidelab.pascaline.managedbuilder.fpc;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;


import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.cdt.utils.WindowsRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import es.sidelab.pascaline.managedbuilder.core.ManagedBuilderCorePlugin;

public class FreePascalEnvironmentVariableSupplier implements
		IConfigurationEnvironmentVariableSupplier {

	static IPath getFPCPath() {
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

	public static IPath getMingwBinDir() {
 
		IPath subPath = new Path("mingw\\bin");
		// 1. Try the mingw directory in the platform install directory
		IPath installPath = new Path(Platform.getInstallLocation().getURL().getFile());
		IPath binPath = installPath.append(subPath);
		if (binPath.toFile().isDirectory())
			return binPath;
		
		// 2. Try the directory above the install dir
		binPath = installPath.removeLastSegments(1).append(subPath);
		if (binPath.toFile().isDirectory())
			return binPath;
		
		// 3. Try looking if the mingw installer ran
		WindowsRegistry registry = WindowsRegistry.getRegistry();
		if (registry==null) return null; // probably not even windows
		
		String mingwPath = registry.getLocalMachineValue(
					"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\MinGW",
					"InstallLocation");
		if (mingwPath != null) {
			binPath = new Path(mingwPath).append("bin");
			if (binPath.toFile().isDirectory())
				return binPath;
		}
		
		// 4. Try the default MinGW install dir
		binPath = new Path("C:\\MinGW\\bin");
		if (binPath.toFile().isDirectory()) 
			return binPath;
		
		// No dice, return null
		return null;
	}
	
	public static IPath getMsysBinDir() {
		// Just look in the install location parent dir
		IPath installPath = new Path(Platform.getInstallLocation().getURL().getFile()).removeLastSegments(1);
		IPath msysBinPath = installPath.append("msys\\bin");
		return msysBinPath.toFile().isDirectory() ? msysBinPath : null;
	}

	IBuildEnvironmentVariable path;
	
	public FreePascalEnvironmentVariableSupplier() {
		initializePathVariable();
	}

	private void initializePathVariable() {
		IPath fpcPath = getFPCPath();
	
		if(fpcPath != null) {
			IPath mingwPath = getMingwBinDir();
			if(mingwPath != null) {
				IPath msysPath = getMsysBinDir();
				if(msysPath != null) {
					String pathStr = mingwPath.toOSString() + ";" + msysPath.toOSString() + ";" + fpcPath.toOSString();
					path = new FreePascalEnvironmentVariable("PATH", pathStr, IBuildEnvironmentVariable.ENVVAR_APPEND);
				}
			}
		}
	}
	
	@Override
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		// We always call initializePathVariable to protect ourselves from Eclipse changes (ie: the user share the workspace among different Eclipse installations). 
		initializePathVariable();
		if (path != null && variableName.equals(path.getName()))
			return path;
		else
			return null;
	}

	@Override
	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		return path != null
		? new IBuildEnvironmentVariable[] { path }
		: new IBuildEnvironmentVariable[0];
	}

	private static class FreePascalEnvironmentVariable implements IBuildEnvironmentVariable {
		private final String name;
		private final String value;
		private final int operation;

		public FreePascalEnvironmentVariable(String name, String value, int op) {
			this.name = name;
			this.value = value;
			this.operation = op;
		}
		
		@Override
		public String getDelimiter() {
			return ";";
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getOperation() {
			return operation;
		}

		@Override
		public String getValue() {
			return value;
		}
		
	}
}
