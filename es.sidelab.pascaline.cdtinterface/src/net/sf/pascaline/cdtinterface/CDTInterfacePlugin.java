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
package net.sf.pascaline.cdtinterface;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CDTInterfacePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.pascaline.cdtinterface";

	// The shared instance
	private static CDTInterfacePlugin plugin;
	
	// Constants
	public static final String LANGUAGE_ID = PLUGIN_ID + ".freepascal";
	public static final String PASCAL_EXTENSION = "pas";
	public static final String CONTENT_TYPE_PASCAL_SOURCE = "net.sf.pascaline.core.pascalSource";
	public static final String WINDOWS_COMMAND_FACTORY = "net.sf.pascaline.cdtinterface.WinShellCommandFactory"; 
	public static final String LAUNCH_CONFIGURATION_TYPE = "net.sf.pascaline.launch.localCLaunch";
	
	// Toolchain compiler ids
	public static final String FREEPASCAL_LINUX = "net.sf.pascaline.managedbuild.target.fpc.exe.linux.oo";
	public static final String TURBOPASCAL_LINUX = "net.sf.pascaline.managedbuild.target.fpc.exe.linux";
	public static final String FREEPASCAL_WIN32	= "net.sf.pascaline.managedbuild.target.fpc.exe.win.oo";
	public static final String TURBOPASCAL_WIN32 = "net.sf.pascaline.managedbuild.target.fpc.exe.win";

	public static final String PASCAL_WIZARD_CATEGORY_ID = "net.sf.pascaline.ui.newFreePascalWizards";

	
	/**
	 * The constructor
	 */
	public CDTInterfacePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CDTInterfacePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * Based on Photran <code>CDTInterfacePlugin</code>.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static void log(String pluginID, Throwable t) {
		getDefault().getLog().log(new Status(IStatus.ERROR, pluginID, t.getMessage(), t));
	}
	
	public static void log(String pluginID, String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR, pluginID, message));
	}
	
}
