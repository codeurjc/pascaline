package net.sf.pascaline.cdtinterface.ui.wizards;

public class TurboPascalProjectWizard extends FreePascalProjectWizard {
	
	private static final String WIZARD_TITLE = "Turbo Pascal Project";
	private static final String WIZARD_DESCRIPTION = "Create Turbo Pascal project of selected type";

	public TurboPascalProjectWizard() {
		super(WIZARD_TITLE, WIZARD_DESCRIPTION);
	}
	
	@Override
	public void addPages() {
		fMainPage= new TurboPascalMainWizardPage(WIZARD_TITLE);
		fMainPage.setDescription(WIZARD_DESCRIPTION);
		addPage(fMainPage);
	}

}
