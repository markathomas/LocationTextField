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

package org.vaadin.addons.ltf.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.HasValue;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;

import org.vaadin.addons.locationtextfield.GeocodedLocation;
import org.vaadin.addons.locationtextfield.LocationTextField;
import org.vaadin.addons.locationtextfield.OpenStreetMapGeocoder;

@Theme("demo")
@Title("LocationTextField Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.addons.ltf.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeUndefined();
        vl.setWidth("50%");
        vl.setSpacing(true);
        vl.setMargin(true);

        final OpenStreetMapGeocoder geocoder = OpenStreetMapGeocoder.getInstance();
        geocoder.setLimit(25);
        final LocationTextField<GeocodedLocation> ltf = new LocationTextField<GeocodedLocation>(geocoder);
        ltf.setCaption("Address: ");
        ltf.setWidth("100%");
        ltf.setInputPrompt("<<Enter Address>>");
        //ltf.setAutoSelectionEnabled(false);
        vl.addComponent(ltf);

        final TextField lat = new TextField("Latitude: ");
        final TextField lon = new TextField("Longitude: ");
        vl.addComponent(lat);
        vl.addComponent(lon);

        ltf.setRequiredIndicatorVisible(true);
        ltf.setAutoSelectionEnabled(true);
        ltf.addLocationValueChangeListener((HasValue.ValueChangeListener<GeocodedLocation>)event -> {
            GeocodedLocation loc = event.getValue();
            if (loc != null) {
                lat.setValue("" + loc.getLat());
                lon.setValue("" + loc.getLon());
            } else {
                lat.setValue("");
                lon.setValue("");
            }
        });

        Button b = new Button("New York City, NY", (Button.ClickListener)event -> ltf.geocode("New York City, NY"));
        vl.addComponent(b);

        Button b2 = new Button("Reset LocationTextField", (Button.ClickListener)event -> {
            ltf.reset();
            lat.clear();
            lon.clear();
        });
        vl.addComponent(b2);

        setContent(vl);
    }

    /*private GeocodedLocation getNYCity() {
        GeocodedLocation loc = new GeocodedLocation();
        loc.setLon(-73.9381406004754);
        loc.setLat(40.6637996714713);
        loc.setOriginalAddress("NYC, New York, United States of America");
        loc.setGeocodedAddress("NYC, New York, United States of America");
        return loc;
    }*/

}
