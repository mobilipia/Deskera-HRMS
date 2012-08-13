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

import com.krawler.hrms.ess.Empexperience;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Assignreviewer;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krawler-user
 */
public class Useraccount {
    private String userID;
    private User user;
    private Role role;
    private ConfigData configdata;
    private String accno; //
    private String salary;
    private String templateid;
    private MasterData department;
    private MasterData designationid;
    private CostCenter costCenter;
    private Integer employeeid; //
    private String employeeIdFormat;
    private Integer dummystatus;
    private Set<Assignmanager> assignmanagers=new HashSet<Assignmanager>(0);
    private Set<Assignreviewer> assignreviewers=new HashSet<Assignreviewer>(0);
    private Empprofile empProfile;
    private Set<Empexperience> empexperience=new HashSet<Empexperience>(0);
    private boolean salarymanager;



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccno() {
        return accno;
    }

    public ConfigData getConfigdata() {
        return configdata;
    }

    public void setConfigdata(ConfigData configdata) {
        this.configdata = configdata;
    }

    public void setAccno(String accno) {
        this.accno = accno;
    }

    public MasterData getDepartment() {
        return department;
    }

    public void setDepartment(MasterData department) {
        this.department = department;
    }

    public MasterData getDesignationid() {
        return designationid;
    }

    public void setDesignationid(MasterData designationid) {
        this.designationid = designationid;
    }

    public Integer getDummystatus() {
        return dummystatus;
    }
    
    public CostCenter getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

    public void setDummystatus(Integer dummystatus) {
        this.dummystatus = dummystatus;
    }

    public Integer getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(Integer employeeid) {
        this.employeeid = employeeid;
    }

    public String getEmployeeIdFormat() {
		return employeeIdFormat;
	}

	public void setEmployeeIdFormat(String employeeIdFormat) {
		this.employeeIdFormat = employeeIdFormat;
	}

	public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Set<Assignmanager> getAssignmanagers() {
        return assignmanagers;
    }

    public void setAssignmanagers(Set<Assignmanager> assignmanagers) {
        this.assignmanagers = assignmanagers;
    }

    public Set<Assignreviewer> getAssignreviewers() {
        return assignreviewers;
    }

    public void setAssignreviewers(Set<Assignreviewer> assignreviewers) {
        this.assignreviewers = assignreviewers;
    }

    public Empprofile getEmpProfile() {
        return empProfile;
    }

    public void setEmpProfile(Empprofile empProfile) {
        this.empProfile = empProfile;
    }

    public Set<Empexperience> getEmpexperience() {
        return empexperience;
    }

    public void setEmpexperience(Set<Empexperience> empexperience) {
        this.empexperience = empexperience;
    }

    public boolean isSalarymanager() {
        return salarymanager;
    }

    public void setSalarymanager(boolean salarymanager) {
        this.salarymanager = salarymanager;
    }

}
