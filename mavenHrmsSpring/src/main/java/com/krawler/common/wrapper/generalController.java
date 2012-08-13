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
package com.krawler.common.wrapper;

import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class generalController extends MultiActionController  implements BeanFactoryAware {
    private String successView;
    private BeanFactory bfobj;

    public void setBfobj(BeanFactory bfobj) {
        this.bfobj = bfobj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public ModelAndView getData(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();

        try {

        ModelAndView model=null;
        Map map=null;
        String modelStr;
        
        int ctr=0;
        while(request.getParameter(String.valueOf(ctr))!=null) {
            JSONObject jtemp = new JSONObject(request.getParameter(String.valueOf(ctr)));
            ctr++;
            JSONObject jparam = jtemp.getJSONObject("params");
            String requrl = jtemp.getString("url");
            int num = jtemp.getInt("no");
            String functionname =requrl.substring((requrl.lastIndexOf("/")+1), requrl.indexOf("."));

            staticUrlMapping urlmapObj = new staticUrlMapping();
            HashMap<String, Object> urlmap = urlmapObj.staticurlmap;
            
            String urlmapstring =requrl.substring(0, (requrl.lastIndexOf("/")+1));
            
            urlmapstring = "/" + urlmapstring +"*" + requrl.substring(requrl.indexOf("."));
            
            Object contobj = null;
            //jobj1.put(urlmap.containsKey(urlmapstring))
            if(urlmap.containsKey(urlmapstring)) {
                
                contobj = bfobj.getBean((String)urlmap.get(urlmapstring));
                Advised pf = (Advised) contobj;
                Object actualclass = pf.getTargetSource().getTarget();
                Class cl  = actualclass.getClass();
                Method callfunction = cl.getMethod(functionname,HttpServletRequest.class,HttpServletResponse.class);

                MockHttpServletRequest mc = new MockHttpServletRequest();
                Enumeration<String> e = request.getAttributeNames();
                while(e.hasMoreElements()){
                    String name = e.nextElement();
                    mc.setAttribute(name, request.getAttribute(name));
                }
                mc.setSession(request.getSession());
                Iterator itr = jparam.keys();
                while(itr.hasNext()){
                    String obj = (String)itr.next();
                    mc.setParameter(obj, jparam.getString(obj));
                }
                model =  (ModelAndView) callfunction.invoke(actualclass,mc,response);
                map = model.getModel();
                modelStr = (String) map.get("model");
                jobj = new JSONObject(modelStr);
                JSONObject newobj = new JSONObject();
                newobj.put("data", jobj);
                newobj.put("no", num);
                jobj1.append("data", newobj);
                jobj1.put("grouper", true);
            }
        }

        

//        model=AccTaxcontrollerObj.getTax(request,response);
//        map = model.getModel();
//        modelStr = (String) map.get("model");
//        obj = new JSONObject(modelStr);
//         jobj.put("taxdata", obj);


        } catch(Exception e) {
                e.printStackTrace();
                jobj1.put("error", e);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory bf) throws BeansException {
        bfobj = bf;
    }

}
