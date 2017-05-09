package com.wuqi.dev;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Risky57 on 2017/4/27.
 */
public class Sender {
    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        Socket socket = new Socket("127.0.0.1", 7890);
        OutputStream os = socket.getOutputStream();

        while (true){
            String line = sc.nextLine();
            System.out.println("发送数据: " + line);
            os.write((line + "\n").getBytes());
            os.flush();
            if ("bye".equals(line)) break;
        }


    }
}
