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

import android.content.res.TypedArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class StateButtonTest {
    private static final String SEND_TEXT = "Send confirmation code";
    private static final String PROGRESS_TEXT = "Sending confirmation code";
    private static final String END_TEXT = "Sent confirmation code";

    private StateButton button;
    private TextView textView;
    private ProgressBar progressBar;
    private ImageView imageView;

    @Before
    public void setUp() throws Exception {
        button = new StateButton(RuntimeEnvironment.application);
        final TypedArray array = mock(TypedArray.class);

        when(array.getText(R.styleable.StateButton_startStateText)).thenReturn(SEND_TEXT);
        when(array.getText(R.styleable.StateButton_progressStateText)).thenReturn
                (PROGRESS_TEXT);
        when(array.getText(R.styleable.StateButton_finishStateText)).thenReturn
                (END_TEXT);

        button.init(array);
        array.recycle();

        textView = (TextView) button.findViewById(R.id.dgts__state_button);
        progressBar = (ProgressBar) button.findViewById(R.id.dgts__state_progress);
        imageView = (ImageView) button.findViewById(R.id.dgts__state_success);
    }

    @Test
    public void testShowStart() throws Exception {
        assertEquals(SEND_TEXT, textView.getText());
        assertEquals(View.GONE, progressBar.getVisibility());
        assertEquals(View.GONE, imageView.getVisibility());
        assertTrue(progressBar.isIndeterminate());
        assertTrue(button.isClickable());
    }

    @Test
    public void testShowProgress() throws Exception {
        button.showProgress();
        assertEquals(PROGRESS_TEXT, textView.getText());
        assertEquals(View.VISIBLE, progressBar.getVisibility());
        assertEquals(View.GONE, imageView.getVisibility());
        assertTrue(progressBar.isIndeterminate());
        assertFalse(button.isClickable());
    }

    @Test
    public void testShowFinish() throws Exception {
        button.showFinish();
        assertEquals(END_TEXT, textView.getText());
        assertEquals(View.GONE, progressBar.getVisibility());
        assertEquals(View.VISIBLE, imageView.getVisibility());
        assertTrue(progressBar.isIndeterminate());
        assertFalse(button.isClickable());
    }

    @Test
    public void testShowError() throws Exception {
        button.showError();
        testShowStart();
    }

}
