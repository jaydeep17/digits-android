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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CountryInfoTests {
    private static final String COUNTRY_NAME_US = "United States";
    private static final int COUNTRY_CODE_US = 1;
    private static final String COUNTRY_NAME_BS = "Bahamas";
    private static final int COUNTRY_CODE_JP = 81;

    @Test
    public void testEquals_differentObject() throws Exception {
        final CountryInfo countryInfo1 = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);
        final CountryInfo countryInfo2 = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);

        assertEquals(countryInfo1, countryInfo2);
        assertEquals(countryInfo2, countryInfo1);
        assertEquals(countryInfo1, countryInfo1);
        assertEquals(countryInfo2, countryInfo2);
        assertNotSame(countryInfo2, countryInfo1);
    }

    @Test
    public void testEquals_null() throws Exception {
        final CountryInfo countryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);

        assertFalse(countryInfo.equals(null));
    }

    @Test
    public void testEquals_differentClass() throws Exception {
        final CountryInfo countryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);

        assertFalse(countryInfo.equals(new Integer(0)));
    }

    @Test
    public void testEquals_differentCountryName() throws Exception {
        final CountryInfo usCountryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);
        final CountryInfo bsCountryInfo = new CountryInfo(COUNTRY_NAME_BS, COUNTRY_CODE_US);

        assertFalse(usCountryInfo.equals(bsCountryInfo));
    }

    @Test
    public void testEquals_nullCountryName() throws Exception {
        final CountryInfo usCountryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);
        final CountryInfo bsCountryInfo = new CountryInfo(null, COUNTRY_CODE_US);

        assertFalse(usCountryInfo.equals(bsCountryInfo));
        assertFalse(bsCountryInfo.equals(usCountryInfo));
    }

    @Test
    public void testEquals_differentCountryCode() throws Exception {
        final CountryInfo usCountryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);
        final CountryInfo jpCountryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_JP);

        assertFalse(usCountryInfo.equals(jpCountryInfo));
    }

    @Test
    public void testHashCode() throws Exception {
        final CountryInfo usCountryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);
        final CountryInfo bsCountryInfo = new CountryInfo(null, COUNTRY_CODE_US);

        assertEquals(1416475714, usCountryInfo.hashCode());
        assertEquals(1, bsCountryInfo.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        final CountryInfo usCountryInfo = new CountryInfo(COUNTRY_NAME_US, COUNTRY_CODE_US);

        assertEquals(usCountryInfo.country + " +" + usCountryInfo.countryCode,
                usCountryInfo.toString());
    }
}
