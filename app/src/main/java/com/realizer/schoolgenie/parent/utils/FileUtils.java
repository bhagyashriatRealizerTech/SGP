package com.realizer.schoolgenie.parent.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by shree on 8/29/2016.
 */
public class FileUtils {
    public static void close(InputStream stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(OutputStream stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
