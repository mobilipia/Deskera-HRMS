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
var refid;

var LOGIN_PREFIX = 'jspfiles/employee.jsp?flag=2';

/* Delete this function to restore regular signup */
function doDirectLogin(){
    var user = "jane";
    var pass = "1234";
    validateLogin(user, pass);
}

function showNewUserForm(){
    $('#new-user-form').fadeIn('normal')
    setLoading2("&nbsp;", 'ajaxResult', '');
    $('#newuserid').focus();
    return false;
}

function validateLogin(u, p){
    var d = new Date().getTime();
    var p = 't=a&x=' + encodeURI(hex_hmac_sha1(d, p) + '&u=' + encodeURI(u) + '&p=' + encodeURI(hex_sha1(p)) + '&dc=' + encodeURI(d));
    http.open('POST', LOGIN_PREFIX, true);
    http.setRequestHeader("Content-length", p.length);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.onreadystatechange = handleHttpValidateLogin;
    http.send(p);
    /*uncomment to enable loading message*/
    //setLoading(1);
    //setMsg("Loading", 1);
}

function SetCookie(name, value){
    document.cookie = name + "=" + value + ";path=/;";
}

function getCookie(c_name){
    if (document.cookie.length > 0) {
        c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1) {
            c_start = c_start + c_name.length + 1;
            c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1)
                c_end = document.cookie.length;
            return unescape(document.cookie.substring(c_start, c_end));
        }
    }
    return "";
}

function handleHttpValidateLogin(){
    if (http.readyState == NORMAL_STATE) {
        if (http.responseText && http.responseText.length > 0) {
            var results = eval("(" + trimStr(http.responseText) + ")");
            if (results.success == true) {
                SetCookie("lid", results.lid);
                SetCookie("username", results.username);
                SetCookie("cid",results.companyid);

                var o = results.roleperms;
                var oS = "[";
                if(o && o.length > 0){
                        for( i = 0; i <  o.length; i++){
                                if(i == 0)
                                        oS += ('"' + o[i].rolegroupid + '"');
                                else
                                        oS += (',"' + o[i].rolegroupid + '"');
                        }
                }
                oS += "]";
                SetCookie("perms", oS);

                var p = results.realroles;
                var oP = "[";
                if(p && p.length > 0){
                        for( j = 0; j <  p.length; j++){
                                if(j == 0)
                                        oP += ('"' + p[j].val + '"');
                                else
                                        oP += (',"' + p[j].val + '"');
                        }
                }
                oP += "]";
                SetCookie("realroles", oP);

                redirect();
            }
            else {
                setMsg(results.message, 0);
            }
        }
        else {
            setMsg(WtfGlobal.getLocaleText("hrms.common.error.connecting.to.service"), 0);
        }
        setLoading(0);
    }
}

function redirect(){
    window.top.location.href = "applicant.html";
}

function setLoading(status){
//    var lBtn = $('#LoginButton');
//    var pwd = $('#Password')[0];
//    switch (status) {
//        case 0:
//        pwd.value = "";
//        lBtn.disabled = false;
//        break;
//        case 1:
//        lBtn.disabled = true;
//        break;
//    }
}

function lValidateEmpty(){
    var usr = $('#UserName')[0];
    var pwd = $('#Password')[0];
    var usrReq = $('#UserNameRequired')[0];
    var pwdReq = $('#PasswordRequired')[0];
    var bL = (!usr.value || trimStr(usr.value).length == 0);
    var bP = (!pwd.value || pwd.value.length == 0);
    setVisibility(bL, usrReq);
    setVisibility(bP, pwdReq);
    if (!(bL || bP)) {
        validateLogin(trimStr(usr.value), pwd.value);
    }
}

function checkCookie(){
    u = getCookie('username');
    if (!(u == null || u == "")) {
        redirect();
    }
}

function formFocus(){
    var page = window.location.href.split('?')[1];
    if(page !== undefined){
        var params = page.split('&');
        var signupFlag = false;
        for(var pcnt = 0; pcnt < params.length; pcnt++){
            var tparam = params[pcnt].split('=');
            if(tparam[0] == "signup"){
                signupFlag = true;
            } else if(tparam[0] == 'g'){
                refid = tparam[1];
            }
        }
        if(signupFlag){
            showNewUserForm();
        }
        if(page == "timeout"){
            setMsg("Session Timed Out", 0);
        }
    } else {
        var f = $("#loginForm")[0];
        if (f) {
            if (f.UserName.value == null || f.UserName.value == "") {
                f.UserName.focus();
            }
            else {
                f.Password.focus();
            }
        }
    }
}

function handleSignUpUser(){
    if (http.readyState == NORMAL_STATE) {
        if (http.responseText && http.responseText.length > 0) {
            var res = trimStr(http.responseText);
            var msg = "&nbsp;";
            $('input#newuserid')[0].value = $('input#email')[0].value = $('input#newpassword')[0].value = $('input#newpassword2')[0].value = $('input#company-name')[0].value = '';
            if(res != "" && res != null) {
                var resObj = eval("(" + res + ")");
                if (resObj.success) {
                    msg = WtfGlobal.getLocaleText("hrms.common.congratulations.email.sent.you.confirmation.link.to.complete.signup");
                    setLoading2(msg, 'ajaxResult', '');
                    $('#new-user-form').fadeOut('slow');
                }
                else{
                    if (resObj.failure == 0) {
                        msg = WtfGlobal.getLocaleText("hrms.common.login.id.not.available");
                    }
                    else {
                        if (resObj.failure == 1) {
                            msg = WtfGlobal.getLocaleText("hrms.common.emailid.already.registered");
                        }
                        else {
                            msg = WtfGlobal.getLocaleText("hrms.common.error.signing.up");
                        }
                    }
                    setLoading2(msg, 'ajaxResult', '');
                }
            }
        }
        else {
            setLoading2(WtfGlobal.getLocaleText("hrms.common.error.connecting"), 'ajaxResult', 'errorFB');
        }
    }
}

