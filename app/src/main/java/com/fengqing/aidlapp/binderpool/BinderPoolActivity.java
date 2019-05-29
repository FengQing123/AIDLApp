package com.fengqing.aidlapp.binderpool;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author fengqing
 * @date 2019/5/29
 */

public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dowork();
            }
        }).start();

    }

    private void dowork() {
        BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);
        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        ISecurityCenter securityCenter = SecurityCenterImpl.asInterface(securityBinder);

        Log.e(TAG, "-------ISecurityCenter");

        String msg = "helloworld-安卓";
        Log.e(TAG, "content=" + msg);
        try {
            String password = securityCenter.encrypt(msg);
            Log.e(TAG, "encrypt=" + password);

            String decrypt = securityCenter.decrypt(password);
            Log.e(TAG, "decrypt=" + decrypt);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "-------IComputer");

        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        ICompute compute = ComputeImpl.asInterface(computeBinder);
        try {
            int result = compute.add(1, 2);
            Log.e(TAG, "compute result =" + result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
