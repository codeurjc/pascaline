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
package es.sidelab.pascaline.cdtinterface.dom;

import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.dom.ast.IASTCompletionNode;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Patxi Gortázar
 *
 */
public class SimplePascalDOMParser implements IPascalDOMParser {

	/* (non-Javadoc)
	 * @see es.sidelab.pascaline.cdtinterface.dom.IPascalDOMParser#getASTTranslationUnit(org.eclipse.cdt.core.parser.CodeReader, org.eclipse.cdt.core.parser.IScannerInfo, org.eclipse.cdt.core.dom.ICodeReaderFactory, org.eclipse.cdt.core.index.IIndex, org.eclipse.cdt.core.parser.IParserLogService)
	 */
	@Override
	public IASTTranslationUnit getASTTranslationUnit(CodeReader reader, IScannerInfo scanInfo,
			ICodeReaderFactory fileCreator, IIndex index, IParserLogService log) throws CoreException {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see es.sidelab.pascaline.cdtinterface.dom.IPascalDOMParser#getCompletionNode(org.eclipse.cdt.core.parser.CodeReader, org.eclipse.cdt.core.parser.IScannerInfo, org.eclipse.cdt.core.dom.ICodeReaderFactory, org.eclipse.cdt.core.index.IIndex, org.eclipse.cdt.core.parser.IParserLogService, int)
	 */
	@Override
	public IASTCompletionNode getCompletionNode(CodeReader reader, IScannerInfo scanInfo,
			ICodeReaderFactory fileCreator, IIndex index, IParserLogService log, int offset) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see es.sidelab.pascaline.cdtinterface.dom.IPascalDOMParser#getSelectedNames(org.eclipse.cdt.core.dom.ast.IASTTranslationUnit, int, int)
	 */
	@Override
	public IASTName[] getSelectedNames(IASTTranslationUnit ast, int start, int length) {
		// TODO Auto-generated method stub
		return null;
	}

}
