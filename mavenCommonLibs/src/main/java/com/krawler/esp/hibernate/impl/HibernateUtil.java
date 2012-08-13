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

package com.krawler.esp.hibernate.impl;


import org.hibernate.cfg.*;
import java.util.*;
import org.hibernate.*;
import com.krawler.common.service.ServiceException;
//import com.krawler.esp.database.ReportHandlers;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.common.KwlReturnObject;
import java.lang.reflect.Method;
import java.sql.SQLException;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Column;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static Configuration config;
    static {
        try {
            config = new Configuration().configure();
            sessionFactory = config.buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception.
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    private static Configuration getConfiguration() {
         if (config == null) {
             System.out.print("configuring Hibernate ... ");
             try {
                 config = new Configuration().configure();
                 System.out.println("ok");
             } catch (HibernateException e) {
                 System.out.println("failure");
                 e.printStackTrace();
             }
         }
         return config;
    }
     
    private static SessionFactory getSessionFactory() {
         if (sessionFactory == null) {
             sessionFactory = getConfiguration().buildSessionFactory();
         }
         return sessionFactory;
    }
    
    public static Session getCurrentSession() {
        Session session;
        try {
            session = getSessionFactory().openSession();
        } catch (Throwable ex) {
            System.err.println("Rebuilding the SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        return session;
    }
    
    public static SessionFactory createSessionFactory() {
        try {
            if(sessionFactory == null) {
                sessionFactory = config.buildSessionFactory();
            }
        } catch (Throwable ex) {
            System.err.println("Rebuilding the SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        return sessionFactory;
    }
    
    public static Configuration getConfig() {
        return config;
    }
    
    public static void setSessionFactory(String resourceName) {
        synchronized (sessionFactory) {
            try {
                if (resourceName.length() > 0) {
                    config = config.addResource(resourceName);
                    sessionFactory = config.buildSessionFactory();
                } else {
                    sessionFactory = config.buildSessionFactory();
                }
                config.buildMappings();
            } catch (Throwable ex) {
                // Log the exception.
                System.err.println("Rebuilding the SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
    }
    
    public static void closeSession(Session session) {
        if (session != null) {
             session.flush();
             if (session.isOpen()) {
                 session.close();
             }
         }
    }

    public static void closeSessionFactory() {
        if(sessionFactory != null)
            sessionFactory.close();
        sessionFactory = null;
    }

    public static String getPrimaryColName(Table table) throws ServiceException {
        String colName = "";
        try {
            PrimaryKey pk = table.getPrimaryKey();
            List lst = pk.getColumns();
            Column col = (Column) lst.get(0);
            colName = col.getName();
        } catch(Exception e) {
            throw ServiceException.FAILURE("HibernateUtil.getPrimaryColName", e);
        }
        return colName;
    }

    public static String getPrimaryColName(String tableName) throws ServiceException{
        String colName = "";
        Configuration cfg = HibernateUtil.getConfig();
        java.util.Iterator itr = cfg.getTableMappings();
        while(itr.hasNext()) {
            Table table = (Table)itr.next();
            if(tableName.equals(table.getName())){
                colName = getPrimaryColName(table);
                break;
            }
        }
        return colName;
    }
    public static List executeQueryPaging(Session session, String hql,
			Object[] params, Integer[] pagingParam) throws ServiceException {
		List results = null;
		try {
            Query query = session.createQuery(hql);

			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}
            query.setFirstResult(pagingParam[0]);
            query.setMaxResults(pagingParam[1]);

			results = query.list();
		} catch (HibernateException e) {
			String message = "SQL: '" + hql + "'";
			throw ServiceException.FAILURE(message, e);
		} finally {
//			DbPool.closeStatement(stmt);
		}
		return results;
	}

    public static List executeQueryPaging(Session session, String hql,
			 Integer[] pagingParam) throws ServiceException {
		return executeQueryPaging(session, hql, null, pagingParam);
	}

    public static List executeQuery(Session session, String hql,
			Object[] params) throws ServiceException {
		List results = null;
		try {
            Query query = session.createQuery(hql);
            if (params != null) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}
			results = query.list();
		} catch (Exception e) {
			String message = "SQL: '" + hql + "'";
			throw ServiceException.FAILURE(message, e);
		} finally {
//			DbPool.closeStatement(stmt);
		}
		return results;
	}

    public static List executeQuery(Session session, String hql,
			Object param) throws ServiceException {
		Object[] params = { param };
        return executeQuery(session, hql, params);
	}

    public static List executeQuery(Session session, String hql)
			throws ServiceException {
		return executeQuery(session, hql, null);
	}

    public static List executeQuery(String hql, Object[] params)
			throws ServiceException {
        Session session = null;
        Transaction tx = null;
		try {
			session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
			return executeQuery(session, hql, params);
		} catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                String message = "SQL: '" + hql + "'";
                throw ServiceException.FAILURE(message, ex);
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                String message = "SQL: '" + hql + "'";
                throw ServiceException.FAILURE(message, ex);
        } finally {
			HibernateUtil.closeSession(session);
        }
    }

    public static List executeQuery(String query, Object param)
			throws ServiceException {
		Object[] params = { param };
		return executeQuery(query, params);
	}

    public static List executeQuery(String query) throws ServiceException {
		return executeQuery(query, null);
	}

    public static int executeUpdate(Session session, String hql, Object[] params)
			throws ServiceException {
		int numRows = 0;
		try {
			Query query = session.createQuery(hql);

			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}
			numRows = query.executeUpdate();
		} catch (HibernateException e) {
			String message = "SQL: '" + hql + "'";
			throw ServiceException.FAILURE(message, e);
		} catch (Exception e) {
			String message = "SQL: '" + hql + "'";
			throw ServiceException.FAILURE(message, e);
		} finally {
//			DbPool.closeStatement(stmt);
		}

		return numRows;
	}

   //Override functions for spring + hibernateTemplate
    public static int executeUpdate(HibernateTemplate hibernateTemplate, final String hql, final Object[] params)
            throws ServiceException {
        int numRow = 0;
        try {
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql);
                    if (params != null) {
                        for (int i = 0; i < params.length; i++) {
                            query.setParameter(i, params[i]);
                        }
                    }
                    numRows = query.executeUpdate();
                    return numRows;
                }
            });
        } catch (HibernateException e) {
            String message = "SQL: '" + hql + "'";
            throw ServiceException.FAILURE(message, e);
        } catch (Exception e) {
            String message = "SQL: '" + hql + "'";
            throw ServiceException.FAILURE(message, e);
        }
        return numRow;
    }


    public static int executeUpdate(Session session, String hql, Object param)
			throws ServiceException {
		Object[] params = { param };
		return executeUpdate(session, hql, params);
	}

    public static int executeUpdate(Session session, String sql)
			throws ServiceException {
		return executeUpdate(session, sql, null);
	}

    public static int executeUpdate(String hql, Object[] params)
			throws ServiceException {
        Session session = null;
        Transaction tx = null;
		try {
			session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            int numRows = executeUpdate(session, hql, params);
			tx.commit();
			return numRows;
		} catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                String message = "SQL: '" + hql + "'";
                throw ServiceException.FAILURE(message, ex);
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                String message = "SQL: '" + hql + "'";
                throw ServiceException.FAILURE(message, ex);
        } finally {
			HibernateUtil.closeSession(session);
        }
    }

    public static int executeUpdate(String sql, Object param)
			throws ServiceException {
		Object[] params = { param };
		return executeUpdate(sql, params);
	}

	public static int executeUpdate(String sql) throws ServiceException {
		return executeUpdate(sql, null);
	}        
    public static List executeSQLQuery(Session session, String sql,
			Object[] params) throws ServiceException {
		List results = null;
		try {
            Query query = session.createSQLQuery(sql);
            if (params != null) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}
			results = query.list();
		} catch (Exception e) {
			String message = "SQL: '" + sql + "'";
			throw ServiceException.FAILURE(message, e);
		} finally {
//			DbPool.closeStatement(stmt);
		}
		return results;
	}
    
    public static List executeSQLQuery(Session session, String hql,
			Object param) throws ServiceException {
		Object[] params = { param };
        return executeSQLQuery(session, hql, params);
	}

   public static List executeSQLQuery(Session session, String hql)
			throws ServiceException {
		return executeSQLQuery(session, hql, null);
	}
   
   public static int executeSQLUpdate(Session session, String hql, Object[] params)
			throws ServiceException {
		int numRows = 0;
		try {
			SQLQuery query = session.createSQLQuery(hql);

			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}
			numRows = query.executeUpdate();
		} catch (HibernateException e) {
			String message = "SQL: '" + hql + "'";
			throw ServiceException.FAILURE(message, e);
		} catch (Exception e) {
			String message = "SQL: '" + hql + "'";
			throw ServiceException.FAILURE(message, e);
		} finally {
//			DbPool.closeStatement(stmt);
		}

		return numRows;
	}

     public static int executeSQLUpdate(Session session, String hql, Object param)
			throws ServiceException {
		Object[] params = { param };
		return executeUpdate(session, hql, params);
	}

    public static int executeSQLUpdate(Session session, String sql)
			throws ServiceException {
		return executeSQLUpdate(session, sql, null);
	}

    //Override functions for spring + hibernateTemplate
    public static List executeQuery(HibernateTemplate hibernateTemplate, String hql,
			Object[] params) throws ServiceException {
		List results = null;
		try {
            results = hibernateTemplate.find(hql, params);
		} catch (Exception e) {
			String message = "SQL: '" + hql + "'";
			e.printStackTrace();
		} finally {
//			DbPool.closeStatement(stmt);
		}
		return results;
	}

