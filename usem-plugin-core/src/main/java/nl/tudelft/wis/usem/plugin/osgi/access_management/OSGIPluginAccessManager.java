package nl.tudelft.wis.usem.plugin.osgi.access_management;

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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.wis.usem.plugin.access_management.PluginAccessManager;
import nl.tudelft.wis.usem.plugin.osgi.Utils;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;

public class OSGIPluginAccessManager implements PluginAccessManager {
	
	private Framework framework;

	public OSGIPluginAccessManager(){
		System.out.println("OSGI plugin access framework started!");
		try {
			createFramework();
		} catch (BundleException e) {
			e.printStackTrace();
		}
	}

	private void createFramework() throws BundleException {
		
		framework = Utils.startFramework();

		BundleContext context = framework.getBundleContext();

		for (Bundle bundle : context.getBundles()) {
			bundle.start();
		}
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
			createFramework();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
