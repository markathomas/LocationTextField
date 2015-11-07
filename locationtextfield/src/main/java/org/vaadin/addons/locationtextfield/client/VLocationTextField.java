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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.vaadin.client.VConsole;
import com.zybnet.autocomplete.client.SimpleSuggestionsDisplay;
import com.zybnet.autocomplete.client.VAutocompleteField;
import com.zybnet.autocomplete.shared.AutocompleteFieldSuggestion;

import java.util.Collections;
import java.util.List;

public class VLocationTextField extends VAutocompleteField {

    private List<AutocompleteFieldSuggestion> currentSuggestions = Collections.emptyList();
    private boolean skipNextEnter;

    public VLocationTextField() {
        super();
        addStyleName("v-textfield");
        addStyleName("v-locationtextfield");
        this.getSuggestBox().addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                skipNextEnter = true;
            }
        });
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        super.onKeyUp(event);

        VConsole.log("received key up event: " + event.toDebugString());

        final SuggestBox box = this.getSuggestBox();
        final SimpleSuggestionsDisplay display = (SimpleSuggestionsDisplay)box.getSuggestionDisplay();
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_ENTER:
            if (!display.isSuggestionListShowing()) {
                if (this.skipNextEnter) {
                    this.skipNextEnter = false;
                    return;
                }
                VConsole.log("ENTER pressed and list not showing. try to open list");
                super.setSuggestions(this.currentSuggestions);
            } else {
                VConsole.log("ENTER pressed and list IS showing. close list");
                display.hideSuggestions();
            }
            break;
        case KeyCodes.KEY_DOWN:
            if (!display.isSuggestionListShowing()) {
                VConsole.log("DOWN pressed and list not showing. try to open list");
                super.setSuggestions(this.currentSuggestions);
            }
            break;
        }
    }

    SuggestBox getSuggestBox() {
        return (SuggestBox)super.getWidget();
    }

    public void setSuggestions(List<AutocompleteFieldSuggestion> suggestions) {
        this.currentSuggestions = suggestions;
        super.setSuggestions(this.currentSuggestions);
    }
}
