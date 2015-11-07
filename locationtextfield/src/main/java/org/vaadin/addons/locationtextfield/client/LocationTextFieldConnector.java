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
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;
import com.zybnet.autocomplete.client.AutocompleteConnector;

import org.vaadin.addons.locationtextfield.LocationTextField;

@Connect(LocationTextField.class)
public class LocationTextFieldConnector extends AutocompleteConnector {

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
        getWidget().getSuggestBox().setAutoSelectEnabled(getState().autoSelectEnabled);
    }

}
