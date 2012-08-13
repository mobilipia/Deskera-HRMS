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
<%@page import="com.krawler.esp.handlers.StorageHandler"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.krawler.esp.database.hrmsDbcon"
         import="com.krawler.common.util.*"
         import="com.krawler.esp.hibernate.impl.HibernateUtil"
         import="org.hibernate.*"
         import="com.krawler.esp.database.DBCon"
         import="com.krawler.hrms.hrmsManager"
         import="com.krawler.hrms.recruitment.Positionmain"
         import="com.krawler.common.admin.CompanyPreferences" %>
<%@ page import="com.krawler.utils.json.base.*"%>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%
        String company = URLUtil.getDomainName(request);
        String designation = request.getParameter("jobid");
        String msg = "";
        JSONObject jobj = new JSONObject();
        Session hbsession = null;
        String jobname = "";
        String companyname = "";
        String companyid = "";
        String country = "";
        Boolean status = false;
        Boolean fields = false;
        String imageURL="http://apps.deskera.com";
        try {
            if (!StringUtil.isNullOrEmpty(company) && !StringUtil.isNullOrEmpty(designation)) {
                hbsession = HibernateUtil.getCurrentSession();
                Positionmain pos = (Positionmain) hbsession.get(Positionmain.class, designation);
                if (pos != null) {
                    jobname = pos.getPosition().getValue()+" [" + (pos.getJobid()!=null?pos.getJobid():"") + "] ";
                    companyname = pos.getCompany().getCompanyName();
                    companyid = pos.getCompany().getCompanyID();
                    country = pos.getCompany().getCountry().getCountryName();
                    status = true;
                    if (!StringUtil.isNullOrEmpty(request.getParameter("flag"))) {
                        if (Integer.parseInt(request.getParameter("flag")) == 1) {
                            String colnumbers = request.getParameter("colnumbers");
                            jobj = hrmsDbcon.saveJobsapplication(hbsession, designation, request, companyid, colnumbers);
                            status = Boolean.parseBoolean(jobj.getString("success"));
                            fields = Boolean.parseBoolean(jobj.getString("fields"));
                            msg = jobj.getString("msg");
                        }
                    }
                } else {
                    msg = hrmsDbcon.getLocalTextforJsp(2, company);;
                }
            } else {
                msg = hrmsDbcon.getLocalTextforJsp(2, company);
            }
            
            if(StringUtil.isStandAlone()){
                imageURL=URLUtil.getPageURL(request, "");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            msg = hrmsDbcon.getLocalTextforJsp(1, company);;
        } finally {
            hbsession.close();
        }

%>


<html>
    <head>
        <link rel="shortcut icon" href="../../images/deskera.png"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="../../style/view.css" rel="stylesheet" type="text/css" />
        <title>
            <%if (status || !StringUtil.isNullOrEmpty(jobname)) {
            out.print(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(13, companyid)+" " + jobname + " -  " + companyname + "");
        } else {
            out.print("Deskera HRMS-Job Page");
        }
            %>
        </title>
        <style type="text/css" media="screen">
            body{font-family:tahoma,arial,sans-serif;overflow:auto;text-align:center;}
            .mainDiv{width:770px;margin-left:auto;margin-right:auto;padding:5px;}
            .compSymbolDiv{padding:5px;}
            .formTable{width:30%}
            table{font-size : 11px; width : 100%;}
            .tdName{width : 20%}
            .tdTextBox{width : 40%}
            .tdLink{width : 10%;}
            .tdTextBox input{width : 92%}
            .tdTextAreaFont{font-size : 13.9px; font-family:tahoma,arial,sans-serif;}
            input, .tenSpacing, select{margin-left : 10px}
            td div{margin-left : 17px}
        </style>
    </head>
    <body >
        <div class="container-main">
            <div class="container">
                <div class="wrapper">
                        <div id="top-bg" class="compSymbolDiv" style="text-align:left;">
                            <div class="companylogo" style="margin-top:22px;">
                                <img src='<%=imageURL%>/b/<%=company%>/images/store?company=true&original=true'/>
                            </div>
                            <span class="jobheader" style="margin-left:0px"><%=companyname%></span><br>
                        </div>
                        <div class="companyName" style="margin-top:22px;">
                                <%--<span class="jobheader" style="margin-left:25px"><%=companyname%></span><br>--%>
                                <span class="jobheader" style="margin-left:25px"><%=com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(14, company)%></span>
                        </div>
                    <%if (StringUtil.isNullOrEmpty(msg) || fields == true) {%>
                    <div class ="mainDiv" style="margin:auto;">
                        <%if (fields == true) {%> <div style="color:red;font-size:16px"> <%=msg%></div><%}%>
                        <form action='../<%=company%>/applicant.jsp?jobid=<%=designation%>&flag=1' name='applicantform' id='applicantform' method="post" enctype="multipart/form-data"  onSubmit="return ValidateForm(<%=company%>)" >
                            <fieldset>
                                <legend>
                                    <%=com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(15, company)%>
                                </legend>
                                <table >
                                    <tbody id="Personal" class ="mainTable"></tbody>
                                </table>
                            </fieldset>
                            <!--User exists-->
                            <fieldset>
                                <legend>
                                    <%=com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(16, company)%>
                                </legend>
                                <table >
                                    <tbody id="Contact" class ="mainTable"></tbody>
                                </table>

                            </fieldset>
                            <!--User Creation-->
                            <fieldset>
                                <legend><%=com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(17, company)%></legend>
                                <table >
                                    <tbody id="Academic" class ="mainTable"></tbody>
                                </table>
                            </fieldset>

                            <!--Company Creation-->
                            <fieldset>
                                <legend><%=com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(18, company)%></legend>
                                <table >
                                    <tbody id="Work" class ="mainTable"></tbody>
                                </table>
                            </fieldset>


                            <fieldset>
                                <legend>
                                    <%=com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(19, company)%>
                                </legend>
                                <table >
                                     <tbody id="other" class ="mainTable"></tbody>
                                </table>
                            </fieldset>
                            <p style="text-align:left;"><input type="submit" value="Submit" style="margin-left:0px;" class="applybutton" id="submitapplication"/></p>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            if(document.getElementById("submitapplication")!=null)
            document.getElementById("submitapplication").value="<%=com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(20, company)%>";
            var http_object;
            // MSIE Proprietary method
            /*@cc_on
                @if (@_jscript_version >= 5)
                        try {
                                http_object = new ActiveXObject("Msxml2.XMLHTTP");
                        }
                        catch (e) {
                                try {
                                        http_object = new ActiveXObject("Microsoft.XMLHTTP");
                                }
                                catch (E) {
                                        http_object = false;
                                }
                        }
                @else
                        xmlhttp = http_object;
                @end @*/
            if (!http_object && typeof XMLHttpRequest != 'undefined') {
                try {
                    http_object = new XMLHttpRequest();
                }
                catch (e) {
                    http_object = false;
                }
            }
            var p = 'fetchmaster=true&visible=True&applicantform=true&companyid=<%=companyid%>';
            http_object.open('POST', "Rec/Jobs/getConfigRecruitmentApplyOnline.rec", true);
            http_object.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            http_object.onreadystatechange = handlegetConfigTypeResponse;
            http_object.send(p);
            var emailnumbers='';
            function numbersonly(myfield, e, dec,min,max)
            {
                var key;
                var keychar;
                if(window.event){
                    e = window.event;
                    myfield = e.srcElement
                    min = myfield.min;
                    max = myfield.max;
                    key = e.keyCode;
                }else if (e)
                   key = e.which;
                else
                   return true;
                
                var minflag = false,maxflag = false;
                if(min){
                    minflag = true;
                }
                if(max){
                    maxflag = true;
                }
                keychar = String.fromCharCode(key);

                // control keys
                if ((key==null) || (key==0) || (key==8) ||
                    (key==9) || (key==13) || (key==27) )
                   return true;

                // numbers
                else if ((("0123456789").indexOf(keychar) > -1)){
                        if(minflag && maxflag && myfield.value){
                            if(parseInt(myfield.value + keychar) >= min && parseInt(myfield.value + keychar) <= max)
                                return true;
                            else
                                return false;
                        }else
                             return true;
                        
                }
                   

                // decimal point check
                else if (dec && (keychar == "."))
                   		return false;
                else
                   return false;
            }
            function echeck(str,company) {

                var at="@"
                var dot="."
                var lat=str.indexOf(at)
                var lstr=str.length
                var ldot=str.indexOf(dot)
                if (str.indexOf(at)==-1){
                   alert(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(21, company))
                   return false
                }

                if ( (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr) ){
                   alert(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(21, company))
                   return false
                }

                if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
                    alert(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(21, company))
                    return false
                }

                 if (str.indexOf(at,(lat+1))!=-1){
                    alert(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(21, company))
                    return false
                 }

                 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
                    alert(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(21, company))
                    return false
                 }

                 if (str.indexOf(dot,(lat+2))==-1){
                    alert(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(21, company))
                    return false
                 }

                 if (str.indexOf(" ")!=-1){
                    alert(com.krawler.esp.database.hrmsDbcon.getLocalTextforJsp(21, company))
                    return false
                 }

                 return true
            }
            function ValidateForm(company){
                if(emailnumbers != '')
                {
                    var emailarray = emailnumbers.substr(0,emailnumbers.length-1).split(",");
                    for(var i=0;i<emailarray.length;i++){
                        var emailIDobj = document.getElementById("Col"+emailarray[i]);

                        if (echeck(emailIDobj.value,company)==false){
                            emailIDobj.value=""
                            emailIDobj.focus()
                            return false
                        }
                    }
                }
                return true
            }
            function getcombooption(obj) {
                var choice = document.createElement('option');
                choice.value = obj.masterid;
                choice.appendChild(document.createTextNode(obj.masterdata));
                return choice;
            }
            function getcombofield(obj) {
                // Create the element:
                var combo_box = document.createElement('select');

                setfieldname(combo_box,obj);
                combo_box.setAttribute("style", "width:40%;");
                combo_box.style.width="20%";
                var dataobj = obj.data;
                for(var i = 0; i < dataobj.length; i++) {
                    var choice = getcombooption(dataobj[i]);
                    combo_box.appendChild(choice);
                }

                return combo_box;
            }
            function setfieldname(fieldobj,obj){
                fieldobj.setAttribute("name", "Col" +obj.colnum);
                fieldobj.setAttribute("id", "Col" +obj.colnum);
            }
            function setnumberlistener(obj,maxlen,min,max){
                if(navigator.appName=="Microsoft Internet Explorer"){
                    <%--obj.onkeypress= numbersonly;--%>
                    obj.onkeypress= numbersonly; // Fix it : when min max are defined then need to send tha to event, done for other browsers as obj.setAttribute("onkeypress", "return numbersonly(this, event,true,"+min+","+max+")");
                    obj.maxLength=maxlen;
                    obj.min=min;
                    obj.max=max;
                }else{
                    obj.setAttribute("maxlength", maxlen);
                    if(min && max){
                        obj.setAttribute("onkeypress", "return numbersonly(this, event,true,"+min+","+max+")");
                    }else{
                        obj.setAttribute("onkeypress", "return numbersonly(this, event,true)");
                    }
            }
            }
            
            function gettextfield(obj) {
                var txt = document.createElement("input");
                txt.setAttribute("type", "text");
                setfieldname(txt,obj);
                txt.setAttribute("maxlength", "100");
                txt.maxLength=100;
                return txt;
            }
            function getnumberfield(obj) {
                var txt = document.createElement("input");
                txt.setAttribute("type", "text");
                setnumberlistener(txt,10);
                setfieldname(txt,obj);
                return txt;
            }
            function gethiddentextfield(colnumbers) {
                var txt = document.createElement("input");
                txt.setAttribute("type", "hidden");
                txt.setAttribute("name", "colnumbers");
                txt.setAttribute("value", colnumbers);
                txt.setAttribute("maxlength", "100");
                return txt;
            }
            function getCheckboxfield(obj) {
                var checkdiv = document.createElement("span");
                var dataobj = obj.data;
                for(var i = 0; i < dataobj.length; i++) {
                    var txt = document.createElement("input");
                    txt.setAttribute("type", "checkbox");
                    
                    setfieldname(txt,obj);
                    // add different ids to each check box becuase calling setfieldname function assign same id to each checkbox
                    txt.setAttribute('value',dataobj[i].masterid);
                    checkdiv.appendChild(txt);
                    var label = document.createTextNode(dataobj[i].masterdata);
                    checkdiv.appendChild(label);
                }
                return checkdiv;
            }
            function getDateFieldfield(obj) {
                var datediv = document.createElement("span");

                var yy = document.createElement("input");
                yy.setAttribute("type", "text");
                //setfieldname(txt,obj);
                yy.setAttribute("name", "Col" +obj.colnum + "_yy");
                yy.setAttribute("id", "Col" +obj.colnum + "_yy");
                yy.setAttribute("style", "width:50px;");
                yy.style.width="35px";
                setnumberlistener(yy,4);
                <%--yy.onkeypress= numbersonly;--%>
                var yylabel = document.createTextNode(" yyyy");

                var mm = document.createElement("input");
                mm.setAttribute("type", "text");
                //setfieldname(txt,obj);
                mm.setAttribute("name", "Col" +obj.colnum + "_mm");
                mm.setAttribute("id", "Col" +obj.colnum + "_mm");
                mm.setAttribute("style", "width:25px;");
                mm.style.width="25px";
                setnumberlistener(mm,2,1,12);
                <%--mm.onkeypress= numbersonly;--%>
                <%--mm.setAttribute("onkeypress", "return numbersonly(this, event,false,1,12)");--%>

                var mmlabel = document.createTextNode(" mm");

                var dd = document.createElement("input");
                dd.setAttribute("type", "text");
                //setfieldname(txt,obj);
                dd.setAttribute("name", "Col" +obj.colnum + "_dd");
                dd.setAttribute("id", "Col" +obj.colnum + "_dd");
                dd.setAttribute("style", "width:25px;");
                dd.style.width="25px";
                <%--dd.onkeypress= numbersonly;--%>
                <%--dd.setAttribute("onkeypress", "return numbersonly(this, event,false,1,31)");--%>
                setnumberlistener(dd,2,1,31);
                var ddlabel = document.createTextNode(" dd");
                
                datediv.appendChild(yy);
                datediv.appendChild(yylabel);
                datediv.appendChild(mm);
                datediv.appendChild(mmlabel);
                datediv.appendChild(dd);
                datediv.appendChild(ddlabel);
                
                return datediv;
            }
            function getTextAreafield(obj) {
                var txt = document.createElement("textarea");
                txt.setAttribute("maxlength", "200");
                txt.onkeyup = function(){textLimit(txt,200)};
                txt.maxLength=200;
                setfieldname(txt,obj);
                txt.setAttribute("rows", "2");
                txt.setAttribute("cols", "30");
                txt.setAttribute("style", "margin-left:10px;width:268px;");
                txt.style.marginLeft="10px";
                txt.style.width="268px";
                txt.setAttribute("class", "tdTextAreaFont");
                return txt;
            }
            function getFileUploadfield(obj) {
                var txt = document.createElement("input");
                txt.setAttribute("type", "file");
                setfieldname(txt,obj);
                txt.setAttribute("style", "width:92%;");
                txt.setAttribute("margin-left", "10px");
                return txt;
            }
            function getfield(obj) {
                var val = obj.configtype;
                switch(val){
                    case 0:
                        return gettextfield(obj);
                    case 1:
                        return getCheckboxfield(obj);
                    case 2:
                        return getDateFieldfield(obj);
                    case 3:
                        return getcombofield(obj);
                    case 4:
                        return getTextAreafield(obj);
                    case 5:
                        return getFileUploadfield(obj);
                    case 6:
                        return getnumberfield(obj);
                    case 7:
                        return gettextfield(obj);
                }


            }
            function textLimit(field,maxlen) {
            	if(field.value.length > maxlen){
            		while(field.value.length > maxlen){
            			field.value=field.value.substring( 0, maxlen);
            		}
            	}
           }
        	
            function getrow(obj) {
                var labelcolumn = document.createElement("td");
                labelcolumn.setAttribute("class", "tdName");
                var fieldcolumn = document.createElement("td");

                var fieldpadd = document.createElement("td");
                fieldpadd.setAttribute("class", "tdLink");
                // fieldcolumn.setAttribute("class", "tdTextBox");
                fieldcolumn.setAttribute("style", "width:80%;");
                fieldcolumn.style.width="80%";
                var addrow = document.createElement("tr");
                var label;
                if(obj.allownull)
                    label = document.createTextNode(obj.fieldname);
                else
                    label = document.createTextNode(obj.fieldname + "*");
                
                var txt = getfield(obj);
                labelcolumn.appendChild(label);
                fieldcolumn.appendChild(txt);
                addrow.appendChild(labelcolumn);
                addrow.appendChild(fieldcolumn);
                addrow.appendChild(fieldpadd);
                return addrow;
            }
            function gethiddenrow(colnumbers) {
                var labelcolumn = document.createElement("td");
                labelcolumn.setAttribute("class", "tdName");
                var fieldcolumn = document.createElement("td");

                var fieldpadd = document.createElement("td");
                fieldpadd.setAttribute("class", "tdLink");

                // fieldcolumn.setAttribute("class", "tdTextBox");
                fieldcolumn.setAttribute("style", "width:80%;");

                var addrow = document.createElement("tr");
                addrow.setAttribute("style", "display:none;");
                var label = document.createTextNode(colnumbers);
                var txt = gethiddentextfield(colnumbers);
                labelcolumn.appendChild(label);
                fieldcolumn.appendChild(txt);
                addrow.appendChild(labelcolumn);
                addrow.appendChild(fieldcolumn);
                addrow.appendChild(fieldpadd);
                return addrow;
            }
            
            function handlegetConfigTypeResponse() {
                if (http_object.readyState == 4) {
                    if (http_object.responseText && http_object.responseText.length > 0) {
                        var responseObj = eval('('+http_object.responseText+')');
                        if(responseObj.data !='' && responseObj.data !=null){
                            responseObj = eval('('+responseObj.data+')');
                            var colnumbers ='',datenumbers ='',mandatorynumbers ='';
                            var fileitems ='';
                            var count = responseObj.data.length;
                            for(var i = 0; i < responseObj.data.length; i++) {
                                var addrow = getrow(responseObj.data[i]);
                                if(responseObj.data[i].configtype!=5)
                                    if(responseObj.data[i].configtype!=2){
                                        if(responseObj.data[i].configtype!=7){
                                            colnumbers = colnumbers + responseObj.data[i].colnum + ",";
                                            if(!responseObj.data[i].allownull){
                                                mandatorynumbers = mandatorynumbers + responseObj.data[i].colnum + ",";
                                            }
                                        }else{
                                            colnumbers = colnumbers + responseObj.data[i].colnum + ",";
                                            if(!responseObj.data[i].allownull){
                                                emailnumbers = emailnumbers + responseObj.data[i].colnum + ",";
                                            }
                                            if(!responseObj.data[i].allownull){
                                                mandatorynumbers = mandatorynumbers + responseObj.data[i].colnum + ",";
                                            }
                                        }
                                    }
                                    else{
                                        datenumbers = datenumbers + responseObj.data[i].colnum + ",";
                                        if(!responseObj.data[i].allownull){
                                            mandatorynumbers = mandatorynumbers + responseObj.data[i].colnum + ",";
                                        }
                                    }
                                    <%--alert(addrow);--%>
                                document.getElementById(responseObj.data[i].formtype).appendChild(addrow);
                                <%--alert("fds");--%>
                                <%--document.getElementById(responseObj.data[i].formtype).innerHTML+="";--%>
                                 //   document.getElementById(responseObj.data[i].formtype).innerHTML=document.getElementById(responseObj.data[i].formtype).innerHTML;
                               // alert(document.getElementById(responseObj.data[i].formtype).innerHTML);
                            }
                            var urlappend = "";
                            if(colnumbers != '')
                            {
                                colnumbers = colnumbers.substr(0,colnumbers.length-1);
                                //var addrow = gethiddenrow(colnumbers);
                                //document.getElementById("other").appendChild(addrow);
                                urlappend +="&colnumbers="+ colnumbers;
                                
                            }
                            
                            if(datenumbers != '')
                            {
                                datenumbers = datenumbers.substr(0,datenumbers.length-1);
                                urlappend +="&datenumbers="+ datenumbers;
                                
                            }

                            if(mandatorynumbers != '')
                            {
                                mandatorynumbers = mandatorynumbers.substr(0,mandatorynumbers.length-1);
                                urlappend +="&mandatorynumbers="+ mandatorynumbers;

                            }
                            document.getElementById("applicantform").action +=urlappend;

                        }
                        setTimeout("loadvalues()", 500);
                    }
                }
            }
        </script>
        <%} else {%>
        <div style="border: 1px solid #ddd; padding: 20px; margin-top: 40px; font-size: 20px; text-align: center;"><%=msg%></div>
        <%}%>

        
        <script type="text/javascript">
            function loadvalues(){
                    <%
                    if (fields == true) {
                        if (!jobj.isNull("filledcols")) {
                            String filledcols = jobj.getString("filledcols");
                            String[] filledcolumns = filledcols.split(",");

                            for (int i = 0; i < filledcolumns.length; i++) {

                    %>

                                    document.getElementById("Col" +<%=filledcolumns[i]%>).value="<%=jobj.getString("Col" + filledcolumns[i])%>";
                    <%
                            }
                        }

                            if (!jobj.isNull("datecols")) {
                            String datecols = jobj.getString("datecols");
                            String[] datecolumns = datecols.split(",");

                            for (int i = 0; i < datecolumns.length; i++) {
                                    String[] date = jobj.getString("Col" + datecolumns[i]).split("/");
                    %>
                                    document.getElementById("Col" +<%=datecolumns[i]%>+"_dd").value="<%=date[1]%>";
                                    document.getElementById("Col" +<%=datecolumns[i]%>+"_mm").value="<%=date[0]%>";
                                    document.getElementById("Col" +<%=datecolumns[i]%>+"_yy").value="<%=date[2]%>";
                    <%
                            }
                        }
                    }
                    %>
            }

        </script>
    </body>
</html>
