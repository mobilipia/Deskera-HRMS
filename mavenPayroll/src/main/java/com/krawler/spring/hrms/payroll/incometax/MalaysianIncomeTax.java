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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import masterDB.ComponentResourceMappingHistory;
import masterDB.MalaysianTaxSlab;
import masterDB.MalaysianUserIncomeTaxInfo;
import masterDB.MalaysianUserTaxBenefits;
import masterDB.MalaysianUserTaxComponent;
import masterDB.PayrollHistory;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.hrms.common.HrmsPayrollConstants;
import com.krawler.hrms.common.MalaysianIncomeTaxConstants;
import com.krawler.spring.hrms.payroll.hrmsPayrollDAO;

import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Map;
import masterDB.MalaysianUserTaxComponentHistory;

/**
 *
 * @author krawler
 */
public class MalaysianIncomeTax implements IncomeTax{

    private HibernateTemplate hibernateTemplate;
    private MalaysianIncomeTaxDAO malaysianIncomeTaxDAO;
    private hrmsPayrollDAO hrmsPayrollDAOObj;
    private String userid;
    private Date financialDate;
    private int frequency;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    public void setMalaysianIncomeTaxDAO(MalaysianIncomeTaxDAO malaysianIncomeTaxDAO) {
		this.malaysianIncomeTaxDAO = malaysianIncomeTaxDAO;
	}

    public void setHrmsPayrollDAO(hrmsPayrollDAO hrmsPayrollDAOObj) {
        this.hrmsPayrollDAOObj = hrmsPayrollDAOObj;
    }
    
    @Override
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    @Override
    public void setFinancialDate(Date financialDate) {
        this.financialDate = financialDate;
    }

    @Override
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public double getEarnings() {
        MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo = getUserInfo();
        
        int n = MalaysianIncomeTaxUtil.getRemainingWorkingMonth(this.financialDate);
    	List<PayrollHistory> histories = malaysianIncomeTaxDAO.getPayrollHistories(this.userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate), MalaysianIncomeTaxUtil.getEndFinanacialDate(this.financialDate));
    	List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
    	List<MalaysianUserTaxBenefits> benefits = getUserBenefits();
    	double Y = MalaysianIncomeTaxUtil.getEarningsAndRemunerationPaid(histories, malaysianUserIncomeTaxInfo, benefits);
    	double K = getEPFAndLICPaid(histories, malaysianUserIncomeTaxInfo);
    	if(K>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
    		K=MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC;
    	}
    	
    	double Y1 = MalaysianIncomeTaxUtil.getCurrentEarnings(components);

        Y1 = Y1 + MalaysianIncomeTaxUtil.getBenefitsInKind(malaysianUserIncomeTaxInfo);

        
        Map<String, Double> data = MalaysianIncomeTaxUtil.getCurrentEPFAndLIC(components, K, malaysianUserIncomeTaxInfo);
        double K1 = data.get("availedepf")+data.get("availedlic")+data.get("availedbik");
    	double Yt = 0;//getCurrentAdditionalRemuneration(components);
    	double Kt = 0;//getCurrentAdditionalEPFAndLIC(Yt);
    	double Y2 = Y1;
    	double K2 = MalaysianIncomeTaxUtil.getEstimatedEPFAndLIC(K, K1, Kt, n);
    	
