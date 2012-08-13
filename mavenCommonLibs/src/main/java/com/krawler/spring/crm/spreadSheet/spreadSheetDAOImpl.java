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
package com.krawler.spring.crm.spreadSheet;
import com.krawler.common.admin.User;
import java.lang.reflect.InvocationTargetException;
import org.hibernate.SessionFactory;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.common.admin.SpreadSheetConfig;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class spreadSheetDAOImpl implements spreadSheetDAO {
    private HibernateTemplate hibernateTemplate;
    private sessionHandlerImpl sessionHandlerImplObj;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    public KwlReturnObject getSpreadsheetConfig(HttpServletRequest request) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String module = request.getParameter("module");
            String hql = "from SpreadSheetConfig where module=? and user.userID=? ";
            ll = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{module, sessionHandlerImplObj.getUserid(request)});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.getSpreadsheetConfig : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true,"002","",ll,dl);
    }

    public SpreadSheetConfig saveSpreadsheetConfig(JSONObject jobj) throws ServiceException {
        SpreadSheetConfig cm = null;
        try {
            cm = (SpreadSheetConfig) hibernateTemplate.get(SpreadSheetConfig.class, jobj.getString("cid"));
            if (cm == null) {
                cm = new SpreadSheetConfig();
                if(jobj.has("cmuuid")) {
                    cm.setCid(jobj.getString("cmuuid"));
                }
                if(jobj.has("module")) {
                    cm.setCid(jobj.getString("module"));
                }
                if(jobj.has("userid")) {
                    cm.setUser((User) hibernateTemplate.get(User.class, jobj.getString("userid")));
                }
            }
            if(jobj.has("state")) {
                cm.setState(jobj.getString("state"));
            }
            if(jobj.has("rule")) {
                cm.setRules(jobj.getString("rule"));
            }
            if(jobj.has("updatedon")) {
                cm.setUpdatedon(new Date());
            }
            hibernateTemplate.saveOrUpdate(cm);
        } catch (Exception e) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.getSpreadsheetConfig : "+e.getMessage(), e);
        }
        return cm;
    }

    public JSONObject saveModuleRecordStyle(JSONObject jo) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        try{
            Class cl = Class.forName(jo.getString("classname"));
            Object invoker = hibernateTemplate.get(cl, jo.getString("id"));
            if(invoker != null) {
                Field  field= invoker.getClass().getDeclaredField("cellstyle");
                Class type = field.getType();
                Class  arguments[] = new Class[] {type};
                java.lang.reflect.Method objMethod;
                objMethod = cl.getMethod("setCellstyle", arguments);
                Object[] obj = new Object[]{jo.getString("cellStyle")};
                Object result1 = objMethod.invoke(invoker, obj);
                hibernateTemplate.update(invoker);
            }
            myjobj.put("success", true);
        } catch (IllegalAccessException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (NoSuchFieldException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (SecurityException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.getSpreadsheetConfig : "+ex.getMessage(), ex);
        }
        return myjobj;
    }
}
