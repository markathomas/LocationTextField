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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class URLConnectionGeocoder implements LocationProvider {

    public Collection<GeocodedLocation> geocode(String address) throws GeocodingException {
        final Set<GeocodedLocation> locations = new LinkedHashSet<GeocodedLocation>();
        BufferedReader reader = null;
        try {
            String addr = getURL(address);
            URLConnection con = new URL(addr).openConnection();
            con.setDoOutput(true);
            con.connect();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            final StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
            locations.addAll(createLocations(address, builder.toString()));
        } catch (Exception e) {
            throw new GeocodingException(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return locations;
    }

    protected abstract String getURL(String address) throws UnsupportedEncodingException;

    protected abstract Collection<? extends GeocodedLocation> createLocations(String address, String input)
      throws GeocodingException;
}
