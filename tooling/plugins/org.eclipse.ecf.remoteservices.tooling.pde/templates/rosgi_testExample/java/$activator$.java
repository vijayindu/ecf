package $packageName$;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.ecf.osgi.services.distribution.IDistributionConstants;
import org.eclipse.ecf.tests.remoteservice.r_osgi.AbstractConcatHostApplication;
import org.eclipse.ecf.tests.remoteservice.r_osgi.R_OSGi;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.eclipse.ecf.examples.remoteservices.hello.HelloMessage;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;

@SuppressWarnings("restriction")
public class $activator$ implements BundleActivator {

	// This is the hello service implementation
	public class ROsgiConcatHostApplication extends
	AbstractConcatHostApplication {

   public String getContainerType() {
	return R_OSGi.HOST_CONTAINER_TYPE;
}

}


	

	public void start(BundleContext context) throws Exception {
		
		Dictionary<String,Object> props = new Hashtable<String,Object>();
		// add OSGi service property indicated export of all interfaces exposed
		// by service (wildcard)
		props.put("service.exported.interfaces", "*");
		// add OSGi service property specifying config
		
		props.put("service.exported.configs", "$containerType$");
		// add ECF service property specifying container factory args
		props.put(id,"$containerId$");
		
		
	}

	

}
