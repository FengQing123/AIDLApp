package com.fengqing.aidlapp.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fengqing
 * @date 2019/5/25
 */

public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";

    /**
     * 因为AIDL方法是在服务端的Binder线程池中执行的，
     * 当多个客户端同时连接的时候，会存在多个线程同时访问的情形，
     * 所以要在AIDL方法中处理线程同步
     * 而CopyOnWriteArrayList支持并发读和写，类似的还有ConcurrentHashMap
     */
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    /**
     * RemoteCallbackList 是系统专门提供的用于删除跨进程 listener 的接口(使用CopyOnWriteArrayList无法做的删除已注册的IOnNewBoorArrivedListener对象)
     * 在客户端生成的同一个对象跨进程传输到服务端时，在服务端会生成不同的对象，但是它们底层的Binder对象是同一个,
     * RemoteCallbackList的内部有一个Map,而map的key正好是Binder,所以可以实现解注册listener
     * RemoteCallbackList还可以做到在客户端进程终止后，能够自动移除客户端注册的listener
     * RemoteCallbackList 内部还实现了线程同步功能，所以用它来注册和解注册时，无需做额外的线程同步工作
     * 需要注意的是遍历RemoteCallbackList时，beginBroadcast和finishBroadcast必须配合使用。
     */
    private RemoteCallbackList<IOnNewBoorArrivedListener> mListenerList = new RemoteCallbackList<>();

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));

        new Thread(new ServiceWork()).start();
    }

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBoorArrivedListener listener) throws RemoteException {
            //添加客户端传输过来的listener
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBoorArrivedListener listener) throws RemoteException {
            //删除客户端传输过来的listener
            mListenerList.unregister(listener);
        }
    };

    private class ServiceWork implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book book = new Book(bookId, "new Book#" + bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);

        /**
         * 新书到来时，遍历客户端的 listener 发送通知
         * beginBroadcast 和 finishBroadcast 要配合使用
         */
        int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBoorArrivedListener listener = mListenerList.getBroadcastItem(i);
            if (listener != null) {
                listener.OnNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
