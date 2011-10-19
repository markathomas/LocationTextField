/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.vaadin.addons.locationtextfield;

import java.io.Serializable;

/**
 * POJO for the constituent parts of a geocoded address
 */
public class GeocodedLocation implements Serializable {

    private String originalAddress;
    private String geocodedAddress;
    private String streetNumber;
    private String route;                       // a.k.a. street name
    private String locality;                    // a.k.a. city
    private String administrativeAreaLevel1;    // a.k.a. state
    private String administrativeAreaLevel2;    // a.k.a. county
    private String country;
    private String postalCode;                  // a.k.a. zip code
    private double lon;
    private double lat;
    private boolean ambiguous;
    private LocationType type;

    /**
     * This should be the original input address to the {@link LocationProvider}
     */
    public String getOriginalAddress() {
        return originalAddress;
    }
    public void setOriginalAddress(String originalAddress) {
        this.originalAddress = originalAddress;
    }

    /**
     * This should be the full geocoded address
     */
    public String getGeocodedAddress() {
        return geocodedAddress;
    }
    public void setGeocodedAddress(String geocodedAddress) {
        this.geocodedAddress = geocodedAddress;
    }

    public String getStreetNumber() {
        return streetNumber;
    }
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Same as street name for U.S.
     */
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

    /**
     * Same as city for U.S.
     */
    public String getLocality() {
        return locality;
    }
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * Same as state for U.S.
     */
    public String getAdministrativeAreaLevel1() {
        return administrativeAreaLevel1;
    }
    public void setAdministrativeAreaLevel1(String administrativeAreaLevel1) {
        this.administrativeAreaLevel1 = administrativeAreaLevel1;
    }

    /**
     * Same as county for U.S.
     */
    public String getAdministrativeAreaLevel2() {
        return administrativeAreaLevel2;
    }
    public void setAdministrativeAreaLevel2(String administrativeAreaLevel2) {
        this.administrativeAreaLevel2 = administrativeAreaLevel2;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Same as zip code for U.S.
     */
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Should be true if the {@link LocationProvider} returned more than one result for the input address
     */
    public boolean isAmbiguous() {
        return ambiguous;
    }
    public void setAmbiguous(boolean ambiguous) {
        this.ambiguous = ambiguous;
    }

    public LocationType getType() {
        return type;
    }
    public void setType(LocationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getGeocodedAddress();
    }
}
