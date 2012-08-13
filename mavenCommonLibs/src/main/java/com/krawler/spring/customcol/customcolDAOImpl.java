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

package com.krawler.spring.customcol;

import com.krawler.common.admin.ConfigData;
import com.krawler.common.admin.ConfigType;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class customcolDAOImpl implements customcolDAO {
    private HibernateTemplate hibernateTemplate;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
    public KwlReturnObject getConfigType(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from ConfigType ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                 int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replace("("+index+")", "("+value.get(index).toString()+")");
                    value.remove(index);
                }
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            
        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();
            
        } finally {
            return result;
        }
    }

     public KwlReturnObject addConfigType(HashMap<String, Object> requestParams) {
            List <ConfigType> list = new ArrayList<ConfigType>();
            boolean success = false;
            try {
                ConfigType user = (ConfigType) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.common.admin.ConfigType", "Configid");
                list.add(user);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Configtype added successfully.", "-1", list, list.size());
            }
   }

   public KwlReturnObject getConfigData(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from ConfigData ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql.replace("("+index+")", value.get(index).toString());
                    value.remove(index);
                }
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally {
            return result;
        }
    }

   public KwlReturnObject addConfigData(HashMap<String, Object> requestParams) {
            List <ConfigData> list = new ArrayList<ConfigData>();
            boolean success = false;
            try {
                ConfigData user = (ConfigData) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.common.admin.ConfigData", "Id");
                list.add(user);
                success = true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "ConfigData added successfully.", "-1", list, list.size());
            }
   }

   public KwlReturnObject deleteConfigType(String configid) {
            boolean success = false;
            try {
                ConfigType configtype = (ConfigType) hibernateTemplate.get(ConfigType.class, configid);
                hibernateTemplate.delete(configtype);
                success = true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "ConfigType deleted successfully.", "-1", null,0);
            }
   }
}
