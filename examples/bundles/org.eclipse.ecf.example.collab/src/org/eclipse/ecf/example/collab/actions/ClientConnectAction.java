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

package org.eclipse.ecf.example.collab.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.example.collab.Client;
import org.eclipse.ecf.example.collab.ClientPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ClientConnectAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
    
    public static final String DEFAULT_SERVER_ID = "ecftcp://localhost:3282//server";

    public void setData(Object data) {
        this.data = data;
    }
    public void setProject(IProject project) {
        this.project = project;
        if (project==null) {
            projectName = "<workspace>";
        } else {
            projectName = project.getName();
        }
    }
    public void setTargetID(ID targetID) {
        this.targetID = targetID;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setContainerType(String type) {
        this.containerType = type;
    }
    protected String containerType = null;
    protected ID targetID = null;
    protected String username = null;
    protected Object data = null;
    protected IProject project = null;
    protected String projectName = null;
    
	public ClientConnectAction() {
        try {
            targetID = IDFactory.makeStringID(DEFAULT_SERVER_ID);
        } catch (Exception e) {
            
        }
	}
    
    public ClientConnectAction(ID targetID, String username, Object data, IProject project) {
        this.targetID = targetID;
        this.username = username;
        this.data = data;
        setProject(project);
    }
	public class ClientConnectJob extends Job {
        public ClientConnectJob(String name) {
            super(name);
        }
        public IStatus run(IProgressMonitor pm) {
            Client ct = null;
            try {
                ct = new Client();
                // Make sure we're disconnected
                if (ct.isConnected()) {
                    ct.disposeClient();
                }
                // Actually create and connect client
                ct.createAndConnectClient(containerType,targetID,username,data,project);
                return new Status(IStatus.OK,ClientPlugin.PLUGIN_ID,IStatus.OK,"Connected",null);
            } catch (Exception e) {
                if (ct != null) {
                    ct.disposeClient();
                }
                return new Status(IStatus.ERROR,ClientPlugin.PLUGIN_ID,IStatus.OK,"Could not connect to group '"+targetID.getName()+"'",e);
            }
        }        
    }
	public void run(IAction action) {
        ClientConnectJob clientConnect = new ClientConnectJob("Collaboration for '"+projectName+"'");
        clientConnect.schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}