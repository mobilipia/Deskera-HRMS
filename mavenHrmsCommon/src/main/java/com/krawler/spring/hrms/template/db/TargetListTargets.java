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
package com.krawler.spring.hrms.template.db;

import java.util.Date;

/**
 *
 * @author Abhishek Dubey <abhishek.dubey@krawlernetworks.com>
 */
public class TargetListTargets {
    private String id;
    private String fname;
    private String lname;
    private String emailid;
    private TargetList targetlistid;
    private Date modifiedon;
    private Date createdon;
    private int deleted;
    private int relatedto;
    private String relatedid;
    private String usrid;

    public String getUsrid() {
        return usrid;
    }

    public void setUsrid(String usrid) {
        this.usrid = usrid;
    }
    
    public Date getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Date getModifiedon() {
        return modifiedon;
    }

    public void setModifiedon(Date modifiedon) {
        this.modifiedon = modifiedon;
    }

    public String getRelatedid() {
        return relatedid;
    }

    public void setRelatedid(String relatedid) {
        this.relatedid = relatedid;
    }

    public int getRelatedto() {
        return relatedto;
    }

    public void setRelatedto(int relatedto) {
        this.relatedto = relatedto;
    }

    public TargetList getTargetlistid() {
        return targetlistid;
    }

    public void setTargetlistid(TargetList targetlistid) {
        this.targetlistid = targetlistid;
    }
}
