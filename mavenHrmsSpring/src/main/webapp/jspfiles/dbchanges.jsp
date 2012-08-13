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
<%@ page import="com.krawler.hrms.recruitment.Positionmain" %>
<%@ page import="com.krawler.common.admin.*" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="org.hibernate.Query"%>
<%@ page import="org.hibernate.Session"%>
<%@ page import="java.util.List"%>
<%@ page import="com.krawler.esp.hibernate.impl.HibernateUtil.*"%>
<%

        org.hibernate.Session hSession = com.krawler.esp.hibernate.impl.HibernateUtil.getCurrentSession();
        org.hibernate.Transaction trans = hSession.beginTransaction();
        trans.begin();
        try {
            String hql = "";
            java.util.List lst = null;
            java.util.Iterator it = null;
             java.util.List lst1 = null;
            java.util.Iterator it1 = null;
            int j=1;

            hql = "";
            lst = null;
            it = null;

           hql = "from Company";
            lst = com.krawler.esp.hibernate.impl.HibernateUtil.executeQuery(hql);
            it = lst.iterator();
            while (it.hasNext()) {
                Company cobj = (Company) it.next();
                String cmpid = cobj.getCompanyID();
            Query query = hSession.createQuery("from Positionmain where company.companyID=:cid");
            query.setString("cid",cmpid);
             lst1 =query.list();
             it1=lst1.iterator();
             j=1;
                while (it1.hasNext()) {

                    Positionmain uobj = (Positionmain) it1.next();
                    uobj.setJobidwthformat(j);
                    j++;
                    hSession.saveOrUpdate(uobj);
                }

            }
             trans.commit();
                out.print("JobID inserted successfully in Position table.");
            }
        catch  (org.hibernate.HibernateException err) {
            trans.rollback();
            out.print(err.getMessage());
        } finally {
            com.krawler.esp.hibernate.impl.HibernateUtil.closeSession(hSession);
        }


%>
