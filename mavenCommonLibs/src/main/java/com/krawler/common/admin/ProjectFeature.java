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

import java.util.Set;

/**
 *
 * @author krawler-user
 */
public class ProjectFeature {
    private String featureID;
    private String featureName;
    private String displayFeatureName;
    private Set<ProjectActivity> activities;
    private Set<UserPermission> permissions;

    public static String campaignFName = "Campaign";
    public static String leadFName = "Lead";
    public static String accountFName = "Account";
    public static String contactFName = "Contact";
    public static String opportunityFName = "Opportunity";
    public static String caseFName = "Case";
    public static String activityFName = "Activity";
    public static String productFName = "Product";
    public static String targetFName = "TargetModule";
    public String getDisplayFeatureName() {
        return displayFeatureName;
    }

    public void setDisplayFeatureName(String displayFeatureName) {
        this.displayFeatureName = displayFeatureName;
    }

    public String getFeatureID() {
        return featureID;
    }

    public void setFeatureID(String featureID) {
        this.featureID = featureID;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Set<ProjectActivity> getActivities() {
        return activities;
    }

    public void setActivities(Set<ProjectActivity> activities) {
        this.activities = activities;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }
}
