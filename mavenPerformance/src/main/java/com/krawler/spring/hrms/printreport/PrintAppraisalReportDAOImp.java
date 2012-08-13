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
package com.krawler.spring.hrms.printreport;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.fontsettings.FontContext;
import com.krawler.common.fontsettings.FontFamilySelector;
import com.krawler.common.fontsettings.FontSetting;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONArray;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class PrintAppraisalReportDAOImp implements PrintAppraisalReportDAO, MessageSourceAware {
	
	private static final long serialVersionUID = -763555229410947890L;
	private static FontFamilySelector fontFamilySelector = FontSetting.getFontFamilySelector();
	
	private HibernateTemplate hibernateTemplate;
    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private MessageSource messageSource;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }
	
	public void createPrintPriviewFile(HttpServletRequest request, HttpServletResponse response, JSONArray obj ,Map<String, String> personalData){
		try {
			HashMap<String, Object> requestParams = new HashMap<String, Object>();
			boolean isQuestion = request.getParameter("question").equals("true")?true:false;
			boolean promotion = request.getParameter("promotion").equals("false")?false:true;
			CompanyPreferences companyPreferences = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, sessionHandlerImplObj.getCompanyid(request));
			Boolean isMyAppraisal = Boolean.parseBoolean(personalData.get("isMyAppraisal"));
			String space = "<span style='padding-left:20px'></span>";
			String ashtmlString = "<html> " +
            "<head><style type=\"text/css\">@media print {button#print {display: none;}}</style><title>"+messageSource.getMessage("hrms.Featurelist.appraisalform", null, RequestContextUtils.getLocale(request))+"</title></head>";        	
        	ashtmlString +="<body style = \"font-family: Tahoma, Verdana, Arial, Helvetica, sans-sarif;\"><center>";
        	ashtmlString +="<h2 align='center'>"+messageSource.getMessage("hrms.Featurelist.appraisalform", null, RequestContextUtils.getLocale(request))+"</h2>";
        	
        	ashtmlString +="<table cellspacing=0 border=0 cellpadding=2 width='100%'>";
        	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td>";
        	ashtmlString +="<table align='left' cellspacing=0 border=0 cellpadding=2 width='100%'>";
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=messageSource.getMessage("hrms.performance.appraisal.cycle.name", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=personalData.get("appcycname")+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
        	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=personalData.get("empname")+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
        	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=messageSource.getMessage("hrms.performance.appraisal.cycle.period", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=messageSource.getMessage("hrms.common.form", null, RequestContextUtils.getLocale(request))+": "+personalData.get("startdate")+messageSource.getMessage("hrms.common.to", null, RequestContextUtils.getLocale(request))+" : "+personalData.get("enddate")+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
        	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=messageSource.getMessage("hrms.Masters.Designation", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
        	ashtmlString +=personalData.get("desgname")+"</br></br>";
        	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
        	
        	requestParams.clear();
        	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("checklink", "overallcomments");
        	if(isMyAppraisal && hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
        		ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
        		ashtmlString +=messageSource.getMessage("hrms.performance.overall.self.comments", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
        		ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	ashtmlString +=personalData.get("selfcomment")+"</br></br>";
            	ashtmlString +="</td>";
            	ashtmlString +="</tr>";
        	}
        	
        	if(!isMyAppraisal){
        		requestParams.clear();
            	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("checklink", "promotion");
            	if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
	        		ashtmlString +="<tr>";
	            	ashtmlString +="<td style='white:space:pre-line'>";
	        		ashtmlString +=messageSource.getMessage("hrms.performance.performance.rating", null, RequestContextUtils.getLocale(request))+"*:  "+"</br></br>";
	        		ashtmlString +="</td>";
	            	ashtmlString +="<td style='white:space:pre-line'>";
	        		ashtmlString +=personalData.get("performance")+"</br></br>";
	        		ashtmlString +="</td>";
	            	ashtmlString +="</tr>";
            	}
        	
            	requestParams.clear();
            	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("checklink", "overallcomments");
            	if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
            		ashtmlString +="<tr>";
            		ashtmlString +="<td style='white:space:pre-line'>";
            		ashtmlString +=messageSource.getMessage("hrms.performance.overall.appraiser.comments", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
            		ashtmlString +="</td>";
            		ashtmlString +="<td style='white:space:pre-line'>";
            		ashtmlString +=personalData.get("empcomment")+"</br></br>";
            		ashtmlString +="</td>";
            		ashtmlString +="</tr>";
            	}
        	}
        	
        	requestParams.clear();
        	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("checklink", "promotion");
        	if(promotion && !isMyAppraisal && hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
        		ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
        		ashtmlString +=messageSource.getMessage("hrms.performance.promotion.recommendation", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
        		ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
        		ashtmlString +=(promotion?messageSource.getMessage("hrms.MasterData.Yes", null, RequestContextUtils.getLocale(request)):messageSource.getMessage("hrms.MasterData.No", null, RequestContextUtils.getLocale(request)))+"</br></br>";
        		ashtmlString +="</td>";
            	ashtmlString +="</tr>";
        		
        		ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
        		String newDept = personalData.get("newDept");
        		ashtmlString +=messageSource.getMessage("hrms.performance.new.department", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
        		ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	ashtmlString +=(newDept!=null?newDept:"")+"</br></br>";
            	ashtmlString +="</td>";
            	ashtmlString +="</tr>";
            	
            	ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	String newDesig = personalData.get("newDesig");
            	ashtmlString +=messageSource.getMessage("hrms.performance.new.designation", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
            	ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	ashtmlString +=(newDesig!=null?newDesig:"")+"</br></br>";
            	ashtmlString +="</td>";
            	ashtmlString +="</tr>";
            	
            	ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	String salInc = personalData.get("salInc");
            	ashtmlString +=messageSource.getMessage("hrms.performance.salary.increment", null, RequestContextUtils.getLocale(request))+"(%):  "+"</br></br>";
            	ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	ashtmlString +=(salInc!=null?salInc:"")+"</br></br>";
            	ashtmlString +="</td>";
            	ashtmlString +="</tr>";
        	}
        	ashtmlString +="</table>";
        	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
        	
        	requestParams.clear();
        	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("checklink", "competency");
            if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
            	if(!isQuestion){
            		try{
            			JSONArray jsonComp = obj.getJSONArray(0);
            			ashtmlString +="<tr>";
            			ashtmlString +="<td>";
            			ashtmlString+="</br><h4 align='left'>"+messageSource.getMessage("hrms.performance.competency.evaluation", null, RequestContextUtils.getLocale(request))+"</h4>";
            			ashtmlString+="<table cellspacing=0 border=1 cellpadding=2 width='100%'>";
            			if(!isMyAppraisal){	
            				if(companyPreferences.isWeightage()){
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.competency", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.competency.description", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.weightage", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}else{
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.competency", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.competency.description", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}
            				for(int i=0; i<jsonComp.length(); i++){
            					ashtmlString+="<tr>";
            					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("cmptname")?(jsonComp.getJSONObject(i).getString("cmptname").equals("")?space:jsonComp.getJSONObject(i).getString("cmptname")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("cmptdesc")?(jsonComp.getJSONObject(i).getString("cmptdesc").equals("")?space:jsonComp.getJSONObject(i).getString("cmptdesc")):space)+"</td>";
            					if(companyPreferences.isWeightage()){
            						ashtmlString+="<td style='white:space:pre-line'><center>"+(jsonComp.getJSONObject(i).has("cmptwt")?(jsonComp.getJSONObject(i).getString("cmptwt").equals("")?space:jsonComp.getJSONObject(i).getString("cmptwt")):space)+"</center></td>";
            					}
            					ashtmlString+="<td style='white:space:pre-line'><center>"+(jsonComp.getJSONObject(i).has("manrat")?((jsonComp.getJSONObject(i).getString("manrat").equals("")||jsonComp.getJSONObject(i).getString("manrat").equals("0"))?space:jsonComp.getJSONObject(i).getString("manrat")):space)+"</center></td>";
            					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("mancompcomment")?(jsonComp.getJSONObject(i).getString("mancompcomment").equals("")?space:jsonComp.getJSONObject(i).getString("mancompcomment")):space)+"</td>";
            					ashtmlString+="</tr>";
            				}
            			}else{
            				if(companyPreferences.isWeightage()){
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.competency", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.competency.description", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.weightage", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}else{
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.competency", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.competency.description", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}
            				for(int i=0; i<jsonComp.length(); i++){
            					ashtmlString+="<tr>";
            					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("cmptname")?(jsonComp.getJSONObject(i).getString("cmptname").equals("")?space:jsonComp.getJSONObject(i).getString("cmptname")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("cmptdesc")?(jsonComp.getJSONObject(i).getString("cmptdesc").equals("")?space:jsonComp.getJSONObject(i).getString("cmptdesc")):space)+"</td>";
            					if(companyPreferences.isWeightage()){
            						ashtmlString+="<td style='white:space:pre-line'><center>"+(jsonComp.getJSONObject(i).has("cmptwt")?(jsonComp.getJSONObject(i).getString("cmptwt").equals("")?space:jsonComp.getJSONObject(i).getString("cmptwt")):space)+"</center></td>";
            					}
            					ashtmlString+="<td style='white:space:pre-line'><center>"+(jsonComp.getJSONObject(i).has("emprat")?((jsonComp.getJSONObject(i).getString("emprat").equals("")||jsonComp.getJSONObject(i).getString("emprat").equals("0"))?space:jsonComp.getJSONObject(i).getString("emprat")):space)+"</center></td>";
            					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("empcompcomment")?(jsonComp.getJSONObject(i).getString("empcompcomment").equals("")?space:jsonComp.getJSONObject(i).getString("empcompcomment")):space)+"</td>";
            					ashtmlString+="</tr>";
            				}
            			}
            			ashtmlString+="</table>";
            			ashtmlString +="</td>";
            			ashtmlString +="</tr>";
            		}catch (JSONException e) {
            			ashtmlString+="</table></div>";
            			ashtmlString +="</td>";
            			ashtmlString +="</tr>";
            			e.printStackTrace();
            		}
            	}else{
            		try{
            			JSONArray jsonQues = obj.getJSONArray(1);
            			JSONArray jsonAnsTemp = obj.getJSONArray(2);
            			ashtmlString +="<tr>";
            			ashtmlString +="<td>";
            			ashtmlString+="</br><h4 align='left'>"+messageSource.getMessage("hrms.performance.qualitative.appraisal", null, RequestContextUtils.getLocale(request))+"</h4>";
            			JSONArray jsonAns = new JSONArray();
            			try{
            				for(int i=0; i<jsonQues.length(); i++){
            					String qid = (String) jsonQues.getJSONObject(i).get("qdescription");
            					for(int j=0; j<jsonAnsTemp.length(); j++){
            						try{
            							if(qid.equals((String) jsonAnsTemp.getJSONObject(j).get("question"))){
            								jsonAns.put(jsonAnsTemp.getJSONObject(j));
            								break;	
            							}
            						}catch (JSONException e) {
            							jsonAns.put("");
            						}
            					}
            				}
            			}catch (JSONException e) {
            				e.printStackTrace();
            			}
			
            			for(int i=0; i<jsonQues.length(); i++){
            				ashtmlString+="<table style='page-break-after:auto' cellspacing=0 border=1 cellpadding=2 width='100%'>";
            				ashtmlString+="<tr>";
            				ashtmlString+="<td align='center' width='5%'>Que.</td>";
            				ashtmlString+="<td style='white:space:pre-line'>"+(jsonQues.getJSONObject(i).has("qdesc")?jsonQues.getJSONObject(i).getString("qdesc"):space)+"</td>";
            				ashtmlString+="</tr>";
            				int cnt = 0;
            				try{
            					cnt= jsonQues.getJSONObject(i).has("qans")?Integer.parseInt(jsonQues.getJSONObject(i).getString("qans")):0;
            				}catch (NumberFormatException e) {
            					e.printStackTrace();	
            				}
            				if(jsonAns.length()>0){
            					JSONArray arr = jsonAns.getJSONObject(i).getJSONArray("answer");
            					for(int j=0; j<arr.length(); j++){
            						ashtmlString+="<tr><td align='center' width='5%'>"+(j+1)+"</td>";
            						ashtmlString+="<td style='white:space:pre-line' height='100px'>";
            						ashtmlString+=(arr.getJSONObject(j).getString(""+j+"").equals("")?space:arr.getJSONObject(j).getString(""+j+""));	
            						ashtmlString+="</td></tr>";
            					}
            				}else{
            					for(int j=0; j<cnt; j++){
            						ashtmlString+="<tr><td align='center' width='5%'>"+(j+1)+"</td>";
            						ashtmlString+="<td style='white:space:pre-line' height='100px'>";
            						ashtmlString+=(space);	
            						ashtmlString+="</td></tr>";
            					}
            				}
            				ashtmlString+="</br>";
            				ashtmlString+="</table>";
            			}
            			ashtmlString +="</td>";
        				ashtmlString +="</tr>";
            		}catch (JSONException e) {
            			ashtmlString+="</table>";
            			ashtmlString +="</td>";
            			ashtmlString +="</tr>";
            			e.printStackTrace();
            		}
            	}
            }
        	
            requestParams.clear();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("checklink", "goal");
            if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
            	try{
            		JSONArray jsonGoals = obj.getJSONArray(3);
            		if(jsonGoals.length()>0){
            			ashtmlString +="<tr>";
            			ashtmlString +="<td>";
            			ashtmlString+="</br><h4 align='left'>"+messageSource.getMessage("hrms.performance.goal.evaluation", null, RequestContextUtils.getLocale(request))+"</h4>"; 
            			ashtmlString+="<table cellspacing=0 border=1 cellpadding=2 width='100%'>";
            			if(!isMyAppraisal){
            				if(companyPreferences.isWeightage()){
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.percent.completed", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.goal.weightage", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}else{
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.percent.completed", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}
            				for(int i=0 ;i<jsonGoals.length();i++){
            					ashtmlString+="<tr>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("gname")?(jsonGoals.getJSONObject(i).getString("gname").equals("")?space:jsonGoals.getJSONObject(i).getString("gname")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("gcomplete")?(jsonGoals.getJSONObject(i).getString("gcomplete").equals("")?space:jsonGoals.getJSONObject(i).getString("gcomplete")):space)+"</td>";
            					if(companyPreferences.isWeightage())
            						ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("gwth")?(jsonGoals.getJSONObject(i).getString("gwth").equals("")?space:jsonGoals.getJSONObject(i).getString("gwth")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("gmanrat")?((jsonGoals.getJSONObject(i).getString("gmanrat").equals("")||jsonGoals.getJSONObject(i).getString("gmanrat").equals("0"))?space:jsonGoals.getJSONObject(i).getString("gmanrat")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("mangoalcomment")?(jsonGoals.getJSONObject(i).getString("mangoalcomment").equals("")?space:jsonGoals.getJSONObject(i).getString("mangoalcomment")):space)+"</td>";
            					ashtmlString+="</tr>";
            				}
            			}else{
            				if(companyPreferences.isWeightage()){
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.percent.completed", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.goal.weightage", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.common.assigned.by", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}else{
            					ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.percent.completed", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.common.assigned.by", null, RequestContextUtils.getLocale(request))+"</th></tr>";
            				}
            				for(int i=0 ;i<jsonGoals.length();i++){
            					ashtmlString+="<tr>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("gname")?(jsonGoals.getJSONObject(i).getString("gname").equals("")?space:jsonGoals.getJSONObject(i).getString("gname")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("gcomplete")?(jsonGoals.getJSONObject(i).getString("gcomplete").equals("")?space:jsonGoals.getJSONObject(i).getString("gcomplete")):space)+"</td>";
            					if(companyPreferences.isWeightage())
            						ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("gwth")?(jsonGoals.getJSONObject(i).getString("gwth").equals("")?space:jsonGoals.getJSONObject(i).getString("gwth")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line'  align='center'>"+(jsonGoals.getJSONObject(i).has("gemprat")?((jsonGoals.getJSONObject(i).getString("gemprat").equals("")||jsonGoals.getJSONObject(i).getString("gemprat").equals("0"))?space:jsonGoals.getJSONObject(i).getString("gemprat")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("empgoalcomment")?(jsonGoals.getJSONObject(i).getString("empgoalcomment").equals("")?space:jsonGoals.getJSONObject(i).getString("empgoalcomment")):space)+"</td>";
            					ashtmlString+="<td style='white:space:pre-line' align='center'>"+(jsonGoals.getJSONObject(i).has("assignedby")?(jsonGoals.getJSONObject(i).getString("assignedby").equals("")?space:jsonGoals.getJSONObject(i).getString("assignedby")):space)+"</td>";
            					ashtmlString+="</tr>";
            				}
            			}
            			ashtmlString+="</table>";
            			ashtmlString +="</td>";
            			ashtmlString +="</tr>";
            		}
            	}catch (JSONException e) {
            		ashtmlString+="</table>";
            		ashtmlString +="</td>";
        			ashtmlString +="</tr>";
            		e.printStackTrace();
            	}
            }
            ashtmlString +="</table>";
            ashtmlString +="</center>";
            ashtmlString +="<div style='float: left; padding-top: 3px; padding-right: 5px;'>" + "<button id = 'print' title='Print' onclick='window.print();' style='color: rgb(8, 55, 114);' href='#'>"+messageSource.getMessage("hrms.common.print", null, RequestContextUtils.getLocale(request))+"</button>" +"</div>" ;
			ashtmlString +="</body>" +
        	"</html>";
        	
        	response.setContentType("text/html");
        	response.setContentLength(ashtmlString.length());
        	response.getOutputStream().write(ashtmlString.getBytes());
        	response.getOutputStream().flush();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
	}
	
	public class EndPage extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                PdfPTable footer = new PdfPTable(1);
                footer.setWidthPercentage(100);
                footer.setSpacingBefore(20);
                String FootPager = String.valueOf(document.getPageNumber());//current page no
                PdfPCell footerPageNocell = new PdfPCell(new Phrase(fontFamilySelector.process(FootPager, FontContext.SMALL_BOLD_HELVETICA)));
                footerPageNocell.setBorder(0);
                footerPageNocell.setPaddingBottom(5);
                footerPageNocell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                footer.addCell(footerPageNocell);
                footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() - 5, writer.getDirectContent());

            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
	
	public void createPDFPriview(HttpServletRequest request, HttpServletResponse response, JSONArray obj ,Map<String, String> personalData){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate(), 25, 25, 25, 25);

		PdfWriter writer=null;
		try {
				HashMap<String, Object> requestParams = new HashMap<String, Object>();
			
				boolean isQuestion = request.getParameter("question").equals("true")?true:false;
				boolean promotion = request.getParameter("promotion").equals("false")?false:true;
				Boolean isMyAppraisal = Boolean.parseBoolean(personalData.get("isMyAppraisal"));
				CompanyPreferences companyPreferences = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, sessionHandlerImplObj.getCompanyid(request));
				writer=PdfWriter.getInstance(document, baos);
				writer.setPageEvent(new EndPage());
				document.open();
				java.awt.Color tColor = new Color(9, 9, 9);
				//fontSmallBold.setColor(tColor);

				//heading
				PdfPTable headTable = new PdfPTable(1);
				headTable.setTotalWidth(90);
				headTable.setWidthPercentage(100);
				headTable.setSpacingBefore(20);
				PdfPCell headcell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.AppraisalForm", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_HELVETICA)));
                headcell.setBackgroundColor(new Color(0xEEEEEE));
                headcell.setPadding(5);
                headTable.addCell(headcell);
                document.add(headTable);
                //heading
               
                //Details
                int flag = 0;
                String appDetailsParam[] = { messageSource.getMessage("hrms.performance.appraisal.cycle.name", null, RequestContextUtils.getLocale(request))+":  ",
                		                     messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request))+":  ",
                		                     messageSource.getMessage("hrms.performance.appraisal.cycle.period", null, RequestContextUtils.getLocale(request))+":  ",
                		                     messageSource.getMessage("hrms.Masters.Designation", null, RequestContextUtils.getLocale(request))+":  ",
                		                     messageSource.getMessage("hrms.performance.performance.rating", null, RequestContextUtils.getLocale(request))+"*:  "};
                String appDetailsValue[] = {personalData.get("appcycname"),personalData.get("empname"), messageSource.getMessage("hrms.common.form", null, RequestContextUtils.getLocale(request))+": "+personalData.get("startdate")+" "+messageSource.getMessage("hrms.common.to", null, RequestContextUtils.getLocale(request))+": "+personalData.get("enddate"),personalData.get("desgname"),personalData.get("performance")};
                
                requestParams.clear();
            	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("checklink", "promotion");
            	if(!hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
            		flag = 1;
            	}
                
               	PdfPTable appDetailsTable = new PdfPTable(2);
               	appDetailsTable.setWidthPercentage(100);
               	appDetailsTable.setWidths(new float[]{55,75});
               	if(isMyAppraisal){
               		flag = 1;
               	}
               	
               	for(int i=0;i<(appDetailsParam.length-flag);i++){
               		PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(appDetailsParam[i], FontContext.MEDIUM_BOLD_HELVETICA)));
               		pcell.setBorder(0);
               		pcell.setPaddingTop(10);
               		pcell.setPaddingLeft(15);
               		pcell.setPaddingBottom(4);
               		pcell.setBorderColor(new Color(0xF2F2F2));
               		pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
               		pcell.setVerticalAlignment(Element.ALIGN_LEFT);
               		appDetailsTable.addCell(pcell);
                
               		PdfPCell pcell1 = new PdfPCell(new Paragraph(fontFamilySelector.process(appDetailsValue[i], FontContext.SMALL_NORMAL_HELVETICA)));
               		pcell1.setBorder(0);	
               		pcell1.setPaddingTop(10);
               		pcell1.setPaddingLeft(15);
               		pcell1.setPaddingBottom(4);
               		pcell1.setBorderColor(new Color(0xF2F2F2));
               		pcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
               		pcell1.setVerticalAlignment(Element.ALIGN_LEFT);
               		appDetailsTable.addCell(pcell1);
               	}
               	
               	requestParams.clear();
            	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("checklink", "overallcomments");
            	if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
               		PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process((isMyAppraisal?messageSource.getMessage("hrms.performance.self.overall.comment", null, RequestContextUtils.getLocale(request))+":  ":messageSource.getMessage("hrms.performance.overall.appraiser.comments", null, RequestContextUtils.getLocale(request))+":  "), FontContext.MEDIUM_BOLD_HELVETICA)));
               		pcell.setBorder(0);
               		pcell.setPaddingTop(10);
               		pcell.setPaddingLeft(15);
               		pcell.setPaddingBottom(4);
               		pcell.setBorderColor(new Color(0xF2F2F2));
               		pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
               		pcell.setVerticalAlignment(Element.ALIGN_LEFT);
               		appDetailsTable.addCell(pcell);
                
               		PdfPCell pcell1 = new PdfPCell(new Paragraph(fontFamilySelector.process((isMyAppraisal?personalData.get("selfcomment"):personalData.get("empcomment")), FontContext.SMALL_NORMAL_HELVETICA)));
               		pcell1.setBorder(0);	
               		pcell1.setPaddingTop(10);
               		pcell1.setPaddingLeft(15);
               		pcell1.setPaddingBottom(4);
               		pcell1.setBorderColor(new Color(0xF2F2F2));
               		pcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
               		pcell1.setVerticalAlignment(Element.ALIGN_LEFT);
               		appDetailsTable.addCell(pcell1);
               	}
               	
            	requestParams.clear();
            	requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("checklink", "promotion");
                if(!isMyAppraisal && hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
                	PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.performance.promotion.recommendation", null, RequestContextUtils.getLocale(request))+":  ", FontContext.MEDIUM_BOLD_HELVETICA)));
               		pcell.setBorder(0);
               		pcell.setPaddingTop(10);
               		pcell.setPaddingLeft(15);
               		pcell.setPaddingBottom(4);
               		pcell.setBorderColor(new Color(0xF2F2F2));
               		pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
               		pcell.setVerticalAlignment(Element.ALIGN_LEFT);
               		appDetailsTable.addCell(pcell);
                
               		PdfPCell pcell1 = new PdfPCell(new Paragraph(fontFamilySelector.process((promotion?messageSource.getMessage("hrms.MasterData.Yes", null, RequestContextUtils.getLocale(request)):messageSource.getMessage("hrms.MasterData.No", null, RequestContextUtils.getLocale(request))), FontContext.SMALL_NORMAL_HELVETICA)));
               		pcell1.setBorder(0);	
               		pcell1.setPaddingTop(10);
               		pcell1.setPaddingLeft(15);
               		pcell1.setPaddingBottom(4);
               		pcell1.setBorderColor(new Color(0xF2F2F2));
               		pcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
               		pcell1.setVerticalAlignment(Element.ALIGN_LEFT);
               		appDetailsTable.addCell(pcell1);
                }
                
            	if(promotion && !isMyAppraisal && hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
               		String [] appDetaisPromotionParam = { messageSource.getMessage("hrms.performance.new.department", null, RequestContextUtils.getLocale(request))+":  ",
               				                              messageSource.getMessage("hrms.performance.new.designation", null, RequestContextUtils.getLocale(request))+":  ",
               				                              messageSource.getMessage("hrms.performance.salary.increment", null, RequestContextUtils.getLocale(request))+"(%):  "};
            		String [] appDetaisPromotionValue = {personalData.get("newDept")!=null?personalData.get("newDept"):"",personalData.get("newDesig")!=null?personalData.get("newDesig"):"",personalData.get("salInc")!=null?personalData.get("salInc"):""};
            		for(int i=0;i<appDetaisPromotionParam.length;i++){
                   		PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(appDetaisPromotionParam[i], FontContext.MEDIUM_BOLD_HELVETICA)));
                   		pcell.setBorder(0);
                   		pcell.setPaddingTop(10);
                   		pcell.setPaddingLeft(15);
                   		pcell.setPaddingBottom(4);
                   		pcell.setBorderColor(new Color(0xF2F2F2));
                   		pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                   		pcell.setVerticalAlignment(Element.ALIGN_LEFT);
                   		appDetailsTable.addCell(pcell);
                    
                   		PdfPCell pcell1 = new PdfPCell(new Paragraph(fontFamilySelector.process(appDetaisPromotionValue[i], FontContext.SMALL_NORMAL_HELVETICA)));
                   		pcell1.setBorder(0);	
                   		pcell1.setPaddingTop(10);
                   		pcell1.setPaddingLeft(15);
                   		pcell1.setPaddingBottom(4);
                   		pcell1.setBorderColor(new Color(0xF2F2F2));
                   		pcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                   		pcell1.setVerticalAlignment(Element.ALIGN_LEFT);
                   		appDetailsTable.addCell(pcell1);
                   	}
               	}
                document.add(appDetailsTable);
                //Details
                
                //Comp
                requestParams.clear();
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("checklink", "competency");
                if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
                	try{
                		if(!isQuestion){
                			JSONArray jsonComp = obj.getJSONArray(0);
                			if(!isMyAppraisal){
                				String[] compParam = { messageSource.getMessage("hrms.performance.competency", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.competency.description", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.weightage", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))};
                				String[] colParam = {"cmptname","cmptdesc","cmptwt","manrat","mancompcomment"};
                				PdfPTable questionTable = null;
                				if(companyPreferences.isWeightage()){
                					questionTable = new PdfPTable(5);
                					questionTable.setWidths(new float[]{50,60,30,30,42});
                				}else{
                					questionTable = new PdfPTable(4);
                					questionTable.setWidths(new float[]{50,60,30,42});
                				}
                				questionTable.setWidthPercentage(100);
                				questionTable.setSpacingBefore(20);
                				questionTable.setHeaderRows(1);
                				for(int i=0;i<compParam.length;i++){
                					if(!(!companyPreferences.isWeightage() && i==2)){
                						PdfPCell pgcell = new PdfPCell(new Paragraph(fontFamilySelector.process(compParam[i], FontContext.MEDIUM_BOLD_HELVETICA)));
                						pgcell.setBorder(0);
                						pgcell.setBorder(PdfPCell.BOX);
                						pgcell.setPadding(4);
                						pgcell.setBorderColor(Color.GRAY);
                						pgcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                						questionTable.addCell(pgcell);
                					}
                				}
                            
                				for(int i=0 ;i<jsonComp.length();i++){
                					for (int j = 0; j < colParam.length; j++) {
                						if(!(!companyPreferences.isWeightage() && j==2)){
                						PdfPCell pcell = null;
                						if(j!=3)
                							pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonComp.getJSONObject(i).has(colParam[j])?jsonComp.getJSONObject(i).getString(colParam[j]):"", FontContext.SMALL_NORMAL_HELVETICA)));
                						else{
                							pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonComp.getJSONObject(i).has(colParam[j])?(jsonComp.getJSONObject(i).getString(colParam[j]).equals("0")?"":jsonComp.getJSONObject(i).getString(colParam[j])):"", FontContext.SMALL_NORMAL_HELVETICA)));	
                						}
                						pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                						pcell.setBorderColor(Color.GRAY);
                						pcell.setPadding(4);
                						pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                						pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                						questionTable.addCell(pcell);
                						}
                					}
                				}
                				document.add(questionTable);
                			}else{
                				String[] compParam = { messageSource.getMessage("hrms.performance.competency", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.competency.description", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.weightage", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request)),
                						               messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request))};
                				String[] colParam = {"cmptname","cmptdesc","cmptwt","emprat","empcompcomment"};
                				PdfPTable questionTable = null;
                				if(companyPreferences.isWeightage()){
                					questionTable = new PdfPTable(5);
                					questionTable.setWidths(new float[]{50,60,30,30,42});
                				}else{
                					questionTable = new PdfPTable(4);
                					questionTable.setWidths(new float[]{50,60,30,42});
                				}
                				questionTable.setWidthPercentage(100);
                				questionTable.setSpacingBefore(20);
                				questionTable.setHeaderRows(1);
                				for(int i=0;i<compParam.length;i++){
                					if(!(!companyPreferences.isWeightage() && i==2)){
                                    PdfPCell pgcell = new PdfPCell(new Paragraph(fontFamilySelector.process(compParam[i], FontContext.MEDIUM_BOLD_HELVETICA)));
                                    pgcell.setBorder(0);
                                    pgcell.setBorder(PdfPCell.BOX);
                                    pgcell.setPadding(4);
                                    pgcell.setBorderColor(Color.GRAY);
                                    pgcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    questionTable.addCell(pgcell);
                					}
                				}
                            
                				for(int i=0 ;i<jsonComp.length();i++){
                					for (int j = 0; j < colParam.length; j++) {
                						if(!(!companyPreferences.isWeightage() && j==2)){
                						PdfPCell pcell;
                						if(j!=3)
                							pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonComp.getJSONObject(i).has(colParam[j])?jsonComp.getJSONObject(i).getString(colParam[j]):"", FontContext.SMALL_NORMAL_HELVETICA)));
                						else
                							pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonComp.getJSONObject(i).has(colParam[j])?(jsonComp.getJSONObject(i).getString(colParam[j]).equals("0")?"":jsonComp.getJSONObject(i).getString(colParam[j])):"", FontContext.SMALL_NORMAL_HELVETICA)));
                						pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                						pcell.setBorderColor(Color.GRAY);
                						pcell.setPadding(4);
                						pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                						pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                						questionTable.addCell(pcell);
                						}
                					}
                				}
                				document.add(questionTable);
                			}
                		}else{//Questions
                			try{
                				JSONArray jsonQues = obj.getJSONArray(1);
                				JSONArray jsonAnsTemp = obj.getJSONArray(2);
                				JSONArray jsonAns = new JSONArray();
                			
                				PdfPTable quesMainTable = new PdfPTable(1);
                				quesMainTable.setTotalWidth(90);
                				quesMainTable.setWidthPercentage(100);
                				quesMainTable.setSpacingBefore(20);
                				quesMainTable.setSkipFirstHeader(true);
                				PdfPCell quesHeadcell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.performance.qualitative.appraisal", null, RequestContextUtils.getLocale(request)), FontContext.MEDIUM_BOLD_HELVETICA)));
                				quesHeadcell.setPadding(5);
                				quesMainTable.addCell(quesHeadcell);
                				document.add(quesMainTable);
                			
                				PdfPTable[] quesTable = new PdfPTable[jsonQues.length()];
                			
                				try{
                					for(int i=0; i<jsonQues.length(); i++){
                						String qid = (String) jsonQues.getJSONObject(i).get("qdescription");
                						for(int j=0; j<jsonAnsTemp.length(); j++){
                							try{
                								if(qid.equals((String) jsonAnsTemp.getJSONObject(j).get("question"))){
                									jsonAns.put(jsonAnsTemp.getJSONObject(j));
                									break;	
                								}
                							}catch (JSONException e) {
                								jsonAns.put("");
                							}
                						}
                					}
                				}catch(JSONException e){
                					e.printStackTrace();
                				}
                			
                				for(int i=0; i<jsonQues.length(); i++){
                					quesTable[i] = new PdfPTable(2);
                					quesTable[i].setWidthPercentage(90);
                					quesTable[i].setWidths(new float[]{10,80});
                					quesTable[i].setSpacingBefore(20);
                					quesTable[i].setHeaderRows(1);
                				
                					PdfPCell pgcell1 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.performance.que", null, RequestContextUtils.getLocale(request))+".", FontContext.SMALL_NORMAL_HELVETICA)));
                					pgcell1.setBorder(0);
                					pgcell1.setBorder(PdfPCell.BOX);
                					pgcell1.setPadding(4);
                					pgcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                					quesTable[i].addCell(pgcell1);
                				
                					PdfPCell pgcell2 = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonQues.getJSONObject(i).has("qdesc")?jsonQues.getJSONObject(i).getString("qdesc"):"", FontContext.SMALL_NORMAL_HELVETICA)));
                					pgcell2.setBorder(0);
                					pgcell2.setBorder(PdfPCell.BOX);
                					pgcell2.setPadding(4);
                					pgcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                					quesTable[i].addCell(pgcell2);
                    			
                					int cnt = 0;
                					try{
                						cnt= jsonQues.getJSONObject(i).has("qans")?Integer.parseInt(jsonQues.getJSONObject(i).getString("qans")):0;
                					}catch (NumberFormatException e) {
                						e.printStackTrace();	
                					}
                					if(jsonAns.length()>0){
                						JSONArray arr = jsonAns.getJSONObject(i).getJSONArray("answer");
                						for(int j=0; j<arr.length() ;j++){
                							PdfPCell pcell1 = new PdfPCell(new Paragraph(fontFamilySelector.process((j+1)+"", FontContext.SMALL_NORMAL_HELVETICA)));
                							pcell1.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                							pcell1.setBorderColor(Color.GRAY);
                							pcell1.setPadding(4);
                							pcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                							pcell1.setVerticalAlignment(Element.ALIGN_CENTER);
                							quesTable[i].addCell(pcell1);
                						
                							PdfPCell pcell2 = new PdfPCell(new Paragraph(fontFamilySelector.process(arr.getJSONObject(j).getString(""+j+""), FontContext.SMALL_NORMAL_HELVETICA)));
                							pcell2.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                							pcell2.setBorderColor(Color.GRAY);
                							pcell2.setPadding(4);
                							pcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                							pcell2.setVerticalAlignment(Element.ALIGN_CENTER);
                							quesTable[i].addCell(pcell2);
                						}
                					}else{
                						for(int j=0; j<cnt ;j++){
                							PdfPCell pcell1 = new PdfPCell(new Paragraph(fontFamilySelector.process((j+1)+"", FontContext.SMALL_NORMAL_HELVETICA)));
                							pcell1.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                							pcell1.setBorderColor(Color.GRAY);
                							pcell1.setPadding(4);
                							pcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                							pcell1.setVerticalAlignment(Element.ALIGN_CENTER);
                							quesTable[i].addCell(pcell1);
                						
                							PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process("", FontContext.SMALL_NORMAL_HELVETICA)));
                							pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                							pcell.setBorderColor(Color.GRAY);
                							pcell.setPadding(4);
                							pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                							pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                							quesTable[i].addCell(pcell);
                						}
                					}
                					PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(" ", FontContext.SMALL_NORMAL_HELVETICA)));
                					pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                					pcell.setBorderColor(Color.GRAY);
                					pcell.setPadding(4);
                					pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                					pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                					//questionTable.addCell(pcell);
                                
                					document.add(quesTable[i]);
                				}	
                				//document.add(questionTable);
                			}catch(JSONException e){	
                				e.printStackTrace();
                			}
                		}
                	}catch(JSONException e){
                		e.printStackTrace();
                	}
                }
                //Comp
                
                //Goals
                requestParams.clear();
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("checklink", "goal");
                if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
                	try{
                		JSONArray jsonGoals = obj.getJSONArray(3);
                		if(!isMyAppraisal){
                			String goalParam[] = { messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.percent.completed", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.goal.weightage", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))};
                			String colHeader[] = {"gname","gcomplete","gwth","gmanrat","mangoalcomment"};
                			PdfPTable goalTable = null;
            				if(companyPreferences.isWeightage()){
            					goalTable = new PdfPTable(5);
            					goalTable.setWidths(new float[]{50,60,30,30,42});
            				}else{
            					goalTable = new PdfPTable(4);
            					goalTable.setWidths(new float[]{50,60,30,42});
            				}
                			goalTable.setWidthPercentage(100);
                			goalTable.setSpacingBefore(20);
                			goalTable.setHeaderRows(1);
                			for(int i=0;i<goalParam.length;i++){
                				if(!(!companyPreferences.isWeightage() && i==2)){
                                PdfPCell pgcell = new PdfPCell(new Paragraph(fontFamilySelector.process(goalParam[i], FontContext.MEDIUM_BOLD_HELVETICA)));
                                pgcell.setBorder(0);
                                pgcell.setBorder(PdfPCell.BOX);
                                pgcell.setPadding(4);
                                pgcell.setBorderColor(Color.GRAY);
                                pgcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                goalTable.addCell(pgcell);
                				}
                			}
                        
                			for(int i=0 ;i<jsonGoals.length();i++){
                				for (int j = 0; j < colHeader.length; j++) {
                					if(!(!companyPreferences.isWeightage() && j==2)){
                					PdfPCell pcell ;
                					if(j!=3)
                						pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonGoals.getJSONObject(i).has(colHeader[j])?jsonGoals.getJSONObject(i).getString(colHeader[j]):"", FontContext.SMALL_NORMAL_HELVETICA)));
                					else
                						pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonGoals.getJSONObject(i).has(colHeader[j])?(jsonGoals.getJSONObject(i).getString(colHeader[j]).equals("0")?"":jsonGoals.getJSONObject(i).getString(colHeader[j])):"", FontContext.SMALL_NORMAL_HELVETICA)));
                					pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                					pcell.setBorderColor(Color.GRAY);
                					pcell.setPadding(4);
                					pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                					pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                					goalTable.addCell(pcell);
                					}
                				}
                			}
                			document.add(goalTable);
                		}else{
                			String goalParam[] = { messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.percent.completed", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.goal.weightage", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request)),
                					               messageSource.getMessage("hrms.common.assigned.by", null, RequestContextUtils.getLocale(request))};
                			String colHeader[] = {"gname","gcomplete","gwth","gemprat","empgoalcomment","assignedby"};
                			PdfPTable goalTable = null;
            				if(companyPreferences.isWeightage()){
            					goalTable = new PdfPTable(6);
            					goalTable.setWidths(new float[]{50,60,30,50,42,60});
            				}else{
            					goalTable = new PdfPTable(5);
            					goalTable.setWidths(new float[]{50,60,50,42,60});
            				}
                			goalTable.setWidthPercentage(100);
                			goalTable.setSpacingBefore(20);
                			goalTable.setHeaderRows(1);
                			for(int i=0;i<goalParam.length;i++){
                				if(!(!companyPreferences.isWeightage() && i==2)){
                                PdfPCell pgcell = new PdfPCell(new Paragraph(fontFamilySelector.process(goalParam[i], FontContext.MEDIUM_BOLD_HELVETICA)));
                                pgcell.setBorder(0);
                                pgcell.setBorder(PdfPCell.BOX);
                                pgcell.setPadding(4);
                                pgcell.setBorderColor(Color.GRAY);
                                pgcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                goalTable.addCell(pgcell);
                				}
                			}
                        
                			for(int i=0 ;i<jsonGoals.length();i++){
                				for (int j = 0; j < colHeader.length; j++) {
                					if(!(!companyPreferences.isWeightage() && j==2)){
                					PdfPCell pcell;
                					if(j!=3)
                						pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonGoals.getJSONObject(i).has(colHeader[j])?jsonGoals.getJSONObject(i).getString(colHeader[j]):"", FontContext.SMALL_NORMAL_HELVETICA)));
                					else
                						pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(jsonGoals.getJSONObject(i).has(colHeader[j])?(jsonGoals.getJSONObject(i).getString(colHeader[j]).equals("0")?"":jsonGoals.getJSONObject(i).getString(colHeader[j])):"", FontContext.SMALL_NORMAL_HELVETICA)));
                					pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                					pcell.setBorderColor(Color.GRAY);
                					pcell.setPadding(4);
                					pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                					pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                					goalTable.addCell(pcell);
                					}
                				}
                			}
                			document.add(goalTable);
                		}
                	}catch(JSONException e){
                		e.printStackTrace();
                	}
                }
                //Goals
                
                document.newPage();
                document.close();
                response.setHeader("Content-Disposition", "attachment; filename=\""+ "App_Form" +".pdf\"");
                response.setContentType("application/octet-stream");
                response.setContentLength(baos.size());
                response.getOutputStream().write(baos.toByteArray());
		} catch (DocumentException e) {	
			e.printStackTrace();
		}catch (IOException e) {	
			e.printStackTrace();
		}catch (Exception e) {	
			e.printStackTrace();
		}finally{
               writer.close();
        }
	}
	
	@Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
