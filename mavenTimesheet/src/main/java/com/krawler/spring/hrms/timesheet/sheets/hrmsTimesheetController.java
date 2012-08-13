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
package com.krawler.spring.hrms.timesheet.sheets;

import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.timesheet.Timesheet;
import com.krawler.hrms.timesheet.TimesheetTimer;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

public class hrmsTimesheetController extends MultiActionController implements MessageSourceAware{

    private String successView;
    private sessionHandlerImpl sessionHandlerImplObj;
	private hrmsCommonDAO hrmsCommonDAOObj;
    private hrmsTimesheetDAO hrmsTimesheetDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private HibernateTransactionManager txnManager;
    private MessageSource messageSource;
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
    
    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public hrmsTimesheetDAO getHrmsTimesheetDAO() {
        return hrmsTimesheetDAOObj;
    }

    public void setHrmsTimesheetDAO(hrmsTimesheetDAO hrmsTimesheetDAOObj) {
        this.hrmsTimesheetDAOObj = hrmsTimesheetDAOObj;
    }

    public profileHandlerDAO getProfileHandlerDAO() {
        return profileHandlerDAOObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }
    
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public ModelAndView AllTimesheets(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;

        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy");
        Date startdate;
        Date enddate;
        String count = "";
        int start, limit;
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("Cid", AuthHandler.getCompanyid(request));
            requestParams.put("startdate", request.getParameter("startdate"));
            requestParams.put("enddate", request.getParameter("enddate"));
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("userid", userid);
            if(hrmsCommonDAOObj.isAdmin(userid)){
            	requestParams.put("isAdmin", true);
            }else{
            	requestParams.put("isAdmin", false);
            }
            if (request.getParameter("start") == null) {
                start = 0;
                limit = 15;
            } else {
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            requestParams.put("start", start);
            requestParams.put("limit", limit);

            startdate = fmt.parse(request.getParameter("startdate"));
            enddate = fmt.parse(request.getParameter("enddate"));

            result = hrmsTimesheetDAOObj.AllTimesheets(requestParams);
            count = Integer.toString(result.getRecordTotalCount());
            Iterator ite = result.getEntityList().iterator();
            while (ite.hasNext()) {
                Object[] obj1 = (Object[]) ite.next();

                int total = Integer.parseInt(obj1[5].toString());
                int min = Integer.parseInt(obj1[6].toString());

                total += min/60;
                min = min%60;
                if (obj1[4] == null) {
                    obj1[4] = "";
                }
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("empid", obj1[0]);
                tmpObj.put("empname", obj1[1] + " " + obj1[4]);
                tmpObj.put("status", obj1[2]);
                tmpObj.put("approvedby", obj1[3]);
                tmpObj.put("work", total + " hrs," + min + " mins");
                tmpObj.put("hours", 48);
                tmpObj.put("startdate", df.format(startdate));
                tmpObj.put("enddate", df.format(enddate));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView EmployeesTimesheet(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        String userid;
        String min;
        String hrs;
        try {
            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                userid = AuthHandler.getUserid(request);
            } else {
                userid = request.getParameter("empid");
            }
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("userid", userid);
                requestParams.put("startdate", request.getParameter("startdate"));
                requestParams.put("enddate", request.getParameter("enddate"));

            result = hrmsTimesheetDAOObj.timesheetByUserID(requestParams);
            List lst=result.getEntityList();
            Iterator ite = lst.iterator();
            JSONObject tmpObj = null;
            for (int i = 1; i <= 7; i++) {
                if (!ite.hasNext()) {
                    break;
                }
                Timesheet tmsht = (Timesheet) ite.next();
                if (i == 1) {
                    tmpObj = new JSONObject();
                    if(tmsht.isText()){
                    	tmpObj.put("jobtype", tmsht.getJobtype());
                    	tmpObj.put("jobtypename", tmsht.getJobtype());
                    }else{
                    	MasterData md = (MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", tmsht.getJobtype());
                    	tmpObj.put("jobtypename",(md!=null?md.getValue():tmsht.getJobtype()));
                    	tmpObj.put("jobtype", tmsht.getJobtype());
                    }
                    tmpObj.put("isSubmitted", tmsht.getIsSubmitted()==1);//1 to submit
                }
                hrs= tmsht.getWorktime().toString();
                hrs=(hrs.length()==1?"0"+hrs:hrs);
                min= tmsht.getWorktimemin().toString();
                min=(min.length()==1?"0"+min:min);
                tmpObj.put("col" + i, hrs+":"+min);
                tmpObj.put("colid" + i, tmsht.getId());
                if (i == 7) {
                    jarr.put(tmpObj);
                    i = 0;
                }

            }
            
            TimesheetTimer timer = hrmsTimesheetDAOObj.getTimer(sessionHandlerImplObj.getUserid(request));
            if(timer!=null){
            	jobj.put("timerflag", true);
            }else{
            	jobj.put("timerflag", false);
            }
            	
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView insertTimeSheet(HttpServletRequest request,HttpServletResponse response){
        JSONObject msg = new JSONObject();
        JSONObject titleMsg = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jobj = new JSONObject();
        KwlReturnObject result=null;
        
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
        	Boolean isSubmitted = request.getParameter("isSubmitted").equals("true")?true:false;
        	String[] dateA = request.getParameterValues("colHeader");
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date submissionLastDate = dateA.length>0 ? simpleDateFormat.parse(dateA[dateA.length-1]):null;
            if(isSubmitted && submissionLastDate.compareTo(new Date())>0){
            	msg.put("msg",messageSource.getMessage("hrms.timesheet.YoucannotsubmittheTimesheetforfuturedates", null,"You cannot submit the 'Timesheet' for future dates.", RequestContextUtils.getLocale(request)));
            	titleMsg.put("titleMsg", messageSource.getMessage("hrms.common.warning", null,"Warning", RequestContextUtils.getLocale(request)));
            	jsonArray.put(titleMsg);
            	jsonArray.put(msg);
            	jobj.put("valid", true);
                jobj.put("data", jsonArray.toString());
            }else{
            	HashMap<String,Object> requestParams = new HashMap<String, Object>();
            	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                String empid;
                if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                    requestParams.put("empid",AuthHandler.getUserid(request));
                    empid=AuthHandler.getUserid(request);
                } else {
                    requestParams.put("empid",request.getParameter("empid"));
                    empid=request.getParameter("empid");
                }
                requestParams.put("isSubmitted", request.getParameter("isSubmitted"));
                requestParams.put("jsondata", request.getParameter("jsondata"));
                requestParams.put("colHeader", request.getParameterValues("colHeader"));
                Locale locale = RequestContextUtils.getLocale(request);
                requestParams.put("locale", locale);
                result=hrmsTimesheetDAOObj.insertTimeSheet(requestParams);
                msg.put("msg",result.getMsg());
                titleMsg.put("titleMsg", messageSource.getMessage("hrms.common.success", null,"Success", RequestContextUtils.getLocale(request)));
                jsonArray.put(titleMsg);
                jsonArray.put(msg);
                jobj.put("valid", true);
                jobj.put("data", jsonArray.toString());
                if(result.isSuccessFlag() && (requestParams.get("isSubmitted").equals("true")?true:false))
                	auditTrailDAOObj.insertAuditLog(AuditAction.TIMESHEET_SUBMITTED, "Employee " + profileHandlerDAOObj.getUserFullName(empid) + " has filled timesheet for the duration from " + dateA[0] +" to " +dateA[6], request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        } 
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deletetimesheetjobs(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject msg = new JSONObject();
        KwlReturnObject result = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("ids", request.getParameterValues("ids"));
            result=hrmsTimesheetDAOObj.deletetimesheetjobs(requestParams);
            msg.put("msg",result.getMsg());
            jobj.put("valid", true);
            jobj.put("data", msg.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView AlltimesheetsApproval(HttpServletRequest request, HttpServletResponse response) {

        JSONObject jobj = new JSONObject();
        JSONObject msg = new JSONObject();
        KwlReturnObject result = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            startdate = (Date) fmt.parse(request.getParameter("startdate"));
            enddate = (Date) fmt.parse(request.getParameter("enddate"));
            HashMap<String, Object> requestParams =new HashMap<String, Object>();
            requestParams.put("startdate", request.getParameter("startdate"));
            requestParams.put("enddate", request.getParameter("enddate"));
            requestParams.put("managername",profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)));
            requestParams.put("ids",request.getParameterValues("empids"));
            String action = request.getParameter("action");
            if(action.equals("reject")) {
                requestParams.put("submitstatus",0);//reset to draft 
                requestParams.put("approvestatus",2);//reject
            } else if(action.equals("approve")) {
                requestParams.put("approvestatus",1);//approve
            }
            

            result=hrmsTimesheetDAOObj.AlltimesheetsApproval(requestParams);
            msg.put("success",result.getMsg());
            jobj.put("valid", true);
            jobj.put("data", msg.toString());
            String[] ids = request.getParameterValues("empids");
//            ProfileHandler.insertAuditLog(session, AuditAction.TIME_SHEET_APPROVED, "Timesheet of "+AuthHandler.getFullName(tmsht.getUserID())+" from "+startdate+" to "+enddate+" approved by "+AuthHandler.getFullName(session, AuthHandler.getUserid(request)), request);
            for(int i=0;i<ids.length;i++){
                auditTrailDAOObj.insertAuditLog(AuditAction.TIME_SHEET_APPROVED, "Timesheet of " + profileHandlerDAOObj.getUserFullName(ids[i]) + " from " + startdate + " to " +enddate + " is approved by " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)), request, "0");
            }
            txnManager.commit(status);

        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        } 
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView timesheetExport(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        String userid;
        String min;
        String hrs;
        int hr=0;
        int mn=0;
        try {

            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                userid = AuthHandler.getUserid(request);
            } else {
                userid = request.getParameter("empid");
            }
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("userid", userid);
                requestParams.put("startdate", request.getParameter("startdate"));
                requestParams.put("enddate", request.getParameter("enddate"));

            result = hrmsTimesheetDAOObj.timesheetByUserID(requestParams);
            List lst=result.getEntityList();
            Iterator ite = lst.iterator();
            JSONObject tmpObj = null;
            for (int i = 1; i <= 7; i++) {
                if (!ite.hasNext()) {
                    break;
                }
                Timesheet tmsht = (Timesheet) ite.next();
                if (i == 1) {
                    tmpObj = new JSONObject();
                    if(tmsht.isText()){
                    	tmpObj.put("jobtype", tmsht.getJobtype());
                    	tmpObj.put("jobtypename", tmsht.getJobtype());
                    }else{
                    	MasterData md = (MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", tmsht.getJobtype());
                    	tmpObj.put("jobtypename",(md!=null?md.getValue():tmsht.getJobtype()));
                    	tmpObj.put("jobtype", tmsht.getJobtype());
                    }
                }
                    hr += tmsht.getWorktime();
                    mn +=tmsht.getWorktimemin();
                    if(mn>=60){
                     hr++;
                     mn -=60;
                    }
                hrs= tmsht.getWorktime().toString();
                hrs=(hrs.length()==1?"0"+hrs:hrs);
                min= tmsht.getWorktimemin().toString();
                min=(min.length()==1?"0"+min:min);
                tmpObj.put("col" + i, hrs+":"+min+" hrs");
                tmpObj.put("colid" + i, tmsht.getId());
                if (i == 7) {
                    tmpObj.put("total",(hr<10?"0"+hr:hr)+":"+(mn<10?"0"+mn:mn)+" hrs");
                    jarr.put(tmpObj);
                    i = 0;
                    hr=0;
                    mn=0;
                }

            }
            jobj.put("data", jarr);
            jobj.put("count", count);

            exportDAOImplObj.processRequest(request, response, jobj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }

    
    
    public ModelAndView setTimer(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams =new HashMap<String, Object>();
            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
            requestParams.put("startdate", new Date());
            requestParams.put("stopdate", null);
            requestParams.put("name", request.getParameter("id"));
            	
            hrmsTimesheetDAOObj.setTimer(requestParams);
            jobj.put("valid", true);
            jobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        } 
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    
    public ModelAndView stopTimer(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject msg = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
        	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        	TimesheetTimer time = hrmsTimesheetDAOObj.getTimer(sessionHandlerImplObj.getUserid(request));
            Calendar cal = Calendar.getInstance();	
        	        	
            String[] dates = (String[]) request.getParameterValues("colHeader");
            String hours = "";
    		String minutes = "";
    		boolean timeExceeds = false;
    		
    		if(!StringUtil.isNullOrEmpty(request.getParameter("timeExceeds"))){
    			timeExceeds = Boolean.parseBoolean(request.getParameter("timeExceeds"));
    		}
    		
    		if(timeExceeds){
    			String[] arr = request.getParameter("time").split(":");
    			if(arr.length>0){
    				hours = arr[0];
    			}
    			
    			if(arr.length>1){
    				minutes = arr[1];
    			}
    		}else{
    			long diffMinute = 0;
	            if(time!=null){
	            	diffMinute = getMinutes(time.getStartTime(), new Date());
	            }
	            
	    		if(diffMinute/60<10){
	    			hours="0"+diffMinute/60;
	    		}else{
	    			hours=String.valueOf(diffMinute/60);
	    		}
	    		
	    		if(diffMinute%60<10){
	    			minutes="0"+diffMinute%60;
	    		}else{
	    			minutes=String.valueOf(diffMinute%60);
	    		}
    		}
    		
    		if(time!=null){
    			cal.setTime(time.getStartTime());
    		}else{
    			cal.setTime(new Date());
    		}
    		cal.set(Calendar.HOUR_OF_DAY, 0);
        	cal.set(Calendar.MINUTE, 0);
        	cal.set(Calendar.SECOND, 0);
        	cal.set(Calendar.MILLISECOND, 0);
    		
    		boolean success = false;
            if(Integer.parseInt(hours)<24){
            	HashMap<String, Object> requestParams =new HashMap<String, Object>();
                requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
                requestParams.put("stopdate", new Date());
                requestParams.put("name", request.getParameter("name"));
                
                TimesheetTimer timer = hrmsTimesheetDAOObj.setTimer(requestParams);
                
            	Date startdate = null;
        		if(!StringUtil.isNullOrEmpty(request.getParameter("startdate"))){
        			startdate = fmt.parse(request.getParameter("startdate"));
        		}
        		Date enddate = null;
        		if(!StringUtil.isNullOrEmpty(request.getParameter("enddate"))){
        			enddate = fmt.parse(request.getParameter("enddate"));
        		}
        		
                List<Timesheet> list = hrmsTimesheetDAOObj.getTimesheet(sessionHandlerImplObj.getUserid(request), startdate, enddate, timer.getJobname());
                if(list!=null && list.isEmpty()){
                    JSONObject object = new JSONObject(request.getParameter("jsondata"));
                    for (int j = 1; j <= 7; j++) {
                    	Date date = fmt.parse(dates[j - 1]);
                    	if(timer.getStopTime()!=null){
    	                	if(date.compareTo(cal.getTime())==0){
    	                		object.put("jobtype", timer.getJobname());
    	                		object.put("col"+j, hours+":"+minutes);
    	                	}
                    	}
                    }
                    requestParams.put("empid", sessionHandlerImplObj.getUserid(request));
                    requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    requestParams.put("isSubmitted", request.getParameter("isSubmitted"));
                    requestParams.put("jsondata", object.toString());
                    requestParams.put("colHeader", request.getParameterValues("colHeader"));
                    Locale locale = RequestContextUtils.getLocale(request);
                	requestParams.put("locale", locale);
                    hrmsTimesheetDAOObj.insertTimeSheet(requestParams);
                }else{
                	for(Timesheet timesheet: list){
                		if(timesheet.getDatevalue()!=null && timesheet.getDatevalue().compareTo(cal.getTime())==0){
                			int hr = 0;
                			if(timesheet.getWorktime()!=null){
                				hr = timesheet.getWorktime();
                			}
                			int min = 0;
                			if(timesheet.getWorktimemin()!=null){
                				min = timesheet.getWorktimemin();
                			}
                			long timeInMinutes = getMinutes(Integer.parseInt(hours), hr, Integer.parseInt(minutes), min);
                			if(timeInMinutes/60<10){
            	    			hours="0"+timeInMinutes/60;
            	    		}else{
            	    			hours=String.valueOf(timeInMinutes/60);
            	    		}
            	    		
            	    		if(timeInMinutes%60<10){
            	    			minutes="0"+timeInMinutes%60;
            	    		}else{
            	    			minutes=String.valueOf(timeInMinutes%60);
            	    		}
                			
    	            		timesheet.setWorktime(Integer.parseInt(hours));
    	            		timesheet.setWorktimemin(Integer.parseInt(minutes));
    	            		hrmsTimesheetDAOObj.saveTimesheet(timesheet);
    	            		break;
                		}
                	}
                }
                success = true;
            }else{
            	success = false;
            }
            
            msg.put("success", success);
            jobj.put("valid", true);
            jobj.put("data", msg.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        } 
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView timerStatus(HttpServletRequest request,HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
        	TimesheetTimer timer = hrmsTimesheetDAOObj.getTimer(sessionHandlerImplObj.getUserid(request));
        	if(timer!=null && timer.getStopTime()==null){
        		jobj.put("started", true);
        		jobj.put("jobname", timer.getJobname());
        	}else{
        		jobj.put("started", false);
        	}
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    
    
    public ModelAndView cancelTimer(HttpServletRequest request,HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
        	TimesheetTimer timer = hrmsTimesheetDAOObj.getTimer(sessionHandlerImplObj.getUserid(request));
        	hrmsTimesheetDAOObj.deleteTimesheet(timer);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    
    
    public long getMinutes(Date startDate, Date stopDate){
    	long diffMinutes = 0;
    	if(startDate!=null && stopDate!=null){
    		long diff = stopDate.getTime() - startDate.getTime();
    		diffMinutes = diff / (60 * 1000);
    	}
    	return diffMinutes;
    }
    
    
    public long getMinutes(int hours1, int hours2, int minutes1, int minutes2){
    	long diffMinutes = 0;
    	diffMinutes = (hours1*60) + (hours2*60) + minutes1 + minutes2;
    	return diffMinutes;
    }
}