//public List find(final String queryString, final Object[] values) throws DataAccessException {
//	return (List) executeWithNativeSession(new HibernateCallback() {
//		public Object doInHibernate(Session session) throws HibernateException {
//			Query queryObject = session.createQuery(queryString);
////			prepareQuery(queryObject);
//			if (values != null) {
//				for (int i = 0; i < values.length; i++) {
//					queryObject.setParameter(i, values[i]);
//				}
//			}
//			return queryObject.list();
//		}
//	});
//}

    /**
     * executes the native SQL query
     *
     * @param query the given query string
     * @param params the parameters to pass in the query
     * @return the list of records (rows)
     */
    public static List executeNativeQuery(HibernateTemplate hibernateTemplate, String query, Object[] params) {
        HibernateCallback hcb = new HibernateCallback() {
            private String sql;
            private Object[] params;
            public HibernateCallback setQuery(String sql, Object[] params){
                this.sql = sql;
                this.params = params;
                return this;
            }

            @Override
            public List doInHibernate(Session sn) throws HibernateException, SQLException {
                Query q=sn.createSQLQuery(sql);
                if(params!=null){
                    for(int i=0; i<params.length;i++){
                        q.setParameter(i, params[i]);
                    }
                }
                    return q.list();
            }
        }.setQuery(query, params);

        return hibernateTemplate.executeFind(hcb);
    }

     //Override functions for spring + hibernateTemplate
    public static List executeQueryPaging(HibernateTemplate hibernateTemplate, final String hql,
            final Object[] params, final Integer[] pagingParam) throws ServiceException {
        List results = null;
        try {
            results = hibernateTemplate.executeFind(new HibernateCallback() {

                public Object doInHibernate(Session session) {
                    Query query = session.createQuery(hql);
                    if (params != null) {
                        for (int i = 0; i < params.length; i++) {
                            query.setParameter(i, params[i]);
                        }
                    }
                    query.setFirstResult(pagingParam[0]);
                    query.setMaxResults(pagingParam[1]);
                    return query.list();
                }
            });
        } catch (HibernateException e) {
            String message = "SQL: '" + hql + "'";
            throw ServiceException.FAILURE(message, e);
        }
        return results;
    }

    public static List executeQuery(HibernateTemplate hibernateTemplate, String hql,
			Object param) throws ServiceException {
		Object[] params = { param };
        return executeQuery(hibernateTemplate, hql, params);
	}

    public static List executeQuery(HibernateTemplate hibernateTemplate, String hql)
			throws ServiceException {
		return executeQuery(hibernateTemplate, hql, null);
	}

    public static KwlReturnObject getTableData(HibernateTemplate hibernateTemplate, HashMap<String, Object> queryParams, boolean allFlag) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String tableName = queryParams.get("table_name").toString();
            String userListParam = queryParams.get("userlist_param").toString();
            String userListVal = queryParams.get("userlist_value").toString();
            ArrayList filter_names = (ArrayList) queryParams.get("filter_names");
            ArrayList filter_values = (ArrayList) queryParams.get("filter_values");
            ArrayList order_by = null;
            ArrayList order_type = null;
            if(queryParams.containsKey("order_by"))
                order_by =(ArrayList) queryParams.get("order_by");
            if(queryParams.containsKey("order_type"))
                order_type = (ArrayList) queryParams.get("order_type");

            String Hql = "select c from "+tableName+" c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where") + " and "+userListParam+" in (" + userListVal + ")" ;
            Hql += filterQuery;

            String orderQuery = StringUtil.orderQuery(order_by,order_type);
            Hql += orderQuery;

            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, filter_values.toArray());
            dl = ll.size();
            if(!allFlag) {
                int start = Integer.parseInt(queryParams.get("start").toString());
                int limit = Integer.parseInt(queryParams.get("limit").toString());
                ll = HibernateUtil.executeQueryPaging(hibernateTemplate, Hql, filter_values.toArray(),new Integer[]{start,limit});
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("HibernateUtil.getTableData : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true,"002","",ll,dl);
    }

    public static Object setterMethod(HibernateTemplate hibernateTemplate,HashMap<String,Object> requestParams, String classstr,String primarykey) {
        Object obj = null;
        try {
            Class cl = Class.forName(classstr);
            if(requestParams.get(primarykey)!=null)
                obj =hibernateTemplate.get(cl, requestParams.get(primarykey).toString());
            else{
                obj = cl.newInstance();
                Method setter = cl.getMethod("set"+primarykey,String.class);
                String id = UUID.randomUUID().toString();
                setter.invoke(obj, id);
            }
            for(Object key: requestParams.keySet()){
                Class rettype = cl.getMethod("get"+key).getReturnType();
                Method setter = cl.getMethod("set"+key,rettype);
                if(requestParams.get(key)!=null){
                    if(rettype.isPrimitive()||rettype.equals(String.class)||rettype.equals(Date.class)||rettype.equals(Integer.class)||rettype.equals(Boolean.class)){
                        setter.invoke(obj,requestParams.get(key));
                    }else{
                        setter.invoke(obj,hibernateTemplate.get(rettype, requestParams.get(key).toString()));
                    }
                }
            }
            hibernateTemplate.save(obj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally{
            return obj;
        }
    }

    public static Object setterMethod(Session session,HashMap<String,Object> requestParams, String classstr,String primarykey) {
        Object obj = null;
        try {
            Class cl = Class.forName(classstr);
            if(requestParams.get(primarykey)!=null)
                obj =session.get(cl, requestParams.get(primarykey).toString());
            else{
                obj = cl.newInstance();
                Method setter = cl.getMethod("set"+primarykey,String.class);
                String id = UUID.randomUUID().toString();
                setter.invoke(obj, id);
            }
            for(Object key: requestParams.keySet()){
                Class rettype = cl.getMethod("get"+key).getReturnType();
                Method setter = cl.getMethod("set"+key,rettype);
                if(requestParams.get(key)!=null){
                    if(rettype.isPrimitive()||rettype.equals(String.class)||rettype.equals(Date.class)||rettype.equals(Integer.class)||rettype.equals(Boolean.class)){
                        setter.invoke(obj,requestParams.get(key));
                    }else{
                        setter.invoke(obj,session.get(rettype, requestParams.get(key).toString()));
                    }
                }
            }
                session.save(obj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally{
            return obj;
        }
    }
    
    public static Object objectSetterMethod(HibernateTemplate hibernateTemplate, HashMap<String,Object> requestParams, String classstr, String primarykey) throws DataInvalidateException {
        Object obj = null;
        Object column = null;
        try {
            Class cl = Class.forName(classstr);
            if(requestParams.get(primarykey)!=null)
                obj =hibernateTemplate.get(cl, requestParams.get(primarykey).toString());
            else{
                obj = cl.newInstance();
                Method setter = cl.getMethod("set"+primarykey,String.class);
                String id = UUID.randomUUID().toString();
                setter.invoke(obj, id);
            }
            for(Object key: requestParams.keySet()){
                column = key;
                Class rettype = cl.getMethod("get"+key).getReturnType();
                Method setter = cl.getMethod("set"+key,rettype);
                if(requestParams.get(key)!=null){
                    if(rettype.isPrimitive()||rettype.equals(String.class)||rettype.equals(Date.class)||rettype.equals(Integer.class)||rettype.equals(Double.class)||rettype.equals(Boolean.class)){
                        setter.invoke(obj,requestParams.get(key));
                    }else{
                        setter.invoke(obj,hibernateTemplate.get(rettype, requestParams.get(key).toString()));
                    }
                }
            }
            hibernateTemplate.save(obj);
        } catch (ClassNotFoundException ex) {
            throw new DataInvalidateException(classstr+" class not found, check pojo class path from module setting.");
        } catch (NoSuchMethodException ex) {
            throw new DataInvalidateException("Incorrect pojo method name for column "+column);
        } catch (Exception ex) {
            throw new DataInvalidateException(""+ex.getMessage());
        }
        return obj;
    }
    
    public static boolean save(Object obj){
    	boolean success = false;
		Session session = null;
		Transaction tx = null;
		try{
			session = HibernateUtil.getCurrentSession();
		    tx = session.beginTransaction();
		    session.save(obj);
		    tx.commit();
		    success = true;
		}catch(Exception ex){
			if (tx!=null) {
	        	tx.rollback();
	        }
		    ex.printStackTrace();
		} finally {
			HibernateUtil.closeSession(session);
		}
		return success;
	}
}
