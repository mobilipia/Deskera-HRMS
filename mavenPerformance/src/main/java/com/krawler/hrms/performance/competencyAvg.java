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
public class competencyAvg {
    private String id;
    private User employee;
    private Appraisalcycle appcycle;
    private Managecmpt competency;
    private double manavg;

    public competencyAvg() {
    }

    public Appraisalcycle getAppcycle() {
        return appcycle;
    }

    public void setAppcycle(Appraisalcycle appcycle) {
        this.appcycle = appcycle;
    }

    public Managecmpt getCompetency() {
        return competency;
    }

    public void setCompetency(Managecmpt competency) {
        this.competency = competency;
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

    public double getManavg() {
        return manavg;
    }

    public void setManavg(double manavg) {
        this.manavg = manavg;
    }

    
}
