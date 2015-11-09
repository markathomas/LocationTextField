
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

package org.vaadin.addons.locationtextfield;

import com.sun.org.apache.bcel.internal.util.Objects;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.addons.locationtextfield.client.GeocodedLocationSuggestion;
import org.vaadin.addons.locationtextfield.client.LocationTextFieldServerRpc;
import org.vaadin.addons.locationtextfield.client.LocationTextFieldState;

public class LocationTextField<E extends GeocodedLocation> extends AbstractField<String> {

    private static final long serialVersionUID = 6356456959417951791L;

    private Property<E> property;
    private GeocoderController<E> geocoderController;
    private final Map<Integer, E> items = new HashMap<Integer, E>();
    private final Set<ValueChangeListener> locationValueChangeListeners = new HashSet<ValueChangeListener>();

    public LocationTextField(LocationProvider<E> locationProvider) {
        this(locationProvider, null, null);
    }
    public LocationTextField(LocationProvider<E> locationProvider, String caption) {
        this(locationProvider, null, caption);
    }
    public LocationTextField(LocationProvider<E> locationProvider, Property<E> property) {
        this(locationProvider, property, null);
    }
    public LocationTextField(LocationProvider<E> locationProvider, E initialValue) {
        this(locationProvider, initialValue == null ? null : new ObjectProperty<E>(initialValue),
          initialValue == null ? null : initialValue.getGeocodedAddress());
    }
    public LocationTextField(LocationProvider<E> locationProvider, Property<E> property, String caption) {
        if (locationProvider == null) {
            throw new IllegalArgumentException("LocationProvider cannot be null");
        }
        this.property = property;
        this.setCaption(caption);
        this.setDelay(500);
        this.geocoderController = new DefaultGeocoderController<E>(locationProvider);

        final LocationTextFieldServerRpc rpc = new LocationTextFieldServerRpc() {
            public void geocode(String query) {
                LocationTextField.this.geocode(query);
            }

            @Override
            public void locationSelected(GeocodedLocationSuggestion suggestion) {
                LocationTextField.this.setText(suggestion.getDisplayString());
                E location = LocationTextField.this.items.get(suggestion.getId());
                LocationTextField.this.fireLocationChanged(location);
            }
        };
        this.registerRpc(rpc, LocationTextFieldServerRpc.class);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    public GeocoderController<E> getGeocoderController() {
        return this.geocoderController;
    }
    public void setGeocoderController(GeocoderController<E> geocoderController) {
        this.geocoderController = geocoderController;
    }

    private void fireLocationChanged(E suggestion) {
        if (suggestion == null) {
            return;
        }

        if (this.property == null) {
            this.property = new ObjectProperty<E>(suggestion);
        }
        this.property.setValue(suggestion);

        Set<ValueChangeListener> someListeners;
        synchronized (this.locationValueChangeListeners) {
            someListeners = new HashSet<ValueChangeListener>(this.locationValueChangeListeners);
        }

        if (!someListeners.isEmpty()) {
            final Property.ValueChangeEvent event = new ValueChangeEvent(this) {
                @Override
                public Property getProperty() {
                    return LocationTextField.this.property;
                }
            };

            for (ValueChangeListener listener : someListeners) {
                listener.valueChange(event);
            }
        }
    }

    public void addLocationValueChangeListener(ValueChangeListener listener) {
        synchronized (this.locationValueChangeListeners) {
            this.locationValueChangeListeners.add(listener);
        }
    }
    public void removeLocationValueChangeListener(ValueChangeListener listener) {
        synchronized (this.locationValueChangeListeners) {
            this.locationValueChangeListeners.remove(listener);
        }
    }

    public boolean isAutoSelectionEnabled() {
        return getState().autoSelectEnabled;
    }
    public void setAutoSelectionEnabled(boolean autoSelectionEnabled) {
        this.getState().autoSelectEnabled = autoSelectionEnabled;
    }

    /**
     * Convenience method for explicitly setting the location
     */
    @SuppressWarnings("unchecked")
    public void setLocation(E location) {
        this.reset();
        if (location != null) {
            this.addSuggestion(location, location.getGeocodedAddress());
            this.setText(location.getGeocodedAddress());
        }
    }

    /**
     * Removes all options and resets text field
     */
    public void reset() {
        this.clearChoices();
        this.setText("");
    }

    private void clearChoices() {
        getState().suggestions = Collections.emptyList();
        this.items.clear();
    }

    /**
     * Allows developer to set a known address string to be geocoded on the server-side
     * @param address String representation of an address
     */
    public void geocode(String address) {
        this.setText(address, true);
    }

    public String getText() {
        return getState().text;
    }
    public void setText(String text) {
        this.setText(text, false);
    }

    protected void setText(String text, boolean geocodeIfDifferent) {
        if (!Objects.equals(getText(), text)) {
            this.getState().text = text;
            if (geocodeIfDifferent) {
                clearChoices();
                this.geocoderController.geocode(this, text);
            }
        }
    }

    /**
     * Minimum length of text WITHOUT whitespace in order to initiate geocoding
     */
    public int getMinTextLength() {
        return getState().minimumQueryCharacters;
    }
    public void setMinTextLength(int minTextLength) {
        if (minTextLength != this.getMinTextLength()) {
            getState().minimumQueryCharacters = minTextLength;
        }
    }

    @Override
    public LocationTextFieldState getState() {
        return (LocationTextFieldState)super.getState();
    }

    public int getDelay() {
        return getState().delayMillis;
    }
    public void setDelay(int delayMillis) {
        getState().delayMillis = delayMillis;
    }

    public int getTabIndex() {
        return getState().tabIndex;
    }
    public void setTabIndex(int tabIdx) {
        getState().tabIndex = tabIdx;
    }

    public boolean isEnabled() {
        return getState().enabled;
    }
    public void setEnabled(boolean enabled) {
        getState().enabled = enabled;
    }

    public void addSuggestion(E id, String title) {
        int index = getState().suggestions.size();
        this.items.put(index, id);
        List<GeocodedLocationSuggestion> newSuggestionList = new ArrayList<GeocodedLocationSuggestion>(getState().suggestions);
        GeocodedLocationSuggestion suggestion = new GeocodedLocationSuggestion();
        suggestion.setId(index);
        suggestion.setDisplayString(title);
        newSuggestionList.add(suggestion);
        getState().suggestions = newSuggestionList;
    }
}
