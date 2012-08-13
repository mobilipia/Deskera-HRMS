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
package com.krawler.spring.exportFunctionality;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.admin.Projreport_Template;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 *
 * @author Karthik
 */
public class exportPdfTemplateController extends MultiActionController {

    private exportPdfTemplateDAO exportPdfTemplateDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setexportPdfTemplateDAO(exportPdfTemplateDAO exportPdfTemplateDAOObj1) {
        this.exportPdfTemplateDAOObj = exportPdfTemplateDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    
    public ModelAndView saveReportTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String jsondata = request.getParameter("data");
            String userid = request.getParameter("userid");
            String name = request.getParameter("name");
            String desc = request.getParameter("desc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("jsondata", StringUtil.checkForNull(jsondata));
            requestParams.put("userid", StringUtil.checkForNull(userid));
            requestParams.put("name", StringUtil.checkForNull(name));
            requestParams.put("desc", StringUtil.checkForNull(desc));

            kmsg = exportPdfTemplateDAOObj.saveReportTemplate(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getReportTemplateJson(List ll) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Projreport_Template obj = (Projreport_Template) ite.next();
                JSONObject jtemp = new JSONObject();

                jtemp.put("tempid", obj.getTempid());
                jtemp.put("tempname", obj.getTempname());
                jtemp.put("description", obj.getDescription());
                jtemp.put("configstr", obj.getConfigstr());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public ModelAndView getAllReportTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        try {

            String userid = sessionHandlerImplObj.getUserid(request);

            kmsg = exportPdfTemplateDAOObj.getAllReportTemplate(userid);
            jobj = getReportTemplateJson(kmsg.getEntityList());
            jobj.put("success", kmsg.isSuccessFlag());
            jobj1.put("data",jobj);
            jobj1.put("valid",true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView deleteReportTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String tempid = StringUtil.checkForNull(request.getParameter("deleteflag"));
            kmsg = exportPdfTemplateDAOObj.deleteReportTemplate(tempid);

            jobj.put("success", kmsg.isSuccessFlag());
            jobj1.put("data",jobj);
            jobj1.put("valid",true);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView editReportTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String tempid = request.getParameter("edit");
            String newconfig = request.getParameter("data");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("tempid", StringUtil.checkForNull(tempid));
            requestParams.put("newconfig", StringUtil.checkForNull(newconfig));

            kmsg = exportPdfTemplateDAOObj.editReportTemplate(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
