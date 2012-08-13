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
package com.krawler.spring.companyDetails;

import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Session;

/**
 *
 * @author Karthik
 */
public class companyDetailsHandler {

    public static String getCompanyid(List ll) throws ServiceException {
        String companyId = "";
        try {
            Iterator ite = ll.iterator();
            if (ite.hasNext()) {
                companyId = (String) ite.next();
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsHandler.getCompanyid", e);
        }
        return companyId;
    }

    public static String getSubDomain(HttpServletRequest request)
			throws SessionExpiredException {
        String subdomain = "";
        Session  session = null;
        String companyid = sessionHandlerImpl.NullCheckAndThrow(request.getSession().getAttribute(
				"companyid"), SessionExpiredException.USERID_NULL);
        try {
            session = HibernateUtil.getCurrentSession();
            Company company = (Company) session.get(Company.class,companyid);
            subdomain = company.getSubDomain();
        } catch (Exception e) {
            HibernateUtil.closeSession(session);
        }

		return subdomain;
	}
}
