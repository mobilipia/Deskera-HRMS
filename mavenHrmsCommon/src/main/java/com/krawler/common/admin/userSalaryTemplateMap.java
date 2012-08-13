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

import com.krawler.common.admin.Useraccount;
import com.krawler.hrms.ess.Empprofile;
import java.util.Date;
import masterDB.Template;

/**
 *
 * @author krawler
 */
public class userSalaryTemplateMap {

    private Useraccount userAccount;
    private Empprofile empProfile;
    private Template salaryTemplate;
    private Date effectiveDate;
    private String mappingid;
    private Double basic;

    public String getMappingid() {
        return mappingid;
    }

    public Empprofile getEmpProfile() {
        return empProfile;
    }

    public void setEmpProfile(Empprofile empProfile) {
        this.empProfile = empProfile;
    }

    public Double getBasic() {
        return basic;
    }

    public void setBasic(Double basic) {
        this.basic = basic;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public Template getSalaryTemplate() {
        return salaryTemplate;
    }

    public Useraccount getUserAccount() {
        return userAccount;
    }

    
    public void setSalaryTemplate(Template salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setUserAccount(Useraccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setMappingid(String mappingid) {
        this.mappingid = mappingid;
    }

    public userSalaryTemplateMap(Useraccount userAccount, Template salaryTemplate, Date effectiveDate, String mappingid) {
        this.userAccount = userAccount;
        this.salaryTemplate = salaryTemplate;
        this.effectiveDate = effectiveDate;
        this.mappingid = mappingid;
    }

    public userSalaryTemplateMap() {
    }
    

    
}
