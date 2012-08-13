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
package com.krawler.spring.hrms.payroll.payslip;

import java.util.Date;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import masterDB.Historydetail;
import masterDB.Payhistory;

public class ExportPayslipDAOImpl implements ExportPayslipDAO{
	
	private HibernateTemplate hibernateTemplate;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	@Override
	public List<Payhistory> getPayhistory(String userid, Date startDate, Date endDate){
		List<Payhistory> list = null;
		try {
			String hql = "from Payhistory where userID.userID= ? and paycyclestart= ? and paycycleend= ?";
			list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid, startDate, endDate});
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public List<Historydetail> getHistorydetail(String historyid, String name){
		List<Historydetail> list = null;
		try {
			String hql = "from Historydetail where payhistory.historyid= ? and name= ? order by amount";
			list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{historyid, name});
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public List<Historydetail> getHistorydetail(String historyid, String name, String type){
		List<Historydetail> list = null;
		try {
			String hql = "from Historydetail where payhistory.historyid = ? and name= ? and type= ?";
			list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{historyid, name, type});
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public List<Historydetail> getHistorydetailNotType(String historyid, String name, String type){
		List<Historydetail> list = null;
		try {
			String hql = "from Historydetail where payhistory.historyid= ? and name= ? and type<> ? order by amount";
			list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{historyid, name, type});
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public List<Historydetail> getHistorydetail(String userid, Date startDate, Date endDate, String type){
		List<Historydetail> list = null;
		try {
			String hql = "from Historydetail where payhistory.userID.userID =? and createdfor >=? and createdfor <=? and type =?";
			list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid, startDate, endDate, type});
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return list;
	}
}
