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
import com.krawler.common.admin.User;

/**
 *
 * @author krawler
 */
public class Agency {
     private String agencyid;
     private String agencyname;
     private String agencyweb;
     private String reccost;
     private String conperson;
     private User apprman;
     private String agencyno;
     private String agencyadd;
     private Company company;
     private Integer delflag;

    public Agency() {
    }

    public Agency(String agencyid, String agencyname, String agencyweb, String reccost, String conperson, User apprman, String agencyno, String agencyadd, Company company, Integer delflag) {
        this.agencyid = agencyid;
        this.agencyname = agencyname;
        this.agencyweb = agencyweb;
        this.reccost = reccost;
        this.conperson = conperson;
        this.apprman = apprman;
        this.agencyno = agencyno;
        this.agencyadd = agencyadd;
        this.company = company;
        this.delflag=delflag;
    }

    public String getAgencyadd() {
        return agencyadd;
    }

    public void setAgencyadd(String agencyadd) {
        this.agencyadd = agencyadd;
    }

    public String getAgencyid() {
        return agencyid;
    }

    public void setAgencyid(String agencyid) {
        this.agencyid = agencyid;
    }

    public String getAgencyname() {
        return agencyname;
    }

    public void setAgencyname(String agencyname) {
        this.agencyname = agencyname;
    }

    public String getAgencyno() {
        return agencyno;
    }

    public void setAgencyno(String agencyno) {
        this.agencyno = agencyno;
    }

    public String getAgencyweb() {
        return agencyweb;
    }

    public void setAgencyweb(String agencyweb) {
        this.agencyweb = agencyweb;
    }

    public User getApprman() {
        return apprman;
    }

    public void setApprman(User apprman) {
        this.apprman = apprman;
    }

    public String getConperson() {
        return conperson;
    }

    public void setConperson(String conperson) {
        this.conperson = conperson;
    }

    public String getReccost() {
        return reccost;
    }

    public void setReccost(String reccost) {
        this.reccost = reccost;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Integer getDelflag() {
        return delflag;
    }

    public void setDelflag(Integer delflag) {
        this.delflag = delflag;
    }
}
