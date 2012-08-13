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

package com.krawler.spring.dashboardWidget;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.common.admin.widgetManagement;
import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class widgetDAOImpl implements widgetDAO {
    private HibernateTemplate hibernateTemplate;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
    public KwlReturnObject insertWidgetIntoState(HashMap<String, Object> requestParams) throws ServiceException {
        List ll =  new ArrayList();
        int dl = 0;
        try {
            String userId = requestParams.get("userid").toString();
            String wid = requestParams.get("wid").toString();
            String colno = requestParams.get("colno").toString();
            String columnToUpdate = "col" + colno;
            widgetManagement wmObj = null;
            String query = "FROM widgetManagement wm WHERE wm.user.userID =?";
            List lst = HibernateUtil.executeQuery(this.hibernateTemplate, query, new Object[]{userId});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                wmObj = (widgetManagement) ite.next();
            }
            JSONObject jobj = new JSONObject(wmObj.getWidgetstate());
            JSONObject check = getColumnPositionInWidgetState(jobj, wid);
            if (!check.getBoolean("present")) {
                JSONObject empty = new JSONObject();
                empty.put("id", wid);
                jobj.append(columnToUpdate, empty);
                wmObj.setWidgetstate(jobj.toString());
            }
            this.hibernateTemplate.saveOrUpdate(wmObj);
            ll.add(wmObj);
            dl = ll.size();
        } catch (ServiceException e) {
            throw ServiceException.FAILURE("widgetDAOImpl.insertWidgetIntoState : "+e.getMessage(), e);
        } catch (Exception e) {
            throw ServiceException.FAILURE("widgetDAOImpl.insertWidgetIntoState : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true,"002","",ll,dl);
    }

    public KwlReturnObject removeWidgetFromState(HashMap<String, Object> requestParams) throws ServiceException {
        List ll =  new ArrayList();
        int dl = 0;
        try {
            String wid = requestParams.get("wid").toString();
            String userId = requestParams.get("userid").toString();
            widgetManagement wmObj = new widgetManagement();
            JSONObject empty = new JSONObject();
            String query = "FROM widgetManagement wm WHERE wm.user.userID =?";
            List lst = HibernateUtil.executeQuery(this.hibernateTemplate, query, new Object[]{userId});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                wmObj = (widgetManagement) ite.next();
                empty = new JSONObject(wmObj.getWidgetstate());
            }
            JSONObject _state = getColumnPositionInWidgetState(empty, wid);
            String column = _state.getString("column");
            JSONObject jobj = deleteFromWidgetStateJson(empty, "id", wid, column);

            // update WidgetManagement Table
            wmObj.setWidgetstate(jobj.toString());

            this.hibernateTemplate.saveOrUpdate(wmObj);
            ll.add(wmObj);
            dl = ll.size();
        } catch (ServiceException e) {
            throw ServiceException.FAILURE("widgetDAOImpl.removeWidgetFromState : "+e.getMessage(), e);
        } catch (Exception e) {
            throw ServiceException.FAILURE("widgetDAOImpl.removeWidgetFromState : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true,"002","",ll,dl);
    }

    public KwlReturnObject changeWidgetState(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String wid = requestParams.get("wid").toString();
            String userId = requestParams.get("userid").toString();
            String colno = requestParams.get("colno").toString();
            int position = Integer.parseInt(requestParams.get("position").toString());
            String columnToEdit = "col" + colno;

            widgetManagement wmObj = null;
            String query = "FROM widgetManagement wm WHERE wm.user.userID =?";
            List lst = HibernateUtil.executeQuery(this.hibernateTemplate, query, new Object[]{userId});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                wmObj = (widgetManagement) ite.next();
            }

            JSONObject jobj = new JSONObject(wmObj.getWidgetstate());
            JSONObject previous_details = getColumnPositionInWidgetState(jobj, wid);

            String pre_column = previous_details.getString("column");
            int pre_position = previous_details.getInt("position");
            if (pre_position < position && columnToEdit.equals(pre_column)) {
                position--;
            }
            jobj = deleteFromWidgetStateJson(jobj, "id", wid, pre_column);
            com.krawler.utils.json.base.JSONArray jobj_col = jobj.getJSONArray(columnToEdit);
            JSONObject empty = new JSONObject();
            empty.put("id", wid);
            jobj_col = insertIntoJsonArray(jobj_col, position, empty);
            jobj.put(columnToEdit, jobj_col);

            wmObj.setWidgetstate(jobj.toString());
            this.hibernateTemplate.saveOrUpdate(wmObj);

            ll.add(wmObj);
            dl = ll.size();
        } catch (ServiceException e) {
            throw ServiceException.FAILURE("widgetDAOImpl.changeWidgetState : "+e.getMessage(), e);
        } catch (Exception e) {
            throw ServiceException.FAILURE("widgetDAOImpl.changeWidgetState : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true,"002","",ll,dl);
    }

    private JSONArray insertIntoJsonArray(JSONArray jArr, int position, JSONObject newjObj) throws JSONException {
        JSONArray toReturn = new JSONArray();
        Boolean added = false;
        for (int i = 0; i < jArr.length(); i++) {
            if (i == position) {
                toReturn.put(newjObj);
                added = true;

            }
            JSONObject empty = jArr.getJSONObject(i);
            toReturn.put(empty);
        }
        if (!added) {
            toReturn.put(newjObj);
        }
        return toReturn;
    }

    private JSONObject getColumnPositionInWidgetState(JSONObject jobj, String wid) throws JSONException {
        JSONObject toReturn = new JSONObject();
        for (int i = 1; i <= 3; i++) {
            String column = "col" + String.valueOf(i);
            com.krawler.utils.json.base.JSONArray jArr = jobj.getJSONArray(column);
            for (int j = 0; j < jArr.length(); j++) {
                JSONObject empty = jArr.getJSONObject(j);
                if (empty.get("id").toString().equals(wid)) {
                    toReturn.put("column", column);
                    toReturn.put("position", j);
                    toReturn.put("present", true);
                    return toReturn;
                }
            }
        }
        toReturn.put("present", false);
        return toReturn;
    }

    private JSONObject deleteFromWidgetStateJson(JSONObject jobj, String key, String value, String column) throws JSONException {
        com.krawler.utils.json.base.JSONArray jobj_col = deleteFromWidgetStateJsonArray(jobj.getJSONArray(column), key, value);
        jobj.put(column, jobj_col);
        return jobj;
    }

    private com.krawler.utils.json.base.JSONArray deleteFromWidgetStateJsonArray(com.krawler.utils.json.base.JSONArray jArr, String key, String value) throws JSONException {
        com.krawler.utils.json.base.JSONArray toReturn = new com.krawler.utils.json.base.JSONArray();
        for (int i = 0; i < jArr.length(); i++) {
            JSONObject empty = jArr.getJSONObject(i);
            if (!empty.get("id").toString().equals(value)) {
                toReturn.put(empty);
            }
        }
        return toReturn;
    }

//    public KwlReturnObject getWidgetStatus(HashMap<String, Object> requestParams) throws ServiceException {
//        List ll = null;
//        int dl = 0;
//        try {
//            JSONObject empty = new JSONObject();
//            String userId = requestParams.get("userid").toString();
//            User userObj = (User) this.hibernateTemplate.get(User.class, userId);
//            String query = "FROM widgetManagement as wm WHERE wm.user = ?";
//            List lst = this.hibernateTemplate.find(query,new Object[]{userObj});
//            if (lst == null || lst.size() == 0) {
//                empty = insertDefaultWidgetState();
//                widgetManagement wmObj = new widgetManagement();
//                wmObj.setWidgetstate(empty.toString());
//                wmObj.setUser(userObj);
//                wmObj.setModifiedon(new Date());
//                this.hibernateTemplate.save(wmObj);
//            }
//            query = "FROM widgetManagement as wm WHERE wm.user = ?";
//            ll = this.hibernateTemplate.find(query,new Object[]{userObj});
//            dl = ll.size();
//        } catch (ServiceException e) {
//            throw ServiceException.FAILURE("widgetDAOImpl.getWidgetStatus : "+e.getMessage(), e);
//        } catch (Exception e) {
//            throw ServiceException.FAILURE("widgetDAOImpl.getWidgetStatus : "+e.getMessage(), e);
//        }
//        return new KwlReturnObject(true,"002","",ll,dl);
//    }


   public KwlReturnObject getwidgetManagement(HashMap<String,Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from widgetManagement ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = (ArrayList)requestParams.get("order_by");
                ordertype = (ArrayList)requestParams.get("order_type");
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    public static JSONObject insertDefaultWidgetState() throws ServiceException {
        JSONObject jobj = null;
        try {
            String col1 = "";
            String col2 = "";
            String col3 = "";
            col2 = "{'id':'crmmodule_drag'}";
            col1 = "{'id':'DSBMyWorkspaces'}";
            col3 = "{'id':'reports_drag'}";
            jobj = new JSONObject("{'col1':" + (!col1.equals("") ? "[" + col1 + "]" : "[]") + ",'col2':" + (!col2.equals("") ? "[" + col2 + "]" : "[]") + ",'col3':" + (!col3.equals("") ? "[" + col3 + "]" : "[]") + "}");
        } catch(JSONException e) {
            throw ServiceException.FAILURE("widgetDAOImpl.insertDefaultWidgetState : "+e.getMessage(), e);
        }
        return jobj;
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
