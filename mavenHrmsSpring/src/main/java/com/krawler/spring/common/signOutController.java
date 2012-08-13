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

package com.krawler.spring.common;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.web.resource.Links;
import com.krawler.common.util.URLUtil;
import java.net.URLEncoder;
import com.krawler.common.util.AuditAction;
import com.krawler.esp.handlers.SessionHandler;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
/**
 *
 * @author krawler
 */
public class signOutController extends MultiActionController{
    private auditTrailDAO auditTrailDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }



    
    public ModelAndView signOut(HttpServletRequest request, HttpServletResponse response){
        SessionHandler sessionbean = new SessionHandler();
        String _sO = request.getParameter("type");
        String uri = URLUtil.getPageURL(request, Links.loginpageFull);
        String redirectUri = "";
        try {

            if (request.getSession().getAttribute("initialized") != null) {
            User user = (User)kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", sessionHandlerImplObj.getUserid(request));
            auditTrailDAOObj.insertAuditLog(AuditAction.LOGOUT_SUCCESS, "User "
                    + StringUtil.getFullName(user)
                    + " has logged out"
                    + (!StringUtil.isNullOrEmpty(_sO) ? (" due to " + _sO)
                            : ""), request,"0");
            }


        String logoutUrl = this.getServletContext().getInitParameter(
                "casServerLogoutUrl");
        if (StringUtil.isNullOrEmpty(logoutUrl)) {
            redirectUri = uri + "login.html";
            if (!StringUtil.isNullOrEmpty(_sO)) {
                redirectUri += ("?" + _sO);
            }
        } else {
            String subdomain = URLUtil.getDomainName(request);
            redirectUri = logoutUrl
                    + String.format("?url=%s&subdomain=%s", URLEncoder
                            .encode(uri, "UTF-8"), subdomain, _sO);
            if (!StringUtil.isNullOrEmpty(_sO)) {
                redirectUri += ("&type=" + _sO);
            }
        }
        sessionbean.destroyUserSession(request, response);
        response.sendRedirect(redirectUri);
        } catch (Exception exc) {
            exc.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model","");
        }
    }
}
