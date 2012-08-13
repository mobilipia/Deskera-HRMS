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

import com.krawler.common.admin.Country;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author Karthik
 */
public class kwlCommonTablesController extends MultiActionController {

    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public ModelAndView getAllTimeZones(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            kmsg = kwlCommonTablesDAOObj.getAllTimeZones();
            jobj = getAllTimeZonesJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            jobj1.put("data",jobj);
            jobj1.put("valid",true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public JSONObject getAllTimeZonesJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                KWLTimeZone timeZone = (KWLTimeZone) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("id", timeZone.getTimeZoneID());
                obj.put("name", timeZone.getName());
                obj.put("difference", timeZone.getDifference());
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getAllCurrencies(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            kmsg = kwlCommonTablesDAOObj.getAllTimeZones();
            jobj = getAllCurrenciesJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getAllCurrenciesJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                KWLCurrency currency = (KWLCurrency) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("currencyid", currency.getCurrencyID());
                obj.put("symbol", currency.getSymbol());
                obj.put("currencyname", currency.getName());
                obj.put("htmlcode", currency.getHtmlcode());
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getAllCountries(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            kmsg = kwlCommonTablesDAOObj.getAllCountries();
            jobj = getAllCountriesJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getAllCountriesJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Country country = (Country) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("id", country.getID());
                obj.put("name", country.getCountryName());
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getAllDateFormats(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            kmsg = kwlCommonTablesDAOObj.getAllDateFormats();
            jobj = getAllDateFormatsJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    private static String getFormattedDate(Date curDate, String javaForm) {
        SimpleDateFormat sdf = new SimpleDateFormat(javaForm);
        return sdf.format(curDate);
    }

    public JSONObject getAllDateFormatsJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                KWLDateFormat dateFormat = (KWLDateFormat) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("formatid", dateFormat.getFormatID());
                obj.put("formalname", dateFormat.getName());
                obj.put("name", getFormattedDate(new Date(), dateFormat.getJavaForm()));
                obj.put("javaform", dateFormat.getJavaForm());
                obj.put("scriptform", dateFormat.getScriptForm());
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }
}
