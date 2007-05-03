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

package org.eclipse.ecf.presence.ui.roster;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRosterEntry;

/**
 * Abstract contribution item class for creating menu contribution items for
 * roster entries. Subclasses should be created as appropriate.
 */
public abstract class AbstractRosterEntryContributionItem extends
		AbstractPresenceContributionItem {

	public AbstractRosterEntryContributionItem() {

	}

	public AbstractRosterEntryContributionItem(String id) {
		super(id);
	}

	/**
	 * Get the currently selected IRosterEntry.
	 * 
	 * @return IRosterEntry that is current workbenchwindow selection. Returns
	 *         <code>null</code> if nothing is selected or if something other than
	 *         IRosterEntry is selected.
	 */
	protected IRosterEntry getSelectedRosterEntry() {
		Object selection = getSelection();
		if (selection instanceof IRosterEntry)
			return (IRosterEntry) selection;
		return null;
	}

	/**
	 * Get container for the given IRosterEntry.
	 * 
	 * @param entry
	 *            the IRosterEntry. May be <code>null</code>.
	 * 
	 * @return IContainer associated with currently selected IRosterEntry.
	 *         Returns <code>null</code> if the given <code>entry</code> is
	 *         null, or if the container associated with the <code>entry</code>
	 *         cannot be accessed.
	 */
	protected IContainer getContainerForRosterEntry(IRosterEntry entry) {
		if (entry == null)
			return null;
		IPresenceContainerAdapter pca = (IPresenceContainerAdapter) entry
				.getRoster().getPresenceContainerAdapter();
		if (pca != null)
			return (IContainer) pca.getAdapter(IContainer.class);
		return null;
	}

}
