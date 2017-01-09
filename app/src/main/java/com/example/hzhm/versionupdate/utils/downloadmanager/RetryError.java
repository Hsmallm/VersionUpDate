package com.example.hzhm.versionupdate.utils.serves.utils.downloadmanager;

/**
 * Created by maniselvaraj on 15/4/15.
 */
@SuppressWarnings("serial")
public class RetryError extends Exception {

    public RetryError() {
        super("Maximum retry exceeded");
    }

    public RetryError(Throwable cause) {
        super(cause);
    }
}
