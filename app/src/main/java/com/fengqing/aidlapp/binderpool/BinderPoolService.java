package com.fengqing.aidlapp.binderpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author fengqing
 * @date 2019/5/29
 */

public class BinderPoolService extends Service {

    private Binder mBinderPool = new BinderPool.BinderPoolImpl();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinderPool;
    }
}
