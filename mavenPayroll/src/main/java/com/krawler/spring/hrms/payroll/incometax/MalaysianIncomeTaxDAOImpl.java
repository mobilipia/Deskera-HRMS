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
package com.krawler.spring.hrms.payroll.incometax;

import java.util.Date;
import java.util.List;
import java.util.Map;
import masterDB.MalaysianDeduction;
import masterDB.MalaysianTaxSlab;
import masterDB.MalaysianUserIncomeTaxInfo;
import masterDB.MalaysianUserTaxBenefits;
import masterDB.MalaysianUserTaxComponent;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;

import masterDB.MalaysianUserTaxComponentHistory;
import masterDB.PayrollHistory;

public class MalaysianIncomeTaxDAOImpl implements MalaysianIncomeTaxDAO{
	private HibernateTemplate hibernateTemplate;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	@Override
	public List<MalaysianDeduction> getMalaysianDeductionComponents(Date year){
		List<MalaysianDeduction> list = null;
		try{
			list = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianDeduction where taxdate=? order by name", new Object[]{ year});
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	
	@Override
	public boolean saveUserIncomeTaxInformation(Map<MalaysianDeduction, String> malaysianDeductionIds, String userid, Date year){
		boolean success = false;
		List<MalaysianUserTaxComponent> list = null;
		try{
			User user = (User) hibernateTemplate.get(User.class, userid);
			for(Map.Entry<MalaysianDeduction, String> entry : malaysianDeductionIds.entrySet()){
				list = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserTaxComponent where user.userID=? and deduction.id = ? and year=?", new Object[]{userid, entry.getKey().getId(), year});
				if(list!=null && list.size()>0){
					for(MalaysianUserTaxComponent component: list){
						if(entry.getKey().getDataType()==1){
							if(StringUtil.isNullOrEmpty(entry.getValue())){
								component.setAmount(0);
							}else{
								component.setAmount(Double.parseDouble(entry.getValue()));
							}
						}else if(entry.getKey().getDataType()==2){
							if(StringUtil.isNullOrEmpty(entry.getValue())){
								component.setChecked(false);
							}else{
								component.setChecked(entry.getValue().equals("on")?true:false);
							}
						}
						hibernateTemplate.save(component);
					}
				}else{
					MalaysianUserTaxComponent component = new MalaysianUserTaxComponent();
					component.setUser(user);
					component.setDeduction(entry.getKey());
					if(entry.getKey().getDataType()==1){
						if(StringUtil.isNullOrEmpty(entry.getValue())){
							component.setAmount(0);
						}else{
							component.setAmount(Double.parseDouble(entry.getValue()));
						}
					}else if(entry.getKey().getDataType()==2){
						if(StringUtil.isNullOrEmpty(entry.getValue())){
							component.setChecked(false);
						}else{
							component.setChecked(entry.getValue().equals("on")?true:false);
						}
					}
					component.setYear(year);
					component.setModifiedon(new Date());
					hibernateTemplate.save(component);
				}
			}
			success = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return success;
	}

    @Override
    public List<MalaysianUserTaxComponent> getUserIncomeTaxComponent(String userid, Date year, boolean submitted) {
        List<MalaysianUserTaxComponent> userComponents = null;
        try{
            userComponents = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserTaxComponent where user.userID=? and year=? order by deduction.name", new Object[]{userid, year});
        }catch(Exception e){
            e.printStackTrace();
        }
        return userComponents;
    }

    @Override
    public List<MalaysianUserTaxComponentHistory> getUserIncomeTaxComponentHistory(String userid, Date startdate, Date enddate) {
        List<MalaysianUserTaxComponentHistory> userComponents = null;
        try{
            userComponents = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserTaxComponentHistory where payrollHistory.user.userID=? and payrollHistory.paycycleenddate >=? and payrollHistory.paycycleenddate <=? ", new Object[]{userid, startdate, enddate});
        }catch(Exception e){
            e.printStackTrace();
        }
        return userComponents;
    }

    @Override
    public List<MalaysianUserTaxComponentHistory> getUserIncomeTaxComponentHistoryForParticularComponentByPassingComponentsUniqueCode(String userid, Date startdate, Date enddate, int uniquecode) {
        List<MalaysianUserTaxComponentHistory> userComponents = null;
        try{
            userComponents = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserTaxComponentHistory where payrollHistory.user.userID=? and payrollHistory.paycycleenddate >=? and payrollHistory.paycycleenddate <=? and deduction.uniqueCode=? ", new Object[]{userid, startdate, enddate, uniquecode});
        }catch(Exception e){
            e.printStackTrace();
        }
        return userComponents;
    }


    @Override
    public List<MalaysianUserTaxComponent> getUserIncomeTaxComponentByPassingComponentsUniqueCode(String userid, Date year, int uniquecode) {
        List<MalaysianUserTaxComponent> userComponents = null;
        try{
            userComponents = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserTaxComponent where user.userID=? and year=? and deduction.uniqueCode=? ", new Object[]{userid, year, uniquecode});
        }catch(Exception e){
            e.printStackTrace();
        }
        return userComponents;
    }
    @Override
    public boolean saveUserInformation(String userid, Date year, int category, double prevEarning,double prevIncomeTax,double prevEPF,double prevLIC,double prevZakat,boolean epf,double lic,double zakat,double bik, double prevOtherDeduction, int empStatus){
    	boolean success = false;
        try{
        	MalaysianUserIncomeTaxInfo userTaxInfo = null;
        	List<MalaysianUserIncomeTaxInfo> list = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserIncomeTaxInfo where user.userID=? and submittedon=?", new Object[]{userid, year});
        	if(list!=null && list.size()>0){
        		for(MalaysianUserIncomeTaxInfo obj: list){
        			userTaxInfo = obj;
        		}
        	}else{
        		userTaxInfo = new MalaysianUserIncomeTaxInfo();
        	}
        	userTaxInfo.setCategoryid(category);
            userTaxInfo.setPreviousEmployerEarning(prevEarning);
            userTaxInfo.setPreviousEmployerIncomeTax(prevIncomeTax);
            userTaxInfo.setPreviousEmployerEPF(prevEPF);
            userTaxInfo.setPreviousEmployerLIC(prevLIC);
            userTaxInfo.setPreviousEmployerZakat(prevZakat);
            userTaxInfo.setCurrentEPF(epf);
            userTaxInfo.setCurrentLICAndOther(lic);
            userTaxInfo.setCurrentZakat(zakat);
            userTaxInfo.setCurrentBenefitInKind(bik);
            userTaxInfo.setPreviousEmployerOtherDeduction(prevOtherDeduction);
        	userTaxInfo.setSubmittedon(year);
            userTaxInfo.setEmpStatus(empStatus);
        	userTaxInfo.setUser((User) hibernateTemplate.get(User.class, userid));
        	hibernateTemplate.save(userTaxInfo);
            success = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return success;
    }


    @Override
    public List<MalaysianUserIncomeTaxInfo> getUserIncomeTaxInfo(String userid, Date year) {
        List<MalaysianUserIncomeTaxInfo> userInfo = null;
        try{
            String hql ="from MalaysianUserIncomeTaxInfo where user.userID=? and submittedon=? ";
            userInfo = HibernateUtil.executeQuery(hibernateTemplate,hql , new Object[]{userid, year});
        }catch(Exception e){
            e.printStackTrace();
        }
        return userInfo;
    }


    @Override
    public List<MalaysianTaxSlab> getIncomeTaxSlab(double taxableAmount, String categoryid) {
       List<MalaysianTaxSlab> taxSlab = null;
        try{
            String hql ="from MalaysianTaxSlab where startTaxableAmount <=? and categoryId=? order by startTaxableAmount*1 desc ";
            taxSlab = HibernateUtil.executeQuery(hibernateTemplate,hql , new Object[]{taxableAmount, categoryid});
        }catch(Exception e){
            e.printStackTrace();
        }
        return taxSlab;
    }
    @Override
    public MalaysianUserIncomeTaxInfo getUserInformation(String userid, Date year){
    	List<MalaysianUserIncomeTaxInfo> list = null;
    	MalaysianUserIncomeTaxInfo taxInfo = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserIncomeTaxInfo where user.userID=? and submittedon=?", new Object[]{userid, year});
    		for(MalaysianUserIncomeTaxInfo info: list){
    			taxInfo = info;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return taxInfo;
    }
    
    @Override
    public List<MalaysianUserTaxBenefits> getMalaysianUserTaxBenefits(String ids){
    	List<MalaysianUserTaxBenefits> list = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from MalaysianUserTaxBenefits where payrollHistory.historyid in("+ids+")");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    @Override
    public List<PayrollHistory> getPayrollHistories(String userid, int status, Date startDate, Date endDate){
    	List<PayrollHistory> list = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from PayrollHistory where user.userID = ? and salarystatus = ? and paycycleenddate >=? and paycycleenddate <=? order by paycyclestartdate desc", new Object[]{userid, status, startDate, endDate});
    	}catch(Exception e){	
    		e.printStackTrace();
    	}
    	return list;
    }

    @Override
    public List<MalaysianUserTaxBenefits> getUserBenefits(Date startdate, Date enddate, String userid) {
        List<MalaysianUserTaxBenefits> benefits = null;
        try{
            String hql ="from MalaysianUserTaxBenefits where payrollHistory.paycycleenddate >=? and payrollHistory.paycycleenddate<=? and payrollHistory.user.userID=? ";
            benefits = HibernateUtil.executeQuery(hibernateTemplate,hql , new Object[]{startdate, enddate, userid});
        }catch(Exception e){
            e.printStackTrace();
        }
        return benefits;
    }

    @Override
    public boolean saveUserTaxBenefits(String payrollHistoryid, double zakat, double epf, double lic, double otherdeduction, double bik) {
    	boolean success = false;
        try{
            PayrollHistory ph = (PayrollHistory) hibernateTemplate.get(PayrollHistory.class, payrollHistoryid);
        	MalaysianUserTaxBenefits benefits = new MalaysianUserTaxBenefits();

            benefits.setPaidZakat(zakat);
            benefits.setPaidEPF(epf);
            benefits.setPaidLICAndOther(lic);
            benefits.setPaidBIK(bik);
            benefits.setPayrollHistory(ph);
            benefits.setPaidOtherDeduction(otherdeduction);
            
        	hibernateTemplate.save(benefits);
            success = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public boolean saveUserTaxComponentHistory(String payrollhistoryid, List<MalaysianUserTaxComponent> componentList) {
        boolean success = false;
        try{
            PayrollHistory ph = (PayrollHistory) hibernateTemplate.get(PayrollHistory.class, payrollhistoryid);

            for(MalaysianUserTaxComponent comp : componentList){

                MalaysianUserTaxComponentHistory compHistory = new MalaysianUserTaxComponentHistory();

                compHistory.setPayrollHistory(ph);
                compHistory.setAmount(comp.getAmount());
                compHistory.setDeduction(comp.getDeduction());

                hibernateTemplate.save(compHistory);

            }

            success = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return success;
    }
    
    
    public List<PayrollHistory> getPayrollHistory(String userid, int frequency, Date startDate, Date endDate, int status){
    	List<PayrollHistory> list = null;
    	try{
    		String hql = "from PayrollHistory where user.userID = ? and frequency = ? and paycycleenddate>= ? and paycycleenddate<= ? and salarystatus<> ? and salarystatus<> ?";
    		Object[] obj = {userid, String.valueOf(frequency), startDate, endDate, status, 1};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
}
