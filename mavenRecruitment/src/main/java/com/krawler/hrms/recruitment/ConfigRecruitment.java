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
package com.krawler.hrms.recruitment;

import com.krawler.common.admin.Company;

/**
 *
 * @author krawler
 */
public class ConfigRecruitment {

    String configid;
    int configtype;
    int colnum;
    String name;
    int position;
    String formtype;
    private boolean isSystemProperty;
    private boolean visible;
    private boolean allownull;
    private boolean deleted;
    private Company company;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getConfigid() {
        return configid;
    }

    public void setConfigid(String configid) {
        this.configid = configid;
    }

    public int getConfigtype() {
        return configtype;
    }

    public void setConfigtype(int configtype) {
        this.configtype = configtype;
    }

    public String getFormtype() {
        return formtype;
    }

    public void setFormtype(String formtype) {
        this.formtype = formtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColnum() {
        return colnum;
    }

    public void setColnum(int colnum) {
        this.colnum = colnum;
    }
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAllownull() {
        return allownull;
    }

    public boolean getAllownull() {
        return allownull;
    }

    public boolean isIsSystemProperty() {
        return isSystemProperty;
    }

    public boolean getIsSystemProperty() {
        return isSystemProperty;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setAllownull(boolean allownull) {
        this.allownull = allownull;
    }

    public void setIsSystemProperty(boolean isSystemProperty) {
        this.isSystemProperty = isSystemProperty;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
