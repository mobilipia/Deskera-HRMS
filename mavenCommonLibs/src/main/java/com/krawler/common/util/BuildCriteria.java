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
package com.krawler.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

/**
 *
 * @author krawler
 */
public class BuildCriteria {

    public static final int ISNOTNULL = 1;
    public static final int NOTIN = 2;
    public static final int LIKE = 3;
    public static final int LE = 4;
    public static final int GE = 5;
    public static final int ISNULL = 6;
    public static final int IN = 7;
    public static final int NE = 8;
    public static final int LT = 9;
    public static final int GT = 10;
    public static final int EQ = 11;
    public static final int ORDER = 12;
    
    public static final String OPERATORISNOTNULL = "ISNOT";
    public static final String OPERATORNOTIN = "NOTIN";
    public static final String OPERATORLIKE = "LIKE";
    public static final String OPERATORLE = "<=";
    public static final String OPERATORGE = ">=";
    public static final String OPERATORISNULL = "IS";
    public static final String OPERATORIN = "IN";
    public static final String OPERATORNE = "!";
    public static final String OPERATORLT = "<";
    public static final String OPERATORGT = ">";
    public static final String OPERATOREQ = "=";
    public static final String OPERATORORDER = "ORDER";
    public static final String OPERATORORDERASC = "asc";
    public static final HashMap<String,Integer> OPERATORMAP = new HashMap<String, Integer>();
    static {
        OPERATORMAP.put(OPERATORISNOTNULL, ISNOTNULL);
        OPERATORMAP.put(OPERATORNOTIN, NOTIN);
        OPERATORMAP.put(OPERATORLIKE, LIKE);
        OPERATORMAP.put(OPERATORLE, LE);
        OPERATORMAP.put(OPERATORGE, GE);
        OPERATORMAP.put(OPERATORISNULL, ISNULL);
        OPERATORMAP.put(OPERATORIN, IN);
        OPERATORMAP.put(OPERATORNE, NE);
        OPERATORMAP.put(OPERATORLT, LT);
        OPERATORMAP.put(OPERATORGT, GT);
        OPERATORMAP.put(OPERATOREQ, EQ);
        OPERATORMAP.put(OPERATORORDER, ORDER);
    }

    private static void buildCriteria(Object value, Integer conditionval, DetachedCriteria crit, String association, HashMap<String, String> associationparams, HashMap<String, DetachedCriteria> existedassociationparams) {
        if (association != null) {
            String[] associationArr = association.split("\\.");
            if (associationArr.length >= 2) {
                DetachedCriteria parentcrit = null;
                for (int i = 0; i < associationArr.length - 1; i++) { // -1 is to ignore property from association

                    if (parentcrit == null && existedassociationparams.containsKey(associationArr[i])) {
                        parentcrit = existedassociationparams.get(associationArr[i]);
                    } else {
                        if (parentcrit != null) {
                            parentcrit = parentcrit.createCriteria(associationArr[i],associationArr[i], DetachedCriteria.INNER_JOIN);
                        } else {
                            parentcrit = crit.createCriteria(associationArr[i],associationArr[i], DetachedCriteria.INNER_JOIN);
                        }
                    }

                    if (i == associationArr.length - 2) {
                        associationparams.put(association, associationArr[i] + "." + associationArr[i + 1]);
                        pushCriteriaValue(value, conditionval, parentcrit, associationArr[i + 1]);
                    }
                    existedassociationparams.put(associationArr[i], parentcrit);
                }
            } else {
                pushCriteriaValue(value, conditionval, crit, association);
            }
        }

    }
    public static void buildCriteriaAssociation(DetachedCriteria crit,Integer JOIN_TYPE, String association, HashMap<String, String> associationparams, HashMap<String, DetachedCriteria> existedassociationparams) {
        if (association != null && !StringUtil.isNullOrEmpty(association)) {
            String[] associationArr = association.split("\\.");
                DetachedCriteria parentcrit = null;
                for (int i = 0; i < associationArr.length; i++) { 
                    if (parentcrit == null && existedassociationparams.containsKey(associationArr[i])) {
                        parentcrit = existedassociationparams.get(associationArr[i]);
                    } else {
                        if (parentcrit != null) {
                            parentcrit = parentcrit.createCriteria(associationArr[i],associationArr[i], JOIN_TYPE);
                        } else {
                            parentcrit = crit.createCriteria(associationArr[i],associationArr[i], JOIN_TYPE);
                        }
                    }

                    if (i == associationArr.length - 1) {
                        associationparams.put(association, associationArr[i]);
                    }
                    existedassociationparams.put(associationArr[i], parentcrit);
                }
        }

    }
    private static void pushCriteriaValue(Object value, Integer conditionval, DetachedCriteria crit, String propertyname) {

        switch (conditionval) {
            case ISNOTNULL:
                crit.add(Restrictions.isNotNull(propertyname));
                break;
            case NOTIN: 
                String[] strArr = String.valueOf(value).split(",");
                List ls = Arrays.asList(strArr);
                crit.add(Restrictions.not(Restrictions.in(propertyname, ls)));
                break;
            case LIKE:
                crit.add(Restrictions.or(Restrictions.like(propertyname, value + "%"), Restrictions.like(propertyname, "% " + value + "%")));
                break;
            case LE:
                crit.add(Restrictions.le(propertyname, value));
                break;
            case GE:
                crit.add(Restrictions.ge(propertyname, value));
                break;
            case ISNULL:
                crit.add(Restrictions.isNull(propertyname));
                break;
            case IN:
                strArr = String.valueOf(value).split(",");
                ls = Arrays.asList(strArr);
                crit.add(Restrictions.in(propertyname, ls));
                break;
            case NE:
                crit.add(Restrictions.ne(propertyname, value));
                break;
            case LT:
                crit.add(Restrictions.lt(propertyname, value));
                break;
            case GT:
                crit.add(Restrictions.gt(propertyname, value));
                break;
            case EQ:
                crit.add(Restrictions.eq(propertyname, value));
                break;
            case ORDER:
                if (value.equals(OPERATORORDERASC)) {
                    crit.addOrder(Order.asc(propertyname));
                } else {
                    crit.addOrder(Order.desc(propertyname));
                }
                break;
        }
    }

