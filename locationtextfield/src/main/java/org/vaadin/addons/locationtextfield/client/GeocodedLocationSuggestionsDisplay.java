/*
 * Copyright (C) 2015 Elihu, LLC. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.addons.locationtextfield.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.VOverlay;

import java.util.Collection;

public class GeocodedLocationSuggestionsDisplay extends SuggestBox.DefaultSuggestionDisplay {

    private final Widget widget;

    public GeocodedLocationSuggestionsDisplay(Widget widget) {
        this.widget = widget;
    }

    @Override
    public PopupPanel createPopup() {
        VOverlay popup = GWT.create(VOverlay.class);
        popup.setOwner(this.widget);
        popup.setStyleName("gwt-SuggestBoxPopup");
        popup.addStyleName(VLocationTextField.CLASSNAME);
        popup.setAutoHideEnabled(true);
        return popup;
    }

    @Override
    public void showSuggestions(final SuggestBox suggestBox, final Collection<? extends SuggestOracle.Suggestion> suggestions,
      final boolean isDisplayStringHTML, final boolean isAutoSelectEnabled, final SuggestBox.SuggestionCallback callback) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                GeocodedLocationSuggestionsDisplay.super.showSuggestions(suggestBox, suggestions, isDisplayStringHTML,
                  isAutoSelectEnabled, callback);
            }
        });
    }
}
