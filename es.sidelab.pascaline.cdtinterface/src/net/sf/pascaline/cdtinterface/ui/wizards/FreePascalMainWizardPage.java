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
package net.sf.pascaline.cdtinterface.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.pascaline.cdtinterface.CDTInterfacePlugin;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSWizardHandler;
import org.eclipse.cdt.ui.wizards.CDTMainWizardPage;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.cdt.utils.Platform;
import org.eclipse.core.runtime.CoreException;

public class FreePascalMainWizardPage extends CDTMainWizardPage {
	
	public FreePascalMainWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public List<EntryDescriptor> filterItems(List items) {
		List<EntryDescriptor> filteredItems = new ArrayList<EntryDescriptor>();
		
		for(int i = 0; i < items.size(); i++) {
			EntryDescriptor ed = (EntryDescriptor) items.get(i);
			if(Platform.getOS().equals(Platform.OS_LINUX)) {
				if(ed.getId().equals(CDTInterfacePlugin.FREEPASCAL_LINUX)) {
					filteredItems.add(ed);
				}				
			} else {
				if(ed.getId().equals(CDTInterfacePlugin.FREEPASCAL_WIN32)) {
					filteredItems.add(ed);
				}									
			}
		}
		return filteredItems; 
	}

	
	
}
