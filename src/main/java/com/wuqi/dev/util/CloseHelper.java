package com.wuqi.dev.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Risky57 on 2017/5/9.
 */
public class CloseHelper {

    public static void close(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
