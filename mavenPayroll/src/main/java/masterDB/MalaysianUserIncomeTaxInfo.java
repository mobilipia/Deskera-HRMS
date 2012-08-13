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
public class MalaysianUserIncomeTaxInfo {

    private String id;
    private User user;
    private int categoryid; // 1: Single, 2: Married Spouse not Working, 3: Married Spouse Working
    private Date submittedon;
    private double previousEmployerEarning;
    private double previousEmployerIncomeTax;
    private double previousEmployerEPF;
    private double previousEmployerLIC;
    private double previousEmployerZakat;
    private double previousEmployerOtherDeduction;
    private boolean currentEPF;
    private double currentLICAndOther;
    private double currentZakat;
    private double currentBenefitInKind;
    private int empStatus;
    
    public double getPreviousEmployerEarning() {
		return previousEmployerEarning;
	}

	public void setPreviousEmployerEarning(double previousEmployerEarning) {
		this.previousEmployerEarning = previousEmployerEarning;
	}

	public double getPreviousEmployerIncomeTax() {
		return previousEmployerIncomeTax;
	}

	public void setPreviousEmployerIncomeTax(double previousEmployerIncomeTax) {
		this.previousEmployerIncomeTax = previousEmployerIncomeTax;
	}

	public double getPreviousEmployerEPF() {
		return previousEmployerEPF;
	}

	public void setPreviousEmployerEPF(double previousEmployerEPF) {
		this.previousEmployerEPF = previousEmployerEPF;
	}

	public double getPreviousEmployerLIC() {
		return previousEmployerLIC;
	}

	public void setPreviousEmployerLIC(double previousEmployerLIC) {
		this.previousEmployerLIC = previousEmployerLIC;
	}

	public double getPreviousEmployerZakat() {
		return previousEmployerZakat;
	}

	public void setPreviousEmployerZakat(double previousEmployerZakat) {
		this.previousEmployerZakat = previousEmployerZakat;
	}

	public boolean isCurrentEPF() {
		return currentEPF;
	}

	public void setCurrentEPF(boolean currentEPF) {
		this.currentEPF = currentEPF;
	}

	public double getCurrentLICAndOther() {
		return currentLICAndOther;
	}

	public void setCurrentLICAndOther(double currentLICAndOther) {
		this.currentLICAndOther = currentLICAndOther;
	}

	public double getCurrentZakat() {
		return currentZakat;
	}

	public void setCurrentZakat(double currentZakat) {
		this.currentZakat = currentZakat;
	}

    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getSubmittedon() {
        return submittedon;
    }

    public void setSubmittedon(Date submittedon) {
        this.submittedon = submittedon;
    }

    public double getCurrentBenefitInKind() {
        return currentBenefitInKind;
    }

    public void setCurrentBenefitInKind(double currentBenefitInKind) {
        this.currentBenefitInKind = currentBenefitInKind;
    }

    public double getPreviousEmployerOtherDeduction() {
        return previousEmployerOtherDeduction;
    }

    public void setPreviousEmployerOtherDeduction(double previousEmployerOtherDeduction) {
        this.previousEmployerOtherDeduction = previousEmployerOtherDeduction;
    }

    public int getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(int empStatus) {
        this.empStatus = empStatus;
    }
    
}
