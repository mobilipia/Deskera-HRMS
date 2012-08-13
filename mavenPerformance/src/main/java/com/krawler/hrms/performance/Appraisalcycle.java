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

package com.krawler.hrms.performance;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class Appraisalcycle {
    private String id;
    private String cyclename;
    private Date startdate;
    private Date enddate;
    private User createdby;
    private Company company;
    private Date submitstartdate;
    private Date submitenddate;
    private boolean cycleapproval;
    private boolean reviewed;
    public Appraisalcycle() {
    }

    public Appraisalcycle(String id, String cyclename, Date startdate, Date enddate, User createdby, Company company, Date submitstartdate, Date submitenddate, boolean cycleapproval) {
        this.id = id;
        this.cyclename = cyclename;
        this.startdate = startdate;
        this.enddate = enddate;
        this.createdby = createdby;
        this.company = company;
        this.submitstartdate = submitstartdate;
        this.submitenddate = submitenddate;
        this.cycleapproval = cycleapproval;
    }
    
    public String getCyclename() {
        return cyclename;
    }

    public void setCyclename(String cyclename) {
        this.cyclename = cyclename;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public User getCreatedby() {
        return createdby;
    }

    public void setCreatedby(User createdby) {
        this.createdby = createdby;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getSubmitenddate() {
        return submitenddate;
    }

    public void setSubmitenddate(Date submitenddate) {
        this.submitenddate = submitenddate;
    }

    public Date getSubmitstartdate() {
        return submitstartdate;
    }

    public void setSubmitstartdate(Date submitstartdate) {
        this.submitstartdate = submitstartdate;
    }

    public boolean isCycleapproval() {
        return cycleapproval;
    }

    public void setCycleapproval(boolean cycleapproval) {
        this.cycleapproval = cycleapproval;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    
}
