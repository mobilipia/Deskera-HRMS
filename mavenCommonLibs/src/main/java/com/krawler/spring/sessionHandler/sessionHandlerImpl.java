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
package com.krawler.spring.sessionHandler;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author karthik
 */
public class sessionHandlerImpl {

    private sessionHandlerImpl sessionHandlerImplObj;

    public sessionHandlerImpl(){
	}

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public static boolean isValidSession(HttpServletRequest request,
            HttpServletResponse response) {
        boolean bSuccess = false;
        try {
            if (request.getSession().getAttribute("initialized") != null) {
                bSuccess = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bSuccess;
    }

    public void updatePreferences(HttpServletRequest request,
            String currencyid, String dateformatid, String timezoneid,
            String tzdiff) {
        if (currencyid != null) {
            request.getSession().setAttribute("currencyid", currencyid);
        }
        if (timezoneid != null) {
            request.getSession().setAttribute("timezoneid", timezoneid);
            request.getSession().setAttribute("tzdiff", tzdiff);
        }
        if (dateformatid != null) {
            request.getSession().setAttribute("dateformatid", dateformatid);
        }
    }

    /* Update date preference only. */
    public void updateDatePreferences(HttpServletRequest request, String dateformatid) {
        if (dateformatid != null) {
            request.getSession().setAttribute("dateformatid", dateformatid);
        }
    }

    /* Time Format included here. */
    public void updatePreferences(HttpServletRequest request,
            String currencyid, String dateformatid, String timezoneid,
            String tzdiff, String timeformat) {
        if (currencyid != null) {
            request.getSession().setAttribute("currencyid", currencyid);
        }
        if (timezoneid != null) {
            request.getSession().setAttribute("timezoneid", timezoneid);
            request.getSession().setAttribute("tzdiff", tzdiff);
        }
        if (dateformatid != null) {
            request.getSession().setAttribute("dateformatid", dateformatid);
        }
        if (timeformat != null) {
            request.getSession().setAttribute("timeformat", timeformat);
        }
    }

    public boolean validateSession(HttpServletRequest request,
            HttpServletResponse response) {
        return sessionHandlerImpl.isValidSession(request, response);
    }

    public void createUserSession(HttpServletRequest request, JSONObject jObj) throws ServiceException {
        HttpSession session = request.getSession(true);
        try {
            if(!jObj.isNull("username"))
                session.setAttribute("username", jObj.getString("username"));
            if(!jObj.isNull("lid"))
                session.setAttribute("userid", jObj.getString("lid"));
            if(!jObj.isNull("companyid"))
                session.setAttribute("companyid", jObj.getString("companyid"));
            if(!jObj.isNull("company"))
                session.setAttribute("company", jObj.getString("company"));
            if(!jObj.isNull("timezoneid"))
                session.setAttribute("timezoneid", jObj.getString("timezoneid"));
            if(!jObj.isNull("tzdiff"))
                session.setAttribute("tzdiff", jObj.getString("tzdiff"));
            if(!jObj.isNull("dateformatid"))
                session.setAttribute("dateformatid", jObj.getString("dateformatid"));
            if(!jObj.isNull("currencyid"))
                session.setAttribute("currencyid", jObj.getString("currencyid"));
            if(!jObj.isNull("callwith"))
                session.setAttribute("callwith", jObj.getString("callwith"));
            if(!jObj.isNull("timeformat"))
                session.setAttribute("timeformat", jObj.getString("timeformat"));
            if(!jObj.isNull("companyPreferences"))
                session.setAttribute("companyPreferences", jObj.getString("companyPreferences"));
            if(!jObj.isNull("roleid"))
                session.setAttribute("roleid", jObj.getString("roleid"));
            session.setAttribute("initialized", "true");
            if(!jObj.isNull("subscription"))
                session.setAttribute("cmpsubscription", jObj.getString("subscription"));  //@@ for hrms
            if(!jObj.isNull("tzid"))
                session.setAttribute("tzid", jObj.getString("tzid"));  //@@ for hrms
            if(!jObj.isNull("sysemailid"))
                session.setAttribute("sysemailid", jObj.getString("sysemailid"));

            JSONArray jarr = null;
            if(!jObj.isNull("perms")){
                jarr = jObj.getJSONArray("perms");
                for (int l = 0; l < jarr.length(); l++) {
                    String keyName = jarr.getJSONObject(l).names().get(0)
                            .toString();
                    session.setAttribute(keyName, jarr.getJSONObject(l)
                            .get(keyName));
                }
            }
        } catch (JSONException e) {
            throw ServiceException.FAILURE("sessionHandlerImpl.createUserSession", e);
        }
    }

    public void destroyUserSession(HttpServletRequest request,
            HttpServletResponse response) {
        request.getSession().invalidate();
    }

     public String getUserid(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                "userid"), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getTimeZoneID(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                "timezoneid"), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getTimeZoneDifference(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                "tzdiff"), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getUserCallWith(HttpServletRequest request)
            throws SessionExpiredException {
        String callwith = NullCheckAndThrow(request.getSession().getAttribute(
                "callwith"), SessionExpiredException.USERID_NULL);
        return callwith;
    }

    public String getUserTimeFormat(HttpServletRequest request)
            throws SessionExpiredException {
        String timeformat = NullCheckAndThrow(request.getSession().getAttribute(
                "timeformat"), SessionExpiredException.USERID_NULL);
        return timeformat;
    }

    public String getUserName(HttpServletRequest request)
            throws SessionExpiredException {
        String userName = NullCheckAndThrow(request.getSession().getAttribute(
                "username"), SessionExpiredException.USERNAME_NULL);
        return userName;
    }

    public JSONArray getcompanypreferences(HttpServletRequest request)
            throws SessionExpiredException,JSONException {
        JSONArray cmppref = new JSONArray(request.getSession().getAttribute("companyPreferences").toString());
        return cmppref;
    }

    public String getRole(HttpServletRequest request)
            throws SessionExpiredException {
        String roleid = NullCheckAndThrow(request.getSession().getAttribute(
                "roleid"), SessionExpiredException.USERID_NULL);
        return roleid;
    }

    public String getDateFormatID(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                "dateformatid"), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getCompanyid(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                "companyid"), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getCompanyName(HttpServletRequest request)
            throws SessionExpiredException {
        String userName = NullCheckAndThrow(request.getSession().getAttribute(
                "company"), SessionExpiredException.USERNAME_NULL);
        return userName;
    }

    public String getCurrencyID(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"currencyid"), SessionExpiredException.USERID_NULL);
		return userId;
	}
    
