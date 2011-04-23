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
package net.sf.pascaline.managedbuilder.core.makegen;

import java.io.File;
import java.util.Arrays;

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

public class FreePascalCommandLineGenerator implements IManagedCommandLineGenerator {

	private static final String FPC_COMMAND = "fpc";

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
			return FPC_COMMAND;
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
			return FPC_COMMAND;
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
	
	public FreePascalCommandLineGenerator() {
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
		sb.append(commandName);
		
		sb.append(" ");

		sb.append("-B ");
		sb.append("-g ");
		
		if(tc.getBaseId().startsWith(ManagedBuilderCorePlugin.MINGW_FREEPASCAL_TOOLCHAIN_ID) || tc.getBaseId().startsWith(ManagedBuilderCorePlugin.LINUX_FREEPASCAL_TOOLCHAIN_ID)) {
			sb.append("-Mfpc");
		} else if(tc.getBaseId().startsWith(ManagedBuilderCorePlugin.MINGW_TURBOPASCAL_ID) || tc.getBaseId().startsWith(ManagedBuilderCorePlugin.LINUX_TURBOPASCAL_TOOLCHAIN_ID)) {
			sb.append("-Mtp");
		} 
			
		sb.append(" ");
		
		IConfiguration configuration = tc.getParent();
		IProject project = (IProject) configuration.getOwner();
		ICDescriptor description = null;
		try {
			description = CCorePlugin.getDefault().getCProjectDescription(project, false);
			if (description != null) {
				ICOutputEntry outputDir = description.getConfigurationDescription().getBuildSetting()
				.getOutputDirectories()[0];
				IPath outputPath = outputDir.getFullPath().makeAbsolute();
				int numDirsUp = 0;
				while(!outputPath.equals(project.getFullPath())) {
					outputPath = outputPath.removeLastSegments(1);
					numDirsUp++;
				}
				
				// This is not guaranteed to work for multiple source directories
				ICSourceEntry[] srcEntries = description.getConfigurationDescription().getSourceEntries();
				for(ICSourceEntry srcEntry : srcEntries) {
					sb.append("-Fu");
					for(int i = 0; i < numDirsUp; i++) {
						sb.append(".." + File.separator);
					}
					sb.append(srcEntry.getFullPath().removeFirstSegments(1).toOSString());
					sb.append(" ");
				}
				
				File outputLocal = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString(), outputDir.getName());
				sb.append("-FE. ");
				
				sb.append(outputFlag);
				sb.append(outputName);
				// This is specified in the configuration of the projectType
//				if(Platform.getOS().equals(Platform.OS_WIN32)) {
//					sb.append(".exe");
//				}
				sb.append(" ");

				// Old way of compiling programs. Just compile the main program
//				IPath programToCompile = ManagedBuilderCorePlugin.getProgram(project, true);
//				if(programToCompile != null) {
//					programToCompile = programToCompile.makeRelative();
//					int numSegmentsToRemove = programToCompile.matchingFirstSegments(project.getFullPath());
//					programToCompile = programToCompile.removeFirstSegments(numSegmentsToRemove);
//					for(int i = 0; i < numDirsUp; i++) {
//						sb.append(".." + File.separator);
//					}
//					sb.append(programToCompile.toOSString());
//				}
				sb.append(inputResources[0]);
			}
		} catch (CoreException e) {
			ManagedBuilderCorePlugin.log(ManagedBuilderCorePlugin.PLUGIN_ID, e);
		}
						
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
				return FPC_COMMAND;
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
