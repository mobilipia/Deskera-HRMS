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
package com.krawler.spring.dashboardWidget;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import com.krawler.common.admin.widgetManagement;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class widgetController extends MultiActionController {
    private String successView;
    private widgetDAO widgetDAOObj;
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

    public void setwidgetDAO(widgetDAO widgetDAOObj) {
        this.widgetDAOObj = widgetDAOObj;
    }

    public ModelAndView removeWidgetFromState(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJson = new JSONObject();
        KwlReturnObject kmsg = null;
        widgetManagement wm = null;
       //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String wid = request.getParameter("wid");
            requestParams.put("wid", wid);
            requestParams.put("userid", AuthHandler.getUserid(request));
            kmsg = this.widgetDAOObj.removeWidgetFromState(requestParams);
            resultJson.put("success", kmsg.isSuccessFlag());
            if(kmsg.isSuccessFlag()) {
                wm = (widgetManagement) kmsg.getEntityList().get(0);
                resultJson.put("ID", wm.getId());
            }
            txnManager.commit(status);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch(JSONException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch(ServiceException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }
    
    public ModelAndView insertWidgetIntoState(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJson = new JSONObject();
        KwlReturnObject kmsg = null;
        widgetManagement wm = null;
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String wid = request.getParameter("wid");
            requestParams.put("wid", wid);
            requestParams.put("colno", request.getParameter("colno"));
            requestParams.put("userid", AuthHandler.getUserid(request));
            kmsg = this.widgetDAOObj.insertWidgetIntoState(requestParams);
            resultJson.put("success", kmsg.isSuccessFlag());
            if(kmsg.isSuccessFlag()) {
                wm = (widgetManagement) kmsg.getEntityList().get(0);
                resultJson.put("ID", wm.getId());
            }
            txnManager.commit(status);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch(JSONException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch(ServiceException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }

    public ModelAndView changeWidgetState(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJson = new JSONObject();
        KwlReturnObject kmsg = null;
        widgetManagement wm = null;
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String wid = request.getParameter("wid");
            requestParams.put("wid", wid);
            requestParams.put("colno", request.getParameter("colno"));
            requestParams.put("userid", AuthHandler.getUserid(request));
            requestParams.put("position", request.getParameter("position"));
            kmsg = this.widgetDAOObj.changeWidgetState(requestParams);
            resultJson.put("success", kmsg.isSuccessFlag());
            if(kmsg.isSuccessFlag()) {
                wm = (widgetManagement) kmsg.getEntityList().get(0);
                resultJson.put("ID", wm.getId());
            }
            txnManager.commit(status);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch(JSONException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch(ServiceException ex) {
            Logger.getLogger(widgetController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }
}
