/*******************************************************************************
 *  Copyright (c) 2005, 2007 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.osgi;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;

/**
 * @since 1.1
 */
public class UsemPluginNewWizard extends NewPluginTemplateWizard {

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.AbstractNewPluginTemplateWizard#init(org.eclipse.pde.ui.IFieldData)
	 */
	public void init(IFieldData data) {
		super.init(data);
		setWindowTitle("U-Sem plugin template.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.NewPluginTemplateWizard#createTemplateSections()
	 */
	public ITemplateSection[] createTemplateSections() {
		return new ITemplateSection[] {new UsemTemplate()};
	}

	public String[] getImportPackages() {
		return new String[] {" nl.tudelft.rdfgears.engine,\n nl.tudelft.rdfgears.plugin,\n nl.tudelft.rdfgears.rgl.datamodel.type,\n nl.tudelft.rdfgears.rgl.datamodel.value,\n nl.tudelft.rdfgears.rgl.function,\n nl.tudelft.rdfgears.util.row"}; //$NON-NLS-1$
	}

}
