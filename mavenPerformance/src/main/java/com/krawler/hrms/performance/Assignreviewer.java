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

/**
 *
 * @author krawler
 */
public class Assignreviewer {
     private String id;
     private User employee;
     private User reviewer;
     private Integer reviewerstatus;

    public Assignreviewer() {
    }

    public Assignreviewer(String id, User employee, User reviewer, Integer reviewerstatus) {
        this.id = id;
        this.employee = employee;
        this.reviewer = reviewer;
        this.reviewerstatus = reviewerstatus;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public Integer getReviewerstatus() {
        return reviewerstatus;
    }

    public void setReviewerstatus(Integer reviewerstatus) {
        this.reviewerstatus = reviewerstatus;
    }


}