    public static String filterQuery(DetachedCriteria crit, ArrayList filter_names, ArrayList filter_values, String appendCase) {
        HashMap<String, String> associationparams = new HashMap<String, String>();
        HashMap<String, DetachedCriteria> existedassociationparams = new HashMap<String, DetachedCriteria>();
        return filterQuery(crit, filter_names, filter_values, appendCase, associationparams, existedassociationparams);
    }
    public static String filterQuery(DetachedCriteria crit, ArrayList filter_names, ArrayList filter_values, String appendCase,HashMap<String, String> associationparams,HashMap<String, DetachedCriteria> existedassociationparams) {
        StringBuilder filterQuery = new StringBuilder();
        String op = "";
        Integer conditionval = 0;
        for (int i = 0; i < filter_names.size(); i++) {
            if (filter_names.get(i).toString().length() >= 5) {
                op = filter_names.get(i).toString().substring(0, 5);
            }
            if (OPERATORMAP.containsKey(op)) {
                String opstr = filter_names.get(i).toString();
                filter_names.set(i, opstr.substring(5, opstr.length()));
            } else {
                if (filter_names.get(i).toString().length() >= 4) {
                    op = filter_names.get(i).toString().substring(0, 4);
                }
                if (OPERATORMAP.containsKey(op)) {
                    String opstr = filter_names.get(i).toString();
                    filter_names.set(i, opstr.substring(4, opstr.length()));
                } else {
                    op = filter_names.get(i).toString().substring(0, 2);
                    if (OPERATORMAP.containsKey(op)) {
                        String opstr = filter_names.get(i).toString();
                        filter_names.set(i, opstr.substring(2, opstr.length()));
                    } else {
                        op = filter_names.get(i).toString().substring(0, 1);
                        if (OPERATORMAP.containsKey(op)) {
                            String opstr = filter_names.get(i).toString();
                            filter_names.set(i, opstr.substring(1, opstr.length()));
                        } else {
                            op = OPERATOREQ;
                        }
                    }
                }
            }
            conditionval = OPERATORMAP.get(op);
            String fieldnamepath = String.valueOf(filter_names.get(i));
            buildCriteria(filter_values.get(i), conditionval, crit, fieldnamepath, associationparams, existedassociationparams);
        }
        return filterQuery.toString();
    }
}
