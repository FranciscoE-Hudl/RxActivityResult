/*
 * Copyright 2016 Copyright 2016 Víctor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rx_activity_result;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

public class HolderActivity extends Activity {
    private static Request request;
    private OnResult onResult;
    private int resultCode;
    private Intent data;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onResult = request.onResult();

        if (savedInstanceState != null) return;

        if (request instanceof RequestIntentSender) {
            RequestIntentSender requestIntentSender = (RequestIntentSender) request;

            if (requestIntentSender.getOptions() == null) startIntentSender(requestIntentSender);
            else startIntentSenderWithOptions(requestIntentSender);
        } else {
            startActivityForResult(request.intent(), 0);
        }
    }

    private void startIntentSender(RequestIntentSender requestIntentSender) {
        try {
            startIntentSenderForResult(requestIntentSender.getIntentSender(), 0,
                    requestIntentSender.getFillInIntent(), requestIntentSender.getFlagsMask(),
                    requestIntentSender.getFlagsValues(), requestIntentSender.getExtraFlags());
        } catch (IntentSender.SendIntentException exception) {
            exception.printStackTrace();
            onResult.response(RESULT_CANCELED, null);
        }
    }

    private void startIntentSenderWithOptions(RequestIntentSender requestIntentSender) {
        try {
            startIntentSenderForResult(requestIntentSender.getIntentSender(), 0,
                    requestIntentSender.getFillInIntent(), requestIntentSender.getFlagsMask(),
                    requestIntentSender.getFlagsValues(), requestIntentSender.getExtraFlags(), requestIntentSender.getOptions());
        } catch (IntentSender.SendIntentException exception) {
            exception.printStackTrace();
            onResult.response(RESULT_CANCELED, null);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(HolderActivity.class.getSimpleName(), "Result received: " + System.currentTimeMillis());
        super.onActivityResult(requestCode, resultCode, data);
        this.resultCode = resultCode;
        this.data = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override protected void onDestroy() {
        Log.d(HolderActivity.class.getSimpleName(), "Result forwarded: " + System.currentTimeMillis());
        super.onDestroy();
        onResult.response(resultCode, data);
    }

    static void setRequest(Request aRequest) {
        request = aRequest;
    }
}
