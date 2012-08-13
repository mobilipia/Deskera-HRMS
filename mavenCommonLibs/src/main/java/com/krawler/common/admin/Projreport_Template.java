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

import com.krawler.common.admin.User;

/**
 *
 * @author trainee
 */
public class Projreport_Template {
    private String tempid;
    private String tempname;
    private String description;
    private String configstr;
    private User userid;
    private int deleteflag;

    public Projreport_Template() {
    }

    public Projreport_Template(String tempid, String tempname, String description, String configstr, User userid) {
        this.tempid = tempid;
        this.tempname = tempname;
        this.description = description;
        this.configstr = configstr;
        this.userid = userid;
    }

    public String getConfigstr() {
        return configstr;
    }

    public void setConfigstr(String configstr) {
        this.configstr = configstr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTempid() {
        return tempid;
    }

    public void setTempid(String tempid) {
        this.tempid = tempid;
    }

    public String getTempname() {
        return tempname;
    }

    public void setTempname(String tempname) {
        this.tempname = tempname;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public int getDeleteflag() {
        return this.deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }
    
}
