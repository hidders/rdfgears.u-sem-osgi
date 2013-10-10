package $packageName$;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import nl.tudelft.rdfgears.plugin.FunctionDescriptor;
import nl.tudelft.rdfgears.plugin.WorkflowTemplate;

public class $activator$ implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		try {
			context.registerService(FunctionDescriptor.class, new FunctionDescriptor(getClass().getResourceAsStream("/functions/$function$.xml"), $function$.class), null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			context.registerService(WorkflowTemplate.class, new WorkflowTemplate(getClass().getResourceAsStream("/templates/$workflow$.xml")), null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
