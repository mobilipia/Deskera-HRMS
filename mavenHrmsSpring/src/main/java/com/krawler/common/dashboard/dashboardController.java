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
package com.krawler.common.dashboard;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.UserSearchState;
import com.krawler.common.admin.hrms_Modules;
import com.krawler.common.update.Updates;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.APICallHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.appraisal.hrmsAppraisalDAO;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.employee.hrmsEmpDAO;
import com.krawler.spring.hrms.goal.hrmsGoalDAO;
import com.krawler.spring.hrms.rec.job.hrmsRecJobDAO;
import com.krawler.spring.hrms.timesheet.sheets.hrmsTimesheetDAO;
import com.krawler.spring.permissionHandler.permissionHandler;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author karthik
 */
public class dashboardController extends MultiActionController implements MessageSourceAware{

    private hrmsCommonDAO hrmsCommonDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;
    private hrmsAppraisalDAO hrmsAppraisalDAOObj;
    private hrmsGoalDAO hrmsGoalDAOObj;
    private hrmsRecJobDAO hrmsRecJobDAOObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private hrmsTimesheetDAO hrmsTimesheetDAOObj;
    private hrmsEmpDAO hrmsEmpDAOObj;
	private HibernateTransactionManager txnManager;
    private String successView;
    private profileHandlerDAO profileHandlerDAOObj;
    private MessageSource messageSource;

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setHrmsAppraisalDAO(hrmsAppraisalDAO hrmsAppraisalDAOObj) {
        this.hrmsAppraisalDAOObj = hrmsAppraisalDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setPermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj;
    }

    public void setHrmsTimesheetDAO(hrmsTimesheetDAO hrmsTimesheetDAOObj) {
		this.hrmsTimesheetDAOObj = hrmsTimesheetDAOObj;
	}

    public void setHrmsEmpDAO(hrmsEmpDAO hrmsEmpDAOObj) {
		this.hrmsEmpDAOObj = hrmsEmpDAOObj;
	}

