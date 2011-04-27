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
package es.sidelab.pascaline.cdtinterface.ui.wizards;

import es.sidelab.pascaline.cdtinterface.CDTInterfacePlugin;
import es.sidelab.pascaline.core.PascalineCorePlugin;

/**
 * 
 * @author patxi
 * 
 */
public class NewSourceFolderCreationWizard extends org.eclipse.cdt.ui.wizards.NewSourceFolderCreationWizard {

	public NewSourceFolderCreationWizard() {
		super();
		setDefaultPageImageDescriptor(CDTInterfacePlugin.imageDescriptorFromPlugin(PascalineCorePlugin.PLUGIN_ID,
				"icons/wizban/newsrcfldr_wiz.gif"));
		setWindowTitle("New Source Folder");
	}

}
