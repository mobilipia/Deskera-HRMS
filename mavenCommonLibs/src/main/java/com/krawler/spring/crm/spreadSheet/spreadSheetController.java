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
package com.krawler.spring.crm.spreadSheet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.common.admin.SpreadSheetConfig;
import java.util.Date;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class spreadSheetController extends MultiActionController {
    private spreadSheetDAO spreadSheetDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
		return successView;
	}

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    
	public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setspreadSheetDAO(spreadSheetDAO spreadSheetDAOObj1) {
        this.spreadSheetDAOObj = spreadSheetDAOObj1;
    }

    public ModelAndView getSpreadsheetConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       try{
            JSONArray jarr = new JSONArray();
            kmsg = spreadSheetDAOObj.getSpreadsheetConfig(request);
            String rules = "{rules:[]}";
            String states = "{columns:false}";
            Iterator ite = kmsg.getEntityList().iterator();
            if (ite.hasNext()) {
                SpreadSheetConfig cm = (SpreadSheetConfig) ite.next();
                JSONObject obj = new JSONObject();
                if (cm.getRules() != null && !cm.getRules().equals("")) {
                    rules = cm.getRules();
                }
                if (cm.getState() != null && !cm.getState().equals("")) {
                    states = cm.getState();
                }

                obj.put("cid", cm.getCid());
                obj.put("state", new JSONObject(states));
                obj.put("rules", new JSONObject(rules));
                jarr.put(obj);
            } else {
                JSONObject obj = new JSONObject();
                obj.put("cid", "0");
                obj.put("state", new JSONObject(states));
                obj.put("rules", new JSONObject(rules));
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("success", true);
       } catch(Exception e) {
          System.out.println(e.getMessage());
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveSpreadsheetConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
       //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            JSONObject jobj = new JSONObject();
            SpreadSheetConfig cm = null;            
            String cmuuid = "";
            String cid = request.getParameter("cid");
            if (StringUtil.isNullOrEmpty(cid)) {
                cid = "";
                cmuuid = java.util.UUID.randomUUID().toString();
                jobj.put("cmuuid", cmuuid);
                jobj.put("module", request.getParameter("module"));
                jobj.put("userid", sessionHandlerImplObj.getUserid(request));
            }
            jobj.put("cid", cid);
            if(request.getParameter("rules") != null) {
                String rule = request.getParameter("rules");
                jobj.put("rule", rule);
            }
            if(request.getParameter("state") != null) {
                String state = request.getParameter("state");
                jobj.put("state", state);
            }

            jobj.put("updatedon", new Date());
            cm = spreadSheetDAOObj.saveSpreadsheetConfig(jobj);
            JSONObject obj = new JSONObject();

//            String states = "{columns:false}"; //Used for saveGridState
            String rules = "{rules:[]}";
            String states = "{state:{}}";
            if (!StringUtil.isNullOrEmpty(cm.getRules())) {
                rules = cm.getRules();
            }
            if (!StringUtil.isNullOrEmpty(cm.getState())) {
                states = cm.getState();
            }

            obj.put("cid", cm.getCid());
            obj.put("state", new JSONObject(states));
            obj.put("rules", new JSONObject(rules));
            jarr.put(obj);

            jobj1.put("data", jarr);
            jobj1.put("success", true);
            txnManager.commit(status);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
       return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView saveModuleRecordStyle(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject myjobj = new JSONObject();
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            myjobj.put("success", false);
            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                String classname = "com.krawler.crm.database.tables.Crm"+request.getParameter("module");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jo = jarr.getJSONObject(i);
                    jo.put("classname", classname);
                    myjobj = spreadSheetDAOObj.saveModuleRecordStyle(jo);
                }
            }
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
}
