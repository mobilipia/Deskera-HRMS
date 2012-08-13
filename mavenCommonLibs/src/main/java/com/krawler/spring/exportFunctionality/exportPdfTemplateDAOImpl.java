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
package com.krawler.spring.exportFunctionality;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.Projreport_Template;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Karthik
 */
public class exportPdfTemplateDAOImpl implements exportPdfTemplateDAO {

    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject saveReportTemplate(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {

            Projreport_Template proj_temp = new Projreport_Template();
            if (requestParams.containsKey("name") && !requestParams.get("name").toString().equals("")) {
                proj_temp.setTempname(requestParams.get("name").toString());
            }
            if (requestParams.containsKey("desc") && !requestParams.get("desc").toString().equals("")) {
                proj_temp.setDescription(requestParams.get("desc").toString());
            }
            if (requestParams.containsKey("jsondata") && !requestParams.get("jsondata").toString().equals("")) {
                proj_temp.setConfigstr(requestParams.get("jsondata").toString());
            }
            if (requestParams.containsKey("userid") && !requestParams.get("userid").toString().equals("")) {
                proj_temp.setUserid((User) hibernateTemplate.get(User.class, requestParams.get("userid").toString()));
            }
            hibernateTemplate.save(proj_temp);

            ll.add(proj_temp);
        } catch (HibernateException ex) {
            throw ServiceException.FAILURE("exportPdfTemplateDAOImpl.saveReportTemplate", ex);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAllReportTemplate(String userid) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select p from com.krawler.common.admin.Projreport_Template p where p.userid.userID=? and p.deleteflag=0 ";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, new Object[]{userid});
            dl = ll.size();
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("exportPdfTemplateDAOImpl.getAllReportTemplate", ex);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject deleteReportTemplate(String tempid) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            if (!StringUtil.isNullOrEmpty(tempid)) {
                Projreport_Template proj_temp = (Projreport_Template) hibernateTemplate.load(Projreport_Template.class, tempid);
                proj_temp.setDeleteflag(1);
                hibernateTemplate.save(proj_temp);

                ll.add(proj_temp);
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("exportPdfTemplateDAOImpl.deleteReportTemplate", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject editReportTemplate(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            if (requestParams.containsKey("tempid") && !requestParams.get("tempid").toString().equals("")) {
                Projreport_Template proj_temp = (Projreport_Template) hibernateTemplate.load(Projreport_Template.class, requestParams.get("tempid").toString());
                if (requestParams.containsKey("newconfig") && !requestParams.get("newconfig").toString().equals("")) {
                    proj_temp.setConfigstr(requestParams.get("newconfig").toString());
                }
                hibernateTemplate.save(proj_temp);
                ll.add(proj_temp);
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("exportPdfTemplateDAOImpl.editReportTemplate", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }
}
