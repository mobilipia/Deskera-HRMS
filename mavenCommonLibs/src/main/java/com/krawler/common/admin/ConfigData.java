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
package com.krawler.common.admin;

/**
 *
 * @author krawler
 */
public class ConfigData {

    String id;
    String referenceid;
    String col1;
    String col2;
    String col3;
    String col4;
    String col5;
    String col6;
    String col7;
    String col8;
    String col9;
    String col10;
    String col11;
    String col12;
    String col13;
    String col14;
    String col15;
    String col16;
    String col17;
    String col18;
    String col19;
    String col20;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferenceid() {
        return referenceid;
    }

    public void setReferenceid(String referenceid) {
        this.referenceid = referenceid;
    }

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol10() {
        return col10;
    }

    public void setCol10(String col10) {
        this.col10 = col10;
    }

    public String getCol11() {
        return col11;
    }

    public void setCol11(String col11) {
        this.col11 = col11;
    }

    public String getCol12() {
        return col12;
    }

    public void setCol12(String col12) {
        this.col12 = col12;
    }

    public String getCol13() {
        return col13;
    }

    public void setCol13(String col13) {
        this.col13 = col13;
    }

    public String getCol14() {
        return col14;
    }

    public void setCol14(String col14) {
        this.col14 = col14;
    }

    public String getCol15() {
        return col15;
    }

    public void setCol15(String col15) {
        this.col15 = col15;
    }

    public String getCol16() {
        return col16;
    }

    public void setCol16(String col16) {
        this.col16 = col16;
    }

    public String getCol17() {
        return col17;
    }

    public void setCol17(String col17) {
        this.col17 = col17;
    }

    public String getCol18() {
        return col18;
    }

    public void setCol18(String col18) {
        this.col18 = col18;
    }

    public String getCol19() {
        return col19;
    }

    public void setCol19(String col19) {
        this.col19 = col19;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    public String getCol20() {
        return col20;
    }

    public void setCol20(String col20) {
        this.col20 = col20;
    }

    public String getCol3() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3 = col3;
    }

    public String getCol4() {
        return col4;
    }

    public void setCol4(String col4) {
        this.col4 = col4;
    }

    public String getCol5() {
        return col5;
    }

    public void setCol5(String col5) {
        this.col5 = col5;
    }

    public String getCol6() {
        return col6;
    }

    public void setCol6(String col6) {
        this.col6 = col6;
    }

    public String getCol7() {
        return col7;
    }

    public void setCol7(String col7) {
        this.col7 = col7;
    }

    public String getCol8() {
        return col8;
    }

    public void setCol8(String col8) {
        this.col8 = col8;
    }

    public String getCol9() {
        return col9;
    }

    public void setCol9(String col9) {
        this.col9 = col9;
    }

    public void setCol(int colnum, String coldata) {
        switch (colnum) {
            case 1:
                setCol1(coldata);
                break;
            case 2:
                setCol2(coldata);
                break;
            case 3:
                setCol3(coldata);
                break;
            case 4:
                setCol4(coldata);
                break;
            case 5:
                setCol5(coldata);
                break;
            case 6:
                setCol6(coldata);
                break;
            case 7:
                setCol7(coldata);
                break;
            case 8:
                setCol8(coldata);
                break;
            case 9:
                setCol9(coldata);
                break;
            case 10:
                setCol10(coldata);
                break;
            case 11:
                setCol11(coldata);
                break;
            case 12:
                setCol12(coldata);
                break;
            case 13:
                setCol13(coldata);
                break;
            case 14:
                setCol14(coldata);
                break;
            case 15:
                setCol15(coldata);
                break;
            case 16:
                setCol16(coldata);
                break;
            case 17:
                setCol17(coldata);
                break;
            case 18:
                setCol18(coldata);
                break;
            case 19:
                setCol19(coldata);
                break;
            case 20:
                setCol20(coldata);
                break;
        }
    }

    public String getCol(int colnum) {
        String data = null;
        switch (colnum) {
            case 1:
                data = getCol1();
                break;
            case 2:
                data = getCol2();
                break;
            case 3:
                data = getCol3();
                break;
            case 4:
                data = getCol4();
                break;
            case 5:
                data = getCol5();
                break;
            case 6:
                data = getCol6();
                break;
            case 7:
                data = getCol7();
                break;
            case 8:
                data = getCol8();
                break;
            case 9:
                data = getCol9();
                break;
            case 10:
                data = getCol10();
                break;
            case 11:
                data = getCol11();
                break;
            case 12:
                data = getCol12();
                break;
            case 13:
                data = getCol13();
                break;
            case 14:
                data = getCol14();
                break;
            case 15:
                data = getCol15();
                break;
            case 16:
                data = getCol16();
                break;
            case 17:
                data = getCol17();
                break;
            case 18:
                data = getCol18();
                break;
            case 19:
                data = getCol19();
                break;
            case 20:
                data = getCol20();
                break;
        }
        return data;
    }
}
