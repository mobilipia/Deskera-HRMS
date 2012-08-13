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
package com.krawler.spring.authHandler;

import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.Language;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.permissionHandler.permissionHandler;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Karthik
 */
public class authHandlerController extends MultiActionController {
    private static final Log logger = LogFactory.getLog(authHandlerController.class);
    private authHandlerDAO authHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private companyDetailsDAO companyDetailsDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private String successView;

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public permissionHandlerDAO getPermissionHandlerDAOObj() {
        return permissionHandlerDAOObj;
    }

    public void setPermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj;
    }

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    
    public void setauthHandlerDAO(authHandlerDAO authHandlerDAOObj1) {
        this.authHandlerDAOObj = authHandlerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }
    
    public JSONObject getVerifyLoginJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            Iterator ite = ll.iterator();
            if (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                User user = (User) row[0];
                UserLogin userLogin = (UserLogin) row[1];
                Company company = (Company) row[2];
                CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", company.getCompanyID());

                jobj.put("success", true);
                jobj.put("lid", userLogin.getUserID());
                jobj.put("username", userLogin.getUserName());
                jobj.put("companyid", company.getCompanyID());
                jobj.put("isMalaysianCompany", StringUtil.isMalaysianCompany(company.getCountry().getID()));
                jobj.put("isStandAlone", StringUtil.isStandAlone());
                jobj.put("company", company.getCompanyName());
                Language lang = company.getLanguage();
                if(lang!=null){
                	jobj.put("language", lang.getLanguageCode()+(lang.getCountryCode()!=null?"_"+lang.getCountryCode():""));
                }
                jobj.put("roleid", user.getRoleID());
                jobj.put("callwith", user.getCallwith());
                jobj.put("timeformat", user.getTimeformat());
                KWLTimeZone timeZone = user.getTimeZone();
                if (timeZone == null) {
                    timeZone = company.getTimeZone();
                }
                if (timeZone == null) {
                    timeZone = (KWLTimeZone) ll.get(1);
                }
                jobj.put("timezoneid", timeZone.getTimeZoneID());
                jobj.put("tzdiff", timeZone.getDifference());
                jobj.put("tzid", timeZone.getTzID());
                jobj.put("subscription",cp.getSubscriptionCode());
                KWLDateFormat dateFormat = user.getDateFormat();
                if (dateFormat == null) {
                    dateFormat = (KWLDateFormat) ll.get(2);
                }
                jobj.put("dateformatid", dateFormat.getFormatID());
                KWLCurrency currency = company.getCurrency();
                if (currency == null) {
                    currency = (KWLCurrency) ll.get(3);
                }
                jobj.put("currencyid", currency.getCurrencyID());
                kmsg = companyDetailsDAOObj.getCompanyPreferences(company.getCompanyID());
                if(StringUtil.checkResultobjList(kmsg))
                       jobj.put("companyPreferences",kmsg.getEntityList().get(0));
                jobj.put("sysemailid",hrmsCommonDAOObj.getSysEmailIdByCompanyID(company.getCompanyID()));

//                kmsg = authHandlerDAOObj.getPermissionsOfUser(userLogin.getUserID());
//                ite = kmsg.getEntityList().iterator();
//                while (ite.hasNext()) {
//                    JSONObject jo = new JSONObject();
//                    Object[] roww = (Object[]) ite.next();
//                    jo.put(roww[0].toString(), roww[1]);
//                    jarr.put(jo);
//                }
//                jobj.put("perms", jarr);
                jobj.put("success", true);
            } else {
                jobj.put("failure", true);
               // jobj.put("success", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobj;
    }