    public void setHrmsRecJobDAO(hrmsRecJobDAO hrmsRecJobDAOObj) {
        this.hrmsRecJobDAOObj = hrmsRecJobDAOObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setHrmsGoalDAO(hrmsGoalDAO hrmsGoalDAOObj) {
        this.hrmsGoalDAOObj = hrmsGoalDAOObj;
    }

    public void setHrmsAppraisalcycleDAO(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj) {
        this.hrmsAppraisalcycleDAOObj = hrmsAppraisalcycleDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }


    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public ModelAndView getBookmarksForWidgets(HttpServletRequest request, HttpServletResponse response){
         KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jArr = new JSONArray();
        int ctr = 0;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            kmsg = profileHandlerDAOObj.getUseBookmarks(sessionHandlerImplObj.getUserid(request));
            List ll = kmsg.getEntityList();
            for(Object Bookmarksobj : ll){
                    Object[] row = (Object[]) Bookmarksobj;
                    JSONObject obj = new JSONObject();
                    obj.put("linkname", row[1]);
                    obj.put("linkurl", row[2]);
                    jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", kmsg.getRecordTotalCount());
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    public ModelAndView getUpdatesForWidgets(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray temp = new JSONArray();
        JSONArray jarr = new JSONArray();
        JSONArray jArr = new JSONArray();
        List<Updates> updates = new ArrayList<Updates>();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            Iterator<Updates> ite;
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            int start1 = Integer.parseInt(start);
            int limit1 = Integer.parseInt(limit);
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = new StringBuffer();
            usersList.append("'" + userid + "'");
            String cmpsub = sessionHandlerImplObj.getCmpSubscription(request);
            // check for fullupdates option

            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, dateFmt.format(new Date()), sessionHandlerImplObj.getUserid(request));
            DateFormat df = kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request), sessionHandlerImplObj.getUserTimeFormat(request), sessionHandlerImplObj.getTimeZoneDifference(request));

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("checklink", "fullupdates");

            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
            requestParams1.put("userid", userid);
            requestParams1.put("userdate", userdate);

            Locale locale = RequestContextUtils.getLocale(request);
            
            if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                // submit appraisal update

                // check if subscribed for appraisal
                HashMap<String, Object> requestParams2 = new HashMap<String, Object>();
                requestParams2.clear();
                requestParams2.put("cmpsubscription", cmpsub);
                requestParams2.put("module", hrms_Modules.appraisal);

                if (hrmsCommonDAOObj.isSubscribed(requestParams2).isSuccessFlag()) {
                // Appraisal approved update
                requestParams.put("checklink", "appraisal");
                    if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                    	requestParams1.put("locale", locale);
                        result = hrmsAppraisalcycleDAOObj.getAppraisalapprovalupdate(requestParams1);
                        if(StringUtil.checkResultobjList(result)){
                            ite = result.getEntityList().iterator();
                            while (ite.hasNext()) {
                                    updates.add(ite.next());
                            }
                        }

                        requestParams.put("checklink", "goal");

                        //check goal option
                        if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                                // goal assigned update
                                requestParams1.put("df", df);
                                requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                                requestParams1.put("locale", locale);
                                result= hrmsGoalDAOObj.getgoalassignedupdate(requestParams1);
                                if(StringUtil.checkResultobjList(result)){
                                    ite = result.getEntityList().iterator();
                                    while (ite.hasNext()) {
                                        updates.add(ite.next());
                                    }
                                }

                                requestParams1.put("userdate", userdate);
                                requestParams1.put("locale", locale);
                                result= hrmsGoalDAOObj.getGoalEditUpdates(requestParams1);
                                if(StringUtil.checkResultobjList(result)){
                                    ite = result.getEntityList().iterator();
                                    while (ite.hasNext()) {
                                        updates.add(ite.next());
                                    }
                                }
                        }
                    }
                }

                // check if subscribed for timesheet
                requestParams1.clear();
                requestParams1.put("cmpsubscription", cmpsub);
                requestParams1.put("module", hrms_Modules.timesheet);
                if (hrmsCommonDAOObj.isSubscribed(requestParams1).isSuccessFlag()){
                	// Update for timesheet
                    requestParams1.clear();
                    requestParams1.put("df", df);
                    requestParams1.put("userid", userid);
                    requestParams1.put("userdate", userdate);
                    requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    requestParams1.put("status", 1);//Approve
                    requestParams1.put("locale", locale);
                    result = hrmsTimesheetDAOObj.timesheetsApprovalUpdates(requestParams1);
                    if(StringUtil.checkResultobjList(result)){
                    	ite = result.getEntityList().iterator();
                        while (ite.hasNext()) {
                            updates.add(ite.next());
                        }
                    }
                    requestParams1.put("status", 2);//Reject
                    requestParams1.put("locale", locale);
                    result = hrmsTimesheetDAOObj.timesheetsApprovalUpdates(requestParams1);
                    if(StringUtil.checkResultobjList(result)){
                    	ite = result.getEntityList().iterator();
                        while (ite.hasNext()) {
                            updates.add(ite.next());
                        }
                    }
                }

                // check if subscribed for recruitment
                requestParams1.clear();
                requestParams1.put("cmpsubscription", cmpsub);
                requestParams1.put("module", hrms_Modules.recruitment);
                if (hrmsCommonDAOObj.isSubscribed(requestParams1).isSuccessFlag()){
                // Update for interviewer
                    requestParams1.clear();
                    requestParams1.put("userid", userid);
                    requestParams1.put("userdate", userdate);
                    requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    requestParams1.put("df", df);
                    if(hrmsCommonDAOObj.isAdmin(userid)){
                    	requestParams1.put("isAdmin", true);
                    }else{
                    	requestParams1.put("isAdmin", false);
                    }
                    requestParams1.put("locale", locale);
                    result = hrmsRecJobDAOObj.getAppliedJobUpdate(requestParams1);
                    if(StringUtil.checkResultobjList(result)){
                    	ite = result.getEntityList().iterator();
                        while (ite.hasNext()) {
                        	updates.add(ite.next());
                        }
                    }
                }
            } else {
                requestParams.put("checklink", "appraisal");
                requestParams1.put( "selfappraisal",hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag());
                requestParams1.put("df", df);
                requestParams1.put("locale", locale);
                result = hrmsAppraisalcycleDAOObj.getAppraisalcycleupdatesDetail(requestParams1);
                if(StringUtil.checkResultobjList(result)){
                    ite = result.getEntityList().iterator();
                    while (ite.hasNext()) {
                    	updates.add(ite.next());
                    }
                }

                // Details update for appraisal cycle

            }
            requestParams.put("checklink", "reviewappraisal");
            requestParams1.put("userid", userid);
            requestParams1.put("userdate", userdate);
            if (!hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                hrmsAppraisalcycleDAOObj.updateappraisalstatus(requestParams1);
                requestParams.clear();
                requestParams.put("review", 1);
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("userdate", userdate);
                calculateModAvg(requestParams);
            } else {
                requestParams.clear();
                requestParams.put("review", 0);
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("userdate", userdate);
                calculateModAvg(requestParams);
            }
            Collections.sort(updates);
            updates = Updates.setUpdatesOrder(updates);
            Iterator itrUpdates = updates.iterator();
            while(itrUpdates.hasNext()){
            	JSONObject obj = new JSONObject();
            	obj.put("update", ((Updates) itrUpdates.next()).getUpdateDiv());
            	jArr.put(obj);
            }
            temp = jArr;
            int ed = Math.min(temp.length(), start1 + limit1);
            for (int i = start1; i < ed; i++) {
                jarr.put(temp.getJSONObject(i));
            }
            jobj.put("data", jarr);
            jobj.put("count", updates.size());
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
            Date lastActivityDate = null;
            if(request.getSession().getAttribute("lastActivityDate")!=null){
            	lastActivityDate = (Date) request.getSession().getAttribute("lastActivityDate");
            }
            terminationOfEmpForFutureDates(lastActivityDate);
            rehiringOfEmpForFutureDates(lastActivityDate);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getSavedSearchesForWidgets(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray temp = new JSONArray();
        JSONArray jarr = new JSONArray();
        JSONArray jArr = new JSONArray();
        
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            int start1 = Integer.parseInt(start);
            int limit1 = Integer.parseInt(limit);
            
            String userid = sessionHandlerImplObj.getUserid(request);
            
            List<UserSearchState> tabledata = hrmsCommonDAOObj.getSavedSearchesForUser(userid);

        	for(UserSearchState us :tabledata) {
	        	JSONObject obj = new JSONObject();
	            
	            obj.put("searchname", us.getSearchName().toString());
	            obj.put("searchid", us.getId().toString());
	            obj.put("module", us.getSearchFlag());
	        	jArr.put(obj);
            }	
            temp = jArr;
            int ed = Math.min(temp.length(), start1 + limit1);
            for (int i = start1; i < ed; i++) {
                jarr.put(temp.getJSONObject(i));
            }
            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getAlertsForWidgets(HttpServletRequest request, HttpServletResponse response){
    	KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray temp = new JSONArray();
        JSONArray jarr = new JSONArray();
        JSONArray jArr = new JSONArray();
        List<Updates> updates = new ArrayList<Updates>();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            Iterator<Updates> ite;
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            int start1 = Integer.parseInt(start);
            int limit1 = Integer.parseInt(limit);
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = new StringBuffer();
            usersList.append("'" + userid + "'");
            String cmpsub = sessionHandlerImplObj.getCmpSubscription(request);

            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, dateFmt.format(new Date()), sessionHandlerImplObj.getUserid(request));
            DateFormat df = kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request), sessionHandlerImplObj.getUserTimeFormat(request), sessionHandlerImplObj.getTimeZoneDifference(request));

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("checklink", "alerts");

            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
            requestParams1.put("userid", userid);
            requestParams1.put("userdate", userdate);

            // check if subscribed for appraisal
            HashMap<String, Object> requestParams2 = new HashMap<String, Object>();
            requestParams2.clear();
            requestParams2.put("cmpsubscription", cmpsub);
            requestParams2.put("module", hrms_Modules.appraisal);
            
            Locale locale = RequestContextUtils.getLocale(request);
        	
            if (hrmsCommonDAOObj.isSubscribed(requestParams2).isSuccessFlag()) {
            	requestParams1.put("locale", locale);
            	result = hrmsAppraisalcycleDAOObj.getappraisalcycleUpdates(requestParams1);
                if(StringUtil.checkResultobjList(result)){
                	ite = result.getEntityList().iterator();
                    while (ite.hasNext()) {
                    	updates.add(ite.next());
                    }
                }

                //Fill appraisal form update
                requestParams1.clear();
                requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams1.put("checklink", "appraisal");
                if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                	requestParams1.clear();
                	requestParams1.put("userid", userid);
                	requestParams1.put("userdate", userdate);
                	requestParams1.put("locale", locale);
                	result = hrmsAppraisalcycleDAOObj.getFillappraisalupdate(requestParams1);
                	if(StringUtil.checkResultobjList(result)){
                		ite = result.getEntityList().iterator();
                		while (ite.hasNext()) {
                			updates.add(ite.next());
                		}
                	}
                }
            }

            // check if subscribed for timesheet
            requestParams1.clear();
            requestParams1.put("cmpsubscription", cmpsub);
            requestParams1.put("module", hrms_Modules.timesheet);
            if (hrmsCommonDAOObj.isSubscribed(requestParams1).isSuccessFlag()){
            	// Update for timesheet
                requestParams1.clear();
                requestParams1.put("df", df);
                requestParams1.put("userid", userid);
                requestParams1.put("userdate", userdate);
                requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                if(hrmsCommonDAOObj.isAdmin(userid)){
                	requestParams1.put("isAdmin", true);
                }else{
                	requestParams1.put("isAdmin", false);
                }
                requestParams1.put("locale", locale);
                result = hrmsTimesheetDAOObj.timesheetsSubmittedUpdates(requestParams1);
                if(StringUtil.checkResultobjList(result)){
                 	ite = result.getEntityList().iterator();
                    while (ite.hasNext()) {
                      	updates.add(ite.next());
                    }
                }
                
                //Updates for timer
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            	sdf.setTimeZone(TimeZone.getTimeZone("GMT" + sessionHandlerImplObj.getTimeZoneDifference(request)));
            	List <Updates> list = hrmsTimesheetDAOObj.timerUpdates(hrmsTimesheetDAOObj.getTimer(sessionHandlerImplObj.getUserid(request)), sdf, locale);
                for(Updates update: list) {
                  	updates.add(update);
                }
            }

            // check if subscribed for recruitment
            requestParams1.clear();
            requestParams1.put("cmpsubscription", cmpsub);
            requestParams1.put("module", hrms_Modules.recruitment);
            if (hrmsCommonDAOObj.isSubscribed(requestParams1).isSuccessFlag()){
            	// Update for interviewer
                requestParams1.clear();
                requestParams1.put("userid", userid);
                requestParams1.put("locale", locale);
                result = hrmsRecJobDAOObj.getRecruiterupdate(requestParams1);
                if(StringUtil.checkResultobjList(result)){
                  	ite = result.getEntityList().iterator();
                    while (ite.hasNext()) {
                    	updates.add(ite.next());
                    }
                }
            }

            // check if subscribed for payroll
            requestParams1.clear();
            requestParams1.put("cmpsubscription", cmpsub);
            requestParams1.put("module", hrms_Modules.payroll);
            if (hrmsCommonDAOObj.isSubscribed(requestParams1).isSuccessFlag()){
            	// Update for interviewer
            	
            	CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", sessionHandlerImplObj.getCompanyid(request));
            	if(cp!=null && cp.getPayrollbase()!=null && cp.getPayrollbase().equals("Template")){
            	requestParams1.clear();
            	requestParams1.put("df", df);
            	requestParams1.put("userid", userid);
            	requestParams1.put("userdate", userdate);
                requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams1.put("locale", locale);
                result = hrmsEmpDAOObj.salaryGeneratedUpdates(requestParams1);
                if(StringUtil.checkResultobjList(result)){
                 	ite = result.getEntityList().iterator();
                    while (ite.hasNext()) {
                    	updates.add(ite.next());
                    }
                }
            	}
            }
            Collections.sort(updates);
            updates = Updates.setUpdatesOrder(updates);
            Iterator itrUpdates = updates.iterator();
            while(itrUpdates.hasNext()){
            	JSONObject obj = new JSONObject();
            	obj.put("alerts", ((Updates) itrUpdates.next()).getUpdateDiv());
            	jArr.put(obj);
            }
            temp = jArr;
            int ed = Math.min(temp.length(), start1 + limit1);
            for (int i = start1; i < ed; i++) {
                jarr.put(temp.getJSONObject(i));
            }
            jobj.put("data", jarr);
            jobj.put("count", updates.size());
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
            txnManager.commit(status);
        }catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getHRMSrecruitmentLinks(HttpServletRequest request, HttpServletResponse response){
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        JSONObject perms = new JSONObject();
        JSONObject permJobj = null;
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            KwlReturnObject kmsg = permissionHandlerDAOObj.getActivityFeature();
            permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            perms.put("Perm", permJobj.get("Perm"));
            requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            kmsg = permissionHandlerDAOObj.getPermission(requestParams);
            if(StringUtil.checkResultobjList(kmsg))
                permJobj = (JSONObject)kmsg.getEntityList().get(0);
            perms.put("UPerm", permJobj);
           if(hrmsCommonDAOObj.isPermitted(perms, "externaljobs", "view")) {
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.AddJobs", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "AddJobs2()");
                tempJObj.put("img","../../images/Add-jobs.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.recruitment.add.jobs.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if(hrmsCommonDAOObj.isPermitted(perms, "agencies", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.RecruitmentAgencies", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "Recruitagencies()");
                tempJObj.put("img","../../images/manage-recruitment-agencies.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.recruitment.agency.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if(hrmsCommonDAOObj.isPermitted(perms, "allapps", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.AllApplications", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "allapps()");
                tempJObj.put("img","../../images/all-application.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.AllApplications.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);

           }
            if(hrmsCommonDAOObj.isPermitted(perms, "recruiters", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.AssignInterviewer", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "recruiters()");
                tempJObj.put("img","../../images/Assign-interviewer.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.AssignInterviewer.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if (hrmsCommonDAOObj.isPermitted(perms, "internaljobboard", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.InternalJobBoard", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "internaljobBoard1()");
                tempJObj.put("img","../../images/internal-job-board.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.InternalJobBoard.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", "true");
            finalString = jobj1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",finalString);
        }

    }

    public ModelAndView getHRMSpayrollLinks(HttpServletRequest request,HttpServletResponse response) {
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        JSONObject perms = new JSONObject();
        JSONObject permJobj = null;
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("companyid", companyid);
            Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
            String countrycode= company.getCountry().getID();
            CompanyPreferences cp = hrmsCommonDAOObj.getCompanyPreferences(companyid);
            String userid = sessionHandlerImplObj.getUserid(request);
            KwlReturnObject kmsg = permissionHandlerDAOObj.getActivityFeature();
            permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            perms.put("Perm", permJobj.get("Perm"));
            requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            kmsg = permissionHandlerDAOObj.getPermission(requestParams);
            if(StringUtil.checkResultobjList(kmsg))
                permJobj = (JSONObject)kmsg.getEntityList().get(0);
            perms.put("UPerm", permJobj);
            if (hrmsCommonDAOObj.isPermitted(perms, "payroll", "view")) {
                if(cp.getPayrollbase() != null && cp.getPayrollbase().equals("Date")) {
                    tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.PayrollComponents", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "datePayrollComponentList()");
                    tempJObj.put("img","../../images/component-setting.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.PayrollComponents.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.generatepayroll", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "generatePayrollProcess()");
                    tempJObj.put("img","../../images/Target-List.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.generate.payroll.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.AuthorizePayroll", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "authorizePayrollProcess()");
                    tempJObj.put("img","../../images/Accounts.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.authorize.payroll.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                    
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.process.payroll", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "processPayroll()");
                    tempJObj.put("img","../../images/payroll-management.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.process.payroll.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                } else {//Template based
                    tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.PayrollComponents", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "masterConfig()");
                    tempJObj.put("img","../../images/component-setting.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.PayrollComponents.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.PayrollManagement", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "PayrollManagement()");
                    tempJObj.put("img","../../images/payroll-management.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.management.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.templatemanagement", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "TemplateManagement()");
                    tempJObj.put("img","../../images/Target-List.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.templatemanagement.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.SalaryReport", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "SalaryReport()");
                    tempJObj.put("img","../../images/salary-report.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.SalaryReport.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                    
                }
            }
            if(cp.getPayrollbase() == null || cp.getPayrollbase().equals("Template")) {
                if (hrmsCommonDAOObj.isPermitted(perms, "mypayslip", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.my.payslip", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "viewmypayslip()");
                    tempJObj.put("img","../../images/view-my-payslip.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.my.payslip.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                }
                
                if(hrmsCommonDAOObj.isAdmin(userid) || hrmsCommonDAOObj.isSalaryManager(userid)){
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.AuthorizeSalary", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "ApproveSalary()");
                    tempJObj.put("img","../../images/Accounts.png");
                    tempJObj.put("qtip","Click on the link to authorize salary for the employees.");
                    jArr.put(tempJObj);
                }                
            }else{
            	tempJObj = new JSONObject();
            	tempJObj.put("name", messageSource.getMessage("hrms.payroll.my.payslip", null, RequestContextUtils.getLocale(request)));
            	tempJObj.put("onclick", "myPayslipDate()");
            	tempJObj.put("img","../../images/view-my-payslip.png");
            	tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.my.payslip.tooltip", null, RequestContextUtils.getLocale(request)));
            	jArr.put(tempJObj);

                if(StringUtil.equal(countrycode, Constants.MALAYSIAN_COUNTRY_CODE)){

                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.my.tax.declaration", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "payrollData()");
                    tempJObj.put("img","../../images/incometax-user.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.my.tax.declaration.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.my.statutory.forms.details", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "payrollUserData()");
                    tempJObj.put("img","../../images/incometax.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.my.statutory.forms.details.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                
                }else {
                
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.payroll.my.tax.declaration", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "payrollDeclarationForm()");
                    tempJObj.put("img","../../images/incometax-user.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.payroll.my.tax.declaration.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                
                }
            }

            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", "true");
            finalString = jobj1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
             return new ModelAndView("jsonView","model",finalString);
        }
    }

    public ModelAndView getHRMStimesheetLinks(HttpServletRequest request,HttpServletResponse response){
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
         JSONObject perms = new JSONObject();
        JSONObject permJobj = null;
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            KwlReturnObject kmsg = permissionHandlerDAOObj.getActivityFeature();
            permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            perms.put("Perm", permJobj.get("Perm"));
            requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            kmsg = permissionHandlerDAOObj.getPermission(requestParams);
            if(StringUtil.checkResultobjList(kmsg))
                permJobj = (JSONObject)kmsg.getEntityList().get(0);
            perms.put("UPerm", permJobj);
             if(hrmsCommonDAOObj.isPermitted(perms, "viewtimesheet", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.ApproveTimesheets", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "viewtimesheet()");
                tempJObj.put("img","../../images/approve-timesheets.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.timesheet.view.timesheets.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if(hrmsCommonDAOObj.isPermitted(perms, "mytimesheet", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Featurelist.mytimesheet", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "timesheet()");
                tempJObj.put("img","../../images/my-timesheet.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.timesheet.timesheet.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
                
                JSONObject tempJObj1 = new JSONObject();
                tempJObj1.put("name", messageSource.getMessage("hrms.timesheet.timer", null, RequestContextUtils.getLocale(request)));
                tempJObj1.put("onclick", "timer()");
                tempJObj1.put("img","../../images/timer.png");
                tempJObj1.put("qtip",messageSource.getMessage("hrms.timesheet.timer.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj1);
            }

            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", "true");
            finalString = jobj1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",finalString);
        }

    }

     public ModelAndView getHRMSperformanceLinks(HttpServletRequest request, HttpServletResponse response){
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject perms = new JSONObject();
        JSONObject permJobj = null;
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        KwlReturnObject result = null;
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            KwlReturnObject kmsg = permissionHandlerDAOObj.getActivityFeature();
            permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            perms.put("Perm", permJobj.get("Perm"));
            requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            kmsg = permissionHandlerDAOObj.getPermission(requestParams);
            if(StringUtil.checkResultobjList(kmsg))
                permJobj = (JSONObject)kmsg.getEntityList().get(0);
            perms.put("UPerm", permJobj);
            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
            requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams1.put("checklink", "competency");
            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
//              if ((!hrmsCommonDAOObj.isEmployee(requestParams).isSuccessFlag())&&(!hrmsCommonDAOObj.isManager(requestParams).isSuccessFlag())) {
                if (hrmsCommonDAOObj.isPermitted(perms, "competencymanagement", "view") || hrmsCommonDAOObj.isPermitted(perms, "competencymaster", "view")) {
                  tempJObj = new JSONObject();
                  tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.ManageCompetencies", null, RequestContextUtils.getLocale(request)));
                  tempJObj.put("onclick", "configCompetency()");
                  tempJObj.put("img","../../images/manage-competencies.png");
                  tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.ManageCompetencies.tooltip", null, RequestContextUtils.getLocale(request)));
                  jArr.put(tempJObj);
              }
            }
            requestParams1.put("checklink", "goal");
            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                if (hrmsCommonDAOObj.isPermitted(perms, "assigngoals", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.GoalSetting", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "allemployeegoals()");
                    tempJObj.put("img","../../images/goal-setting.png");
                    tempJObj.put("qtip", messageSource.getMessage("hrms.performance.assign.goal.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                }
            }
            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                if (hrmsCommonDAOObj.isPermitted(perms, "mygoals", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.MyGoals", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "myGoals()");
                    tempJObj.put("img","../../images/my-goals.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.MyGoals.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                }
            }
            if (hrmsCommonDAOObj.isPermitted(perms, "initiateappraisal", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.performance.initiate.appraisal", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "InitiateAppraisal()");
                tempJObj.put("img","../../images/initiate-appraisal.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.performance.initiate.appraisal.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);

            }
            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, dateFmt.format(new Date()), sessionHandlerImplObj.getUserid(request));
            requestParams.put("userdate", userdate);
            requestParams.put("employee", false);
            if (hrmsAppraisalcycleDAOObj.showappraisalForm(requestParams).isSuccessFlag()) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.AppraiseOthers", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "competencyedit()");
                tempJObj.put("img","../../images/appraisal-form.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.performance.appraisal.form.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            requestParams1.put("checklink", "reviewappraisal");
            if(hrmsAppraisalcycleDAOObj.isReviewer(requestParams).isSuccessFlag()&&hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()){
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.performance.review.appraisal", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "reviewAppraisal()");
                tempJObj.put("img","../../images/review-appraisal.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.ReviewAppraisal.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }

            requestParams1.put("checklink", "appraisal");
            requestParams.put("employee", true);
            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                if (hrmsCommonDAOObj.isPermitted(perms, "myappraisalform", "view") && hrmsAppraisalcycleDAOObj.showappraisalForm(requestParams).isSuccessFlag()) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.performance.my.appraisal.form", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "myAppraisal()");
                    tempJObj.put("img","../../images/my-apprisal-form.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.performance.my.appraisal.form.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                }
            }

            if (hrmsAppraisalcycleDAOObj.isReviewer(requestParams).isSuccessFlag()) {
                if (hrmsCommonDAOObj.isPermitted(perms, "finalscore", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.performance.appraisal.report", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "finalReport()");
                    tempJObj.put("img","../../images/appraisal-report.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.AppraisalReport.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                }
            }
            //if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
//                String qry = "from Appraisalmanagement where employee.userID=? and appcycle.submitenddate<? and appcycle.cycleapproval=?";
//                List tabledata1 = HibernateUtil.executeQuery(session, qry, new Object[]{AuthHandler.getUserid(request), userdate,true});
                ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
                filter_names.add("employee.userID");
                filter_values.add(sessionHandlerImplObj.getUserid(request));

                filter_names.add("<appcycle.submitenddate");
                filter_values.add(userdate);

                filter_names.add("appcycle.cycleapproval");
                filter_values.add(true);
                HashMap<String,Object> requestParams2 = new HashMap<String, Object>();
                requestParams2.put("filter_names", filter_names);
                requestParams2.put("filter_values", filter_values);

                result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams2);
                List tabledata1 = result.getEntityList();
                if (!tabledata1.isEmpty()) {
                    if (hrmsCommonDAOObj.isPermitted(perms, "myfinalscore", "view")) {
                        tempJObj = new JSONObject();
                        tempJObj.put("name", messageSource.getMessage("hrms.performance.my.appraisal.report", null, RequestContextUtils.getLocale(request)));
                        tempJObj.put("onclick", "myfinalReport()");
                        tempJObj.put("img", "../../images/my-apprisal-report.png");
                        tempJObj.put("qtip", messageSource.getMessage("hrms.Dashboard.MyAppraisalReport.tooltip", null, RequestContextUtils.getLocale(request)));
                        jArr.put(tempJObj);
                    }
                }
            //}

            requestParams1.put("checklink", "goal");
            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                if (hrmsCommonDAOObj.isPermitted(perms, "archivegoals", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.performance.archived.goals", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "archivedgoals()");
                    tempJObj.put("img","../../images/archived-goals.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.performance.archived.goals.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);
                }
            }
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

     public ModelAndView getHRMSadministrationLinks(HttpServletRequest request,HttpServletResponse response){
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        JSONObject perms = new JSONObject();
        JSONObject permJobj = null;
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("companyid", company.getCompanyID());
            CompanyPreferences cp = hrmsCommonDAOObj.getCompanyPreferences(company.getCompanyID());
            String countrycode= company.getCountry().getID();
            KwlReturnObject kmsg = permissionHandlerDAOObj.getActivityFeature();
            permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            perms.put("Perm", permJobj.get("Perm"));
            requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            kmsg = permissionHandlerDAOObj.getPermission(requestParams);
            if(StringUtil.checkResultobjList(kmsg))
                permJobj = (JSONObject)kmsg.getEntityList().get(0);
            perms.put("UPerm", permJobj);
            if(hrmsCommonDAOObj.isPermitted(perms, "useradmin", "view")) {
                tempJObj.put("name", messageSource.getMessage("hrms.administration.user.administration", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "employeemnt()");
                tempJObj.put("img","../../images/user-administration.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.UserAdministration.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }

            if(hrmsCommonDAOObj.isPermitted(perms, "masterconf", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.administration.master.configuration", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "loadAdminPage(2)");
                tempJObj.put("img","../../images/master-configuration.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.administration.master.configuration.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }

            if(hrmsCommonDAOObj.isPermitted(perms, "setappcycle", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.performance.set.appraisal.cycle", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "ConfigAppraisalCycleMaster()");
                tempJObj.put("img","../../images/set-appraisal-cycle.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.performance.set.appraisal.cycle.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if(hrmsCommonDAOObj.isPermitted(perms, "mytimesheet2", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.EmailTemplates", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "addEmailTemplate()");
                tempJObj.put("img","../../images/Email-Template.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.EmailTemplates.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if(hrmsCommonDAOObj.isPermitted(perms, "mytimesheet4", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.AddEmailTemplate", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "addNewEmailTemplate()");
                tempJObj.put("img","../../images/Email-Template-add.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.AddEmailTemplate.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if(hrmsCommonDAOObj.isPermitted(perms, "audittrail", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", messageSource.getMessage("hrms.administration.audit.trail", null, RequestContextUtils.getLocale(request)));
                tempJObj.put("onclick", "auditTrail()");
                tempJObj.put("img","../../images/audit-trail.png");
                tempJObj.put("qtip",messageSource.getMessage("hrms.administration.audit.trail.tooltip", null, RequestContextUtils.getLocale(request)));
                jArr.put(tempJObj);
            }
            if(cp.getPayrollbase() != null && cp.getPayrollbase().equals("Date")) {
                if(StringUtil.equal(countrycode, Constants.MALAYSIAN_COUNTRY_CODE)){
                    tempJObj = new JSONObject();
                    tempJObj.put("name", messageSource.getMessage("hrms.administration.user.tax.details", null, RequestContextUtils.getLocale(request)));
                    tempJObj.put("onclick", "payrollUserList()");
                    tempJObj.put("img","../../images/incometax-user-list.png");
                    tempJObj.put("qtip",messageSource.getMessage("hrms.administration.user.tax.details.tooltip", null, RequestContextUtils.getLocale(request)));
                    jArr.put(tempJObj);

                }
            }
            
            //To do - Need to uncomment
            /*if(hrmsCommonDAOObj.isPermitted(perms, "importf", "viewimportlog")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Import log");
                tempJObj.put("onclick", "callImportFilesLog()");
                tempJObj.put("img","../../images/import-log.png");
                tempJObj.put("qtip","Imported file\'s log.");
                jArr.put(tempJObj);
            }*/
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", "true");
            finalString = jobj1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",finalString);
        }
    }

     public ModelAndView getEPFLinks(HttpServletRequest request,HttpServletResponse response){
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        JSONObject perms = new JSONObject();
        JSONObject permJobj = null;
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            KwlReturnObject kmsg = permissionHandlerDAOObj.getActivityFeature();
            permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            perms.put("Perm", permJobj.get("Perm"));
            requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            kmsg = permissionHandlerDAOObj.getPermission(requestParams);
            if(StringUtil.checkResultobjList(kmsg))
                permJobj = (JSONObject)kmsg.getEntityList().get(0);
            perms.put("UPerm", permJobj);

            tempJObj = new JSONObject();
            tempJObj.put("name", messageSource.getMessage("hrms.Dashboard.EPFService", null, RequestContextUtils.getLocale(request)));
            tempJObj.put("onclick", "loadEPFPage()");
            tempJObj.put("img","../../images/master-configuration.png");
            tempJObj.put("qtip",messageSource.getMessage("hrms.Dashboard.EPFService.tooltip", null, RequestContextUtils.getLocale(request)));
            jArr.put(tempJObj);

            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", "true");
            finalString = jobj1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",finalString);
        }
    }

     @SuppressWarnings("finally")
     public ModelAndView getPartnerLinks(HttpServletRequest request,HttpServletResponse response){
         JSONObject jResult = new JSONObject();
         try {
             
             if(!StringUtil.isStandAlone()){
                String companyid = request.getParameter("companyid");
                String platformURL = this.getServletContext().getInitParameter("platformURL");
                JSONObject jobj = new JSONObject();
                jobj.put("companyid",companyid);
                jobj.put("userid",sessionHandlerImplObj.getUserid(request));
                jobj.put("subdomain",URLUtil.getDomainName(request));
                jobj.put("appid",Constants.DESKERA_APPLICATION_ID_HRMS);
                JSONObject appdata = APICallHandler.callApp(platformURL, jobj, companyid,"15");
                jResult.put("valid", true);
                jResult.put("success", true);
                jResult.put("data", appdata.toString());
             }   
             
         } catch (JSONException ex) {
             ex.printStackTrace();
         } finally{
        	 return new ModelAndView("jsonView","model",jResult.toString());
         }
     }


     // @@ to be tested
     public void calculateModAvg(HashMap<String,Object> requestParams){
         KwlReturnObject result = null;
        try {
            int review = Integer.valueOf(requestParams.get("review").toString());
            Date userdate = (Date) requestParams.get("userdate");
            String companyid = requestParams.get("companyid").toString();
            String query;
            HashMap<String,Object> requestParams1 = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList(),select = new ArrayList();
            if (review == 1) {
                filter_names.add("reviewstatus");
                filter_values.add(2);
                filter_names.add("appcycle.reviewed");
                filter_values.add(false);
                filter_names.add("<appcycle.submitenddate");
                filter_values.add(userdate);
                filter_names.add("appcycle.company.companyID");
                filter_values.add(companyid);
                select.add("distinct appcycle.id");
            } else {
                filter_names.add("<appcycle.submitenddate");
                filter_values.add(userdate);
                filter_names.add("appcycle.reviewed");
                filter_values.add(false);
                filter_names.add("appcycle.company.companyID");
                filter_values.add(companyid);
                select.add("distinct appcycle.id");
            }
            requestParams1.put("filter_names", filter_names);
            requestParams1.put("filter_values", filter_values);
            requestParams1.put("select", select);
            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams1);
            List recordTotalCount = result.getEntityList();
            Iterator itr = recordTotalCount.iterator();
            while (itr.hasNext()) {
                Object app = (Object) itr.next();
                StringUtil.Clear(requestParams1);
                StringUtil.Clear(filter_names,filter_values,select);
                if (review == 1) {
                    filter_names.add("reviewstatus");
                    filter_values.add(2);
                    filter_names.add("appcycle.reviewed");
                    filter_values.add(false);
                    filter_names.add("<appcycle.submitenddate");
                    filter_values.add(userdate);
                    filter_names.add("appcycle.id");
                    filter_values.add(app.toString());
                    select.add("distinct employee.userID");
                } else {
                    filter_names.add("appcycle.reviewed");
                    filter_values.add(false);
                    filter_names.add("<appcycle.submitenddate");
                    filter_values.add(userdate);
                    filter_names.add("appcycle.id");
                    filter_values.add(app.toString());
                    select.add("distinct employee.userID");
                }
                requestParams1.put("filter_names", filter_names);
                requestParams1.put("filter_values", filter_values);
                requestParams1.put("select", select);
                result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams1);
                List listCount1 = result.getEntityList();
                Iterator itr1 = listCount1.iterator();
                while (itr1.hasNext()) {
                    Object usr = (Object) itr1.next();
                    StringUtil.Clear(requestParams1);
                    StringUtil.Clear(filter_names,filter_values,select);
                    if (review == 1) {
                        filter_names.add("appcycle.id");
                        filter_values.add(app.toString());
                        filter_names.add("employee.userID");
                        filter_values.add(usr.toString());
                        filter_names.add("reviewstatus");
                        filter_values.add(2);
                        filter_names.add("appcycle.reviewed");
                        filter_values.add(false);
                        select.add("appraisalid");
//                        query2 = "select appraisalid from Appraisalmanagement where " +
//                                "appcycle.id=? and employee.userID = ? and reviewstatus=2 and " +
//                                "appcycle.reviewed=false";
                    } else {
                        filter_names.add("appcycle.id");
                        filter_values.add(app.toString());
                        filter_names.add("employee.userID");
                        filter_values.add(usr.toString());
                        select.add("appraisalid");
//                        query2 = "select appraisalid from Appraisalmanagement where " +
//                                "appcycle.id=? and employee.userID = ?";
                    }
                    requestParams1.put("filter_names", filter_names);
                    requestParams1.put("filter_values", filter_values);
                    requestParams1.put("select", select);
//                    List listCount2 = HibernateUtil.executeQuery(hibernateTemplate, query2, new Object[]{app.toString(), usr.toString()});
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams1);
                    List listCount2 = result.getEntityList();
                    StringUtil.Clear(requestParams1);
                    if (listCount2.size() > 0) {
//                        requestParams1.put("filter_names", Arrays.asList("appraisal.appraisalid","ISNOTcompetency"));
//                        requestParams1.put("filter_values", Arrays.asList(String.valueOf(listCount2.get(0)),null));
                        requestParams1.put("filter_names", Arrays.asList("appraisal.appraisalid"));
                        requestParams1.put("filter_values", Arrays.asList(String.valueOf(listCount2.get(0))));
                        requestParams1.put("append", " and competency is not null ");
                        requestParams1.put("select", Arrays.asList("competency.mid"));
                        result = hrmsAppraisalDAOObj.getAppraisal(requestParams1);
                        List lst = result.getEntityList();
//                        String hql = "select competency.mid from Appraisal where appraisal.appraisalid = ? and competency is not null";
//                        List lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{String.valueOf(listCount2.get(0))});
                        requestParams1.clear();
                        for (int i = 0; i < lst.size(); i++) {
                            ArrayList newArr=new ArrayList();
                            for (int j = 0; j < listCount2.size(); j++) {
                                requestParams1.put("filter_names", Arrays.asList("appraisal.appraisalid","competency.mid"));
                                requestParams1.put("filter_values", Arrays.asList(String.valueOf(listCount2.get(j)),String.valueOf(lst.get(i))));
                                requestParams1.put("select", Arrays.asList("compmanrating"));
                                result = hrmsAppraisalDAOObj.getAppraisal(requestParams1);
                                List wlst = result.getEntityList();
//                                hql = "select compmanrating from Appraisal where appraisal.appraisalid = ? and competency.mid=?";
//                                List wlst = HibernateUtil.executeQuery(session, hql, new Object[]{String.valueOf(listCount2.get(j)), String.valueOf(lst.get(i))});
                                if (wlst.size() > 0) {
                                    Double wt1;
                                    wt1 = (Double) wlst.get(0);
                                    if(wt1.intValue()!=0){
                                        newArr.add(wt1);
                                    }
                                }
                            }
                            double avgRat = 0;
                            requestParams1.put("companyid", companyid);
                            requestParams1.put("checklink", "modaverage");
                            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                                Collections.sort(newArr);
                                if (newArr.size() > 2) {
                                    for (int c = 1; c < newArr.size() - 1; c++) {
                                        avgRat += (Double)newArr.get(c);
                                    }
                                    avgRat = avgRat / (newArr.size() - 2);
                                } else {
                                    for (int c = 0; c < newArr.size(); c++) {
                                        avgRat += (Double)newArr.get(c);
                                    }
                                    if(newArr.size()>0){
                                    avgRat = avgRat / newArr.size();
                                    }
                                }
                            } else {
                                for (int c = 0; c < newArr.size(); c++) {
                                    avgRat += (Double)newArr.get(c);
                                }
                                if(newArr.size()>0){
                                avgRat = avgRat / newArr.size();
                                }
                            }
                            requestParams1.clear();
                            requestParams1.put("filter_names", Arrays.asList("appcycle.id","employee.userID","competency.mid"));
                            requestParams1.put("filter_values", Arrays.asList(app.toString(),usr.toString(),String.valueOf(lst.get(i))));
                            requestParams1.put("select", Arrays.asList("id"));
                            result = hrmsAppraisalDAOObj.getcompetencyAvg(requestParams1);
                            List wlst = result.getEntityList();
//                            hql = "select id from competencyAvg where appcycle.id = ? and employee.userID=? and competency.mid=?";
//                            List wlst = HibernateUtil.executeQuery(session, hql, new Object[]{app.toString(), usr.toString(), String.valueOf(lst.get(i))});
                            if (wlst.isEmpty()) {
//                                competencyAvg cmpavg = new competencyAvg();
//                                cmpavg.setAppcycle((Appraisalcycle) session.load(Appraisalcycle.class, app.toString()));
//                                cmpavg.setEmployee((User) session.load(User.class, usr.toString()));
//                                cmpavg.setCompetency((Managecmpt) session.load(Managecmpt.class, String.valueOf(lst.get(i))));
//                                cmpavg.setManavg(avgRat);
//                                session.save(cmpavg);
                                requestParams1.clear();
                                requestParams1.put("Appcycle", app.toString());
                                requestParams1.put("Employee", usr.toString());
                                requestParams1.put("Competency", String.valueOf(lst.get(i)));
                                requestParams1.put("Manavg", avgRat);
                                result = hrmsAppraisalDAOObj.addcompetencyAvg(requestParams1);

                            } else if (wlst.size() > 0) {
//                                competencyAvg cmpavg = (competencyAvg) session.get(competencyAvg.class, String.valueOf(wlst.get(0)));
//                                cmpavg.setManavg(avgRat);
//                                session.update(cmpavg);
                                requestParams1.clear();
                                requestParams1.put("Id", String.valueOf(wlst.get(0)));
                                requestParams1.put("Manavg", avgRat);
                                result = hrmsAppraisalDAOObj.addcompetencyAvg(requestParams1);
                            }
                        }
                    }
                }
                requestParams1.clear();
                requestParams1.put("reviewed", true);
                requestParams1.put("id", app.toString());
                result = hrmsAppraisalcycleDAOObj.addAppraisalcycle(requestParams1);
//                Appraisalcycle appcycle = (Appraisalcycle) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company.Appraisalcycle", app.toString());
//                appcycle.setReviewed(true);
//                hibernateTemplate.update(appcycle);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

     public boolean isFirstLoginForThisDay(Date lastActivityDate) {
 		boolean isFirstLogin = false;
 		if(lastActivityDate==null){
 			isFirstLogin = true;
 		}else{
 			Date date = new Date(lastActivityDate.getTime());
 			Calendar calendar = Calendar.getInstance();
 			calendar.set(Calendar.HOUR, 0);
 			calendar.set(Calendar.MINUTE, 0);
 			calendar.set(Calendar.SECOND, 0);
 			Date today = calendar.getTime();
 			if(date.compareTo(today) < 0){
 				isFirstLogin = true;
 			}
 		}
 		return isFirstLogin;
 	}

     public boolean terminationOfEmpForFutureDates(Date lastActivityDate){
    	if(isFirstLoginForThisDay(lastActivityDate)){
    		hrmsCommonDAOObj.editEmpProfileForTerOfFutureDates();
    		hrmsCommonDAOObj.editUserForTerOfFutureDates();
    	}
    	return false;
     }

     public boolean rehiringOfEmpForFutureDates(Date lastActivityDate){
    	if(isFirstLoginForThisDay(lastActivityDate)){
    		hrmsCommonDAOObj.editEmpProfileForRehOfFutureDates();
    		hrmsCommonDAOObj.editUserForRehOfFutureDates();
    	}
    	return false;
     }

     public ModelAndView getMaintainanceDetails(HttpServletRequest request, HttpServletResponse response) {
    	 JSONObject jobj = new JSONObject();
    	 boolean issuccess = false;
    	 String msg = "";
    	 try{
             if(!StringUtil.isStandAlone()){
                    JSONArray jarr=null;
                    String platformURL=this.getServletContext().getInitParameter("platformURL");
                    String hrmsURL = this.getServletContext().getInitParameter("hrmsURL");
                    String action = "9";
                    String companyID = sessionHandlerImplObj.getCompanyid(request);
                    JSONObject userData = new JSONObject();
                    userData.put("remoteapikey",StorageHandler.GetRemoteAPIKey());
                    userData.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                    userData.put("requesturl",hrmsURL);
                    JSONObject resObj = APICallHandler.callApp(platformURL, userData, companyID, action);
                    resObj.put("count", 1);
                    if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                            jarr=resObj.getJSONArray("data");
                    }
                    if (jarr!=null&&jarr.length()>0) {
                            jobj.put("data", jarr);
                            msg="Data fetched successfully";
                            issuccess = true;
                    } else {
                            msg="Error occurred while fetching data ";
                            issuccess = false;
                            resObj = new JSONObject();
                            resObj.put("msg", msg);
                            resObj.put("success", issuccess);
                    }
                    jobj.put("valid", true);
                    jobj.put("success", issuccess);
                    jobj.put("data", resObj.toString());
             }
             
    	 } catch (Exception ex){
    		 logger.warn("Error occured", ex);
    		 ex.printStackTrace();
    	 } finally {
    		 return new ModelAndView("jsonView","model", jobj.toString());
    	 }
     }
     
     @Override
     public void setMessageSource(MessageSource ms) {
         this.messageSource = ms;
     }
}
