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
package com.krawler.spring.hrms.goal;

import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import java.util.List;
import com.krawler.hrms.performance.Finalgoalmanagement;

public interface hrmsGoalDAO {

    public KwlReturnObject Employeesgoalfinal(HashMap<String, Object> requestParams);
    public KwlReturnObject getGoalCommentsWithID(HashMap<String, Object> requestParams);
    public KwlReturnObject getFinalgoalmanagement(HashMap<String,Object> requestParams);
    public KwlReturnObject addCommentsfunction(HashMap<String, Object> requestParams);
    public KwlReturnObject insertGoal(HashMap<String, Object> requestParams);
    public KwlReturnObject changeMyGoalPercent(HashMap<String, Object> requestParams);
    public KwlReturnObject assignedgoalsdelete(HashMap<String, Object> requestParams);
    public KwlReturnObject archivedgoalsfunction(HashMap<String, Object> requestParams);
    public KwlReturnObject getGoalsforAppraisal(HashMap<String, Object> requestParams);
    public KwlReturnObject getgoalassignedupdate(HashMap<String,Object> requestParams);
    public KwlReturnObject getGoalEditUpdates(HashMap<String,Object> requestParams);
    public List<Finalgoalmanagement> getGoals(HashMap<String, Object> requestParams);
}
