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
package com.krawler.spring.hrms.payroll;

import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import masterDB.*;
;


/**
 *
 * @author shs
 */

public interface hrmsPayrollDAO {

    KwlReturnObject getPaycomponent_date(HashMap<String,Object> requestParams);
    public KwlReturnObject addPaycomponent_date(HashMap<String, Object> requestParams);
    public KwlReturnObject deletePaycomponent_date(String componentid);
    public List<ComponentResourceMapping> getAssignedComponent(String userid);
    public List<ComponentResourceMappingHistory> getAssignedComponentsToResource(String userid, Date enddate, int frequency);
    public List<ComponentResourceMappingHistory> getSalaryComponentForEmployee(String userid, Date startdate, Date enddate, boolean  showAll);
    public boolean addAssignedComponent(String userid, String componentid);
    public ComponentResourceMappingHistory editAssignedComponentsToResource(String id, double amount);
    public boolean deleteAssignedComponents(String userid);
    public KwlReturnObject deleteComponentOfResource(StringBuffer userids,Date startdate, Date enddate, int frequency);
    public KwlReturnObject deleteGeneratePayrollData(StringBuffer userids,Date startdate, Date enddate, Integer frequency);
    public boolean assignComponentOfResource(String userid, Componentmaster component, double amount, Date startdate, Date enddate, int frequency);
    KwlReturnObject getPayrollHistory(HashMap<String,Object> requestParams);
    public boolean isResourceMappingExit(String userid, Date startdate, Date enddate); 
    public KwlReturnObject calculatePayroll(JSONObject jobj);
    KwlReturnObject updatePayrollHistory(HashMap<String, Object> requestParams);
    Componentmaster getComponentObj(String componentid);
    List<ComponentResourceMappingHistory> getSalaryDetails(String userid, Date enddate, int frequency, int type, Integer otherRemuneration);
    public boolean editResorcePayrollData(HashMap<String, Object> requestParams);
    public KwlReturnObject getGeneratedPayrollList(HashMap<String, Object> requestParams);
    public KwlReturnObject getPayrollUserList(StringBuffer userList,String companyid, String searchText, int frequency, String start, String limit);
    public List<PayrollHistory> getGeneratedSalaries(String userid ,Date startdate, Date enddate);
    public List<Componentmaster> getDefaultComponents(String companyid, int frequency);
    public List<Componentmaster> getAvailableComponent(StringBuffer assignedComponentList, String companyid ,int frequency);
    public boolean editAssignFrequency(String[] empids, int frequency);
    public KwlReturnObject getDependentComponent(String parentComponent, Date startdate, Date enddate, String userid);
    public List<ComponentResourceMappingHistory> getComponentsToResourceHistoryForComponent(String userid, int frequency, Date enddate, String componentid);
    public List<Componentmaster> getComputeOnComponents(String companyid, String componentid, int frequency);
    public List<Componentmaster> getComponentmaster(HashMap<String,Object> requestParams);
    public List<ComponentResourceMapping> getComponentResourceMapping(HashMap<String,Object> requestParams);
    public List<ComponentResourceMappingHistory> getComponentResourceMappingHistory(HashMap<String,Object> requestParams);
    public List<ComponentResourceMappingHistory> getYearlySalaryComponentsForEmployee(String userid, String componentid, Date startdate, Date enddate);
    public List<PayrollHistory> getPayslips(String userid, int status);
    public List<SpecifiedComponents> getSpecifiedComponents(String masterComponent);
    public List<SpecifiedComponents> getDependentSpecifiedComponents(String component);
    public boolean deleteSpecifiedComponents(String masterComponent);
    public List<PayrollHistory> getGeneratedSalariesForMonth(String companyid , Date enddate, String frequecy);
    public PayrollHistory getPayrollHistoryForUser(String userid , Date enddate, int frequency, int status);
    public PayrollHistory getPayrollHistoryForUser(String userid , Date enddate, int frequency);
    public List<PayrollHistory> getGeneratedSalariesForUser(String userid ,Date startdate, Date enddate,int status, String frequency);
    public boolean editSalaryStatus(String[] historyid, int status, String comment);
    public List<Componentmaster> getDepenentCompoment(String compid);
    public boolean setComponentRuleObject(ComponentRule componentRuleObj);
    public List<ComponentRule> getComponentsRules(String componentid);
    public boolean deleteRule(String ruleid);
    public boolean deleteComponentRule(String componentid);
    public List<ComponentResourceMapping> getUserIncomeTaxComponent(String userid, Date year);
    List<UserTaxDeclaration> getUserIncomeTaxComponent(String userid, StringBuffer componentList);
    public boolean saveUserIncomeTaxDeclaration(HashMap<Componentmaster,String> map, String userid, Double savings);
    public boolean deleteComponentUserTaxDeclaration(String componentid);
    public List<ComponentResourceMappingHistory> getIncomeTaxComponentCalculationForUser(String userid, Date enddate, int frequency);
    public List<ComponentRule> getIncomeTaxComponentRule(String componentid);
}





