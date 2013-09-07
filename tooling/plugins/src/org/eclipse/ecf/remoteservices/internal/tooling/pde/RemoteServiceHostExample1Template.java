package org.eclipse.ecf.remoteservices.internal.tooling.pde;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
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
	private String[][] Value_ecg_providers;	
	private Properties providers=new Properties();//create the property file for the providers
	int a=1;	// for testing

/*	 public void handleEvent(Event event) {
	      setPageComplete(validatePage());
	   }*/

	public RemoteServiceHostExample1Template() {
		
	setPageCount(2);//set page count of the wizard two	
		
	//read from the property file		
		try {
			providers.load(RemoteServiceConsumerExample1Template.class.getClassLoader().getResourceAsStream("providers.properties"));
			Value_ecg_providers = fetchArrayFromPropFile("ecf_providers",providers);			
			
		} catch (FileNotFoundException e) {
			System.out.print("Property file not found");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
		addComboChoiceOption("containerID", "service.exported.configs", Value_ecg_providers, "",0);
		//addOption("containerType", "service.exported.configs", "ecf.generic.server", 0);		
		//addOption("containerId", "ECF Generic Server URL", "", 1); 
		//addOption("host", "host", "", 1);
		//addOption("port", "Port", "", 1);
		//addOption("path","Path","", 1);
		//TemplateOption[] option=getOptions(1);
		
		
	
	}
	
	
	//to get data from the property file
	private static String[][] fetchArrayFromPropFile(String propertyName, Properties propFile) {
		  String[] a = propFile.getProperty(propertyName).split(";");
		  String[][] array = new String[a.length][a.length];
		  for(int i = 0;i < a.length;i++) {
		    array[i] = a[i].split(",");
		  }
		  return array;
		}


	
	public void addPages(Wizard wizard) {
		
		WizardPage page = createPage(0, "org.eclipse.pde.doc.user.rcp_mail");		
		page.setTitle("Hello! Remote Service Host");
		page.setDescription("This template creates and exports a Hello remote service");		
		wizard.addPage(page);
		markPagesAdded();
		
		WizardPage page1 = createPage(1, "org.eclipse.pde.doc.user.rcp_mail");
			
				page1.setTitle("Values");
				page1.setDescription("Fill the values you need to for your remote");
				wizard.addPage(page1);
		
	//	addOption("path", getStringOption("$containerType$"), "",1);
		//addOption("containerId",test(), "ecftcp://localhost:3282/server", 1);
				
		
		
		
	}
	
	public IWizardPage getNextPage(IWizardPage currentPage) {
		   
	    return null;
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
