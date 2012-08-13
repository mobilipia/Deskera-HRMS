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
package com.krawler.spring.hrms.masterconfig;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.LocaleUtil;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.master.Master;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

public class hrmsMasterConfigController extends MultiActionController implements MessageSourceAware  {

    private String successView;
    private hrmsMasterConfigDAO hrmsMasterConfigDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private HibernateTransactionManager txnManager;
    private sessionHandlerImpl sessionHandlerImplObj;
    private MessageSource messageSource;
    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public void setHrmsMasterConfigDAO(hrmsMasterConfigDAO hrmsMasterConfigDAOObj) {
        this.hrmsMasterConfigDAOObj = hrmsMasterConfigDAOObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    



    public ModelAndView getMasterField(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result=null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            result=hrmsMasterConfigDAOObj.getMasterField();
            List lst=result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Master mst = (Master) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", mst.getId());
                tmpObj.put("name", messageSource.getMessage("hrms.Masters."+StringUtil.replaceNametoLocalkey(mst.getName()), null,mst.getName() ,RequestContextUtils.getLocale(request)));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getCompanyInformation(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONObject modobj=new JSONObject();

        KwlReturnObject result= null;

        try {

            HashMap<String, Object> requestParams =  new HashMap<String, Object>();

            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            result=hrmsMasterConfigDAOObj.getCompanyInformation(requestParams);
            List list= result.getEntityList();
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                Company company =(Company) row[1];
                CompanyPreferences cmpr=null;
                JSONObject obj = new JSONObject();
                obj.put("phone", company.getPhoneNumber());
                obj.put("state", company.getState());
                obj.put("currency", (company.getCurrency() == null ? "1" : company.getCurrency().getCurrencyID()));
                obj.put("city", company.getCity());
                obj.put("emailid", company.getEmailID());
                obj.put("companyid", company.getCompanyID());
                obj.put("timezone", (company.getTimeZone() == null ? "1" : company.getTimeZone().getTimeZoneID()));
                obj.put("zip", company.getZipCode());
                obj.put("fax", company.getFaxNumber());
                obj.put("website", company.getWebsite());
                obj.put("image", company.getCompanyLogo());
                obj.put("modifiedon", (company.getModifiedOn() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(company.getModifiedOn())));
                obj.put("createdon", (company.getCreatedOn() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(company.getCreatedOn())));
                obj.put("companyname", company.getCompanyName());
                obj.put("country", (company.getCountry() == null ? "" : company.getCountry().getID()));
                obj.put("address", company.getAddress());
                obj.put("subdomain", company.getSubDomain());
                if(row[0]!=null){
                    cmpr=(CompanyPreferences)row[0];
                    obj.put("employeeidformat", cmpr.getEmpidformat());
                    obj.put("jobidformat", cmpr.getJobidformat());
                    obj.put("emailNotification", cmpr.getEmailNotification());
                    obj.put("finanacialmonth", cmpr.getFinancialmonth());
                    obj.put("weeklyholiday", cmpr.getWeeklyholidays());
                    obj.put("subscription",cmpr.getSubscriptionCode());
                    obj.put("holidaycode",cmpr.getHolidaycode());
                    obj.put("selfapp",cmpr.isSelfappraisal());
                    obj.put("competency",cmpr.isCompetency());
                    obj.put("timesheetjob",cmpr.isTimesheetjob());
                    obj.put("blockemployees",cmpr.isBlockemployees());
                    obj.put("goal",cmpr.isGoal());
                    obj.put("annmng",cmpr.isAnnmanager());
                    obj.put("approveappraisal",cmpr.isApproveappraisal());
                    obj.put("promotionrec",cmpr.isPromotion());
                    obj.put("weightage",cmpr.isWeightage());
                    obj.put("reviewappraisal",cmpr.isReviewappraisal());
                    obj.put("partial",cmpr.isPartial());
                    obj.put("fullupdates",cmpr.isFullupdates());
                    obj.put("modaverage",cmpr.isModaverage());
                    obj.put("overallcomments",cmpr.isOverallcomments());
                    obj.put("approvesalary",cmpr.isApprovesalary());
                    if(StringUtil.isNullOrEmpty(cmpr.getDefaultapps())){
                        obj.put("defaultapps","Internal");
                    } else{
                        obj.put("defaultapps",cmpr.getDefaultapps());
                    }
                    if(StringUtil.isNullOrEmpty(cmpr.getPayrollbase())){
                        obj.put("payrollbase","Template");
                    } else{
                        obj.put("payrollbase",cmpr.getPayrollbase());
                    }
                }else{
                    obj.put("selfapp",false);
                    obj.put("competency",false);
                    obj.put("timesheetjob",true);
                    obj.put("goal",false);
                    obj.put("annmng",false);
                    obj.put("approveappraisal",false);
                    obj.put("promotionrec",false);
                    obj.put("weightage",false);
                    obj.put("reviewappraisal",false);
                    obj.put("partial",false);
                    obj.put("fullupdates",false);
                    obj.put("modaverage",false);
                    obj.put("blockemployees",false);
                    obj.put("defaultapps","Internal");
                    obj.put("payrollbase","Template");
                }
                result=hrmsCommonDAOObj.getHrmsmodule(RequestContextUtils.getLocale(request));
                modobj=(JSONObject) result.getEntityList().get(0);
                obj.put("modules",modobj);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("data",jobj);
            jobj1.put("valid",true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView addMasterDataField(HttpServletRequest request, HttpServletResponse response) {
        String auditID = "";
        String auditMsg = "";

        JSONObject jobj= new JSONObject();
        KwlReturnObject result = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams =  new HashMap<String, Object>();
            requestParams.put("configid",request.getParameter("configid"));

            String fullName = profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request));
            requestParams.put("action",request.getParameter("action"));
            requestParams.put("name",request.getParameter("name"));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("id", request.getParameter("id"));
            requestParams.put("weightage", request.getParameter("wt"));
            result = hrmsMasterConfigDAOObj.addMasterDataField(requestParams);
            if(result.isSuccessFlag()){
                if (request.getParameter("action").equals("Add")) {
                    auditID = AuditAction.MASTER_ADDED;
                    auditMsg = "added ";
                } else {
                    auditID = AuditAction.MASTER_EDITED;
                    auditMsg = " updated ";
                }
                List lst= result.getEntityList();
                auditTrailDAOObj.insertAuditLog(auditID, "User <b>"+fullName+"</b> "+auditMsg+"master item <b>"+lst.get(0).toString()+"</b> of master type <b>"+lst.get(1).toString()+".</b>", request, "0");
            }
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+"master item <b>"+gmst.getValue()+"</b> of master group <b>"+master.getName()+".</b>", request);

            jobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }

        return new ModelAndView("jsonView","model",jobj.toString());
    }
    public ModelAndView addCostCenter(HttpServletRequest request, HttpServletResponse response){
        String auditID = "";
        String auditMsg = "";
        JSONObject jobj= new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        KwlReturnObject result = null;
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String fullName = profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request));
            HashMap<String, Object> requestParams =  new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("code", request.getParameter("code"));
            requestParams.put("action", request.getParameter("action"));
            requestParams.put("name", request.getParameter("name"));
            requestParams.put("id", request.getParameter("id"));
            requestParams.put("creationDate",new java.util.Date());
            result = hrmsMasterConfigDAOObj.addCostCenterField(requestParams);
             if (request.getParameter("action").equals("Add")) {
                    auditID = AuditAction.MASTER_ADDED;
                    auditMsg = "added ";
                } else {
                    auditID = AuditAction.MASTER_EDITED;
                    auditMsg = " updated ";
                }
            List lst= result.getEntityList();
            auditTrailDAOObj.insertAuditLog(auditID, "User <b>"+fullName+"</b> "+auditMsg+"cost center <b>"+request.getParameter("name")+"</b>", request, "0");
             jobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);

        }
        return new ModelAndView("jsonView","model",jobj.toString());
    }

    public ModelAndView deletemasterdata(HttpServletRequest request, HttpServletResponse response) {
        JSONObject msg = new JSONObject();
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String fullName = profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request));
            HashMap<String, Object> requestParams=  new HashMap<String, Object>();

            requestParams.put("ids", request.getParameterValues("ids"));
            result=hrmsMasterConfigDAOObj.deletemasterdata(requestParams);
            if(result.isSuccessFlag()){
            	String groupName= result.getEntityList().get(0).toString();
            	auditTrailDAOObj.insertAuditLog(AuditAction.MASTER_DELETED, "User <b>"+fullName+"</b> deleted <b>"+result.getRecordTotalCount()+"</b> master item(s) of master type <b>"+groupName+"</b>", request, "0");
            	msg.put("data",MessageSourceProxy.getMessage("hrms.administration.master.configuration.masterdatafielddeletedsuccessfully" ,null,"Master data field deleted successfully.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            }else{
            	msg.put("success",false);
            	msg.put("masterid",result.getMsg());
            }
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+" <b>"+ids.length+"</b> master item(s) of master group <b>"+groupName+"</b>", request);
            jobj.put("valid",true);
            jobj.put("data",msg.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }

        return new ModelAndView("jsonView","model",jobj.toString());
    }
    public ModelAndView deletecostcenter(HttpServletRequest request, HttpServletResponse response) {
        JSONObject msg = new JSONObject();
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String fullName = profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request));
            HashMap<String, Object> requestParams=  new HashMap<String, Object>();

            requestParams.put("ids", request.getParameterValues("ids"));
            result=hrmsMasterConfigDAOObj.deleteCostcenter(requestParams);
            if(result.isSuccessFlag()){
            	String groupName= result.getEntityList().get(0).toString();
            	auditTrailDAOObj.insertAuditLog(AuditAction.MASTER_DELETED, "User <b>"+fullName+"</b> deleted <b>"+result.getRecordTotalCount()+"</b> master item(s) of master type <b>"+groupName+"</b>", request, "0");
            	msg.put("data","Cost center deleted successfully.");
            }else{
            	msg.put("success",false);
            }
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+" <b>"+ids.length+"</b> master item(s) of master group <b>"+groupName+"</b>", request);
            jobj.put("valid",true);
            jobj.put("data",msg.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }

        return new ModelAndView("jsonView","model",jobj.toString());
    }

    public ModelAndView setEmpIdFormat(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        JSONObject jobj =  new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {


            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("employeeidformat",request.getParameter("employeeidformat"));
            requestParams.put("jobidformat",request.getParameter("jobidformat"));
            requestParams.put("financialmonth",request.getParameter("financialmonth"));
            requestParams.put("weeklyholiday",request.getParameter("weeklyholiday"));
            requestParams.put("subcription",request.getParameter("subcription"));
            requestParams.put("holidaycode",request.getParameter("holidaycode"));
            requestParams.put("emailNotification",request.getParameter("emailNotification"));
              if(!StringUtil.isNullOrEmpty(request.getParameter("selfappraisal"))){
                  requestParams.put("selfappraisal",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("competencies"))){
                  requestParams.put("competencies",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("timesheetjob"))){
                  requestParams.put("timesheetjob",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("blockemployees"))){
                  requestParams.put("blockemployees",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("goals"))){
                  requestParams.put("goals",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("annmng"))){
                  requestParams.put("annmng",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("approveappr"))){
                  requestParams.put("approveappr",true);
              }
             if(!StringUtil.isNullOrEmpty(request.getParameter("promotionrec"))){
                  requestParams.put("promotionrec",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("weightage"))){
                  requestParams.put("weightage",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("reviewappraisal"))){
                  requestParams.put("reviewappraisal",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("partial"))){
                  requestParams.put("partial",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("fullupdates"))){
                  requestParams.put("fullupdates",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("modaverage"))){
                  requestParams.put("modaverage",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("overallcomments"))){
                  requestParams.put("overallcomments",true);
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("defaultapps"))){
                  requestParams.put("defaultapps",request.getParameter("defaultapps"));
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("payrollbase"))){
                  requestParams.put("payrollbase",request.getParameter("payrollbase"));
              }
              if(!StringUtil.isNullOrEmpty(request.getParameter("approvesalary"))){
                  requestParams.put("approvesalary",Boolean.parseBoolean(request.getParameter("approvesalary")));
              }
              
              result= hrmsMasterConfigDAOObj.setEmpIdFormat(requestParams);
              if(result.isSuccessFlag())
            	  auditTrailDAOObj.insertAuditLog(AuditAction.COMPANY_UPDATION, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has edited Company Preferences ", request, "0");
              jobj.put("success",true);
              jobj.put("approvesalary",Boolean.parseBoolean(request.getParameter("approvesalary")));
//            session.saveOrUpdate(cmpPref);
                txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }

        return new ModelAndView("jsonView","model",jobj.toString());

    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
