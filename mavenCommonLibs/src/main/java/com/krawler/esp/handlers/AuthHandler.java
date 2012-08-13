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
package com.krawler.esp.handlers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Session;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.ResetPassword;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.esp.web.resource.Links;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AuthHandler {
    public static void main(String[] str){
    }

    public static JSONObject verifyLogin(Session  session, String username, String passwd, String subdomain) throws ServiceException {
		JSONObject jobj = new JSONObject();
		try {
            String SELECT_USER_INFO="select u, u.userLogin, u.company from User as u where u.userLogin.userName = ? and u.userLogin.password = ? and u.company.deleted=0 and u.deleteflag = 0 and u.company.subDomain=?";
			List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[] {username, passwd, subdomain});
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                User user = (User) row[0];
                UserLogin userLogin = (UserLogin) row[1];
                Company company = (Company) row[2];
                jobj.put("success", true);
                jobj.put("lid", userLogin.getUserID());
                jobj.put("username", userLogin.getUserName());
                jobj.put("companyid", company.getCompanyID());
                jobj.put("company", company.getCompanyName());
                jobj.put("roleid", user.getRoleID());
                jobj.put("callwith", user.getCallwith());
                jobj.put("timeformat", user.getTimeformat());
                KWLTimeZone timeZone= user.getTimeZone();
                if(timeZone==null)timeZone=company.getTimeZone();
                if(timeZone==null)timeZone=(KWLTimeZone)session.load(KWLTimeZone.class, "1");
                jobj.put("timezoneid", timeZone.getTimeZoneID());
                jobj.put("tzdiff", timeZone.getDifference());
                jobj.put("tzid", timeZone.getTzID()); //@@hrms
                KWLDateFormat dateFormat= user.getDateFormat();
                if(dateFormat==null)dateFormat=(KWLDateFormat)session.load(KWLDateFormat.class, "1");
                jobj.put("dateformatid", dateFormat.getFormatID());
              //  jobj.put("dateformat", dateFormat.getJavaForm());
                KWLCurrency currency= company.getCurrency();
                if(currency==null)currency=(KWLCurrency)session.load(KWLCurrency.class, "1");
                jobj.put("currencyid", currency.getCurrencyID());
//                jobj.put("subscription",company.getSubscriptionCode()); //@@ for hrms

                String query = "select feature.featureName, permissionCode from UserPermission up where userLogin.userID=?";
                list = HibernateUtil.executeQuery(session, query, userLogin.getUserID());
                Iterator ite2 = list.iterator();
                JSONArray jarr = new JSONArray();
                while (ite2.hasNext()) {
                    JSONObject jo = new JSONObject();
                    Object[] roww = (Object[]) ite2.next();
                    jo.put(roww[0].toString(), roww[1]);
                    jarr.put(jo);
                }
                jobj.put("perms", jarr);

            } else {
                jobj.put("failure", true);
            }
        } catch (Exception e) {
                throw ServiceException.FAILURE("Auth.verifyLogin", e);
        } 
        return jobj;
    }
    
    public static JSONObject verifyLogin(Session session, String username,
			String subdomain) throws ServiceException {
    	JSONObject jobj = new JSONObject();
		try {
            String SELECT_USER_INFO="select u, u.userLogin, u.company from User as u where u.userLogin.userName = ? and u.company.deleted=0 and u.deleteflag = 0 and u.company.subDomain=?";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[] {username, subdomain});
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                User user = (User) row[0];
                UserLogin userLogin = (UserLogin) row[1];
                Company company = (Company) row[2];
                jobj.put("success", true);
                jobj.put("lid", userLogin.getUserID());
                jobj.put("username", userLogin.getUserName());
                jobj.put("companyid", company.getCompanyID());
                jobj.put("company", company.getCompanyName());
                jobj.put("callwith", user.getCallwith());
                jobj.put("timeformat", user.getTimeformat());
                jobj.put("roleid", user.getRoleID());
                KWLTimeZone timeZone= user.getTimeZone();
                if(timeZone==null)timeZone=company.getTimeZone();
                if(timeZone==null)timeZone=(KWLTimeZone)session.load(KWLTimeZone.class, "1");
                jobj.put("timezoneid", timeZone.getTimeZoneID());
                jobj.put("tzdiff", timeZone.getDifference());
                KWLDateFormat dateFormat= user.getDateFormat();
                if(dateFormat==null)dateFormat=(KWLDateFormat)session.load(KWLDateFormat.class, "1");
                jobj.put("dateformatid", dateFormat.getFormatID());
              //  jobj.put("dateformat", dateFormat.getJavaForm());
                KWLCurrency currency= company.getCurrency();
                if(currency==null)currency=(KWLCurrency)session.load(KWLCurrency.class, "1");
                jobj.put("currencyid", currency.getCurrencyID());

                String query = "select feature.featureName, permissionCode from UserPermission up where userLogin.userID=?";
                list = HibernateUtil.executeQuery(session, query, userLogin.getUserID());
                Iterator ite2 = list.iterator();
                JSONArray jarr = new JSONArray();
                while (ite2.hasNext()) {
                    JSONObject jo = new JSONObject();
                    Object[] roww = (Object[]) ite2.next();
                    jo.put(roww[0].toString(), roww[1]);
                    jarr.put(jo);
                }
                jobj.put("perms", jarr);
    
            } else {
                jobj.put("failure", true);
            }
        } catch (Exception e) {
                throw ServiceException.FAILURE("Auth.verifyLogin", e);
        }
        return jobj;
	}

    public static String getUserid(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"userid"), SessionExpiredException.USERID_NULL);
		return userId;
	}
    public static String getUser_hash(Session session, String userid)
			throws ServiceException {
		String res = "";
		try {
            JSONObject resObj = new JSONObject();
            User userObj = (User)session.load(User.class, userid);
            resObj.put("userhash", userObj.getUser_hash());
            resObj.put("subdomain", userObj.getCompany().getSubDomain());
            res = resObj.toString();
        } catch (JSONException e) {
            throw ServiceException.FAILURE("AuthHandler.getUser_hash", e);
        }
		return res;
	}

    public static String getUserCallWith(HttpServletRequest request)
			throws SessionExpiredException {
		String callwith = NullCheckAndThrow(request.getSession().getAttribute(
				"callwith"), SessionExpiredException.USERID_NULL);
		return callwith;
	}

    public static String getUserTimeFormat(HttpServletRequest request)
			throws SessionExpiredException {
		String timeformat = NullCheckAndThrow(request.getSession().getAttribute(
				"timeformat"), SessionExpiredException.USERID_NULL);
		return timeformat;
	}
     
	public static String getUserName(HttpServletRequest request)
			throws SessionExpiredException {
		String userName = NullCheckAndThrow(request.getSession().getAttribute(
				"username"), SessionExpiredException.USERNAME_NULL);
		return userName;
	}

    public static String getRole(HttpServletRequest request)
			throws SessionExpiredException {
		String roleid = NullCheckAndThrow(request.getSession().getAttribute(
				"roleid"), SessionExpiredException.USERID_NULL);
		return roleid;
	}

    public static Integer getPerms(HttpServletRequest request,String keyName)
			throws SessionExpiredException {
        long perl =0;
        int per=0;
        
        try{
            if(request.getSession().getAttribute(keyName) != null) {
                perl =  (Long) request.getSession().getAttribute(keyName);
                }
            per = (int) perl;
          //  per = Integer.parseInt(perm);
        }
        catch(Exception e){
            per = 0;
        }
		return per;
	}

    public static String getCompanyid(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"companyid"), SessionExpiredException.USERID_NULL);
		return userId;
	}

    public static String getSubDomain(HttpServletRequest request)
			throws SessionExpiredException {
        String subdomain = "";
        Session  session = null;
        String companyid = NullCheckAndThrow(request.getSession().getAttribute(
				"companyid"), SessionExpiredException.USERID_NULL);
        try {
            session = HibernateUtil.getCurrentSession();
            Company company = (Company) session.get(Company.class,companyid);
            subdomain = company.getSubDomain();
        } catch (Exception e) {
            HibernateUtil.closeSession(session);
        }
        
		return subdomain;
	}

    public static String getCompanyid(Session session, String domain) {
        String companyID=null;
		try {
            String query="select companyID from Company where subDomain = ?";
            List list = HibernateUtil.executeQuery(session, query, domain);
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                companyID = (String)ite.next();
            }
        } catch (Exception e) {}
        return companyID;
	}

	public static String getCompanyName(HttpServletRequest request)
			throws SessionExpiredException {
		String userName = NullCheckAndThrow(request.getSession().getAttribute(
				"company"), SessionExpiredException.USERNAME_NULL);
		return userName;
	}
    private static String NullCheckAndThrow(Object objToCheck, String errorCode)
			throws SessionExpiredException {
		if (objToCheck != null) {
			String oStr = objToCheck.toString();
			if (!StringUtil.isNullOrEmpty(oStr))
				return oStr;
		}
		throw new SessionExpiredException("Session Invalidated", errorCode);
	}

    public static String getTimeZoneID(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"timezoneid"), SessionExpiredException.USERID_NULL);
		return userId;
	}
   public static String getTimeZoneDifference(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"tzdiff"), SessionExpiredException.USERID_NULL);
		return userId;
    }

   public static String getDateFormatID(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"dateformatid"), SessionExpiredException.USERID_NULL);
		return userId;
	}

   	public static String getDateFormat(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"dateformat"), SessionExpiredException.USERID_NULL);
		return userId;
	}

   public static DateFormat getUserDateFormatter(HttpServletRequest request, Session session)
			throws SessionExpiredException {
        KWLDateFormat df=(KWLDateFormat)session.load(KWLDateFormat.class, getDateFormatID(request));
        String dateformat="";
        String timeformat=AuthHandler.getUserTimeFormat(request);
        if(timeformat.equals("1")) {
            dateformat=df.getJavaForm().replace('H', 'h');
            if(!dateformat.equals(df.getJavaForm()))
                dateformat+=" a";
        } else
            dateformat=df.getJavaForm();
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+getTimeZoneDifference(request)));
        return sdf;
    }
   
   public static DateFormat getDateFormatter(HttpServletRequest request)
        throws SessionExpiredException {
        String dateformat="";
        String timeformat=AuthHandler.getUserTimeFormat(request);
        if(timeformat.equals("1")) {
            dateformat="MMMM d, yyyy hh:mm:ss aa";
        } else
            dateformat="MMMM d, yyyy HH:mm:ss";
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+getTimeZoneDifference(request)));
        return sdf;
    }
   public static DateFormat getDateFormatterforOnlyDate(HttpServletRequest request)
        throws SessionExpiredException {
        String dateformat="";
        String timeformat=AuthHandler.getUserTimeFormat(request);
        dateformat="MMMM d, yyyy";
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+getTimeZoneDifference(request)));
        return sdf;
    }

   public static DateFormat getPrefDateFormatter(HttpServletRequest request,String pref)
        throws SessionExpiredException {
        String dateformat="";
        String timeformat=AuthHandler.getUserTimeFormat(request);
        if(timeformat.equals("1")) {
            dateformat=pref.replace('H', 'h');
            if(!dateformat.equals(pref))
                dateformat+=" a";
        } else
            dateformat=pref;
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+getTimeZoneDifference(request)));
        return sdf;
    }

   public static DateFormat getDateMDYFormatter(HttpServletRequest request)
			throws SessionExpiredException {
        return new SimpleDateFormat("MMMM d, yyyy");
   }

    public static DateFormat getNewDateFormatter(HttpServletRequest request)
			throws SessionExpiredException {
        return new SimpleDateFormat("yyyy-MM-dd");
   }
    
   public static DateFormat getTimeFormatter(HttpServletRequest request)
			throws SessionExpiredException {
       String dateformat="";
        String timeformat=AuthHandler.getUserTimeFormat(request);
        if(timeformat.equals("1"))
            dateformat=" hh:mm:ss aa ";
        else
            dateformat="HH:mm:ss";
        return new SimpleDateFormat(dateformat);
   }

