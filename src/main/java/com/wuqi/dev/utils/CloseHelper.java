package com.wuqi.dev.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Risky57 on 2017/4/27.
 */
public class CloseHelper {

    public static void close(Closeable close){
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
