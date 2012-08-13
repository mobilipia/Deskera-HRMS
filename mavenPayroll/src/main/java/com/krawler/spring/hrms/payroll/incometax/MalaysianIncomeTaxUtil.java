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

import com.krawler.common.util.StringUtil;
import com.krawler.hrms.common.HrmsPayrollConstants;
import com.krawler.hrms.common.MalaysianIncomeTaxConstants;
import com.krawler.spring.hrms.payroll.hrmsPayrollDAO;

import com.krawler.utils.json.base.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import masterDB.MalaysianUserTaxComponentHistory;

/**
 *
 * @author krawler
 */
public class MalaysianIncomeTaxUtil {

    
    public static double getEstimatedEPFAndLIC(double K, double K1, double Kt, int n){//K2
    	double K2=0;
    	try{
    		K2 = (MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC - ( K + K1 + Kt ))/ n;

    		double totalEPF = K + K1 + Kt + (K2*n);
    		if(totalEPF>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
    			K2 = K2 - (totalEPF-MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC);
    		}
            if(K1 < K2){
                K2 = K1;
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return K2;
    }

    public static double getCurrentAdditionalEPF(double Yt, double K, MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo){//kt
    	double Kt=0;
    	try{
    		if(malaysianUserIncomeTaxInfo!=null && malaysianUserIncomeTaxInfo.isCurrentEPF()){
	    		Kt = Yt*MalaysianIncomeTaxConstants.EPF_PERCENT;
	    		double totalEPF_LIC = K+Kt;
	    		if(totalEPF_LIC>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
	    			Kt = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC-K;
	    		}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return Kt;
    }

     public static double getCurrentAdditionalRemuneration(List<ComponentResourceMappingHistory> list){//Yt
    	double Yt=0;
    	try{
    		for(ComponentResourceMappingHistory component: list){
    			if(component.getComponent().getSubtype().getComponenttype()==HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION){
    				Yt+=component.getAmount();
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return Yt;
    }

     public static double getCurrentEPF (List<ComponentResourceMappingHistory> list, MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo){

        double K1=0;
    	try{
    		
    		if(malaysianUserIncomeTaxInfo!=null && malaysianUserIncomeTaxInfo.isCurrentEPF()){
	    		for(ComponentResourceMappingHistory component: list){
	    			if(component.getComponent().getSubtype().getComponenttype()==HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING){
	    				K1+=component.getAmount();
	    			}
	    		}
	    		K1=K1*MalaysianIncomeTaxConstants.EPF_PERCENT;
    		}

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return K1;
    }

     public static double getCurrentLIC (MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo){

        double currentLIC=0;

        if(malaysianUserIncomeTaxInfo!=null){
            currentLIC = malaysianUserIncomeTaxInfo.getCurrentLICAndOther();
        }

        return currentLIC;

    }

    public static Map<String,Double> getCurrentEPFAndLIC(List<ComponentResourceMappingHistory> list, double paidEPFandLIC, MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo){//K1
    	double epf=0;
    	double lic=0;
    	double bik=0;
        Map<String, Double> data = new HashMap<String, Double>();
    	try{
    		
			if(paidEPFandLIC < MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC) {
				
				epf = MalaysianIncomeTaxUtil.getCurrentEPF(list, malaysianUserIncomeTaxInfo);
				lic = MalaysianIncomeTaxUtil.getCurrentLIC(malaysianUserIncomeTaxInfo);
				bik = MalaysianIncomeTaxUtil.getBenefitsInKind(malaysianUserIncomeTaxInfo);
	
	            if((paidEPFandLIC+epf) > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
	                
	            	epf = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC - paidEPFandLIC;
	            
	            } else {
	                
	            	if((paidEPFandLIC+epf+lic) > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
	                	
	            		lic = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC - (paidEPFandLIC+epf);
	                
	            	} else {
	                    
	                	if((paidEPFandLIC+epf+lic+bik) > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){
	                        bik = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC - (paidEPFandLIC+epf+lic);
	                    }
	                }
	            }
			}

	        data.put("availedepf", epf);
	        data.put("availedlic", lic);
	        data.put("availedbik", bik);
        }catch(Exception e){
    		e.printStackTrace();
    	}
    	return data;
    }
    
    
    public static Map<String,Double> getTotalCurrentEPFAndLIC(List<ComponentResourceMappingHistory> list, MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo){//K1
    	Map<String, Double> data = new HashMap<String, Double>();
    	try{

            double epf = MalaysianIncomeTaxUtil.getCurrentEPF(list, malaysianUserIncomeTaxInfo);
            double lic = MalaysianIncomeTaxUtil.getCurrentLIC(malaysianUserIncomeTaxInfo);
            double bik = MalaysianIncomeTaxUtil.getBenefitsInKind(malaysianUserIncomeTaxInfo);

            data.put("availedepf", epf);
            data.put("availedlic", lic);
            data.put("availedbik", bik);
        }catch(Exception e){
    		e.printStackTrace();
    	}
    	return data;
    }

    public static double getCurrentEarnings(List<ComponentResourceMappingHistory> list){//Y1
    	double Y1=0;
    	try{
    		for(ComponentResourceMappingHistory component: list){
    			if(component.getComponent().getSubtype().getComponenttype()==HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING){
    				Y1+=component.getAmount();
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return Y1;
    }

    public static double getEarningsAndRemunerationPaid(List<PayrollHistory> list, MalaysianUserIncomeTaxInfo user, List<MalaysianUserTaxBenefits> benefits){//Y
    	double Y=0;
    	try{
    		for(PayrollHistory history: list){
    			if(history.getEarning()!=null){
    				Y+=history.getEarning();
    			}
    			if(history.getOtherRemuneration()!=null){
    				Y+=history.getOtherRemuneration();
    			}
    		}

            if(user !=null){
                Y+= user.getPreviousEmployerEarning();
            }
            
            for(MalaysianUserTaxBenefits mtb: benefits){//Paid BIK
            	Y+=mtb.getPaidBIK();
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return Y;
    }

    public static double getCurrentMonthZakat(MalaysianUserIncomeTaxInfo user){

        double currentMonthZakat =user.getCurrentZakat();

        return currentMonthZakat;

    }

    public static double getBenefitsInKind(MalaysianUserIncomeTaxInfo malaysianUserIncomeTaxInfo){

        double benefitInKind =0;

        if(malaysianUserIncomeTaxInfo!=null){
        	benefitInKind = malaysianUserIncomeTaxInfo.getCurrentBenefitInKind();
        }

        return benefitInKind;

    }

    public static double formatIncomeTaxAmount(double tax){
    	double amount =0;
    	try{
    		String str = String.valueOf(tax);
    		String delimiter = "\\.";
    		String arr[] = str.split(delimiter);
    		String afterDecimal = "";
    		if(arr.length>=2){
    			afterDecimal = arr[1];
    		}
    		if(afterDecimal.length()>2){
    			afterDecimal=afterDecimal.substring(0, 2);
    		}else{
    			for(int i=afterDecimal.length(); i<2; i++){
    				afterDecimal+="0";
    			}
    		}
    		
    		String amountString = "";
    		int number1=Integer.parseInt(arr[0]);
            int number2=Integer.parseInt(afterDecimal);
            double number = number2/5.0;
            number = Math.ceil(number);
            number = number*5;
            if(number>=100){
            	number1++;
            	amountString = number1+"."+0;
            }else {
            	String nums[] = String.valueOf(number).split(delimiter);
            	amountString = number1+"."+nums[0];
            }
            if(!StringUtil.isNullOrEmpty(amountString)){
            	amount = Double.parseDouble(amountString);
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return amount;
    }

    public static double getPaidOtherDeductions(MalaysianUserIncomeTaxInfo user, List<MalaysianUserTaxBenefits> userBenefits){

        double paidOtherDeduction =0;
        if(user!=null){
	        double paidOtherDeductionPreviousEmployer = user.getPreviousEmployerOtherDeduction();
	        double paidOtherDeductionCurrentEmployer =0;

	        if(userBenefits != null && !userBenefits.isEmpty()){

	            for(MalaysianUserTaxBenefits benefit : userBenefits){

	                paidOtherDeductionCurrentEmployer = paidOtherDeductionCurrentEmployer + benefit.getPaidOtherDeduction();

	            }
	        }

	        paidOtherDeduction = paidOtherDeductionPreviousEmployer + paidOtherDeductionCurrentEmployer;
        }
        return paidOtherDeduction;

    }

     public static double getPaidZakatUser(MalaysianUserIncomeTaxInfo user, List<MalaysianUserTaxBenefits> userBenefits){

        double paidZakat =0;
        double paidZakatPreviousEmployer = user.getPreviousEmployerZakat();
        double paidZakatCurrentEmployer =0;

        if(userBenefits != null && !userBenefits.isEmpty()){

            for(MalaysianUserTaxBenefits benefit : userBenefits){

                paidZakatCurrentEmployer = paidZakatCurrentEmployer + benefit.getPaidZakat();

            }
        }

        paidZakat = paidZakatPreviousEmployer + paidZakatCurrentEmployer;

        return paidZakat;

    }

     // Calculating MTD for current month using formula :( [(P - M)R + B]- (Z+X) )/n+1
    public static double getMTDForCurrentMonth (MalaysianTaxSlab taxSlab, double P, MalaysianUserIncomeTaxInfo user, List<MalaysianUserTaxBenefits> userBenefits, List<PayrollHistory> histories, Date taxdate){

        double tax =0;

        double M = 0;
        int R = 0;
        double B = 0;
        
        if(taxSlab!=null){
        	M = taxSlab.getRangeWiseTaxableAmount();
        	R = taxSlab.getTaxRate();
        	B = taxSlab.getCategoryValue();
        }
        
        tax = P - M;
        tax = tax*R;
        tax=tax/100;
        tax = tax + B;

        double benefits =0;

        double Z = MalaysianIncomeTaxUtil.getPaidZakatUser(user,userBenefits);
        double X = getPaidIncomeTax(histories, user);

        benefits = Z + X;

        tax = tax - benefits;

        int remainingWorkingMonthWithCurrentMonth = getRemainingWorkingMonth(taxdate)+1; // Add 1 for current month : n+1

        tax = tax/ remainingWorkingMonthWithCurrentMonth;

        return tax;
    }

     // Calculating MTD for current month using formula :( (PR - T)- (Z+X) )/n+1
    public static double getMTDForCurrentMonthForReturningExpertProgram (MalaysianTaxSlab taxSlab, double P, MalaysianUserIncomeTaxInfo user, List<MalaysianUserTaxBenefits> userBenefits, List<PayrollHistory> histories, Date taxdate){

        double tax =0;
        double T=getRetuningExpertSlabValue(P, user);
 
        tax = P * MalaysianIncomeTaxConstants.RETURNING_EXPERT_PROGRAM_INTEREST_RATE;
        tax = tax - T;

        double benefits =0;

        double Z = MalaysianIncomeTaxUtil.getPaidZakatUser(user,userBenefits);
        double X = getPaidIncomeTax(histories, user);

        benefits = Z + X;

        tax = tax - benefits;

        int remainingWorkingMonthWithCurrentMonth = getRemainingWorkingMonth(taxdate)+1; // Add 1 for current month : n+1

        tax = tax/ remainingWorkingMonthWithCurrentMonth;

        return tax;
    }

    public static double getRetuningExpertSlabValue (double P,  MalaysianUserIncomeTaxInfo user){
        double T=0;

        if(P< MalaysianIncomeTaxConstants.RETURNING_EXPERT_PROGRAM_SLAB_VALUE){

            if(user.getCategoryid()==MalaysianIncomeTaxConstants.CATEGORYID_MARRIED_WIFE_NOT_WORKING){

                T= MalaysianIncomeTaxConstants.RETURNING_EXPERT_PROGRAM_REBATE_WIFE_NOT_WORKING;

            } else if(user.getCategoryid()==MalaysianIncomeTaxConstants.CATEGORYID_MARRIED_WIFE_WORKING || user.getCategoryid()==MalaysianIncomeTaxConstants.CATEGORYID_SINGLE){

                T= MalaysianIncomeTaxConstants.RETURNING_EXPERT_PROGRAM_REBATE_SINGLE_OR_WIFE_WORKING;

            }

        }
        return T;
    }
      // Calculating MTD for current month using formula :( (PR)- (Z+X) )/n+1
    public static double getMTDForCurrentMonthForKnowledgeWorker (MalaysianTaxSlab taxSlab, double P, MalaysianUserIncomeTaxInfo user, List<MalaysianUserTaxBenefits> userBenefits, List<PayrollHistory> histories, Date taxdate){

        double tax =0;
        
        tax = P * MalaysianIncomeTaxConstants.KNOWLEDGE_WORKER_INTEREST_RATE;
        
        double benefits =0;

        double Z = MalaysianIncomeTaxUtil.getPaidZakatUser(user,userBenefits);
        double X = getPaidIncomeTax(histories, user);

        benefits = Z + X;

        tax = tax - benefits;

        int remainingWorkingMonthWithCurrentMonth = getRemainingWorkingMonth(taxdate)+1; // Add 1 for current month : n+1

        tax = tax/ remainingWorkingMonthWithCurrentMonth;

        return tax;
    }


    public static int getRemainingWorkingMonth(Date taxdate){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taxdate);
        int month = calendar.get(Calendar.MONTH)+1;

        return 12-month;
    }

    public static double getPaidIncomeTax(List<PayrollHistory> histories, MalaysianUserIncomeTaxInfo user){

        double paidIncomeTax =0;

        for(PayrollHistory ph : histories){
            paidIncomeTax = paidIncomeTax+ph.getIncometaxAmount();
        }
        if(user!=null){
            paidIncomeTax += user.getPreviousEmployerIncomeTax();
        }
        return paidIncomeTax;
    }

    public static Date getFinanacialDate(Date taxdate){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taxdate);
        int yr = calendar.get(Calendar.YEAR);
        calendar.set(yr, 00, 01);
        taxdate= calendar.getTime();

        return taxdate;
    }

    public static Date getEndFinanacialDate(Date taxdate){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taxdate);
        int yr = calendar.get(Calendar.YEAR);
        calendar.set(yr, 11, 31);
        taxdate= calendar.getTime();

        return taxdate;
    }

    public static double getNetMTD(double currentMTD, MalaysianUserIncomeTaxInfo user){

        double currentMonthZakat = MalaysianIncomeTaxUtil.getCurrentMonthZakat(user);
        double netMTD =0;

        if(currentMTD > currentMonthZakat){

            netMTD= currentMTD-currentMonthZakat;

        }

        return netMTD;

    }

    public static int getEmployeeStatus(MalaysianUserIncomeTaxInfo user){

        int empstatus =0;

        if(user!=null){
        	empstatus = user.getEmpStatus();
        }

        return empstatus;

    }

    public static Date getStartDateOfMonth(int year, int month) throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        String startdat = year +"-"+(month+1)+"-01";
        Date startdate = sdf.parse(startdat);

        return startdate;

    }

    public static Date getEndDateOfMonth(int year, int month) throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        String enddat = year +"-"+(month+1)+"-"+cal.getActualMaximum(Calendar.DATE);
        Date enddate = sdf.parse(enddat);

        return enddate;

    }
}
