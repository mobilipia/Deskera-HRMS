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
public class MalaysianAllowances {

    private static final Log logger = LogFactory.getLog(MalaysianAllowances.class);
    private static double subsidizedLoanEmployerContributionInIntrest = 0;
    public static double getAllowances(List<MalaysianUserTaxComponent> userComponentList, List<MalaysianUserTaxComponentHistory> componentHistory){
    
        double allowances = 0;
        double subsidizedLoanAmount = 0;
        subsidizedLoanEmployerContributionInIntrest = 0;
        
        

        for (MalaysianUserTaxComponent component : userComponentList) {

            double componentAllowance = 0;
            double accumulatedComponentAmount = getAccumulatedAmountForComponent(component, componentHistory);

             if (!StringUtil.isNullOrEmpty(component.getDeduction().getMethodCall())) {

                    try {
                        
                        subsidizedLoanAmount = subsidizedLoanAmount + accumulatedComponentAmount;
                        Class cl = MalaysianAllowances.class;
                        Object[] obj = new Object[]{component, subsidizedLoanAmount};
                        subsidizedLoanAmount = (Double) cl.getMethod(component.getDeduction().getMethodCall(), Object.class, Object.class).invoke(cl, obj);


                    } catch(NoSuchMethodException e){
                        logger.warn("NoSuchMethodException in MalaysianAllowances.getAllowances :", e);
                    }catch(IllegalAccessException e){
                        logger.warn("IllegalAccessException in MalaysianAllowances.getAllowances :", e);
                    }catch(InvocationTargetException e){
                        logger.warn("InvocationTargetException in MalaysianAllowances.getAllowances :", e);
                    }catch(Exception e){
                        logger.warn("General Exception in MalaysianAllowances.getAllowances: ", e);
                    }

             } else {

                    componentAllowance = component.getAmount();

                    double maxAllowance = Double.parseDouble(component.getDeduction().getAmount());

                    if(maxAllowance>=0){

                        if (componentAllowance > maxAllowance) {
                            componentAllowance = maxAllowance;
                        }
                    }

                    allowances = allowances + componentAllowance;

            }


        }

        double subsidizedLoan= getSubsidizedLoanForIncomeTax(subsidizedLoanAmount);

        allowances = allowances+subsidizedLoan;
        return allowances;

    }

    public static double getSubsidizedLoanAmount(Object component, Object subsidizedLoanAmount){

        double totalSubsidizedLoanAmount =(Double)subsidizedLoanAmount;

        if(component instanceof MalaysianUserTaxComponent){
            
            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;

            totalSubsidizedLoanAmount=totalSubsidizedLoanAmount+comp.getAmount();

        }

        return totalSubsidizedLoanAmount;

    }

    public static double getSubsidizedLoanEmployerContributionInIntrest(Object component, Object subsidizedLoanAmount){

        double subsidizedLoanAmnt =(Double)subsidizedLoanAmount;

        if(component instanceof MalaysianUserTaxComponent){

            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;

            subsidizedLoanEmployerContributionInIntrest = subsidizedLoanEmployerContributionInIntrest+comp.getAmount();

        }

        return subsidizedLoanAmnt;

    }

    private static double getSubsidizedLoanForIncomeTax(double subsidizedLoanAmount){

        double loan = subsidizedLoanEmployerContributionInIntrest;

        if(subsidizedLoanAmount>300000){ // If amout is greater than 3 lacs

            loan = 300000/subsidizedLoanAmount;
            loan = loan*subsidizedLoanEmployerContributionInIntrest;
        }

        return loan;
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
