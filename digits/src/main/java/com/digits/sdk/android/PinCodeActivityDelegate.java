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
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.EditText;
import android.widget.TextView;

import io.fabric.sdk.android.services.common.CommonUtils;

class PinCodeActivityDelegate extends DigitsActivityDelegateImpl {
    private final DigitsScribeService scribeService;
    EditText editText;
    StateButton stateButton;
    TextView termsText;
    DigitsController controller;

    PinCodeActivityDelegate(DigitsScribeService scribeService) {
        this.scribeService = scribeService;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dgts__activity_pin_code;
    }

    @Override
    public void init(Activity activity, Bundle bundle) {
        editText = (EditText) activity.findViewById(R.id.dgts__confirmationEditText);
        stateButton = (StateButton) activity.findViewById(R.id.dgts__createAccount);
        termsText = (TextView) activity.findViewById(R.id.dgts__termsTextCreateAccount);

        controller = initController(bundle);

        setUpEditText(activity, controller, editText);
        setUpSendButton(activity, controller, stateButton);
        setUpTermsText(activity, controller, termsText);

        CommonUtils.openKeyboard(activity, editText);
    }

        DigitsController initController(Bundle bundle) {
            return new PinCodeController(bundle
                    .<ResultReceiver>getParcelable(DigitsClient.EXTRA_RESULT_RECEIVER),
                    stateButton, editText, bundle.getString(DigitsClient.EXTRA_REQUEST_ID),
                    bundle.getLong(DigitsClient.EXTRA_USER_ID), bundle.getString(DigitsClient
                    .EXTRA_PHONE), scribeService, bundle.getBoolean(DigitsClient.EXTRA_EMAIL));
    }

    @Override
    public boolean isValid(Bundle bundle) {
        return BundleManager.assertContains(bundle, DigitsClient.EXTRA_RESULT_RECEIVER,
                DigitsClient.EXTRA_PHONE, DigitsClient.EXTRA_REQUEST_ID,
                DigitsClient.EXTRA_USER_ID);
    }

    @Override
    public void onResume() {
        scribeService.impression();
        controller.onResume();
    }
}
