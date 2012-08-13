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
package com.krawler.spring.hrms.payroll.salaryslip;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.common.util.StringUtil;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONObject;

public class ExportSalarySlipController extends MultiActionController {
	private String successView;
	private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private ExportSalarySlipService exportSalarySlipService;
    private sessionHandlerImpl sessionHandlerImplObj;
    
    public void setSuccessView(String successView) {
        this.successView = successView;
    }
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }
    
    public void setExportSalarySlipService(ExportSalarySlipService exportSalarySlipService) {
		this.exportSalarySlipService = exportSalarySlipService;
	}
    
    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
   
    public ModelAndView exportSalarySlip(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jsonResp = new JSONObject();
    	try{
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    		Date startdate = df.parse(request.getParameter("startdate"));
    		Date enddate = df.parse(request.getParameter("enddate"));
    		exportSalarySlipService.exportSalarySlip(request.getParameter("userid"), startdate, enddate, request, response, request.getParameter("historyid"));
    		jsonResp.put("valid", true);
            jsonResp.put("data", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return new ModelAndView("jsonView", "model", jsonResp.toString());
    }
    
    
    public ModelAndView printSalarySlip(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jsonResp = new JSONObject();
    	try{
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    		Date startdate = df.parse(request.getParameter("startdate"));
    		Date enddate = df.parse(request.getParameter("enddate"));
    		exportSalarySlipService.printSalarySlip(request.getParameter("userid"), startdate, enddate, request, response, request.getParameter("historyid"));
    		jsonResp.put("valid", true);
            jsonResp.put("data", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return new ModelAndView("jsonView", "model", jsonResp.toString());
    }
    
    
    public ModelAndView exportPayDetails(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jsonResp = new JSONObject();
    	try{
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    		Date startdate = df.parse(request.getParameter("startdate"));
    		Date enddate = df.parse(request.getParameter("enddate"));
    		String companyId = sessionHandlerImplObj.getCompanyid(request);
    		int frequency = Integer.parseInt(request.getParameter("frequency"));
            String module = request.getParameter("module");
            Integer status = 0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("status"))){
            	status = Integer.parseInt(request.getParameter("status"));
            }
    		exportSalarySlipService.exportPayDetails(enddate, frequency, request, response, companyId,module, status);
    		jsonResp.put("valid", true);
            jsonResp.put("data", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return new ModelAndView("jsonView", "model", jsonResp.toString());
    }
}
