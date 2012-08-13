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
package com.krawler.spring.companyDetails;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyHoliday;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.util.Constants;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Karthik
 */
public class companyDetailsController extends MultiActionController implements MessageSourceAware {

    private companyDetailsDAO companyDetailsDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private MessageSource messageSource;
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }

//    public ModelAndView getCompanyInformation(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException {
//        KwlReturnObject kmsg = null;
//        JSONObject jobj = new JSONObject();
//        try {
//            String companyid = sessionHandlerImplObj.getCompanyid(request);
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("companyid", companyid);
//
//            ArrayList filter_names = new ArrayList();
//            ArrayList filter_params = new ArrayList();
//            filter_names.add("c.companyID");
//            filter_params.add(companyid);
//
//            kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams, filter_names, filter_params);
//            jobj = getCompanyJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return new ModelAndView("jsonView", "model", jobj.toString());
//    }

//    public ModelAndView getAllCompanyDetails(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException {
//        KwlReturnObject kmsg = null;
//        JSONObject jobj = new JSONObject();
//        try {
//            String companyid = sessionHandlerImplObj.getCompanyid(request);
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("companyid", companyid);
//
//            ArrayList filter_names = new ArrayList();
//            ArrayList filter_params = new ArrayList();
//            filter_names.add("c.deleted");
//            filter_params.add(0);
//
//            kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams, filter_names, filter_params);
//            jobj = getCompanyJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return new ModelAndView("jsonView", "model", jobj.toString());
//    }

