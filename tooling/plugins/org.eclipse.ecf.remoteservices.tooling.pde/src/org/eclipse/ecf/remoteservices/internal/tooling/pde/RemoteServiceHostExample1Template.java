package org.eclipse.ecf.remoteservices.internal.tooling.pde;

import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.osgi.framework.Bundle;
import org.w3c.dom.events.Event;


public class RemoteServiceHostExample1Template extends OptionTemplateSection {

	private String packageName;
	private String Value_ecg_providers;
	
    // for testing
	int a=2;
	
/*	 public void handleEvent(Event event) {
	      setPageComplete(validatePage());
	   }*/
	
	public RemoteServiceHostExample1Template() {
	//set page count of the wizard two
		setPageCount(2);
	//list of providers
		String[][] ecf_providers = {{"0","ecf.base"},{"1","ecf.container.trivial"},{"2","ecf.generic.client"},{"3","ecf.discovery.jmdns"},{"4","ecf.generic.server"},{"5","ecf.filetransfer.bittorrent"},{"6","ecf.msn.msnp"},{"7","ecf.discovery.jslp"},{"9","ecf.msn.msnp"},{"10","ecf.xmpp.smack"}};
		addComboChoiceOption("containerID", "service.exported.configs", ecf_providers, "",0);
		//addOption("containerType", "service.exported.configs", "ecf.generic.server", 0);		
		//addOption("containerId", "ECF Generic Server URL", "", 1); 
		addOption("host", "host", "", 1);
		addOption("port", "Port", "", 1);
		addOption("path","Path","", 1);
		//TemplateOption[] option=getOptions(1);
		
		
	
	}
	
	  

	public String test(){
			
		
		return (String) getValue("$port$");
		//return props.get(12);
		
	}

	
	public void addPages(Wizard wizard) {
		
		WizardPage page = createPage(0, "org.eclipse.pde.doc.user.rcp_mail");		
		page.setTitle("Hello! Remote Service Host");
		page.setDescription("This template creates and exports a Hello remote service");		
		wizard.addPage(page);
		markPagesAdded();
		
		WizardPage page1 = createPage(1, "org.eclipse.pde.doc.user.rcp_mail");
			if ( a==2) {
				page1.setTitle("Values");
				page1.setDescription("Fill the values you need to for your remote");
				wizard.addPage(page1);
		
	//	addOption("pathh", getStringOption("$containerType$"), "",1);
		//addOption("containerId",test(), "ecftcp://localhost:3282/server", 1);
				
		}
		
		
	}


	public URL getTemplateLocation() {
		Bundle b = Activator.getDefault().getBundle();
		String path = "/templates/"+getSectionId();
		URL url = b.getEntry(path);
		if (url != null)
			try {
				return new URL(getInstallURL(), path);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		return null;
	}

	public String getSectionId() {
		
		//return "helloRemoteServiceHostExample1"; //$NON-NLS-1$
		return "helloRemoteServiceConsumerExample1"; //$NON-NLS-1$
	}

	protected void updateModel(IProgressMonitor monitor) { 
	}

	public String getUsedExtensionPoint() {
		return null;
	}

	public boolean isDependentOnParentWizard() {
		return true;
	}

	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}

	public IPluginReference[] getDependencies(String schemaVersion) {
		return new IPluginReference[0];
	}

	protected String getFormattedPackageName(String id) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < id.length(); i++) {
			char ch = id.charAt(i);
			if (buffer.length() == 0) {
				if (Character.isJavaIdentifierStart(ch))
					buffer.append(Character.toLowerCase(ch));
			} else {
				if (Character.isJavaIdentifierPart(ch) || ch == '.')
					buffer.append(ch);
			}
		}
		return buffer.toString().toLowerCase(Locale.ENGLISH);
	}


	protected void initializeFields(IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		String packageName = getFormattedPackageName(data.getId());
		initializeOption(KEY_PACKAGE_NAME, packageName);
		this.packageName = getFormattedPackageName(data.getId());
	}

	public void initializeFields(IPluginModelBase model) {
		String id = model.getPluginBase().getId();
		String packageName = getFormattedPackageName(id);
		initializeOption(KEY_PACKAGE_NAME, packageName);
		this.packageName = getFormattedPackageName(id);
	}

	public String getStringOption(String name) {
		if (name.equals(KEY_PACKAGE_NAME)) {
			return packageName;
		}
		return super.getStringOption(name);
	}

	@Override
	public String[] getNewFiles() {
		return new String[0];
	}

	@Override
	protected URL getInstallURL() {
		return Activator.getDefault().getBundle().getEntry("/");
	}

	@Override
	protected ResourceBundle getPluginResourceBundle() {
		return Platform.getResourceBundle(Activator.getDefault().getBundle());
	}

}
