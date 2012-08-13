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
package com.krawler.spring.profileHandler;

import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.authHandler.authHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Karthik
 */
public class profileHandlerController extends MultiActionController implements MessageSourceAware {

    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private MessageSource messageSource;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public ModelAndView getAllUserDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String lid = StringUtil.checkForNull(request.getParameter("lid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("lid", lid);
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.company.companyID");
            filter_params.add(companyid);
            filter_names.add("u.deleteflag");
            filter_params.add(0);
            if (!StringUtil.isNullOrEmpty(lid)) {
                filter_names.add("u.userID");
                filter_params.add(lid);
            }
            
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
            jobj = getUserDetailsJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getUserDetailsJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String timeFormatId = sessionHandlerImplObj.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImplObj.getTimeZoneDifference(request);
            while (ite.hasNext()) {
                User user = (User) ite.next();
                UserLogin ul = user.getUserLogin();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", ul.getUserName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());
                obj.put("lastlogin", (ul.getLastActivityDate() == null ? "" : authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(ul.getLastActivityDate())));
                obj.put("aboutuser", user.getAboutUser());
                obj.put("address", user.getAddress());
                obj.put("contactno", user.getContactNumber());
                obj.put("formatid", (user.getDateFormat() == null ? "" : user.getDateFormat().getFormatID()));
                obj.put("tzid", (user.getTimeZone() == null ? Constants.NEWYORK_TIMEZONE_ID : user.getTimeZone().getTimeZoneID())); // 23 is id of New York Time Zone. [default]
                obj.put("callwithid", user.getCallwith());
                obj.put("timeformat", (user.getTimeformat() != 1 && user.getTimeformat() != 2) ? 2 : user.getTimeformat()); // 2 is id for '24 hour timeformat'. [default]
                String roleStr = "";
                int roleid = user.getRoleID();
                if ((roleid & 1) == 1) {
                    roleStr = roleStr + " Company Admin,";
                }
                if ((roleid & 2) == 2) {
                    roleStr = roleStr + " Sales Manager,";
                }
                if ((roleid & 4) == 4) {
                    roleStr = roleStr + " Sales Executive,";
                }
                int len = roleStr.length() - 1;
                if (len > 0) {
                    roleStr = roleStr.substring(0, len);
                }
                obj.put("roles", roleStr);
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getAllManagers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            kmsg = profileHandlerDAOObj.getAllManagers(requestParams);
            jobj = getUserDetailsJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveDateFormat(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String dateid = request.getParameter("newformat");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
            requestParams.put("dateformat", StringUtil.checkForNull(dateid));

            profileHandlerDAOObj.saveUser(requestParams);
            request.getSession().setAttribute("dateformatid", dateid);
            jobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] ids = request.getParameterValues("userids");
            for (int i = 0; i < ids.length; i++) {
                profileHandlerDAOObj.deleteUser(ids[i]);
            }
            jobj.put("msg", messageSource.getMessage("hrms.commonlib.user.deleted.successfully", null, RequestContextUtils.getLocale(request)));
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getUserofCompany(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.company.companyID");
            filter_params.add(companyid);
            filter_names.add("u.deleteflag");
            filter_params.add(0);
            
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
            jobj = getUserDetailsJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response)
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
            String platformURL = this.getServletContext().getInitParameter("platformURL");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("currentpassword", StringUtil.checkForNull(request.getParameter("currentpassword")));
            requestParams.put("changepassword", StringUtil.checkForNull(request.getParameter("changepassword")));
            requestParams.put("platformURL", platformURL);
            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            Locale locale = RequestContextUtils.getLocale(request);
            requestParams.put("locale", locale);
            kmsg = profileHandlerDAOObj.changePassword(requestParams);
            jobj = (JSONObject) kmsg.getEntityList().get(0);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
