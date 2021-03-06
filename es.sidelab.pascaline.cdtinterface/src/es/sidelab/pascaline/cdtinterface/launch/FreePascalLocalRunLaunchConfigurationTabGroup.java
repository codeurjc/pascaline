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
package es.sidelab.pascaline.cdtinterface.launch;

import org.eclipse.cdt.launch.internal.ui.MultiLaunchConfigurationTabGroup;
import org.eclipse.cdt.launch.ui.CArgumentsTab;
import org.eclipse.cdt.launch.ui.CDebuggerTab;
import org.eclipse.cdt.launch.ui.CMainTab;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;


public class FreePascalLocalRunLaunchConfigurationTabGroup extends MultiLaunchConfigurationTabGroup {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.launch.internal.ui.LocalRunLaunchConfigurationTabGroup
	 * #createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog,
	 * java.lang.String)
	 */
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
		    new CMainTab(true), new CArgumentsTab(), new EnvironmentTab(), new CDebuggerTab(false), new SourceLookupTab(), new CommonTab()
        };
		setTabs(tabs);
	}	

}
