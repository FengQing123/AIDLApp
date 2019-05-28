// IBookManager.aidl
package com.fengqing.aidlapp.aidl;

import com.fengqing.aidlapp.aidl.Book;
import com.fengqing.aidlapp.aidl.IOnNewBoorArrivedListener;

interface IBookManager {
   List<Book> getBookList();
   void addBook(in Book book);
   void registerListener(IOnNewBoorArrivedListener listener);
   void unregisterListener(IOnNewBoorArrivedListener listener);
}
