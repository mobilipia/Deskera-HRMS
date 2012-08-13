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

package com.krawler.common.util;

import java.util.Set;

/**
 *
 * @author krawler-user
 */
public class AuditGroup {
    private String ID;
    private String groupName;
    private Set<AuditAction> actions;

    public final static String LOG_IN="1";
    public final static String LOG_OUT="2";
    public final static String COMPANY="3";
    public final static String PAYROLL="4";
    public final static String RECRUITMENT="5";
    public final static String TIMESHEET="6";
    public final static String APPERAISAL="7";
    public final static String USER="14";
    public final static String PERMISSIONS="15";
    public final static String PASSWORD="16";
    public final static String MASTER="17";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Set<AuditAction> getActions() {
        return actions;
    }

    public void setActions(Set<AuditAction> actions) {
        this.actions = actions;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
