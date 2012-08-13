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

import com.krawler.common.admin.*;

/**
 *
 * @author krawler
 */
public class ConfigMaster {

    String masterid;
    String masterdata;
    private ConfigType configid;

    public ConfigType getConfigid() {
        return configid;
    }

    public void setConfigid(ConfigType configid) {
        this.configid = configid;
    }

    public String getMasterdata() {
        return masterdata;
    }

    public void setMasterdata(String masterdata) {
        this.masterdata = masterdata;
    }

    public String getMasterid() {
        return masterid;
    }

    public void setMasterid(String masterid) {
        this.masterid = masterid;
    }
}
