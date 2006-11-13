/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.ui;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Required interface for implementing <b>org.eclipse.ecf.ui.connectWizards</b>
 * extension point. Extensions for extension point
 * <b>org.eclipse.ecf.ui.connectWizards</b> must provide a class implementing
 * this interface.
 */
public interface IConnectWizard extends IWizard {

	/**
	 * Initialize the connect wizard.
	 * 
	 * @param workbench
	 *            the currently working workbench instance. Will not be null.
	 * @param description
	 *            the ContainerTypeDescription of the container instance. Will
	 *            not be null.
	 * @param container
	 *            the container that is to be connected. Will not be null.
	 */
	public void init(IWorkbench workbench,
			ContainerTypeDescription description, IContainer container);
}
