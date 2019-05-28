package com.fengqing.aidlapp.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * @author fengqing
 * @date 2019/5/28
 */

public class TCPServerService extends Service {

    private static final String TAG = "TCPServerService";
    private boolean isServiceDestroyed;

    private String[] messages = new String[]{
            "你好啊，哈哈哈",
            "你是谁？",
            "你想要干嘛",
            "快离开",
            "哈哈哈，吓到你了吧"
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        isServiceDestroyed = true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                Log.e(TAG, "establish tcp server failed ,port is 8688");
                e.printStackTrace();
                return;
            }

            while (!isServiceDestroyed) {
                try {
                    final Socket client = serverSocket.accept();
                    Log.e(TAG, "accept");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws Exception {
        //用于接收客户端消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //用于向客户端发送消息
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        out.println("欢迎来到聊天室");
        while (!isServiceDestroyed) {
            String str = in.readLine();
            System.out.println("msg from client:" + str);
            if (str == null) {
                //客户端断开连接
                break;
            }
            int i = new Random().nextInt(messages.length);
            String msg = messages[i];
            out.println(msg);
            System.out.println("send:" + msg);
        }
        System.out.println("client quit");
        out.close();
        in.close();
        client.close();
    }
}
