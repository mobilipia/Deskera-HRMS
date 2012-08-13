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

package com.krawler.spring.hrms.anonymousappraisal;

import java.util.HashMap;

/**
 *
 * @author krawler
 */
public class hrmsAnonymousAppraisalConstants {

    public  static final  String question="question";
    public  static final  String export="export";
    public  static final  String dot=".";
    public  static final  String pdf_break="~";
    public  static final  String html_break="<br/>";
    public  static final  HashMap<Integer,String> break_string=new HashMap<Integer, String>();
    static{
        break_string.put(1,pdf_break);
        break_string.put(2,html_break);
    }
    public  static final  String Answer_Prefix="Fb";
    public  static final  String filter_names="filter_names";
    public  static final  String order_by="order_by";
    public  static final  String order_type="order_type";
    public  static final  String filter_params="filter_params";
    public  static final  String asc="asc";
    public  static final  String answer="answer";
    public  static final  String employeeanswer="employeeanswer";
    public  static final  String userid="userid";
    public  static final  String reviewerid="reviewerid";
    public  static final  String appraisalcycid="appraisalcycid";
    public  static final  String appcycleId="appcycle.id";
    public  static final  String employeeUserID="employee.userID";
    public  static final  String managerUserID="manager.userID";
    public  static final  String cmptquestionQuesid="cmptquestion.quesid";
    public  static final  String cmptquestionOrder="cmptquestion.quesorder";
    public  static final  String success="success";
    public  static final  String data="data";
    public  static final  String totalCount="totalCount";
    public  static final  String valid="valid";
    public  static final  String alias_apqa="apqa";
    public  static final  String alias_emp="emp";
    public  static final  String alias_man="man";
    public  static final  String alias_appc="appc";
    public  static final  String alias_cmptq="cmptq";

    public  static final  String employee="employee";
    public  static final  String manager="manager";
    public  static final  String appcycle="appcycle";
    public  static final  String cmptquestion="cmptquestion";

    public  static final  String userID="userID";
    public  static final  String id="id";
    public  static final  String quesid="quesid";
    public  static final  String question_success="Question are fetched successfully";
    
}
