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

import com.krawler.common.admin.User;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class MalaysiaFormCP21 {

    private String id;
    private String empfilerefno;
    private User user;
    private Date expDateToLeave;
    private String passportno;
    private int natureofemployment;
    private String reasonForDeparture;
    private String corresspondenceAddress;
    private Date dateOfReturn;
    private double amountdue;
    private Date dateOfSubmission;
    private Date salaryFrom;
    private Date salaryTo;
    private double salaryAmount;
    private Date leavePayFrom;
    private Date leavePayTo;
    private double leavePayAmount;
    private Date bonusFrom;
    private Date bonusTo;
    private double bonusAmount;
    private Date gratuityFrom;
    private Date gratuityTo;
    private double gratuityAmount;
    private Date allowanceFrom;
    private Date allowanceTo;
    private double allowanceAmount;
    private Date pensionFrom;
    private Date pensionTo;
    private double pensionAmount;
    private Date residenceFrom;
    private Date residenceTo;
    private double residenceAmount;
    private Date allowanceinkindFrom;
    private Date allowanceinkindTo;
    private double allowanceinkindAmount;
    private Date providentFundFrom;
    private Date providentFundTo;
    private double providentFundAmount;
    private int natureofpayment;
    private Date paymentDate;
    private double amountToBePaid;
    private Integer month;
	private Integer year;
    private int authorizeStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpfilerefno() {
        return empfilerefno;
    }

    public void setEmpfilerefno(String empfilerefno) {
        this.empfilerefno = empfilerefno;
    }

    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
    public double getAllowanceAmount() {
        return allowanceAmount;
    }

    public void setAllowanceAmount(double allowanceAmount) {
        this.allowanceAmount = allowanceAmount;
    }

    public Date getAllowanceFrom() {
        return allowanceFrom;
    }

    public void setAllowanceFrom(Date allowanceFrom) {
        this.allowanceFrom = allowanceFrom;
    }

    public Date getAllowanceTo() {
        return allowanceTo;
    }

    public void setAllowanceTo(Date allowanceTo) {
        this.allowanceTo = allowanceTo;
    }

    public double getAllowanceinkindAmount() {
        return allowanceinkindAmount;
    }

    public void setAllowanceinkindAmount(double allowanceinkindAmount) {
        this.allowanceinkindAmount = allowanceinkindAmount;
    }

    public Date getAllowanceinkindFrom() {
        return allowanceinkindFrom;
    }

    public void setAllowanceinkindFrom(Date allowanceinkindFrom) {
        this.allowanceinkindFrom = allowanceinkindFrom;
    }

    public Date getAllowanceinkindTo() {
        return allowanceinkindTo;
    }

    public void setAllowanceinkindTo(Date allowanceinkindTo) {
        this.allowanceinkindTo = allowanceinkindTo;
    }

    public double getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(double amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
    }

    public double getAmountdue() {
        return amountdue;
    }

    public void setAmountdue(double amountdue) {
        this.amountdue = amountdue;
    }

    public double getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(double bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public Date getBonusFrom() {
        return bonusFrom;
    }

    public void setBonusFrom(Date bonusFrom) {
        this.bonusFrom = bonusFrom;
    }

    public Date getBonusTo() {
        return bonusTo;
    }

    public void setBonusTo(Date bonusTo) {
        this.bonusTo = bonusTo;
    }

    public String getCorresspondenceAddress() {
        return corresspondenceAddress;
    }

    public void setCorresspondenceAddress(String corresspondenceAddress) {
        this.corresspondenceAddress = corresspondenceAddress;
    }

    public Date getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(Date dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    public Date getDateOfSubmission() {
        return dateOfSubmission;
    }

    public void setDateOfSubmission(Date dateOfSubmission) {
        this.dateOfSubmission = dateOfSubmission;
    }

    public Date getExpDateToLeave() {
        return expDateToLeave;
    }

    public void setExpDateToLeave(Date expDateToLeave) {
        this.expDateToLeave = expDateToLeave;
    }

    public double getGratuityAmount() {
        return gratuityAmount;
    }

    public void setGratuityAmount(double gratuityAmount) {
        this.gratuityAmount = gratuityAmount;
    }

    public Date getGratuityFrom() {
        return gratuityFrom;
    }

    public void setGratuityFrom(Date gratuityFrom) {
        this.gratuityFrom = gratuityFrom;
    }

    public Date getGratuityTo() {
        return gratuityTo;
    }

    public void setGratuityTo(Date gratuityTo) {
        this.gratuityTo = gratuityTo;
    }

    public double getLeavePayAmount() {
        return leavePayAmount;
    }

    public void setLeavePayAmount(double leavePayAmount) {
        this.leavePayAmount = leavePayAmount;
    }

    public Date getLeavePayFrom() {
        return leavePayFrom;
    }

    public void setLeavePayFrom(Date leavePayFrom) {
        this.leavePayFrom = leavePayFrom;
    }

    public Date getLeavePayTo() {
        return leavePayTo;
    }

    public void setLeavePayTo(Date leavePayTo) {
        this.leavePayTo = leavePayTo;
    }

    public int getNatureofemployment() {
        return natureofemployment;
    }

    public void setNatureofemployment(int natureofemployment) {
        this.natureofemployment = natureofemployment;
    }

    public int getNatureofpayment() {
        return natureofpayment;
    }

    public void setNatureofpayment(int natureofpayment) {
        this.natureofpayment = natureofpayment;
    }

    public String getPassportno() {
        return passportno;
    }

    public void setPassportno(String passportno) {
        this.passportno = passportno;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getPensionAmount() {
        return pensionAmount;
    }

    public void setPensionAmount(double pensionAmount) {
        this.pensionAmount = pensionAmount;
    }

    public Date getPensionFrom() {
        return pensionFrom;
    }

    public void setPensionFrom(Date pensionFrom) {
        this.pensionFrom = pensionFrom;
    }

    public Date getPensionTo() {
        return pensionTo;
    }

    public void setPensionTo(Date pensionTo) {
        this.pensionTo = pensionTo;
    }

    public double getProvidentFundAmount() {
        return providentFundAmount;
    }

    public void setProvidentFundAmount(double providentFundAmount) {
        this.providentFundAmount = providentFundAmount;
    }

    public Date getProvidentFundFrom() {
        return providentFundFrom;
    }

    public void setProvidentFundFrom(Date providentFundFrom) {
        this.providentFundFrom = providentFundFrom;
    }

    public Date getProvidentFundTo() {
        return providentFundTo;
    }

    public void setProvidentFundTo(Date providentFundTo) {
        this.providentFundTo = providentFundTo;
    }

    public String getReasonForDeparture() {
        return reasonForDeparture;
    }

    public void setReasonForDeparture(String reasonForDeparture) {
        this.reasonForDeparture = reasonForDeparture;
    }

    public double getResidenceAmount() {
        return residenceAmount;
    }

    public void setResidenceAmount(double residenceAmount) {
        this.residenceAmount = residenceAmount;
    }

    public Date getResidenceFrom() {
        return residenceFrom;
    }

    public void setResidenceFrom(Date residenceFrom) {
        this.residenceFrom = residenceFrom;
    }

    public Date getResidenceTo() {
        return residenceTo;
    }

    public void setResidenceTo(Date residenceTo) {
        this.residenceTo = residenceTo;
    }

    public double getSalaryAmount() {
        return salaryAmount;
    }

    public void setSalaryAmount(double salaryAmount) {
        this.salaryAmount = salaryAmount;
    }

    public Date getSalaryFrom() {
        return salaryFrom;
    }

    public void setSalaryFrom(Date salaryFrom) {
        this.salaryFrom = salaryFrom;
    }

    public Date getSalaryTo() {
        return salaryTo;
    }

    public void setSalaryTo(Date salaryTo) {
        this.salaryTo = salaryTo;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public int getAuthorizeStatus() {
        return authorizeStatus;
    }

    public void setAuthorizeStatus(int authorizeStatus) {
        this.authorizeStatus = authorizeStatus;
    }

    

}
