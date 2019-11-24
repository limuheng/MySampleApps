/**
 * @author Hank_Li@novatek
 * Off screen capture 20160630
 */

package com.nvt.fprlinker.OffScreenCapture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class AdbCmdUtils {
    private static final String TAG = AdbCmdUtils.class.getSimpleName();

    public static final String KEYEVENT_HOME = "03";
    public static final String KEYEVENT_BACK = "04";
    public static final String KEYEVENT_CAPTURE = "27";

    public static final int INT_EVENT_DELAY = 1000;

    public static final String PROP_CAPTURE_MODE = "capture.mode";

    public static void execCaptureCmd() {
        String captureCmd = "input keyevent " + KEYEVENT_CAPTURE;

        try {
            Runtime.getRuntime().exec(captureCmd);
            Log.d(TAG, "Finish capturing...");
        } catch (IOException e) {
            Log.e(TAG, "Failed to execCaptureCmd...");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Failed to execCaptureCmd...");
            e.printStackTrace();
        }
    }

    public static void execBackCmd() {
        String backCmd = "input keyevent " + KEYEVENT_BACK;
        try {
            Runtime.getRuntime().exec(backCmd);
            Log.d(TAG, "Execute back command...");
        } catch (IOException e) {
            Log.e(TAG, "Failed to execCaptureCmd...");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Failed to execCaptureCmd...");
            e.printStackTrace();
        }
    }

    public static int execGetPropIntCmd(String propName) {
        int retValue = -1;
        String captureCmd = "getprop " + propName;
        try {
            Process process = Runtime.getRuntime().exec(captureCmd);
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (bufferedReader != null) {
                String value = bufferedReader.readLine();
                bufferedReader.close();
                retValue = Integer.parseInt(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return retValue;
    }
}
