package xyz.dsvshx.collie.core.adaptor.rpc;

import xyz.dsvshx.collie.core.adaptor.ClassAdaptor;

/**
 * @author dongzhonghua
 * Created on 2021-05-26
 */
public class RpcFrameworkAdaptorImpl extends ClassAdaptor {
    @Override
    public byte[] modifyClass(String className, byte[] classBytes, String spyJarPath) {
        return classBytes;
    }
}
