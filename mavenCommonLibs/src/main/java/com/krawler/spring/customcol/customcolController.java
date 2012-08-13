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
package com.krawler.spring.customcol;

import com.krawler.common.admin.ConfigData;
import com.krawler.common.admin.ConfigType;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;


public class customcolController extends MultiActionController implements MessageSourceAware {
    private String successView;
    private HibernateTransactionManager txnManager;
    private customcolDAO customcolDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    
    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public void setCustomcolDAO(customcolDAO customcolDAOObj) {
        this.customcolDAOObj = customcolDAOObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
		return successView;
	}

    public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public ModelAndView getConfigType(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("company.companyID"));
            requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request)));
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("searchcol", new String[]{"name"});
            result = customcolDAOObj.getConfigType(requestParams);
            List lst = result.getEntityList();
//            String hql = "from ConfigType where company.companyID = ? ";
//            params.add(AuthHandler.getCompanyid(request));
//            if (!StringUtil.isNullOrEmpty(ss)) {
//                StringUtil.insertParamSearchString(params, ss, 1);
//                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"name"});
//                hql += searchQuery;
//            }
//            List lst = HibernateUtil.executeQuery(session, hql, params.toArray());
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("configid", contyp.getConfigid());
//                tmpObj.put("allownull", contyp.getAllowblank());
                tmpObj.put("configtype", contyp.getConfigtype());
                tmpObj.put("fieldname", contyp.getName());
                tmpObj.put("formtype", contyp.getFormtype());
                tmpObj.put("blockemployees", contyp.getBlockemployees());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }

    public ModelAndView addConfigType(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        String companyid = "";
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean max = false;
        boolean isRecordExist = false;
        try {
            companyid = sessionHandlerImplObj.getCompanyid(request);
            ConfigType contyp = null;
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            if (!request.getParameter("configid").equals("clone")) {
                int colcount = 0;
                if (request.getParameter("configid").equals("config")) {
//                    String hql ="from ConfigType where company.companyID=?";
//                    List lst = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getCompanyid(request)});
                    requestParams.put("filter_names", Arrays.asList("company.companyID"));
                    requestParams.put("filter_values", Arrays.asList(companyid));
                    result = customcolDAOObj.getConfigType(requestParams);
                    List lst = result.getEntityList();
                    colcount = lst.size();
                    if(colcount==20){
                        //return ("{'success':'msg',title:'Alert',msg:'Cannot add new field. Maximum custom field limit reached.'}");
                        jobj.put("success", "msg");
                        jobj.put("comboTitle",messageSource.getMessage("hrms.common.alert", null,"Alert", RequestContextUtils.getLocale(request)));
                        jobj.put("msg", messageSource.getMessage("hrms.common.CannotaddnewfieldMaximumcustomfieldlimitreached", null,"Cannot add new field. Maximum custom field limit reached.", RequestContextUtils.getLocale(request)));
                        max = true;
                    }
//                    String hql1 = "select configid from ConfigType where formtype=? and name=? and company.companyID=? ";
//                    List lst1 = HibernateUtil.executeQuery(session, hql1, new Object[]{request.getParameter("formtype"), request.getParameter("fieldname"), AuthHandler.getCompanyid(request)});
                    if (!max) {
                        requestParams.clear();
                    requestParams.put("filter_names", Arrays.asList("formtype","name","company.companyID"));
                        requestParams.put("filter_values", Arrays.asList(request.getParameter("formtype"), request.getParameter("fieldname"), companyid));
                        result = customcolDAOObj.getConfigType(requestParams);
                        List lst1 = result.getEntityList();


                        Iterator ite1 = lst1.iterator();
                        if (ite1.hasNext()) {
                            //return ("{'success':'msg',title:'Alert',msg:'FieldName exists. Please provide a different FieldName'}");
                        	isRecordExist = true;
                            jobj.put("success", "msg");
                            jobj.put("comboTitle",messageSource.getMessage("hrms.common.alert", null,"Alert", RequestContextUtils.getLocale(request)));
                            jobj.put("msg", messageSource.getMessage("hrms.common.FieldNameexistsPleaseprovideadifferentFieldName", null,"FieldName exists. Please provide a different FieldName.", RequestContextUtils.getLocale(request)));
                        } else {
//                        contyp = new ConfigType();
                        Iterator ite =lst.iterator();
                        int[] countchk= new int[21];
                        while(ite.hasNext()){
                            ConfigType tmpcontyp= (ConfigType) ite.next();
                            countchk[tmpcontyp.getColnum()]=1;
                            }
                        for(int i=1;i<=20;i++){
                            if(countchk[i]==0){
                                colcount=i;
                                    break;
                                }
                            }
                        	jobj.put("comboTitle",messageSource.getMessage("hrms.common.success", null,"Success", RequestContextUtils.getLocale(request)));
                            jobj.put("msg", messageSource.getMessage("hrms.common.Fieldaddedsuccessfully", null,"Field added successfully.", RequestContextUtils.getLocale(request)));
                        }
                        requestParams.clear();
                    }
                } else {
//                    contyp = (ConfigType) session.load(ConfigType.class, request.getParameter("configid"));
//                    colcount=contyp.getColnum();
                	jobj.put("comboTitle", messageSource.getMessage("hrms.common.success", null,"Success", RequestContextUtils.getLocale(request)));
                    jobj.put("msg", messageSource.getMessage("hrms.common.Fieldeditedsuccessfully", null,"Field edited successfully.", RequestContextUtils.getLocale(request)));
                    requestParams.clear();
                    contyp = (ConfigType) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.ConfigType", request.getParameter("configid"));
                    colcount=contyp.getColnum();
                    requestParams.put("Configid", request.getParameter("configid"));

                }
//                contyp.setConfigtype(0);
//                contyp.setCompany((Company) session.get(Company.class, AuthHandler.getCompanyid(request)));
//                contyp.setFormtype(request.getParameter("formtype"));
//                contyp.setName(request.getParameter("fieldname"));
//                contyp.setColnum(colcount);
//                session.save(contyp);
                if(!isRecordExist){
                requestParams.put("Company", companyid);
                requestParams.put("Formtype", request.getParameter("formtype"));
                requestParams.put("Name", request.getParameter("fieldname"));
                requestParams.put("Colnum", colcount);
                if(!StringUtil.isNullOrEmpty(request.getParameter("blockemployees")) && request.getParameter("blockemployees").equals("true")){
                	requestParams.put("Blockemployees", 1);
                }else{
                	requestParams.put("Blockemployees",0);
                }
                result = customcolDAOObj.addConfigType(requestParams);
                }
            }
            jobj.put("success", "msg");
            jobj.put("title", messageSource.getMessage("hrms.common.alert", null,"Alert", RequestContextUtils.getLocale(request)));
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        //return ("{'success':'msg',title:'Alert',msg:'" + msg + "'}");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return  new ModelAndView(successView,"model",jobj1.toString());
        }
    }

    public ModelAndView deleteConfig(HttpServletRequest request,HttpServletResponse response){
        String mode = request.getParameter("mode");
        String delid = request.getParameter("delid");
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            JSONArray jarr = new JSONArray(delid);
            for (int ctr = 0; ctr < jarr.length(); ctr++) {
                if (mode.equals("config")) {
                    ConfigType contyp = (ConfigType) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.ConfigType", jarr.get(ctr).toString());
                    requestParams.put("filter_names", Arrays.asList("company.companyID"));
                    requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request)));
                    result = profileHandlerDAOObj.getUser(requestParams);
                    List lst = result.getEntityList();
                    Iterator ite = lst.iterator();
                    String userids = "";
                    while (ite.hasNext()) {
                        User user = (User) ite.next();
                        userids += "'"+user.getUserID() + "',";
                    }
                    userids = userids.substring(0,userids.length()-1);
                    requestParams.clear();
                    requestParams.put("filter_names",Arrays.asList("INreferenceid"));
                    requestParams.put("filter_values",Arrays.asList(userids));
                    result = customcolDAOObj.getConfigData(requestParams);
                    lst = result.getEntityList();
                    ite = lst.iterator();
                    while (ite.hasNext()) {
                        ConfigData condata = (ConfigData) ite.next();
                        requestParams.clear();
                        requestParams.put("Id", condata.getId());
                        requestParams.put("Col"+contyp.getColnum(), "");
                        customcolDAOObj.addConfigData(requestParams);
                    }
                    customcolDAOObj.deleteConfigType(contyp.getConfigid());
                    jobj.put("success", true);
                    jobj1.put("data", jobj.toString());
                    jobj1.put("valid", true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }

    public ModelAndView getConfigData(HttpServletRequest request, HttpServletResponse response) {
//        String result = "";
        KwlReturnObject result;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {

              String companyid = sessionHandlerImplObj.getCompanyid(request);
              String configtype = request.getParameter("configType");

              HashMap<String, Object> requestParams = new HashMap<String, Object>();
              requestParams.put("filter_names", Arrays.asList("formtype","company.companyID"));
              requestParams.put("filter_values", Arrays.asList(configtype,companyid));
              result = customcolDAOObj.getConfigType(requestParams);
              List lst = result.getEntityList();
//            String hql = "from ConfigType where formtype=? and company.companyID=?";
//            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("configType"), AuthHandler.getCompanyid(request)});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("fieldname", contyp.getName());
                tmpObj.put("configid", contyp.getConfigid());
                tmpObj.put("configtype", contyp.getConfigtype());
                tmpObj.put("colnum", contyp.getColnum());
                tmpObj.put("blockemployees", contyp.getBlockemployees());
                requestParams.clear();
                requestParams.put("filter_names", Arrays.asList("referenceid"));
                requestParams.put("filter_values", Arrays.asList(request.getParameter("refid")));
                result = customcolDAOObj.getConfigData(requestParams);
//                String hql2 = "from ConfigData where referenceid=?";
//                List lst2 = HibernateUtil.executeQuery(session, hql2, new Object[]{request.getParameter("refid")});
                List lst2 = result.getEntityList();
                Iterator ite2 = lst2.iterator();
                if (!ite2.hasNext()) {
                    tmpObj.put("configdata", "");
                } else {
                    JSONArray jarr2 = new JSONArray();
                    while (ite2.hasNext()) {
                        ConfigData condata = (ConfigData) ite2.next();
                        String configdata = condata.getCol(contyp.getColnum());
                        jarr2.put(configdata);
                    }
                    tmpObj.put("configdata", jarr2);
                }
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
//            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
//        return result;
        return new ModelAndView(successView, "model", jobj1.toString());
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
