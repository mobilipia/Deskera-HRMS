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

package com.krawler.spring.organizationChart.bizservice;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.ess.Empprofile;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.organizationChart.organizationChartDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;
/**
 *
 * @author Kuldeep Singh
 */
public class OrganizationServiceDAOImpl implements OrganizationServiceDAO, MessageSourceAware{
	
	private static final Log logger = LogFactory.getLog(OrganizationServiceDAOImpl.class);

    private organizationChartDAO organizationChartDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private MessageSource messageSource;

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    public void setorganizationChartDAO(organizationChartDAO organizationChartDAOObj1) {
        this.organizationChartDAOObj = organizationChartDAOObj1;
    }
    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    @Override
	public void setMessageSource(MessageSource ms) {
		messageSource = ms;
	}
    
    public String getEmployeeIDforUser(Useraccount useraccount, String companyid)throws ServiceException{

         String employeeID="";
         try {
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("companyid",companyid);
                requestParams.put("empid",useraccount.getEmployeeid());

                if(useraccount.getEmployeeIdFormat()==null){
                	employeeID= useraccount.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString();
                }else{
                	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                	requestParams.put("employeeIdFormat", useraccount.getEmployeeIdFormat());
                    employeeID= profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams);
                }
        } catch (Exception ex) {
            logger.warn("General exception in getEmployeeIDforUser()", ex);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getEmployeeIDforUser", ex);

        }
        return employeeID;
    }

    public void rootUserJson(JSONArray jarr, Useraccount userAccount, String companyid, Locale locale) throws ServiceException {
        JSONObject objU = new JSONObject();
        try {
                objU.put("fromuid", userAccount.getUserID());
                objU.put("userid", userAccount.getUserID());
                objU.put("employeeid", getEmployeeIDforUser( userAccount, companyid));
                objU.put("accno", userAccount.getAccno());
                objU.put("username", userAccount.getUser().getUserLogin().getUserName());
                objU.put("emailid", userAccount.getUser().getEmailID());
                objU.put("contactno", userAccount.getUser().getContactNumber());
                objU.put("fname", userAccount.getUser().getFirstName());
                objU.put("lname", userAccount.getUser().getLastName());
                objU.put("image", userAccount.getUser().getImage());
                objU.put("nodeid", userAccount.getUser().getUserID());
                objU.put("address", userAccount.getUser().getAddress());
                objU.put("level", 0);
                objU.put("department", userAccount.getDepartment()!=null? userAccount.getDepartment().getId():"");
                objU.put("designation", userAccount.getDesignationid()!=null? userAccount.getDesignationid().getValue():"");
                objU.put("designationid", userAccount.getDesignationid()!=null?userAccount.getDesignationid().getId():"");
                if(userAccount!=null && userAccount.getRole()!=null && userAccount.getRole().getCompany()==null){//For Admin, Manager or Employee
                	objU.put("role", messageSource.getMessage("hrms.common.role."+userAccount.getRole().getID(), null, locale)+"("+messageSource.getMessage("hrms.common.you.are.here", null, locale)+")");
                }else{//For custome roles
                	String role = userAccount.getRole()!=null? userAccount.getRole().getName():"";
                	objU.put("role", role+"("+messageSource.getMessage("hrms.common.you.are.here", null, locale)+")");
                }
                objU.put("roleid", userAccount.getRole()!=null? userAccount.getRole().getID():"");

                jarr.put(objU);

        } catch (JSONException ex) {
            logger.warn("General exception in rootUserJson()", ex);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.rootUser", ex);
        }
    }

    public JSONObject getMappedUserJson(List appendedList, JSONArray jarr, String parentid, String companyid, Locale locale) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = appendedList.iterator();

            while (ite.hasNext()) {
                Empprofile empProfile = (Empprofile) ite.next();
                JSONObject obj = new JSONObject();
                int level = 9;
                if (StringUtil.isNullOrEmpty(parentid) || !parentid.equals(empProfile.getUserID())) {

                    obj.put("fromuid", empProfile.getReportto().getUserID());
                    obj.put("userid", empProfile.getUserID());
                    obj.put("employeeid", getEmployeeIDforUser(empProfile.getUseraccount(), companyid));
                    obj.put("accno", empProfile.getUseraccount().getAccno());
                    obj.put("department", empProfile.getUseraccount().getDepartment()!=null?empProfile.getUseraccount().getDepartment().getId():"");
                    obj.put("username", empProfile.getUserLogin().getUserName());
                    obj.put("emailid", empProfile.getUseraccount().getUser().getEmailID());
                    obj.put("contactno", empProfile.getUseraccount().getUser().getContactNumber());
                    obj.put("fname", empProfile.getUseraccount().getUser().getFirstName());
                    obj.put("lname", empProfile.getUseraccount().getUser().getLastName());
                    obj.put("image", empProfile.getUseraccount().getUser().getImage());
                    obj.put("nodeid", empProfile.getUserID());
                    obj.put("address", empProfile.getUseraccount().getUser().getAddress());
                    obj.put("level", level);
                    if(empProfile!=null && empProfile.getUseraccount()!=null && empProfile.getUseraccount().getRole()!=null && empProfile.getUseraccount().getRole().getCompany()==null){//For Admin, Manager or Employee
                    	obj.put("role", messageSource.getMessage("hrms.common.role."+empProfile.getUseraccount().getRole().getID(), null, locale));
                    }else{//For custome roles
                    	String role = empProfile.getUseraccount().getRole()!=null? empProfile.getUseraccount().getRole().getName():"";
                    	obj.put("role", role);
                    }
                    obj.put("roleid", empProfile.getUseraccount().getRole()!=null? empProfile.getUseraccount().getRole().getID():"");
                    obj.put("designation", empProfile.getUseraccount().getDesignationid()!=null?empProfile.getUseraccount().getDesignationid().getValue():"");
                    obj.put("designationid", empProfile.getUseraccount().getDesignationid()!=null?empProfile.getUseraccount().getDesignationid().getId():"");

                    jarr.put(obj);
                }
            }
            jobj.put("data", jarr);
        } catch (JSONException e) {
            logger.warn("JSON exception in getMappedUserJson()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getMappedUserJson", e);
        } catch (Exception e) {
            logger.warn("General exception in getMappedUserJson()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getMappedUserJson", e);
        }finally {
        }
        return jobj;
    }


     public JSONObject getUnmappedUsersJson(List ll, int totalSize, Locale locale) throws ServiceException{

        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();

        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                User user = (User) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", user.getUserLogin().getUserName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID().toString());
                if (ua!=null) {
                    if(ua!=null && ua.getRole()!=null && ua.getRole().getCompany()==null){//For Admin, Manager or Employee
                        obj.put("role", messageSource.getMessage("hrms.common.role."+ua.getRole().getID(), null, locale));
                    } else {
                        obj.put("role", ua.getRole()!=null? ua.getRole().getName():"");
                    }
                    obj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                } else {
                    obj.put("role", "");
                    obj.put("designation", "");
                }
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (JSONException e) {
            logger.warn("JSON exception in getUnmappedUsersJson()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getUnmappedUsersJson", e);
        } catch (Exception e) {
            logger.warn("General exception in getUnmappedUsersJson()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getUnmappedUsersJson", e);
        }
        return jobj;
    }

    @Override
    public JSONObject getUnmappedUsers(String companyid,Locale locale) throws ServiceException {

        JSONObject jobj = new JSONObject();

        KwlReturnObject kmsg = null;
        try {

            kmsg = organizationChartDAOObj.getUnmappedUsers(companyid);
            jobj = getUnmappedUsersJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(), locale);


        } catch (Exception e) {
            logger.warn("General exception in getUnmappedUsers()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getUnmappedUsers", e);
        }
        return jobj;
    }

    @Override
    public HashMap<String, Object> insertNode(String parentid, String childid) throws ServiceException {

        boolean success = false;
        HashMap<String, Object> hm = new HashMap<String, Object>();
        try {

            if (organizationChartDAOObj.isPerfectRole(parentid, childid)) {

                success = organizationChartDAOObj.insertNode(parentid, childid);

                hm.put("parent", organizationChartDAOObj.getUser(parentid));
                hm.put("child", organizationChartDAOObj.getUser(childid));

            }
            hm.put("success", success);

        } catch (JSONException e) {
            logger.warn("JSON exception in insertNode()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.insertNode", e);
        } catch (Exception e) {
            logger.warn("General exception in insertNode()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.insertNode", e);
        }
        return hm;
    }

    @Override
    public JSONObject getMappedUsers(String userid, String companyid, Locale locale) throws ServiceException {

        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int exceptionAt = 0;
        List appendList = new ArrayList();
        String extraQuery = "";
        String parentid = "";
        try {

            Useraccount ua = organizationChartDAOObj.rootUser(userid, companyid);
            if(ua!=null){
                rootUserJson(jarr, ua, companyid, locale);
            }
            organizationChartDAOObj.getAssignManager(userid, appendList, exceptionAt, extraQuery);

            jobj = getMappedUserJson(appendList, jarr, parentid, companyid, locale);


        } catch (Exception e) {
            logger.warn("General exception in getMappedUsers()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getMappedUsers", e);
        }
        return jobj;
    }

    @Override
    public JSONObject getGridMappedUsers(String parentid, String childid, String userid, String companyid, Locale locale) throws ServiceException {

        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List appendList = new ArrayList();
        int dl = 0;

        try {

            String extraQuery = " and userID !='" + childid + "'";

            Useraccount ua = organizationChartDAOObj.rootUser(userid, companyid);
            if(ua!=null){
                rootUserJson(jarr, ua, companyid, locale);
            }
            organizationChartDAOObj.getAssignManager(userid, appendList, dl, extraQuery);

            jobj = getMappedUserJson(appendList, jarr, parentid, companyid, locale);


        } catch (Exception e) {
            logger.warn("General exception in getGridMappedUsers()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.getGridMappedUsers", e);
        }
        return jobj;
    }

    @Override
    public HashMap<String, Object> updateNode(String parentid, String childid) throws ServiceException {

        boolean success = false;

        HashMap<String, Object> hm = new HashMap<String, Object>();
        try {

            if(!StringUtil.isNullOrEmpty(parentid) && !StringUtil.isNullOrEmpty(childid)){
                if (organizationChartDAOObj.isPerfectRole(parentid, childid)) {

                    success = organizationChartDAOObj.updateNode(parentid, childid);

                    hm.put("parent", organizationChartDAOObj.getUser(parentid));
                    hm.put("child", organizationChartDAOObj.getUser(childid));

                }
            }

            hm.put("success", success);

        } catch (JSONException e) {
            logger.warn("JSON exception in updateNode()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.updateNode", e);
        } catch (Exception e) {
            logger.warn("General exception in updateNode()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.updateNode", e);
        }
        return hm;
    }

    @Override
    public HashMap<String, Object> deleteNode(String nodeid) throws ServiceException {

        boolean success = false;
        HashMap<String, Object> hm = new HashMap<String, Object>();
        try {

                  User parentNode = organizationChartDAOObj.getParentNode(nodeid);

                  List<Empprofile> childList = organizationChartDAOObj.getChildNode(nodeid);
                  
                  String [] empProfileIds= new String[childList.size()];

                  for(int i=0; i< childList.size();i++){

                      Empprofile emp = (Empprofile)childList.get(i);
                      empProfileIds[i] = emp.getUserID();
                  }

                  if(empProfileIds.length > 0 && parentNode !=null){
                      success = organizationChartDAOObj.assignNewParent(empProfileIds, parentNode);
                  }
                  
                  Empprofile deletedEmployee = organizationChartDAOObj.deleteNode(nodeid);

                  if(deletedEmployee!=null && parentNode !=null){
                	  success = true;
                  }

                hm.put("success", success);
                hm.put("childList", childList);
                hm.put("deletedEmployee", deletedEmployee);
                hm.put("parentEmployee", parentNode);

        } catch (JSONException e) {
            logger.warn("JSON exception in deleteNode()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.deleteNode", e);
        } catch (Exception e) {
            logger.warn("General exception in deleteNode()", e);
            throw ServiceException.FAILURE("OrganizationServiceDAOImpl.deleteNode", e);
        }
        return hm;
    }
}
