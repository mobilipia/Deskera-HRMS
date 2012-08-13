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

import com.krawler.common.admin.User;
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

/**
 *
 * @author krawler
 */
public interface MalaysianStatutoryFormDAO {

    public boolean saveAmanahSahamNasional(String id, String icno, String accno, String amount, User user, int month, int year, int status);
    public boolean saveTabungHaji(String id,  String icno, String accno, String amount, User user, int month, int year, int status);
    public boolean saveCP21(String id,String empfilerefno, User user, Date datetoleave, String passportno, String natureofemployment, String departurereason, String correspondenceaddress,
                    Date dateofreturn, String dueamount, Date dateofform, Date salaryfrom, Date salaryto, String salaryamount, Date leavepayfrom, Date leavepayto, String leavepayamount,
                    Date bonusfrom, Date bonusto, String bonusamount, Date gratuityfrom, Date gratuityto, String gratuityamount, Date allowancefrom, Date allowanceto, String allowanceamount,
                    Date pensionfrom, Date pensionto, String pensionamount, Date residencefrom, Date residenceto, String residenceamount, Date allowanceinkindfrom, Date allowanceinkindto,
                    String allowanceinkindamount, Date pffrom, Date pfto, String pfamount, String natureofpayment, Date dateofpayment, String amounttobepaid, int month, int year, int status);
    public boolean saveHRDLevy(String id, String baseSalary, String others, String netSalary, String levy, User user, int month, int year, int status);
    public MalaysiaFormAmanahSahamNasional getUserAmanahSahamNasional(String userid, int month, int year);
    public MalaysiaFormTabungHaji getUserTabungHaji(String userid, int month, int year);
    public MalaysiaFormCP21 getUserCP21(String userid, int month, int year);
    public MalaysiaFormHRDLevy getUserHRDLevy(String userid, int month, int year);
    public boolean saveCompanyFormInformation(MalaysiaCompanyForm malaysiaCompanyForm);
    public MalaysiaCompanyForm getMalaysiaCompanyForm(String companyid, Integer month, Integer year);
    public MalaysiaFormTP1 getEmployeeTP1(String userid, int month, int year);
    public boolean saveMalaysiaFormTP1(MalaysiaFormTP1 malaysiaFormTP1);
    public MalaysiaFormTP2 getEmployeeTP2(String userid, int month, int year);
    public boolean saveMalaysiaFormTP2(MalaysiaFormTP2 malaysiaFormTP2);
    public MalaysiaFormTP3 getEmployeeTP3(String userid, int month, int year);
    public boolean saveMalaysiaFormTP3(MalaysiaFormTP3 malaysiaFormTP3);
    public MalaysiaFormCP39 getEmployeeCP39(String userid, int month, int year);
    public boolean saveMalaysiaFormCP39(MalaysiaFormCP39 malaysiaFormCP39);
    public List<MalaysiaFormCP39> getEmployeeCP39List(String companyid, int month, int year);
    public MalaysiaFormCP39A getEmployeeCP39A(String userid, int month, int year);
    public boolean saveMalaysiaFormCP39A(MalaysiaFormCP39A malaysiaFormCP39A);
    public List<MalaysiaFormCP39A> getEmployeeCP39AList(String companyid, int month, int year);
    public MalaysiaFormPCB2 getEmployeePCB2(String userid, int month, int year);
    public boolean saveMalaysiaFormPCB2(MalaysiaFormPCB2 malaysiaFormPCB2);
    public List<MalaysiaFormPCB2> getEmployeePCB2List(String companyid, int year);
    public MalaysiaFormEA getEmployeeEA(String userid, int month, int year);
    public boolean saveMalaysiaFormEA(MalaysiaFormEA malaysiaFormEA);
    public boolean authorizeStatutoryFormsData(String[] empids, int action, String formID, int month, int year);
}
