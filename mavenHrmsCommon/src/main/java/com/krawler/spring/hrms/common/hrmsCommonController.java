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
import com.krawler.common.admin.CostCenter;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.UserSearchState;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.ess.Empexperience;
import com.krawler.hrms.ess.Emphistory;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Assignmanager;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.customcol.customcolDAO;
import com.krawler.spring.organizationChart.bizservice.OrganizationServiceDAO;
import com.krawler.spring.organizationChart.organizationChartDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.Payhistory;
import org.apache.commons.fileupload.FileItem;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author shs
 */
public class hrmsCommonController extends MultiActionController implements MessageSourceAware{

    private String successView;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private customcolDAO customcolDAOObj;
    private HibernateTransactionManager txnManager;
    private exportDAOImpl exportDAOImplObj;
    private auditTrailDAO auditTrailDAOObj;
    private organizationChartDAO organizationChartDAOObj;
    private OrganizationServiceDAO organizationService;
    private MessageSource messageSource;

    public profileHandlerDAO getProfileHandlerDAOObj() {
        return profileHandlerDAOObj;
    }
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }
    public void setCustomcolDAO(customcolDAO customcolDAOObj) {
        this.customcolDAOObj = customcolDAOObj;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    
    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }


    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
 
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setorganizationChartDAO(organizationChartDAO organizationChartDAOObj1) {
        this.organizationChartDAOObj = organizationChartDAOObj1;
    }

    public void setOrganizationServiceDAO(OrganizationServiceDAO organizationService) {
        this.organizationService = organizationService;
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }

    public ModelAndView getMasterDataField(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        int count = 0;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
//            String hql = "from MasterData where masterid.id=? and ( company is null or company.companyID=? )order by value ";
//            Integer i = Integer.parseInt(request.getParameter("configid"));
//            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{i,sessionHandlerImplObj.getCompanyid(request)});
            HashMap<String,Object> requestParams = new HashMap<String,Object>();
            String configid = request.getParameter("configid");
            requestParams.put("masterid", Integer.parseInt(configid));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            result = hrmsCommonDAOObj.getMasterDataField(requestParams);
            List lst = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for (Integer i = 0; i < lst.size(); i++) {
                MasterData mst = (MasterData) lst.get(i);
                JSONObject tmpObj = new JSONObject();
                if(StringUtil.equal(configid, Constants.callBack_CONFIGID)){
                    tmpObj.put("id", mst.getId());
                    tmpObj.put("name", messageSource.getMessage("hrms.recruitment.callback."+mst.getValue(),null, RequestContextUtils.getLocale(request)));
                }else {
                    tmpObj.put("id", mst.getId());
                    tmpObj.put("name", mst.getValue());
                }
                
                if(configid.equals(Constants.payComponent_CONFIGID)){//Component Type
                    tmpObj.put("weightage", mst.getComponenttype());
                }else if(configid.equals(Constants.timesheetjob_CONFIGID)){//Timesheet Job Type
                    tmpObj.put("weightage", mst.getWorktime());
                } else {
                    tmpObj.put("weightage", mst.getWeightage());
                }
//                jobj.append("data", tmpObj);
                jArr.put(tmpObj);
            }
            jobj.put("data",jArr);
            jobj.put("count", result.getRecordTotalCount());
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    public ModelAndView getCostCenter(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        int count = 0;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String,Object> requestParams = new HashMap<String,Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            result = hrmsCommonDAOObj.getCostCenter(requestParams);
            List lst = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for (Integer i = 0; i < lst.size(); i++) {
                CostCenter mst = (CostCenter) lst.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", mst.getId());
                tmpObj.put("name", mst.getName());
                tmpObj.put("code", mst.getCode());
                tmpObj.put("creationDate", mst.getCreationDate());
//                jobj.append("data", tmpObj);
                jArr.put(tmpObj);
            }
            jobj.put("data",jArr);
            jobj.put("count", result.getRecordTotalCount());
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getManagers(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String,Object> requestParams = new HashMap<String,Object>();
            requestParams.put("role1", 2);
            requestParams.put("role2", 1);
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("deleted", false);
            result = hrmsCommonDAOObj.getManagers(requestParams);
            List list = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for (Integer ctr=0;ctr<list.size();ctr++) {
                Useraccount ua = (Useraccount) list.get(ctr);
                User u = ua.getUser();
                JSONObject obj = new JSONObject();
                obj.put("userid", u.getUserID());
                obj.put("username", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("name", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                obj.put("department", ua.getDepartment()!=null?ua.getDepartment().getValue():"");
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getAdmins(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String,Object> requestParams = new HashMap<String,Object>();
            requestParams.put("roleid", 1);
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("deleted", 0);
            result = hrmsCommonDAOObj.getAdmins(requestParams);
            List list = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for (Integer ctr=0;ctr<list.size();ctr++) {
                Useraccount ua = (Useraccount) list.get(ctr);
                User u = ua.getUser();
                JSONObject obj = new JSONObject();
                obj.put("userid", u.getUserID());
                obj.put("username", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("name", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                obj.put("department", ua.getDepartment()!=null?ua.getDepartment().getValue():"");
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getReportingTo(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String,Object> requestParams = new HashMap<String,Object>();
            requestParams.put("roleid", Role.COMPANY_USER);
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("deleted", 0);
            result = hrmsCommonDAOObj.getReportingTo(requestParams);
            List list = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for (Integer ctr=0;ctr<list.size();ctr++) {
                Useraccount ua = (Useraccount) list.get(ctr);
                User u = ua.getUser();
                JSONObject obj = new JSONObject();
                obj.put("userid", u.getUserID());
                obj.put("username", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("name", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                obj.put("department", ua.getDepartment()!=null?ua.getDepartment().getValue():"");
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getEmployeeByAttribute(HttpServletRequest request, HttpServletResponse response){
         JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String,Object> requestParams = new HashMap<String,Object>();
            requestParams.put("searchcol", new String[]{request.getParameter("searchcol")});
            requestParams.put("ss", request.getParameter("ss"));
            
            result = hrmsCommonDAOObj.getEmployeeList(requestParams);
            List list = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for (Integer ctr=0;ctr<list.size();ctr++) {
                Useraccount ua = (Useraccount) list.get(ctr);
                User u = ua.getUser();
                JSONObject obj = new JSONObject();
                obj.put("userid", u.getUserID());
                obj.put("username", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("fullname", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getAllUserDetailsHrms(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject countobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            String Searchjson = request.getParameter("searchJson");
            String appendCase = "and";
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String lid = StringUtil.checkForNull(request.getParameter("lid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names =  new ArrayList(Arrays.asList("ua.user.company.companyID","ua.user.deleteflag"));
            ArrayList filter_values = new ArrayList(Arrays.asList(companyid,0));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("allflag", false);
            requestParams.put("searchcol", new String[]{"u.firstName","u.lastName","ua.role.name","u.emailID"});
            if(request.getParameter("combo")!=null) {
                requestParams.put("combo",request.getParameter("combo"));
                requestParams.put("allflag", true);
            } else {
                requestParams.put("combo","");
            }
            StringUtil.checkpaging(requestParams, request);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            if(!StringUtil.isNullOrEmpty(request.getParameter("stdate"))){
                filter_names.add(">=emp.joindate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("stdate")))));
                filter_names.add("<=emp.joindate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("enddate")))));
            }
            
            if (!StringUtil.isNullOrEmpty(Searchjson)) {
                getMyAdvanceSearchparams(Searchjson, filter_names);
                insertParamAdvanceSearchString(filter_values, Searchjson);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            kmsg = hrmsCommonDAOObj.getUserDetailsHrms(requestParams);
            List lst = kmsg.getEntityList();
            jarr = kwlCommonTablesDAOObj.getDetailsJson(lst,0,"com.krawler.common.admin.User");
            
            int count = 0;
            for(int ctr=0;ctr<jarr.length();ctr++){
                jobj = jarr.getJSONObject(ctr);
                Object[] row = (Object[]) lst.get(ctr);
                User u = (User)jobj.get("instance");
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", row[0].toString());
                if (row[1] != null) {
                    Empprofile e = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile",row[1].toString());
                    if (!StringUtil.isNullOrEmpty(e.getStatus())) {
                        jobj.put("status", e.getStatus());
                    } else {
                        jobj.put("status", "Pending");
                    }
                    jobj.put("joindate", (e.getJoindate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(e.getJoindate())));
                } else {
                    jobj.put("status", "Incomplete");
                }
                jobj.put("department", (ua.getDepartment() == null ? "" : ua.getDepartment().getId()));
                jobj.put("departmentname", (ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                jobj.put("role", (ua.getRole() == null ? "" : ua.getRole().getID()));
                String name="";
                if(ua.getRole()!=null&&ua.getRole().getCompany()!=null){
                	name = ua.getRole().getName();
                }else{
                	name = messageSource.getMessage("hrms.common.role."+ua.getRole().getID(),null, ua.getRole().getName(), RequestContextUtils.getLocale(request));
                }
                jobj.put("rolename", (ua.getRole() == null ? "" : name));
                jobj.put("username", u.getUserLogin().getUserName());
                jobj.put("fullname",u.getFirstName()+" "+ (u.getLastName()==null?"":u.getLastName()));
                jobj.put("lastlogin", (u.getUserLogin().getLastActivityDate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(u.getUserLogin().getLastActivityDate())));
                jobj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                jobj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                jobj.put("templateid", ua.getTemplateid()!=null?ua.getTemplateid():"");
                jobj.put("salary", ua.getSalary());
                jobj.put("accno", ua.getAccno());
                jobj.put("frequency", u.getFrequency());
                requestParams.clear();
                requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("empid",ua.getEmployeeid());
                KwlReturnObject result;
//                KwlReturnObject result = profileHandlerDAOObj.getEmpidFormatEdit(requestParams);
                if(ua.getEmployeeIdFormat()==null){
                	jobj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                }else{
                	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                    jobj.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                }

                requestParams.clear();
                filter_names.clear();
                filter_values.clear();
                filter_names.add("assignemp.userID");
                filter_values.add(u.getUserID());
                
                filter_names.add("assignman.deleteflag");
                filter_values.add(0);

                filter_names.add("managerstatus");
                filter_values.add(1);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);

                result = hrmsCommonDAOObj.getAssignmanager(requestParams);
                List lst1 = result.getEntityList();
                Iterator itr1 = lst1.iterator();

                if (itr1.hasNext()) {
                    while (itr1.hasNext()) {
                        Assignmanager asm = (Assignmanager) itr1.next();
                        if (asm.getAssignman() != null) {
                            jobj.append("managerid", asm.getAssignman().getUserID());
                            jobj.append("manager", asm.getAssignman().getFirstName() + " " + asm.getAssignman().getLastName());
                        }
                    }
                } else {
                    jobj.put("manager", " ");
                    jobj.put("managerid", " ");
                }

                requestParams.clear();
                filter_names.clear();
                filter_values.clear();
                filter_names.add("employee.userID");
                filter_values.add(u.getUserID());
                
                filter_names.add("reviewer.deleteflag");
                filter_values.add(0);
                
                filter_names.add("reviewerstatus");
                filter_values.add(1);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);


                result = hrmsCommonDAOObj.getAssignreviewer(requestParams);
                lst1 = result.getEntityList();
                itr1 = lst1.iterator();


                if (itr1.hasNext()) {
                    while (itr1.hasNext()) {
                        Assignreviewer rev = (Assignreviewer) itr1.next();
                        if (rev.getReviewer() != null) {
                            jobj.append("reviewerid", rev.getReviewer().getUserID());
                            jobj.append("reviewer", rev.getReviewer().getFirstName() + " " + rev.getReviewer().getLastName());
                        }
                    }
                } else {
                    jobj.put("reviewer", " ");
                    jobj.put("reviewerid", " ");
                }
                jarr.put(ctr,jobj);
                count ++;
            }

           
            countobj.put("data", jarr);
            countobj.put("count", kmsg.getRecordTotalCount());
            jobj1.put("data", countobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView getEmpProfile(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        String platformURL = this.getServletContext().getInitParameter("platformURL");
        String userid;
        int count = 0;
        try {
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
//            String hql = "from Empprofile where userID=?  ";
//            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("userid")});
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("primary", true);
            requestParams.put("userid", request.getParameter("userid"));
            result = hrmsCommonDAOObj.getEmpProfile(requestParams);
            List lst = result.getEntityList();
            if (lst.size() != 0) {
                jarr = kwlCommonTablesDAOObj.getDetailsJson(lst, -1, "com.krawler.hrms.ess.Empprofile");
                for (int ctr = 0; ctr < jarr.length(); ctr++) {
                    JSONObject tmpObj = jarr.getJSONObject(ctr);
                    Empprofile extmt = (Empprofile) tmpObj.get("instance");
                    String image="";
                    if(StringUtil.isStandAlone()){
                        image = extmt.getUserLogin().getUser().getImage();
                    }else {
                        image= StringUtil.getAppsImagePath(platformURL, extmt.getUserID(), 100);
                    }
                    User u = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("userid"));
                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", extmt.getUserID());
                    tmpObj.put("fname", extmt.getUserLogin().getUser().getFirstName());
                    tmpObj.put("lname", extmt.getUserLogin().getUser().getLastName());
                    tmpObj.put("image", image);
                    tmpObj.put("aboutuser", extmt.getUserLogin().getUser().getAboutUser() == null ? "" : extmt.getUserLogin().getUser().getAboutUser());
                    String middlename = (extmt.getMiddlename() != null ? extmt.getMiddlename() : "");
                    tmpObj.put("fullname", extmt.getUserLogin().getUser().getFirstName() + " " + middlename + " " + extmt.getUserLogin().getUser().getLastName());
                    tmpObj.put("bankacc", ua.getAccno());
//                    tmpObj.put("empid", extmt.getUserLogin().getUser().getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, extmt.getUserLogin().getUser().getEmployeeid()));
                    requestParams.clear();
                    requestParams.put("companyid", cmpid);
                    requestParams.put("empid", ua.getEmployeeid());
                    if(ua.getEmployeeIdFormat()==null){
                    	tmpObj.put("empid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                    }else{
                    	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                    	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                    	tmpObj.put("empid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                    }
                    tmpObj.put("department", ua.getDepartment() == null ? "" : ua.getDepartment().getId());
                    tmpObj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                    tmpObj.put("costcenter", ua.getCostCenter() == null ? "" : ua.getCostCenter().getId());
                    tmpObj.put("frequency", u.getFrequency());
                    tmpObj.put("managername", (extmt.getReportto() == null ? "" : extmt.getReportto().getUserID()));
                    tmpObj.put("precountry", (extmt.getPresentcountry() == null ? "" : extmt.getPresentcountry().getId()));
                    tmpObj.put("permcountry", (extmt.getPermcountry() == null ? "" : extmt.getPermcountry().getId()));
                    tmpObj.put("trainingmon", (extmt.getTrainperiod() == null ? "" : extmt.getTrainperiod().substring(0, Math.max(0, extmt.getTrainperiod().indexOf(",")))));
                    tmpObj.put("trainingyr", (extmt.getTrainperiod() == null ? "" : extmt.getTrainperiod().substring(Math.max(0, extmt.getTrainperiod().indexOf(",") + 1))));
                    tmpObj.put("probationmon", (extmt.getProbperiod() == null ? "" : extmt.getProbperiod().substring(0, Math.max(0, extmt.getProbperiod().indexOf(",")))));
                    tmpObj.put("probationyr", (extmt.getProbperiod() == null ? "" : extmt.getProbperiod().substring(Math.max(0, extmt.getProbperiod().indexOf(",") + 1))));
                    tmpObj.put("noticemon", (extmt.getNoticeperiod() == null ? "" : extmt.getNoticeperiod().substring(0, Math.max(0, extmt.getNoticeperiod().indexOf(",")))));
                    tmpObj.put("noticeyr", (extmt.getNoticeperiod() == null ? "" : extmt.getNoticeperiod().substring(Math.max(0, extmt.getNoticeperiod().indexOf(",") + 1))));
                    tmpObj.put("brachcountry", (extmt.getBranchcountry() == null ? "" : extmt.getBranchcountry().getId()));
                    jarr.put(ctr, tmpObj);
                }
            } else {
                JSONObject tmpObj = new JSONObject();
                User u = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("userid"));
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", u.getUserID());
                requestParams.clear();
                requestParams.put("companyid", cmpid);
                requestParams.put("empid", ua.getEmployeeid());
                if(ua.getEmployeeIdFormat()==null){
                	tmpObj.put("empid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                }else{
                	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                	tmpObj.put("empid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                }
                
                String image="";
                if(StringUtil.isStandAlone()){
                    image = u.getImage();
                }else {
                    image= StringUtil.getAppsImagePath(platformURL, u.getUserID(), 100);
                }
                
                tmpObj.put("department", ua.getDepartment() == null ? "" : ua.getDepartment().getId());
                tmpObj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                tmpObj.put("costcenter", ua.getCostCenter() == null ? "" : ua.getCostCenter().getId());
                tmpObj.put("frequency", u.getFrequency());
                tmpObj.put("fname", u.getFirstName());
                tmpObj.put("lname", u.getLastName());
                tmpObj.put("image", image);
                tmpObj.put("aboutuser", u.getAboutUser() == null ? "" : u.getAboutUser());
                tmpObj.put("fullname", u.getFirstName() + " " + (u.getLastName() == null ? "" : u.getLastName()));
                tmpObj.put("bankacc", ua.getAccno());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", result.getRecordTotalCount());
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }


    public ModelAndView approveprofile(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
           String[] ids = request.getParameterValues("emp_ids");
            for (int i = 0; i < ids.length; i++)
            {
                requestParams.put("userid", ids[i]);
                requestParams.put("status", "Approved");
                requestParams.put("updated_by", sessionHandlerImplObj.getUserid(request));
                requestParams.put("updated_on", new Date());
                result = hrmsCommonDAOObj.addEmpprofile(requestParams);
                jobj.put("success", "true");
                jobj1.put("valid", true);
                jobj1.put("data", jobj.toString());
            }
           txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
       public ModelAndView terminateEmp(HttpServletRequest request, HttpServletResponse response){
       String hql="";
       SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
       SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
       JSONObject msg = new JSONObject();

       //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

       try {
    	   boolean isCompanySuperAdmin = false;
           Empprofile e = null;
           String ids[] = request.getParameterValues("ids");
           String masterid=request.getParameter("tercause");
           String desc=((StringUtil.isNullOrEmpty(request.getParameter("terdesc")))?"":request.getParameter("terdesc"));
           String reldate=request.getParameter("relievedate");
           Date rvdate=fmt.parse(reldate);
           Empprofile emp=null;
           UserLogin usl=null;
           User usr=null;
           Date now = new Date();
//           User updatedby=(User)session.get(User.class,sessionHandlerImplObj.getUserid(request));
          // User updatedby=(User)kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", sessionHandlerImplObj.getUserid(request));
           String updatedby=sessionHandlerImplObj.getUserid(request);
           
           String companyid = sessionHandlerImplObj.getCompanyid(request);
           for(int i=0; i<ids.length; i++){
        	   if(hrmsCommonDAOObj.isCompanySuperAdmin(ids[i], companyid)){
        		   isCompanySuperAdmin = true;
        		   break;
        	   }
           }
            if(isCompanySuperAdmin) {
            	msg.put("msg", "adminerror");
                msg.put("success", true);
                txnManager.commit(status);
                return  new ModelAndView("jsonView","model",msg.toString());
            } 

            for (int i = 0; i < ids.length; i++) {
                e = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", ids[i]);
                if(e!=null){
                    if(e.getJoindate()!=null){
                        if(e.getJoindate().after(rvdate)){
                            msg.put("msg", "invalidterminatedate");
                            msg.put("success", true);
                            txnManager.commit(status);
                            return  new ModelAndView("jsonView","model",msg.toString());
                        }
                    }
                }
//                hql = "from Empprofile where userLogin.userID=?";
//                List lst = HibernateUtil.executeQuery(session, hql, new Object[]{ids[i]});
////                usl=(UserLogin)session.get(UserLogin.class,ids[i]);
//                usl=(UserLogin)HibernateUtil.getPrimary("com.krawler.common.admin.UserLogin", "userID");
////                usr=(User)session.get(User.class,ids[i]);
//                usr=(User)HibernateUtil.getPrimary("com.krawler.common.admin.User", "userID");
//                if(lst.isEmpty()){
//                          emp=new Empprofile();
//                          emp.setUserLogin(usl);
//                }else{
//                  emp=(Empprofile)session.get(Empprofile.class,ids[i]);
//                }
//                 Emphistory ehst=new Emphistory();
//                 ehst.setUserid(usr);
//                 ehst.setDepartment(usr.getDepartment());
//                 ehst.setDesignation(usr.getDesignationid());
//                 ehst.setSalary(usr.getSalary());
//                 ehst.setEmpid(usr.getEmployeeid());
//                 ehst.setUpdatedon(rvdate);
//                 ehst.setJoindate(emp.getJoindate());
//                 ehst.setEnddate(rvdate);
//                 ehst.setUpdatedby(updatedby);
//                 ehst.setCategory(Emphistory.Emp_Desg_change);
//                 emp.setTerminatedby(updatedby);
//                 emp.setTercause((MasterData)session.get(MasterData.class,masterid));
//                 emp.setRelievedate(rvdate);
//                 emp.setTerReason(desc);
//                 emp.setTermnd(true);
//                 usr.setDeleted(true);
//                 session.saveOrUpdate(ehst);
//                 session.saveOrUpdate(emp);
//                 session.saveOrUpdate(usr);
//                 //@@ProfileHandler.insertAuditLog(session, AuditAction.EMPLOYEE_TERMINATED, "Employee " + sessionHandlerImplObj.getFullName(usr) + " terminated by " + sessionHandlerImplObj.getFullName(session, sessionHandlerImplObj.getUserid(request)),request);
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                if(rvdate.before(now) || rvdate.equals(now)){
                    requestParams.put("termnd", true);
                }
                requestParams.put("userid", ids[i]);
                requestParams.put("terminatedby", updatedby);
                requestParams.put("tercause", masterid);
                requestParams.put("terReason", desc);
                requestParams.put("relievedate", reldate);
                KwlReturnObject result = hrmsCommonDAOObj.addEmpprofile(requestParams);

                HashMap<String, Object> deleteJobj = organizationService.deleteNode(ids[i]);

                if(rvdate.before(now) || rvdate.equals(now)){
                    requestParams.clear();
                    requestParams.put("UserID", ids[i]);
                    requestParams.put("Deleteflag", 1);
                    hrmsCommonDAOObj.adduser(requestParams);
                }
            }
           msg.put("success", true);
           txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",msg.toString());
        }
    }
//
//       public ModelAndView gethrmsModules(){
//        JSONObject jobj = new JSONObject();
//        KwlReturnObject result = null;
//        try {
////            String SELECT_USER_INFO = "from hrms_Modules";
////            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO);
//            result = hrmsCommonDAOObj.getHrmsmodule();
//            List list  = result.getEntityList();
//            Iterator itr = list.iterator();
//            JSONArray jArr = new JSONArray();
//            while (itr.hasNext()) {
//                hrms_Modules u = (hrms_Modules) itr.next();
//                JSONObject obj = new JSONObject();
//                obj.put("moduleid", u.getModuleID());
//                obj.put("modulename",u.getModuleName() );
//                obj.put("moduledispname",u.getDisplayModuleName());
//                jArr.put(obj);
//            }
//            jobj.put("data", jArr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            return new ModelAndView("jsonView","model",jobj.toString());
//
//        }
//    }
 public ModelAndView getEmpForManagerFunction(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jArr = new JSONArray();
        int count = 0;
        Iterator ite;
        String start, limit;
        try {
            String userid=sessionHandlerImplObj.getUserid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            if (request.getParameter("start") == null) {
                start = "0";
                limit = "15";
            } else {
                start = request.getParameter("start");
                limit = request.getParameter("limit");
            }
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("paging", request.getParameter("paging"));
            requestParams.put("start", start);
            requestParams.put("limit", limit);

            if (hrmsCommonDAOObj.isAdmin(userid)) {
                result = hrmsCommonDAOObj.getUserList(requestParams);
                count = result.getRecordTotalCount();
                ite = result.getEntityList().iterator();
                while (ite.hasNext()) {
//                    User log = (User) ite.next();
//                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", log.getUserID());

                    Useraccount ua = (Useraccount) ite.next();
                    User log = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", ua.getUserID());//use ua.user
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getUserID());
                    tmpObj.put("username", log.getFirstName());
                    tmpObj.put("designationid", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getId()));
                    tmpObj.put("designation", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue()));
                    tmpObj.put("emailid", log.getEmailID());
                    tmpObj.put("contactno", log.getContactNumber());
                    tmpObj.put("fullname", log.getFirstName() + " " + (log.getLastName() == null ? "" : log.getLastName()));
                    jArr.put(tmpObj);
                }
            } else if(hrmsCommonDAOObj.isManager(requestParams).isSuccessFlag()){
                result = hrmsCommonDAOObj.getEmpForManagerFunction(requestParams);
                count = result.getRecordTotalCount();
                ite = result.getEntityList().iterator();
                while (ite.hasNext()) {
                    Empprofile log = (Empprofile) ite.next();
                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", log.getUserID());
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getUserID());
                    tmpObj.put("username", log.getUserLogin().getUser().getFirstName());
                    tmpObj.put("designationid", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getId()));
                    tmpObj.put("designation", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue()));
                    tmpObj.put("emailid", log.getUserLogin().getUser().getEmailID());
                    tmpObj.put("contactno", log.getUserLogin().getUser().getContactNumber());
                    tmpObj.put("fullname", log.getUserLogin().getUser().getFirstName() + " " + (log.getUserLogin().getUser().getLastName() == null ? "" : log.getUserLogin().getUser().getLastName()));
                    jArr.put(tmpObj);
                }
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getparticularUserDetails(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;

        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            String companyid = sessionHandlerImplObj.getCompanyid(request);

            String lid = request.getParameter("lid");

            filter_names.add("userID");
            filter_values.add(lid);

            filter_names.add("company.companyID");
            filter_values.add(companyid);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            result = hrmsCommonDAOObj.getUsers(requestParams);

            List list = result.getEntityList();

            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                User user = (User) itr.next();
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
                UserLogin ul = user.getUserLogin();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", ul.getUserName());
                obj.put("firstname", user.getFirstName());
                obj.put("lastname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());
                obj.put("lastlogin", (ul.getLastActivityDate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(ul.getLastActivityDate())));
                obj.put("aboutuser", user.getAboutUser());
                obj.put("address", user.getAddress());
                obj.put("contactno", user.getContactNumber());
                obj.put("formatid", (user.getDateFormat() == null ? "4" : user.getDateFormat().getFormatID()));
                obj.put("tzid", (user.getTimeZone() == null ? "23" : user.getTimeZone().getTimeZoneID()));
                requestParams.clear();
                requestParams.put("companyid", companyid);
                requestParams.put("empid", ua.getEmployeeid());
                if(ua.getEmployeeIdFormat()==null){
                	obj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                }else{
                	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                	obj.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                }
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView saveUser(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        Integer codeid2 = null;
        KwlReturnObject result = null;
        String msg = "";
        int roleflag=0;
        String employeeIdFormat = "";
        boolean isStadardEmpFormatWithIdAvilable = false;
        
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            HashMap newhm = new FileUploadHandler().getItems(request);
            HashMap<String,String> hm = new HashMap<String,String>();
            for(Object key: newhm.keySet()){
                hm.put(key.toString(), new String (newhm.get(key.toString()).toString().getBytes ("iso-8859-1"), "UTF-8"));
            }
            String id = (String) hm.get("userid");
            //String lastname = (String) hm.get("lastname");
            //lastname = new String (lastname.getBytes ("iso-8859-1"), "UTF-8");

            if (!StringUtil.isNullOrEmpty((String) hm.get("employeeid"))) {
                String[] codeid = ((String) hm.get("employeeid")).split("-");

                for (int x = 0; x < codeid.length; x++) {
                    if (codeid[x].matches("[0-9]*") == true) {
                        codeid2 = Integer.parseInt(codeid[x]);
                    }else{
                    	employeeIdFormat += (codeid[x]+"-");
                    }
                }
                if(employeeIdFormat.length()>0){
                	employeeIdFormat = employeeIdFormat.substring(0, employeeIdFormat.length() - 1);
                }
            }
            if(StringUtil.isNullOrEmpty(employeeIdFormat))
            	employeeIdFormat = null;
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String pwd = null;

            if (!StringUtil.isNullOrEmpty(id)) {
                requestParams.clear();

//                filter_names.add("employeeid");
//                filter_values.add(codeid2);
//
//                filter_names.add("userID");
//                filter_values.add(id);
//
//                filter_names.add("company.companyID");
//                filter_values.add(companyid);
//
//                requestParams.put("filter_names", filter_names);
//                requestParams.put("filter_values", filter_values);
//
//                result = hrmsCommonDAOObj.getUsers(requestParams);
//                if (result.getEntityList().isEmpty()) {
                	requestParams.put("employeeIdFormat", employeeIdFormat);
                	requestParams.put("userID", id);
                	requestParams.put("employeeid", codeid2);
                	requestParams.put("request", request);
                	isStadardEmpFormatWithIdAvilable = isStadardEmpFormatWithIdAvilable(requestParams);                		
                	String standardEmpId = getStadardEmpFormat(requestParams);
                	if(standardEmpId!=null && employeeIdFormat!=null && standardEmpId.equals(employeeIdFormat)){
                		employeeIdFormat = null;
                	}
                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("employeeid");
                    filter_values.add(codeid2);
                    
                    if(employeeIdFormat == null){
                    	filter_names.add("IS employeeIdFormat");
                    }else{
                    	filter_names.add("employeeIdFormat");
                    	filter_values.add(employeeIdFormat);
                    }
                    filter_names.add("!userID");
                    filter_values.add(id);

                    filter_names.add("user.company.companyID");
                    filter_values.add(companyid);

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);

                    result = hrmsCommonDAOObj.getUseraccount(requestParams);

                    if (!result.getEntityList().isEmpty() || isStadardEmpFormatWithIdAvilable) {
                        throw new Exception("Employee ID already present.");
                    }
//                }
                requestParams.clear();
                requestParams.put("id", id);
                if (((String) hm.get("formname")).equals("user")) {
                    if (!StringUtil.isNullOrEmpty((String) hm.get("templateid"))) {
                        requestParams.put("templateid", (String) hm.get("templateid"));
                    } else {
                        requestParams.put("templateid", " ");
                    }
                }
            } else {
                requestParams.clear();
                filter_names.clear();
                filter_values.clear();

                filter_names.add("userLogin.userName");
                filter_values.add(hm.get("username"));

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);

                result = hrmsCommonDAOObj.getUsers(requestParams);
                if (!result.getEntityList().isEmpty()) {
                    throw new Exception("User name not available.");
                }
                requestParams.clear();
                filter_names.clear();
                filter_values.clear();

                filter_names.add("employeeid");
                filter_values.add(codeid2);

                filter_names.add("company.companyID");
                filter_values.add(companyid);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);

                result = hrmsCommonDAOObj.getUsers(requestParams);

                if (!result.getEntityList().isEmpty()) {
                    throw new Exception("Employee ID already present.");
                }

                requestParams.clear();
                requestParams.put("username", hm.get("username"));
                pwd = AuthHandler.generateNewPassword();
                requestParams.put("pwd", AuthHandler.getSHA1(pwd));
                requestParams.put("companyid", companyid);
            }

            requestParams.put("fname", hm.get("firstname"));
            requestParams.put("lname", hm.get("lastname"));
            requestParams.put("emailid", hm.get("emailid"));
            requestParams.put("address", hm.get("address"));
            requestParams.put("contactno", hm.get("contactnumber"));
            requestParams.put("empid", codeid2);
            requestParams.put("employeeIdFormat", employeeIdFormat);
            requestParams.put("companyid", companyid);

            int histsave = 0;
            String histdept="";
            String histdesig="";
            String histsal="";
            Date saveDate = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
            saveDate = new Date(fmt.format(saveDate));
            String updatedby = sessionHandlerImplObj.getUserid(request);
            Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", id);

            if (!StringUtil.isNullOrEmpty((String) hm.get("roleid")) && !hm.get("roleid").equals(ua.getRole().getID())) {
            	if(ua.getRole().getID().equals("1") && hrmsCommonDAOObj.isCompanySuperAdmin(id, companyid)){//Can't Edit role for super admin
                    roleflag=1;	
                }else{

                    String newRoleId = hm.get("roleid").toString();
                    if(StringUtil.equal(newRoleId, Role.COMPANY_USER)){ // Check whether new Role is Company User/ Company Employee

                        List<Empprofile> childList = organizationChartDAOObj.getChildNode(id); // Check for child list before changing its role to Employee.

                        if(childList.size()>0){
                            roleflag=2;
                        } else {
                            requestParams.put("roleid", newRoleId);
                        }
                    } else {

                        requestParams.put("roleid", newRoleId);
                    }
                    
                }
            }
            if (!StringUtil.isNullOrEmpty((String) hm.get("designationid"))) {
                if ((MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", (String) hm.get("designationid")) != ua.getDesignationid()) {
                    histdesig = (ua.getDesignationid()==null)?"":ua.getDesignationid().getId();
                    histsave = 1;
                }
                requestParams.put("designationid", hm.get("designationid"));
            }
            if (!StringUtil.isNullOrEmpty((String) hm.get("department"))) {
                if ((MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", (String) hm.get("department")) != ua.getDepartment()) {
                    histdept = (ua.getDepartment()==null)?"":ua.getDepartment().getId();
                    if (histsave == 0) {
                        histdesig = (ua.getDesignationid() == null)?"":ua.getDesignationid().getId();
                    }
                    histsave = 2;
                }
                requestParams.put("department", hm.get("department"));
            }
            if (!StringUtil.isNullOrEmpty((String) hm.get("salary"))) {
                String tempsal="0";
                if (((String) hm.get("salary")).length() > 0) {
                    tempsal= hm.get("salary").toString();
                }
                if (!tempsal.equals(ua.getSalary())) {
                    if(ua.getSalary()!=null){
                        histsal=ua.getSalary();
            }
                }
                requestParams.put("salary",tempsal);
            }

            if (!StringUtil.isNullOrEmpty((String) hm.get("accno"))) {
                if (((String) hm.get("accno")).length() > 0) {
                    requestParams.put("accno", hm.get("accno"));
                } else {
                    requestParams.put("accno", "0");
                }
            }

            if (!StringUtil.isNullOrEmpty((String) hm.get("formatid"))) {
                requestParams.put("formatid", hm.get("formatid"));
            }
            String diff = null;
            if (!StringUtil.isNullOrEmpty((String) hm.get("tzid"))) {
                KWLTimeZone timeZone = (KWLTimeZone) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.KWLTimeZone", (String) hm.get("tzid"));
                diff = timeZone.getDifference();
                requestParams.put("tzid", hm.get("tzid"));
            }
            if (!StringUtil.isNullOrEmpty((String) hm.get("aboutuser"))) {
                requestParams.put("aboutuser", hm.get("aboutuser"));
            }
            String imageName = "";
            if(newhm.get("userimage")!=null) {
                imageName = ((FileItem) (newhm.get("userimage"))).getName();
                if (!StringUtil.isNullOrEmpty(imageName)) {
                    requestParams.put("userimage", hm.get("userimage"));
                }
            }

            result = hrmsCommonDAOObj.saveUser(requestParams, RequestContextUtils.getLocale(request));
            if (!StringUtil.isNullOrEmpty(imageName)) {
                User user = (User) result.getEntityList().get(0);
                String fileName= user.getImage().substring(user.getImage().lastIndexOf("/")+1, user.getImage().length());
                new FileUploadHandler().uploadImage((FileItem) (newhm.get("userimage")),
                            fileName,
                            StorageHandler.GetProfileImgStorePath(), 100, 100, false, false);
            }
            msg = result.getMsg();
            requestParams.clear();
            if (histsave == 1) {
                histdept=ua.getDepartment().getId();
            }
            if (histsave == 1 || histsave == 2) {
                String latestUpdate = "";
                HashMap<String, Object> requestParams2 = new HashMap<String, Object>();
                requestParams2.put("id", id);
                requestParams2.put("cat",Emphistory.Emp_Desg_change);
                result = hrmsCommonDAOObj.getLastUpdatedHistory(requestParams2);
                latestUpdate = result.getEntityList().get(0).toString();
                if (!latestUpdate.equals("")) {
                    latestUpdate = latestUpdate.replace("-", "/");
                    requestParams.put("Joindate", fmt.parse(latestUpdate));
                }
                requestParams.put("Department",histdept);
                requestParams.put("Designation",histdesig);
                requestParams.put("Userid",id);
                requestParams.put("Empid",ua.getEmployeeid());
                requestParams.put("Updatedon",saveDate);
                requestParams.put("Updatedby",updatedby);
                requestParams.put("Category",Emphistory.Emp_Desg_change);
                result = hrmsCommonDAOObj.addEmphistory(requestParams);
            }
            if(!histsal.equals("")){
                requestParams.clear();
                requestParams.put("Userid",id);
                requestParams.put("Salary",histsal);
                requestParams.put("Updatedon",saveDate);
                requestParams.put("Updatedby",updatedby);
                requestParams.put("Category",Emphistory.Emp_Salary);
                result = hrmsCommonDAOObj.addEmphistory(requestParams);
            }

            sessionHandlerImplObj.updatePreferences(request, null, (StringUtil.isNullOrEmpty((String) hm.get("formatid")) ? null : (String) hm.get("formatid")), (StringUtil.isNullOrEmpty((String) hm.get("tzid")) ? null : (String) hm.get("tzid")), diff);
            if(roleflag==1) {
                msg=msg+" "+messageSource.getMessage("hrms.common.Rolecannotbechangedforsuperadministrator",null, "Role cannot be changed for Super Administrator.", RequestContextUtils.getLocale(request));
                jobj.put("roleflag", roleflag);
            }
            if(roleflag==2){
                msg=msg+" <br><br><br>"+messageSource.getMessage("hrms.common.rolecannotbechangedtocompanyemployee",null, "Note : Role cannot be changed to Company Employee. Please re-assign or remove its child node in Organization Chart before changing its role to Company Employee.", RequestContextUtils.getLocale(request));
                jobj.put("roleflag", roleflag);
            }
            jobj.put("msg", msg);
            jobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            try{
                if(e.getMessage().equals("Employee ID already present.")){
                    jobj.put("msg", e.getMessage());
                }
            }catch(Exception ex){
                e.printStackTrace();
            }
            e.printStackTrace();
            txnManager.rollback(status);
        }
            return new ModelAndView("jsonView", "model", jobj.toString());
        }

    @SuppressWarnings("finally")
	public boolean isStadardEmpFormatWithIdAvilable(HashMap<String, Object> requestParams){
    	String standardEmpIdFormat = null;
    	boolean isStadardEmpFormatAvilable = false;
    	try{
    		HttpServletRequest request = (HttpServletRequest) requestParams.get("request");
    		String employeeIdFormat = null;
    		if(requestParams.get("employeeIdFormat")!=null)
    			employeeIdFormat = requestParams.get("employeeIdFormat").toString();
    		String companyid = sessionHandlerImplObj.getCompanyid(request);
    		requestParams.put("companyid", companyid);
    		CompanyPreferences cp = hrmsCommonDAOObj.getCompanyPreferences(companyid);
    		standardEmpIdFormat = cp.getEmpidformat();
    		String[] idFormates = standardEmpIdFormat!=null?standardEmpIdFormat.split("-"):null;
    		if(standardEmpIdFormat!=null){
    			for (int i = 0; i < idFormates.length; i++) {
    				if (idFormates[i].matches("[0-9]*") != true) {
    					standardEmpIdFormat = idFormates[i];
    				}
    			}
    		}
    		if(StringUtil.isNullOrEmpty(standardEmpIdFormat))
    			standardEmpIdFormat = null;
    		ArrayList<Object> filter_names = new ArrayList(), filter_values = new ArrayList();
    		filter_names.add("employeeid");
            filter_values.add(requestParams.get("employeeid"));
            filter_names.add("!userID");
            filter_values.add(requestParams.get("userID"));
            filter_names.add("user.company.companyID");
            filter_values.add(companyid);
            if(standardEmpIdFormat == null){
            	filter_names.add("IS employeeIdFormat");
            }else{
            	filter_names.add("employeeIdFormat");
            	filter_values.add(standardEmpIdFormat);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            if(employeeIdFormat!=null && standardEmpIdFormat!=null && !hrmsCommonDAOObj.getUseraccount(requestParams).getEntityList().isEmpty()){
    			isStadardEmpFormatAvilable = standardEmpIdFormat.equals(employeeIdFormat);
    		}
        }catch (Exception e) {
			e.printStackTrace();
		}finally{
			return isStadardEmpFormatAvilable;
		}
    }
    
    @SuppressWarnings("finally")
	public String getStadardEmpFormat(HashMap<String, Object> requestParams){
    	String standardEmpIdFormat = null;
    	try{
    		HttpServletRequest request = (HttpServletRequest) requestParams.get("request");
    		String companyid = sessionHandlerImplObj.getCompanyid(request);
    		requestParams.put("companyid", companyid);
    		CompanyPreferences cp = hrmsCommonDAOObj.getCompanyPreferences(companyid);
    		standardEmpIdFormat = cp.getEmpidformat();
    		String[] idFormates = standardEmpIdFormat!=null?standardEmpIdFormat.split("-"):null;
    		if(standardEmpIdFormat!=null){
    			for (int i = 0; i < idFormates.length; i++) {
    				if (idFormates[i].matches("[0-9]*") != true) {
    					standardEmpIdFormat = idFormates[i];
    				}
    			}
    		}
        }catch (Exception e) {
			e.printStackTrace();
		}finally{
			return standardEmpIdFormat;
		}
    }
    
    public ModelAndView getAvailableManagers(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        try {
            String userid = request.getParameter("userid");
            String companyid = sessionHandlerImplObj.getCompanyid(request);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String mid[] = request.getParameterValues("managerids");
            String ids = "";
            for (int i = 0; i < mid.length; i++) {
                if (i == 0) {
                    ids = ids + " ( ";
                }
                ids = ids + "'" + mid[i] + "',";
            }
            if (mid.length != 0) {
                ids = ids.substring(0, ids.length() - 1);
                ids = ids + " )";
            }
            requestParams.clear();
            requestParams.put("filter_names", Arrays.asList("deleteflag", "company.companyID", "!userID"));
            requestParams.put("filter_values", Arrays.asList(0, companyid, userid));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("searchcol", new String[]{"firstName", "lastName"});
            requestParams.put("append", " and userID not in " + ids + " ");
            requestParams.put("allflag", false);
            StringUtil.checkpaging(requestParams, request);
            result = hrmsCommonDAOObj.getUsers(requestParams);
            Iterator ite = result.getEntityList().iterator();
            JSONArray jarr = new JSONArray();
            while (ite.hasNext()) {
                User u = (User) ite.next();
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", u.getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("userid", u.getUserID());
                tmpObj.put("username", u.getFirstName() + " " + (u.getLastName() == null ? "" : u.getLastName()));
                tmpObj.put("departmentname", ua.getDepartment() == null ? "" : ua.getDepartment().getValue());
                tmpObj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", result.getRecordTotalCount());
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getAssignedManager(HttpServletRequest request,HttpServletResponse response){
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject result = null;

        try {
            String manager = request.getParameter("manager");
            String salarymanager = request.getParameter("salarymanager");
            if (Boolean.parseBoolean(manager)) {
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names", Arrays.asList("assignemp.userID","managerstatus","assignman.deleteflag"));
                requestParams.put("filter_values", Arrays.asList(request.getParameter("userid"),1,0));
                result = hrmsCommonDAOObj.getAssignmanager(requestParams);
                tabledata = result.getEntityList();
                Iterator ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Assignmanager u = (Assignmanager) ite.next();
                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", u.getAssignman().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", u.getAssignman().getUserID());
                    tmpObj.put("username", StringUtil.getFullName(u.getAssignman()));
                    tmpObj.put("departmentname", ua.getDepartment() == null ? "" : ua.getDepartment().getValue());
                    tmpObj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                    jarr.put(tmpObj);
                }
            } else if (Boolean.parseBoolean(salarymanager)) {
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                String companyid= sessionHandlerImplObj.getCompanyid(request);
                requestParams.put("filter_names", Arrays.asList("salarymanager","user.company.companyID"));
                requestParams.put("filter_values", Arrays.asList(true,companyid));
                result = hrmsCommonDAOObj.getAssignSalaryManager(requestParams);
                tabledata = result.getEntityList();
                Iterator ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Useraccount ua = (Useraccount) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", ua.getUser().getUserID());
                    tmpObj.put("username", StringUtil.getFullName(ua.getUser()));
                    tmpObj.put("departmentname", ua.getDepartment() == null ? "" : ua.getDepartment().getValue());
                    tmpObj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                    jarr.put(tmpObj);
                }
            } else {
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                ArrayList filter_names = new ArrayList(),filter_values = new ArrayList();
                filter_names.add("employee.userID");
                filter_names.add("reviewerstatus");
                filter_names.add("reviewer.deleteflag");

                filter_values.add(request.getParameter("userid"));
                filter_values.add(1);
                filter_values.add(0);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result = hrmsCommonDAOObj.getAssignreviewer(requestParams);
                tabledata = result.getEntityList();
                Iterator ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Assignreviewer u = (Assignreviewer) ite.next();
                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", u.getReviewer().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", u.getReviewer().getUserID());
                    tmpObj.put("username", StringUtil.getFullName(u.getReviewer()));
                    tmpObj.put("departmentname", ua.getDepartment() == null ? "" : ua.getDepartment().getValue());
                    tmpObj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                    jarr.put(tmpObj);
                }
            }

            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView assignManager(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        List tabledata = null;
        String[] userids = request.getParameterValues("userid");
        String[] reviewerids = request.getParameterValues("reviewerid");
        String[] managerids = request.getParameterValues("managerid");
        String[] availmanagerid = request.getParameterValues("availmanagerid");
        JSONObject jobj = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            if (Boolean.parseBoolean(request.getParameter("isManager"))) {
                for (int i = 0; i < userids.length; i++) {
                    requestParams.put("filter_names", Arrays.asList("assignemp.userID","managerstatus"));
                    requestParams.put("filter_values", Arrays.asList(userids[i],1));
                    result = hrmsCommonDAOObj.getAssignmanager(requestParams);
                    tabledata = result.getEntityList();
                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        Assignmanager log = (Assignmanager) ite.next();
                        requestParams.clear();
                        requestParams.put("Id", log.getId());
                        requestParams.put("Managerstatus", 0);
                        hrmsCommonDAOObj.addAssignmanager(requestParams);
                    }
                }
                for (int i = 0; i < userids.length; i++) {
                    for (int j = 0; j < managerids.length; j++) {
                        requestParams.clear();
                        requestParams.put("Assignemp", userids[i]);
                        requestParams.put("Assignman", managerids[j]);
                        requestParams.put("Managerstatus", 1);
                        hrmsCommonDAOObj.addAssignmanager(requestParams);
                    }
                }
            }else if (Boolean.parseBoolean(request.getParameter("salaryManagerF"))) {
                    
                    hrmsCommonDAOObj.addAssignSalaryManager(managerids,availmanagerid);
            }
            else {
                for (int i = 0; i < userids.length; i++) {
                    requestParams.clear();
                    requestParams.put("filter_names", Arrays.asList("employee.userID","reviewerstatus"));
                    requestParams.put("filter_values", Arrays.asList(userids[i],1));
                    result = hrmsCommonDAOObj.getAssignreviewer(requestParams);
                    tabledata = result.getEntityList();
                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        Assignreviewer log = (Assignreviewer) ite.next();
                        requestParams.clear();
                        requestParams.put("Id", log.getId());
                        requestParams.put("Reviewerstatus", 0);
                        hrmsCommonDAOObj.addAssignreviewer(requestParams);

                    }
                }
                for (int i = 0; i < userids.length; i++) {
                    for (int j = 0; j < managerids.length; j++) {
                        requestParams.clear();
                        requestParams.put("Employee", userids[i]);
                        requestParams.put("Reviewer", managerids[j]);
                        requestParams.put("Reviewerstatus", 1);
                        hrmsCommonDAOObj.addAssignreviewer(requestParams);
                    }
                }
            }
            jobj.put("valid", true);
            jobj.put("data", "");
            txnManager.commit(status);
        }catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }
 
    public ModelAndView getexEmployees(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        int count = 0;
        try {
            String Searchjson = request.getParameter("searchJson");
            String appendCase = "and";
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String lid = StringUtil.checkForNull(request.getParameter("lid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names =  new ArrayList(Arrays.asList("ua.user.company.companyID","ua.user.deleteflag","emp.termnd"));
            ArrayList filter_values = new ArrayList(Arrays.asList(companyid,1,true));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("searchcol", new String[]{"u.firstName","u.lastName","ua.department.value","ua.designationid.value","ua.role.name","u.emailID"});
            requestParams.put("allflag", false);
            if(request.getParameter("combo")!=null) {
                requestParams.put("combo",request.getParameter("combo"));
            } else {
                requestParams.put("combo","");
            }
            StringUtil.checkpaging(requestParams, request);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            if(!StringUtil.isNullOrEmpty(request.getParameter("startdate"))){
                filter_names.add(">=emp.relievedate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("startdate")))));
                filter_names.add("<=emp.relievedate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("enddate")))));
            }

            if (!StringUtil.isNullOrEmpty(Searchjson)) {
                getMyAdvanceSearchparams(Searchjson, filter_names);
                insertParamAdvanceSearchString(filter_values, Searchjson);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);


            result = hrmsCommonDAOObj.getUserDetailsHrms(requestParams);
            List list1 = result.getEntityList();
            Iterator itr = list1.iterator();
            JSONArray jArr = new JSONArray();
            count = result.getRecordTotalCount();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                JSONObject obj = new JSONObject();
                Empprofile e = null;
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", row[0].toString()) ;
                User u = (User) ua.getUser();
                e = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", row[1].toString());
                obj.put("department", (ua.getDepartment() == null ? "" : ua.getDepartment().getId()));
                obj.put("departmentname", (ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                obj.put("userid", u.getUserID());
                obj.put("fname", u.getFirstName());
                obj.put("lname", u.getLastName());
                obj.put("fullname",u.getFirstName()+" "+ (u.getLastName()==null?"":u.getLastName()));
                obj.put("emailid", u.getEmailID());
                obj.put("contactno", u.getContactNumber());
                obj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                obj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                obj.put("salary", ua.getSalary());
                requestParams.clear();
                requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("empid",ua.getEmployeeid());
                obj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                obj.put("termdate",sessionHandlerImplObj.getDateFormatter(request).format(e.getRelievedate()));
                if(e.getTercause()!=null)
                    obj.put("termreason",e.getTercause().getValue());
                else
                    obj.put("termreason","");
                obj.put("termdesc",e.getTerReason());
                if(e.getTerminatedby()!=null)
                    obj.put("termby",StringUtil.getFullName(e.getTerminatedby()));
                jArr.put(obj);
            }
            jobj.put("count", result.getRecordTotalCount());
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView rehireEmp(HttpServletRequest request, HttpServletResponse response){
       SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
       SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
       JSONObject msg = new JSONObject();

       //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try {
           Empprofile e = null;
           String ids[] = request.getParameterValues("ids");
           String deptid=request.getParameter("dept");
           String desgid=request.getParameter("desg");
           String salary=(request.getParameter("salary"));
           String reldate=request.getParameter("joindate");
           String tempid=request.getParameter("templateid");
           Date rvdate=fmt.parse(reldate);
           Date now = new Date();
            for (int i = 0; i < ids.length; i++) {
                e = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", ids[i]);
                if(e!=null)
                {
                	if(e.getRelievedate()!=null){
                        if(e.getRelievedate().after(rvdate)){
                            msg.put("msg", "invalidjoindate");
                            msg.put("success", true);
                            txnManager.commit(status);
                            return  new ModelAndView("jsonView","model",msg.toString());
                        }
                    }
                }
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                if(rvdate.before(now) || rvdate.equals(now)){
                    requestParams.put("termnd", false);
                }
                requestParams.put("userid", ids[i]);
                requestParams.put("joindate", request.getParameter("joindate"));
                requestParams.put("confirmdate", null);
                requestParams.put("relievedate", null);
                hrmsCommonDAOObj.addEmpprofile(requestParams);
                
                requestParams.clear();
                requestParams.put("UserID", ids[i]);
                requestParams.put("Department", deptid);
                requestParams.put("Designationid", desgid);
                requestParams.put("Salary", salary);
                requestParams.put("Templateid", tempid);
                hrmsCommonDAOObj.addUseraccount(requestParams);

                if(rvdate.before(now) || rvdate.equals(now)){
                    requestParams.clear();
                    requestParams.put("UserID", ids[i]);
                    requestParams.put("Deleteflag", 0);
                    hrmsCommonDAOObj.adduser(requestParams);
                }
                

//                User u=(User)session.get(User.class,ids[i]);
//                Empprofile emp=(Empprofile)session.get(Empprofile.class,ids[i]);
//                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
//                ua.setDepartment((dept));
//                ua.setDesignationid(desg);
//                ua.setSalary(salary);
//                u.setDeleteflag(0);
//                if (StringUtil.isNullOrEmpty(tempid) == false) {
//                     ua.setTemplateid(tempid);
//                }
//                emp.setJoindate(rvdate);
//                emp.setTermnd(false);
//                emp.setRelievedate(null);
//                session.saveOrUpdate(emp);
//                session.saveOrUpdate(u);
//                session.saveOrUpdate(ua);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.REHIRE_EMPLOYEE, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has rehired employee " + AuthHandler.getFullName(u) ,request);
            }
           msg.put("success", true);
           txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally{
            //return  new ModelAndView("jsonView","model","{\"success\":\"true\"}");
            return  new ModelAndView("jsonView","model",msg.toString());
        }
    }

    public String getMyAdvanceSearchString(String Searchjson, String appendCase) throws Exception{
        StringBuilder myResult = new StringBuilder();
        int any = 0;
        JSONObject jobj = new JSONObject(Searchjson);
        int count = jobj.getJSONArray("root").length();
        for (int i = 0; i < count; i++) {
            JSONObject jobj1 = jobj.getJSONArray("root").getJSONObject(i);
            any++;
            myResult.append(" ");
            if (i == 0) {
                myResult.append(appendCase);
                myResult.append(" (( ");
            } else {
                myResult.append(" ( ");
            }
            if(jobj1.getString("xtype").equals("datefield")){
                myResult.append(jobj1.getString("column") + " >= ? and " + jobj1.getString("column") + " <= ?");
            } else {
                myResult.append(jobj1.getString("column") + " like ? or " + jobj1.getString("column") + " like ?");
            }if (i + 1 < count) {
                myResult.append(") ");
            } else {
                myResult.append(")) ");
            }
            if (i + 1 < count) {
                myResult.append(" and ");
            }
        }
        if (any == 0) {
            myResult.append(" ");
        }
        return myResult.toString();
    }

    public void getMyAdvanceSearchparams(String Searchjson, List filter_names) throws Exception{
        int any = 0;
        JSONObject jobj = new JSONObject(Searchjson);
        int count = jobj.getJSONArray("root").length();
        for (int i = 0; i < count; i++) {
            JSONObject jobj1 = jobj.getJSONArray("root").getJSONObject(i);
            if(jobj1.getString("xtype").equals("datefield")){
//                myResult.append(jobj1.getString("column") + " >= ? and " + jobj1.getString("column") + " <= ?");
                  filter_names.add(">="+jobj1.getString("column"));
                  filter_names.add("<="+jobj1.getString("column"));
            } else {
//                myResult.append(jobj1.getString("column") + " like ? or " + jobj1.getString("column") + " like ?");
                filter_names.add("LIKE"+jobj1.getString("column"));
            }
        }
    }

    public void insertParamAdvanceSearchString(List al, String Searchjson)
            throws Exception {

        JSONObject jobj = new JSONObject(Searchjson);
        int count = jobj.getJSONArray("root").length();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        try {
        for (int i = 0; i < count; i++) {
            JSONObject jobj1 = jobj.getJSONArray("root").getJSONObject(i);
            String trimedStr = jobj1.getString("searchText").trim();
            if(jobj1.getString("xtype").equals("datefield")){
                String[] dates=trimedStr.split("##");
                al.add(new Date(df.format(new Date(dates[0].toString()))));
                al.add(new Date(df.format(new Date(dates[1].toString()))));
            } else {
                al.add(trimedStr + "%");
//                al.add("%" + trimedStr + "%");
            }
        }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public ModelAndView saveempprofile(HttpServletRequest request, HttpServletResponse reponse) {
        JSONObject msg = new JSONObject();
        KwlReturnObject result;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
            String currentuser = sessionHandlerImplObj.getUserid(request);
            String userid = request.getParameter("userid");
            String jsondata = request.getParameter("jsondatawk");
            String jsondata1 = request.getParameter("jsondatacad");
            msg.put("msg", "Error in updating profile.");
            msg.put("success", false);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid",userid);

            if (!StringUtil.isNullOrEmpty(request.getParameter("formname")) && request.getParameter("formname").equals("Personal")) {

                    requestParams.put("middlename",request.getParameter("mname"));
                    requestParams.put("bankbranch",request.getParameter("bankbranch"));
                    requestParams.put("bankname",request.getParameter("bankname"));
                    requestParams.put("bloodgrp",request.getParameter("bloodgrp"));
                    if (!StringUtil.isNullOrEmpty(request.getParameter("childDoB1"))) {
                        requestParams.put("child1dob",request.getParameter("childDoB1"));
                    }
                    requestParams.put("child1name",request.getParameter("child1name"));
                    if (!StringUtil.isNullOrEmpty(request.getParameter("childDoB2"))) {
                        requestParams.put("child2dob",request.getParameter("childDoB2"));
                    }
                    requestParams.put("child2name",request.getParameter("child2name"));
                    if (!StringUtil.isNullOrEmpty(request.getParameter("DoB"))) {
                        requestParams.put("dob",request.getParameter("DoB"));
                    }
                    requestParams.put("drvlicense",request.getParameter("drvlicense"));
                    if (!StringUtil.isNullOrEmpty(request.getParameter("exppassport"))) {
                        requestParams.put("exppassport",request.getParameter("exppassport"));
                    }
                    if (!StringUtil.isNullOrEmpty(request.getParameter("fatherDoB"))) {
                        requestParams.put("fatherdob",request.getParameter("fatherDoB"));
                    }
                    requestParams.put("fathername",request.getParameter("fathername"));
                    requestParams.put("gender",request.getParameter("gender"));
                    requestParams.put("marriage",request.getParameter("marital"));
                    if (!StringUtil.isNullOrEmpty(request.getParameter("motherDoB"))) {
                        requestParams.put("motherdob",request.getParameter("motherDoB"));
                    }
                    requestParams.put("mothername",request.getParameter("mothername"));
                    requestParams.put("panno",request.getParameter("panno"));
                    requestParams.put("passportno",request.getParameter("passportno"));
                    requestParams.put("pfno",request.getParameter("pfno"));
                    if (!StringUtil.isNullOrEmpty(request.getParameter("spouseDoB"))) {
                        requestParams.put("spousedob",request.getParameter("spouseDoB"));
                    }
                    requestParams.put("spousename",request.getParameter("spousename"));
                    requestParams.put("keyskills",request.getParameter("keyskills"));

                    requestParams.put("updated_by",currentuser);
                    requestParams.put("updated_on",fmt1.format(new Date()));
                    if (StringUtil.equal(userid, currentuser)) {
                        requestParams.put("status","Pending");
                    } else {
                        requestParams.put("status","Approved");
                    }
                    result = hrmsCommonDAOObj.addEmpprofile(requestParams);
                    if(result.isSuccessFlag()){
                    requestParams.clear();
                    requestParams.put("UserID",userid);
                    requestParams.put("FirstName",request.getParameter("fname"));
                    requestParams.put("LastName",request.getParameter("lname"));
                    result = hrmsCommonDAOObj.adduser(requestParams);
                    if(result.isSuccessFlag()) {
                        requestParams.clear();
                        requestParams.put("UserID",userid);
                        requestParams.put("Salary",request.getParameter("salarypermonth"));
                        requestParams.put("Accno",request.getParameter("bankacc"));
                        result = hrmsCommonDAOObj.addUseraccount(requestParams);
                        requestParams.put("Useraccount",(Object) result.getEntityList().get(0));

                        if(result.getRecordTotalCount() > 0)
                        hrmsCommonDAOObj.insertConfigData(request, "Personal", userid,sessionHandlerImplObj.getCompanyid(request),requestParams);
                        else
                        hrmsCommonDAOObj.insertConfigData(request, "Personal", userid,sessionHandlerImplObj.getCompanyid(request));

                        auditTrailDAOObj.insertAuditLog(AuditAction.PROFILE_EDITED, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has edited " + profileHandlerDAOObj.getUserFullName(userid) + "'s profile", request, "0");
                    msg.put("msg", "Profile updated successfully.");
                    msg.put("success", true);
                    }
                    }

            } else if (!StringUtil.isNullOrEmpty(request.getParameter("formname")) && request.getParameter("formname").equals("Contact")) {
                requestParams.put("emgaddr",request.getParameter("emgaddr"));
                requestParams.put("emghome",request.getParameter("emghome"));
                requestParams.put("emgmob",request.getParameter("emgmob"));
                requestParams.put("emgname",request.getParameter("emgname"));
                requestParams.put("emgreln",request.getParameter("emgreln"));
                requestParams.put("emgwork",request.getParameter("emgwork"));
                requestParams.put("landno",request.getParameter("landno"));
                requestParams.put("mailaddr",request.getParameter("mailaddr"));
                requestParams.put("mobno",request.getParameter("mobno"));
                requestParams.put("permaddr",request.getParameter("permaddr"));
                requestParams.put("permcity",request.getParameter("permcity"));
                if (!StringUtil.isNullOrEmpty(request.getParameter("permcountry"))) {
                    requestParams.put("permcountry", request.getParameter("permcountry"));
                }
                requestParams.put("permstate",request.getParameter("permstate"));
                requestParams.put("presentaddr",request.getParameter("presentaddr"));
                requestParams.put("presentcity",request.getParameter("presentcity"));
                if (!StringUtil.isNullOrEmpty(request.getParameter("presentcountry"))) {
                    requestParams.put("presentcountry",request.getParameter("presentcountry"));
                }
                requestParams.put("presentstate",request.getParameter("presentstate"));
                requestParams.put("workno",request.getParameter("workno"));
                requestParams.put("workmail",request.getParameter("workmail"));
                requestParams.put("othermail",request.getParameter("othermail"));
                requestParams.put("weekoff",request.getParameter("weekoff"));
                requestParams.put("wkstarttime",request.getParameter("starttime"));
                requestParams.put("wkendtime",request.getParameter("endtime"));
                result = hrmsCommonDAOObj.addEmpprofile(requestParams);
                if(result.isSuccessFlag()) {

                requestParams.put("filter_names", Arrays.asList("userID"));
                requestParams.put("filter_values",Arrays.asList(userid));

                result = hrmsCommonDAOObj.getUseraccount(requestParams);

                requestParams.put("Useraccount",(Object) result.getEntityList().get(0));

                if(result.getRecordTotalCount() > 0)
                hrmsCommonDAOObj.insertConfigData(request, "Contact", userid,sessionHandlerImplObj.getCompanyid(request),requestParams);
                
                    msg.put("msg", "Profile updated successfully.");
                    msg.put("success", true);
                }
            } else if (!StringUtil.isNullOrEmpty(request.getParameter("formname")) && request.getParameter("formname").equals("Organizational")) {
                if (!StringUtil.isNullOrEmpty(request.getParameter("reportto"))) {
                    requestParams.put("reportto", request.getParameter("reportto"));
                }
                requestParams.put("emptype",request.getParameter("emptype"));
                requestParams.put("commid",request.getParameter("commid"));
                requestParams.put("branchcode",request.getParameter("branchcode"));
                requestParams.put("branchaddr",request.getParameter("branchaddr"));
                requestParams.put("branchcity",request.getParameter("branchcity"));
                if (!StringUtil.isNullOrEmpty(request.getParameter("relievedate"))) {
                    requestParams.put("relievedate",request.getParameter("relievedate"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("branchcountry"))) {
                    requestParams.put("branchcountry", request.getParameter("branchcountry"));
                }
                if(!StringUtil.isNullOrEmpty(request.getParameter("probationmon"))&&!StringUtil.isNullOrEmpty(request.getParameter("probationyr"))){
                	requestParams.put("probperiod",request.getParameter("probationmon") + "," + request.getParameter("probationyr"));
                }
                if(!StringUtil.isNullOrEmpty(request.getParameter("trainingmon"))&&!StringUtil.isNullOrEmpty(request.getParameter("trainingyr"))){
                	requestParams.put("trainperiod",request.getParameter("trainingmon") + "," + request.getParameter("trainingyr"));
                }
                if(!StringUtil.isNullOrEmpty(request.getParameter("noticemon"))&&!StringUtil.isNullOrEmpty(request.getParameter("noticeyr"))){
                	requestParams.put("noticeperiod",request.getParameter("noticemon") + "," + request.getParameter("noticeyr"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("confirmdate"))) {
                    requestParams.put("confirmdate",request.getParameter("confirmdate"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("joindate"))) {
                    requestParams.put("joindate",request.getParameter("joindate"));
                }
                result = hrmsCommonDAOObj.addEmpprofile(requestParams);

                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", userid);
                int histsave = 0;

                Date saveDate = new Date();
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
                saveDate = new Date(fmt.format(saveDate));
                Emphistory ehst = new Emphistory();
                String updatedby = sessionHandlerImplObj.getUserid(request);


                String histdept="";
                String histdesig="";

                requestParams.clear();
                requestParams.put("UserID", userid);

                if (!StringUtil.isNullOrEmpty(request.getParameter("designationid"))) {
                    if ((MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", (String) request.getParameter("designationid")) != ua.getDesignationid() && ua.getDesignationid() != null) {
                        histdesig=ua.getDesignationid().getId();
                        histsave = 1;
                    }
                    requestParams.put("Designationid",request.getParameter("designationid"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                    if ((MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", (String) request.getParameter("department")) != ua.getDepartment() && ua.getDepartment() != null) {
                        histdept=ua.getDepartment().getId();
                        if (histsave == 0) {
                            histdesig = ua.getDesignationid().getId();
                        }
                        histsave = 2;
                    }
                    requestParams.put("Department", request.getParameter("department"));
                }
                
                if(!StringUtil.isNullOrEmpty(request.getParameter("costcenter"))){
                	requestParams.put("CostCenter", request.getParameter("costcenter"));
                }
                String employeeIdFormat = "";
                if (!StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                    String[] codeid = (request.getParameter("empid")).split("-");
                    Integer codeid2 = null;
                    for (int x = 0; x < codeid.length; x++) {
                        if (codeid[x].matches("[0-9]*") == true) {
                            codeid2 = Integer.parseInt(codeid[x]);
                        }else{
                        	employeeIdFormat += (codeid[x]+"-");
                        }
                    }
                    if(employeeIdFormat.length()>0){
                    	employeeIdFormat = employeeIdFormat.substring(0, employeeIdFormat.length() - 1);
                    }
                    
                    if(StringUtil.isNullOrEmpty(employeeIdFormat))
                    	employeeIdFormat = null;
                    HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                    ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
                    requestParams1.put("employeeIdFormat", employeeIdFormat);
                	requestParams1.put("userID", userid);
                	requestParams1.put("employeeid", codeid2);
                	requestParams1.put("request", request);
                	boolean isStadardEmpFormatWithIdAvilable = isStadardEmpFormatWithIdAvilable(requestParams1);                		
                	String standardEmpId = getStadardEmpFormat(requestParams1);
                	if(standardEmpId!=null && employeeIdFormat!=null && standardEmpId.equals(employeeIdFormat)){
                		employeeIdFormat = null;
                	}
                    requestParams1.clear();
                    filter_names.add("employeeid");
                    filter_values.add(codeid2);
                    
                    if(employeeIdFormat == null){
                    	filter_names.add("IS employeeIdFormat");
                    }else{
                    	filter_names.add("employeeIdFormat");
                    	filter_values.add(employeeIdFormat);
                    }
                    filter_names.add("!userID");
                    filter_values.add(userid);

                    filter_names.add("user.company.companyID");
                    filter_values.add(sessionHandlerImplObj.getCompanyid(request));

                    requestParams1.put("filter_names", filter_names);
                    requestParams1.put("filter_values", filter_values);
                    result = hrmsCommonDAOObj.getUseraccount(requestParams1);
                    if (!result.getEntityList().isEmpty() || isStadardEmpFormatWithIdAvilable) {
                        msg.put("msg", "Employee ID is already assigned to another employee.");
                        msg.put("success", false);
                        txnManager.commit(status);
                        return new ModelAndView(successView, "model", msg.toString());
                    } else {
                        requestParams.put("Employeeid",codeid2);
                        requestParams.put("EmployeeIdFormat",employeeIdFormat);
                    }
                    if (histsave == 1) {
                        histdept=ua.getDepartment().getId();
                    }
                    requestParams1.clear();
                    if (histsave == 1 || histsave == 2) {
                        requestParams1.put("Department",histdept);
                        requestParams1.put("Designation",histdesig);
                        requestParams1.put("Userid",userid);
                        requestParams1.put("Empid",ua.getEmployeeid());
                        requestParams1.put("Updatedon",saveDate);
                        requestParams1.put("Updatedby",updatedby);
                        requestParams1.put("Category",Emphistory.Emp_Desg_change);
                        result = hrmsCommonDAOObj.addEmphistory(requestParams1);
                    }
                    result = hrmsCommonDAOObj.addUseraccount(requestParams);
                    requestParams.put("Useraccount",(Object) result.getEntityList().get(0));
                }
                

                if(result.getRecordTotalCount() > 0 && requestParams.get("Useraccount")!=null)
                    hrmsCommonDAOObj.insertConfigData(request, "Organizational", userid,sessionHandlerImplObj.getCompanyid(request),requestParams);
                else
                    hrmsCommonDAOObj.insertConfigData(request, "Organizational", userid,sessionHandlerImplObj.getCompanyid(request));

                msg.put("msg", "Profile updated successfully.");
                msg.put("success", true);

            } else {
                if (jsondata.length() > 0) {
                    JSONArray jarr = new JSONArray("[" + jsondata + "]");
                    for (int i = 0; i < jarr.length(); i++) {
                    	Empexperience empexp = new Empexperience();
                        JSONObject jobj = jarr.getJSONObject(i);
                        requestParams.clear();
                        if (!StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                            empexp.setId(jobj.getString("id"));
                        }
                        empexp.setUserid((UserLogin) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.UserLogin", userid));
                    	empexp.setType(jobj.getString("type"));
                        empexp.setOrganization(jobj.getString("organisation"));
                        empexp.setPosition(jobj.getString("position"));
                        empexp.setBeginyear(jobj.getString("beginyear"));
                        empexp.setEndyear(jobj.getString("endyear"));
                        empexp.setComment(jobj.getString("comment"));
                        hrmsCommonDAOObj.addEmpExperience(empexp);
                    }
                }
                
                if (jsondata1.length() > 0) {
                	JSONArray jarr1 = new JSONArray("[" + jsondata1 + "]");
                    for (int j = 0; j < jarr1.length(); j++) {
                    	Empexperience empexp = new Empexperience();
                    	JSONObject jobj = jarr1.getJSONObject(j);
                        requestParams.clear();
                        if (!StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                            empexp.setId(jobj.getString("id"));
                        }
                        empexp.setUserid((UserLogin) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.UserLogin", userid));
                    	empexp.setType(jobj.getString("type"));
                        empexp.setQualification(jobj.getString("qualification"));
                        empexp.setInstitution(jobj.getString("institution"));
                        empexp.setYearofgrad(jobj.getString("gradyear"));
                        empexp.setMarks(jobj.getString("marks"));
                        empexp.setFrmyear(jobj.getString("yeargrdfrm"));
                        empexp.setQaulin(jobj.getString("qualificationin"));
                        hrmsCommonDAOObj.addEmpExperience(empexp);
                    }
                }
                JSONObject jobj1 = new JSONObject();

                jobj1.put("msg", "Profile updated successfully.");
                jobj1.put("success", true);
                msg.put("valid", true);
                msg.put("data", jobj1.toString());
            }

            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView(successView, "model", msg.toString());
    }

    public ModelAndView getEmpHistory(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count=0;
        List list=null;
        Iterator itr;
        KwlReturnObject result;
        try {
            String userid=request.getParameter("userid");
            String cmpid=sessionHandlerImplObj.getCompanyid(request);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("filter_names", Arrays.asList("userid.userID","userid.company.companyID"));
            requestParams.put("filter_values", Arrays.asList(userid,cmpid));
            requestParams.put("allflag",false);
            StringUtil.checkpaging(requestParams, request);

            result = hrmsCommonDAOObj.getEmpHistory(requestParams);
            count = result.getRecordTotalCount();
            list=result.getEntityList();

            itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                JSONObject obj = new JSONObject();
                Emphistory ehst = (Emphistory) itr.next();
                obj.put("designation",(ehst.getDesignation()!=null?ehst.getDesignation().getValue():""));
                obj.put("department",(ehst.getDepartment()!=null?ehst.getDepartment().getValue():""));
                obj.put("startdate",ehst.getJoindate()!=null?sessionHandlerImplObj.getDateFormatter(request).format(ehst.getJoindate()):"");
                obj.put("enddate",ehst.getUpdatedon()!=null?sessionHandlerImplObj.getDateFormatter(request).format(ehst.getUpdatedon()):"");
                obj.put("salary",ehst.getSalary()!=null?ehst.getSalary():"");
                obj.put("category",ehst.getCategory().equals("1")?messageSource.getMessage("hrms.common.designation",null, "Designation", RequestContextUtils.getLocale(request)):messageSource.getMessage("hrms.common.Salary",null, "Salary", RequestContextUtils.getLocale(request)));
                obj.put("hid",ehst.getHid());
                jArr.put(obj);
            }

            requestParams.clear();
            requestParams.put("filter_names", Arrays.asList("userID.userID"));
            requestParams.put("filter_values", Arrays.asList(userid));
            requestParams.put("allflag",false);
            StringUtil.checkpaging(requestParams, request);

            result = hrmsCommonDAOObj.getPayHistory(requestParams);
            count+=result.getRecordTotalCount();
            list = result.getEntityList();

            itr = list.iterator();
            while (itr.hasNext()) {
                JSONObject jobjtemp = new JSONObject();
                Payhistory group = (Payhistory) itr.next();
                jobjtemp.put("designation", group.getDesign());
                jobjtemp.put("department", group.getDepartment());
                jobjtemp.put("salary", group.getNet());
                jobjtemp.put("startdate", sessionHandlerImplObj.getDateFormatter(request).format(group.getCreatedon()));
                jobjtemp.put("enddate", sessionHandlerImplObj.getDateFormatter(request).format(group.getCreatedfor()));
                jobjtemp.put("category",messageSource.getMessage("hrms.Featurelist.payroll",null, "Payroll", RequestContextUtils.getLocale(request)));
                jobjtemp.put("hid", group.getHistoryid());
                jArr.put(jobjtemp);
            }
            jobj.put("count", count);
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView(successView, "model", jobj1.toString());
    }


    public ModelAndView getPromotedEmp(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        int count=0;
        List list=null;
        Iterator itr;
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        try {
            String cmpid=sessionHandlerImplObj.getCompanyid(request);

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("searchcol", new String[]{"userid.firstName","userid.lastName"});
            requestParams.put("filter_names", Arrays.asList("userid.company.companyID",">=updatedon","<=updatedon"));
            requestParams.put("filter_values",Arrays.asList(cmpid,new Date(df.format(new Date(request.getParameter("stdate")))),new Date(df.format(new Date(request.getParameter("enddate"))))));
            requestParams.put("allflag",false);
            StringUtil.checkpaging(requestParams, request);
            result = hrmsCommonDAOObj.getEmpHistory(requestParams);
            count = result.getRecordTotalCount();
            list = result.getEntityList();
            itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                JSONObject obj = new JSONObject();
                Emphistory ehst = (Emphistory) itr.next();
                User user = ehst.getUserid();
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount",user.getUserID());
                obj.put("olddesignation",(ehst.getDesignation()!=null?ehst.getDesignation().getValue():""));
                obj.put("olddepartmentname",(ehst.getDepartment()!=null?ehst.getDepartment().getValue():""));
                obj.put("updatedate",ehst.getUpdatedon()!=null?sessionHandlerImplObj.getDateFormatter(request).format(ehst.getUpdatedon()):"");
                requestParams.clear();
                requestParams.put("companyid", cmpid);
                requestParams.put("empid", ua.getEmployeeid());
                if(ua.getEmployeeIdFormat()==null){
                	obj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                }else{
                	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                	obj.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                }
                obj.put("username",(user.getUserLogin()==null)?"":user.getUserLogin().getUserName());
                obj.put("fullname",user.getFirstName()+" "+ (user.getLastName()==null?"":user.getLastName()));
                obj.put("newdesignation",(ua.getDesignationid()==null)?"":ua.getDesignationid().getValue());
                obj.put("newdepartmentname",(ua.getDepartment()==null)?"":ua.getDepartment().getValue());
                jArr.put(obj);
            }

            jobj.put("count", count);
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView(successView, "model", jobj1.toString());
    }

    public ModelAndView exportUserInfo(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject countobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            String Searchjson = request.getParameter("searchJson");
            String appendCase = "and";
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String lid = StringUtil.checkForNull(request.getParameter("lid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names =  new ArrayList(Arrays.asList("ua.user.company.companyID","ua.user.deleteflag"));
            ArrayList filter_values = new ArrayList(Arrays.asList(companyid,0));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("allflag", true);
            requestParams.put("searchcol", new String[]{"u.firstName","u.lastName","ua.department.value","ua.designationid.value","ua.role.name","u.emailID"});
            if(request.getParameter("combo")!=null) {
                requestParams.put("combo",request.getParameter("combo"));
            } else {
                requestParams.put("combo","");
            }
            StringUtil.checkpaging(requestParams, request);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            if(!StringUtil.isNullOrEmpty(request.getParameter("stdate"))){
                filter_names.add(">=emp.joindate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("stdate")))));
                filter_names.add("<=emp.joindate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("enddate")))));
            }

            if (!StringUtil.isNullOrEmpty(Searchjson)) {
                getMyAdvanceSearchparams(Searchjson, filter_names);
                insertParamAdvanceSearchString(filter_values, Searchjson);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            kmsg = hrmsCommonDAOObj.getUserDetailsHrms(requestParams);
            List lst = kmsg.getEntityList();
            jarr = kwlCommonTablesDAOObj.getDetailsJson(lst,0,"com.krawler.common.admin.User");

            int count = 0;
            for(int ctr=0;ctr<jarr.length();ctr++){
                jobj = jarr.getJSONObject(ctr);
                Object[] row = (Object[]) lst.get(ctr);
                User u = (User)jobj.get("instance");
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", row[0].toString());
                if (row[1] != null) {
                    Empprofile e = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile",row[1].toString());
                    if (!StringUtil.isNullOrEmpty(e.getStatus())) {
                        jobj.put("status", messageSource.getMessage("hrms.administration."+e.getStatus(), null, e.getStatus(), RequestContextUtils.getLocale(request)));
                    } else {
                        jobj.put("status", messageSource.getMessage("hrms.administration.Pending", null, "Pending", RequestContextUtils.getLocale(request)));
                    }
                    jobj.put("dob",e.getDoB() == null ? "" : e.getDoB());
                    jobj.put("gender",e.getGender() == null ? "" : e.getGender());
                    jobj.put("bloodgrp",e.getBloodgrp() == null ? "" : e.getBloodgrp());
                    jobj.put("fathername",e.getFathername() == null ? "" : e.getFathername());
                    jobj.put("mothername",e.getMothername() == null ? "" : e.getMothername());
                    jobj.put("passportno",e.getPassportno() == null ? "" : e.getPassportno());
                    jobj.put("joindate",e.getJoindate() == null ? "" : e.getJoindate());
                    jobj.put("confirmdate",e.getConfirmdate() == null ? "" : e.getConfirmdate());
                    jobj.put("middlename",e.getMiddlename() == null ? "" : e.getMiddlename());
                    jobj.put("keyskills",e.getKeyskills() == null ? "" : e.getKeyskills());
                    jobj.put("wkstarttime",e.getWkstarttime() == null ? "" : e.getWkstarttime());
                    jobj.put("wkendtime",e.getWkendtime() == null ? "" : e.getWkendtime());
                    jobj.put("weekoff",e.getWeekoff() == null ? "" : e.getWeekoff());
                    jobj.put("pannumber",e.getPanno() == null ? "" : e.getPanno());
                    jobj.put("updatedon",e.getUpdated_on() == null ? "" : e.getUpdated_on());
                } else {
                    jobj.put("status", messageSource.getMessage("hrms.recruitment.InComplete", null, "Incomplete", RequestContextUtils.getLocale(request)));
                    jobj.put("dob", "");
                    jobj.put("gender", "");
                    jobj.put("bloodgrp", "");
                    jobj.put("fathername", "");
                    jobj.put("mothername", "");
                    jobj.put("passportno", "");
                    jobj.put("joindate", "");
                    jobj.put("confirmdate", "");
                    jobj.put("middlename", "");
                    jobj.put("keyskills", "");
                    jobj.put("wkstarttime", "");
                    jobj.put("wkendtime", "");
                    jobj.put("weekoff", "");
                }
                jobj.put("department", (ua.getDepartment() == null ? "" : ua.getDepartment().getId()));
                jobj.put("departmentname", (ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                jobj.put("role", (ua.getRole() == null ? "" : ua.getRole().getID()));
                String name="";
                if(ua.getRole()!=null&&ua.getRole().getCompany()!=null){
                	name = ua.getRole().getName();
                }else{
                	name = messageSource.getMessage("hrms.common.role."+ua.getRole().getID(),null, ua.getRole().getName(), RequestContextUtils.getLocale(request));
                }
                jobj.put("rolename", (ua.getRole() == null ? "" : name));
                jobj.put("username", u.getUserLogin().getUserName());
                jobj.put("fullname",u.getFirstName()+" "+ (u.getLastName()==null?"":u.getLastName()));
                jobj.put("lastlogin", (u.getUserLogin().getLastActivityDate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(u.getUserLogin().getLastActivityDate())));
                jobj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                jobj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                jobj.put("templateid", ua.getTemplateid()!=null?ua.getTemplateid():"");
                jobj.put("salary", ua.getSalary()!=null?ua.getSalary():"");
                jobj.put("accno", ua.getAccno()!=null?ua.getAccno():"");
                jobj.put("createdon", ua.getUser().getCreatedon()!=null?ua.getUser().getCreatedon():"");
                requestParams.clear();
                requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("empid",ua.getEmployeeid());
                KwlReturnObject result;
//                KwlReturnObject result = profileHandlerDAOObj.getEmpidFormatEdit(requestParams);
                if(ua.getEmployeeIdFormat()==null){
                	jobj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                }else{
                	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                	jobj.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                }
                requestParams.clear();
                filter_names.add("assignemp.userID");
                filter_values.add(u.getUserID());

                filter_names.add("managerstatus");
                filter_values.add(1);

                requestParams.put("filter_names", Arrays.asList("assignemp.userID","managerstatus", "assignman.deleteflag"));
                requestParams.put("filter_values", Arrays.asList(u.getUserID(),1, 0));

                result = hrmsCommonDAOObj.getAssignmanager(requestParams);
                List lst1 = result.getEntityList();
                Iterator itr1 = lst1.iterator();

                if (itr1.hasNext()) {
                    String manager ="";
                    while (itr1.hasNext()) {
                        Assignmanager asm = (Assignmanager) itr1.next();
                        if (asm.getAssignman() != null) {
                            jobj.append("managerid", asm.getAssignman().getUserID());
                            manager +=  asm.getAssignman().getFirstName() + " " + asm.getAssignman().getLastName()+ "," ;
                        }
                    }
                    jobj.put("manager", manager.substring(0,manager.length()-1));
                } else {
                    jobj.put("manager", " ");
                    jobj.put("managerid", " ");
                }

                requestParams.clear();
                filter_names.clear();
                filter_values.clear();
                filter_names.add("employee.userID");
                filter_values.add(u.getUserID());
                
                filter_names.add("reviewer.deleteflag");
                filter_values.add(0);

                filter_names.add("reviewerstatus");
                filter_values.add(1);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);


                result = hrmsCommonDAOObj.getAssignreviewer(requestParams);
                lst1 = result.getEntityList();
                itr1 = lst1.iterator();
                if (itr1.hasNext()) {
                    String reviewer ="";
                    while (itr1.hasNext()) {
                        Assignreviewer rev = (Assignreviewer) itr1.next();
                        if (rev.getReviewer() != null) {
                            jobj.append("reviewerid", rev.getReviewer().getUserID());
                            reviewer += rev.getReviewer().getFirstName() + " " + rev.getReviewer().getLastName()+ "," ;
                        }
                    }
                    jobj.put("reviewer", reviewer.substring(0,reviewer.length()-1));
                } else {
                    jobj.put("reviewer", " ");
                    jobj.put("reviewerid", " ");
                }

                jarr.put(ctr,jobj);
                count ++;
            }


            countobj.put("data", jarr);
            countobj.put("count", kmsg.getRecordTotalCount());
            exportDAOImplObj.processRequest(request, response, countobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    /*public ModelAndView getEmpidFormat(HttpServletRequest request, HttpServletResponse response ) {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject obj = new JSONObject();
        int count = 0;
        String mainstr = "";
        String empids = "";
        Integer maxcount = null;
        try {

            String cmpnyid = AuthHandler.getCompanyid(request);
            maxcount = getMaxCountEmpid(session, request);
            String SELECT_USER_INFO = "from Company where companyID=?";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, cmpnyid);
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Company cmp = (Company) itr.next();
                String empidformat = cmp.getEmpidformat() == null ? "" : cmp.getEmpidformat();
                String[] row = (empidformat).split("-");
                if (row.length == 1) {
                    if ((row[0]).startsWith("0")) {
                        int len = row[0].length();
                        String regex = "%0" + len + "d";
                        empids = String.format(regex, maxcount);
                        mainstr = empids;
                    } else {
                        mainstr = maxcount.toString();
                    }

                } else {

                    for (int i = 0; i < row.length; i++) {
                        if ((row[i]).startsWith("0")) {
                            int len = row[i].length();
                            String regex = "%0" + len + "d";
                            empids = String.format(regex, maxcount);

                        }
                        if (!row[i].equals("")) {
                            if (!(row[i]).startsWith("0")) {
                                mainstr = mainstr + row[i] + "-";
                            } else {
                                mainstr = mainstr + empids + "-";
                            }
                        } else {
                            mainstr = mainstr + empids + "-";
                        }
                    }
                    mainstr = mainstr.substring(0, mainstr.length() - 1);
                }

            }
            obj.put("maxempid", mainstr);
            jarr.put(obj);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return  new ModelAndView("jsonView","model","{\"success\":\"true\"}");
        }
//        return result;
    }*/

//    public static Integer getMaxCountEmpid(HttpServletRequest request) {
//        Integer maxcount = null;
//        try {
//            String cmpnyid = AuthHandler.getCompanyid(request);
//            String SELECT_USER_INFO1 = "select max(employeeid) from Useraccount where user.company.companyID=?";
//            List list1 = HibernateUtil.executeQuery(session, SELECT_USER_INFO1, cmpnyid);
//            Iterator itr1 = list1.iterator();
//            while (itr1.hasNext()) {
//                maxcount = ((Integer) itr1.next()) + 1;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return maxcount;
//    }
    public ModelAndView saveSearch(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
                String saveJson = request.getParameter("saveJson");
                String searchName = request.getParameter("saveSearchName");
                String userid = sessionHandlerImplObj.getUserid(request);
                int searchFlag = Integer.parseInt(request.getParameter("searchFlag"));
                String msg ="";
                boolean isDuplicate = false;
                if(!StringUtil.isNullOrEmpty(searchName) && !StringUtil.isNullOrEmpty(saveJson)){

                    boolean checkforExistingSearchName = hrmsCommonDAOObj.checkForSearchName (searchName, userid);

                    if(!checkforExistingSearchName){
                        result = hrmsCommonDAOObj.saveSearch(searchName, searchFlag, saveJson, userid);
                        msg = result.getMsg();
                    } else {
                        isDuplicate =true;
                    }
                }
                
                jobj.put("msg", msg);
                jobj.put("isduplicate", isDuplicate);
                jobj.put("success", "true");
                jobj1.put("valid", true);
                jobj1.put("data", jobj.toString());
                txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView deleteSavedSearch(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
                String searchid = request.getParameter("searchid");
                boolean success = false;
                if(!StringUtil.isNullOrEmpty(searchid)){

                    success = hrmsCommonDAOObj.deleteSavedSearch(searchid);
                   
                }
 
                jobj.put("success", success);
                jobj1.put("valid", true);
                jobj1.put("data", jobj.toString());
                txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getSavedSearch(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            String searchid = request.getParameter("searchid");
            String searchFlag = request.getParameter("searchFlag");
            int srchFlag = Integer.parseInt(searchFlag);
            if(!StringUtil.isNullOrEmpty(searchid)){
                result = hrmsCommonDAOObj.getSavedSearch(searchid, srchFlag);
                List list = result.getEntityList();
                if (list.size() > 0) {
                    UserSearchState UserSearchStateobj = (UserSearchState) list.get(0);
                    jobj = new JSONObject(URLDecoder.decode(UserSearchStateobj.getSearchState()));
                    JSONArray jobjdata = jobj.getJSONArray("data");
                    for(int i=0;i<jobjdata.length();i++){
                        JSONObject jobj2 = jobjdata.getJSONObject(i);
                        jobj2.put("searchstate", UserSearchStateobj.getSearchState());
                    }
                }else{
                    jobj.put("data", new JSONArray());
                }
            }
            
            jobj.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }
}
