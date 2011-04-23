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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class PascalConfiguration extends SourceViewerConfiguration {
	
	private PascalScanner scanner;
	private ColorManager colorManager;
	private IAnnotationHover annotationHover;

	public PascalConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			PascalPartitionScanner.PAS_COMMENT };
	}

	protected PascalScanner getPascalFCScanner() {
		if (scanner == null) {
			scanner = new PascalScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(colorManager.getColor(IPascalColorConstants.DEFAULT))));
		}
		return scanner;
	}
	
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer){
		if(annotationHover == null){
			this.annotationHover = new DefaultAnnotationHover();
		}
		return annotationHover;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getPascalFCScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IPascalColorConstants.COMMENT)));
		reconciler.setDamager(ndr, PascalPartitionScanner.PAS_COMMENT);
		reconciler.setRepairer(ndr, PascalPartitionScanner.PAS_COMMENT);

		return reconciler;
	}

}