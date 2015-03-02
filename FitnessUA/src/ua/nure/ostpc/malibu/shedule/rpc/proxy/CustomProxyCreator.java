package ua.nure.ostpc.malibu.shedule.rpc.proxy;

import java.util.Map;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.ProxyCreator;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * The <code>CustomProxyCreator</code> class.
 * 
 * @author Volodymyr_Semerkov
 *
 */
public class CustomProxyCreator extends ProxyCreator {
	private static final String METHOD_STR_TEMPLATE = "@Override\n"
			+ "protected <T> com.google.gwt.http.client.Request doInvoke(ResponseReader responseReader, "
			+ "String methodName, RpcStatsContext statsContext, String requestData, "
			+ "com.google.gwt.user.client.rpc.AsyncCallback<T> callback) {\n"
			+ "${method-body}" + "}\n";

	public CustomProxyCreator(JClassType serviceIntf) {
		super(serviceIntf);
	}

	@Override
	protected void generateProxyMethods(SourceWriter sourceWriter,
			SerializableTypeOracle serializableTypeOracle,
			TypeOracle typeOracle, Map<JMethod, JMethod> syncMethToAsyncMethMap) {
		super.generateProxyMethods(sourceWriter, serializableTypeOracle,
				typeOracle, syncMethToAsyncMethMap);

		StringBuilder methodBody = new StringBuilder();
		methodBody
				.append("final com.google.gwt.user.client.rpc.AsyncCallback newAsyncCallback = new "
						+ CustomAsyncCallback.class.getCanonicalName()
						+ "(callback);\n");
		methodBody
				.append("return super.doInvoke(responseReader, methodName, statsContext, requestData, newAsyncCallback);\n");

		String methodStr = METHOD_STR_TEMPLATE.replace("${method-body}",
				methodBody);
		sourceWriter.print(methodStr);
	}
}
