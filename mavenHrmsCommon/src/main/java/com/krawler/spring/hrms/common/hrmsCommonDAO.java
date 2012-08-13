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

package com.krawler.spring.hrms.common;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.UserSearchState;
import com.krawler.common.admin.Useraccount;
import com.krawler.hrms.ess.Empexperience;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author shs
 */
public interface hrmsCommonDAO {
    public KwlReturnObject getMasterDataField(HashMap<String,Object> requestParams);
    public KwlReturnObject getManagers(HashMap<String,Object> requestParams);
    public KwlReturnObject getEmpDetailsbyEmpid(HashMap<String, Object> requestParams);

    
    public KwlReturnObject getUserDetailsbyUserid(HashMap<String, Object> requestParams);
    public KwlReturnObject getUserDetails(HashMap<String, Object> requestParams);
    public KwlReturnObject getMasterData(HashMap<String,Object> requestParams);
    public KwlReturnObject getEmployeeList(HashMap<String,Object> requestParams);
    public KwlReturnObject getEmpprofileuser(HashMap<String,Object> requestParams);
    public KwlReturnObject getEmpidFormatEdit(HashMap<String,Object> requestParameters);
    public KwlReturnObject getUserDateFormatter(HashMap<String,Object> requestParams);
    public KwlReturnObject getEmpForManagerFunction(HashMap<String,Object> requestParams);
    public KwlReturnObject getUserList(HashMap<String, Object> requestParams);
    public KwlReturnObject gethrms_EmailTemplates(HashMap<String,Object> requestParams);
    public int insertConfigData(HttpServletRequest request, String string, String userid, String companyid, HashMap<String, Object> requestParams);
    public KwlReturnObject isEmployee(HashMap<String, Object> requestParams);
    public KwlReturnObject isManager(HashMap<String, Object> requestParams);
    public KwlReturnObject getAssignmanager(HashMap<String, Object> requestParams);
    public KwlReturnObject getAssignSalaryManager(HashMap<String, Object> requestParams);
    public KwlReturnObject getAssignreviewer(HashMap<String, Object> requestParams);
    public KwlReturnObject getManagerCountforUserid(HashMap<String, Object> requestParams);
    public KwlReturnObject getReviewerCountforUserid(HashMap<String, Object> requestParams);
    public KwlReturnObject getEmpProfile(HashMap<String,Object> requestParams);
    public KwlReturnObject addEmpprofile(HashMap<String, Object> requestParams);
    public KwlReturnObject addEmphistory(HashMap<String, Object> requestParams);
    public KwlReturnObject getEmpHistory(HashMap<String, Object> requestParams);
    KwlReturnObject getLastUpdatedHistory(HashMap<String, Object> requestParams);
    public KwlReturnObject adduser(HashMap<String, Object> requestParams);
    public KwlReturnObject getHrmsmodule(Locale localObj);
    public KwlReturnObject getUseraccount(HashMap<String,Object> requestParams);
    public CompanyPreferences getCompanyPreferences(String companyid);
    public KwlReturnObject checkForAdminTermination(HashMap<String,Object> requestParams);
    public KwlReturnObject getAdmins(HashMap<String,Object> requestParams);
    public KwlReturnObject getReportingTo(HashMap<String,Object> requestParams);
    public KwlReturnObject getUserDetailsHrms(HashMap<String, Object> requestParams);
    public KwlReturnObject checkModule(HashMap<String, Object> requestParams);
    public KwlReturnObject isSubscribed(HashMap<String, Object> requestParams);
    public boolean isPermitted(JSONObject perms, String featureName, String activityName);
    public KwlReturnObject getUsers(HashMap<String,Object> requestParams);
    public KwlReturnObject saveUser( HashMap<String, Object> requestParams ,Locale localObj);
    public KwlReturnObject saveSearch( String searchName,int searchFlag, String searchState, String userid );
    public List<UserSearchState> getSavedSearchesForUser(String userid);
    public KwlReturnObject addAssignmanager(HashMap<String, Object> requestParams);
    public KwlReturnObject addAssignSalaryManager(String [] managerids, String []availmanagerid);
    public KwlReturnObject addAssignreviewer(HashMap<String, Object> requestParams);
    public KwlReturnObject addUseraccount(HashMap<String, Object> requestParams);
    public boolean addEmpExperience(Empexperience empexp);
    public int insertConfigData(HttpServletRequest request, String formtype, String referenceid, String companyid);
    public KwlReturnObject getPayHistory(HashMap<String, Object> requestParams);
    public boolean isAdmin(String userid);
    public boolean isSalaryManager(String userid);
    public boolean editEmpProfileForTerOfFutureDates();
    public boolean editUserForTerOfFutureDates();
    public boolean editEmpProfileForRehOfFutureDates();
    public boolean editUserForRehOfFutureDates();
    public Useraccount getUseraccountByUserId(String userId);
    public String getSysEmailIdByCompanyID(String companyid);
    public boolean isCompanySuperAdmin(String userid, String companyid);
    public String getUserDateFormat(String userid);
    public KwlReturnObject getCostCenter(HashMap<String,Object> requestParams);
    public boolean checkForSearchName(String searchName, String userid);
    public boolean deleteSavedSearch(String searchid);
    public KwlReturnObject getSavedSearch( String searchid, int searchFlag );
}
