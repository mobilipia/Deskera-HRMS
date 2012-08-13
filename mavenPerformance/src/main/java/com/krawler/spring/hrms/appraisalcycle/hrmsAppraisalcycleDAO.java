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
package com.krawler.spring.hrms.appraisalcycle;

import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;

public interface hrmsAppraisalcycleDAO {
    public KwlReturnObject getAppraisalCycle(HashMap<String,Object> requestParams);
    public KwlReturnObject getAppraisalcycle1(HashMap<String, Object> requestParams);
    public KwlReturnObject getAppraisalcycle2(HashMap<String, Object> requestParams);
    public KwlReturnObject addAppraisalcycle(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteAppraisalcycle(HashMap<String, Object> requestParams);
    public KwlReturnObject getAppraisalmanagement(HashMap<String,Object> requestParams);
    public KwlReturnObject getAppraisalcycleform(HashMap<String, Object> requestParams);
    public KwlReturnObject getAppraisalcycleemployee(HashMap<String, Object> requestParams);
    public KwlReturnObject getAppraisallistbyManager(HashMap<String, Object> requestParams);
    public KwlReturnObject addAppraisalmanagement(HashMap<String, Object> requestParams);
    public KwlReturnObject getUsersforAppCyc(HashMap<String, Object> requestParams);
    public KwlReturnObject getUsersforAppCycleMYSQL(HashMap<String, Object> requestParams);
    public KwlReturnObject getAppraisalManagers(HashMap<String, Object> requestParams);
    public KwlReturnObject submitAppraisalCheck(HashMap<String, Object> requestParams);
    public KwlReturnObject getappraisalcycleUpdates(HashMap<String, Object> requestParams);
    public KwlReturnObject updateappraisalstatus(HashMap<String,Object> requestParams);
    public KwlReturnObject getAppraisalapprovalupdate(HashMap<String,Object> requestParams);
    public KwlReturnObject getFillappraisalupdate(HashMap<String,Object> requestParams);
    public KwlReturnObject getAppraisalcycleupdatesDetail(HashMap<String,Object> requestParams);
    public KwlReturnObject showappraisalForm(HashMap<String,Object>requestParams);
    public KwlReturnObject isReviewer(HashMap<String,Object>requestParams);
     KwlReturnObject getQuestionAnswerGrid(HashMap<String, Object> requestParams);
     KwlReturnObject getQuestions(HashMap<String, Object> requestParams);
}
