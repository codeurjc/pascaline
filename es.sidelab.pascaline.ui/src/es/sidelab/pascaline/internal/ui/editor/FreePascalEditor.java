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
package es.sidelab.pascaline.internal.ui.editor;


import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.actions.SelectionConverter;
import org.eclipse.cdt.internal.ui.editor.CContentOutlinePage;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.IWorkingCopyManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import es.sidelab.pascaline.ui.FreePascalUIPlugin;


public class FreePascalEditor extends TextEditor implements ISelectionChangedListener {

	private ColorManager colorManager;
	private CContentOutlinePage outlinePage;
	private ChainedPreferenceStore fCombinedPreferenceStore;

	/**
	 * Constructor for SampleEditor.
	 * 
	 * Based on AbstractFortranEditor
	 */
	public FreePascalEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new PascalConfiguration(colorManager));
		setRangeIndicator(new DefaultRangeIndicator());
		// setDocumentProvider(new PascalDocumentProvider());
		setDocumentProvider(CUIPlugin.getDefault().getDocumentProvider());
		
        // This has to be set to be notified of changes to preferences
		// Without this, the editor will not auto-update
		IPreferenceStore store = FreePascalUIPlugin.getDefault().getPreferenceStore();
		IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
		fCombinedPreferenceStore = new ChainedPreferenceStore(new IPreferenceStore[] { store, generalTextStore,
				getPreferenceStore() });
		setPreferenceStore(fCombinedPreferenceStore);

		setRulerContextMenuId("#CEditorRulerContext");
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException
	{
		super.doSetInput(input);
		IDocument document = this.getDocumentProvider().getDocument(input);
		
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new PascalPartitionScanner(),
					new String[] {
//						PascalPartitionScanner.XML_TAG,
						PascalPartitionScanner.PAS_COMMENT });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
	}
	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	public CContentOutlinePage getOutlinePage() {
		if (outlinePage == null) {
			outlinePage = new CContentOutlinePage(null);
			outlinePage.addSelectionChangedListener(this);
		}
		setOutlinePageInput(outlinePage, getEditorInput());
		return outlinePage;

	}

	/**
	 * @param outlinePage
	 * @param editorInput
	 */
	public static void setOutlinePageInput(CContentOutlinePage outlinePage, IEditorInput editorInput) {
		if (outlinePage != null) {
			IWorkingCopyManager manager = CUIPlugin.getDefault().getWorkingCopyManager();
			IWorkingCopy workingCopy = manager.getWorkingCopy(editorInput);
			outlinePage.setInput(workingCopy);
		}
	}
	
	/*
	 * Copied from org.eclipse.photran.internal.ui.editor.AbstractFortranEditor
	 * (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {
//		if (IContentOutlinePage.class.equals(required)) {
//			return getOutlinePage();
//		}
		if (required == IShowInTargetList.class) {
			return new IShowInTargetList() {

				@Override
				public String[] getShowInTargetIds() {
					return new String[] { CUIPlugin.CVIEW_ID, IPageLayout.ID_OUTLINE, IPageLayout.ID_RES_NAV };
				}

			};
		}
		if (required == IShowInSource.class) {
			ICElement ce = null;
			try {
				ce = SelectionConverter.getElementAtOffset(this);
			} catch (CModelException e) {
				ce = null;
			}
			if(ce != null) {
				final ISelection selection = new StructuredSelection(ce);
				return new IShowInSource() {

					@Override
					public ShowInContext getShowInContext() {
						return new ShowInContext(getEditorInput(), selection);
					}
					
				};
			}
		}
		return super.getAdapter(required);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection sel = event.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) sel;
			Object obj = selection.getFirstElement();
			if (obj instanceof ISourceReference) {
				try {
					ISourceRange range = ((ISourceReference) obj).getSourceRange();
					if (range != null) {
						setSelection(range, !isActivePart());
					}
				} catch (CModelException e) {
					// Selection change not applied
				}
			}
		}
	}

	/**
	 * @param range
	 * @param b
	 */
	private void setSelection(ISourceRange element, boolean moveCursor) {
        if (element == null) {
			return;
		}

		try {
			IRegion alternateRegion = null;
			int start = element.getStartPos();
			int length = element.getLength();

			// Sanity check sometimes the parser may throw wrong numbers.
			if (start < 0 || length < 0) {
				start = 0;
				length = 0;
			}

			// 0 length and start and non-zero start line says we know
			// the line for some reason, but not the offset.
			if (length == 0 && start == 0 && element.getStartLine() > 0) {
				// We have the information in term of lines, we can work it out.
				// Binary elements return the first executable statement so we have to substract -1
				start = getDocumentProvider().getDocument(getEditorInput()).getLineOffset(element.getStartLine() - 1);
				if (element.getEndLine() > 0) {
					length = getDocumentProvider().getDocument(getEditorInput()).getLineOffset(element.getEndLine())
							- start;
				} else {
					length = start;
				}
				// create an alternate region for the keyword highlight.
				alternateRegion = getDocumentProvider().getDocument(getEditorInput()).getLineInformation(
						element.getStartLine() - 1);
				if (start == length || length < 0) {
					if (alternateRegion != null) {
						start = alternateRegion.getOffset();
						length = alternateRegion.getLength();
					}
				}
			}
			setHighlightRange(start, length, moveCursor);

			if (moveCursor) {
				start = element.getIdStartPos();
				length = element.getIdLength();
				if (start == 0 && length == 0 && alternateRegion != null) {
					start = alternateRegion.getOffset();
					length = alternateRegion.getLength();
				}
				if (start > -1 && getSourceViewer() != null) {
					getSourceViewer().revealRange(start, length);
					getSourceViewer().setSelectedRange(start, length);
				}
				updateStatusField(ITextEditorActionConstants.STATUS_CATEGORY_INPUT_POSITION);
			}
			return;
		} catch (IllegalArgumentException x) {
			// No information to the user
		} catch (BadLocationException e) {
			// No information to the user
		}

        if (moveCursor)
			resetHighlightRange();
	}

	/**
	 * Checks if the editor is active part.
	 * 
	 * @return <code>true</code> if editor is the active part of the workbench.
	 */
	private boolean isActivePart() {
        IWorkbenchWindow window = getSite().getWorkbenchWindow();
		IPartService service = window.getPartService();
		return (this == service.getActivePart());
	}
	
}
