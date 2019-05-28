// IOnNewBoorArrivedListener.aidl
package com.fengqing.aidlapp.aidl;

import com.fengqing.aidlapp.aidl.Book;

interface IOnNewBoorArrivedListener {
    void OnNewBookArrived(in Book newBook);
}
