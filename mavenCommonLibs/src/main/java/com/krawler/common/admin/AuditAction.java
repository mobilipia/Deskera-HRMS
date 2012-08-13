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

public class AuditAction {
    public final static String DOC_TAG_ADDED = "128";
    
    public final static String ADMIN_Role = "150";
    public final static String ADMIN_Permission = "151";
    public final static String ADMIN_Organization = "152";
    
    private String ID;
    private String actionName;
    private AuditGroup auditGroup;    

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public AuditGroup getAuditGroup() {
        return auditGroup;
    }

    public void setAuditGroup(AuditGroup auditGroup) {
        this.auditGroup = auditGroup;
    }
}
