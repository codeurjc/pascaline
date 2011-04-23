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
package net.sf.pascaline.internal.core.model;

import net.sf.pascaline.cdtinterface.CDTInterfacePlugin;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IContributedCElement;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.internal.core.model.Parent;
import org.eclipse.cdt.internal.core.model.SourceManipulation;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This class is based on code from <code>FortranElement</code>.
 * 
 * {@see org.eclipse.photran.internal.core.model.FortranElement}
 * 
 * @author patxi
 * 
 */
public abstract class PascalElement extends SourceManipulation implements ICElement, IParent, ISourceReference,
		IContributedCElement {

	/**
	 * Creates a new <code>PascalElement</code> under the given parent, with the
	 * given name.
	 * 
	 * @param parent
	 * @param name
	 */
	public PascalElement(Parent parent, String name) {
		super(parent, name, -1);
	}

	/**
	 * Provides an image descriptor for the node
	 * 
	 * @return the node image descriptor
	 */
	protected abstract ImageDescriptor getBaseImageDescriptor();

	/**
	 * Returns an <code>ImageDescriptor</code> for an icon in the icons folder of the core plugin. Same as in
	 * <code>FotranElement</code>.
	 * 
	 * @param filename
	 * @return <code>ImageDescriptor</code>
	 */
	public static ImageDescriptor getImageDescriptorForIcon(String filename) {
		return CDTInterfacePlugin.getImageDescriptor("icons/model/" + filename);
	}

	public static ImageDescriptor unknownImageDescriptor() {
		return getImageDescriptorForIcon("unknown.gif");
	}
    
	/*
	 * Eclipse calls the getAdapter method to know if a given object provides some functionality
	 * (non-Javadoc)
	 * @see org.eclipse.cdt.internal.core.model.CElement#getAdapter(java.lang.Class)
	 */
	public java.lang.Object getAdapter(Class required) {
		if (ImageDescriptor.class.equals(required))
			return getBaseImageDescriptor();
		else
			return super.getAdapter(required);
	}
	
	//--------------------------------------------------------------------------
	// ------------------------- Node Subclasses
	// --------------------------------
	//--------------------------------------------------------------------------

	public static class ErrorNode extends PascalElement {

		public ErrorNode(Parent parent, String name) {
			super(parent, name);
		}

		@Override
		protected ImageDescriptor getBaseImageDescriptor() {
			return unknownImageDescriptor();
		}

	}
	
	public static class Program extends PascalElement {

		public Program(Parent parent, String name) {
			super(parent, name);
		}

		@Override
		protected ImageDescriptor getBaseImageDescriptor() {
			return getImageDescriptorForIcon("mainprogram.gif");
		}

	}
	
	public static class Unit extends PascalElement {

		/**
		 * @param parent
		 * @param name
		 */
		public Unit(Parent parent, String name) {
			super(parent, name);
		}

		/* (non-Javadoc)
		 * @see net.sf.pascaline.internal.core.model.PascalElement#getBaseImageDescriptor()
		 */
		@Override
		protected ImageDescriptor getBaseImageDescriptor() {
			return getImageDescriptorForIcon("module.gif");
		}

	}

	public static class Procedure extends PascalElement {

		public Procedure(Parent parent, String name) {
			super(parent, name);
		}

		@Override
		protected ImageDescriptor getBaseImageDescriptor() {
			return getImageDescriptorForIcon("subroutine.gif");
		}

	}
	
	public static class Function extends PascalElement {

		public Function(Parent parent, String name) {
			super(parent, name);
		}

		@Override
		protected ImageDescriptor getBaseImageDescriptor() {
			return getImageDescriptorForIcon("function.gif");
		}

	}
	
	public static class ObjectType extends PascalElement {

		public ObjectType(Parent parent, String name) {
			super(parent, name);
		}

		@Override
		protected ImageDescriptor getBaseImageDescriptor() {
			return getImageDescriptorForIcon("class.gif");
		}

	}
}
