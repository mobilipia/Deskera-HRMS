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
package com.krawler.spring.hrms.payroll.salaryslip;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masterDB.ComponentResourceMappingHistory;
import masterDB.PayrollHistory;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.ess.*;
public class ExportSalarySlipDAOImpl implements ExportSalarySlipDAO{
	private HibernateTemplate hibernateTemplate;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
	
	@SuppressWarnings("finally")
	@Override
	public User getUser(String userid){
		User user = null;
		try{
			user = (User) hibernateTemplate.get(User.class, userid);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return user;
		}
	}
	
	@SuppressWarnings("finally")
	@Override
	public Empprofile getEmpprofile(String userid){
		Empprofile empprofile = null;
		try{
			empprofile = (Empprofile) hibernateTemplate.get(Empprofile.class, userid);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return empprofile;
		}
	}
	
	@SuppressWarnings("finally")
	@Override
	public PayrollHistory getPayrollHistory(String id){
		PayrollHistory payrollHistory = null;
		try{
			payrollHistory = (PayrollHistory) hibernateTemplate.get(PayrollHistory.class, id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return payrollHistory;
		}
	}
	
	@SuppressWarnings("finally")
	@Override
	public Useraccount getUserAccount(String id){
		Useraccount useraccount = null;
		try{
			useraccount = (Useraccount) hibernateTemplate.get(Useraccount.class, id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return useraccount;
		}
	}
	
	
	@SuppressWarnings("finally")
	@Override
	public List<String> getHeaders(Date enddate, String companyId, String amountText){
		List<String> header = new ArrayList<String>();
		try{
			List<ComponentResourceMappingHistory> list = HibernateUtil.executeQuery(hibernateTemplate, "from ComponentResourceMappingHistory where periodenddate = ? and user.company.companyID = ? group by component.compid", new Object[]{enddate, companyId});
			for(ComponentResourceMappingHistory crmh: list){
				if(crmh.getComponent()!=null){
					header.add(crmh.getComponent().getDescription()+"("+amountText+")");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return header;
		}
	}
	
	
	@SuppressWarnings("finally")
	@Override
	public List<User> getUsersList(String frequency, Date enddate, String companyId, String module, Integer status){
		List<User> users = new ArrayList<User>();
		try{
            String hql = "from PayrollHistory where frequency=? and paycycleenddate = ? and salarystatus = ? and user.company.companyID = ? ";
            
            if(StringUtil.equal(module, "authorization")){
                hql+=" and salarystatus in ("+2+","+3+","+4+") ";
            } else if(StringUtil.equal(module, "process")){
                hql+=" and salarystatus in ("+3+","+5+","+6+") ";
            }

            
            hql+=" group by user.userID ";
			List<PayrollHistory> list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{frequency, enddate, status, companyId});
			for(PayrollHistory crmh: list){
				users.add(crmh.getUser());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return users;
		}
	}
	
	@SuppressWarnings("finally")
	@Override
	public List<String> getComponentIds(Date enddate, int frequency, String companyId){
		List<String> ids = new ArrayList<String>();
		try{
			List<ComponentResourceMappingHistory> list = HibernateUtil.executeQuery(hibernateTemplate, "from ComponentResourceMappingHistory where periodenddate = ? and user.company.companyID = ? and frequency = ?  group by component.compid", new Object[]{enddate, companyId, frequency});
			for(ComponentResourceMappingHistory crmh: list){
				if(crmh.getComponent()!=null){
					ids.add(crmh.getComponent().getCompid());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return ids;
		}
	}
	
	@SuppressWarnings("finally")
	@Override
	public Map<String, masterDB.ComponentResourceMappingHistory> getSalaryHistoryForUser(Date enddate, int frequency, String userid){
		Map<String, ComponentResourceMappingHistory> map = new HashMap();
		try{
			List<ComponentResourceMappingHistory> list = HibernateUtil.executeQuery(hibernateTemplate, "from ComponentResourceMappingHistory where periodenddate = ? and user.userID = ? and frequency = ? group by component.compid", new Object[]{enddate, userid, frequency});
			for(ComponentResourceMappingHistory crmh: list){
				map.put(crmh.getComponent().getCompid(), crmh);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return map;
		}
	}
}
