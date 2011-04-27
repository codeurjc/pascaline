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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.ui.wizards.CDTMainWizardPage;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.core.runtime.Platform;

import es.sidelab.pascaline.cdtinterface.CDTInterfacePlugin;

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
			} else if(Platform.getOS().equals(Platform.OS_MACOSX)) {
				if(ed.getId().equals(CDTInterfacePlugin.FREEPASCAL_MACOS)) {
					filteredItems.add(ed);
				}
			} else if(Platform.getOS().equals(Platform.OS_WIN32)) {
				if(ed.getId().equals(CDTInterfacePlugin.FREEPASCAL_WIN32)) {
					filteredItems.add(ed);
				}									
			}
		}
		return filteredItems; 
	}

	
	
}
