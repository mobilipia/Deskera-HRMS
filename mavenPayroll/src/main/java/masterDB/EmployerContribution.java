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

import com.krawler.common.admin.Company;

/**
 *
 * @author krawler
 */
public class EmployerContribution {
     private String id;
     private Company companyid;
     private String empcontritype;
     private Integer rate;
     private String empcontricode;
     private double  cash;
     private boolean isdefault;
     private Integer computeon;
     private String expr;

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public Company getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Company companyid) {
        this.companyid = companyid;
    }

    public Integer getComputeon() {
        return computeon;
    }

    public void setComputeon(Integer computeon) {
        this.computeon = computeon;
    }

    public String getEmpcontricode() {
        return empcontricode;
    }

    public void setEmpcontricode(String empcontricode) {
        this.empcontricode = empcontricode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpcontritype() {
        return empcontritype;
    }

    public void setEmpcontritype(String empcontritype) {
        this.empcontritype = empcontritype;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public boolean getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

}
