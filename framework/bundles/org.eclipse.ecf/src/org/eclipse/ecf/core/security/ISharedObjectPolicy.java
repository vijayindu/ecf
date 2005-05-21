/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/

package org.eclipse.ecf.core.security;

import java.security.PermissionCollection;

import org.eclipse.ecf.core.SharedObjectDescription;
import org.eclipse.ecf.core.identity.ID;

public interface ISharedObjectPolicy extends IContainerPolicy {
	/**
	 * Check the request to add a shared object from external source (i.e. remote container).
	 * 
	 * @param fromID the ID of the container making the container add request
	 * @param toID the ID of the container receiving/handling the add request
	 * @param newObject the shared object description associated with the shared object being added
	 * @return PermissionCollection the permission collection associated with successful acceptance
	 * of the add request.  Null if the add should be refused, Non-null if add request is accepted.
	 * @throws SecurityException if request should be refused <b>and</b> associated container should leave group
	 */
	public PermissionCollection checkAddSharedObject(ID fromID, ID toID, SharedObjectDescription newObject) throws SecurityException;
}
