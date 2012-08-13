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

import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

import masterDB.PayrollHistory;

/**
 *
 * @author krawler
 */
public interface IncomeTax {

    public double getEarnings();
    public double getIncomeTaxDeductions();
    public double getTaxableIncome();
    public double getTax();
    public void setUserid(String userid);
    public void setFinancialDate(Date financialDate);
    public void afterProcessSalary(String  payrollHistoryid);
    public void setFrequency(int frequency);
    public List<PayrollHistory> getPayrollHistories(String userid, int status, Date startDate, Date endDate);
    public Map<String, Double> getEPF(List<PayrollHistory> list, PayrollHistory payrollHistory);
    public JSONObject getALlDataForDebug();
    public boolean countryLevelValidation();
    public Map<String, Double> getUserTaxBenefitsData(PayrollHistory history);
    public boolean checkForDeclarationFormFilled();
}
