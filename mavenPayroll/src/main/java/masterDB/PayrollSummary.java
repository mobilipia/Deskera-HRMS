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
package masterDB;

public class PayrollSummary {

    private String id;
    private String empname;
    private String empid;
    private String nric;
    private String grosssalary;
    private String epfnumber;
    private String epfemployer;
    private String epfemployee;
    private String month;
    private boolean deleteflag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getEpfemployee() {
        return epfemployee;
    }

    public void setEpfemployee(String epfemployee) {
        this.epfemployee = epfemployee;
    }

    public String getEpfemployer() {
        return epfemployer;
    }

    public void setEpfemployer(String epfemployer) {
        this.epfemployer = epfemployer;
    }

    public String getGrosssalary() {
        return grosssalary;
    }

    public void setGrosssalary(String grosssalary) {
        this.grosssalary = grosssalary;
    }

    public String getEpfnumber() {
        return epfnumber;
    }

    public void setEpfnumber(String epfnumber) {
        this.epfnumber = epfnumber;
    }

    
    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public boolean isDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(boolean deleteflag) {
        this.deleteflag = deleteflag;
    }

    
    
}
