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

package com.krawler.hrms.master;

import com.krawler.common.admin.Company;
import com.krawler.hrms.performance.Managecmpt;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krawler
 */
public class MasterData {
String id;
Master masterid ;
private Company company;
String value;
int weightage;
int componenttype; // 0:Neutral/Employer Contribution   1:Earning   2:Deduction   3:Tax    4:Other Remuneration  5:Income Tax
String worktime; //In the format 00:00 used for timesheet job.
private Set<Managecmpt> managecmpts = new HashSet<Managecmpt>(0);

    public Set<Managecmpt> getManagecmpts() {
        return managecmpts;
    }

    public void setManagecmpts(Set<Managecmpt> managecmpts) {
        this.managecmpts = managecmpts;
    }

    public MasterData() {
    }

    public MasterData(String id, Master masterid, Company company, String value, int weightage) {
        this.id = id;
        this.masterid = masterid;
        this.company = company;
        this.value = value;
        this.weightage = weightage;
    }

      public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Master getMasterid() {
        return masterid;
    }

    public void setMasterid(Master masterid) {
        this.masterid = masterid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getComponenttype() {
        return componenttype;
    }

    public void setComponenttype(int componenttype) {
        this.componenttype = componenttype;
    }

    public String getWorktime() {
        return worktime;
    }

    public void setWorktime(String worktime) {
        this.worktime = worktime;
    }

}
