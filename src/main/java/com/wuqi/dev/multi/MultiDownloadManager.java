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

    public MultiDownloadManager(String url, int threadCount, File saved){
        if (url == null || url.equals("")) throw new NullPointerException("url must not be null");
        if (threadCount < 1) throw new IllegalArgumentException("threadCount must be > 0");
        if (saved == null) throw new NullPointerException("savedFile must not be null");
        String fileName = url.substring(url.lastIndexOf("/"), url.length());
        if (fileName.length() == 0) throw new IllegalArgumentException("Unable to get file name from url");
        if (saved.isDirectory()){
            saved.mkdirs();
            this.savedFile = new File(saved, fileName);
        }else{
            this.savedFile = saved;
        }
        this.url = url;
        this.threadCount = threadCount;
        init();
    }

    public MultiDownloadManager(String url, int threadCount, String filePath){
        this(url, threadCount, new File(filePath));
    }

    private void init() {
        if (!savedFile.exists()) {
            try {
                savedFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void startDownload() {
        Request request = new Request.Builder().url(url).build();
        Response response = HttpManager.startRequest(request);
        if (response == null) {
            return;
        }
        mContentLength = response.body().contentLength();
        response.body().close();
        long start = 0;
        // 平均分割文件的大小
        long fragment = mContentLength / threadCount;
        for (int i = 0; i < threadCount; i++) {
            // 计算每个部分的开始和结束位置
            long end;
            if (i < threadCount - 1) {
                end = fragment * (i + 1);
            } else {
                // 最后的部分的结束位置为总长度, 防止做除法取整, 文件下载不完全
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

    public static void main(String[] args){
        String url = "https://dl.google.com/dl/android/studio/ide-zips/2.4.0.6/android-studio-ide-171.3934896-mac.zip";
        String filePath = "/Users/Risky/Downloads/";
        final MultiDownloadManager manager = new MultiDownloadManager(url, 3, filePath);
        manager.setOnProgressListener(new OnProgressListener() {
            private long currentLength;
            private int lastProgress;

            // 添加synchronized为了进度计算更准确, 但是会稍微影响下载速度
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
