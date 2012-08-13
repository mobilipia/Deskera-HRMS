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

package com.krawler.spring.hrms.payroll.statutoryform;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.common.MalaysianIncomeTaxConstants;
import java.util.Date;
import java.util.List;
import masterDB.MalaysiaCompanyForm;
import masterDB.MalaysiaFormAmanahSahamNasional;
import masterDB.MalaysiaFormCP21;
import masterDB.MalaysiaFormCP39;
import masterDB.MalaysiaFormCP39A;
import masterDB.MalaysiaFormEA;
import masterDB.MalaysiaFormHRDLevy;
import masterDB.MalaysiaFormTP1;
import masterDB.MalaysiaFormPCB2;
import masterDB.MalaysiaFormTP2;
import masterDB.MalaysiaFormTP3;
import masterDB.MalaysiaFormTabungHaji;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author krawler
 */
public class MalaysianStatutoryFormDAOImpl implements MalaysianStatutoryFormDAO{

    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    @Override
    public boolean saveAmanahSahamNasional(String id, String icno, String accno, String amount, User user, int month, int year, int status) {
        boolean success = false;
        try{
            MalaysiaFormAmanahSahamNasional amn = null;
            if(StringUtil.isNullOrEmpty(id)){
                amn = new MalaysiaFormAmanahSahamNasional();
            }else {
                amn = (MalaysiaFormAmanahSahamNasional) hibernateTemplate.get(MalaysiaFormAmanahSahamNasional.class, id);
            }
            
            if(!StringUtil.isNullOrEmpty(icno)){
                long icn = Long.parseLong(icno);
                amn.setIcno(icn);
            }
            if(!StringUtil.isNullOrEmpty(accno)){
                long accn = Long.parseLong(accno);
                amn.setAccountno(accn);
            }
            if(!StringUtil.isNullOrEmpty(amount)){
                double amt = Double.parseDouble(amount);
                amn.setAmount(amt);
            }
            amn.setMonth(month);
            amn.setYear(year);
            amn.setUser(user);
            amn.setAuthorizeStatus(status);

            hibernateTemplate.save(amn);

            success = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return success;
    }

     @Override
    public boolean saveTabungHaji(String id, String icno, String accno, String amount, User user, int month, int year, int status) {
        boolean success = false;
        try{
            MalaysiaFormTabungHaji th = null;
            if(StringUtil.isNullOrEmpty(id)){
                th = new MalaysiaFormTabungHaji();
            }else {
                th = (MalaysiaFormTabungHaji) hibernateTemplate.get(MalaysiaFormTabungHaji.class, id);
            }

            if(!StringUtil.isNullOrEmpty(icno)){
                long icn = Long.parseLong(icno);
                th.setIcno(icn);
            }
            if(!StringUtil.isNullOrEmpty(accno)){
                long accn = Long.parseLong(accno);
                th.setAccountno(accn);
            }
            if(!StringUtil.isNullOrEmpty(amount)){
                double amt = Double.parseDouble(amount);
                th.setAmount(amt);
            }

            th.setMonth(month);

            th.setYear(year);

            th.setUser(user);

            th.setAuthorizeStatus(status);

            hibernateTemplate.save(th);

            success = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return success;
    }

     public boolean saveCP21(String id, String empfilerefno, User user, Date datetoleave, String passportno, String natureofemployment, String departurereason, String correspondenceaddress,
                    Date dateofreturn, String dueamount, Date dateofform, Date salaryfrom, Date salaryto, String salaryamount, Date leavepayfrom, Date leavepayto, String leavepayamount,
                    Date bonusfrom, Date bonusto, String bonusamount, Date gratuityfrom, Date gratuityto, String gratuityamount, Date allowancefrom, Date allowanceto, String allowanceamount,
                    Date pensionfrom, Date pensionto, String pensionamount, Date residencefrom, Date residenceto, String residenceamount, Date allowanceinkindfrom, Date allowanceinkindto,
                    String allowanceinkindamount, Date pffrom, Date pfto, String pfamount, String natureofpayment, Date dateofpayment, String amounttobepaid, int month, int year, int status){

         boolean success = false;
         try{
            MalaysiaFormCP21 cp = null;
            if(StringUtil.isNullOrEmpty(id)){
                cp = new MalaysiaFormCP21();
            }else {
                cp = (MalaysiaFormCP21) hibernateTemplate.get(MalaysiaFormCP21.class, id);
            }
            if(user!=null){
                cp.setUser(user);
            }
            if(datetoleave!=null){
                cp.setExpDateToLeave(datetoleave);
            }
            if(!StringUtil.isNullOrEmpty(passportno)){
                cp.setPassportno(passportno);
            }
            if(!StringUtil.isNullOrEmpty(empfilerefno)){
                cp.setEmpfilerefno(empfilerefno);
            }
            if(!StringUtil.isNullOrEmpty(natureofemployment)){
                cp.setNatureofemployment(Integer.parseInt(natureofemployment));
            }
            if(!StringUtil.isNullOrEmpty(departurereason)){
                cp.setReasonForDeparture(departurereason);
            }
            if(!StringUtil.isNullOrEmpty(correspondenceaddress)){
                cp.setCorresspondenceAddress(correspondenceaddress);
            }
            if(dateofreturn!=null){
                cp.setDateOfReturn(dateofreturn);
            }
            if(!StringUtil.isNullOrEmpty(dueamount)){
                cp.setAmountdue(Double.parseDouble(dueamount));
            }
            if(dateofform!=null){
                cp.setDateOfSubmission(dateofform);
            }
            if(salaryfrom!=null){
                cp.setSalaryFrom(salaryfrom);
            }
            if(salaryto!=null){
                cp.setSalaryTo(salaryto);
            }
            if(!StringUtil.isNullOrEmpty(salaryamount)){
                cp.setSalaryAmount(Double.parseDouble(salaryamount));
            }
            if(leavepayfrom!=null){
                cp.setLeavePayFrom(leavepayfrom);
            }
            if(leavepayto!=null){
                cp.setLeavePayTo(leavepayto);
            }
            if(!StringUtil.isNullOrEmpty(leavepayamount)){
                cp.setLeavePayAmount(Double.parseDouble(leavepayamount));
            }
            if(bonusfrom!=null){
                cp.setBonusFrom(bonusfrom);
            }
            if(bonusto!=null){
                cp.setBonusTo(bonusto);
            }
            if(!StringUtil.isNullOrEmpty(bonusamount)){
                cp.setBonusAmount(Double.parseDouble(bonusamount));
            }
            if(gratuityfrom!=null){
                cp.setGratuityFrom(gratuityfrom);
            }
            if(gratuityto!=null){
                cp.setGratuityTo(gratuityto);
            }
            if(!StringUtil.isNullOrEmpty(gratuityamount)){
                cp.setGratuityAmount(Double.parseDouble(gratuityamount));
            }
            if(allowancefrom!=null){
                cp.setAllowanceFrom(allowancefrom);
            }
            if(allowanceto!=null){
                cp.setAllowanceTo(allowanceto);
            }
            if(!StringUtil.isNullOrEmpty(allowanceamount)){
                cp.setAllowanceAmount(Double.parseDouble(allowanceamount));
            }
            if(pensionfrom!=null){
                cp.setPensionFrom(pensionfrom);
            }
            if(pensionto!=null){
                cp.setPensionTo(pensionto);
            }
            if(!StringUtil.isNullOrEmpty(pensionamount)){
                cp.setPensionAmount(Double.parseDouble(pensionamount));
            }
            if(residencefrom!=null){
                cp.setResidenceFrom(residencefrom);
            }
            if(residenceto!=null){
                cp.setResidenceTo(residenceto);
            }
            if(!StringUtil.isNullOrEmpty(residenceamount)){
                cp.setResidenceAmount(Double.parseDouble(residenceamount));
            }
            if(allowanceinkindfrom!=null){
                cp.setAllowanceinkindFrom(allowanceinkindfrom);
            }
            if(allowanceinkindto!=null){
                cp.setAllowanceinkindTo(allowanceinkindto);
            }
            if(!StringUtil.isNullOrEmpty(allowanceinkindamount)){
                cp.setAllowanceinkindAmount(Double.parseDouble(allowanceinkindamount));
            }
            if(pffrom!=null){
                cp.setProvidentFundFrom(pffrom);
            }
            if(pfto!=null){
                cp.setProvidentFundTo(pfto);
            }
            if(!StringUtil.isNullOrEmpty(pfamount)){
                cp.setProvidentFundAmount(Double.parseDouble(pfamount));
            }

            if(dateofpayment!=null){
                cp.setPaymentDate(dateofpayment);
            }
            if(!StringUtil.isNullOrEmpty(natureofpayment)){
                cp.setNatureofpayment(Integer.parseInt(natureofpayment));
            }
            if(!StringUtil.isNullOrEmpty(amounttobepaid)){
                cp.setAmountToBePaid(Double.parseDouble(amounttobepaid));
            }

            cp.setMonth(month);

            cp.setYear(year);

            cp.setAuthorizeStatus(status);
            
            hibernateTemplate.save(cp);
            
        }catch(Exception e){
            e.printStackTrace();
        }
         return success;

     }
   @Override
    public boolean saveHRDLevy(String id, String baseSalary, String others, String netSalary, String levy, User user, int month, int year, int status) {
        boolean success = false;
        try{
            MalaysiaFormHRDLevy hl = null;
            if(StringUtil.isNullOrEmpty(id)){
                hl = new MalaysiaFormHRDLevy();
            }else {
                hl = (MalaysiaFormHRDLevy) hibernateTemplate.get(MalaysiaFormHRDLevy.class, id);
            }

            if(!StringUtil.isNullOrEmpty(baseSalary)){
                double bs = Double.parseDouble(baseSalary);
                hl.setBaseSalary(bs);
            }
            if(!StringUtil.isNullOrEmpty(others)){
                double ot = Double.parseDouble(others);
                hl.setOthers(ot);
            }
            if(!StringUtil.isNullOrEmpty(netSalary)){
                double ns = Double.parseDouble(netSalary);
                hl.setNetSalary(ns);
            }
            if(!StringUtil.isNullOrEmpty(levy)){
                double lv = Double.parseDouble(levy);
                hl.setHrdLevy(lv);
            }

            hl.setMonth(month);

            hl.setYear(year);
            
            hl.setUser(user);

            hl.setAuthorizeStatus(status);

            hibernateTemplate.save(hl);

            success = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return success;
    }
         
    @Override
    public MalaysiaFormAmanahSahamNasional getUserAmanahSahamNasional(String userid, int month, int year) {
        List<MalaysiaFormAmanahSahamNasional> list = null;
        MalaysiaFormAmanahSahamNasional amn = null;
        try{

            String hql = "from MalaysiaFormAmanahSahamNasional where user.userID = ? and month=? and year=? ";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
                amn = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return amn;
    }
    
    @Override
    public MalaysiaFormTabungHaji getUserTabungHaji(String userid, int month, int year) {
        List<MalaysiaFormTabungHaji> list = null;
        MalaysiaFormTabungHaji th = null;
        try{

            String hql = "from MalaysiaFormTabungHaji where user.userID = ?  and month=? and year=? ";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
                th = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return th;
    }
    
    @Override
    public MalaysiaFormHRDLevy getUserHRDLevy(String userid, int month, int year) {
        List<MalaysiaFormHRDLevy> list = null;
        MalaysiaFormHRDLevy cp = null;
        try{

            String hql = "from MalaysiaFormHRDLevy where user.userID = ?  and month=? and year=?  ";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
                cp = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return cp;
    }

    @Override
    public MalaysiaFormCP21 getUserCP21(String userid, int month, int year) {
        List<MalaysiaFormCP21> list = null;
        MalaysiaFormCP21 cp = null;
        try{

            String hql = "from MalaysiaFormCP21 where user.userID = ?  and month=? and year=? ";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
                cp = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return cp;
    }
    @Override
    public boolean saveCompanyFormInformation(MalaysiaCompanyForm malaysiaCompanyForm){
    	boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaCompanyForm);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
    }
    
    public MalaysiaCompanyForm getMalaysiaCompanyForm(String companyid, Integer month, Integer year){
    	List<MalaysiaCompanyForm> list = null;
    	MalaysiaCompanyForm malaysiaCompanyForm = null;
        try{

            String hql = "from MalaysiaCompanyForm where company.companyID = ? and month = ? and year = ?";
    		Object[] obj = {companyid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaCompanyForm = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaCompanyForm;
    }

    
    @Override
    public MalaysiaFormTP1 getEmployeeTP1(String userid, int month, int year){
    	List<MalaysiaFormTP1> list = null;
    	MalaysiaFormTP1 malaysiaFormTP1 = null;
        try{

            String hql = "from MalaysiaFormTP1 where useraccount.userID = ? and month = ? and year = ?";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaFormTP1 = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaFormTP1;
    }
    
    @Override
    public boolean saveMalaysiaFormTP1(MalaysiaFormTP1 malaysiaFormTP1){
    	boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaFormTP1);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
    }
    
    @Override
    public MalaysiaFormTP2 getEmployeeTP2(String userid, int month, int year){
    	List<MalaysiaFormTP2> list = null;
    	MalaysiaFormTP2 malaysiaFormTP2 = null;
        try{

            String hql = "from MalaysiaFormTP2 where useraccount.userID = ? and month = ? and year = ?";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaFormTP2 = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaFormTP2;
    }
    
    @Override
    public boolean saveMalaysiaFormTP2(MalaysiaFormTP2 malaysiaFormTP2){
    	boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaFormTP2);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
    }
    
    
    @Override
    public MalaysiaFormTP3 getEmployeeTP3(String userid, int month, int year){
    	List<MalaysiaFormTP3> list = null;
    	MalaysiaFormTP3 malaysiaFormTP3 = null;
        try{

            String hql = "from MalaysiaFormTP3 where useraccount.userID = ? and month = ? and year = ?";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaFormTP3 = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaFormTP3;
    }
    
    @Override
    public boolean saveMalaysiaFormTP3(MalaysiaFormTP3 malaysiaFormTP3){
    	boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaFormTP3);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
    }

	@Override
	public MalaysiaFormCP39 getEmployeeCP39(String userid, int month, int year) {
		List<MalaysiaFormCP39> list = null;
		MalaysiaFormCP39 malaysiaFormCP39 = null;
        try{

            String hql = "from MalaysiaFormCP39 where useraccount.userID = ? and month = ? and year = ?";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaFormCP39 = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaFormCP39;
	}

	@Override
	public boolean saveMalaysiaFormCP39(MalaysiaFormCP39 malaysiaFormCP39) {
		boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaFormCP39);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
	}

	@Override
	public List<MalaysiaFormCP39> getEmployeeCP39List(String companyid,	int month, int year) {
		List<MalaysiaFormCP39> list = null;
		try{

            String hql = "from MalaysiaFormCP39 where useraccount.user.company.companyID = ? and month = ? and year = ?";
    		Object[] obj = {companyid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
	}
	
	
	@Override
	public MalaysiaFormCP39A getEmployeeCP39A(String userid, int month, int year) {
		List<MalaysiaFormCP39A> list = null;
		MalaysiaFormCP39A malaysiaFormCP39A = null;
        try{

            String hql = "from MalaysiaFormCP39A where useraccount.userID = ? and month = ? and year = ?";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaFormCP39A = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaFormCP39A;
	}

	@Override
	public boolean saveMalaysiaFormCP39A(MalaysiaFormCP39A malaysiaFormCP39A) {
		boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaFormCP39A);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
	}

	@Override
	public List<MalaysiaFormCP39A> getEmployeeCP39AList(String companyid,	int month, int year) {
		List<MalaysiaFormCP39A> list = null;
		try{

            String hql = "from MalaysiaFormCP39A where useraccount.user.company.companyID = ? and month = ? and year = ?";
    		Object[] obj = {companyid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
	}

	@Override
	public MalaysiaFormPCB2 getEmployeePCB2(String userid, int month, int year) {
		List<MalaysiaFormPCB2> list = null;
		MalaysiaFormPCB2 malaysiaFormPCB2 = null;
        try{

            String hql = "from MalaysiaFormPCB2 where useraccount.userID = ? and month = ? and year = ?";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaFormPCB2 = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaFormPCB2;
	}
	
	
	@Override
	public List<MalaysiaFormPCB2> getEmployeePCB2List(String userid, int year) {
		List<MalaysiaFormPCB2> list = null;
        try{

            String hql = "from MalaysiaFormPCB2 where useraccount.userID = ? and year = ?";
    		Object[] obj = {userid, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
	}

	@Override
	public boolean saveMalaysiaFormPCB2(MalaysiaFormPCB2 malaysiaFormPCB2) {
		boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaFormPCB2);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
	}

	@Override
	public MalaysiaFormEA getEmployeeEA(String userid, int month, int year) {
		List<MalaysiaFormEA> list = null;
		MalaysiaFormEA malaysiaFormEA = null;
        try{

            String hql = "from MalaysiaFormEA where useraccount.userID = ? and month = ? and year = ?";
    		Object[] obj = {userid, month, year};
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, obj);
            if(list!=null && !list.isEmpty()){
            	malaysiaFormEA = list.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return malaysiaFormEA;
	}

	@Override
	public boolean saveMalaysiaFormEA(MalaysiaFormEA malaysiaFormEA) {
		boolean success = true;
    	try{
    		hibernateTemplate.save(malaysiaFormEA);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
	}


    @Override
    public boolean authorizeStatutoryFormsData(final String[] empids, final int authorizeStatus, final String formID, final int month, final int year) {
        boolean success = false;
        int numRow = 0;
        try{
            String tableName ="";
            String users="";
            if(StringUtil.equal(formID, "1")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_AMANAH_SAHAM_NASIONAL;
                users="user.userID";
            } else if(StringUtil.equal(formID, "2")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_CP39;
                users="useraccount.userID";
            } else if(StringUtil.equal(formID, "3")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_CP39A;
                users="useraccount.userID";
            } else if(StringUtil.equal(formID, "4")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_CP21;
                users="user.userID";
            } else if(StringUtil.equal(formID, "5")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_HRD_LEVY;
                users="user.userID";
            } else if(StringUtil.equal(formID, "6")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_PCB2;
                users="useraccount.userID";
            } else if(StringUtil.equal(formID, "7")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_TABUNG_HAJI;
                 users="user.userID";
            } else if(StringUtil.equal(formID, "8")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_TP1;
                users="useraccount.userID";
            } else if(StringUtil.equal(formID, "9")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_TP2;
                users="useraccount.userID";
            } else if(StringUtil.equal(formID, "10")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_TP3;
                users="useraccount.userID";
            } else if(StringUtil.equal(formID, "11")){
                tableName = MalaysianIncomeTaxConstants.STATUTORY_FORM_TABLE_POJO_NAME_EA;
                users="useraccount.userID";
            } 


            if(StringUtil.isNullOrEmpty(tableName)){
                return success=false;
            }
            
            String hql = "Update "+tableName+" set authorizeStatus= "+authorizeStatus+" where month ="+month+" and year="+year+" and "+users+" in (:userids) ";

            final String hql1 = hql;
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                @Override
                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql1);
                    if (empids != null) {

                        query.setParameterList("userids", empids);

                    }
                    numRows = query.executeUpdate();
                    return numRows;
                }

            });

            success = true;
        }catch(Exception ex){
            success = false;
            ex.printStackTrace();
        }finally{
           return success;
        }
    }
}
