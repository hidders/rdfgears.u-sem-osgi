package nl.tudelft.wis.usem.plugin.osgi.admin;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.tudelft.wis.usem.plugin.admin.PluginAdmin;
import nl.tudelft.wis.usem.plugin.admin.PluginDetails;
import nl.tudelft.wis.usem.plugin.osgi.Utils;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;

public class OSGIPluginAdmin implements PluginAdmin{
	
	private Framework framework;

	public OSGIPluginAdmin(){
		try {
			framework = Utils.startFramework();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OSGI plugin framework started!");
		
	}

	@Override
	public List<PluginDetails> listPlugins() {
		try {
			BundleContext context = framework.getBundleContext();

			List<PluginDetails> boundles = new ArrayList<PluginDetails>();
			for (Bundle bundle : context.getBundles()) {
				boundles.add(new PluginDetails(bundle.getBundleId(), bundle
						.getSymbolicName(), bundle.getVersion(), "N/A"));
			}

			return boundles;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean deletePlugin(String id) {
		try {
			BundleContext context = framework.getBundleContext();

			context.getBundle(Long.valueOf(id)).uninstall();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void installPlugin(String name, InputStream plugin) throws Exception {
		BundleContext context = framework.getBundleContext();
		Bundle bundle = context.installBundle(name, plugin);
		
		framework.update();
	}
	
	@Override
	public <E> List<E> getServices(Class<E> type) {
		Object[] services;
		try {
			ServiceTracker tracker = new ServiceTracker(
					framework.getBundleContext(), type, null);
			tracker.open();
			services = tracker.getServices();

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}

		List<E> result = new ArrayList<E>();

		if (services != null) {
			for (Object obj : services) {
				result.add((E) obj);
			}
		}

		return result;
	}
	
	@Override
	public void refresh() {
		try {
			framework.stop();
			framework.waitForStop(0);
			framework.start();
			//framework = Utils.startFramework();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
