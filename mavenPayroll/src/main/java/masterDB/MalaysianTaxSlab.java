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

/**
 *
 * @author krawler
 */

/**
 *  MTD for current month = [(P-M)R + B ] - ( Z-X)/ n+1
 * 
 */

public class MalaysianTaxSlab {

    private String id;
    private double startTaxableAmount;// Pmin
    private double endTaxableAmount; // Pmax
    private double rangeWiseTaxableAmount; // M
    private int taxRate; // R
    private double categoryValue;// B
    private String categoryId;  // 1: Single, 2: Married Spouse not Working, 3: Married Spouse Working

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getEndTaxableAmount() {
        return endTaxableAmount;
    }

    public void setEndTaxableAmount(double endTaxableAmount) {
        this.endTaxableAmount = endTaxableAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRangeWiseTaxableAmount() {
        return rangeWiseTaxableAmount;
    }

    public void setRangeWiseTaxableAmount(double rangeWiseTaxableAmount) {
        this.rangeWiseTaxableAmount = rangeWiseTaxableAmount;
    }

    public double getStartTaxableAmount() {
        return startTaxableAmount;
    }

    public void setStartTaxableAmount(double startTaxableAmount) {
        this.startTaxableAmount = startTaxableAmount;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(int taxRate) {
        this.taxRate = taxRate;
    }

    public double getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(double categoryValue) {
        this.categoryValue = categoryValue;
    }
}
