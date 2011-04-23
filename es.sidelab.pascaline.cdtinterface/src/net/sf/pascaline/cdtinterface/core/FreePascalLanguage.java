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
package net.sf.pascaline.cdtinterface.core;

import java.util.Arrays;
import java.util.Collection;

import net.sf.pascaline.cdtinterface.dom.IPascalDOMParser;
import net.sf.pascaline.core.PascalineCorePlugin;
import net.sf.pascaline.internal.core.model.IFreePascalModelBuilder;
import net.sf.pascaline.internal.core.model.SimpleFreePascalModelBuilder;

import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.IASTCompletionNode;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.IContributedModelBuilder;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.core.runtime.CoreException;


/**
 * 
 * @author Patxi Gortázar
 * 
 */
public class FreePascalLanguage extends AbstractLanguage {
	
	public static final String LANGUAGE_ID = "net.sf.pascaline.cdtinterface.freepascal";
	private static final String FREEPASCAL_MODEL_BUILDER_EXTENSION_POINT_ID = "net.sf.pascaline.cdtinterface.modelbuilder";

	@Override
	public IContributedModelBuilder createModelBuilder(ITranslationUnit tu) {
		IFreePascalModelBuilder modelBuilder = null;

		// IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(
		// FREEPASCAL_MODEL_BUILDER_EXTENSION_POINT_ID);
		// if (configs.length > 0) {
		// int index = findPreferredModelBuilder(configs);
		// try {
		// modelBuilder = (IFreePascalModelBuilder) configs[index].createExecutableExtension("class");
		// } catch (CoreException e) {
		// }
		// }

		if (modelBuilder == null) {
			// El modelbuilder por defecto.
			modelBuilder = new SimpleFreePascalModelBuilder();
		}
		
		modelBuilder.setTranslationUnit(tu);
		return modelBuilder;
	}

	@Override
	public IASTTranslationUnit getASTTranslationUnit(CodeReader reader, IScannerInfo scanInfo,
			ICodeReaderFactory fileCreator, IIndex index, IParserLogService log) throws CoreException {
		IPascalDOMParser domParser = getDOMParser();
		if (domParser == null) {
			return null;
		}
		return domParser.getASTTranslationUnit(reader, scanInfo, fileCreator, index, log);
	}

	@Override
	public IASTCompletionNode getCompletionNode(CodeReader reader, IScannerInfo scanInfo,
			ICodeReaderFactory fileCreator, IIndex index, IParserLogService log, int offset) throws CoreException {
		IPascalDOMParser domParser = getDOMParser();
		if (domParser == null) {
			return null;
		}
		return domParser.getCompletionNode(reader, scanInfo, fileCreator, index, log, offset);
	}

	@Override
	public IASTName[] getSelectedNames(IASTTranslationUnit ast, int start, int length) {
		IPascalDOMParser domParser = getDOMParser();
		if (domParser == null) {
			return null;
		}
		return domParser.getSelectedNames(ast, start, length);
	}

	private IPascalDOMParser getDOMParser() {
		IPascalDOMParser modelBuilder = null;
		return modelBuilder;
	}
	
	@Override
	public String getId() {
		return LANGUAGE_ID;
	}

	/*
	 * 
	 * Copied from FortranLanguage.java
	 * 
	 * @see org.eclipse.cdt.core.model.ILanguage#getLinkageID()
	 */
	@Override
	public int getLinkageID() {
		return ILinkage.C_LINKAGE_ID;
	}

	/**
	 * @return
	 */
	public Collection getRegisteredContentTypeIds() {
		return Arrays.asList(new String[] { PascalineCorePlugin.FREEPASCAL_CONTENT_TYPE });
	}

}