function signUpUser(u, p, eml, cn){
    var p = 'mode=1&u=' + encodeURI(u) + '&p=' + encodeURI(hex_sha1(p)) + '&e=' + encodeURI(eml) + '&c=' + encodeURI(cn);
    if(refid !== undefined){
        p += '&g=' + encodeURI(refid);
    }
    http.open('POST', SIGNUP_PREFIX, true);
    http.setRequestHeader("Content-length", p.length);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.onreadystatechange = handleSignUpUser;
    http.send(p);
    setLoading2(WtfGlobal.getLocaleText("hrms.common.checking"), 'ajaxResult', 'loadingFB');
}

function sValidateEmpty(){
    var usr = $('#newuserid')[0];
    var eml = $('#email')[0];
    var p1 = $('#newpassword')[0];
    var p2 = $('#newpassword2')[0];
    var cname = $('#company-name')[0];
    var usrReq = $('#NewUserNameRequired')[0];
    var emlReq = $('#NewEmailRequired')[0];
    var p1Req = $('#P1Required')[0];
    var p2Req = $('#P2Required')[0];
    var bL = (!usr.value || trimStr(usr.value).length == 0 || !usr.value.match(/\w+/g));
    var bE = (!eml.value || trimStr(eml.value).length == 0 || eml.value.indexOf('@') < 0);
    var bP1 = (!p1.value || p1.value.length < 4);
    var bP2 = (!p2.value || p2.value.length < 4);
    var bP1P2 = (p1.value != p2.value);
    setVisibility(bL, usrReq);
    setVisibility(bE, emlReq);
    setVisibility(bP1, p1Req);
    setVisibility(bP2 | bP1P2, p2Req);
    setLoading2("&nbsp;", 'ajaxResult', '');
    if (!(bL || bE || bP1 || bP2 || bP1P2)) {
        signUpUser(trimStr(usr.value),p1.value, trimStr(eml.value), trimStr(cname.value));
    }
}

function checkLoginid(u){
    var p = 'mode=0&id=' + encodeURI(u);
    http.open('POST', SIGNUP_PREFIX, true);
    http.setRequestHeader("Content-length", p.length);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.onreadystatechange = handleHttpCheckLoginid;
    http.send(p);
    setLoading2(WtfGlobal.getLocaleText("hrms.common.checking"), 'ajaxResult', 'loadingFB');
}

function handleHttpCheckLoginid(){
    if (http.readyState == NORMAL_STATE) {
        if (trimStr(http.responseText) == "success") {
            setLoading2(WtfGlobal.getLocaleText("hrms.common.available"), 'ajaxResult', '');
        }
        else{
            setLoading2(WtfGlobal.getLocaleText("hrms.common.not.available"), 'ajaxResult', 'errorFB');
        }
    }
}

function setLoading2(msg, elm, cls){
    $('#' + elm)[0].innerHTML = msg;
    $('#' + elm)[0].className = cls;
}

function position(){
    $('#master').css({
        top: ($(window).height() - $('#master').height()) / 2,
        left: ($(window).width() - $('#master').width()) / 2
    })

    $('#new-user-form').css({
        top: ($(window).height() - $('#new-user-form').height()) / 2 + 40,
        left: ($(window).width() - $('#new-user-form').width()) / 2
    })
}

$(function(){
    $('#btn-new-user span').hide()

    position()

    $('#tabs-bottom').css({
        position: 'absolute',
        bottom: 12,
        left: 13,
        zIndex: 200
    })

    if (jQuery.browser.version == '6.0') {
        $('#tabs-bottom').css({
            bottom: 11
        })
    }

    $('.container').css({
        position: 'absolute',
        top: 110,
        left: 25
    })

    $("#tabs-bottom li").click(function(obj){
        a = $("#tabs-bottom li:not(.active)")
        $('#tabs-bottom li').removeClass('active')
        $('div.container').removeClass('active')
        $('div.container#container-' + a.attr('id').substring(4, a.attr('id').length)).addClass('active')
        a.addClass('active')
        return false;
    })

    $('#btn-new-user').hover(function(){
        $('#btn-new-user span').fadeIn()
    }, function(){
        $('#btn-new-user span').fadeOut()
    })

    $('#btn-new-user').click(showNewUserForm)

    $('#chkusravail').click(function(){
        var usr = $('#newuserid')[0];
        var usrReq = $('#NewUserNameRequired')[0];
		var bL = (!usr.value || trimStr(usr.value).length == 0);
        setVisibility(bL, usrReq);
        if (!bL) {
            checkLoginid(trimStr(usr.value));
        }
        return false;
    })

    $('#link-hide').click(function(){
        $('#new-user-form').fadeOut('normal')
    })

    $(window).resize(position)
})
