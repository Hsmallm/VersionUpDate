package com.example.hzhm.versionupdate.utils.downloadmanager;

import android.os.Process;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

public class DownloadDispatcher extends Thread {

    /**
     * Tag used for debugging/logging
     */
    public static final String TAG = "ThinDownloadManager";
    /**
     * The buffer size used to stream the data
     */
    public final int BUFFER_SIZE = 4096;
    /**
     * The maximum number of redirects.
     */
    public final int MAX_REDIRECTS = 5; // can't be more than 7.
    /**
     * The queue of download requests to service.
     */
    private final BlockingQueue<com.example.hzhm.versionupdate.utils.downloadmanager.DownloadRequest> mQueue;
    private final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    private final int HTTP_TEMP_REDIRECT = 307;
    boolean shouldAllowRedirects = true;
    Timer mTimer;
    /**
     * Used to tell the dispatcher to die.
     */
    private volatile boolean mQuit = false;
    /**
     * Current Download request that this dispatcher is working
     */
    private com.example.hzhm.versionupdate.utils.downloadmanager.DownloadRequest mRequest;
    /**
     * To Delivery call back response on main thread
     */
    private com.example.hzhm.versionupdate.utils.downloadmanager.DownloadRequestQueue.CallBackDelivery mDelivery;
    /**
     * How many times redirects happened during a download request.
     */
    private int mRedirectionCount = 0;
    private long mContentLength;

