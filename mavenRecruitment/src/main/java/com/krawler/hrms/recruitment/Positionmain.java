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

/**
 *
 * @author krawler
 */
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.hrms.master.MasterData;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author krawler
 */
public class Positionmain implements java.io.Serializable{

    private String positionid;
    private MasterData position;
    private String details;
    private String  department;
    private String apprvmangr;
    private Date startdate;
    private Date enddate;
    private String jobtype;
    private Company company;
    private Integer delflag;
    private String travel;
    private String relocation;
    private String location;
    private String jobshift;
    private Integer experiencemonth;
    private Integer experienceyear;
    private Set<Applicants>applicantes=new HashSet<Applicants>(0);
    private Set<Allapplications>allapplicationses=new HashSet<Allapplications>(0);
    private MasterData departmentid;
    private User manager;
    private Integer jobidwthformat;
    private String jobid;
    private Integer noofpos;
    private User createdBy;
    private Integer positionsfilled;
    public Positionmain() {
    }

    public Positionmain(String positionid, MasterData position, String details, String department, String apprvmangr, Date startdate, Date enddate, String jobtype, Company company, Integer delflag, String travel, String relocation, String location, String jobshift, Integer experiencemonth, Integer experienceyear, MasterData departmentid, User manager, Integer jobidwthformat, Integer noofpos, User createdBy, Integer positionsfilled, String jobid) {
        this.positionid = positionid;
        this.position = position;
        this.details = details;
        this.department = department;
        this.apprvmangr = apprvmangr;
        this.startdate = startdate;
        this.enddate = enddate;
        this.jobtype = jobtype;
        this.company = company;
        this.delflag = delflag;
        this.travel = travel;
        this.relocation = relocation;
        this.location = location;
        this.jobshift = jobshift;
        this.experiencemonth = experiencemonth;
        this.experienceyear = experienceyear;
        this.departmentid = departmentid;
        this.manager = manager;
        this.jobidwthformat = jobidwthformat;
        this.noofpos = noofpos;
        this.createdBy = createdBy;
        this.positionsfilled = positionsfilled;
        this.jobid = jobid;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

   public Set<Allapplications> getAllapplicationses() {
        return allapplicationses;
    }

    public void setAllapplicationses(Set<Allapplications> allapplicationses) {
        this.allapplicationses = allapplicationses;
    }

    public Set<Applicants> getApplicantes() {
        return applicantes;
    }

    public void setApplicantes(Set<Applicants> applicantes) {
        this.applicantes = applicantes;
    }

    public String getApprvmangr() {
        return apprvmangr;
    }

    public void setApprvmangr(String apprvmangr) {
        this.apprvmangr = apprvmangr;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getJobtype() {
        return jobtype;
    }

    public void setJobtype(String jobtype) {
        this.jobtype = jobtype;
    }

    public MasterData getPosition() {
        return position;
    }

    public void setPosition(MasterData position) {
        this.position = position;
    }

    public String getPositionid() {
        return positionid;
    }

    public void setPositionid(String positionid) {
        this.positionid = positionid;
    }
   
    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
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

    public Integer getExperiencemonth() {
        return experiencemonth;
    }

    public void setExperiencemonth(Integer experiencemonth) {
        this.experiencemonth = experiencemonth;
    }

    public Integer getExperienceyear() {
        return experienceyear;
    }

    public void setExperienceyear(Integer experienceyear) {
        this.experienceyear = experienceyear;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRelocation() {
        return relocation;
    }

    public void setRelocation(String relocation) {
        this.relocation = relocation;
    }

    public String getTravel() {
        return travel;
    }

    public void setTravel(String travel) {
        this.travel = travel;
    }

    public String getJobshift() {
        return jobshift;
    }

    public void setJobshift(String jobshift) {
        this.jobshift = jobshift;
    }

    public MasterData getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(MasterData departmentid) {
        this.departmentid = departmentid;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
    
    public Integer getJobidwthformat() {
        return jobidwthformat;
    }

    public void setJobidwthformat(Integer jobidwthformat) {
        this.jobidwthformat = jobidwthformat;
    }

    public Integer getNoofpos() {
        return noofpos;
    }

    public void setNoofpos(Integer noofpos) {
        this.noofpos = noofpos;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getPositionsfilled() {
        return positionsfilled;
    }

    public void setPositionsfilled(Integer positionsfilled) {
        this.positionsfilled = positionsfilled;
    }

}
