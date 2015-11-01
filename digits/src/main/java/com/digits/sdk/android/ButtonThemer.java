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

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

class ButtonThemer {
    private final Resources resources;

    public ButtonThemer(Resources resources) {
        this.resources = resources;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setBackgroundAccentColor(View view, int accentColor) {
        final StateListDrawable background = new StateListDrawable();
        final float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                resources.getDisplayMetrics());

        // Pressed state
        GradientDrawable tmp = new GradientDrawable();
        tmp.setCornerRadius(radius);
        tmp.setColor(getPressedColor(accentColor));
        background.addState(new int[]{android.R.attr.state_focused,
                android.R.attr.state_pressed}, tmp);
        background.addState(new int[]{-android.R.attr.state_focused,
                android.R.attr.state_pressed}, tmp);
        background.addState(new int[]{android.R.attr.state_focused}, tmp);

        // Default state
        tmp = new GradientDrawable();
        tmp.setColor(accentColor);
        tmp.setCornerRadius(radius);
        background.addState(StateSet.WILD_CARD, tmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setBackgroundAccentColorInverse(View view, int accentColor) {
        final StateListDrawable background = new StateListDrawable();
        final float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                resources.getDisplayMetrics());
        final float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                resources.getDisplayMetrics());

        // Pressed state
        GradientDrawable tmp = new GradientDrawable();
        tmp.setCornerRadius(radius);
        tmp.setStroke((int) strokeWidth, getPressedColor(accentColor));
        background.addState(new int[]{android.R.attr.state_focused,
                android.R.attr.state_pressed}, tmp);
        background.addState(new int[]{-android.R.attr.state_focused,
                android.R.attr.state_pressed}, tmp);
        background.addState(new int[]{android.R.attr.state_focused}, tmp);

        // Default state
        tmp = new GradientDrawable();
        tmp.setCornerRadius(radius);
        tmp.setStroke((int) strokeWidth, accentColor);
        background.addState(StateSet.WILD_CARD, tmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    private int getPressedColor(int accentColor) {
        if (ThemeUtils.isLightColor(accentColor)) {
            return ThemeUtils.calculateOpacityTransform(.20, Color.BLACK, accentColor);
        } else {
            return ThemeUtils.calculateOpacityTransform(.20, Color.WHITE, accentColor);
        }
    }

    void setTextAccentColor(TextView view, int accentColor) {
        view.setTextColor(getTextColor(accentColor));
    }

    void setTextAccentColorInverse(TextView view, int accentColor) {
        final int pressedColor = getPressedColor(accentColor);
        final int[][] states = new int[][]{
                new int[]{android.R.attr.state_focused, -android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused, android.R.attr.state_pressed},
                new int[]{-android.R.attr.state_focused, android.R.attr.state_pressed},
                StateSet.WILD_CARD,
        };
        final int[] colors = new int[]{accentColor, pressedColor, pressedColor, accentColor};

        final ColorStateList stateList = new ColorStateList(states, colors);
        view.setTextColor(stateList);
    }

    int getTextColor(int accentColor) {
        return ThemeUtils.isLightColor(accentColor) ? resources.getColor(R.color
                .dgts__text_dark) : resources.getColor(R.color.dgts__text_light);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void disableDropShadow(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setStateListAnimator(null);
            view.setElevation(0);
        }
    }
}
