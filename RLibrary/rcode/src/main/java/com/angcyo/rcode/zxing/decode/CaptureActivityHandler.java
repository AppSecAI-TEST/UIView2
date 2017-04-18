/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.angcyo.rcode.zxing.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.angcyo.rcode.IDecodeCallback;
import com.angcyo.rcode.zxing.camera.CameraManager;
import com.angcyo.rcode.zxing.view.ViewfinderResultPointCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.util.Collection;
import java.util.Map;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private final IDecodeCallback mDecodeCallback;
    private final DecodeThread decodeThread;
    private final CameraManager cameraManager;
    private State state;

    public CaptureActivityHandler(IDecodeCallback mDecodeCallback,
                                  Collection<BarcodeFormat> decodeFormats,
                                  Map<DecodeHintType, ?> baseHints,
                                  String characterSet,
                                  CameraManager cameraManager) {
        this.mDecodeCallback = mDecodeCallback;
        decodeThread = new DecodeThread(mDecodeCallback, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(mDecodeCallback.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case IDecodeCallback.restart_preview:
                Log.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                break;
            case IDecodeCallback.decode_succeeded:
                Log.d(TAG, "Got decode succeeded message");
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = null;
                float scaleFactor = 1.0f;
                if (bundle != null) {
                    byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                    if (compressedBitmap != null) {
                        barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                        // Mutable copy:
                        barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                    }
                    scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
                }
                mDecodeCallback.handleDecode((Result) message.obj, barcode, scaleFactor);
                break;
            case IDecodeCallback.decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), IDecodeCallback.decode);
                break;
//            case R.id.return_scan_result:
//                Log.d(TAG, "Got return scan result message");
//                mDecodeCallback.setResult(Activity.RESULT_OK, (Intent) message.obj);
//                mDecodeCallback.finish();
//                break;
//            case R.id.launch_product_query:
//                Log.d(TAG, "Got product query message");
//                String url = (String) message.obj;
//
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                intent.setData(Uri.parse(url));
//
//                ResolveInfo resolveInfo =
//                        mDecodeCallback.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                String browserPackageName = null;
//                if (resolveInfo.activityInfo != null) {
//                    browserPackageName = resolveInfo.activityInfo.packageName;
//                    Log.d(TAG, "Using browser in package " + browserPackageName);
//                }
//
//                // Needed for default Android browser / Chrome only apparently
//                if ("com.android.browser".equals(browserPackageName) || "com.android.chrome".equals(browserPackageName)) {
//                    intent.setPackage(browserPackageName);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
//                }
//
//                try {
//                    mDecodeCallback.startActivity(intent);
//                } catch (ActivityNotFoundException ignored) {
//                    Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
//                }
//                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), IDecodeCallback.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(IDecodeCallback.decode_succeeded);
        removeMessages(IDecodeCallback.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), IDecodeCallback.decode);
            mDecodeCallback.drawViewfinder();
        }
    }

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

}
