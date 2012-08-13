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

package com.krawler.hrms.ess;

import java.io.Serializable;

import com.krawler.common.admin.UserLogin;

/**
 *
 * @author krawler
 */
public class Empexperience implements Serializable{

private String id;
private UserLogin userid;
private String institution;
private String qualification;
private String yearofgrad;
private String marks;
private String organization;
private String position;
private String beginyear;
private String endyear;
private String comment;
private String type;
private String frmyear;
private String qaulin;

    public Empexperience() {
    }

    public String getBeginyear() {
        return beginyear;
    }

    public void setBeginyear(String beginyear) {
        this.beginyear = beginyear;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEndyear() {
        return endyear;
    }

    public void setEndyear(String endyear) {
        this.endyear = endyear;
    }

    public String getFrmyear() {
        return frmyear;
    }

    public void setFrmyear(String frmyear) {
        this.frmyear = frmyear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getQaulin() {
        return qaulin;
    }

    public void setQaulin(String qaulin) {
        this.qaulin = qaulin;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserLogin getUserid() {
        return userid;
    }

    public void setUserid(UserLogin userid) {
        this.userid = userid;
    }

    public String getYearofgrad() {
        return yearofgrad;
    }

    public void setYearofgrad(String yearofgrad) {
        this.yearofgrad = yearofgrad;
    }


}
