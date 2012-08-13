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
public class GoalComments {

    private String commentid;
    private User userid;
    private Finalgoalmanagement goalid;
    private String comment;
    private Date createdon;

    public GoalComments() {
    }
       
    public GoalComments(String commentid, User userid, Finalgoalmanagement goalid, String comment, Date createdon) {
        this.commentid = commentid;
        this.userid = userid;
        this.goalid = goalid;
        this.comment = comment;
        this.createdon = createdon;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public Date getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
    }

    public Finalgoalmanagement getGoalid() {
        return goalid;
    }

    public void setGoalid(Finalgoalmanagement goalid) {
        this.goalid = goalid;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }
}
