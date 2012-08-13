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
package com.krawler.common.util;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import org.hibernate.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;

public class Timezone {
    
    public static Date getGmtDate() throws ServiceException {
        Date cmpdate = new Date();
        try{
            Calendar calInstance = Calendar.getInstance();
            int gmtoffset =calInstance.get(Calendar.DST_OFFSET)
                                +calInstance.get(Calendar.ZONE_OFFSET);
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm a");
            String cmp = sdf.format(new java.util.Date(date));
            cmpdate = new Date(sdf.parse(cmp).getTime()-gmtoffset);
        } catch(ParseException e){
            throw ServiceException.FAILURE("Timezone.getGmtDate", e);
        }
        return cmpdate;
    }
    
    public static java.sql.Timestamp getGmtTimestamp() throws ServiceException{
        java.util.Date post_time = getGmtDate();
        java.sql.Timestamp sqlPostDate = new java.sql.Timestamp(post_time.getTime());
        return sqlPostDate;
    }

    // used to store user date to db date
    public static String getUserToGmtTimezone(Session session,HttpServletRequest request, String userid, String date) throws ServiceException{
        String userDate = "";
        String timezone = getTimeZone(session, request, userid);
        if(timezone.startsWith("-"))
              timezone = "+"+timezone.substring(1);
        else if(timezone.startsWith("+"))
              timezone = "-"+timezone.substring(1);
        userDate = toUserDefTimezone(session, date, timezone);//user to gmt
        userDate = toUserDefTimezone(session, userDate, getSystemTimezone(session));// gmt to db
        return userDate;
    }

    public static String toUserTimezone(Session session,HttpServletRequest request, String date, String userid) throws ServiceException {
        String result = "";
        String hql = "select DATE_FORMAT(CONVERT_TZ('"+ date +"', '+00:00' , '"+ Timezone.getTimeZone(session, request, userid) +"'),'%Y-%m-%d %H:%i:%S') " +
                "as time_convt; ";
        Query query = session.createSQLQuery(hql);
        List ls = query.list();
        Iterator ie = ls.iterator();
        if(ie.hasNext()) {
            result = ie.next().toString();
        }
        return result;
    }

    // consider system timezone
    public static String toUserSystemTimezone(Session session,HttpServletRequest request, String date, String userid) throws ServiceException {
        String result = "";
        String hql = "select DATE_FORMAT(CONVERT_TZ('"+ date +"', '"+getSystemTimezone(session)+"', '"+ Timezone.getTimeZone(session,request, userid) +"'),'%Y-%m-%d %H:%i:%S') " +
                "as time_convt; ";
        Query query = session.createSQLQuery(hql);
        List ls = query.list();
        Iterator ie = ls.iterator();
        if(ie.hasNext()) {
            result = ie.next().toString();
        }
        return result;
    }
    
    // consider system timezone
    public static Date toUserSystemTimezoneDate(Session session,HttpServletRequest request, String date, String userid) throws ServiceException, SessionExpiredException {
        String result = "";
        java.util.Date d = new java.util.Date();
        try {
            String hql = "select DATE_FORMAT(CONVERT_TZ('"+ date +"', '"+getSystemTimezone(session)+"', '"+ Timezone.getTimeZone(session,request, userid) +"'),'%Y-%m-%d %H:%i:%S') " +
                    "as time_convt; ";
            Query query = session.createSQLQuery(hql);
            List ls = query.list();
            Iterator ie = ls.iterator();
            if(ie.hasNext()) {
                result = ie.next().toString();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                d = sdf.parse(result);
            }
        } catch(ParseException ex) {
            ServiceException.FAILURE(Timezone.class.getName(), ex);
        }
        return d;
    }


    public static Date toUserTimezoneDate(Session session,HttpServletRequest request, String date, String userid) throws ServiceException {
        java.util.Date d = new java.util.Date();
        try {
            String hql = "select DATE_FORMAT(CONVERT_TZ('"+ date +"', '+00:00' , '"+ Timezone.getTimeZone(session, request, userid) +"'),'%Y-%m-%d %H:%i:%S') " +
                    "as time_convt; ";
            Query query = session.createSQLQuery(hql);
            List ls = query.list();
            Iterator ie = ls.iterator();
            if(ie.hasNext()) {
                String result = ie.next().toString();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                d = sdf.parse(result);
            }
        } catch(ParseException ex) {
            ServiceException.FAILURE(Timezone.class.getName(), ex);
        }
        return d;
    }

    public static String toUserDefTimezone(Session session, String date, String tz) throws ServiceException {
        if(tz.compareTo("")==0)
            tz = Timezone.getSystemTimezone(session);
        String result = "";
        String hql = "select DATE_FORMAT(CONVERT_TZ('"+ date +"', '+00:00' , '"+ tz +"'),'%Y-%m-%d %H:%i:%S') " +
                "as time_convt; ";
        Query query = session.createSQLQuery(hql);
        List ls = query.list();
        Iterator ie = ls.iterator();
        if(ie.hasNext()) {
//             Object[] row = (Object[]) ie.next();
            result = ie.next().toString();
        }
        return result;
    }

