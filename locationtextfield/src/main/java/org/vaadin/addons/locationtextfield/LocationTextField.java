
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
        this(locationProvider, null, null, null);
    }

    private LocationTextField(LocationProvider<E> locationProvider, E initialValue, Property<E> property, String caption) {
        if (locationProvider == null) {
            throw new IllegalArgumentException("LocationProvider cannot be null");
        }
        this.property = property;
        if (initialValue != null) {
            this.setLocation(initialValue);
        }
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

    private LocationTextField(Builder<E> builder) {
        this(builder.locationProvider, builder.initialValue, builder.property, builder.caption);
        if (builder.geocoderController != null) {
            setGeocoderController(builder.geocoderController);
        }
        if (builder.text != null) {
            setText(builder.text);
        }
        if (builder.suggestions != null) {
            for (E suggestion : builder.suggestions) {
                this.addSuggestion(suggestion, suggestion.getDisplayString());
            }
        }
        if (builder.delayMillis > 0) {
            setDelay(builder.delayMillis);
        }
        if (builder.minimumQueryCharacters > 0) {
            setMinimumQueryCharacters(builder.minimumQueryCharacters);
        }
        setAutoSelectionEnabled(builder.autoSelectEnabled);
    }

    public static <E extends GeocodedLocation> Builder<E> newBuilder() {
        return new Builder<E>();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    /**
     * Specifies the controller for bridging between user input and the suggestions for this field.  This is the object that invokes
     * the {@link LocationProvider} to geocode user input. It is then responsible for updating the field's collection of
     * suggestions.
     * @return controller object
     */
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

        this.updateProperty(suggestion);

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

    private void updateProperty(E suggestion) {
        if (this.property == null) {
            this.property = new ObjectProperty<E>(suggestion);
        }
        this.property.setValue(suggestion);
    }

    /**
     * Adds a listener to receive changes to the selected location
     * @param listener a listener for changes to the selected location
     */
    public void addLocationValueChangeListener(ValueChangeListener listener) {
        synchronized (this.locationValueChangeListeners) {
            this.locationValueChangeListeners.add(listener);
        }
    }

    /**
     * Removed a listener from receiving changes to the selected location
     * @param listener a listener for changes to the selected location
     */
    public void removeLocationValueChangeListener(ValueChangeListener listener) {
        synchronized (this.locationValueChangeListeners) {
            this.locationValueChangeListeners.remove(listener);
        }
    }

    /**
     * Specifies whether or not auto-selection is enabled. True by default.
     * @return whether or not auto-selection is enabled
     */
    public boolean isAutoSelectionEnabled() {
        return getState().autoSelectEnabled;
    }
    public void setAutoSelectionEnabled(boolean autoSelectionEnabled) {
        this.getState().autoSelectEnabled = autoSelectionEnabled;
    }

    /**
     * Convenience method for explicitly setting the location
     */
    public void setLocation(E location) {
        this.reset();
        if (location != null) {
            this.updateProperty(location);
            this.addSuggestion(location, location.getGeocodedAddress());
            this.setText(location.getGeocodedAddress());
        }
    }

    /**
     * Removes all options and resets text field value to an empty string
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

    /**
     * The value currently shown in the field on the GUI
     * @return value currently shown in the field on the GUI
     */
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
     * Minimum length of displayText WITHOUT whitespace in order to initiate geocoding. Defaults to 3 characters.
     * @return minimum number of characters required to perform geocoding on user input
     */
    public int getMinimumQueryCharacters() {
        return getState().minimumQueryCharacters;
    }
    public void setMinimumQueryCharacters(int minTextLength) {
        if (minTextLength != this.getMinimumQueryCharacters()) {
            getState().minimumQueryCharacters = minTextLength;
        }
    }

    @Override
    public LocationTextFieldState getState() {
        return (LocationTextFieldState)super.getState();
    }

    /**
     * Specifies the delay (in milliseconds) between when the user types a character and the geocoding is performed
     * @return start delay in milliseconds
     */
    public int getDelay() {
        return getState().delayMillis;
    }
    public void setDelay(int delayMillis) {
        getState().delayMillis = delayMillis;
    }

    /**
     * Specifies the tab index of this field in the DOM
     * @return configured tab index
     */
    public int getTabIndex() {
        return getState().tabIndex;
    }
    public void setTabIndex(int tabIdx) {
        getState().tabIndex = tabIdx;
    }

    /**
     * Specifies whether or not this field is enabled. Disable fields will accept not input and will appear 'greyed out'.
     * @return whether or not this field is enabled
     */
    public boolean isEnabled() {
        return getState().enabled;
    }
    public void setEnabled(boolean enabled) {
        getState().enabled = enabled;
    }

    /**
     * Adds a suggested location to the field.
     * @param id a geocoded location
     * @param title how the location should be shown in the GUI. By default this value is set to
     *   {@link GeocodedLocation#getDisplayString()}
     */
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

    public static final class Builder<E extends GeocodedLocation> {

        private E initialValue;
        private LocationProvider<E> locationProvider;
        private Property<E> property;
        private GeocoderController<E> geocoderController;
        private String text;
        private String caption;
        private List<E> suggestions = Collections.emptyList();
        private int delayMillis = 500;
        private int minimumQueryCharacters = 5;
        private boolean autoSelectEnabled = true;

        private Builder() {
        }

        public Builder withInitialValue(E initialValue) {
            this.initialValue = initialValue;
            if (initialValue != null) {
                this.property = new ObjectProperty<E>(initialValue);
            }
            return this;
        }

        public Builder withLocationProvider(LocationProvider<E> locationProvider) {
            this.locationProvider = locationProvider;
            return this;
        }

        public Builder withProperty(Property<E> property) {
            this.property = property;
            return this;
        }

        public Builder withGeocoderController(GeocoderController<E> geocoderController) {
            this.geocoderController = geocoderController;
            return this;
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withCaption(String caption) {
            this.caption = caption;
            return this;
        }

        public Builder withSuggestions(List<E> suggestions) {
            if (suggestions == null) {
                suggestions = Collections.emptyList();
            }
            this.suggestions = suggestions;
            return this;
        }

        public Builder withDelayMillis(int delayMillis) {
            if (delayMillis < 0) {
                throw new IllegalArgumentException("delayMillis must be greater than zero");
            }
            this.delayMillis = delayMillis;
            return this;
        }

        public Builder withMinimumQueryCharacters(int minimumQueryCharacters) {
            if (minimumQueryCharacters < 1) {
                throw new IllegalArgumentException("minimumQueryCharacters must be greater than one");
            }
            this.minimumQueryCharacters = minimumQueryCharacters;
            return this;
        }

        public Builder withAutoSelectEnabled(boolean autoSelectEnabled) {
            this.autoSelectEnabled = autoSelectEnabled;
            return this;
        }

        public LocationTextField<E> build() {
            return new LocationTextField<E>(this);
        }
    }
}
