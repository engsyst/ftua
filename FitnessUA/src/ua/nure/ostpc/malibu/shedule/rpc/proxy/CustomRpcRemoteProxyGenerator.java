package ua.nure.ostpc.malibu.shedule.rpc.proxy;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.rpc.ProxyCreator;
import com.google.gwt.user.rebind.rpc.ServiceInterfaceProxyGenerator;

/**
 * The <code>CustomRpcRemoteProxyGenerator</code> class.
 * 
 * @author Volodymyr_Semerkov
 *
 */
public class CustomRpcRemoteProxyGenerator extends
		ServiceInterfaceProxyGenerator {

	@Override
	protected ProxyCreator createProxyCreator(JClassType remoteService) {
		return new CustomProxyCreator(remoteService);
	}
}