//    public JSONObject getCompanyJson(List ll, HttpServletRequest request, int totalSize) {
//        JSONArray jarr = new JSONArray();
//        JSONObject jobj = new JSONObject();
//        try {
//            Iterator ite = ll.iterator();
//            String timeFormatId = sessionHandlerImplObj.getUserTimeFormat(request);
//            String timeZoneDiff = sessionHandlerImplObj.getTimeZoneDifference(request);
//            while (ite.hasNext()) {
//                Object[] row = (Object[]) ite.next();
//                Company company =(Company) row[1];
//                CompanyPreferences cmpr=null;
//
//                JSONObject obj = new JSONObject();
//                obj.put("phone", company.getPhoneNumber());
//                obj.put("state", company.getState());
//                obj.put("currency", (company.getCurrency() == null ? Constants.CURRENCY_DEFAULT : company.getCurrency().getCurrencyID()));
//                obj.put("city", company.getCity());
//                obj.put("emailid", company.getEmailID());
//                obj.put("companyid", company.getCompanyID());
//                obj.put("timezone", (company.getTimeZone() == null ? Constants.TIMEZONE_DEFAULT : company.getTimeZone().getTimeZoneID()));
//                obj.put("zip", company.getZipCode());
//                obj.put("fax", company.getFaxNumber());
//                obj.put("website", company.getWebsite());
//                obj.put("image", company.getCompanyLogo());
//                obj.put("modifiedon", (company.getModifiedOn() == null ? "" : authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(company.getModifiedOn())));
//                obj.put("createdon", (company.getCreatedOn() == null ? "" : authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(company.getCreatedOn())));
//                obj.put("companyname", company.getCompanyName());
//                obj.put("country", (company.getCountry() == null ? "" : company.getCountry().getID()));
//                obj.put("address", company.getAddress());
//                obj.put("subdomain", company.getSubDomain());
//                obj.put("companyid", company.getCompanyID());
//                obj.put("companyname", company.getCompanyName());
//                obj.put("admin_fname", company.getCreator().getFirstName());
//                obj.put("admin_lname", company.getCreator().getLastName());
//                obj.put("admin_uname", company.getCreator().getUserLogin().getUserName());
//                obj.put("phoneno", company.getPhoneNumber());
//                if(row[0]!=null){
//                    cmpr=(CompanyPreferences)row[0];
//                    obj.put("campaign",cmpr.isCampaign());
//                    obj.put("lead",cmpr.isLead());
//                    obj.put("account",cmpr.isAccount());
//                    obj.put("contact",cmpr.isContact());
//                    obj.put("opportunity",cmpr.isOpportunity());
//                    obj.put("cases",cmpr.isCases());
//                    obj.put("product",cmpr.isProduct());
//                    obj.put("activity",cmpr.isActivity());
//
//                }else{
//                    obj.put("campaign",false);
//                    obj.put("lead",false);
//                    obj.put("account",false);
//                    obj.put("contact",false);
//                    obj.put("opprotunity",false);
//                    obj.put("cases",false);
//                    obj.put("product",true);
//                    obj.put("activity",false);
//
//                }
//                jarr.put(obj);
//            }
//            jobj.put("data", jarr);
//            jobj.put("count", totalSize);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return jobj;
//    }

    public ModelAndView getCompanyHolidays(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            kmsg = companyDetailsDAOObj.getCompanyHolidays(requestParams, filter_names, filter_params);
            jobj = getCompanyHolidaysJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getCompanyHolidaysJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String timeFormatId = sessionHandlerImplObj.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImplObj.getTimeZoneDifference(request);
            while (ite.hasNext()) {
                CompanyHoliday holiday = (CompanyHoliday) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("holiday", authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(holiday.getHolidayDate()));
                obj.put("description", holiday.getDescription());
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView updateCompany(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        HashMap hm = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String timeFormatId = sessionHandlerImplObj.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImplObj.getTimeZoneDifference(request);
            hm = new FileUploadHandler().getItems(request);
            hm.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            hm.put("dateformat", authHandler.getDateFormatter(timeFormatId, timeZoneDiff));

            companyDetailsDAOObj.updateCompany(hm);
            jobj.put("msg", messageSource.getMessage("hrms.commonlib.company.updated.successfully", null, RequestContextUtils.getLocale(request)));
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteCompany(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            companyDetailsDAOObj.deleteCompany(requestParams);
            jobj.put("msg", messageSource.getMessage("hrms.commonlib.company.deleted.successfully", null, RequestContextUtils.getLocale(request)));
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", "success:true");
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
    
//    public ModelAndView setCompanyPref(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException {
//       JSONObject myjobj = new JSONObject();
//       KwlReturnObject kmsg = null;
//       //Create transaction
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("JE_Tx");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        TransactionStatus status = txnManager.getTransaction(def);
//       try{
//            String cmpid = sessionHandlerImplObj.getCompanyid(request);
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("companyid", cmpid);
//            requestParams.put("heirarchypermisssioncampaign", request.getParameter("heirarchypermisssioncampaign"));
//            requestParams.put("heirarchypermisssionleads", request.getParameter("heirarchypermisssionleads"));
//            requestParams.put("heirarchypermisssionaccounts", request.getParameter("heirarchypermisssionaccounts"));
//            requestParams.put("heirarchypermisssioncontacts", request.getParameter("heirarchypermisssioncontacts"));
//            requestParams.put("heirarchypermisssionopportunity", request.getParameter("heirarchypermisssionopportunity"));
//            requestParams.put("heirarchypermisssioncases", request.getParameter("heirarchypermisssioncases"));
//            requestParams.put("heirarchypermisssionproduct", request.getParameter("heirarchypermisssionproduct"));
//            requestParams.put("heirarchypermisssionactivity", request.getParameter("heirarchypermisssionactivity"));
//
//            kmsg = companyDetailsDAOObj.setCompanyPref(requestParams);
//            request.getSession(true).setAttribute("companyPreferences", companyDetailsDAOObj.getCompanyPreferences(cmpid));
//            myjobj.put("success", kmsg.isSuccessFlag());
//            txnManager.commit(status);
//       } catch (Exception e) {
//           System.out.println(e.getMessage());
//           txnManager.rollback(status);
//       }
//       return new ModelAndView("jsonView", "model", myjobj.toString());
//    }
}
