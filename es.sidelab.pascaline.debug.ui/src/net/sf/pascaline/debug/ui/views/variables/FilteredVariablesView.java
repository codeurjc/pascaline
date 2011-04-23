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
package net.sf.pascaline.debug.ui.views.variables;

import net.sf.pascaline.debug.internal.ui.PFrame;

import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.internal.core.model.CStackFrame;
import org.eclipse.cdt.debug.internal.core.model.CThread;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


public class FilteredVariablesView extends AbstractDebugView implements IDebugContextListener {

	private SashForm fSashForm;
	private PascalVariablesViewModelPresentation fModelPresentation;
	private TreeViewer variablesViewer;
	private TreeColumn[] columns;

	public FilteredVariablesView() {
//		CDebugCorePlugin.getDefault().getActiveDebugConfigurations();
	}

	@Override
	protected void configureToolBar(IToolBarManager tbm) {

	}

	@Override
	protected void createActions() {

	}

	@Override
	protected Viewer createViewer(Composite parent) {

		// create the sash form that will contain the tree viewer & text viewer
		// fSashForm = new SashForm(parent, SWT.NONE);

		fModelPresentation = new PascalVariablesViewModelPresentation();

		variablesViewer = createTreeViewer(parent);
		setViewer(variablesViewer);

		// fSashForm.setMaximizedControl(variablesViewer.getControl());

		// fDetailPane = DetailPaneManager.getDefault().getDetailPaneFromID(ID);
		// fDetailPane.display(null); // Bring up the default pane so the user doesn't see an empty composite

		// IMemento memento = getMemento();
		// if (memento != null) {
		// variablesViewer.initState(memento);
		// }
		//
		// initDragAndDrop(variablesViewer);

		return variablesViewer;
	}

	/**
	 * @param sashForm
	 * @return
	 */
	private TreeViewer createTreeViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		TreeViewer treeViewer = new TreeViewer(tree);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		// set the column headers implement this
		createTreeColumns(tree);

		// set the content and label providers
		treeViewer.setContentProvider(getContentProvider());
		treeViewer.setLabelProvider(getLabelProvider());

		// using internal hash table speeds the lookup
		treeViewer.setUseHashlookup(true);

		// layout the table tree viewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalSpan = 1;
		treeViewer.getControl().setLayoutData(layoutData);

		// have a way to get the column names
		treeViewer.setColumnProperties(getColumnNames());

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Object o = ((IStructuredSelection) selection).getFirstElement();
				}
			}
		});
		
		DebugUITools.getDebugContextManager().getContextService(getSite().getWorkbenchWindow())
				.addDebugContextListener(this);
		
		return treeViewer;
	}

	/**
	 * @return
	 */
	private String[] getColumnNames() {
		String[] names = new String[columns.length];
		int index = 0;
		for (TreeColumn tc : columns) {
			names[index++] = tc.getText();
		}
		return names;
	}

	/**
	 * @return
	 */
	private IBaseLabelProvider getLabelProvider() {
		return new PascalVariablesLabelProvider();
	}

	/**
	 * @return
	 */
	private IContentProvider getContentProvider() {
		return new PascalVariablesContentProvider();
	}

	/**
	 * 
	 */
	private void createTreeColumns(Tree tree) {
		columns = new TreeColumn[2];
		TreeColumn columnVariableName = new TreeColumn(tree, SWT.LEFT);
		columnVariableName.setText("Name");
		columnVariableName.setWidth(200);
		columns[0] = columnVariableName;
		TreeColumn columnValue = new TreeColumn(tree, SWT.LEFT);
		columnValue.setText("Value");
		columnValue.setWidth(200);
		columns[1] = columnValue;
	}

	@Override
	protected void fillContextMenu(IMenuManager menu) {
	}

	@Override
	protected String getHelpContextId() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextListener#debugContextChanged(org.eclipse.debug.ui.contexts.DebugContextEvent)
	 */
	@Override
	public void debugContextChanged(DebugContextEvent event) {
		if (event.getContext() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) event.getContext();
			if(selection.getFirstElement() instanceof CStackFrame) {
				CStackFrame frame = (CStackFrame) selection.getFirstElement();
				variablesViewer.setInput(new PFrame(frame));
			} else if(selection.getFirstElement() instanceof CThread) {
				CThread thread = (CThread) selection.getFirstElement();
				// Clean the variables view... no frame is selected
				
			}
		}
	}

}
