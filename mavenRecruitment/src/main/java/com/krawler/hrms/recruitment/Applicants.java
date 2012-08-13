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

import com.krawler.common.admin.User;

/**
 *
 * @author krawler
 */
public class Applicants {

    private String appid;
    private User applempid;
    private String empname;
    private Positionmain positionmain;
    private String status;

    public Applicants() {
    }

    public Applicants(String appid, User applempid, String empname, Positionmain positionmain, String status) {
        this.appid = appid;
        this.applempid = applempid;
        this.empname = empname;
        this.positionmain = positionmain;
        this.status = status;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public User getApplempid() {
        return applempid;
    }

    public void setApplempid(User applempid) {
        this.applempid = applempid;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public Positionmain getPositionmain() {
        return positionmain;
    }

    public void setPositionmain(Positionmain positionmain) {
        this.positionmain = positionmain;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