    /**
     * Constructor take the dependency (DownloadRequest queue) that all the Dispatcher needs
     */
    public DownloadDispatcher(BlockingQueue<com.example.hzhm.versionupdate.utils.downloadmanager.DownloadRequest> queue,
                              com.example.hzhm.versionupdate.utils.downloadmanager.DownloadRequestQueue.CallBackDelivery delivery) {
        mQueue = queue;
        mDelivery = delivery;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mTimer = new Timer();
        while (true) {
            try {
                mRequest = mQueue.take();
                mRedirectionCount = 0;
//                LogUtil.v(TAG, "Download initiated for " + mRequest.getDownloadId());
                updateDownloadState(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.STATUS_STARTED);
                executeDownload(mRequest.getUri().toString());
            } catch (Exception e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    if (mRequest != null) {
                        mRequest.finish();
                        updateDownloadFailed(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.ERROR_DOWNLOAD_CANCELLED, "Download cancelled");
                        mTimer.cancel();
                    }
                    return;
                }
                continue;
            }
        }
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }


    private void executeDownload(String downloadUrl) {
        URL url = null;
        try {
            url = new URL(downloadUrl);
        } catch (Exception e) {
            updateDownloadFailed(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.ERROR_MALFORMED_URI, "MalformedURLException: URI passed is malformed.");
            return;
        }

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(mRequest.getRetryPolicy().getCurrentTimeout());
            conn.setReadTimeout(mRequest.getRetryPolicy().getCurrentTimeout());

            HashMap<String, String> customHeaders = mRequest.getCustomHeaders();
            if (customHeaders != null) {
                for (String headerName : customHeaders.keySet()) {
                    conn.addRequestProperty(headerName, customHeaders.get(headerName));
                }
            }

            // Status Connecting is set here before
            // urlConnection is trying to connect to destination.
            updateDownloadState(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.STATUS_CONNECTING);

            final int responseCode = conn.getResponseCode();

//            LogUtil.v(TAG, "Response code obtained for downloaded Id " + mRequest.getDownloadId() + " : httpResponse Code " + responseCode);

            switch (responseCode) {
                case HTTP_OK:
                    shouldAllowRedirects = false;
                    if (readResponseHeaders(conn) == 1) {
                        transferData(conn);
                    } else {
                        updateDownloadFailed(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.ERROR_DOWNLOAD_SIZE_UNKNOWN, "Can't know size of download, giving up");
                    }
                    return;
                case HTTP_MOVED_PERM:
                case HTTP_MOVED_TEMP:
                case HTTP_SEE_OTHER:
                case HTTP_TEMP_REDIRECT:
                    // Take redirect url and call executeDownload recursively until
                    // MAX_REDIRECT is reached.
                    while (mRedirectionCount++ < MAX_REDIRECTS && shouldAllowRedirects) {
//                        LogUtil.v(TAG, "Redirect for downloaded Id " + mRequest.getDownloadId());
                        final String location = conn.getHeaderField("Location");
                        executeDownload(location);
                        continue;
                    }

                    if (mRedirectionCount > MAX_REDIRECTS) {
                        updateDownloadFailed(DownloadManager.ERROR_TOO_MANY_REDIRECTS, "Too many redirects, giving up");
                        return;
                    }
                    break;
                case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
                    updateDownloadFailed(HTTP_REQUESTED_RANGE_NOT_SATISFIABLE, conn.getResponseMessage());
                    break;
                case HTTP_UNAVAILABLE:
                    updateDownloadFailed(HTTP_UNAVAILABLE, conn.getResponseMessage());
                    break;
                case HTTP_INTERNAL_ERROR:
                    updateDownloadFailed(HTTP_INTERNAL_ERROR, conn.getResponseMessage());
                    break;
                default:
                    updateDownloadFailed(DownloadManager.ERROR_UNHANDLED_HTTP_CODE, "Unhandled HTTP response:" + responseCode + " message:" + conn.getResponseMessage());
                    break;
            }
        } catch (SocketTimeoutException e) {
//            LogUtil.e(e);
            // Retry.
            attemptRetryOnTimeOutException();
        } catch (ConnectTimeoutException e) {
//            LogUtil.e(e);
            attemptRetryOnTimeOutException();
        } catch (IOException e) {
//            LogUtil.e(e);
            updateDownloadFailed(DownloadManager.ERROR_HTTP_DATA_ERROR, "Trouble with low-level sockets");
        } catch (Exception e){
//            LogUtil.e(e);
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void transferData(HttpURLConnection conn) {
        InputStream in = null;
        OutputStream out = null;
        FileDescriptor outFd = null;
        cleanupDestination();
        try {
            try {
                in = conn.getInputStream();
            } catch (Exception e) {
//                LogUtil.e(e);
            }

            File destinationFile = new File(mRequest.getDestinationURI().getPath());

            try {
                out = new FileOutputStream(destinationFile, true);
                outFd = ((FileOutputStream) out).getFD();
            } catch (Exception e) {
//                LogUtil.e(e);
                updateDownloadFailed(DownloadManager.ERROR_FILE_ERROR, "Error in writing download contents to the destination file");
            }

            // Start streaming data
            transferData(in, out);

        } finally {
            try {
                in.close();
            } catch (Exception e) {
//                LogUtil.e(e);
            }

            try {
                if (out != null) out.flush();
                if (outFd != null) outFd.sync();
            } catch (Exception e) {
//                LogUtil.e(e);
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
//                    LogUtil.e(e);
                }
            }
        }
    }

    private void transferData(InputStream in, OutputStream out) {
        final byte data[] = new byte[BUFFER_SIZE];
        long mCurrentBytes = 0;
        mRequest.setDownloadState(DownloadManager.STATUS_RUNNING);
//        LogUtil.v(TAG, "Content Length: " + mContentLength + " for Download Id " + mRequest.getDownloadId());
        for (; ; ) {
            if (mRequest.isCanceled()) {
//                LogUtil.v(TAG, "Stopping the download as Download Request is cancelled for Downloaded Id " + mRequest.getDownloadId());
                mRequest.finish();
                updateDownloadFailed(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.ERROR_DOWNLOAD_CANCELLED, "Download cancelled");
                return;
            }
            int bytesRead = readFromResponse(data, in);

            if (mContentLength != -1 && mContentLength > 0) {
                int progress = (int) ((mCurrentBytes * 100) / mContentLength);
                updateDownloadProgress(progress, mCurrentBytes);
            }

            if (bytesRead == -1) { // success, end of stream already reached
                updateDownloadComplete();
                return;
            } else if (bytesRead == Integer.MIN_VALUE) {
                return;
            }

            writeDataToDestination(data, bytesRead, out);
            mCurrentBytes += bytesRead;
        }
    }

    private int readFromResponse(byte[] data, InputStream entityStream) {
        try {
            return entityStream.read(data);
        } catch (Exception ex) {
            if ("unexpected end of stream".equals(ex.getMessage())) {
                return -1;
            }
            updateDownloadFailed(DownloadManager.ERROR_HTTP_DATA_ERROR, "IOException: Failed reading response");
            return Integer.MIN_VALUE;
        }
    }

    private void writeDataToDestination(byte[] data, int bytesRead, OutputStream out) {
        while (true) {
            try {
                out.write(data, 0, bytesRead);
                return;
            } catch (Exception ex) {
                updateDownloadFailed(DownloadManager.ERROR_FILE_ERROR, "IOException when writing download contents to the destination file");
                mRequest.cancel();
                break;
            }
        }
    }

    private int readResponseHeaders(HttpURLConnection conn) {
        final String transferEncoding = conn.getHeaderField("Transfer-Encoding");

        if (transferEncoding == null) {
            mContentLength = getHeaderFieldLong(conn, "Content-Length", -1);
        } else {
//            LogUtil.v(TAG, "Ignoring Content-Length since Transfer-Encoding is also defined for Downloaded Id " + mRequest.getDownloadId());
            mContentLength = -1;
        }

        if (mContentLength == -1
                && (transferEncoding == null || !transferEncoding.equalsIgnoreCase("chunked"))) {
            return -1;
        } else {
            return 1;
        }
    }

    public long getHeaderFieldLong(URLConnection conn, String field, long defaultValue) {
        try {
            return Long.parseLong(conn.getHeaderField(field));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void attemptRetryOnTimeOutException() {
        updateDownloadState(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.STATUS_RETRYING);
        final RetryPolicy retryPolicy = mRequest.getRetryPolicy();
        try {
            retryPolicy.retry();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    executeDownload(mRequest.getUri().toString());
                }
            }, retryPolicy.getCurrentTimeout());
        } catch (RetryError e) {
            // Update download failed.
            updateDownloadFailed(com.example.hzhm.versionupdate.utils.downloadmanager.DownloadManager.ERROR_CONNECTION_TIMEOUT_AFTER_RETRIES,
                    "Connection time out after maximum retires attempted");
        }
    }

    /**
     * Called just before the thread finishes, regardless of status, to take any necessary action on
     * the downloaded file.
     */
    private void cleanupDestination() {
//        LogUtil.d(TAG, "cleanupDestination() deleting " + mRequest.getDestinationURI().toString());
        File destinationFile = new File(mRequest.getDestinationURI().toString());
        if (destinationFile.exists()) {
            destinationFile.delete();
        }
    }

    public void updateDownloadState(int state) {
        mRequest.setDownloadState(state);
    }

    public void updateDownloadComplete() {
        mRequest.setDownloadState(DownloadManager.STATUS_SUCCESSFUL);
        if (mRequest.getDownloadListener() != null) {
            mDelivery.postDownloadComplete(mRequest);
            mRequest.finish();
        }
    }

    public void updateDownloadFailed(int errorCode, String errorMsg) {
        shouldAllowRedirects = false;
        mRequest.setDownloadState(DownloadManager.STATUS_FAILED);
        cleanupDestination();
        if (mRequest.getDownloadListener() != null) {
            mDelivery.postDownloadFailed(mRequest, errorCode, errorMsg);
            mRequest.finish();
        }
    }

    public void updateDownloadProgress(int progress, long downloadedBytes) {
        if (mRequest.getDownloadListener() != null) {
            mDelivery.postProgressUpdate(mRequest, mContentLength, downloadedBytes, progress);
        }
    }
}
