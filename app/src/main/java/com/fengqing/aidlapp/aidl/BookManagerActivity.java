package com.fengqing.aidlapp.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fengqing.aidlapp.R;

/**
 * @author fengqing
 * @date 2019/5/25
 * <p>
 * AIDL实现跨进程：
 * 1.两个进程 BookManagerActivity（客户端进程） 和 BookManagerService（服务端进程）
 * 2.客户端进程 的 onCreate 中通过 bindService 方法连接 服务端进程
 * 3.连接成功后 onServiceConnected()方法里会返回 服务端进程 中创建的Binder对象(IBookManager)
 * 4.接着把客户端的跨进程接口IOnNewBoorArrivedListener 通过 IBookManager的方法registerListener注册进 服务端进程的 RemoteCallbackList 变量里
 * 5.服务端进程会启动线程执行ServiceWork，每5秒会添加新书，并遍历RemoteCallbackList，回调IOnNewBoorArrivedListener的OnNewBookArrived()方法
 * 6.客户端通过Handler打印新书
 * 7.客户端销毁时，需要执行 服务端进程的Binder对象（IBookManager）unregisterListener，删除RemoteCallbackList里的接口对象，
 *   执行unbindService 解除客户端进程和服务端进程的连接
 */

public class BookManagerActivity extends AppCompatActivity {

    private static final String TAG = "BookManagerActivity";

    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.e(TAG, "receive new Book=" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };

    private IOnNewBoorArrivedListener mOnNewBookArrivedListener = new IOnNewBoorArrivedListener.Stub() {
        @Override
        public void OnNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager bookManager = (IBookManager.Stub.asInterface(service));
            try {
                mRemoteBookManager = bookManager;

                bookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
