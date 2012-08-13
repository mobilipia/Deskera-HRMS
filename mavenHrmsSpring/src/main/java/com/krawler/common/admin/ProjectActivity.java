/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.krawler.common.admin;

/**
 *
 * @author krawler-user
 */
public class ProjectActivity {
    private String activityID;
    private String activityName;
    private String displayActivityName;
    private ProjectFeature feature;

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDisplayActivityName() {
        return displayActivityName;
    }

    public void setDisplayActivityName(String displayActivityName) {
        this.displayActivityName = displayActivityName;
    }

    public ProjectFeature getFeature() {
        return feature;
    }

    public void setFeature(ProjectFeature feature) {
        this.feature = feature;
    }
}
