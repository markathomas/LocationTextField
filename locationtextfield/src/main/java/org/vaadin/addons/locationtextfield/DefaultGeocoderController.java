
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

/**
 * Default geocoding query listener sticking with all the defaults
 * @param <E> type of geocoding result
 */
public class DefaultGeocoderController<E extends GeocodedLocation> extends AbstractGeocoderController<E> {

    private static final long serialVersionUID = -1200986877452048060L;

    public DefaultGeocoderController(LocationProvider<E> locationProvider) {
        super(locationProvider);
    }
}
