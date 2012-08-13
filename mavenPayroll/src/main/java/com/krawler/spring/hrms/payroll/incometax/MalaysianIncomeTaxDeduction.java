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

import java.util.ArrayList;
import java.util.List;
import masterDB.MalaysianUserIncomeTaxInfo;
import masterDB.MalaysianUserTaxComponent;
import masterDB.MalaysianUserTaxComponentHistory;

/**
 *
 * @author krawler
 */
public class MalaysianIncomeTaxDeduction {

    public static double getIncomeTaxDeduction(List<MalaysianUserTaxComponent> userComponentList, MalaysianUserIncomeTaxInfo userinfo, List<MalaysianUserTaxComponentHistory> componentHistory){
         // D+S+Su+Du+Q*C+ Summation (LP+LP1)

        double incomeTaxDeduction =0;
        
        double compDeduction= getCompulsoryDeduction(userComponentList,userinfo);

        double optDeduction= getOptionalDeduction(userComponentList, componentHistory);

        double allowances= getUserAllowances(userComponentList, componentHistory);

        incomeTaxDeduction = compDeduction+optDeduction+allowances;

        return incomeTaxDeduction;

    }

    private static List<MalaysianUserTaxComponent> getUserComponentListByType(List<MalaysianUserTaxComponent> userComponentList, int type){
        List<MalaysianUserTaxComponent> lst = new ArrayList<MalaysianUserTaxComponent>();

        for(MalaysianUserTaxComponent component: userComponentList){

            if(component.getDeduction().getType()==type){
                lst.add(component);
            }
        }

        return lst;

    }

    private static List<MalaysianUserTaxComponentHistory> getUserComponentHistoryListByType(List<MalaysianUserTaxComponentHistory> userComponentList, int type){
        List<MalaysianUserTaxComponentHistory> lst = new ArrayList<MalaysianUserTaxComponentHistory>();

        for(MalaysianUserTaxComponentHistory component: userComponentList){

            if(component.getDeduction().getType()==type){
                lst.add(component);
            }
        }

        return lst;

    }

    private static double getCompulsoryDeduction(List<MalaysianUserTaxComponent> userComponentList, MalaysianUserIncomeTaxInfo userinfo){
        double deduction =0;

        List<MalaysianUserTaxComponent> lst = getUserComponentListByType(userComponentList,1);
        if(lst!=null){
            deduction = MalysianDeductionCompulsory.getDeductionCompulsary(lst, userinfo);
        }

        return deduction;

    }

    private static double getOptionalDeduction(List<MalaysianUserTaxComponent> userComponentList, List<MalaysianUserTaxComponentHistory> componentHistory){
        double deduction =0;

        List<MalaysianUserTaxComponent> lst = getUserComponentListByType(userComponentList,2);
        List<MalaysianUserTaxComponentHistory> lstHistory = getUserComponentHistoryListByType(componentHistory, 2);
        if(lst!=null){
            deduction = MalaysianDeductionOptional.getDeductionOptional(lst, lstHistory);
        }

        return deduction;

    }

    private static double getUserAllowances(List<MalaysianUserTaxComponent> userComponentList,List<MalaysianUserTaxComponentHistory> componentHistory){
        double deduction =0;

        List<MalaysianUserTaxComponent> lst = getUserComponentListByType(userComponentList,3);
        List<MalaysianUserTaxComponentHistory> lstHistory = getUserComponentHistoryListByType(componentHistory, 3);
        if(lst!=null){
            deduction = MalaysianAllowances.getAllowances(lst, lstHistory);
        }

        return deduction;

    }

    
    public static double getOtherDeductions(List<MalaysianUserTaxComponent> userComponentList, List<MalaysianUserTaxComponentHistory> componentHistory){

        double optDeduction= getOptionalDeduction(userComponentList,componentHistory);

        double allowances= getUserAllowances(userComponentList,componentHistory);

        return optDeduction+allowances;
    }

 
}
