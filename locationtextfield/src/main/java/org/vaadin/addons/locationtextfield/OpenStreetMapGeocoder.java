
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link org.vaadin.addons.locationtextfield.LocationProvider} which uses OpenStreetMap
 */
public final class OpenStreetMapGeocoder extends URLConnectionGeocoder<GeocodedLocation> {

    private static final long serialVersionUID = -1577822453327050058L;

    private static final String BASE_URL = "http://nominatim.openstreetmap.org/search?format=json&addressdetails=1&q=";

    private static final OpenStreetMapGeocoder INSTANCE = new OpenStreetMapGeocoder();

    private OpenStreetMapGeocoder() {
        // nuthin'
    }

    public static OpenStreetMapGeocoder getInstance() {
        return INSTANCE;
    }

    protected String getURL(String address) throws UnsupportedEncodingException {
        String url = BASE_URL + URLEncoder.encode(address, "UTF-8");
        if (this.getLimit() > 0)
            url += "&limit=" + this.getLimit();
        return url;
    }

    protected Collection<GeocodedLocation> createLocations(String address, String input) throws GeocodingException {
        final Set<GeocodedLocation> locations = new LinkedHashSet<GeocodedLocation>();
        try {
            JSONArray results = new JSONArray(input);
            boolean ambiguous = results.length() > 1;
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                GeocodedLocation loc = new GeocodedLocation();
                loc.setAmbiguous(ambiguous);
                loc.setOriginalAddress(address);
                loc.setGeocodedAddress(result.getString("display_name"));
                loc.setLat(Double.parseDouble(result.getString("lat")));
                loc.setLon(Double.parseDouble(result.getString("lon")));
                loc.setType(getLocationType(result));
                if (result.has("address")) {
                    JSONObject obj = result.getJSONObject("address");
                    if (obj.has("house_number"))
                        loc.setStreetNumber(obj.getString("house_number"));
                    if (obj.has("road"))
                        loc.setRoute(obj.getString("road"));
                    if (obj.has("city"))
                        loc.setLocality(obj.getString("city"));
                    if (obj.has("county"))
                        loc.setAdministrativeAreaLevel2(obj.getString("county"));
                    if (obj.has("state"))
                        loc.setAdministrativeAreaLevel1(obj.getString("state"));
                    if (obj.has("postcode"))
                        loc.setPostalCode(obj.getString("postcode"));
                    if (obj.has("country_code"))
                        loc.setCountry(obj.getString("country_code").toUpperCase());
                }
                locations.add(loc);
            }
        } catch (JSONException e) {
            throw new GeocodingException(e.getMessage(), e);
        }
        return locations;
    }

    private LocationType getLocationType(JSONObject result) throws JSONException {
        final String classValue = result.getString("class");
        final String type = result.getString("type");
        if ("highway".equals(classValue) || "railway".equals(classValue))
            return LocationType.ROUTE;
        else if ("amenity".equals(classValue) || "liesure".equals(classValue) || "natural".equals(type)
          || "shop".equals(classValue) || "tourism".equals(classValue) || "waterway".equals(type)) {
            return LocationType.POI;
        } else if ("building".equals(classValue))
            return LocationType.STREET_ADDRESS;
        else if ("place".equals(classValue)) {
            if ("house".equals(type) || "houses".equals(type) || "airport".equals(type) || "farm".equals(type))
                return LocationType.STREET_ADDRESS;
            else if ("city".equals(type) || "hamlet".equals(type) || "town".equals(type) || "unincorporated_area".equals(type)
              || "locality".equals(type) || "village".equals(type) || "municipality".equals(type)) {
                return LocationType.LOCALITY;
            } else if ("state".equals(type) || "region".equals(type))
                return LocationType.ADMIN_LEVEL_1;
            else if ("postcode".equals(type))
                return LocationType.POSTAL_CODE;
            else if ("country".equals(type))
                return LocationType.COUNTRY;
            else if ("county".equals(type))
                return LocationType.ADMIN_LEVEL_2;
            else if ("subdivision".equals(type) || "suburb".equals(type))
                return LocationType.NEIGHBORHOOD;
            else if ("moor".equals(type) || "island".equals(type) || "islet".equals(type) || "sea".equals(type))
                return LocationType.POI;
        } else if ("boundary".equals(classValue) && "administrative".equals(type))
                return LocationType.ADMIN_LEVEL_1;
        return LocationType.UNKNOWN;
    }
}
