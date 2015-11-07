
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

import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractGeocodingQueryListener<E extends GeocodedLocation> implements AutocompleteQueryListener<E>, Serializable {

    private static final long serialVersionUID = -1363264311250337780L;

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeocodingQueryListener.class);

    private final LocationProvider<E> locationProvider;

    protected AbstractGeocodingQueryListener(LocationProvider<E> locationProvider) {
        this.locationProvider = locationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleUserQuery(AutocompleteField<E> autocompleteField, String query) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Attempting to geocode query: {}", query);
            }
            final Collection<E> results = this.locationProvider.geocode(query);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} results geocoded successfully from query: {}", (results == null ? 0 : results.size()), query);
            }
            this.handleQueryResults(autocompleteField, query, results);
        } catch (GeocodingException e) {
            LOGGER.error("Error geocoding query: {}", query, e);
            this.handleQueryError(autocompleteField, query, e);
        }
    }

    /**
     * Handle results of query. By default, the field is cleared of options then the new options are sorted and added.
     * @param autocompleteField target field
     * @param query the actual query from the client
     * @param results results of geocoding
     */
    protected void handleQueryResults(AutocompleteField<E> autocompleteField, String query, Collection<E> results) {
        autocompleteField.clearChoices();
        Collection<E> sorted = this.sortResults(results);
        autocompleteField.clearChoices();
        for (E option : sorted) {
            autocompleteField.addSuggestion(option, option.getGeocodedAddress());
        }
    }

    /**
     * Sort the results. Be default, Collections.sort(collection) is used.
     * @param results results to sort
     * @return collection of sorted results
     */
    protected Collection<E> sortResults(Collection<E> results) {
        List<E> sorted = new ArrayList<E>(results);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * This method handles any exception while performing the geocoding. By default it simply does nothing and moves on.
     * Sub-classes may wish to perform other options such as clearing the available choices, setting a default/override value, etc.
     * @param autocompleteField target field
     * @param query the actual query from the client
     * @param e exception raised while geocoding query
     */
    protected void handleQueryError(AutocompleteField<E> autocompleteField, String query, GeocodingException e) {

    }
}
