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
public class AppraisalQuestionAnswers {

    private String questionanswerid;
    private String answer;
    private int orderofans;
    private Appraisalmanagement appraisal;
    private AppraisalmanagementQuestionAnswers appraisalmanagementquestionanswers;

    public int getOrderofans() {
        return orderofans;
    }

    public void setOrderofans(int orderofans) {
        this.orderofans = orderofans;
    }

    public Appraisalmanagement getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisalmanagement appraisal) {
        this.appraisal = appraisal;
    }
    
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public AppraisalmanagementQuestionAnswers getAppraisalmanagementquestionanswers() {
        return appraisalmanagementquestionanswers;
    }

    public void setAppraisalmanagementquestionanswers(AppraisalmanagementQuestionAnswers appraisalmanagementquestionanswers) {
        this.appraisalmanagementquestionanswers = appraisalmanagementquestionanswers;
    }

    public String getQuestionanswerid() {
        return questionanswerid;
    }

    public void setQuestionanswerid(String questionanswerid) {
        this.questionanswerid = questionanswerid;
    }
    
}
