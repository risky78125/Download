package com.wuqi.dev.control;

import com.wuqi.dev.OnMessageListener;
import com.wuqi.dev.utils.CloseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Risky57 on 2017/4/27.
 */
public class ControlRunnable implements Runnable {

    private OnMessageListener mListener;

    public ControlRunnable(OnMessageListener listener) {
        mListener = listener;
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7890);
            Socket controllerSocket = serverSocket.accept();
            InputStream is = controllerSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null){
                System.out.println("Main接收数据: " + line);
                mListener.read(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            CloseHelper.close(serverSocket);
        }
    }

    public void start(){
        new Thread(this).start();
    }

}
