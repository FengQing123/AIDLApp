package com.fengqing.aidlapp.socket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fengqing.aidlapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author fengqing
 * @date 2019/5/28
 */

public class TCPClientActivity extends AppCompatActivity {

    private static final String TAG = "TCPClientActivity";

    public static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    public static final int MESSAGE_SOCKET_CONNECTED = 2;

    private TextView mMsgTextView;
    private EditText mEditView;
    private Button mBtnSend;
    private Socket mClientSocket;
    private PrintWriter mPrintWriter;

    @SuppressLint("HandlerLink")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    mMsgTextView.setText(mMsgTextView.getText() + (String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    mBtnSend.setText("可发送");
                    break;
                default:
                    break;
            }
        }
    };


    private class MsgTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            mPrintWriter.println(strings);
            return null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);

        mMsgTextView = findViewById(R.id.tv_msg);
        mEditView = findViewById(R.id.edit_msg);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mEditView.getText().toString();
                if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                    MsgTask msgTask = new MsgTask();
                    msgTask.execute(msg);
                    mEditView.setText("");
                    String showMsg = "self:" + msg + "\n";
                    Log.e(TAG, "self:" + msg);
                    mMsgTextView.setText(mMsgTextView.getText() + showMsg);
                }
            }
        });

        Intent intent = new Intent(this, TCPServerService.class);
        startService(intent);

        new Thread() {
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void connectTCPServer() {
        Socket socket = null;

        while (socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                System.out.println("connect server success--------------");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                System.out.println("connect tcp server failed , retry....");
            }
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TCPClientActivity.this.isFinishing()) {
                String msg = br.readLine();
                Log.e(TAG,"server:"+msg);

                String showMsg = "serve:" + msg + "\n";
                mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showMsg).sendToTarget();

            }
            System.out.println("quit....");
            mPrintWriter.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
