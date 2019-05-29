// IBinderPool.aidl
package com.fengqing.aidlapp.binderpool;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}
