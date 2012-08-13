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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Karthik
 */
public class kwlCommonTablesDAOImpl implements kwlCommonTablesDAO{

    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public Object getObject(String classpath, String id) throws ServiceException {
        List list = new ArrayList();
        Object obj = null;
        try {
            Class cls = Class.forName(classpath);
            obj = hibernateTemplate.get(cls, id);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return obj;
    }

    public Object getClassObject(String classpath, String id) throws ServiceException {
        Object obj = null;
        try {
            Class cls = Class.forName(classpath);
            obj = hibernateTemplate.get(cls, id);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return obj;
    }

    public static Object getObject(HibernateTemplate hibernateTemplate, String classpath, String id) throws ServiceException {
        Object obj = null;
        try {
            Class cls = Class.forName(classpath);
            obj = hibernateTemplate.get(cls, id);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return obj;
    }

    public KwlReturnObject getAllTimeZones() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from KWLTimeZone order by sortOrder";
            ll = HibernateUtil.executeQuery(hibernateTemplate, query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllTimeZones", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAllCurrencies() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from KWLCurrency";
            ll = HibernateUtil.executeQuery(hibernateTemplate, query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllCurrencies", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAllCountries() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from Country";
            ll = HibernateUtil.executeQuery(hibernateTemplate, query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllCountries", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAllDateFormats() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from KWLDateFormat";
            ll = HibernateUtil.executeQuery(hibernateTemplate, query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getCompanyInformation", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public DateFormat getUserDateFormatter(String dateFormatId, String userTimeFormatId, String timeZoneDiff) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            KWLDateFormat df = (KWLDateFormat) hibernateTemplate.load(KWLDateFormat.class, dateFormatId);
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = df.getJavaForm().replace('H', 'h');
                if (!dateformat.equals(df.getJavaForm())) {
                    dateformat += " a";
                }
            } else {
                dateformat = df.getJavaForm();
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getUserDateFormatter", e);
        }
        return sdf;
    }

    public String currencyRender(String currency, String currencyid) throws SessionExpiredException {
        KWLCurrency cur = (KWLCurrency) hibernateTemplate.load(KWLCurrency.class, currencyid);
        String symbol = cur.getHtmlcode();
        char temp = (char) Integer.parseInt(symbol, 16);
        symbol = Character.toString(temp);
        float v = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        if (currency.equals("")) {
            return symbol;
        }
        v = Float.parseFloat(currency);
        String fmt = decimalFormat.format(v);
        fmt = symbol + fmt;
        return fmt;
    }

 public Date toUserSystemTimezoneDate(HttpServletRequest request, String date, String userid){
        String result = "";
        java.util.Date d = new java.util.Date();
        try {
            String usertz = getTimeZone(request, userid);
            boolean is14 = false;
            if(usertz.equals("+14:00")) {
                is14 = true;
                usertz = "+13:00";
            }
            String hql = "select DATE_FORMAT(CONVERT_TZ('"+ date +"', '"+getSystemTimezone()+"', '"+ usertz +"'),'%Y-%m-%d %H:%i:%S') "+
                    "as time_convt from User";
            List ls = HibernateUtil.executeQuery(hibernateTemplate, hql);
            Iterator ie = ls.iterator();
            if(ie.hasNext()) {
                result = ie.next().toString();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                d = sdf.parse(result);
                if(is14) {
                    d.setHours(d.getHours()+1);
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return d;
    }

    public String getSystemTimezone(){
        String hql = "";
        String diff = null;
        try{
            hql = "select difference from SystemTimeZone";
            List ls = HibernateUtil.executeQuery(hibernateTemplate, hql);
            Iterator ie = ls.iterator();
            if(ie.hasNext()) {
                diff = ie.next().toString();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            return diff;
        }
    }
    
    public String getTimeZone(HttpServletRequest request, String uid){
        String time = "";
        try {
            String tzid = AuthHandler.getTZID(request); // @@ to be changed. pass value from controller
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

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return time;
    }

// @@ to be removed from here. Change ref.
    public String getEmpidFormatEdit(HttpServletRequest request, Integer empid) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject obj = new JSONObject();
        int count = 0;
        String mainstr = "";
        String empids = "";
        String maxcount = null;
        try {

            String cmpnyid = AuthHandler.getCompanyid(request);  // @@ to be changed pass value from controller
            String SELECT_USER_INFO = "from CompanyPreferences where companyID=?";
            List list = HibernateUtil.executeQuery(hibernateTemplate, SELECT_USER_INFO, cmpnyid);
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                CompanyPreferences cmp = (CompanyPreferences) itr.next();
                String empidformat = cmp.getEmpidformat() == null ? "" : cmp.getEmpidformat();
                String[] row = (empidformat).split("-");
                if (row.length == 1) {
                    if ((row[0]).startsWith("0")) {
                        int len = row[0].length();
                        String regex = "%0" + len + "d";
                        empids = String.format(regex, empid);
                        mainstr = empids;
                    } else {
                        mainstr = empid.toString();
                    }

                } else {
                    for (int i = 0; i < row.length; i++) {

                        if ((row[i]).startsWith("0")) {
                            int len = row[i].length();
                            String regex = "%0" + len + "d";
                            empids = String.format(regex, empid);

                        }
                        if (!row[i].equals("")) {
                            if (!(row[i]).startsWith("0")) {
                                mainstr = mainstr + row[i] + "-";
                            } else {
                                mainstr = mainstr + empids + "-";
                            }
                        } else {
                            mainstr = mainstr + empids + "-";

                        }
                    }
                    mainstr = mainstr.substring(0, mainstr.length() - 1);
                }
            }
            obj.put("maxempid", mainstr);
            jarr.put(obj);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainstr;
    }

    public JSONArray getDetailsJson(List ll, int index, String classstr) {
        JSONArray jarr = new JSONArray();
        JSONObject obj = new JSONObject();
        Object user = null;
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                if(index==-1)
                     user =  ite.next();
                else{
                    Object[] row =(Object[]) ite.next();
                    user =  row[index];
                }

                if(!user.getClass().getName().equals(classstr)){
                    user = hibernateTemplate.get(classstr, user.toString());
                }
                Class cl = Class.forName(classstr);
                Method[] getter = cl.getMethods();
                obj = new JSONObject();
                obj.put("instance", user);
                for(int ctr=0;ctr<getter.length;ctr++){
                    if(!getter[ctr].getReturnType().equals(Void.class)){
                        String funcname = getter[ctr].getName();
                        if(funcname.substring(0,3).equals("get")){
                            String name = funcname.substring(3);
                            name = name.toLowerCase();
                            if(getter[ctr].getParameterTypes().length==0){
                               if(getter[ctr].invoke(user)!=null)
                                    obj.put(name,getter[ctr].invoke(user));
                               else
                                    obj.put(name,"");
                            }
                        }
                    }
                }
                jarr.put(obj);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jarr;
    }
    
}
