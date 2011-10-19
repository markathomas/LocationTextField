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

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class LocationUpdateApplication extends Application {

    public void init() {
        Window mainWindow = new Window("Location update");

        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("500px");
        vl.setHeight("250px");
        vl.setMargin(true);

        final LocationTextField ltf = new LocationTextField(OpenStreetMapGeocoder.getInstance(), "Address: ");
        ltf.setWidth("100%");
        vl.addComponent(ltf);

        final TextField lat = new TextField("Latitude: ");
        final TextField lon = new TextField("Longitude: ");
        vl.addComponent(lat);
        vl.addComponent(lon);

        ltf.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                GeocodedLocation loc = ltf.getValue();
                if (loc != null) {
                    lat.setValue("" + loc.getLat());
                    lon.setValue("" + loc.getLon());
                } else {
                    lat.setValue("");
                    lon.setValue("");
                }
            }
        });

        mainWindow.setContent(vl);
        setMainWindow(mainWindow);
    }
}
