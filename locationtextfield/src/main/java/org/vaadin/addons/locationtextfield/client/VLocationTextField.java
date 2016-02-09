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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.vaadin.client.Focusable;
import com.vaadin.client.ui.VTextField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VLocationTextField extends Composite implements KeyUpHandler, Focusable {

    public static final String CLASSNAME = "v-locationtextfield";

    private final SuggestOracle oracle;
    private final GeocodedLocationSuggestionsDisplay suggestionsDisplay;
    private final VTextField textField;
    private final SuggestBox suggestBox;

    private int delayMillis = 300;
    private Timer sendQueryToServer = null;
    private GeocodeListener geocodeListener = null;
    private List<GeocodedLocationSuggestion> suggestions = Collections.emptyList();
    private boolean isInitiatedFromServer = false;
    private TextChangeListener textChangeHandler;
    private int minimumQueryCharacters = 3;
    boolean skipNextEnter;

    public VLocationTextField() {
        this.oracle = new GeocoderSuggestOracle();
        this.suggestionsDisplay = new GeocodedLocationSuggestionsDisplay(this);
        this.textField = GWT.create(VTextField.class);
        this.suggestBox = new SuggestBox(this.oracle, this.textField, this.suggestionsDisplay);
        this.initWidget(this.suggestBox);
        this.suggestBox.getValueBox().addKeyUpHandler(this);
        setStyleName(CLASSNAME);
        addStyleName("v-textfield");
        /*this.suggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                skipNextEnter = true;
            }
        });*/
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        // hide suggestion auto-popup on restore state
        this.suggestionsDisplay.hideSuggestions();
    }

    public void setAutoSelectEnabled(boolean autoSelectEnabled) {
        this.suggestBox.setAutoSelectEnabled(autoSelectEnabled);
    }

    private class GeocoderSuggestOracle extends SuggestOracle {

        @Override
        public void requestSuggestions(Request request, Callback callback) {
            if (isInitiatedFromServer) {
                // invoke the callback
                Response response = new Response();
                response.setSuggestions(wrapSuggestions(suggestions));
                callback.onSuggestionsReady(request, response);
            } else {
                // send event to the server side
                String query = request.getQuery().trim();

                if (query.length() >= getMinimumQueryCharacters()) {
                    scheduleQuery(request.getQuery());
                }
            }
        }
    }

    private void scheduleQuery(final String query) {

        if (this.sendQueryToServer != null) {
            this.sendQueryToServer.cancel();
        }

        this.sendQueryToServer = new Timer() {
            @Override
            public void run() {
                sendQueryToServer = null;
                if (geocodeListener != null && query != null && query.equals(suggestBox.getText())) {
                    geocodeListener.handleGeocode(query);
                }
            }
        };

        this.sendQueryToServer.schedule(this.delayMillis);
    }

    private List<SuggestOracle.Suggestion> wrapSuggestions(List<GeocodedLocationSuggestion> in) {
        List<SuggestOracle.Suggestion> out = new ArrayList<SuggestOracle.Suggestion>();
        for (final GeocodedLocationSuggestion wrappedSuggestion : in) {
            out.add(new GeocodedLocationOracleSuggestion(wrappedSuggestion));
        }
        return out;
    }

    public void setSuggestions(List<GeocodedLocationSuggestion> suggestions) {
        this.isInitiatedFromServer = true;
        try {
            this.suggestions = Collections.unmodifiableList(suggestions);
            this.suggestBox.refreshSuggestionList();
            this.suggestBox.showSuggestionList();
        } finally {
            this.isInitiatedFromServer = false;
        }
    }

    public void setInputPrompt(String inputPrompt) {
        this.textField.getElement().setAttribute("placeholder", inputPrompt);
    }

    public void addSelectionHandler(SelectionHandler<SuggestOracle.Suggestion> handler) {
        this.suggestBox.addSelectionHandler(handler);
    }

    public void setGeocodeListener(GeocodeListener listener) {
        this.geocodeListener = listener;
    }

    public interface GeocodeListener {
        void handleGeocode(String query);
    }

    public interface TextChangeListener {
        void onTextChange(String text);
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public void setDisplayedText(String text) {
        suggestBox.getValueBox().setText(text);
    }

    public void addTextChangeHandler(TextChangeListener handler) {
        this.textChangeHandler = handler;
    }

    public int getMinimumQueryCharacters() {
        return minimumQueryCharacters;
    }

    public void setMinimumQueryCharacters(int minimumQueryCharacters) {
        this.minimumQueryCharacters = minimumQueryCharacters;
    }

    public String getDisplayedText() {
        return suggestBox.getValueBox().getText();
    }

    @Override
    public void focus() {
        suggestBox.setFocus(true);
    }

    public void setTabIndex(int tabIdx) {
        suggestBox.setTabIndex(tabIdx);
    }

    public void setEnabled(boolean enabled) {
        suggestBox.setEnabled(enabled);
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {

        if (this.textChangeHandler != null) {
            this.textChangeHandler.onTextChange(this.suggestBox.getText());
        }

        final GeocodedLocationSuggestionsDisplay display =
          (GeocodedLocationSuggestionsDisplay)this.suggestBox.getSuggestionDisplay();
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_ESCAPE:
        case KeyCodes.KEY_TAB:
            display.hideSuggestions();
            event.stopPropagation();
            break;
        case KeyCodes.KEY_ENTER:
            if (!display.isSuggestionListShowing()) {
                if (this.skipNextEnter) {
                    this.skipNextEnter = false;
                    return;
                }
                this.setSuggestions(this.suggestions);
            } else {
                display.hideSuggestions();
            }
            break;
        case KeyCodes.KEY_DOWN:
            if (!display.isSuggestionListShowing()) {
                this.setSuggestions(this.suggestions);
            }
            break;
        }
    }
}
