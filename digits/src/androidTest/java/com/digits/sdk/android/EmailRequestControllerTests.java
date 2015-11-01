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

import android.os.Bundle;
import android.text.Editable;

import com.twitter.sdk.android.core.Result;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailRequestControllerTests extends DigitsControllerTests<EmailRequestController> {
    @Captor
    private ArgumentCaptor<DigitsCallback> callbackCaptor;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        controller = new DummyEmailRequestController(resultReceiver, sendButton, phoneEditText,
                sessionManager, new ActivityClassManagerImp(), digitsClient,
                PHONE_WITH_COUNTRY_CODE, scribeService, errors);
    }

    @Override
    DigitsCallback executeRequest() {
        when(phoneEditText.getText()).thenReturn(
                Editable.Factory.getInstance().newEditable(TestConstants.VALID_EMAIL));
        when(sessionManager.getActiveSession()).thenReturn(
                DigitsSession.create(TestConstants.DIGITS_USER, PHONE_WITH_COUNTRY_CODE));

        mainRequest();

        final DigitsApiClient.SdkService sdkService = controller.getSdkService(null);
        verify(sdkService).email(eq(TestConstants.VALID_EMAIL), callbackCaptor.capture());
        return callbackCaptor.getValue();
    }

    private void mainRequest() {
        controller.executeRequest(context);

        verify(scribeService).click(DigitsScribeConstants.Element.SUBMIT);
        verify(sendButton).showProgress();
        verify(phoneEditText, times(2)).getText();
        verify(sessionManager).getActiveSession();
        verifyNoInteractions(digitsClient);
    }

    public void testExecuteRequest_success() throws Exception {
        final DigitsSessionResponse response = TestConstants.DIGITS_USER;
        final Result<DigitsSessionResponse> result = new Result(response, null);

        final DigitsCallback callback = executeRequest();
        callback.success(result);

        verify(scribeService).success();
        verify(sessionManager).setActiveSession(DigitsSession.create(response,
                PHONE_WITH_COUNTRY_CODE));
        verify(sendButton).showFinish();
        final ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass
                (Runnable.class);
        verify(phoneEditText).postDelayed(runnableArgumentCaptor.capture(),
                eq(DigitsControllerImpl.POST_DELAY_MS));
        final Runnable runnable = runnableArgumentCaptor.getValue();
        runnable.run();
        final ArgumentCaptor<Bundle> bundleArgumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(resultReceiver).send(eq(LoginResultReceiver.RESULT_OK),
                bundleArgumentCaptor.capture());
        assertEquals(PHONE_WITH_COUNTRY_CODE, bundleArgumentCaptor.getValue().getString
                (DigitsClient.EXTRA_PHONE));
    }

    public void testExecuteRequest_logoutSession() throws Exception {
        when(phoneEditText.getText()).thenReturn(
                Editable.Factory.getInstance().newEditable(TestConstants.VALID_EMAIL));
        when(sessionManager.getActiveSession()).thenReturn(
                DigitsSession.create(TestConstants.LOGGED_OUT_USER, PHONE));
        mainRequest();
        verifyUnrecoverableException();
    }

    public void testExecuteRequest_invalidEmail() throws Exception {
        when(phoneEditText.getText()).thenReturn(
                Editable.Factory.getInstance().newEditable(TestConstants.INVALID_EMAIL));
        controller.executeRequest(context);
        verify(phoneEditText).setError(context.getString(R.string.dgts__invalid_email));
    }

    public void testValidateInput_null() throws Exception {
        assertFalse(controller.validateInput(null));
    }

    public void testValidateInput_empty() throws Exception {
        assertFalse(controller.validateInput(EMPTY_CODE));
    }

    public void testValidateInput_invalidEmail() throws Exception {
        assertFalse(controller.validateInput(TestConstants.INVALID_EMAIL));
    }

    public void testValidateInput_validEmail() throws Exception {
        assertTrue(controller.validateInput(TestConstants.VALID_EMAIL));
    }

}
