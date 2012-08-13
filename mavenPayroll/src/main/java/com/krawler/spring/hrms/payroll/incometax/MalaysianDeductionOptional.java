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

import com.krawler.common.util.StringUtil;
import com.krawler.hrms.common.MalaysianIncomeTaxConstants;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import masterDB.MalaysianUserTaxComponent;
import masterDB.MalaysianUserTaxComponentHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author krawler
 */
public class MalaysianDeductionOptional {
    private static final Log logger = LogFactory.getLog(MalaysianDeductionOptional.class);
    private static double medicalSerious = 0;
    private static double medicalExamination = 0;
    private static double monthlyInternet = 0;
    
    public static double getDeductionOptional(List<MalaysianUserTaxComponent> userComponentList, List<MalaysianUserTaxComponentHistory> componentHistory){
    
        double optDeduction =0;
        double medicalDeduction=0;

        medicalSerious = 0;
        medicalExamination = 0;
        monthlyInternet = 0;
        
        for (MalaysianUserTaxComponent component : userComponentList) {

            double deduction = 0;
            double accumulatedComponentAmount = getAccumulatedAmountForComponent(component, componentHistory);

            if (!StringUtil.isNullOrEmpty(component.getDeduction().getMethodCall())) {

                try {
                    
                    medicalDeduction = medicalDeduction + accumulatedComponentAmount;
                    Class cl = MalaysianDeductionOptional.class;
                    Object[] obj = new Object[]{component, medicalDeduction,accumulatedComponentAmount};
                    medicalDeduction = (Double) cl.getMethod(component.getDeduction().getMethodCall(), Object.class, Object.class, Object.class).invoke(cl, obj);


                }catch(NoSuchMethodException e){
                    logger.warn("NoSuchMethodException in MalaysianDeductionOptional.getDeductionOptional :", e);
                }catch(IllegalAccessException e){
                    logger.warn("IllegalAccessException in MalaysianDeductionOptional.getDeductionOptional :", e);
                }catch(InvocationTargetException e){
                    logger.warn("InvocationTargetException in MalaysianDeductionOptional.getDeductionOptional :", e);
                }catch(Exception e){
                    logger.warn("General Exception in MalaysianDeductionOptional.getDeductionOptional: ", e);
                }
            } else {

                deduction = component.getAmount();
                double maxAmount = Double.parseDouble(component.getDeduction().getAmount());
                if(accumulatedComponentAmount > maxAmount){ // If already exceed from its max limit

                    deduction = 0;

                } else {

                    if ( (deduction+accumulatedComponentAmount) > maxAmount) {
                        deduction = maxAmount-accumulatedComponentAmount;
                    }
                    optDeduction = optDeduction + deduction;
                }
                
                
            }


        }

        medicalSerious = getMedicalSeriousDeductionAmount(medicalDeduction);
        medicalExamination = getMedicalExaminationDeductionAmount(medicalDeduction+medicalSerious);
        
        optDeduction= optDeduction+medicalSerious+medicalExamination+monthlyInternet;

        return optDeduction;

    }

    public static double getMedicalSeriousDeductionAmount(double availedMedicalDeduction){
        double deduction =0;

        if(availedMedicalDeduction <= MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE){

            if((availedMedicalDeduction+medicalSerious) <= MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE){
                deduction = medicalSerious;
            } else {
                deduction = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE - availedMedicalDeduction;
            }

        }
        return deduction;
    }

    public static double getMedicalExaminationDeductionAmount(double availedMedicalDeduction){
        double deduction =0;

        if(availedMedicalDeduction <= MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE){

            if((availedMedicalDeduction+medicalExamination) <= MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE){
                deduction = medicalExamination;
            } else {
                deduction = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE - availedMedicalDeduction;
            }

        }
        return deduction;
    }

    public static double getMedicalSeriousDeduction(Object component ,Object availDeductionAmount, Object prevAvailedAmount){
        
        double availedMedicalDeducation =(Double)availDeductionAmount;
        if(component instanceof MalaysianUserTaxComponent){

            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;

            medicalSerious = comp.getAmount();
            

        }
        return availedMedicalDeducation;
    }

    public static double getMedicalExaminationDeduction(Object component ,Object availDeductionAmount, Object prevAvailedAmount){

        double availedMedicalDeducation =(Double)availDeductionAmount;
        double availedAmount =(Double)prevAvailedAmount;
        if(component instanceof MalaysianUserTaxComponent){

            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;
            
            if(availedAmount < MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP){

                medicalExamination = comp.getAmount();
                if(comp.getAmount()>MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP){
                    medicalExamination = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP;
                }
                if( (availedAmount + medicalExamination) <= MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP){
                    medicalExamination = comp.getAmount();
                } else {
                    medicalExamination = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP - availedAmount;
                }

            }

        }
        return availedMedicalDeducation;
    }

    public static double getMonthlyInternet(Object component ,Object availDeductionAmount, Object prevAvailedAmount){

        double availedMedicalDeducation =(Double)availDeductionAmount;
        double prevAvailedAmountMonthlyInternet =(Double)prevAvailedAmount;
        if(component instanceof MalaysianUserTaxComponent){

            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;

            if(prevAvailedAmountMonthlyInternet < 500){

                monthlyInternet = comp.getAmount();
                if(comp.getAmount()>500){
                    monthlyInternet = 500;
                }
                if( (prevAvailedAmountMonthlyInternet + monthlyInternet) <= 500){
                    monthlyInternet = comp.getAmount();
                } else {
                    monthlyInternet = 500 - prevAvailedAmountMonthlyInternet;
                }
            }
           
            if(comp.getAmount()>=500){
                monthlyInternet = 500;
            }

        }
        return availedMedicalDeducation;
    }

    private static double getAccumulatedAmountForComponent(MalaysianUserTaxComponent component, List<MalaysianUserTaxComponentHistory> componentHistory){

        double amount =0;

        for(MalaysianUserTaxComponentHistory compHistory : componentHistory){

            if(StringUtil.equal(component.getDeduction().getId(), compHistory.getDeduction().getId()) ){

                amount = amount+compHistory.getAmount();

            }

        }

        return amount;
    }

}
