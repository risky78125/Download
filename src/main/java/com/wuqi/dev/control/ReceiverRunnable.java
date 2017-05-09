package com.wuqi.dev.control;

import com.wuqi.dev.utils.CloseHelper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Risky57 on 2017/4/27.
 */
public class ReceiverRunnable implements Runnable {

    private OutputStream mOs;

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7891);
            Socket receiverSocket = serverSocket.accept();
            mOs = receiverSocket.getOutputStream();


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
//            CloseHelper.close(serverSocket);
        }
    }

    public OutputStream getOs() {
        return mOs;
    }

    public void start() {
        new Thread(this).start();
    }
}
