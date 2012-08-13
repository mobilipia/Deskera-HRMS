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

import com.krawler.common.admin.User;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class Finalgoalmanagement {

    private String id;
    private User userID;
    private String goalname;
    private String goaldesc;
    private Integer goalwth;
    private String priority;
    private String context;
    private Date startdate;
    private Date enddate;
    private String comment;
    private String assignedby;
    private Date createdon;
    private Date updatedon;
    private Integer archivedflag;
    private Integer percentcomplete;
    private boolean deleted;
    private boolean internal; //  true - HRMS GOAL / false - External Goal
    private boolean completed;
    private User manager;
    private User updatedBy;
    public static final int MaxGoalRate = 5;

    public Finalgoalmanagement() {
    }

    public Finalgoalmanagement(String id, User userID, String goalname, String goaldesc, Integer goalwth, String priority, String context, Date startdate, Date enddate, String comment, String assignedby, Date createdon, Date updatedon, Integer archivedflag, boolean deleted, User manager, Integer percentcomplete) {
        this.id = id;
        this.userID = userID;
        this.goalname = goalname;
        this.goaldesc = goaldesc;
        this.goalwth = goalwth;
        this.priority = priority;
        this.context = context;
        this.startdate = startdate;
        this.enddate = enddate;
        this.comment = comment;
        this.assignedby = assignedby;
        this.createdon = createdon;
        this.updatedon = updatedon;
        this.archivedflag = archivedflag;
        this.deleted = deleted;
        this.manager = manager;
        this.percentcomplete = percentcomplete;
    }

    public Integer getPercentcomplete() {
        return percentcomplete;
    }

    public void setPercentcomplete(Integer percentcomplete) {
        this.percentcomplete = percentcomplete;
    }
  
    public Date getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
    }

    public Date getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(Date updatedon) {
        this.updatedon = updatedon;
    }

    public String getAssignedby() {
        return assignedby;
    }

    public void setAssignedby(String assignedby) {
        this.assignedby = assignedby;
    }

    public String getcomment() {
        return comment;
    }

    public void setcomment(String comment) {
        this.comment = comment;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getGoaldesc() {
        return goaldesc;
    }

    public void setGoaldesc(String goaldesc) {
        this.goaldesc = goaldesc;
    }

    public String getGoalname() {
        return goalname;
    }

    public void setGoalname(String goalname) {
        this.goalname = goalname;
    }

    public Integer getGoalwth() {
        return goalwth;
    }

    public void setGoalwth(Integer goalwth) {
        this.goalwth = goalwth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public User getUserID() {
        return userID;
    }

    public void setUserID(User userID) {
        this.userID = userID;
    }

    public Integer getArchivedflag() {
        return archivedflag;
    }

    public void setArchivedflag(Integer archivedflag) {
        this.archivedflag = archivedflag;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }


    /**
     * @return the completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * @return the internal
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * @param internal the internal to set
     */
    public void setInternal(boolean internal) {
        this.internal = internal;
    }

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}
}
