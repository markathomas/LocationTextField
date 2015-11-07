
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
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

import java.util.HashSet;
import java.util.Set;

import org.vaadin.addons.locationtextfield.client.LocationTextFieldState;

public class LocationTextField<E extends GeocodedLocation> extends AutocompleteField<E> {

    private static final long serialVersionUID = 6356456959417951791L;

    private Property<E> property;
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
        this.setQueryListener(new DefaultGeocodingQueryListener<E>(locationProvider));
        super.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<E>() {
            public void onSuggestionPicked(E suggestion) {
                LocationTextField.this.fireLocationChanged(suggestion);
            }
        });
    }

    @Override
    public void setSuggestionPickedListener(AutocompleteSuggestionPickedListener<E> listener) {
        throw new UnsupportedOperationException("Please use addLocationValueChangeListener(ValueChangeListener)");
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

    public void setAutoSelectionEnabled(boolean autoSelectionEnabled) {
        this.getState().autoSelectEnabled = autoSelectionEnabled;
    }

    /**
     * Convenience method for explicitly setting the location
     */
    @SuppressWarnings("unchecked")
    public void setLocation(E location) {
        this.clear();
        if (location != null) {
            this.addSuggestion(location, location.getGeocodedAddress());
            this.setText(location.getGeocodedAddress());
        }
    }

    /**
     * Removes all options and resets text field
     */
    public void clear() {
        this.clearChoices();
        this.setText("");
    }

    @Override
    public void setText(String text) {
        this.setText(text, false);
    }

    /**
     * Allows developer to set a known address string to be geocoded on the server-side
     * @param address String representation of an address
     */
    public void geocode(String address) {
        this.setText(address, true);
    }

    private void setText(String text, boolean geocodeIfDifferent) {
        if (!Objects.equals(getText(), text)) {
            super.setText(text);
            if (geocodeIfDifferent) {
                this.onQuery(text);
            }
        }
    }

    /**
     * Minimum length of text WITHOUT whitespace in order to initiate geocoding
     * @return
     */
    public int getMinTextLength() {
        return getState().minimumQueryCharacters;
    }
    public void setMinTextLength(int minTextLength) {
        if (minTextLength != this.getMinTextLength()) {
            this.setMinimumQueryCharacters(minTextLength);
        }
    }

    @Override
    public LocationTextFieldState getState() {
        return (LocationTextFieldState)super.getState();
    }
}
