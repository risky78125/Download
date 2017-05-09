package com.wuqi.dev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Risky57 on 2017/4/27.
 */
public class Receiver {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("127.0.0.1", 7891);
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null){
            System.out.println("接收到数据: " + line);
        }


    }
}
