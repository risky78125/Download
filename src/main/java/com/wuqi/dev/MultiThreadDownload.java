package com.wuqi.dev;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Risky57 on 2017/5/9.
 */
public class MultiThreadDownload implements Runnable {

    private static final String URL_STUDIO = "https://dl.google.com/dl/android/studio/ide-zips/2.4.0.6/android-studio-ide-171.3934896-mac.zip";
    private static final String PATH_STUDIO = "/Users/Risky/Downloads";
    private static final String NAME_FILE_STUDIO = "AndroidStudio.zip";
    private static final int COUNT_THREAD = 5;
    private static OkHttpClient sClient;
    private static File sFile;
    private final File file;

    private long start, end;
    private static long sContentLength;

    public MultiThreadDownload(File file, long start, long end) {
        this.start = start;
        this.end = end;
        this.file = file;
    }

    private static long currentLength = 0;

    private static OnProgressListener mOnProgressListener = new OnProgressListener() {
        private int lastPro;
        public void onUpdate(long progress) {
            currentLength += progress;
            int currentProgress = (int) (currentLength * 100f / sContentLength);
            if (currentProgress != lastPro){
                lastPro = currentProgress;
                System.out.println("当前下载进度为: " + currentProgress + "%");
            }

        }
    };

    public void run() {
        Request request = new Request.Builder()
                .url(URL_STUDIO)
                .header("Range", "bytes=" + start + "-" + end)
                .build();
        try {
            Response response = sClient.newCall(request).execute();
            RandomAccessFile access = new RandomAccessFile(file, "rwd");
            InputStream inputStream = response.body().byteStream();
            byte[] bytes = new byte[1024];
            int length = -1;
            access.seek(start);
            while ((length = inputStream.read(bytes)) != -1) {
                access.write(bytes, 0, length);
                if (mOnProgressListener != null) {
                    mOnProgressListener.onUpdate(length);
                }
            }
            response.body().close();
            access.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public static void main(String[] args) throws IOException {
        sClient = new OkHttpClient.Builder().build();
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        sFile = new File(PATH_STUDIO, NAME_FILE_STUDIO);
        if (!sFile.exists()) {
            sFile.createNewFile();
        }
        Request request = new Request.Builder().url(URL_STUDIO).build();
        Call call = sClient.newCall(request);
        Response response = call.execute();
        sContentLength = response.body().contentLength();
        response.body().close();
        call.cancel();
        long start = 0;
        long fragment = sContentLength / COUNT_THREAD;
        for (int i = 0; i < COUNT_THREAD; i++) {
            long end = fragment * (i + 1);
            MultiThreadDownload thread = new MultiThreadDownload(sFile, start, end);
            thread.start();
            start = end + 1;
        }
        if (start <= sContentLength){
            MultiThreadDownload thread = new MultiThreadDownload(sFile, start, sContentLength);
            thread.start();
        }

    }



    public interface OnProgressListener{
        void onUpdate(long progress);
    }
}
