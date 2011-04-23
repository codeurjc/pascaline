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
package net.sf.pascaline.managedbuilder.fpc;

import java.io.File;
import java.util.Arrays;
import java.util.jar.JarFile;

import net.sf.pascaline.internal.core.model.PascalElement;
import net.sf.pascaline.managedbuilder.core.ManagedBuilderCorePlugin;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICOutputEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.core.settings.model.extension.CBuildData;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

public class MetaFreePascalCommandLineGenerator implements IManagedCommandLineGenerator {

	private static final String METAFPC_COMMAND = "java";

	class FreePascalCommandLineInfo implements IManagedCommandLineInfo {

		@Override
		public String getCommandLine() {
			return "fpc -B -g -Mfpc";
		}

		@Override
		public String getCommandLinePattern() {
			return "";
		}

		@Override
		public String getCommandName() {
			return METAFPC_COMMAND;
		}

		@Override
		public String getFlags() {
			return "-B -g -Mfpc";
		}

		@Override
		public String getInputs() {
			return "";
		}

		@Override
		public String getOutput() {
			return "";
		}

		@Override
		public String getOutputFlag() {
			return "-o";
		}

		@Override
		public String getOutputPrefix() {
			return "";
		}
		
	}
	
	class TurboPascalCommandLineInfo implements IManagedCommandLineInfo {

		@Override
		public String getCommandLine() {
			return "fpc -B -g -Mtp";
		}

		@Override
		public String getCommandLinePattern() {
			return "";
		}

		@Override
		public String getCommandName() {
			return METAFPC_COMMAND;
		}

		@Override
		public String getFlags() {
			return "-B -g -Mtp";
		}

		@Override
		public String getInputs() {
			return "";
		}

		@Override
		public String getOutput() {
			return "";
		}

		@Override
		public String getOutputFlag() {
			return "-o";
		}

		@Override
		public String getOutputPrefix() {
			return "";
		}
		
	}
	
	public MetaFreePascalCommandLineGenerator() {
	}

	@Override
	public IManagedCommandLineInfo generateCommandLineInfo(ITool tool, String commandName, String[] flags,
			String outputFlag, String outputPrefix, String outputName, String[] inputResources,
			String commandLinePattern) {
		IToolChain tc = (IToolChain) tool.getParent();
		if (inputResources == null) {
			
			if(tc.getBaseId().startsWith(ManagedBuilderCorePlugin.MINGW_FREEPASCAL_TOOLCHAIN_ID) || tc.getBaseId().startsWith(ManagedBuilderCorePlugin.LINUX_FREEPASCAL_TOOLCHAIN_ID)) {
				return new FreePascalCommandLineInfo();
			} else if(tc.getBaseId().startsWith(ManagedBuilderCorePlugin.MINGW_TURBOPASCAL_ID) || tc.getBaseId().startsWith(ManagedBuilderCorePlugin.LINUX_TURBOPASCAL_TOOLCHAIN_ID)) {
				return new TurboPascalCommandLineInfo();
			} 
		}
		
		final StringBuffer sb = new StringBuffer();
		sb.append("java -jar \"");
		IPath pluginStateArea = ManagedBuilderCorePlugin.getDefault().getStateLocation();
		IPath jarFile = pluginStateArea.append("metafpc.jar");
		sb.append(jarFile.toOSString());
		sb.append("\" ");
		for(String inputFile : inputResources) {
			sb.append(inputFile);
			sb.append(" ");
		}
		
		// This mark is used to signal the end of files to be compiled and the start of the fpc command and parameters.
		sb.append("__");
		sb.append(" ");
		
		if(Platform.getOS().equals(Platform.OS_WIN32)) {
			IPath fpcPath = FreePascalEnvironmentVariableSupplier.getFPCPath();
			sb.append("\"" + fpcPath.toOSString() + File.separator + "fpc.exe\"");
		} else {
			// On *nix systems, fpc would usually be in the path
			sb.append("fpc");
		}
		
		sb.append(" ");

		// Compile this file and all other files referenced from it
		sb.append("-B ");
		// Compile with debugging symbols
		sb.append("-g ");
		// Generate output files in the current directory (usually, the Debug folder)
		sb.append("-FE. ");
		
		sb.append(outputFlag);
		sb.append(outputName);
		
		sb.append(" ");
		
		if(tc.getBaseId().startsWith(ManagedBuilderCorePlugin.MINGW_FREEPASCAL_TOOLCHAIN_ID) || tc.getBaseId().startsWith(ManagedBuilderCorePlugin.LINUX_FREEPASCAL_TOOLCHAIN_ID)) {
			sb.append("-Mfpc");
		} else if(tc.getBaseId().startsWith(ManagedBuilderCorePlugin.MINGW_TURBOPASCAL_ID) || tc.getBaseId().startsWith(ManagedBuilderCorePlugin.LINUX_TURBOPASCAL_TOOLCHAIN_ID)) {
			sb.append("-Mtp");
		} 
		
		ManagedBuilderCorePlugin.log(ManagedBuilderCorePlugin.PLUGIN_ID, sb.toString());
			
		IManagedCommandLineInfo cli = new IManagedCommandLineInfo() {

			@Override
			public String getCommandLine() {
				return sb.toString();
			}

			@Override
			public String getCommandLinePattern() {
				return "";
			}

			@Override
			public String getCommandName() {
				return METAFPC_COMMAND;
			}

			@Override
			public String getFlags() {
				return "";
			}

			@Override
			public String getInputs() {
				return "";
			}

			@Override
			public String getOutput() {
				return "";
			}

			@Override
			public String getOutputFlag() {
				return "";
			}

			@Override
			public String getOutputPrefix() {
				return "";
			}
		};
		
		return cli;
	}

	
}
