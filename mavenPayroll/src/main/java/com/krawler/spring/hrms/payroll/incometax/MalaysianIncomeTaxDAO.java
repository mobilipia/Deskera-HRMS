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
import masterDB.MalaysianUserTaxComponentHistory;
import masterDB.PayrollHistory;

public interface MalaysianIncomeTaxDAO {
	public List<MalaysianDeduction> getMalaysianDeductionComponents( Date year);
	public boolean saveUserIncomeTaxInformation(Map<MalaysianDeduction, String> malaysianDeductionIds, String userid, Date year);
    public List<MalaysianUserTaxComponent> getUserIncomeTaxComponent(String userid, Date year, boolean submitted);
    public List<MalaysianUserTaxComponentHistory> getUserIncomeTaxComponentHistory(String userid, Date startdate, Date enddate);
    public List<MalaysianUserTaxComponentHistory> getUserIncomeTaxComponentHistoryForParticularComponentByPassingComponentsUniqueCode(String userid, Date startdate, Date enddate, int uniquecode);
    public List<MalaysianUserTaxComponent> getUserIncomeTaxComponentByPassingComponentsUniqueCode(String userid, Date year, int uniquecode);
    public List<MalaysianUserIncomeTaxInfo> getUserIncomeTaxInfo(String userid, Date year);
    public List<MalaysianTaxSlab> getIncomeTaxSlab(double taxableAmount, String categoryid);
    public boolean saveUserInformation(String userid, Date year, int category, double prevEarning,double prevIncomeTax,double prevEPF,double prevLIC,double prevZakat,boolean epf,double lic,double zakat,double bik, double prevOtherDeduction, int empStatus);
    public MalaysianUserIncomeTaxInfo getUserInformation(String userid, Date year);
    public List<MalaysianUserTaxBenefits> getMalaysianUserTaxBenefits(String ids);
    public List<PayrollHistory> getPayrollHistories(String userid, int status, Date startDate, Date endDate);
    public List<MalaysianUserTaxBenefits> getUserBenefits(Date startdate, Date enddate, String userid);
    public boolean saveUserTaxBenefits(String payrollhistoryid, double zakat, double epf, double lic, double otherdeduction, double bik);
    public boolean saveUserTaxComponentHistory(String payrollhistoryid, List<MalaysianUserTaxComponent> lst);
    public List<PayrollHistory> getPayrollHistory(String userid, int frequency, Date startDate, Date endDate, int status);
}