//    public ModelAndView verifyLogin(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException {
//        JSONObject jobj = new JSONObject();
//        KwlReturnObject kmsg = null;
//        try {
//            String user = request.getParameter("u");
//            String pass = request.getParameter("p");
//            String subdomain = URLUtil.getDomainName(request);
//
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("user", StringUtil.checkForNull(user));
//            requestParams.put("pass", StringUtil.checkForNull(pass));
//            requestParams.put("subdomain", StringUtil.checkForNull(subdomain));
//
//            kmsg = authHandlerDAOObj.verifyLogin(requestParams);
//            jobj = getVerifyLoginJson(kmsg.getEntityList(), request);
//            if (jobj.has("success") && (jobj.get("success").equals(true))) {
//                sessionHandlerImplObj.createUserSession(request, jobj);
//                String userid = sessionHandlerImplObj.getUserid(request);
//                requestParams.put("userloginid", StringUtil.checkForNull(userid));
//                profileHandlerDAOObj.saveUserLogin(requestParams);
//            } else {
//                jobj = new JSONObject();
//                jobj.put("success", false);
//                jobj.put("message", "Authentication failed");
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return new ModelAndView("jsonView", "model", jobj.toString());
//    }

    public ModelAndView verifyLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject rjobj = new JSONObject();
        JSONObject ujobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        String result = "noaccess";
        String userid = "";
        String companyid = "";
        HashMap<String, Object> requestParams2 = null;
        JSONObject obj = null;
        boolean isValidUser = false;
        try {
            String user = request.getParameter("u");
            String pass = request.getParameter("p");
            String login = request.getParameter("blank");
            String subdomain = URLUtil.getDomainName(request);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("user", StringUtil.checkForNull(user));
            requestParams.put("pass", StringUtil.checkForNull(pass));
            requestParams.put("subdomain", StringUtil.checkForNull(subdomain));
            if (StringUtil.isNullOrEmpty(login)) {
                kmsg = authHandlerDAOObj.verifyLogin(requestParams);
                jobj = getVerifyLoginJson(kmsg.getEntityList(), request);
                if (jobj.has("success") && (jobj.get("success").equals(true))) {
                    obj = new JSONObject();
                    companyid = jobj.getString("companyid");
                    userid = jobj.getString("lid");

//                    requestParams2 = new HashMap<String, Object>();
//                    requestParams2.put("userid", userid);
//                    kmsg = permissionHandlerDAOObj.getUserPermission(requestParams2);
//                    Iterator ite = kmsg.getEntityList().iterator();
//                    JSONArray jarr = new JSONArray();
//                    while (ite.hasNext()) {
//                        JSONObject jo = new JSONObject();
//                        Object[] roww = (Object[]) ite.next();
//                        jo.put(roww[0].toString(), roww[1]);
//                        jarr.put(jo);
//                    }
//                    jobj.put("perms", jarr);

                    sessionHandlerImplObj.createUserSession(request, jobj);
                    setLocale(request, response, jobj.optString("language",null));
                    requestParams.put("userloginid", StringUtil.checkForNull(userid));
                    Date lastActivityDate = profileHandlerDAOObj.getLastActivityDate();
                    request.getSession().setAttribute("lastActivityDate", lastActivityDate);	
                    profileHandlerDAOObj.saveUserLogin(requestParams);
                } else {
                    jobj = new JSONObject();
                    jobj.put("success", false);
                    jobj.put("reason", result);
                    jobj.put("message", "Authentication failed.");
                }
                jobj1 = jobj;
            } else {
                String username = request.getRemoteUser();

                if (!StringUtil.isNullOrEmpty(username)) {
                    boolean toContinue = true;
                    if(sessionHandlerImplObj.validateSession(request, response)){
                        String companyid_session = sessionHandlerImplObj.getCompanyid(request);
                        String subdomainFromSession = companyDetailsDAOObj.getSubDomain(companyid_session);
                        if (!subdomain.equalsIgnoreCase(subdomainFromSession)) {
                            result = "alreadyloggedin";
                            toContinue = false;
                        }
                    }
                    if(toContinue){
                        requestParams = new HashMap<String, Object>();
                        requestParams.put("user", username);
                        requestParams.put("subdomain", subdomain);
                        kmsg = authHandlerDAOObj.verifyLogin(requestParams);
                        jobj = getVerifyLoginJson(kmsg.getEntityList(), request);
                        if (jobj.has("success") && jobj.get("success").equals(true)){
                            sessionHandlerImplObj.createUserSession(request, jobj);
                            setLocale(request, response, jobj.optString("language",null));
                            isValidUser = true;
                        } else {
                            result = "noaccess";
                        }
                    }
                } else {
                    if (sessionHandlerImplObj.isValidSession(request, response)) {
                        username = sessionHandlerImplObj.getUserName(request);
                        companyid = sessionHandlerImplObj.getCompanyid(request);
                        String companyName = sessionHandlerImplObj.getCompanyName(request);
                        jobj.put("username", username);
                        jobj.put("companyid", companyid);
                        jobj.put("company", companyName);
                        isValidUser = true;
                    } else {
                        result = "timeout";
                    }
                }
                
                
                if (isValidUser) {
                	companyid = sessionHandlerImplObj.getCompanyid(request);
                	Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", companyid);
                    userid = sessionHandlerImplObj.getUserid(request);
                    jobj.put("subdomain", company.getSubDomain());
                    jobj.put("fullname", profileHandlerDAOObj.getUserFullName(userid));
                    jobj.put("lid", userid);
                    jobj.put("callwith", sessionHandlerImplObj.getUserCallWith(request));
                    jobj.put("companypreferences", sessionHandlerImplObj.getcompanypreferences(request));
                    if (!permissionHandlerDAOObj.isSuperAdmin(userid, companyid)) {
                        JSONObject permJobj = new JSONObject();
                        JSONObject Jobj = new JSONObject();
                        kmsg = permissionHandlerDAOObj.getActivityFeature();
                        permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
                        Jobj.put("Perm", permJobj.get("Perm"));

                        requestParams2 = new HashMap<String, Object>();
                        requestParams2.put("userid", userid);
                        kmsg = permissionHandlerDAOObj.getPermission(requestParams2);
                        if(StringUtil.checkResultobjList(kmsg))
                            permJobj = (JSONObject)kmsg.getEntityList().get(0);
                        Jobj.put("UPerm", permJobj);
                        kmsg = hrmsCommonDAOObj.getHrmsmodule(RequestContextUtils.getLocale(request));
                        if(StringUtil.checkResultobjList(kmsg))
                            permJobj = (JSONObject)kmsg.getEntityList().get(0);
                        Jobj.put("hrms_modules", permJobj);
                        
//                        kmsg = permissionHandlerDAOObj.getUserPermission(requestParams2);
//                        permJobj = permissionHandler.getRolePermissionJson(kmsg.getEntityList(), permJobj);
                        


                        jobj.put("perm", Jobj);
                    } else {
                        jobj.put("deskeraadmin", true);
                    }
                    requestParams2 = new HashMap<String, Object>();
                    requestParams2.put("timezoneid", sessionHandlerImplObj.getTimeZoneID(request));
                    requestParams2.put("dateformatid", sessionHandlerImplObj.getDateFormatID(request));
                    requestParams2.put("currencyid", sessionHandlerImplObj.getCurrencyID(request));
                    requestParams2.put("subscriptioncode",sessionHandlerImplObj.getCmpSubscription(request));
                    
                    JSONObject prefJson = new JSONObject();
                    kmsg = authHandlerDAOObj.getPreferences(requestParams2);
                    prefJson = getPreferencesJson(kmsg.getEntityList(), request);
                    jobj.put("preferences", prefJson.getJSONArray("data").get(0));
                    jobj.put("subscriptioncode",sessionHandlerImplObj.getCmpSubscription(request));
                    jobj.put("isMalaysianCompany", StringUtil.isMalaysianCompany(company.getCountry().getID()));
                    jobj.put("isStandAlone", StringUtil.isStandAlone());

//                    JSONObject roleJson = new JSONObject();
//                    kmsg = permissionHandlerDAOObj.getRoleList();
//                    Iterator ite = kmsg.getEntityList().iterator();
//                    int inc = 0;
//                    while (ite.hasNext()) {
//                        Object row = (Object) ite.next();
//                        String rname = ((Rolelist) row).getRolename();
//                        rjobj.put(rname, (int) Math.pow(2, inc));
//                        inc++;
//                    }
//                    kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
//                    ite = kmsg.getEntityList().iterator();
//                    if(ite.hasNext()) {
//                        Object[] row = (Object[]) ite.next();
//                        ujobj.put("roleid", row[0].toString());
//                    }
//
//                    roleJson.put("Role", rjobj);
//                    roleJson.put("URole", ujobj);
//                    jobj.put("role", roleJson);
                    Useraccount u = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", userid);
                    jobj.put("roleid", u.getRole().getID());
                    jobj.put("base_url", URLUtil.getPageURL(request,""));
                    jobj1.put("valid", true);
                    jobj1.put("data", jobj.toString());

                } else {
                    jobj.put("success", false);
                    jobj.put("reason", result);
                    jobj1.put("valid", false);
                    jobj1.put("data", jobj);
                }
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public JSONObject getPreferencesJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONObject retJobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String dateformat = "";
        try {
            String timeformat = sessionHandlerImplObj.getUserTimeFormat(request);

            KWLTimeZone timeZone = (KWLTimeZone) ll.get(0);
            KWLDateFormat dateFormat = (KWLDateFormat) ll.get(1);
            KWLCurrency currency = (KWLCurrency) ll.get(2);

            jobj.put("Timezone", timeZone.getName());
            jobj.put("Timezoneid", timeZone.getTimeZoneID());
            jobj.put("Timezonediff", timeZone.getDifference());
            if (timeformat.equals("1")) {
                dateformat = dateFormat.getScriptForm().replace('H', 'h');
                if (!dateformat.equals(dateFormat.getScriptForm())) {
                    dateformat += " T";
                }
            } else {
                dateformat = dateFormat.getScriptForm();
            }
            jobj.put("DateFormat", dateformat);
            jobj.put("DateFormatid", dateFormat.getFormatID());
            jobj.put("seperatorpos", dateFormat.getScriptSeperatorPosition());
            jobj.put("Currency", currency.getHtmlcode());
            jobj.put("CurrencyName", currency.getName());
            jobj.put("CurrencySymbol", currency.getSymbol());
            jobj.put("Currencyid", currency.getCurrencyID());
            jarr.put(jobj);

            retJobj.put("data", jarr);
            retJobj.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retJobj;
    }

    public ModelAndView getPreferences(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("timezoneid", sessionHandlerImplObj.getTimeZoneID(request));
            requestParams.put("dateformatid", sessionHandlerImplObj.getDateFormatID(request));
            requestParams.put("currencyid", sessionHandlerImplObj.getCurrencyID(request));

            kmsg = authHandlerDAOObj.getPreferences(requestParams);
            jobj = getPreferencesJson(kmsg.getEntityList(), request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    protected void setLocale(HttpServletRequest request, HttpServletResponse response, String newLocale) {
		if (newLocale != null) {
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			if (localeResolver == null) {
				logger.debug("No LocaleResolver found: not in a DispatcherServlet request?");
				return;
			}
			LocaleEditor localeEditor = new LocaleEditor();
			localeEditor.setAsText(newLocale);
			localeResolver.setLocale(request, response, (Locale) localeEditor.getValue());
		}
	}
}
