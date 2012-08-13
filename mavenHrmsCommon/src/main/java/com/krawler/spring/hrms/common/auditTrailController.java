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
package com.krawler.spring.hrms.common;

import com.krawler.common.admin.AuditGroup;
import com.krawler.common.admin.AuditTrail;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;


public class auditTrailController extends MultiActionController implements MessageSourceAware{

    private String successView;
    private auditTrailDAO auditTrailDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private MessageSource messageSource;


    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public JSONObject getAuditJSONData(List ll, HttpServletRequest request, int totalSize) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            Iterator itr = ll.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                AuditTrail auditTrail = (AuditTrail) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("id", auditTrail.getID());
                obj.put("username", auditTrail.getUser().getUserLogin().getUserName() + " [ " +  profileHandlerDAOObj.getUserFullName(auditTrail.getUser().getUserID()) + " ]");
                obj.put("ipaddr", auditTrail.getIPAddress());
                obj.put("details", auditTrail.getDetails());
                obj.put("actionname", auditTrail.getAction().getActionName());
                obj.put("timestamp", AuthHandler.getDateFormatter(request).format(auditTrail.getAuditTime()));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", totalSize);

        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public JSONObject getAuditGroupJsonData(List ll, HttpServletRequest request, int totalSize) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        try {
            JSONObject objN = new JSONObject();
            objN.put("groupid", "");
            objN.put("groupname", "--All--");
            jArr.put(objN);
            Iterator itr = ll.iterator();
            while (itr.hasNext()) {
                AuditGroup auditGroup = (AuditGroup) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("groupid", auditGroup.getID());
                obj.put("groupname",messageSource.getMessage("hrms.AuditGroup."+StringUtil.replaceNametoLocalkey(auditGroup.getGroupName()), null, RequestContextUtils.getLocale(request)));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", totalSize);

        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public ModelAndView getAuditData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("groupid", StringUtil.checkForNull(request.getParameter("groupid")));
            requestParams.put("search", StringUtil.checkForNull(request.getParameter("search")));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            kmsg = auditTrailDAOObj.getAuditData(requestParams);
            jobj = getAuditJSONData(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getAuditGroupData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            kmsg = auditTrailDAOObj.getAuditGroupData();
            jobj = getAuditGroupJsonData(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