    	double earnings = ((Y-K)+(Y1-K1)+( (Y2-K2)*n ))+(Yt-Kt); 
    	return earnings;
    }

    @Override
    public double getIncomeTaxDeductions() { 
        List<MalaysianUserTaxComponent> lst =  getUserComponents();
        List<MalaysianUserTaxComponentHistory> componentHistory =  getUserComponentHistory();
        MalaysianUserIncomeTaxInfo user = getUserInfo();

        double incomeTaxDeduction = 0;
        double paidOtherDeduction=0;
        if(lst!=null){
            
            incomeTaxDeduction = MalaysianIncomeTaxDeduction.getIncomeTaxDeduction(lst,user,componentHistory);
            
            List<MalaysianUserTaxBenefits> userBenefits= getUserBenefits();
            paidOtherDeduction = MalaysianIncomeTaxUtil.getPaidOtherDeductions(user, userBenefits);
        }
        
        incomeTaxDeduction = incomeTaxDeduction + paidOtherDeduction;
        return incomeTaxDeduction;
    }

    @Override
    public double getTaxableIncome() { // P
        double taxableIncome=0;

        taxableIncome = getEarnings()-getIncomeTaxDeductions();

        return taxableIncome;
    }

    @Override
    public double getTax() { 
    	double tax=0;
        
        MalaysianUserIncomeTaxInfo user = getUserInfo();
        
        int empstatus = MalaysianIncomeTaxUtil.getEmployeeStatus(user);

        if(empstatus==MalaysianIncomeTaxConstants.STATUSID_RESIDENT || empstatus==MalaysianIncomeTaxConstants.STATUSID_RETURNING_EXPERT_PROGRAM || empstatus==MalaysianIncomeTaxConstants.STATUSID_KNOWLEDGE_WORKER ) {
            
            tax = getTaxforResident(user);

        } else if(empstatus==MalaysianIncomeTaxConstants.STATUSID_NON_RESIDENT){ // Non-Resident

            tax = getTaxforNonResident(user);

        }
        return tax;
    }	

    

    public double getTaxforResident(MalaysianUserIncomeTaxInfo user){

        double tax=0;
        double incomeTax=0;

        double taxableAmount = getTaxableIncome();
        double additionalRenum=0;
        List<PayrollHistory> histories = malaysianIncomeTaxDAO.getPayrollHistories(this.userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate), MalaysianIncomeTaxUtil.getEndFinanacialDate(this.financialDate));
        List<MalaysianUserTaxBenefits> userBenefits= getUserBenefits();
        MalaysianTaxSlab taxSlab =null;
        if(user!=null){
            int userCategoryid = user.getCategoryid();

            taxSlab = getIncomeTaxSlab(taxableAmount, userCategoryid);

            //if(taxSlab!=null){
                
                double currentMTD=0;
                
                if(user.getEmpStatus()==MalaysianIncomeTaxConstants.STATUSID_RESIDENT){
                    
                    currentMTD = MalaysianIncomeTaxUtil.getMTDForCurrentMonth(taxSlab, taxableAmount, user, userBenefits, histories, this.financialDate);
                    additionalRenum =  getAdditionalRemuneration(taxSlab ,user, taxableAmount, histories, userBenefits);

                } else if(user.getEmpStatus()==MalaysianIncomeTaxConstants.STATUSID_RETURNING_EXPERT_PROGRAM){

                    currentMTD = MalaysianIncomeTaxUtil.getMTDForCurrentMonthForReturningExpertProgram(taxSlab, taxableAmount, user, userBenefits, histories, this.financialDate);
                    additionalRenum =  getAdditionalRemunerationForReturningExpert(currentMTD , user, histories, userBenefits);
                    
                } else if(user.getEmpStatus()==MalaysianIncomeTaxConstants.STATUSID_KNOWLEDGE_WORKER){

                    currentMTD = MalaysianIncomeTaxUtil.getMTDForCurrentMonthForKnowledgeWorker(taxSlab, taxableAmount, user, userBenefits, histories, this.financialDate);
                    additionalRenum =  getAdditionalRemunerationForKnowledgeWorker(currentMTD ,user, histories, userBenefits);

                }
                    

                if(currentMTD>=10){

                    incomeTax = MalaysianIncomeTaxUtil.getNetMTD(currentMTD, user);

                }
            //}
        }
       
       incomeTax = incomeTax+additionalRenum;

       tax = MalaysianIncomeTaxUtil.formatIncomeTaxAmount(incomeTax);

       return tax;
    }

    public double getTaxforNonResident(MalaysianUserIncomeTaxInfo user){

        double tax=0;
        List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
    	
    	double earnings = MalaysianIncomeTaxUtil.getCurrentEarnings(components);

        double bonus = MalaysianIncomeTaxUtil.getCurrentAdditionalRemuneration(components);

        earnings = earnings + bonus;

        tax = earnings * MalaysianIncomeTaxConstants.NON_RESIDENT_EMPLOYEE_TAX_COEFFICIENT;

        return tax;
    }
    
    public double getAdditionalRemuneration(MalaysianTaxSlab taxSlabWAR ,MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo, double taxableAmountWAR, List<PayrollHistory> histories, List<MalaysianUserTaxBenefits> userBenefits){
    	double totalMTDForYear=0;
        double MTDForAdditionalRemuneration =0;
    	try{
    		List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
            double Yt = MalaysianIncomeTaxUtil.getCurrentAdditionalRemuneration(components);
            if(Yt>0){
            	
            	int n = MalaysianIncomeTaxUtil.getRemainingWorkingMonth(this.financialDate);
            	List<MalaysianUserTaxBenefits> benefits = getUserBenefits();
            	double Y = MalaysianIncomeTaxUtil.getEarningsAndRemunerationPaid(histories, malaysianUserIncomeTaxInfo, benefits);
            	double K = getEPFAndLICPaid(histories, malaysianUserIncomeTaxInfo);
            	if(K>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
            		K=MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC;
            	}
            	
            	double Y1 = MalaysianIncomeTaxUtil.getCurrentEarnings(components);
                
                Y1 = Y1 + MalaysianIncomeTaxUtil.getBenefitsInKind(malaysianUserIncomeTaxInfo);
                
                Map<String, Double> data = MalaysianIncomeTaxUtil.getCurrentEPFAndLIC(components, K, malaysianUserIncomeTaxInfo);
            	
                double K1 = data.get("availedepf")+data.get("availedlic")+data.get("availedbik");
            	double Kt = MalaysianIncomeTaxUtil.getCurrentAdditionalEPF(Yt, K, malaysianUserIncomeTaxInfo);
            	double Y2 = Y1;
            	double K2 = MalaysianIncomeTaxUtil.getEstimatedEPFAndLIC(K, K1, Kt, n);
            	double earnings = ((Y-K)+(Y1-K1)+( (Y2-K2)*n ))+(Yt-Kt);
            	double P = earnings-getIncomeTaxDeductions();
            	double R1=0;
            	double M1=0;
            	double B1=0;
            	if(taxSlabWAR!=null){
            		R1 = taxSlabWAR.getTaxRate();
            		M1 = taxSlabWAR.getRangeWiseTaxableAmount();
            		B1 = taxSlabWAR.getCategoryValue();
            	}

                
            	double paidZakat1 = MalaysianIncomeTaxUtil.getPaidZakatUser(malaysianUserIncomeTaxInfo, userBenefits);
                
            	double paidIncomeTax1 = MalaysianIncomeTaxUtil.getPaidIncomeTax(histories, malaysianUserIncomeTaxInfo);
            	R1=R1/100;
            	
            	MalaysianTaxSlab taxSlab = getIncomeTaxSlab(P, malaysianUserIncomeTaxInfo.getCategoryid());
            	double M = taxSlab.getRangeWiseTaxableAmount(); 
                double R = taxSlab.getTaxRate();
                R=R/100;
                double B = taxSlab.getCategoryValue();
                double totalTaxForYear = (P-M)*R+B;
                
                double currentMTDperMonth = (taxableAmountWAR-M1)*R1+B1;
            	currentMTDperMonth = currentMTDperMonth-(paidIncomeTax1+paidZakat1);
            	currentMTDperMonth = currentMTDperMonth/(n+1);
            	totalMTDForYear = paidIncomeTax1 + (currentMTDperMonth*(n+1));
                
                MTDForAdditionalRemuneration = totalTaxForYear-totalMTDForYear-paidZakat1;//+getPaidZakat(malaysianUserIncomeTaxInfo);
                
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return MTDForAdditionalRemuneration;
    }

    public double getAdditionalRemunerationForReturningExpert(double currentMTD ,MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo, List<PayrollHistory> histories, List<MalaysianUserTaxBenefits> userBenefits){
    	
        double MTDForAdditionalRemuneration =0;
    	try{
    		List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
            double Yt = MalaysianIncomeTaxUtil.getCurrentAdditionalRemuneration(components);
            if(Yt>0){

            	int n = MalaysianIncomeTaxUtil.getRemainingWorkingMonth(this.financialDate);
            	List<MalaysianUserTaxBenefits> benefits = getUserBenefits();
            	double Y = MalaysianIncomeTaxUtil.getEarningsAndRemunerationPaid(histories, malaysianUserIncomeTaxInfo, benefits);
            	double K = getEPFAndLICPaid(histories, malaysianUserIncomeTaxInfo);
            	if(K>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
            		K=MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC;
            	}

            	double Y1 = MalaysianIncomeTaxUtil.getCurrentEarnings(components);

                Y1 = Y1 + MalaysianIncomeTaxUtil.getBenefitsInKind(malaysianUserIncomeTaxInfo);

                Map<String, Double> data = MalaysianIncomeTaxUtil.getCurrentEPFAndLIC(components, K, malaysianUserIncomeTaxInfo);

                double K1 = data.get("availedepf")+data.get("availedlic")+data.get("availedbik");
            	double Kt = MalaysianIncomeTaxUtil.getCurrentAdditionalEPF(Yt, K, malaysianUserIncomeTaxInfo);
            	double Y2 = Y1;
            	double K2 = MalaysianIncomeTaxUtil.getEstimatedEPFAndLIC(K, K1, Kt, n);
            	double earnings = ((Y-K)+(Y1-K1)+( (Y2-K2)*n ))+(Yt-Kt);
            	double P = earnings-getIncomeTaxDeductions();
            	double T = MalaysianIncomeTaxUtil.getRetuningExpertSlabValue(P, malaysianUserIncomeTaxInfo);
                
                P = P * MalaysianIncomeTaxConstants.RETURNING_EXPERT_PROGRAM_INTEREST_RATE;
                P = P-T;

                double MTD = currentMTD * (n+1);
                MTD = MTD + MalaysianIncomeTaxUtil.getPaidIncomeTax(histories, malaysianUserIncomeTaxInfo);
                MTD = MTD + MalaysianIncomeTaxUtil.getPaidZakatUser(malaysianUserIncomeTaxInfo, userBenefits);
                
                MTDForAdditionalRemuneration = P - MTD ;

            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return MTDForAdditionalRemuneration;
    }

    public double getAdditionalRemunerationForKnowledgeWorker(double currentMTD ,MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo, List<PayrollHistory> histories, List<MalaysianUserTaxBenefits> userBenefits){
    	
        double MTDForAdditionalRemuneration =0;
    	try{
    		List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
            double Yt = MalaysianIncomeTaxUtil.getCurrentAdditionalRemuneration(components);
            if(Yt>0){

            	int n = MalaysianIncomeTaxUtil.getRemainingWorkingMonth(this.financialDate);
            	List<MalaysianUserTaxBenefits> benefits = getUserBenefits();
            	double Y = MalaysianIncomeTaxUtil.getEarningsAndRemunerationPaid(histories, malaysianUserIncomeTaxInfo, benefits);
            	double K = getEPFAndLICPaid(histories, malaysianUserIncomeTaxInfo);
            	if(K>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
            		K=MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC;
            	}

            	double Y1 = MalaysianIncomeTaxUtil.getCurrentEarnings(components);

                Y1 = Y1 + MalaysianIncomeTaxUtil.getBenefitsInKind(malaysianUserIncomeTaxInfo);

                Map<String, Double> data = MalaysianIncomeTaxUtil.getCurrentEPFAndLIC(components, K, malaysianUserIncomeTaxInfo);

                double K1 = data.get("availedepf")+data.get("availedlic")+data.get("availedbik");
            	double Kt = MalaysianIncomeTaxUtil.getCurrentAdditionalEPF(Yt, K, malaysianUserIncomeTaxInfo);
            	double Y2 = Y1;
            	double K2 = MalaysianIncomeTaxUtil.getEstimatedEPFAndLIC(K, K1, Kt, n);
            	double earnings = ((Y-K)+(Y1-K1)+( (Y2-K2)*n ))+(Yt-Kt);
            	double P = earnings-getIncomeTaxDeductions();
            	P = P * MalaysianIncomeTaxConstants.KNOWLEDGE_WORKER_INTEREST_RATE;

                double MTD = currentMTD * (n+1);
                MTD = MTD + MalaysianIncomeTaxUtil.getPaidIncomeTax(histories, malaysianUserIncomeTaxInfo);
                MTD = MTD + MalaysianIncomeTaxUtil.getPaidZakatUser(malaysianUserIncomeTaxInfo, userBenefits);
                
                MTDForAdditionalRemuneration = P - MTD;

            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return MTDForAdditionalRemuneration;
    }
    
    @Override
    public void afterProcessSalary(String payrollHistoryid) { 
    	List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
        double zakat = 0;
        MalaysianUserIncomeTaxInfo user = getUserInfo();
        
        if(user !=null){
            zakat = user.getCurrentZakat();
        }

        List<PayrollHistory> histories = malaysianIncomeTaxDAO.getPayrollHistories(this.userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate), MalaysianIncomeTaxUtil.getEndFinanacialDate(this.financialDate));
    	double K = getEPFAndLICPaid(histories, user);

        Map<String, Double> data = MalaysianIncomeTaxUtil.getTotalCurrentEPFAndLIC(components, user);
        
        double epf = data.get("availedepf");
        double lic = data.get("availedlic");
        double bik = data.get("availedbik");
        
        double Yt = MalaysianIncomeTaxUtil.getCurrentAdditionalRemuneration(components);
        double additionalRemuneration = MalaysianIncomeTaxUtil.getCurrentAdditionalEPF(Yt, K, user);
        epf = epf + additionalRemuneration;
        
        
        List<MalaysianUserTaxComponent> lst =  getUserComponents();
        
        List<MalaysianUserTaxComponentHistory> lstHistory= getUserComponentHistory();

        double otherDeduction = MalaysianIncomeTaxDeduction.getOtherDeductions(lst,lstHistory);
        

        malaysianIncomeTaxDAO.saveUserTaxBenefits(payrollHistoryid, zakat, epf, lic, otherDeduction, bik);

        malaysianIncomeTaxDAO.saveUserTaxComponentHistory(payrollHistoryid, lst);
    }

    public List<MalaysianUserTaxComponentHistory> getUserComponentHistory(){

        List<MalaysianUserTaxComponentHistory> userComponentsHistory = null;

        Date startdate = MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate);
        Date enddate = MalaysianIncomeTaxUtil.getEndFinanacialDate(this.financialDate);

        userComponentsHistory = malaysianIncomeTaxDAO.getUserIncomeTaxComponentHistory(this.userid, startdate, enddate);

        return userComponentsHistory;
    }

    public List<MalaysianUserTaxComponent> getUserComponents(){

        List<MalaysianUserTaxComponent> userComponents = null;

        Date taxdate = MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate);

        userComponents = malaysianIncomeTaxDAO.getUserIncomeTaxComponent(this.userid, taxdate, true);

        return userComponents;
    }

    public MalaysianUserIncomeTaxInfo getUserInfo(){

        List<MalaysianUserIncomeTaxInfo> userinfo = null;
        MalaysianUserIncomeTaxInfo user = null;

        Date taxdate = MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate);

        userinfo = malaysianIncomeTaxDAO.getUserIncomeTaxInfo(this.userid, taxdate);
        if(userinfo!=null && !userinfo.isEmpty()){
            user = userinfo.get(0);
        }

        return user;
    }

    public MalaysianTaxSlab getIncomeTaxSlab(double taxableAmount, int categoryid){

        List<MalaysianTaxSlab> taxSlabList = null;
        MalaysianTaxSlab taxslab = null;

        String category = Integer.toString(categoryid);

        taxSlabList = malaysianIncomeTaxDAO.getIncomeTaxSlab(taxableAmount, category);

        if(taxSlabList!=null && !taxSlabList.isEmpty()){

            taxslab = taxSlabList.get(0);

        }

        return taxslab;
    }

    public List<MalaysianUserTaxBenefits> getUserBenefits(){

        List<MalaysianUserTaxBenefits> userBenefits = null;

        Date startdate = MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate);
	    Date enddate = MalaysianIncomeTaxUtil.getEndFinanacialDate(this.financialDate);

	    userBenefits =  malaysianIncomeTaxDAO.getUserBenefits(startdate, enddate, this.userid);

        return userBenefits;
    }

    public double getEPFAndLICPaid(List<PayrollHistory> list, MalaysianUserIncomeTaxInfo user){//K
    	double K=0;
    	String ids = "";
    	try{
    		for(PayrollHistory history: list){
    			ids+=("'"+history.getHistoryid()+"',");
    		}
    		if(ids.length()>0){
    			List<MalaysianUserTaxBenefits> taxBenefits = malaysianIncomeTaxDAO.getMalaysianUserTaxBenefits(ids.substring(0, ids.length()-1));
    			for(MalaysianUserTaxBenefits obj: taxBenefits){
    				K+=obj.getPaidEPF();
    				K+=obj.getPaidLICAndOther();
    				K+=obj.getPaidBIK()*MalaysianIncomeTaxConstants.EPF_PERCENT;
    			}
    		}
            if(user!=null){
                K+= user.getPreviousEmployerEPF();
                K+= user.getPreviousEmployerLIC();
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return K;
    }
    
    public JSONObject getALlDataForDebug(){
    	JSONObject jobj = new JSONObject();
    	try{
            MalaysianUserIncomeTaxInfo user = getUserInfo();
            
            double earning = getEarnings();

            jobj.put("earning", earning);

            double deduction = getIncomeTaxDeductions();

            jobj.put("deduction", deduction);
            
            double taxableincome = earning - deduction;

            jobj.put("P", taxableincome);
            List<PayrollHistory> histories = malaysianIncomeTaxDAO.getPayrollHistories(this.userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate), MalaysianIncomeTaxUtil.getEndFinanacialDate(this.financialDate));
            double paidIncometax = MalaysianIncomeTaxUtil.getPaidIncomeTax(histories, user);

            jobj.put("paidincometax", paidIncometax);

            List<MalaysianUserTaxBenefits> userBenefits= getUserBenefits();
            if(user!=null){

                
                
                double paidzakat = MalaysianIncomeTaxUtil.getPaidZakatUser(user, userBenefits);
                jobj.put("paidzakat", paidzakat);

                
                double paidotherdeduction = MalaysianIncomeTaxUtil.getPaidOtherDeductions(user, userBenefits);
                jobj.put("paidotherdeduction", paidotherdeduction);


                double currentCompOptandAllowanceDeduction = deduction - paidotherdeduction;
                jobj.put("currentcompoptandallowanceDeduction", currentCompOptandAllowanceDeduction);
                
                double currentzakat = MalaysianIncomeTaxUtil.getCurrentMonthZakat(user);
                jobj.put("currentzakat", currentzakat);

                double currentbik= MalaysianIncomeTaxUtil.getBenefitsInKind(user);
                jobj.put("currentbik", currentbik);
                
            }
            
        double incomeTax=0;


        MalaysianTaxSlab taxSlab =null;
        if(user!=null){
            int userCategoryid = user.getCategoryid();

            taxSlab = getIncomeTaxSlab(taxableincome, userCategoryid);

            if(taxSlab!=null){

                double currentMTD = MalaysianIncomeTaxUtil.getMTDForCurrentMonth(taxSlab, taxableincome, user, userBenefits, histories, this.financialDate);
                jobj.put("currentmtd", currentMTD);

                if(currentMTD>=10){

                    incomeTax = MalaysianIncomeTaxUtil.getNetMTD(currentMTD, user);
                    

                }
            }
        }


       double additionalRenum =  getAdditionalRemuneration(taxSlab ,user, taxableincome, histories, userBenefits);
       jobj.put("additionalrenum", additionalRenum);
       
       incomeTax = incomeTax+additionalRenum;
       jobj.put("netmtd", incomeTax);

       
    	List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
    	List<MalaysianUserTaxBenefits> benefits = getUserBenefits();
    	double Y = MalaysianIncomeTaxUtil.getEarningsAndRemunerationPaid(histories, user, benefits);
        jobj.put("Y", Y);
    	
        double K = getEPFAndLICPaid(histories, user);
        if(K>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
    		K=MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC;
    	}
        jobj.put("K", K);
        
    	double Y1 = MalaysianIncomeTaxUtil.getCurrentEarnings(components);
        jobj.put("Y1BeforeBIK", Y1);
        
        
        Y1 = Y1 + MalaysianIncomeTaxUtil.getBenefitsInKind(user);
        jobj.put("Y1", Y1);

        Map<String, Double> data = MalaysianIncomeTaxUtil.getCurrentEPFAndLIC(components, K, user);
        double K1 = data.get("availedepf")+data.get("availedlic")+data.get("availedbik");
        jobj.put("K1", K1);

        int n = MalaysianIncomeTaxUtil.getRemainingWorkingMonth(this.financialDate);
        double Yt = 0;
        jobj.put("Yt", Yt);
    	
        double Kt = 0;
        jobj.put("Kt", Kt);
        
    	double Y2 = Y1;
        jobj.put("Y2", Y2);
    	
        double K2 = MalaysianIncomeTaxUtil.getEstimatedEPFAndLIC(K, K1, Kt, n);
        jobj.put("K2", K2);

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return jobj;
    }
    
    @Override
    public List<PayrollHistory> getPayrollHistories(String userid, int status, Date startDate, Date endDate){
    	List<PayrollHistory> histories = null;
    	try{
    		histories = malaysianIncomeTaxDAO.getPayrollHistories(userid, status, startDate, endDate);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return histories;
    }

    @Override
    public Map<String, Double> getEPF(List<PayrollHistory> list, PayrollHistory payrollHistory){
    	Map<String, Double> map = new HashMap<String, Double>();
    	double paidEPF = 0;
    	double currentEPF = 0;
    	String ids = "";
    	try{
    		if(list!=null&&list.size()>0){
	    		for(PayrollHistory history: list){
	    			ids+=("'"+history.getHistoryid()+"',");
	    		}
    		}else{
    			ids+=("'"+payrollHistory.getHistoryid()+"',");
    		}
    		if(ids.length()>0){
    			List<MalaysianUserTaxBenefits> taxBenefits = malaysianIncomeTaxDAO.getMalaysianUserTaxBenefits(ids.substring(0, ids.length()-1));
    			for(MalaysianUserTaxBenefits obj: taxBenefits){
    				paidEPF+=obj.getPaidEPF();
    				if(obj.getPayrollHistory().getHistoryid().equals(payrollHistory.getHistoryid())){
    					currentEPF = obj.getPaidEPF();
    				}
    			}
    		}
    		map.put("paidEPF", paidEPF);
    		map.put("currentEPF", currentEPF);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return map;
    }
    
    public boolean countryLevelValidation(){
    	boolean valid = true;
    	try{
    		List<PayrollHistory> list = malaysianIncomeTaxDAO.getPayrollHistory(userid, frequency, MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate), this.financialDate, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);
    		if(list!=null && list.size()>0){
    			valid = false;
    		}else{
    			valid = true;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return valid;
    }
    
    @Override
    public Map<String, Double> getUserTaxBenefitsData(PayrollHistory history){
    	Map<String, Double> map = new HashMap<String, Double>();
    	try{
    		List<MalaysianUserTaxBenefits> benefits = null;
    		if(history!=null && history.getSalarystatus()==HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL){
    			benefits = malaysianIncomeTaxDAO.getMalaysianUserTaxBenefits("'"+history.getHistoryid()+"'");
    		}
    		
    		if(benefits!=null){
	    		for(MalaysianUserTaxBenefits mtb: benefits){
	    			map.put("epf", mtb.getPaidEPF());
	    			break;
	    		}
    		}else{
    			MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo = getUserInfo();
    			List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, financialDate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
    			double total = 0;
    			double epf = 0;
    			double bik = 0;
    			Map<String, Double> mapValue = MalaysianIncomeTaxUtil.getTotalCurrentEPFAndLIC(components, malaysianUserIncomeTaxInfo);
    			if(mapValue!=null){
    				if(mapValue.get("availedepf")!=null){
    					epf = mapValue.get("availedepf");
    				}
    				if(mapValue.get("availedbik")!=null){
    					bik = mapValue.get("availedbik")*MalaysianIncomeTaxConstants.EPF_PERCENT;
    				}
    			}
    			List<PayrollHistory> histories = malaysianIncomeTaxDAO.getPayrollHistories(this.userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, MalaysianIncomeTaxUtil.getFinanacialDate(this.financialDate), MalaysianIncomeTaxUtil.getEndFinanacialDate(this.financialDate));
    			double K = getEPFAndLICPaid(histories, malaysianUserIncomeTaxInfo);

    			double Yt = MalaysianIncomeTaxUtil.getCurrentAdditionalRemuneration(components);
    	        double additionalRemuneration = MalaysianIncomeTaxUtil.getCurrentAdditionalEPF(Yt, K, malaysianUserIncomeTaxInfo);
    	        total = epf + bik + additionalRemuneration;
    	        map.put("epf", total);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return map;
    }

    @Override
    public boolean checkForDeclarationFormFilled(){
    	boolean formFilled = false;
    	try{
    		List<MalaysianUserTaxComponent> list =  getUserComponents();

            if(list!=null && list.size()>0){
    			formFilled = true;
    		}

        }catch(Exception e){
    		e.printStackTrace();
    	}
    	return formFilled;
    }
}
