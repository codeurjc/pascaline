package net.sf.pascaline.cdtinterface.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import net.sf.pascaline.cdtinterface.CDTInterfacePlugin;

import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.cdt.utils.Platform;

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
			} else {
				if(ed.getId().equals(CDTInterfacePlugin.TURBOPASCAL_WIN32)) {
					filteredItems.add(ed);
				}
			}
		}
		return filteredItems; 
	}


}
