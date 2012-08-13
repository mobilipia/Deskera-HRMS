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

import com.krawler.common.admin.User;
import com.krawler.hrms.master.MasterData;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class Emphistory {
   public static final String Emp_Desg_change="1";
   public static final String Emp_Appraisal="2";
   public static final String Emp_Salary="3";
   private String hid;
   private User userid;
   private MasterData designation;
   private MasterData department;
   private String salary;
   private Integer empid;
   private Date updatedon;
   private Date joindate;
   private Date enddate;
   private User updatedby;
   private String category;

    public Emphistory() {
    }

    public MasterData getDepartment() {
        return department;
    }

    public void setDepartment(MasterData department) {
        this.department = department;
    }

    public MasterData getDesignation() {
        return designation;
    }

    public void setDesignation(MasterData designation) {
        this.designation = designation;
    }

    public Integer getEmpid() {
        return empid;
    }

    public void setEmpid(Integer empid) {
        this.empid = empid;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public User getUpdatedby() {
        return updatedby;
    }

    public void setUpdatedby(User updatedby) {
        this.updatedby = updatedby;
    }

    public Date getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(Date updatedon) {
        this.updatedon = updatedon;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public Date getJoindate() {
        return joindate;
    }

    public void setJoindate(Date joindate) {
        this.joindate = joindate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
   
}
