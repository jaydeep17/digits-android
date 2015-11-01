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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PhoneNumberUtilsTest {
    private static final String INVENTED_ISO = "random";
    private SimManager simManager;

    @Before
    public void setUp() throws Exception {
        simManager = mock(SimManager.class);
    }

    @Test
    public void testGetPhoneNumber_nullSim() throws Exception {
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(null);
        assertEquals(PhoneNumber.emptyPhone(), phoneNumberUtils.getPhoneNumber());
    }

    @Test
    public void testGetPhoneNumber() throws Exception {
        when(simManager.getCountryIso()).thenReturn(TestConstants.US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(TestConstants.RAW_PHONE);
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(simManager);
        final PhoneNumber number = phoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(TestConstants.PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(TestConstants.US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(TestConstants.US_ISO2, number.getCountryIso());
    }

    @Test
    public void testGetPhoneNumberProvidedRawPhoneNumber() throws Exception {
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(null);
        final PhoneNumber phoneNumber = phoneNumberUtils.getPhoneNumber(TestConstants.ES_RAW_PHONE);
        assertEquals(TestConstants.ES_COUNTRY_CODE, phoneNumber.getCountryCode());
        assertEquals(TestConstants.ES_ISO2, phoneNumber.getCountryIso().toLowerCase());
        assertEquals(TestConstants.PHONE_NO_COUNTRY_CODE, phoneNumber.getPhoneNumber());
    }

    @Test
    public void testGetPhoneNumberProvidedPhone_empty() throws Exception {
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(null);
        final PhoneNumber phoneNumber = phoneNumberUtils.getPhoneNumber("");
        assertEquals(PhoneNumber.emptyPhone(), phoneNumber);
    }

    @Test
    public void testGetPhoneNumberProvidedPhone_withoutPlusSign() throws Exception {
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(null);
        final PhoneNumber phoneNumber = phoneNumberUtils.getPhoneNumber(TestConstants.PHONE);
        assertEquals(TestConstants.PHONE, phoneNumber.getPhoneNumber());
        assertEquals(TestConstants.US_COUNTRY_CODE, phoneNumber.getCountryCode());
        assertEquals(TestConstants.US_ISO2, phoneNumber.getCountryIso().toLowerCase());
    }

    @Test
    public void testGetPhoneNumber_noCountryCode() throws Exception {
        when(simManager.getCountryIso()).thenReturn(TestConstants.US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(TestConstants.PHONE_NO_COUNTRY_CODE);
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(simManager);
        final PhoneNumber number = phoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(TestConstants.PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(TestConstants.US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(TestConstants.US_ISO2, number.getCountryIso());
    }

    @Test
    public void testGetPhoneNumber_noPlusSymbol() throws Exception {
        when(simManager.getCountryIso()).thenReturn(TestConstants.US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(TestConstants.PHONE);
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(simManager);
        final PhoneNumber number = phoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(TestConstants.PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(TestConstants.US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(TestConstants.US_ISO2, number.getCountryIso());
    }

    @Test
    public void testGetPhoneNumber_plusSymbolNoCountryCode() throws Exception {
        when(simManager.getCountryIso()).thenReturn(TestConstants.US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(
                TestConstants.PHONE_PLUS_SYMBOL_NO_COUNTRY_CODE);
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(simManager);
        final PhoneNumber number = phoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(TestConstants.PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(TestConstants.US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(TestConstants.US_ISO2, number.getCountryIso());
    }

    @Test
    public void testGetPhoneNumber_nonMatchingISO() throws Exception {
        when(simManager.getCountryIso()).thenReturn(INVENTED_ISO);
        when(simManager.getRawPhoneNumber()).thenReturn(TestConstants.RAW_PHONE);
        final PhoneNumberUtils phoneNumberUtils = new PhoneNumberUtils(simManager);
        final PhoneNumber number = phoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(TestConstants.PHONE, number.getPhoneNumber());
        assertEquals("", number.getCountryCode());
        assertEquals(INVENTED_ISO, number.getCountryIso());
    }
}
