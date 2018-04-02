/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.telegroup.nezavisnetvapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import com.telegroup.nezavisnetvapp.util.AbstractDetailsDescriptionPresenter;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    private static final String TAG = "PresenterDetails";

    @SuppressLint("WrongConstant")
    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Article article = (Article) item;

        if (article != null) {
            viewHolder.getSubtitle().setMaxLines(150);
            viewHolder.getTitle().setTypeface(null, Typeface.BOLD);
            viewHolder.getBody().setMovementMethod(new ScrollingMovementMethod());
            viewHolder.getBody().setVerticalScrollBarEnabled(true);
            viewHolder.getBody().computeScroll();
            viewHolder.getTitle().setText(article.getTitle());
            viewHolder.getSubtitle().setText(article.getDescription());
            viewHolder.getBody().setText(article.getBody());
        }
    }
}
