package com.wuqi.dev.multi;

import com.wuqi.dev.http.HttpManager;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

/**
 * Created by Risky57 on 2017/5/9.
 */
public class MultiDownloadManager {

    private String url;
    private int threadCount;
    private File savedFile;

    private OnProgressListener mOnProgressListener;
    private long mContentLength;

    public MultiDownloadManager(String url, int threadCount, File savedFile) throws IOException {
        if (url == null || url.equals("")) throw new NullPointerException("url must not be null");
        if (threadCount < 1) throw new IllegalArgumentException("threadCount must be > 0");
        if (savedFile == null || savedFile.isDirectory())
            throw new IllegalArgumentException("savedFile must be a file, not be a directory");
        this.url = url;
        this.threadCount = threadCount;
        this.savedFile = savedFile;
        init();
    }

    public MultiDownloadManager(String url, int threadCount, String filePath) throws IOException {
        this(url, threadCount, new File(filePath));
    }

    private void init() throws IOException {
        if (!savedFile.exists()) {
            savedFile.createNewFile();
        }
    }

    public void startDownload() throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = HttpManager.startRequest(request);
        mContentLength = response.body().contentLength();
        response.body().close();
        long start = 0;
        long fragment = mContentLength / threadCount;
        for (int i = 0; i < threadCount; i++) {
            long end;
            if (i < threadCount - 1) {
                end = fragment * (i + 1);
            } else {
                end = mContentLength;
            }
//            System.out.println("start:" + start + ", end:" + end + ", 差为: " + (end - start));
            RealDownloadThread downloadThread = new RealDownloadThread(url, savedFile, start, end);
            downloadThread.setOnProgressListener(mOnProgressListener);
            downloadThread.start();
            start = end + 1;
        }
    }

    public long getContentLength() {
        return mContentLength;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }

    public static void main(String[] args) throws IOException {
        String url = "https://dl.google.com/dl/android/studio/ide-zips/2.4.0.6/android-studio-ide-171.3934896-mac.zip";
        String filePath = "/Users/Risky/Downloads/AndroidStudio.zip";
        final MultiDownloadManager manager = new MultiDownloadManager(url, 3, filePath);
        manager.setOnProgressListener(new OnProgressListener() {
            private long currentLength;
            private int lastProgress;

            public synchronized void onUpdate(int progress) {
                currentLength += progress;
                int pro = (int) (currentLength * 100f / manager.getContentLength());
                if (pro != lastProgress) {
                    lastProgress = pro;
                    System.out.println("当前下载进度为: " + pro + "%");
                }

            }
        });
        manager.startDownload();
    }
}