    public Integer getPerms(HttpServletRequest request, String keyName)
            throws SessionExpiredException {
        long perl = 0;
        int per = 0;
        try {
            if (request.getSession().getAttribute(keyName) != null) {
                perl = (Long) request.getSession().getAttribute(keyName);
            }
            per = (int) perl;
        } catch (Exception e) {
            per = 0;
        }
        return per;
    }

    public static String NullCheckAndThrow(Object objToCheck, String errorCode)
            throws SessionExpiredException {
        if (objToCheck != null) {
            String oStr = objToCheck.toString();
            if (!StringUtil.isNullOrEmpty(oStr)) {
                return oStr;
            }
        }
        throw new SessionExpiredException("Session Invalidated", errorCode);
    }

    public DateFormat getDateFormatter(HttpServletRequest request)
        throws SessionExpiredException {
        String dateformat="";
        String timeformat=getUserTimeFormat(request);
        if(timeformat.equals("1")) {
            dateformat="MMMM d, yyyy hh:mm:ss aa";
        } else
            dateformat="MMMM d, yyyy HH:mm:ss";
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+getTimeZoneDifference(request)));
        return sdf;
    }

    public String getCmpSubscription(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"cmpsubscription"), SessionExpiredException.USERID_NULL);
		return userId;
	}
    
    @SuppressWarnings("finally")
	public String getUserFullName(User user){
    	String name =null;
    	try{
    		name = user!=null?(user.getFirstName()+" "+(user.getLastName()!=null?user.getLastName():"")):"";
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return name;
    	}
    }
    
    public String getSysEmailIdByCompanyID(HttpServletRequest request) throws SessionExpiredException {
    	String sysemailid = NullCheckAndThrow(request.getSession().getAttribute("sysemailid"), SessionExpiredException.USERID_NULL);
    	return sysemailid;
    }
}
