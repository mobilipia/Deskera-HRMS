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

package com.krawler.spring.hrms.rec.job.ExternalJOb;

import com.krawler.common.admin.ConfigType;
import com.krawler.hrms.master.MasterData;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.customcol.customcolDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Prashil
 */
public class externalJobcontroller extends MultiActionController implements MessageSourceAware{

    private customcolDAO customcolDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private String successView;
    private MessageSource messageSource;
    public void setCustomcolDAO(customcolDAO customcolDAOObj) {
        this.customcolDAOObj = customcolDAOObj;
    }
    public String getSuccessView() {
		return successView;
	}

    public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }
    public ModelAndView getConfigType(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names =  new ArrayList(Arrays.asList("formtype"));
            ArrayList filter_values = new ArrayList(Arrays.asList("Recruitment"));
            filter_names.add("company.companyID");
            filter_values.add(request.getParameter("comp"));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("searchcol", new String[]{"name"});
            result = customcolDAOObj.getConfigType(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("configid", contyp.getConfigid());
                tmpObj.put("configtype", contyp.getConfigtype());
                tmpObj.put("fieldname", contyp.getName());
                tmpObj.put("formtype", contyp.getFormtype());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }

    public ModelAndView getMasterDataField(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        int count = 0;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String,Object> requestParams = new HashMap<String,Object>();
            requestParams.put("masterid", Integer.parseInt(request.getParameter("configid")));
            requestParams.put("companyid", request.getParameter("comp"));
            result = hrmsCommonDAOObj.getMasterDataField(requestParams);
            List lst = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for (Integer i = 0; i < lst.size(); i++) {
                MasterData mst = (MasterData) lst.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", mst.getId());
                tmpObj.put("name", messageSource.getMessage("hrms.MasterData."+StringUtil.replaceNametoLocalkey(mst.getValue()), null,mst.getValue(), RequestContextUtils.getLocale(request)));
                tmpObj.put("weightage", mst.getWeightage());
                jArr.put(tmpObj);
            }
            jobj.put("data",jArr);
            jobj.put("count", result.getRecordTotalCount());
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }

}
