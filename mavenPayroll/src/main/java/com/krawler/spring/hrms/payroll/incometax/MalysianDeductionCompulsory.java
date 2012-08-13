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
import masterDB.MalaysianUserIncomeTaxInfo;
import masterDB.MalaysianUserTaxComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 *
 * @author krawler
 */
public class MalysianDeductionCompulsory {

     private static final Log logger = LogFactory.getLog(MalysianDeductionCompulsory.class);
     public static double getDeductionCompulsary(List<MalaysianUserTaxComponent> userComponentList, MalaysianUserIncomeTaxInfo userinfo) {
    // D+S+Su+Du+Q*C
        double compDeduction =0;

        for(MalaysianUserTaxComponent component: userComponentList){

            if (!StringUtil.isNullOrEmpty(component.getDeduction().getMethodCall())) {

                try{
                    Class cl  = MalysianDeductionCompulsory.class;
                    Object[] obj = new Object[]{component, userinfo};
                    double deduction=(Double)cl.getMethod(component.getDeduction().getMethodCall(),Object.class, Object.class).invoke(cl,obj);

                    compDeduction= compDeduction+deduction;

                }catch(NoSuchMethodException e){
                    logger.warn("NoSuchMethodException in MalysianDeductionCompulsory.getDeductionCompulsary :", e);
                }catch(IllegalAccessException e){
                    logger.warn("IllegalAccessException in MalysianDeductionCompulsory.getDeductionCompulsary :", e);
                }catch(InvocationTargetException e){
                    logger.warn("InvocationTargetException in MalysianDeductionCompulsory.getDeductionCompulsary :", e);
                }catch(Exception e){
                    logger.warn("General Exception in MalysianDeductionCompulsory.getDeductionCompulsary: ", e);
                }
            }
        }
        
        return compDeduction;

    }

    public static double getIndividual(Object component, Object userInfo){
        double deduction =0;

        if(component instanceof MalaysianUserTaxComponent){
            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;
            deduction =Double.parseDouble(comp.getDeduction().getAmount());
        }
   
        return deduction;

    }

    public static double getSpouse(Object component, Object userInfo){

        double deduction =0;

        if(component instanceof MalaysianUserTaxComponent){
            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;
            MalaysianUserIncomeTaxInfo info = (MalaysianUserIncomeTaxInfo) userInfo;
            if(info !=null){

                if(info.getCategoryid()==2){
                    deduction =Double.parseDouble(comp.getDeduction().getAmount());
                }

            }

        }
        return deduction;

    }

    public static double getIndividualDisable(Object component, Object userInfo){

        double deduction =0;
        if(component instanceof MalaysianUserTaxComponent){

            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;

            if(comp.getDeduction().getDataType()==2){
                boolean disabel = comp.isChecked();
                if(disabel){
                    deduction =Double.parseDouble(comp.getDeduction().getAmount());
                }
            }
        }

        return deduction;

    }

    public static double getSpouseDisable(Object component, Object userInfo){

        double deduction =0;

        if(component instanceof MalaysianUserTaxComponent){

            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;

            if(comp.getDeduction().getDataType()==2){
                boolean disabel = comp.isChecked();
                if(disabel){
                    deduction =Double.parseDouble(comp.getDeduction().getAmount());
                }
            }
        }
        
        return deduction;

    }

    public static double getQualifyingChildren(Object component, Object userInfo){

        double deduction =0;

        if(component instanceof MalaysianUserTaxComponent){
            
            MalaysianUserTaxComponent comp = (MalaysianUserTaxComponent) component;

            double rebate = Double.parseDouble(comp.getDeduction().getAmount());

            deduction = comp.getAmount()*rebate;
        }

        return deduction;

    }

}
