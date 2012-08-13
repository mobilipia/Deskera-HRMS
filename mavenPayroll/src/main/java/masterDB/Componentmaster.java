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
import java.util.Date;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.CostCenter;
import com.krawler.hrms.master.MasterData;
/**
 *
 * @author sagar
 */
public class Componentmaster implements java.io.Serializable {
    private String compid;
    private Company company;
    private String code;
    private Date sdate;
    private Date edate;
    private String description;
    private MasterData subtype;
    private int frequency;
    private CostCenter costcenter;
    private MasterData paymentterm;
    private double amount;
    private boolean isadjust;
    private boolean isdefault;
    private boolean isblock;
    private boolean istaxablecomponent;
    private int method;
    private Componentmaster computeon;

    public Componentmaster(){
        compid = java.util.UUID.randomUUID().toString();
    }

    public Componentmaster(String compid, Company company, String code, Date sdate, Date edate, String description, MasterData subtype, int frequency, CostCenter costcenter, MasterData paymentterm, double amount, boolean isadjust, boolean isdefault, boolean isblock, int method, Componentmaster computeon) {
        this.compid = compid;
        this.company = company;
        this.code = code;
        this.sdate = sdate;
        this.edate = edate;
        this.description = description;
        this.subtype = subtype;
        this.frequency = frequency;
        this.costcenter = costcenter;
        this.paymentterm = paymentterm;
        this.amount = amount;
        this.isadjust = isadjust;
        this.isdefault = isdefault;
        this.isblock = isblock;
        this.method = method;
        this.computeon = computeon;
    }

    public Componentmaster getComputeon() {
        return computeon;
    }

    public void setComputeon(Componentmaster computeon) {
        this.computeon = computeon;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompid() {
        return compid;
    }

    public void setCompid(String compid) {
        this.compid = compid;
    }

    public CostCenter getCostcenter() {
        return costcenter;
    }

    public void setCostcenter(CostCenter costcenter) {
        this.costcenter = costcenter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEdate() {
        return edate;
    }

    public void setEdate(Date edate) {
        this.edate = edate;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isIsadjust() {
        return isadjust;
    }

    public void setIsadjust(boolean isadjust) {
        this.isadjust = isadjust;
    }

    public boolean isIsblock() {
        return isblock;
    }

    public void setIsblock(boolean isblock) {
        this.isblock = isblock;
    }

    public boolean isIsdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }

    public MasterData getPaymentterm() {
        return paymentterm;
    }

    public void setPaymentterm(MasterData paymentterm) {
        this.paymentterm = paymentterm;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public MasterData getSubtype() {
        return subtype;
    }

    public void setSubtype(MasterData subtype) {
        this.subtype = subtype;
    }

    public boolean isIstaxablecomponent() {
        return istaxablecomponent;
    }

    public void setIstaxablecomponent(boolean istaxablecomponent) {
        this.istaxablecomponent = istaxablecomponent;
    }
    
    
}
