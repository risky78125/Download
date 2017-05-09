package com.wuqi.dev.multi;

import com.wuqi.dev.http.HttpManager;
import com.wuqi.dev.util.CloseHelper;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by Risky57 on 2017/5/9.
 */
public class RealDownloadThread implements Runnable {

    private String url;
    private File file;
    private long start;
    private long end;

    private OnProgressListener mOnProgressListener;

    public RealDownloadThread(String url, File file, long start, long end) {
        this.url = url;
        this.file = file;
        this.start = start;
        this.end = end;
    }

    public void run() {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=" + start + "-" + end)
                .build();
        Response response = null;
        RandomAccessFile access = null;
        try {
            response = HttpManager.startRequest(request);
            InputStream is = response.body().byteStream();
            access = new RandomAccessFile(file, "rw");
            // 从start的位置开始写入数据
            access.seek(start);
            byte [] bytes = new byte[1024];
            int length = -1;
            while ((length = is.read(bytes)) != -1){
                access.write(bytes, 0, length);
                if (mOnProgressListener != null) {
                    mOnProgressListener.onUpdate(length);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                CloseHelper.close(response.body());
            }
            CloseHelper.close(access);
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }
}
