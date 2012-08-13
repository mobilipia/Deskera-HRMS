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
package com.krawler.spring.hrms.template;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.hrms.template.db.HtmlTemplate;
import com.krawler.spring.hrms.template.db.PlaceHolder;
import com.krawler.spring.hrms.template.db.TargetList;
import com.krawler.spring.hrms.template.db.TargetListTargets;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;
/**
 *
 * @author Abhishek Dubey <abhishek.dubey@krawlernetworks.com>
 */
public class hrmsTemplateController extends MultiActionController implements MessageSourceAware{

    private String successView;
    private hrmsTemplateDAO hrmsTemplateDAOObj;
//    private hrmsCommonDAO hrmsCommonDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private HibernateTransactionManager txnManager;
    private HibernateTemplate hibernateTemplate;
    private sessionHandlerImpl sessionHandlerImplObj;
    private MessageSource messageSource;

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
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

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setHrmsTemplateDAO(hrmsTemplateDAO hrmsTemplateDAOObj) {
        this.hrmsTemplateDAOObj = hrmsTemplateDAOObj;
    }

    public ModelAndView getParameterType(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String cid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("Cid", cid);
            result = hrmsTemplateDAOObj.getParameterType(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                JSONObject tempjobj = new JSONObject();
                tempjobj.put("type", messageSource.getMessage("hrms.EmailTemplateCmb."+StringUtil.replaceNametoLocalkey(lst.get(i).toString()), null,lst.get(i).toString(), RequestContextUtils.getLocale(request)));
                jobj.append("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView getParameterTypeValuePair(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String cid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("Cid", cid);
            result = hrmsTemplateDAOObj.getParameterTypeValuePair(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                PlaceHolder ph = (PlaceHolder) lst.get(i);
                JSONObject tempjobj = new JSONObject();
                tempjobj.put("id", ph.getId());
                tempjobj.put("ph", ph.getPlaceholder());
                tempjobj.put("type",messageSource.getMessage("hrms.EmailTemplateCmb."+StringUtil.replaceNametoLocalkey(ph.getType()), null,ph.getType(), RequestContextUtils.getLocale(request)));
                tempjobj.put("value", messageSource.getMessage("hrms.EmailTemplateCmb."+StringUtil.replaceNametoLocalkey(ph.getValue()), null,ph.getType(), RequestContextUtils.getLocale(request)));
                jobj.append("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView saveTemplate(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String action = "Add";
            if (!StringUtil.isNullOrEmpty(request.getParameter("tid"))) {
                action = "Edit";
                requestParams.put("tid", request.getParameter("tid"));
            }
            requestParams.put("Action", action);
            requestParams.put("mode", request.getParameter("mode"));
            requestParams.put("tbody", URLEncoder.encode(request.getParameter("tbody"), "utf-8"));
            requestParams.put("tdesc", request.getParameter("tdesc"));
            requestParams.put("tname", request.getParameter("tname"));
            requestParams.put("tsub", request.getParameter("tsub"));
            requestParams.put("date", new Date());
            requestParams.put("company", (Company) hibernateTemplate.get(Company.class, sessionHandlerImplObj.getCompanyid(request)));

            result = hrmsTemplateDAOObj.saveTemplate(requestParams);
/////////////////            result.getEntityList().get(0);
           ArrayList<String[]> placehPairs = getPlaceHolderPairs(request.getParameter("tbody"));
           if(action.equals("Edit"))
               delTemplatePlaceHolderMapping((String)result.getEntityList().get(0));
           saveTemplatePlaceHolderMapping((String)result.getEntityList().get(0), placehPairs);


/*           if (requestParams.get("Action").equals("Add") && result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getTaxtype() + " of type Tax"  ,request);
                //--        auditTrailDAOObj.insertAuditLog(AuditAction.INVESTMENT_STRUCTURE_ADDED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new Investment Structure " + request.getParameter("investid"), request, "0");
            } else if (result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getTaxtype() ,request);
                //--        auditTrailDAOObj.insertAuditLog(AuditAction.INVESTMENT_STRUCTURE_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited Investment Structure " + request.getParameter("investid"), request, "0");
            }
*/
           txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public static ArrayList<String[]> getPlaceHolderPairs(String /*Buffer*/ htmlmsg) {
        ArrayList<String[]> al = new ArrayList();//#first:second#
        String expr = "[#]{1}[a-zA-Z]+[:]{1}[a-zA-Z]+[#]{1}";
        Pattern p = Pattern.compile(expr);
        Matcher m = p.matcher(htmlmsg);
        while (m.find()) {
            String table = "";
            String woHash = "";
            table = m.group();
            woHash = table.substring(1, table.length() - 1);
            String[] sp = woHash.split(":");
            al.add(new String[]{sp[0],sp[1]});
        }
        return al;
    }

    public void delTemplatePlaceHolderMapping(String template_id) {
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("htid", template_id);
                    hrmsTemplateDAOObj.delTemplatePlaceHolderMapping(requestParams);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
    }


    public void saveTemplatePlaceHolderMapping(String template_id,ArrayList<String[]> placeholders) {
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            List placeholderIds = getIdFromPlaceholders(placeholders);
            HtmlTemplate ht= (HtmlTemplate)hibernateTemplate.get(HtmlTemplate.class, template_id);
            if(placeholderIds!=null){
                for(int i=0;i<placeholderIds.size();i++){
                    requestParams.put("ht", ht);
                    requestParams.put("phid", placeholderIds.get(i));
                            hrmsTemplateDAOObj.saveTemplatePlaceHolderMapping(requestParams);
                }
            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
    }

    public List getIdFromPlaceholders(ArrayList<String[]> placeholders) {
        KwlReturnObject result = null;
        try {
            String placeholderStrings ="";
            for(int i=0;i<placeholders.size();i++){
                placeholderStrings+="'"+placeholders.get(i)[0]+"_"+placeholders.get(i)[1]+"',";//"('"+placeholders.get(i)[0]+"','"+placeholders.get(i)[1]+"'),";
            }
            if(placeholderStrings.length()>1){
                placeholderStrings = placeholderStrings.substring(0, placeholderStrings.length()-1);
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("phstrings", placeholderStrings);
                result = hrmsTemplateDAOObj.getIdFromPlaceholders(requestParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        }
        if(result==null)return null;
        return result.getEntityList();
    }



    public ModelAndView getTemplates(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String cid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("allflag", false);
            requestParams.put("Cid", cid);
            requestParams.put("searchcol", new String[]{"name"});
            requestParams.put("ss", request.getParameter("ss"));
            StringUtil.checkpaging(requestParams, request);
            result = hrmsTemplateDAOObj.getTemplates(requestParams);
            List lst = result.getEntityList();
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            for (int i = 0; i < lst.size(); i++) {
                JSONObject tempjobj = new JSONObject();
                HtmlTemplate ht = (HtmlTemplate) lst.get(i);
                tempjobj.put("templateid", ht.getId());
                tempjobj.put("templatename", ht.getName());
                tempjobj.put("subject", ht.getSubject());
                tempjobj.put("description", ht.getDescr() == null ? "" : ht.getDescr());
                tempjobj.put("createdon", df.format(ht.getCreatedon()));
                tempjobj.put("modifiedon", ht.getModifiedon() == null ? "" : df.format(ht.getModifiedon()));
                tempjobj.put("bodyhtml", ht.getText() == null ? "" : ht.getText());
                tempjobj.put("bodytext", ht.getPlaintext() == null ? "" : ht.getPlaintext());
                jobj.append("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView delTemplate(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("tempid", request.getParameter("tempid"));
            result = hrmsTemplateDAOObj.delTemplate(requestParams);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

  public ModelAndView getTemplateContent(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("id", request.getParameter("templateid"));
            result = hrmsTemplateDAOObj.getTemplateContent(requestParams);
            List lst = result.getEntityList();
              if(lst.size()>0){
                JSONObject tempjobj = new JSONObject();
                HtmlTemplate ht = (HtmlTemplate) lst.get(0);
                tempjobj.put("html", URLDecoder.decode(ht.getText() == null ? "" : ht.getText(), "utf-8"));
                jobj.put("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

  public ModelAndView getTemplateTargetList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String cid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("Cid", cid);
            result = hrmsTemplateDAOObj.getTemplateTargetList(requestParams);
            List lst = result.getEntityList();
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            for (int i = 0; i < lst.size(); i++) {
                JSONObject tempjobj = new JSONObject();
                TargetList tl = (TargetList) lst.get(i);
                tempjobj.put("listid", tl.getId());
                tempjobj.put("listname",tl.getName());
                tempjobj.put("descr", tl.getDescription());
                tempjobj.put("createdon", df.format(tl.getCreatedon()));
                tempjobj.put("modifiedon", tl.getModifiedon() == null ? "" : df.format(tl.getModifiedon()));
                jobj.append("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView saveTemplateTargetList(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String action = "Add";
            if (!StringUtil.isNullOrEmpty(request.getParameter("tid"))) {
                action = "Edit";
                requestParams.put("tid", request.getParameter("tid"));
            }
            requestParams.put("Action", action);
            requestParams.put("name", request.getParameter("name"));
            requestParams.put("descr", request.getParameter("descr"));
            requestParams.put("date", new Date());
            requestParams.put("creator", (User)hibernateTemplate.get(User.class,sessionHandlerImplObj.getUserid(request)));
  //          requestParams.put("company", (Company) hibernateTemplate.get(Company.class, sessionHandlerImplObj.getCompanyid(request)));

            //requestParams.put("Cid", sessionHandlerImplObj.getCompanyid(request));

            result = hrmsTemplateDAOObj.saveTemplateTargetList(requestParams);
  /*          if (requestParams.get("Action").equals("Add") && result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getTaxtype() + " of type Tax"  ,request);
                //--        auditTrailDAOObj.insertAuditLog(AuditAction.INVESTMENT_STRUCTURE_ADDED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new Investment Structure " + request.getParameter("investid"), request, "0");
            } else if (result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getTaxtype() ,request);
                //--        auditTrailDAOObj.insertAuditLog(AuditAction.INVESTMENT_STRUCTURE_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited Investment Structure " + request.getParameter("investid"), request, "0");
            }
  */          txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public ModelAndView delTemplateTargetList(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("listid", request.getParameter("listid"));
            result = hrmsTemplateDAOObj.delTemplateTargetList(requestParams);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }


    public ModelAndView getTemplateTargetListDetails(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("listid", request.getParameter("listid"));
            result = hrmsTemplateDAOObj.getTemplateTargetListDetails(requestParams);
            List lst = result.getEntityList();
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            for (int i = 0; i < lst.size(); i++) {
                JSONObject tempjobj = new JSONObject();
                TargetListTargets tlt = (TargetListTargets) lst.get(i);
                tempjobj.put("id", tlt.getUsrid());//user id is sent instead of table id
                tempjobj.put("fullname",tlt.getFname()+" "+tlt.getLname());
                tempjobj.put("emailid", tlt.getEmailid());
                tempjobj.put("createdon", tlt.getCreatedon() == null ? "" : df.format(tlt.getCreatedon()));
                tempjobj.put("modifiedon", tlt.getModifiedon() == null ? "" : df.format(tlt.getModifiedon()));
                jobj.append("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }


    public ModelAndView saveTemplateTargetListDetails(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
          //  String records = request.getParameter("records");
            JSONArray recordsArr = new JSONArray(request.getParameter("records"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("listid", request.getParameter("listid"));
            result = hrmsTemplateDAOObj.delTemplateTargetListDetails(requestParams);
            TargetList tl = (TargetList)hibernateTemplate.get(TargetList.class, request.getParameter("listid"));
            if(tl!=null){
            for(int i=0;i<recordsArr.length();i++){
                User usr = (User)hibernateTemplate.get(User.class, recordsArr.getJSONObject(i).getString("uid"));
                if(usr==null)continue;
                requestParams.clear();
                requestParams.put("uid", usr.getUserID());
                requestParams.put("emailid", usr.getEmailID());
                requestParams.put("fname", usr.getFirstName());
                requestParams.put("lname", usr.getLastName());
                requestParams.put("list", tl);
                result = hrmsTemplateDAOObj.saveTemplateTargetListDetails(requestParams);               
            }
/*            if (requestParams.get("Action").equals("Add") && result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getTaxtype() + " of type Tax"  ,request);
                //--        auditTrailDAOObj.insertAuditLog(AuditAction.INVESTMENT_STRUCTURE_ADDED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new Investment Structure " + request.getParameter("investid"), request, "0");
            } else if (result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getTaxtype() ,request);
                //--        auditTrailDAOObj.insertAuditLog(AuditAction.INVESTMENT_STRUCTURE_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited Investment Structure " + request.getParameter("investid"), request, "0");
            }*/

            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }


    public ModelAndView getNewTemplateTargetListDetails(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String cid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("Cid", cid);
            result = hrmsTemplateDAOObj.getNewTemplateTargetListDetails(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                JSONObject tempjobj = new JSONObject();
                User usr = (User) lst.get(i);
                tempjobj.put("id", usr.getUserID());
                tempjobj.put("fullname",usr.getFirstName()+" "+usr.getLastName());
                tempjobj.put("emailid", usr.getEmailID());
                jobj.append("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }


   public ModelAndView getBriefTemplateList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String cid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("Cid", cid);
            result = hrmsTemplateDAOObj.getTemplates(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                JSONObject tempjobj = new JSONObject();
                HtmlTemplate ht = (HtmlTemplate) lst.get(i);
                tempjobj.put("id", ht.getId());
                tempjobj.put("name", ht.getName());
                jobj.append("data", tempjobj);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }
   @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }

}
