package com.fengqing.aidlapp.binderpool;

import android.os.RemoteException;

/**
 * @author fengqing
 * @date 2019/5/29
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
