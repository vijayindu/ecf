/*******************************************************************************
 * Copyright (c) 2008 Jan S. Rellermeyer, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan S. Rellermeyer - initial API and implementation
 ******************************************************************************/

package org.eclipse.ecf.internal.provider.r_osgi;

import ch.ethz.iks.r_osgi.RemoteOSGiException;
import java.lang.reflect.*;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallStartEvent;
import org.eclipse.equinox.concurrent.future.*;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.ServiceException;

/**
 * The R-OSGi adapter implementation of the IRemoteService interface.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
final class RemoteServiceImpl implements IRemoteService, InvocationHandler {

	static final Object[] EMPTY_PARAMETERS = new Object[0];

	// the ECF remote refImpl
	RemoteServiceReferenceImpl refImpl;

	// the service object.
	Object service;

	// the next free service ID.
	private long nextID;

	/**
	 * constructor.
	 * 
	 * @param service
	 *            the service (proxy) object.
	 */
	public RemoteServiceImpl(final RemoteServiceReferenceImpl refImpl, final Object service) {
		this.refImpl = refImpl;
		this.service = service;
	}

	/**
	 * call the service asynchronously.
	 * 
	 * @param call
	 *            the call object.
	 * @param listener
	 *            the callback listener.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callAsync(org.eclipse.ecf.remoteservice.IRemoteCall,
	 *      org.eclipse.ecf.remoteservice.IRemoteCallListener)
	 */
	public void callAsync(final IRemoteCall call, final IRemoteCallListener listener) {
		new AsyncResult(call, listener).start();
	}

	/**
	 * call the service asynchronously.
	 * 
	 * @param call
	 *            the call object.
	 * @return the result proxy.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callAsync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
	public IFuture callAsync(final IRemoteCall call) {
		final AbstractExecutor executor = new ThreadsExecutor();
		return executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return callSync(call);
			}
		}, null);
	}

	/**
	 * call the service synchronously.
	 * 
	 * @param call
	 *            the call object.
	 * @return the result or <code>null</code>
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callSync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
	public Object callSync(final IRemoteCall call) throws ECFException {
		Object[] ps = call.getParameters();
		final Object[] parameters = (ps == null) ? EMPTY_PARAMETERS : ps;
		final Class[] formalParams = new Class[parameters.length];
		for (int i = 0; i < formalParams.length; i++) {
			formalParams[i] = call.getParameters()[i].getClass();
		}
		IExecutor executor = new ThreadsExecutor();
		IFuture future = executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				final Method method = getMethod(service.getClass(), call.getMethod(), formalParams);
				return method.invoke(service, parameters);
			}
		}, null);
		Object result = null;
		try {
			result = future.get(call.getTimeout());
		} catch (OperationCanceledException e) {
			throw new ECFException("callSync cancelled", e); //$NON-NLS-1$
		} catch (InterruptedException e) {
			// If thread interrupted, then just return null
			return null;
		} catch (TimeoutException e) {
			throw new ECFException(NLS.bind("callSync timed out after {0} ms", Long.toString(call.getTimeout())), new TimeoutException(call.getTimeout())); //$NON-NLS-1$
		}
		IStatus status = future.getStatus();
		if (!status.isOK())
			throw new ECFException("Exception during callSync", status.getException()); //$NON-NLS-1$
		return result;
	}

	/**
	 * fire an asynchronous call without getting the result returned.
	 * 
	 * @param call
	 *            the call object.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#fireAsync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
	public void fireAsync(final IRemoteCall call) throws ECFException {
		try {
			callAsync(call);
		} catch (RemoteOSGiException r) {
			throw new ECFException(r);
		} catch (Throwable t) {
			// do not rethrow
		}
	}

	/**
	 * get the service proxy object.
	 * 
	 * @return the service proxy object.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#getProxy()
	 */
	public Object getProxy() throws ECFException {
		if (!refImpl.getR_OSGiServiceReference().isActive()) {
			throw new ECFException("Container currently not connected"); //$NON-NLS-1$
		}
		Object proxy;
		try {
			// Get clazz from reference
			final String[] clazzes = refImpl.getR_OSGiServiceReference().getServiceInterfaces();
			List classes = new ArrayList();
			for (int i = 0; i < clazzes.length; i++) {
				Class c = Class.forName(clazzes[i]);
				classes.add(c);
				// check to see if async remote service proxy interface is defined
				Class asyncRemoteServiceProxyClass = findAsyncRemoteServiceProxyClass(c);
				if (asyncRemoteServiceProxyClass != null && asyncRemoteServiceProxyClass.isInterface())
					classes.add(asyncRemoteServiceProxyClass);
			}
			// add IRemoteServiceProxy interface to set of interfaces supported by this proxy
			classes.add(IRemoteServiceProxy.class);
			proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), (Class[]) classes.toArray(new Class[] {}), this);
		} catch (final Exception e) {
			throw new ECFException("Exception creating proxy for remote service", e); //$NON-NLS-1$
		}
		return proxy;
	}

	protected Class findAsyncRemoteServiceProxyClass(Class c) {
		String sourceName = c.getName();
		String asyncRemoteServiceProxyClassname = sourceName + IAsyncRemoteServiceProxy.ASYNC_INTERFACE_SUFFIX;
		try {
			return Class.forName(asyncRemoteServiceProxyClassname);
		} catch (Exception t) {
			return null;
		} catch (NoClassDefFoundError e) {
			return null;
		}
	}

	/**
	 * get the next call id.
	 * 
	 * @return the next call id.
	 */
	synchronized long getNextID() {
		return nextID++;
	}

	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		try {
			// methods declared by Object
			if (method.getName().equals("toString")) { //$NON-NLS-1$
				final String[] clazzes = refImpl.getR_OSGiServiceReference().getServiceInterfaces();
				String proxyClass = (clazzes.length == 1) ? clazzes[0] : Arrays.asList(clazzes).toString();
				return proxyClass + ".proxy@" + refImpl.getID(); //$NON-NLS-1$
			} else if (method.getName().equals("hashCode")) { //$NON-NLS-1$
				return new Integer(hashCode());
			} else if (method.getName().equals("equals")) { //$NON-NLS-1$
				if (args == null || args.length == 0)
					return Boolean.FALSE;
				try {
					return new Boolean(Proxy.getInvocationHandler(args[0]).equals(this));
				} catch (IllegalArgumentException e) {
					return Boolean.FALSE;
				}
				// This handles the use of IRemoteServiceProxy.getRemoteService method
			} else if (method.getName().equals("getRemoteService")) { //$NON-NLS-1$
				return this;
			} else if (method.getName().equals("getRemoteServiceReference")) { //$NON-NLS-1$
				return refImpl;
			}
			// If the method's class is a subclass of IAsyncRemoteServiceProxy, then we assume
			// that the methods are intended to be invoked asynchronously
			if (Arrays.asList(method.getDeclaringClass().getInterfaces()).contains(IAsyncRemoteServiceProxy.class))
				return checkAndCallAsync(method, args);
			return this.callSync(new IRemoteCall() {

				public String getMethod() {
					return method.getName();
				}

				public Object[] getParameters() {
					return (args == null) ? EMPTY_PARAMETERS : args;
				}

				public long getTimeout() {
					return DEFAULT_TIMEOUT;
				}
			});
		} catch (Throwable t) {
			// rethrow as service exception
			throw new ServiceException("Service exception on remote service proxy rsid=" + refImpl.getID(), ServiceException.REMOTE, t); //$NON-NLS-1$
		}
	}

	/**
	 * @since 3.3
	 */
	protected class AsyncArgs {
		private IRemoteCallListener listener;
		private Object[] args;

		public AsyncArgs(IRemoteCallListener listener, Object[] originalArgs) {
			this.listener = listener;
			if (this.listener != null) {
				int asynchArgsLength = originalArgs.length - 1;
				this.args = new Object[asynchArgsLength];
				System.arraycopy(originalArgs, 0, args, 0, asynchArgsLength);
			} else
				this.args = originalArgs;
		}

		public IRemoteCallListener getListener() {
			return listener;
		}

		public Object[] getArgs() {
			return args;
		}
	}

	/**
	 * @since 3.3
	 */
	protected Object checkAndCallAsync(final Method method, final Object[] args) throws Throwable {
		final String invokeMethodName = getAsyncInvokeMethodName(method);
		final AsyncArgs asyncArgs = getAsyncArgs(method, args);
		IRemoteCallListener listener = asyncArgs.getListener();
		IRemoteCall call = new IRemoteCall() {
			public String getMethod() {
				return invokeMethodName;
			}

			public Object[] getParameters() {
				return asyncArgs.getArgs();
			}

			public long getTimeout() {
				return DEFAULT_TIMEOUT;
			}
		};
		if (listener == null)
			return callAsync(call);
		callAsync(call, listener);
		return null;
	}

	/**
	 * @since 3.3
	 */
	protected AsyncArgs getAsyncArgs(Method method, Object[] args) {
		IRemoteCallListener listener = null;
		Class returnType = method.getReturnType();
		// If the return type is declared to be *anything* except an IFuture, then 
		// we are expecting the last argument to be an IRemoteCallListener
		if (!returnType.equals(IFuture.class)) {
			// If the provided args do *not* include an IRemoteCallListener then we have a problem
			if (args == null || args.length == 0)
				throw new IllegalArgumentException("Async calls must include a IRemoteCallListener instance as the last argument"); //$NON-NLS-1$
			// Get the last arg
			Object lastArg = args[args.length - 1];
			// If it's an IRemoteCallListener implementer directly, then just cast and return
			if (lastArg instanceof IRemoteCallListener) {
				listener = (IRemoteCallListener) lastArg;
			}
			// If it's an implementation of IAsyncCallback, then create a new listener based upon 
			// callback and return
			if (lastArg instanceof IAsyncCallback) {
				listener = new CallbackRemoteCallListener((IAsyncCallback) lastArg);
			}
			// If the last are is not an instance of IRemoteCallListener then there is a problem
			if (listener == null)
				throw new IllegalArgumentException("Last argument must be an instance of IRemoteCallListener"); //$NON-NLS-1$
		}
		return new AsyncArgs(listener, args);
	}

	/**
	 * @since 3.3
	 */
	protected String getAsyncInvokeMethodName(Method method) {
		String methodName = method.getName();
		return methodName.endsWith(IAsyncRemoteServiceProxy.ASYNC_METHOD_SUFFIX) ? methodName.substring(0, methodName.length() - IAsyncRemoteServiceProxy.ASYNC_METHOD_SUFFIX.length()) : methodName;
	}

	/**
	 * @param aClass The Class providing method under question (Must not be null)
	 * @param aMethodName The method name to search for (Must not be null)
	 * @param someParameterTypes Method arguments (May be null or parameters)
	 * @return A match. If more than one method matched (due to overloading) an abitrary match is taken
	 * @throws NoSuchMethodException If a match cannot be found
	 */
	Method getMethod(final Class aClass, String aMethodName, final Class[] someParameterTypes) throws NoSuchMethodException {
		// no args makes matching simple
		if (someParameterTypes == null || someParameterTypes.length == 0) {
			return aClass.getMethod(aMethodName, null);
		}

		// match parameters to determine callee
		final Method[] methods = aClass.getMethods();
		final int parameterCount = someParameterTypes.length;
		aMethodName = aMethodName.intern();

		OUTER: for (int i = 0; i < methods.length; i++) {
			Method candidate = methods[i];
			String candidateMethodName = candidate.getName().intern();
			Class[] candidateParameterTypes = candidate.getParameterTypes();
			int candidateParameterCount = candidateParameterTypes.length;
			if (candidateParameterCount == parameterCount && aMethodName == candidateMethodName) {
				for (int j = 0; j < candidateParameterCount; j++) {
					Class clazzA = candidateParameterTypes[j];
					Class clazzB = someParameterTypes[j];
					if (!clazzA.isAssignableFrom(clazzB)) {
						continue OUTER;
					}
				}
				return candidate;
			}
		}
		// if no match has been found, fail with NSME
		throw new NoSuchMethodException("No such method: " + aMethodName + "(" + Arrays.asList(someParameterTypes) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * inner class implementing the asynchronous result object. This
	 * implementation also provides the calling infrastructure.
	 */
	private class AsyncResult extends Thread {

		// the result of the call.
		Object result;

		// the exception, if any happened during the call.
		Throwable exception;

		// the remote call object.
		IRemoteCall call;

		// the callback listener, if provided.
		private IRemoteCallListener listener;

		// constructor
		AsyncResult(final IRemoteCall call, final IRemoteCallListener listener) {
			this.call = call;
			this.listener = listener;
		}

		// the call happens here.
		public void run() {
			Object r = null;
			Throwable e = null;

			final long reqID = getNextID();

			if (listener != null) {
				listener.handleEvent(new IRemoteCallStartEvent() {
					public IRemoteCall getCall() {
						return call;
					}

					public IRemoteServiceReference getReference() {
						return refImpl;
					}

					public long getRequestId() {
						return reqID;
					}
				});
			}

			try {
				r = callSync(call);
			} catch (Throwable t) {
				e = t;
			}

			synchronized (AsyncResult.this) {
				result = r;
				exception = e;
				AsyncResult.this.notify();
			}

			if (listener != null) {
				listener.handleEvent(new IRemoteCallCompleteEvent() {

					public Throwable getException() {
						return exception;
					}

					public Object getResponse() {
						return result;
					}

					public boolean hadException() {
						return exception != null;
					}

					public long getRequestId() {
						return reqID;
					}
				});
			}
		}
	}
}
