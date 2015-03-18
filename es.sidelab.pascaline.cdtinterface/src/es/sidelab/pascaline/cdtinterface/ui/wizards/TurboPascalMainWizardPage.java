package es.sidelab.pascaline.cdtinterface.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.core.runtime.Platform;

import es.sidelab.pascaline.cdtinterface.CDTInterfacePlugin;

public class TurboPascalMainWizardPage extends FreePascalMainWizardPage {

	public TurboPascalMainWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public List<EntryDescriptor> filterItems(List items) {
		List<EntryDescriptor> filteredItems = new ArrayList<EntryDescriptor>();
		
		for(int i = 0; i < items.size(); i++) {
			EntryDescriptor ed = (EntryDescriptor) items.get(i);
			if(Platform.getOS().equals(Platform.OS_LINUX)) {
				if(ed.getId().equals(CDTInterfacePlugin.TURBOPASCAL_LINUX)) {
					filteredItems.add(ed);
				}
			} else if(Platform.getOS().equals(Platform.OS_MACOSX)) {
				if(ed.getId().equals(CDTInterfacePlugin.TURBOPASCAL_MACOS)) {
					filteredItems.add(ed);
				}
			} else if(Platform.getOS().equals(Platform.OS_WIN32)) {
				if(ed.getId().equals(CDTInterfacePlugin.TURBOPASCAL_WIN32)) {
					filteredItems.add(ed);
				}
			}
			if(ed.getId().equals("Others")) {
				filteredItems.add(ed);
			}			
		}
		return filteredItems; 
	}


}
