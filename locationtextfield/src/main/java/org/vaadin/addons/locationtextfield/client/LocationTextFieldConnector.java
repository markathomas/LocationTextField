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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.ui.Connect;

import org.vaadin.addons.locationtextfield.LocationTextField;

@Connect(LocationTextField.class)
public class LocationTextFieldConnector extends AbstractFieldConnector
  implements VLocationTextField.GeocodeListener, VLocationTextField.TextChangeListener, SelectionHandler<SuggestOracle.Suggestion> {

    private final LocationTextFieldServerRpc serverRpc;
    private GeocodedLocationSuggestion selectedSuggestion;

    public LocationTextFieldConnector() {
        this.serverRpc = RpcProxy.create(LocationTextFieldServerRpc.class, this);
        getWidget().setGeocodeListener(this);
        getWidget().addSelectionHandler(this);
        getWidget().addTextChangeHandler(this);
    }

    @Override
    protected VLocationTextField createWidget() {
        return GWT.create(VLocationTextField.class);
    }

    @Override
    public VLocationTextField getWidget() {
        return (VLocationTextField)super.getWidget();
    }

    @Override
    public LocationTextFieldState getState() {
        return (LocationTextFieldState)super.getState();
    }

    @OnStateChange("autoSelectEnabled")
    private void setAutoSelectEnabled() {
        getWidget().setAutoSelectEnabled(getState().autoSelectEnabled);
    }

    @OnStateChange("suggestions")
    private void updateSuggestions() {
        getWidget().setSuggestions(getState().suggestions);
    }

    @OnStateChange("delayMillis")
    private void updateDelayMillis() {
        getWidget().setDelayMillis(getState().delayMillis);
    }

    @OnStateChange("tabIndex")
    private void setTabIndex() {
        getWidget().setTabIndex(getState().tabIndex);
    }

    @OnStateChange("enabled")
    private void setEnabled() {
        getWidget().setEnabled(getState().enabled);
    }

    @OnStateChange("text")
    private void setText() {
        getWidget().setDisplayedText(getState().text);
    }

    @Override
    public void handleGeocode(String query) {
        this.serverRpc.geocode(query);
    }

    @Override
    public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
        GeocodedLocationSuggestion suggestion =
          ((GeocodedLocationOracleSuggestion)event.getSelectedItem()).getGeocodedLocationSuggestion();
        if (suggestion != null && !suggestion.equals(this.selectedSuggestion)) {
            this.serverRpc.locationSelected(suggestion);
            this.selectedSuggestion = suggestion;
            getWidget().skipNextEnter = true;
        }
    }

    @Override
    public void onTextChange(String text) {
        getState().text = text;
        if (text.isEmpty())
            this.serverRpc.inputCleared();
    }
}
