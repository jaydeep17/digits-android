/*
 * Copyright (C) 2015 Twitter, Inc.
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
 *
 */

package com.digits.sdk.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.twitter.sdk.android.core.SessionManager;

import io.fabric.sdk.android.services.common.CommonUtils;

abstract class DigitsControllerImpl implements DigitsController, TextWatcher {
    public static final int MAX_ERRORS = 5;
    static final long POST_DELAY_MS = 1500L;
    final DigitsClient digitsClient;
    final ActivityClassManager activityClassManager;
    final ErrorCodes errors;
    final ResultReceiver resultReceiver;
    final EditText editText;
    final StateButton sendButton;
    final SessionManager<DigitsSession> sessionManager;
    final DigitsScribeService scribeService;
    int errorCount;


    DigitsControllerImpl(ResultReceiver resultReceiver, StateButton stateButton, EditText editText,
                         DigitsClient client, ErrorCodes errors,
                         ActivityClassManager activityClassManager,
                         SessionManager<DigitsSession> sessionManager,
                         DigitsScribeService scribeService) {
        this.resultReceiver = resultReceiver;
        this.digitsClient = client;
        this.activityClassManager = activityClassManager;
        this.sendButton = stateButton;
        this.editText = editText;
        this.errors = errors;
        this.sessionManager = sessionManager;
        this.errorCount = 0;
        this.scribeService = scribeService;
    }

    @Override
    public void showTOS(Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(getTosUri());
        context.startActivity(intent);
    }

    @Override
    public void handleError(Context context, DigitsException exception) {
        errorCount++;
        scribeService.error(exception);
        if (isUnrecoverable(exception)) {
            scribeService.failure();
            startFallback(context, resultReceiver, exception);
        } else {
            editText.setError(exception.getLocalizedMessage());
            sendButton.showError();
        }
    }

    private boolean isUnrecoverable(DigitsException exception) {
        return errorCount == MAX_ERRORS || exception instanceof UnrecoverableException;
    }

    void startActivityForResult(Activity activity, Intent intent) {
        activity.startActivityForResult(intent, DigitsActivity.REQUEST_CODE);
    }

    @Override
    public void startFallback(Context context, ResultReceiver receiver, DigitsException reason) {
        final Intent intent = new Intent(context, activityClassManager.getFailureActivity());
        intent.putExtra(DigitsClient.EXTRA_RESULT_RECEIVER, receiver);
        intent.putExtra(DigitsClient.EXTRA_FALLBACK_REASON, reason);

        context.startActivity(intent);
        CommonUtils.finishAffinity(context, DigitsActivity.RESULT_FINISH_DIGITS);
    }

    @Override
    public boolean validateInput(CharSequence text) {
        return !TextUtils.isEmpty(text);
    }

    @Override
    public void clearError() {
        editText.setError(null);
    }

    @Override
    public ErrorCodes getErrors() {
        return errors;
    }

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public void onResume() {
        sendButton.showStart();
    }

    @Override
    public TextWatcher getTextWatcher() {
        return this;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Nothing to do
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        clearError();
    }

    @Override
    public void afterTextChanged(Editable s) {
        //Nothing to do
    }

    abstract Uri getTosUri();

    Bundle getBundle(String phoneNumber) {
        final Bundle bundle = new Bundle();
        bundle.putString(DigitsClient.EXTRA_PHONE, phoneNumber);
        return bundle;
    }

    void loginSuccess(final Context context, final DigitsSession session,
                      final String phoneNumber) {
        sessionManager.setActiveSession(session);
        sendButton.showFinish();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                resultReceiver.send(LoginResultReceiver.RESULT_OK, getBundle(phoneNumber));
                CommonUtils.finishAffinity((Activity) context,
                        DigitsActivity.RESULT_FINISH_DIGITS);
            }
        }, POST_DELAY_MS);
    }

    void startEmailRequest(final Context context, String phoneNumber) {
        sendButton.showFinish();
        final Intent intent = new Intent(context, activityClassManager.getEmailRequestActivity());
        final Bundle bundle = getBundle(phoneNumber);
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, resultReceiver);
        intent.putExtras(bundle);
        startActivityForResult((Activity) context, intent);
    }
}
