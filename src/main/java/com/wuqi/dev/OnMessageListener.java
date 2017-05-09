package com.wuqi.dev;

import com.wuqi.dev.control.ReceiverRunnable;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Risky57 on 2017/4/27.
 */
public class OnMessageListener {

    private OutputStream mOs;
    private ReceiverRunnable receiver;

    public OnMessageListener(ReceiverRunnable receiver) {
        this.receiver = receiver;
        mOs = receiver.getOs();
    }

    public void read(String msg) throws IOException {
        if (mOs == null) {
            while (true){
                if (mOs != null) break;
                mOs = receiver.getOs();
            }
        }
        mOs.write(msg.getBytes());
        mOs.flush();
    }
}