//   public static String getRoleId(Session session, String userid)
//			throws SessionExpiredException {
//        User u=(User)session.load(User.class, userid);
//        return u.getRole().getID();
//	}

   public static JSONArray getCompanyPreferences(Session session, HttpServletRequest request)
			throws ServiceException,JSONException,SessionExpiredException {
		JSONArray preferences = new JSONArray();
		try {
			JSONObject obj = new JSONObject();
            String cmpid=AuthHandler.getCompanyid(request);
            CompanyPreferences cmpPref=null;
			String query="from CompanyPreferences where company.companyID=?";
            List   tabledata = HibernateUtil.executeQuery(session, query,cmpid );
            if(tabledata.size()>0){
                cmpPref=(CompanyPreferences)session.get(CompanyPreferences.class,cmpid);
                obj.put("selfapp",cmpPref.isSelfappraisal());
                obj.put("competency",cmpPref.isCompetency());
                obj.put("goal",cmpPref.isGoal());
                obj.put("annmng",cmpPref.isAnnmanager());
                obj.put("approveappraisal",cmpPref.isApproveappraisal());
                obj.put("promotionrec",cmpPref.isPromotion());
                obj.put("weightage",cmpPref.isWeightage());
                obj.put("reviewappraisal",cmpPref.isReviewappraisal());
                obj.put("partial",cmpPref.isPartial());
                obj.put("fullupdates",cmpPref.isFullupdates());
                obj.put("modaverage",cmpPref.isModaverage());
                }else{
                obj.put("selfapp",false);
                obj.put("competency",false);
                obj.put("goal",false);
                obj.put("annmng",false);
                obj.put("approveappraisal",false);
                obj.put("promotionrec",false);
                obj.put("weightage",false);
                obj.put("reviewappraisal",false);
                obj.put("partial",false);
                obj.put("fullupdates",false);
                obj.put("modaverage",false);
                }
			preferences.put(obj);
		} catch (com.krawler.utils.json.base.JSONException e) {
			throw ServiceException.FAILURE("Auth.getCompanyPreferences", e);
		} catch (SessionExpiredException e) {
			throw ServiceException.FAILURE("Auth.getCompanyPreferences", e);
		}
		return preferences;
	}

   public static String getCurrencyID(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"currencyid"), SessionExpiredException.USERID_NULL);
		return userId;
	}

    public static String generateNewPassword() {
            return RandomStringUtils.random(8, true, true);
    }

	public static String getSHA1(String inStr) throws ServiceException {
		String outStr = inStr;
		try {
			byte[] theTextToDigestAsBytes = inStr.getBytes("utf-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] digest = sha.digest(theTextToDigestAsBytes);

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				String h = Integer.toHexString(b & 0xff);
				if (h.length() == 1) {
					sb.append("0" + h);
				} else {
					sb.append(h);
				}
			}
			outStr = sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw ServiceException.FAILURE("Auth.getSHA1", e);
		} catch (NoSuchAlgorithmException e) {
			throw ServiceException.FAILURE("Auth.getSHA1", e);
		}
		return outStr;
	}



	public static JSONArray getPreferences(Session session, HttpServletRequest request)
			throws ServiceException {
		JSONArray preferences = new JSONArray();
		try {
				JSONObject j = new JSONObject();
                String dateformat="";
                String timeformat=AuthHandler.getUserTimeFormat(request);
                KWLTimeZone timeZone = (KWLTimeZone)session.load(KWLTimeZone.class, getTimeZoneID(request));
                KWLDateFormat dateFormat = (KWLDateFormat)session.load(KWLDateFormat.class, getDateFormatID(request));
                KWLCurrency currency = (KWLCurrency)session.load(KWLCurrency.class, getCurrencyID(request));
                j.put("Timezone", timeZone.getName());
				j.put("Timezoneid", timeZone.getTimeZoneID());
				j.put("Timezonediff", timeZone.getDifference());
                if(timeformat.equals("1")) {
                    dateformat=dateFormat.getScriptForm().replace('H', 'h');
                    if(!dateformat.equals(dateFormat.getScriptForm()))
                        dateformat+=" T";
                } else
                    dateformat=dateFormat.getScriptForm();
				j.put("DateFormat", dateformat);
                j.put("DateFormatid",dateFormat.getFormatID());
                j.put("seperatorpos",dateFormat.getScriptSeperatorPosition());
				j.put("Currency", currency.getHtmlcode());
				j.put("CurrencyName", currency.getName());
				j.put("CurrencySymbol", currency.getSymbol());
                j.put("Currencyid",currency.getCurrencyID());
				preferences.put(j);
        } catch (Exception e) {
                throw ServiceException.FAILURE("Auth.getPreferences", e);
        }
		return preferences;
	}

    public static String getFullName(User user) {
        String fullname = user.getFirstName();
        if(fullname!=null && user.getLastName()!=null) fullname+=" "+user.getLastName();
        if (StringUtil.isNullOrEmpty(user.getFirstName()) && StringUtil.isNullOrEmpty(user.getLastName())) {
            fullname = user.getUserLogin().getUserName();
        }
        return fullname;
	}

    public static String getFullName(Session session, String userid)
			throws SessionExpiredException {
        return getFullName((User)session.load(User.class, userid));
	}

   //override function to support spring + hibernate
   public static DateFormat getUserDateFormatter(HttpServletRequest request, HibernateTemplate hibernateTemplate)
			throws SessionExpiredException {
        KWLDateFormat df=(KWLDateFormat)hibernateTemplate.load(KWLDateFormat.class, getDateFormatID(request));
        String dateformat="";
        String timeformat=AuthHandler.getUserTimeFormat(request);
        if(timeformat.equals("1")) {
            dateformat=df.getJavaForm().replace('H', 'h');
            if(!dateformat.equals(df.getJavaForm()))
                dateformat+=" a";
        } else
            dateformat=df.getJavaForm();
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+getTimeZoneDifference(request)));
        return sdf;
    }

	public static boolean  chkHeirarchyPerm(HttpServletRequest req, String moduleName)
			throws ServiceException,JSONException,SessionExpiredException {
		boolean permsion=false;
		try {
			JSONObject obj = new JSONObject();
            String cmppref = req.getSession().getAttribute("companyPreferences").toString();
            JSONObject jsnObj = new JSONObject(cmppref);
            if(StringUtil.equal(moduleName, "Lead")){
               permsion = jsnObj.getBoolean("lead");
            } else if(StringUtil.equal(moduleName, "Account")){
               permsion = jsnObj.getBoolean("account");
            } else if(StringUtil.equal(moduleName, "Contact")){
               permsion = jsnObj.getBoolean("contact");
            } else if(StringUtil.equal(moduleName, "Opportunity")){
               permsion = jsnObj.getBoolean("opportunity");
            } else if(StringUtil.equal(moduleName, "Cases")){
               permsion = jsnObj.getBoolean("cases");
            } else if(StringUtil.equal(moduleName, "Product")){
               permsion = jsnObj.getBoolean("product");
            } else if(StringUtil.equal(moduleName, "Activity")){
               permsion = jsnObj.getBoolean("activity");
            } else if(StringUtil.equal(moduleName, "Campaign")){
               permsion = jsnObj.getBoolean("campaign");
            }
            
		} catch (com.krawler.utils.json.base.JSONException e) {
			throw ServiceException.FAILURE("Auth.chkHeirarchyPerm", e);
		}
		return permsion;
	}
 public static String getTZID(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"tzid"), SessionExpiredException.USERID_NULL);
		return userId;
	}

       public static String getCmpSubscription(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"cmpsubscription"), SessionExpiredException.USERID_NULL);
		return userId;
	}

    
}
