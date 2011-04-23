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
package net.sf.pascaline.internal.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class PascalDocumentProvider extends FileDocumentProvider {

	/**
	 * Constructor for PascalDocumentProvider.
	 */
	public PascalDocumentProvider() {
		super();
	}

	/* (non-Javadoc)
	 * Method declared on AbstractDocumentProvider
	 */
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);

		// if(element instanceof FileEditorInput){
		// FileEditorInput fEditorInput = (FileEditorInput) element;
		// IFile iFile = fEditorInput.getFile();
		// IMarker[] markers = iFile.findMarkers(PascalBuilder.MARKER_TEXT_TYPE, true, IResource.DEPTH_INFINITE);
		// for(IMarker marker : markers) {
		// Boolean offsetResolved = (Boolean) marker.getAttribute(PascalBuilder.OFFSET_RESOLVED);
		// if(offsetResolved != null && !offsetResolved) {
		// Integer line = (Integer) marker.getAttribute(PascalBuilder.LINE);
		// Integer column = (Integer) marker.getAttribute(PascalBuilder.COLUMN);
		// int offset;
		// try {
		// offset = document.getLineOffset(line-1) + column-1;
		// } catch (BadLocationException e) {
		// offset = 0;
		// }
		// marker.setAttribute(IMarker.CHAR_START, new Integer(offset));
		// marker.setAttribute(IMarker.CHAR_END, new Integer(offset+1));
		// marker.setAttribute(PascalBuilder.OFFSET_RESOLVED, Boolean.TRUE);
		// }
		// }
		// }
		
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new PascalPartitionScanner(),
					new String[] { PascalPartitionScanner.PAS_MOD, PascalPartitionScanner.PAS_COMMENT });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}