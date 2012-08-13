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
package com.krawler.spring.hrms.payroll.incometax;

import com.krawler.common.util.StringUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.MalaysianDeduction;
import masterDB.MalaysianUserIncomeTaxInfo;
import masterDB.MalaysianUserTaxComponent;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.payroll.hrmsPayrollDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

public class MalaysianIncomeTaxController extends MultiActionController{
	private String successView;
    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsPayrollDAO hrmsPayrollDAOObj;
    private MalaysianIncomeTaxDAO malaysianIncomeTaxDAO;
    
    
    public void setHrmsPayrollDAO(hrmsPayrollDAO hrmsPayrollDAOObj1) {
        this.hrmsPayrollDAOObj = hrmsPayrollDAOObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
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

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

	public void setMalaysianIncomeTaxDAO(MalaysianIncomeTaxDAO malaysianIncomeTaxDAO) {
		this.malaysianIncomeTaxDAO = malaysianIncomeTaxDAO;
	}
	
	
	public ModelAndView getMalaysianDeductionComponents(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArrayCompulsory = new JSONArray();
        JSONArray jsonArrayOptional = new JSONArray();
        JSONArray jsonArrayAllowances = new JSONArray();
        JSONObject jsonType = new JSONObject();
        int count = 0;
        try {
        	int type =-1;
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String userid = request.getParameter("userID");
        	Date year = sdf.parse(request.getParameter("year"));
        	List<MalaysianUserTaxComponent> malaysianUserTaxComponents = malaysianIncomeTaxDAO.getUserIncomeTaxComponent(userid, year, true);
        	if(malaysianUserTaxComponents!=null && malaysianUserTaxComponents.size()>0){
        		count = malaysianUserTaxComponents.size();
        		for(MalaysianUserTaxComponent component: malaysianUserTaxComponents){
        			MalaysianDeduction deduction = component.getDeduction();
	        		if(deduction.getParent()==null){
	        			type = deduction.getType();
	            		JSONObject jsonObj = new JSONObject();
	            		jsonObj.put("id", deduction.getId());
	        			jsonObj.put("name", deduction.getName());
	        			jsonObj.put("description", deduction.getDescription());
	        			jsonObj.put("amount", deduction.getAmount());
	        			jsonObj.put("type", type);
	        			jsonObj.put("categoryid", deduction.getCategoryId());
	        			jsonObj.put("parent", getChildsFromUserComponentList(malaysianUserTaxComponents, deduction.getId()));
	        			jsonObj.put("datatype", deduction.getDataType());
                        jsonObj.put("uniquecode", deduction.getUniqueCode());
	        			if(deduction.getDataType()==1){
	        				jsonObj.put("value", component.getAmount());
	        			}else{
	        				jsonObj.put("value", component.isChecked());
	        			}
	            		if(type==1){//Compulsory deductions
	            			jsonArrayCompulsory.put(jsonObj);
	            		} else if(type==2){//Optional deductions
	            			jsonArrayOptional.put(jsonObj);
	            		} else if(type==3){//Allowances deductions
	            			jsonArrayAllowances.put(jsonObj);
	            		}
	        		}
	        	}
        	}else{
	        	List<MalaysianDeduction> list = malaysianIncomeTaxDAO.getMalaysianDeductionComponents( year);
	        	count = list.size();
	        	for(MalaysianDeduction deduction: list){
	        		if(deduction.getParent()==null){
	        			type = deduction.getType();
	            		JSONObject jsonObj = new JSONObject();
	            		jsonObj.put("id", deduction.getId());
	        			jsonObj.put("name", deduction.getName());
	        			jsonObj.put("description", deduction.getDescription());
	        			jsonObj.put("amount", deduction.getAmount());
	        			jsonObj.put("type", type);
	        			jsonObj.put("categoryid", deduction.getCategoryId());
	        			jsonObj.put("parent", getChilds(list, deduction.getId()));
	        			jsonObj.put("datatype", deduction.getDataType());
                        jsonObj.put("uniquecode", deduction.getUniqueCode());
	        			
	            		if(type==1){//Compulsory deductions
	            			jsonArrayCompulsory.put(jsonObj);
	            		} else if(type==2){//Optional deductions
	            			jsonArrayOptional.put(jsonObj);
	            		} else if(type==3){//Allowances deductions
	            			jsonArrayAllowances.put(jsonObj);
	            		}
	        		}
	        	}
        	}
        	jsonType.put("userid", sessionHandlerImplObj.getUserid(request));
        	jsonType.put("compulsoryDeductions", jsonArrayCompulsory);
        	jsonType.put("optionalDeductions", jsonArrayOptional);
        	jsonType.put("allowanceDeductions", jsonArrayAllowances);
        	JSONObject userdata = getUserInformation(request, response);
        	jsonType.put("userdata", userdata);
        	json.put("success", true);
        	json.put("data", jsonType);
        	json.put("count", count);
            jsonObject.put("data", json.toString());
            jsonObject.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return new ModelAndView("jsonView","model",jsonObject.toString());
	}
	
	public JSONArray getChilds(List<MalaysianDeduction> list, String parent){
		JSONArray jsonArray = new JSONArray();
		try{
			for(MalaysianDeduction deduction: list){
				if(deduction.getParent()!=null && deduction.getParent().getId().equals(parent)){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", deduction.getId());
					jsonObject.put("name", deduction.getName());
					jsonObject.put("description", deduction.getDescription());
					jsonObject.put("amount", deduction.getAmount());
					jsonObject.put("type", deduction.getType());
					jsonObject.put("categoryid", deduction.getCategoryId());
					jsonObject.put("datatype", deduction.getDataType());
                    jsonObject.put("uniquecode", deduction.getUniqueCode());
					jsonArray.put(jsonObject);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonArray;
	}
	
	public JSONArray getChildsFromUserComponentList(List<MalaysianUserTaxComponent> list, String parent){
		JSONArray jsonArray = new JSONArray();
		try{
			for(MalaysianUserTaxComponent component: list){
				MalaysianDeduction deduction = component.getDeduction();
				if(deduction.getParent()!=null && deduction.getParent().getId().equals(parent)){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", deduction.getId());
					jsonObject.put("name", deduction.getName());
					jsonObject.put("description", deduction.getDescription());
					jsonObject.put("amount", deduction.getAmount());
					jsonObject.put("type", deduction.getType());
					jsonObject.put("categoryid", deduction.getCategoryId());
					jsonObject.put("datatype", deduction.getDataType());
                    jsonObject.put("uniquecode", deduction.getUniqueCode());
					if(deduction.getDataType()==1){
						jsonObject.put("value", component.getAmount());
        			}else{
        				jsonObject.put("value", component.isChecked());
        			}
					jsonArray.put(jsonObject);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonArray;
	}
	
	
    public ModelAndView saveUserIncomeTaxInformation(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String userid = request.getParameter("userid");
        	Date year = sdf.parse(request.getParameter("year"));
        	Map<MalaysianDeduction, String> map = new HashMap<MalaysianDeduction, String>();
        	List<MalaysianDeduction> list = malaysianIncomeTaxDAO.getMalaysianDeductionComponents(year);
        	for(MalaysianDeduction deduction:list){
        		map.put(deduction, request.getParameter(deduction.getId()));
        	}
        	
        	malaysianIncomeTaxDAO.saveUserIncomeTaxInformation(map, userid, year);
        	boolean success = saveUserInformation(request, response);
        	jsonObject.put("success", success);
            jsonObject.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jsonObject.toString());
    }
    
    
    public boolean saveUserInformation(HttpServletRequest request, HttpServletResponse response) {
        boolean success = true;
    	try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String userid = request.getParameter("userid");
        	int category = Integer.parseInt(request.getParameter("category"));
            int empStatus = Integer.parseInt(request.getParameter("empstatus"));
        	Date year = sdf.parse(request.getParameter("year"));

            double prevEarning =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("previous_earning"))){
                prevEarning = Double.parseDouble(request.getParameter("previous_earning"));
            }
            double prevIncomeTax =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("previous_income_tax"))){
                prevIncomeTax = Double.parseDouble(request.getParameter("previous_income_tax"));
            }
            double prevEPF =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("previous_epf"))){
                prevEPF = Double.parseDouble(request.getParameter("previous_epf"));
            }
            double prevLIC =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("previous_lic"))){
                prevLIC = Double.parseDouble(request.getParameter("previous_lic"));
            }
            double prevZakat =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("previous_zakat"))){
                prevZakat = Double.parseDouble(request.getParameter("previous_zakat"));
            }

            double prevOtherDeduction =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("previous_otherdeduction"))){
                prevOtherDeduction = Double.parseDouble(request.getParameter("previous_otherdeduction"));
            }

            boolean epf = false;
            if(!StringUtil.isNullOrEmpty(request.getParameter("current_epf"))){
                epf = true;
            }
            
            double lic =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("current_lic"))){
                lic = Double.parseDouble(request.getParameter("current_lic"));
            }

            double zakat =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("current_zakat"))){
                zakat = Double.parseDouble(request.getParameter("current_zakat"));
            }

            double bik =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("current_bik"))){
                bik = Double.parseDouble(request.getParameter("current_bik"));
            }
            
        	malaysianIncomeTaxDAO.saveUserInformation(userid, year, category, prevEarning, prevIncomeTax, prevEPF, prevLIC, prevZakat, epf, lic, zakat, bik, prevOtherDeduction, empStatus);
        } catch (Exception e) {
        	success = false;
            e.printStackTrace();
        }
        return success;
    }
    
    
    public JSONObject getUserInformation(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jsonObj = new JSONObject();
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	Date year = sdf.parse(request.getParameter("year"));
            String userid = request.getParameter("userID");
        	MalaysianUserIncomeTaxInfo taxInfo = malaysianIncomeTaxDAO.getUserInformation(userid, year);
        	if(taxInfo!=null){
        		jsonObj.put("id", taxInfo.getId());
        		jsonObj.put("userid", taxInfo.getUser().getUserID());
        		jsonObj.put("category", taxInfo.getCategoryid());
                jsonObj.put("empstatus", taxInfo.getEmpStatus());
                jsonObj.put("epf", taxInfo.isCurrentEPF());
                jsonObj.put("lic", taxInfo.getCurrentLICAndOther());
                jsonObj.put("zakat", taxInfo.getCurrentZakat());
                jsonObj.put("bik", taxInfo.getCurrentBenefitInKind());
                jsonObj.put("prevearning", taxInfo.getPreviousEmployerEarning());
                jsonObj.put("previncometax", taxInfo.getPreviousEmployerIncomeTax());
                jsonObj.put("prevepf", taxInfo.getPreviousEmployerEPF());
                jsonObj.put("prevlic", taxInfo.getPreviousEmployerLIC());
                jsonObj.put("prevzakat", taxInfo.getPreviousEmployerZakat());
                jsonObj.put("prevotherdeduction", taxInfo.getPreviousEmployerOtherDeduction());
        		jsonObj.put("submittedon", taxInfo.getSubmittedon());
        	}
        }catch(Exception ex) {
            ex.printStackTrace();
        } 
        return jsonObj;
    }
}
