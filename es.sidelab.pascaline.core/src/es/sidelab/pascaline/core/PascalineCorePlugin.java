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
package es.sidelab.pascaline.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PascalineCorePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "es.sidelab.pascaline.core";

	public static final String FREEPASCAL_CONTENT_TYPE = "es.sidelab.pascaline.core.pascalSource";

	// The shared instance
	private static PascalineCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public PascalineCorePlugin() {
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
	public static PascalineCorePlugin getDefault() {
		return plugin;
	}
	
	public void log(String message) {
		log(message, null);
	}

	public void log(String message, Throwable t) {
		log(message, PascalineCorePlugin.PLUGIN_ID, t);
	}

	public void log(String message, String pluginId, Throwable t) {
		this.getLog().log(new Status(IStatus.ERROR, pluginId, message, t));
	}

}
