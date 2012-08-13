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
var http = getHTTPObject();
var LOGIN_PREFIX = "remoteapi.jsp";

function testFunction(action){
    var p = "action=" + action + "&data=" + getTestParam(action);
    http.open('POST', LOGIN_PREFIX, true);
    http.setRequestHeader("Content-length", p.length);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.onreadystatechange = handleResponse;
    http.send(p);
}

function getTestParam(action){
    var param = "{}";
    var element="";
    var regEx = /\s/g;
    var email_check=document.apiform.email_check[0].checked;
    var commit_check=document.apiform.commit_check[0].checked;
    var element1="",element2="",element3="",element4="";
    switch(action){
        case 0://Company Exist
            element = document.getElementById('C_E_companyCheck').value;
           if(!element.replace(regEx,"") == ""){
            param = '{test:true,companyid:"'+element+'"}';
            }
            break;

        case 1://User Exist
            element = document.getElementById('U_E_userIdCheck').value;
            if(!element.replace(regEx,"") == ""){
                param = '{test:true,subdomain:demo,userid:"'+element+'"}';
            }else{
                element = document.getElementById('U_E_username').value;
            if(!element.replace(regEx,"") == ""){
                param = '{test:true,subdomain:demo,username:"'+element+'"}';
                }
            }
            break;

        case 2://Create User
            element1 = document.getElementById('C_U_username').value;
            element2 = document.getElementById('C_U_email').value;
            element3 = document.getElementById('C_U_firstName').value;
            element4 = document.getElementById('C_U_lastName').value;
            var element5=document.getElementById('C_U_companyid').value;
            var role=document.getElementById('C_U_role').value;
            if(!element1.replace(regEx,"")=="" && !element2.replace(regEx,"")=="" && !element3.replace(regEx,"")=="" && !element4.replace(regEx,"")=="" && !element5.replace(regEx,"")=="")
                param = '{username:"'+element1+'",fname:"'+element3+'",lname:"'+element4+'",emailid:"'+element2+'",companyid:"'+element5+'",role:"'+role+'",sendmail:'+email_check+',iscommit:'+commit_check+'}';
            break;

       case 3://Create Comapany
            element1 = document.getElementById('C_C_companyname').value;
            element2 = document.getElementById('C_C_email').value;
            element4 = document.getElementById('C_C_firstName').value;
            element5 = document.getElementById('C_C_lastName').value;
           var element7 = document.getElementById('C_C_username').value;
           element3 = document.getElementById('C_C_phone').value;
           var element6 = document.getElementById('C_C_address').value;
            if(!element1.replace(regEx,"")=="" && !element2.replace(regEx,"")=="" &&  !element4.replace(regEx,"")=="" &&  !element5.replace(regEx,"")=="" &&  !element7.replace(regEx,"")=="")
              param = '{companyname:"'+element1+'",emailid:"'+element2+'",fname:"'+element4+'",lname:"'+element5+'",username:"'+element7+'",phno:"'+element3+'",address:"'+element6+'",sendmail:"'+email_check+'",iscommit:'+commit_check+'}';
            break;

        case 4://Delete User
            element = document.getElementById('D_U_username').value;
            element1 = document.getElementById('D_U_userid').value;
            if(!element.replace(regEx,"")=="")
              param = '{username:"'+element+'",iscommit:'+commit_check+'}';
            else{
                if(!element1.replace(regEx,"")=="")
                 param = '{userid:"'+element1+'",iscommit:'+commit_check+'}';
            }
            break;

        case 5://Assign Role
            element = document.getElementById('A_R_username').value;
            element1 = document.getElementById('A_R_userid').value;
            role=document.getElementById('A_R_role').value;
            if(!element.replace(regEx,"")=="")
              param = '{role:"'+role+'",username:"'+element+'",iscommit:'+commit_check+'}';
             else{
                 if(!element1.replace(regEx,"")=="")
                 param = '{role:"'+role+'",userid:"'+element1+'",iscommit:'+commit_check+'}';
             }
            break;
    }
    return param;
}

function handleResponse(){
    if(http.readyState == NORMAL_STATE) {
        if(http.responseText && http.responseText.length > 0) {
            var results = eval("(" + trimStr(http.responseText) + ")");
            var dom = "";
            var responseMessage = "";
            switch(results.action){
                case 0:
                    dom = document.getElementById("companyCheck_result");
                    break;
                case 1:
                    dom = document.getElementById("userCheck_result");
                    break;
                case 2:
                    dom = document.getElementById("userCreate_result");
                    break;
                case 3:
                    dom = document.getElementById("companyCreate_result");
                    break;
                case 4:
                    dom = document.getElementById("deleteUser_result");
                    break;
                case 5:
                    dom = document.getElementById("assignRole_result");
                    break;
            }
            if(dom !== undefined){
                if(results.success) {
                    switch(results.infocode){
                    case "m01":
                        responseMessage = WtfGlobal.getLocaleText("hrms.api.company.exists");
                        break;
                    case "m02":
                        responseMessage = WtfGlobal.getLocaleText("hrms.api.company.doesnot.exist");
                        break;
                    case "m03":
                        responseMessage = WtfGlobal.getLocaleText("hrms.api.user.exists");
                        break;
                    case "m04":
                        responseMessage = WtfGlobal.getLocaleText("hrms.api.user.doesnot.exist");
                        break;
                    case "m05":
                        responseMessage = WtfGlobal.getLocaleText("hrms.api.user.created.successfully");
                        break;
                    case "m06":
                        responseMessage = WtfGlobal.getLocaleText("hrms.administration.company.created.successfully");
                        break;
                    case "m07":
                        responseMessage = WtfGlobal.getLocaleText("hrms.commonlib.user.deleted.successfully");
                        break;
                    case "m08":
                        responseMessage = WtfGlobal.getLocaleText("hrms.api.role.assigned.successfully");
                        break;
                    }
                    dom.innerHTML = responseMessage;
                } else {
                       switch(results.errorcode){
                        case "e01":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.insufficient.data");
                            break;
                        case "e02":
                            responseMessage = WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow27");
                            break;
                        case "e03":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.user.same.username.already.exists");
                            break;
                        case "e04":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.company.doesnot.exist");
                            break;
                        case "e05":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.error.sending.mail");
                            break;
                        case "e06":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.user.doesnot.exist");
                            break;
                        case "e07":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.email.already.exist");
                            break;
                        case "e08":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.insufficient.data.company.name.not.present");
                            break;
                        case "e09":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.insufficient.data.username.not.present");
                            break;
                        case "e10":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.insufficient.data.email.fname.not.present");
                            break;
                        case "e11":
                            responseMessage = WtfGlobal.getLocaleText("hrms.api.company.already.exist");
                            break;
                    }
                    dom.innerHTML = responseMessage;
                }
            }
        }
    }
}