    public static String getSystemTimezone(Session session) throws ServiceException{
        String hql = "select difference from systemtimezone";
        String diff = null;
        Query query = session.createSQLQuery(hql);
        List ls = query.list();
        Iterator ie = ls.iterator();
        if(ie.hasNext()) {
            diff = ie.next().toString();
        }
        return diff;
    }

    public static String getTimeZone(Session session,HttpServletRequest request, String uid) throws ServiceException {
        String time = "";
        try {
            String tzid = AuthHandler.getTZID(request);
            Calendar calTZ = Calendar.getInstance(TimeZone.getTimeZone(tzid));
            TimeZone tz = calTZ.getTimeZone();
            int miliseconds = tz.getOffset(calTZ.getTimeInMillis());
            
            String signNum = "+";

            if(miliseconds < 0) {
                miliseconds = miliseconds * (-1);
                signNum = "-";
            }

            int seconds = miliseconds/1000;

            String min = "";        // Calculating minutes
            if(seconds > 60) {
                min = String.valueOf(seconds/60%60);
                if((seconds/60%60)<10) {
                    min = "0" + String.valueOf(seconds/60%60);
                }

                if(min.compareTo("0") != 0 && min.compareTo("30") != 0) {       // Checking whether the minimum unit of minutes is 30 mins.
                    if(min.compareTo("45") == 0) {
                        min = "30";

                    } else {
                        min = "00";
                    }
                }

            } else {
                min = "00";
            }

            String hours = "";      // Calculating hours

            if(seconds/60 > 60) {
                hours = String.valueOf(seconds/60/60);
                if((seconds/60/60) < 10) {
                    hours = "0" + String.valueOf(seconds/60/60);
                }

            } else {
               hours = "00";
            }
            time = signNum + hours + ":" + min;
            
        } catch(SessionExpiredException ex) {
            ServiceException.FAILURE(Timezone.class.getName(), ex);
        }
        
        return time;
    }

    public static Date toCompanySystemTimezoneDate(Session session,HttpServletRequest request, String date, String companyid) throws ServiceException, SessionExpiredException {
        String result = "";
        java.util.Date d = new java.util.Date();
        try {
            String hql = "select DATE_FORMAT(CONVERT_TZ('"+ date +"', '"+getSystemTimezone(session)+"', '"+ Timezone.getCompanyTimeZone(session,request, companyid) +"'),'%Y-%m-%d %H:%i:%S') " +
                    "as time_convt; ";
            Query query = session.createSQLQuery(hql);
            List ls = query.list();
            Iterator ie = ls.iterator();
            if(ie.hasNext()) {
                result = ie.next().toString();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                d = sdf.parse(result);
            }
        } catch(ParseException ex) {
            ServiceException.FAILURE(Timezone.class.getName(), ex);
        }
        return d;
    }

    public static String getCompanyTimeZone(Session session,HttpServletRequest request, String companyid) throws ServiceException {
        String time = "";
        try {
            String tzid;
            String query = "select tzID from KWLTimeZone where timeZoneID=(select timeZone from Company where companyID = ?)";
            List list = HibernateUtil.executeQuery(session, query, companyid);
            Iterator ite = list.iterator();
            if (ite.hasNext()) {
                tzid=(String) ite.next();
            } else {
                tzid="America/New_York";
            }

            Calendar calTZ = Calendar.getInstance(TimeZone.getTimeZone(tzid));
            TimeZone tz = calTZ.getTimeZone();
            int miliseconds = tz.getOffset(calTZ.getTimeInMillis());

            String signNum = "+";

            if(miliseconds < 0) {
                miliseconds = miliseconds * (-1);
                signNum = "-";
            }

            int seconds = miliseconds/1000;

            String min = "";        // Calculating minutes
            if(seconds > 60) {
                min = String.valueOf(seconds/60%60);
                if((seconds/60%60)<10) {
                    min = "0" + String.valueOf(seconds/60%60);
                }

                if(min.compareTo("0") != 0 && min.compareTo("30") != 0) {       // Checking whether the minimum unit of minutes is 30 mins.
                    if(min.compareTo("45") == 0) {
                        min = "30";

                    } else {
                        min = "00";
                    }
                }

            } else {
                min = "00";
            }

            String hours = "";      // Calculating hours

            if(seconds/60 > 60) {
                hours = String.valueOf(seconds/60/60);
                if((seconds/60/60) < 10) {
                    hours = "0" + String.valueOf(seconds/60/60);
                }

            } else {
               hours = "00";
            }
            time = signNum + hours + ":" + min;

        } catch (Exception e) {
            System.out.println("Exception is "+e.toString());
        }
        return time;
    }
}
