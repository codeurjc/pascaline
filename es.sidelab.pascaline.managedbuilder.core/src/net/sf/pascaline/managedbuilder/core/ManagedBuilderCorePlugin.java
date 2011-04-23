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
package net.sf.pascaline.managedbuilder.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import net.sf.pascaline.internal.core.model.PascalElement;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ManagedBuilderCorePlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.pascaline.managedbuilder.core";

	public static final String MINGW_FREEPASCAL_TOOLCHAIN_ID = "net.sf.pascaline.managedbuild.toolchain.fpc.exe.debug.mingw.oo";
	public static final String LINUX_FREEPASCAL_TOOLCHAIN_ID = "net.sf.pascaline.managedbuild.toolchain.fpc.exe.debug.linux.oo";
	public static final String MINGW_TURBOPASCAL_ID = "net.sf.pascaline.managedbuild.toolchain.fpc.exe.debug.mingw";
	public static final String LINUX_TURBOPASCAL_TOOLCHAIN_ID = "net.sf.pascaline.managedbuild.toolchain.fpc.exe.debug.linux";

	public static final String FREEPASCAL_COMPILER_WRAPPER_LIB = "metafpc.jar";
	// The shared instance
	private static ManagedBuilderCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public ManagedBuilderCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		IPath path = this.getStateLocation();
		extractFileFromJarIfNecessary("/metafpc/" + FREEPASCAL_COMPILER_WRAPPER_LIB, path.toOSString() + File.separator + FREEPASCAL_COMPILER_WRAPPER_LIB);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	
	private void extractFileFromJarIfNecessary(String srcFile, String destFile) {
		try {
			String path = this.getBundle().getLocation().substring(this.getBundle().getLocation().indexOf("/"));
			File metafpc = new File(path, "metafpc" + File.separator + FREEPASCAL_COMPILER_WRAPPER_LIB);
			InputStream isCompiler = null;
			if(metafpc.exists()) {
				log(PLUGIN_ID, "Compiler exists");
				isCompiler = new FileInputStream(metafpc);
			} else {
				log(PLUGIN_ID, "Compiler doesn't exist");
				isCompiler = this.getClass().getResourceAsStream(srcFile);
			}
			
			File compiler = new File(destFile);
			if(!compiler.exists()) {
				compiler.createNewFile();
			}
			OutputStream outCompiler = new FileOutputStream(compiler);
			int leidos;
			byte[] info = new byte[2048];
			while((leidos = isCompiler.read(info)) != -1) {
				outCompiler.write(info,0,leidos);
			}
			outCompiler.close();
			isCompiler.close();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ManagedBuilderCorePlugin getDefault() {
		return plugin;
	}

	public static void log(String pluginID, Throwable t) {
		getDefault().getLog().log(new Status(IStatus.ERROR, pluginID, t.getMessage(), t));
	}
	
	public static void log(String pluginID, String message) {
		getDefault().getLog().log(new Status(IStatus.INFO, pluginID, message));
	}

	/**
	 * Searchs a main pascal program in the project. The first program found is returned.
	 * 
	 * @param project The project to search into
	 * @return the path of the main program or null if no program was found 
	 * @throws CoreException
	 * @throws CModelException
	 */
	public static IPath getProgram(IProject project, boolean relative) throws CoreException, CModelException {
		CoreModel model = CCorePlugin.getDefault().getCoreModel();
		ICProject icProject = model.create(project);
		ICElement[] children = icProject.getChildren();
		for (ICElement element : children) {
			if (element instanceof ISourceRoot) {
				ISourceRoot sr = (ISourceRoot) element;
				ICElement[] children2 = sr.getChildren();
				for (ICElement element2 : children2) {
					if (element2 instanceof ITranslationUnit) {
						ITranslationUnit tu = (ITranslationUnit) element2;
						ICElement[] children3 = tu.getChildren();
						for (ICElement element3 : children3) {
							if (element3 instanceof PascalElement.Program) {
								return relative ? tu.getResource().getFullPath() : tu.getLocation();
							}
						}
					}
				}
			}
		}
		return null;
	}
}
