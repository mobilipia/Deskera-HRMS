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
package com.krawler.spring.dashboard;

import com.krawler.common.admin.User;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.SessionFactory;
import com.krawler.common.admin.widgetManagement;
import com.krawler.spring.dashboard.hrmsDashboardDAO;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class hrmsDashboardDAOImpl implements hrmsDashboardDAO {
    private HibernateTemplate hibernateTemplate;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

     public JSONObject getDefaultWidgetState(){
         JSONObject jobj = null;
         try{
            String col1="";
            String col2="";
            String col3="";

            col1="{'id':'dash_recruit'}";
            col2="{'id':'dash_performance'},{'id':'dash_payroll'}";
            col3="{'id':'dash_adminwidget'},{'id':'dash_timesheet'}";

            jobj=new JSONObject("{'col1':"+(!col1.equals("")?"["+col1+"]":"[]")+",'col2':"+(!col2.equals("")?"["+col2+"]":"[]")+",'col3':"+(!col3.equals("")?"["+col3+"]":"[]")+"}");
         }catch(Exception ex){
            ex.printStackTrace();
         }finally{
            return jobj;
         }
    }


     public KwlReturnObject insertWidgetManagement(HashMap<String, Object> requestParams) {
            List <widgetManagement> list = new ArrayList<widgetManagement>();
            boolean success = false;
            try {
                widgetManagement user = (widgetManagement) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.common.admin.widgetManagement", "Id");
                list.add(user);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Employee history added successfully.", "-1", list, list.size());
            }
     }

        

}
