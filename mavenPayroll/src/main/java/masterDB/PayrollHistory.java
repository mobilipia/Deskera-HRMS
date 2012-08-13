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

import com.krawler.common.admin.CostCenter;
import com.krawler.common.admin.User;
import com.krawler.hrms.master.MasterData;

import java.util.Date;

/**
 *
 * @author krawler
 */
public class PayrollHistory {

     public static final String DEFAULT_FREQUENCY="0";
    
     private String historyid;
     private String name;
     private String jobtitle;
     private String costcenter;
     private Double contracthours;
     private Double absencehours;
     private Double actualhours;
     private Date employementdate;
     private Date contractdate;
     private Double net;
     private Double earning;
     private Double deduction;
     private Double tax;
     private Double otherRemuneration;
     private String paymonth;
     private User user;
     private Date createdon;
     private Date generatedon;
     private Date paycyclestartdate;
     private Date paycycleenddate;
     private Double unpaidleaves;
     private int salarystatus; // 1: Entered 2:Calculate 3:Authorized 4:Unauthorized 5:Processed[Trail] 6:Processed[Final]
     private CostCenter costCenter;
     private MasterData jobTitle;
     private Date paymentdate;
     private String payspecification;
     private String paysliptxt1;
     private String paysliptxt2;
     private String paysliptxt3;
     private String frequency;
     private double unpaidleavesAmount;
     private double incometaxAmount;
     private String comment;

    public Date getPaymentdate() {
        return paymentdate;
    }

    public void setPaymentdate(Date paymentdate) {
        this.paymentdate = paymentdate;
    }

    public String getPaysliptxt1() {
        return paysliptxt1;
    }

    public void setPaysliptxt1(String paysliptxt1) {
        this.paysliptxt1 = paysliptxt1;
    }

    public String getPaysliptxt2() {
        return paysliptxt2;
    }

    public void setPaysliptxt2(String paysliptxt2) {
        this.paysliptxt2 = paysliptxt2;
    }

    public String getPaysliptxt3() {
        return paysliptxt3;
    }

    public void setPaysliptxt3(String paysliptxt3) {
        this.paysliptxt3 = paysliptxt3;
    }

    public String getPayspecification() {
        return payspecification;
    }

    public void setPayspecification(String payspecification) {
        this.payspecification = payspecification;
    }
     
    public Double getAbsencehours() {
        return absencehours;
    }

    public void setAbsencehours(Double absencehours) {
        this.absencehours = absencehours;
    }

    public Double getActualhours() {
        return actualhours;
    }

    public void setActualhours(Double actualhours) {
        this.actualhours = actualhours;
    }

    public Date getContractdate() {
        return contractdate;
    }

    public void setContractdate(Date contractdate) {
        this.contractdate = contractdate;
    }

    public Double getContracthours() {
        return contracthours;
    }

    public void setContracthours(Double contracthours) {
        this.contracthours = contracthours;
    }

    public Date getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
    }

    public Double getDeduction() {
        return deduction;
    }

    public void setDeduction(Double deduction) {
        this.deduction = deduction;
    }
    
    public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getOtherRemuneration() {
		return otherRemuneration;
	}

	public void setOtherRemuneration(Double otherRemuneration) {
		this.otherRemuneration = otherRemuneration;
	}

	public Double getEarning() {
        return earning;
    }

    public void setEarning(Double earning) {
        this.earning = earning;
    }

    public Date getEmployementdate() {
        return employementdate;
    }

    public void setEmployementdate(Date employementdate) {
        this.employementdate = employementdate;
    }

    public Date getGeneratedon() {
        return generatedon;
    }

    public void setGeneratedon(Date generatedon) {
        this.generatedon = generatedon;
    }

    public String getHistoryid() {
        return historyid;
    }

    public void setHistoryid(String historyid) {
        this.historyid = historyid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Date getPaycycleenddate() {
        return paycycleenddate;
    }

    public void setPaycycleenddate(Date paycycleenddate) {
        this.paycycleenddate = paycycleenddate;
    }

    public Date getPaycyclestartdate() {
        return paycyclestartdate;
    }

    public void setPaycyclestartdate(Date paycyclestartdate) {
        this.paycyclestartdate = paycyclestartdate;
    }

    public String getPaymonth() {
        return paymonth;
    }

    public void setPaymonth(String paymonth) {
        this.paymonth = paymonth;
    }

    public int getSalarystatus() {
        return salarystatus;
    }

    public void setSalarystatus(int salarystatus) {
        this.salarystatus = salarystatus;
    }

    public Double getUnpaidleaves() {
        return unpaidleaves;
    }

    public void setUnpaidleaves(Double unpaidleaves) {
        this.unpaidleaves = unpaidleaves;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public CostCenter getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

    public String getCostcenter() {
        return costcenter;
    }

    public void setCostcenter(String costcenter) {
        this.costcenter = costcenter;
    }

    public MasterData getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(MasterData jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

	public double getUnpaidleavesAmount() {
		return unpaidleavesAmount;
	}

	public void setUnpaidleavesAmount(double unpaidleavesAmount) {
		this.unpaidleavesAmount = unpaidleavesAmount;
	}

    public double getIncometaxAmount() {
        return incometaxAmount;
    }

    public void setIncometaxAmount(double incometaxAmount) {
        this.incometaxAmount = incometaxAmount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    
}
