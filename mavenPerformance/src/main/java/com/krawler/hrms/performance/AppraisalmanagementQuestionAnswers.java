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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krawler
 */
public class AppraisalmanagementQuestionAnswers {

    private String appraisalQAid;
    private User employee;
    private User manager;
    private CompetencyQuestion cmptquestion;
    private Set<AppraisalQuestionAnswers> appraisalquestionanswers=new HashSet<AppraisalQuestionAnswers>(0);
    private Appraisalcycle appcycle;
    

    public String getAppraisalQAid() {
        return appraisalQAid;
    }

    public void setAppraisalQAid(String appraisalQAid) {
        this.appraisalQAid = appraisalQAid;
    }

    public Appraisalcycle getAppcycle() {
        return appcycle;
    }

    public void setAppcycle(Appraisalcycle appcycle) {
        this.appcycle = appcycle;
    }
    
    public CompetencyQuestion getCmptquestion() {
        return cmptquestion;
    }

    public void setCmptquestion(CompetencyQuestion cmptquestion) {
        this.cmptquestion = cmptquestion;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Set<AppraisalQuestionAnswers> getAppraisalquestionanswers() {
        return appraisalquestionanswers;
    }

    public void setAppraisalquestionanswers(Set<AppraisalQuestionAnswers> appraisalquestionanswers) {
        this.appraisalquestionanswers = appraisalquestionanswers;
    }

    
}
