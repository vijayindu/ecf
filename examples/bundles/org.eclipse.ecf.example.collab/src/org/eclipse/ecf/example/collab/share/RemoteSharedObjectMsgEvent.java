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

package org.eclipse.ecf.example.collab.share;

import org.eclipse.ecf.core.events.RemoteSharedObjectEvent;
import org.eclipse.ecf.core.identity.ID;

/**
 * 
 */
public class RemoteSharedObjectMsgEvent extends RemoteSharedObjectEvent {
    /**
     * @param senderObj
     * @param remoteCont
     * @param data
     */
    public RemoteSharedObjectMsgEvent(ID senderObj, ID remoteCont, SharedObjectMsg msg) {
        super(senderObj, remoteCont, msg);
    }
    
    public SharedObjectMsg getMsg() {
        return (SharedObjectMsg) super.getData();
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer("RemoteSharedObjectMsgEvent[");
        buf.append(getSenderSharedObjectID()).append(";").append(getRemoteContainerID()).append(";").append(getMsg());
        buf.append("]");
        return buf.toString();
    }
}
