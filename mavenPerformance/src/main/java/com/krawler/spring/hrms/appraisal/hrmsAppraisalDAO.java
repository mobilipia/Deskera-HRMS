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
package com.krawler.spring.hrms.appraisal;

import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import java.util.List;

public interface hrmsAppraisalDAO {

    public KwlReturnObject getEmployeeAppraisals(HashMap<String, Object> requestParams);

    public Appraisalmanagement AppraisalAssign(HashMap<String, Object> requestParams);

    public KwlReturnObject getAppraisal(HashMap<String, Object> requestParams);

    public KwlReturnObject getAppraisalforAppCyc(HashMap<String, Object> requestParams);

    public KwlReturnObject getCompAvgScore(HashMap<String, Object> requestParams);

    public KwlReturnObject appraisalFunction(HashMap<String, Object> requestParams);

    public KwlReturnObject getcompetencyAvg(HashMap<String, Object> requestParams);

    public KwlReturnObject addcompetencyAvg(HashMap<String, Object> requestParams);

    public KwlReturnObject saveAnswers(HashMap<String, Object> requestParams);
    
    public List<Appraisalmanagement> getAppraisalmanagements(HashMap<String, Object> requestParams);
    
    public List<CompetencyQuestion> getCompetencyQuestions(HashMap<String, Object> requestParams);
}
