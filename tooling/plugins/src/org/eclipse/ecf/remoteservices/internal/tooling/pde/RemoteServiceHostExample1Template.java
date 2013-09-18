package org.eclipse.ecf.remoteservices.internal.tooling.pde;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


class RS_wizard_page1 extends WizardPage {
	
	private Text host;
	private Text port;
	private Text path;
	public String server_url;
	private Composite container;
	private Object user_select_value;
	
		public RS_wizard_page1(Object user_select_value ) {
		super("Hello Remote Service Host");
		this.user_select_value = user_select_value;
		setTitle("Hello Remote Service Host");
		setDescription("Enter data for your server");	
		
	}

	@Override
	public void createControl(Composite parent) {
		
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(new GridLayout(1,false));
		layout.numColumns = 1;
		
		Label example = new Label(container, SWT.NONE);		
		example.setText("Example             ecftcp://localhost:3282/server");

		
		Label Host = new Label(container, SWT.NONE);
		Host.setText("Host");
		host = new Text(container, SWT.BORDER | SWT.SINGLE);
		host.setText("");
		
		Label Port = new Label(container, SWT.NONE);
		Port.setText("Port");
		port = new Text(container, SWT.BORDER| SWT.SINGLE);
		port.setText("");
		
		Label Path = new Label(container, SWT.NONE);		
		Path.setText("Path");
		path = new Text(container, SWT.BORDER| SWT.SINGLE);
		path.setText("");
		
		
		
		host.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!host.getText().isEmpty()) {
					setPageComplete(true);

				}
			}

		});
		
		port.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {			
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (!port.getText().isEmpty()) {
					setPageComplete(true);

				}
				
			}
		});
		
		path.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
								
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (!path.getText().isEmpty()) {
					setPageComplete(true);

				}
			}
		});
		
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		host.setLayoutData(gd);
		port.setLayoutData(gd);
		path.setLayoutData(gd);
		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	public String getText1() {
		return host.getText();
	}

	public String getText2() {
		return port.getText();
	}

	public String getText3() {
		return path.getText();
	}
//to get the full server url
	public String getServer_url() {
		return server_url;
	}

	public void setServer_url(String server_url) {
		this.server_url = server_url;
		server_url= getText1()+getText2()+getText3();
	}
	
	@Override
	public IWizardPage getNextPage() {
		// TODO Auto-generated method stub
		return super.getNextPage();
	}
	
	
	
}


public class RemoteServiceHostExample1Template extends OptionTemplateSection {

	public String packageName;

	public String[][] Value_ecg_providers;
	public Properties providers = new Properties();// create the property file
													// for the providers
	public Object user_select_value;

	
	public RemoteServiceHostExample1Template() {
		setPageCount(3);
		
	// read from the property file
		try {
			providers.load(RemoteServiceConsumerExample1Template.class
					.getClassLoader().getResourceAsStream(
							"providers.properties"));
			Value_ecg_providers = fetchArrayFromPropFile("ecf_providers",
					providers);

		} catch (FileNotFoundException e) {
			System.out.print("Property file not found");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// to get data from the property file
	private static String[][] fetchArrayFromPropFile(String propertyName,
			Properties propFile) {
		String[] a = propFile.getProperty(propertyName).split(";");
		String[][] array = new String[a.length][a.length];
		for (int i = 0; i < a.length; i++) {
			array[i] = a[i].split(",");
		}
		return array;
	}

	public void addPages(Wizard wizard) {
		
		//creation of first page
		WizardPage page = createPage(0, "org.eclipse.pde.doc.user.rcp_mail");
		page.setTitle("Hello Remote Service Host");
		page.setDescription("This template creates and exports a Hello remote service");
		addComboChoiceOption("containerID", "service.exported.configs",
				Value_ecg_providers, "", 0);	
		 
		wizard.addPage(page);
		
				
		RS_wizard_page1 one;
		one = new RS_wizard_page1(user_select_value);
		wizard.addPage(one);
		
		
		markPagesAdded();
		System.out.println("d"+getValue("containerID"));
		
	}
	
	


	// set the user value
	public void setUser_select_value(Object user_select_value) {
		this.user_select_value = user_select_value;
		user_select_value = getValue("containerId");
	}
	// get the value set by user
		public Object getUser_select_value() {
			return user_select_value;
		}

	public URL getTemplateLocation() {
		Bundle b = Activator.getDefault().getBundle();
		String path = "/templates/" + getSectionId();
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
		return "helloRemoteServiceHostExample1"; //$NON-NLS-1$
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