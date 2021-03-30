package top.oply.opuslib;

import android.util.Log;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Created by young on 2015/7/5.
 */
public class Utils {
    public static void printE(String tag, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        Log.e(tag, sw.toString());
    }

    static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static class AudioTime implements Serializable {
        private String mFormat = "%02d:%02d:%02d";
        private int mHour = 0;
        private int mMinute = 0;
        private int mSecond = 0;

        public AudioTime() {

        }

        /**
         * get time in the format of "HH:MM:SS"
         *
         * @return
         */
        public String getTime() {

            return String.format(mFormat, mHour, mMinute, mSecond);
        }

        public void setTimeInSecond(long seconds) {
            mSecond = (int) (seconds % 60);
            long m = seconds / 60;
            mMinute = (int) (m % 60);
            mHour = (int) (m / 60);

        }

        public void add(int seconds) {
            mSecond += seconds;
            if (mSecond >= 60) {
                mSecond %= 60;
                mMinute++;

                if (mMinute >= 60) {
                    mMinute %= 60;
                    mHour++;
                }
            }
        }
    }
}
