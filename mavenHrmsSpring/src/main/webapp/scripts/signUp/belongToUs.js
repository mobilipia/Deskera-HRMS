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
var SIGNUP_PREFIX = 'jspfiles/employee.jsp?flag=1';
var http = getHTTPObject();
var refid;

function redirect(u){
 if (!u || u == undefined) {
 u = "./";
 }
 window.top.location.href = u;
}

function handleSignUpUser(){
 if (http.readyState == NORMAL_STATE) {
 if (http.responseText && http.responseText.length > 0) {
 var res = trimStr(http.responseText);
 var msg = "&nbsp;";

 if (res != "" && res != null) {

 var resObj = eval("(" + res + ")");
 if (resObj.success) {
     msg = WtfGlobal.getLocaleText("hrms.common.congratulations.email.sent.you.confirmation.link.to.complete.signup");
    setLoading2(msg, 'ajaxResult', 'loadingMB');
    setTimeout("redirect('"+resObj.uri+"')", 2000);
//    $('#new-user-form').fadeOut('slow');
//    redirect(resObj.uri);
 }

 else {
 if (resObj.failure == 0) {
 msg = WtfGlobal.getLocaleText("hrms.common.userid.not.available");
 }
 else {
 if (resObj.failure == 1) {
 msg = WtfGlobal.getLocaleText("hrms.common.emailid.already.registered");
 }
 else
 if (resObj.failure == 2) {
 msg = WtfGlobal.getLocaleText("hrms.common.domain.already.registered");
 }
 else {
 msg = WtfGlobal.getLocaleText("hrms.common.error.signing.up");
 }
 }
 setLoading2(msg, 'ajaxResult', 'errorFB');
 }
 }
 }
 else {
 setLoading2(WtfGlobal.getLocaleText("hrms.common.error.connecting"), 'ajaxResult', 'errorFB');
 }
 }
}

function signUpUser(u, p, eml, cn, fn, ln,cmp,cdomain){
 var p = 'u=' + encodeURI(u) + '&p=' + encodeURI(hex_sha1(p)) + '&e=' + encodeURI(eml) + '&c=' + encodeURI(cn) +
 '&contact=' +
 encodeURI(cdomain) +
 '&fname=' +
 encodeURI(fn) +
 '&lname=' +
 encodeURI(ln)+
 '&compname=' + (cmp)
 //encodeURI(cmp);
 if (refid !== undefined) {
 p += '&g=' + encodeURI(refid);
 }
 http.open('POST', SIGNUP_PREFIX, true);
 http.setRequestHeader("Content-length", p.length);
 http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
 http.onreadystatechange = handleSignUpUser;
 http.send(p);
 setLoading2(WtfGlobal.getLocaleText("hrms.common.checking"), 'ajaxResult', 'loadingFB');
}

function sValidateEmpty(companyname){
 $("#vmsg").hide("fast");
 var fname = $('#fname')[0];
 var lname = $('#lname')[0];
 var usr = $('#newuserid')[0];
 var eml = $('#email')[0];
 var p1 = $('#newpassword')[0];
 var p2 = $('#newpassword2')[0];
 var cname = $('#company-name')[0];
 var cdomain = $('#phone-no')[0];

 var fN = (!fname.value || trimStr(fname.value).length == 0 || trimStr(fname.value).length > 32 || fname.value.replace(/[^\w\s_\-\'\"]+/g, '') != fname.value) ? $("#vfname")[0].className = "validation errorimg" : $("#vfname")[0].className = "validation valid";
 var lN = (!lname.value || trimStr(lname.value).length == 0 || trimStr(lname.value).length > 32 || lname.value.replace(/[^\w\s_\-\'\"]+/g, '') != lname.value) ? $("#vlname")[0].className = "validation errorimg" : $("#vlname")[0].className = "validation valid";
 var cN = (!cname.value || trimStr(cname.value).length == 0 || !cname.value.match(/\w+/g) || trimStr(cname.value).length == 0) ? $("#vcompany")[0].className = "validation errorimg" : $("#vcompany")[0].className = "validation valid";
 var cDomain = (!cdomain.value || trimStr(cdomain.value).length < 1 || trimStr(cdomain.value).length > 32 || cdomain.value.replace(/[^0-9.,]/g, '').replace(/[\s]/g, '') != cdomain.value) ? $("#vcompanydomain")[0].className = "validation errorimg" : $("#vcompanydomain")[0].className = "validation valid";
 var bL = (!usr.value || trimStr(usr.value).length == 0 || !usr.value.match(/\w+/) || trimStr(usr.value).length > 32) ? $("#vlogin")[0].className = "validation errorimg" : $("#vlogin")[0].className = "validation valid";
 var bE = (!eml.value || trimStr(eml.value).length == 0 || !(/^([a-zA-Z0-9_\-\.+]+)@(([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$/).test(eml.value)) ? $("#vemail")[0].className = "validation errorimg" : $("#vemail")[0].className = "validation valid";
 var bP1 = (!p1.value || p1.value.length < 4 || p1.value.length > 16) ? $("#vpass")[0].className = "validation errorimg" : $("#vpass")[0].className = "validation valid";
 var bP2 = (!p2.value || p2.value.length < 4 || p1.value != p2.value) ? $("#vpass1")[0].className = "validation errorimg" : $("#vpass1")[0].className = "validation valid";

 var msg = "";
 if (fN != "validation valid")
 msg = WtfGlobal.getLocaleText("hrms.api.enter.your.first.name")+"<br>";
 if (lN != "validation valid")
 msg += WtfGlobal.getLocaleText("hrms.api.enter.your.last.name")+"<br>";
 if (cN != "validation valid")
 msg += WtfGlobal.getLocaleText("hrms.api.enter.your.address")+"<br>";
 if (cDomain != "validation valid")
 msg += WtfGlobal.getLocaleText("hrms.api.enter.your.contact.number")+"<br>";
 if (bL != "validation valid")
 msg += WtfGlobal.getLocaleText("hrms.api.enter.username")+"<br>";
 if (bE != "validation valid")
 msg += WtfGlobal.getLocaleText("hrms.api.enter.your.email.address")+" <br>";
 if (bP1 != "validation valid")
 msg += WtfGlobal.getLocaleText("hrms.api.enter.password")+"<br>";
 else {
 if (bP2 != "validation valid")
 msg += WtfGlobal.getLocaleText("hrms.common.passwords.not.match");
 }
 if (msg == "") {
 signUpUser(trimStr(usr.value), p1.value, trimStr(eml.value), trimStr(cname.value), trimStr(fname.value), trimStr(lname.value),trimStr(companyname),trimStr(cdomain.value));
 clearErrorMsg();
 }
 else {
 setErrorMsg(msg, "error");
 }
}

function reset_form(){
 $("#signup-form")[0].reset();
 $(".validation").removeClass("valid");
 $(".validation").removeClass("errorimg");

}

function activateButton(e){
 if ($("#checkbox:checked").length > 0) {
 $('#button').css({
 backgroundPosition: 'left top'
 });
 $('#button').removeAttr("disabled");
 }
 else {
 $('#button').css({
 backgroundPosition: 'left bottom'
 });
 $('#button').attr("disabled", "disabled");
 }
}

function setErrorMsg(msg, cls){
 $("#error-log")[0].innerHTML = msg;
 $("#error-log")[0].className = cls;
 $("#error-log").fadeIn("slow");
}

function clearErrorMsg(){
 $("#error-log")[0].innerHTML = "";
 $("#error-log")[0].className = "";
 $("#error-log").fadeIn("slow");
}

function validateFName(){
 var fname = $('#fname')[0].value;
 var bL = (!fname || trimStr(fname).length == 0 || trimStr(fname).length > 32 || fname.replace(/[^\w\s_\-\'\"]+/g, '') != fname) ? $("#vfname")[0].className = "validation errorimg" : $("#vfname")[0].className = "validation valid";
}

function validateLName(){
 var lname = $('#lname')[0].value;
 var bL = (!lname || trimStr(lname).length == 0 || trimStr(lname).length > 32 || lname.replace(/[^\w\s_\-\'\"]+/g, '') != lname) ? $("#vlname")[0].className = "validation errorimg" : $("#vlname")[0].className = "validation valid";
}

function validateLoginName(){
 var login = $('#newuserid')[0].value;
 var bL = (!login || trimStr(login).length == 0 || (login.match(/\w+/) != login) || trimStr(login).length > 32) ? $("#vlogin")[0].className = "validation errorimg" : $("#vlogin")[0].className = "validation valid";
}

function validateEmail(){
 var eml = $('#email')[0].value;
 var bE = (!eml || trimStr(eml).length == 0 || !(/^([a-zA-Z0-9_\-\.+]+)@(([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$/).test(eml)) ? $("#vemail")[0].className = "validation errorimg" : $("#vemail")[0].className = "validation valid";
}

function validatePass(){
 var p1 = $('#newpassword')[0].value;
 var bP1 = (!p1 || p1.length < 4 || p1.length > 16) ? $("#vpass")[0].className = "validation errorimg" : $("#vpass")[0].className = "validation valid";
}

function validateRePass(){
 var p = $('#newpassword')[0].value;
 var p1 = $('#newpassword2')[0].value;
 var bP2 = (!p1 || p1.length < 4 || p1 != p) ? $("#vpass1")[0].className = "validation errorimg" : $("#vpass1")[0].className = "validation valid";
}

function validateCompany(){
 var cname = $('#company-name')[0].value;
 var cN = (!cname || trimStr(cname).length == 0 || !cname.match(/\w+/g) || trimStr(cname).length > 32) ? $("#vcompany")[0].className = "validation errorimg" : $("#vcompany")[0].className = "validation valid";
// $('#company-domain')[0].value = (cN == "validation errorimg") ? "" : ($('#company-name')[0].value).replace(/[^a-zA-Z 0-9]+/g, '').replace(/[\s]/g, '').toLowerCase();
// if($('#company-domain')[0].value.length>0) {
// $("#vcompanydomain")[0].className = "validation valid";
// }
// else
// {
// $("#vcompanydomain")[0].className = "validation errorimg";
// }
}

function validateDomain(){
 var cdomain = $('#company-domain')[0].value;
 var cN = (!cdomain || trimStr(cdomain).length < 1 || trimStr(cdomain).length > 32 || cdomain.replace(/[^a-zA-Z 0-9]+/g, '').replace(/[\s]/g, '') != cdomain) ? $("#vcompanydomain")[0].className = "validation errorimg" : $("#vcompanydomain")[0].className = "validation valid";
 if(cN == "validation valid") {
 var d = cdomain.toLowerCase();
 $('#company-domain')[0].value = d;
 var adlObject = document.getElementById("site-address2");
 adlObject.innerHTML = "http://" + d + ".deskera.com";
 }
}

function setLoading2(msg, elm, cls){
 $('#' + elm).show();
 $('#' + elm)[0].innerHTML = msg;
 $('#' + elm)[0].className = cls;
}

function _mm(val){
 _to = "";
 switch (val) {
 case 1:
 _to = "support@deskera.com";
 break;
 case 2:
 _to = "enterprise@deskera.com";
 break;
 }
 var _mt = window.open("mailto:" + _to, "");
 _mt.close();
}

function updateLabel(val){
 var lblObject = null;
 var valObject = null;
 var delObject = null;
 var udlObject = null;
 var adlObject = null;
 switch (val) {
 case 1:
 valObject = document.getElementById("fname"); //value from
 lblObject = document.getElementById("lblLastname"); //value to
 validateFName();
 if (valObject && valObject.value) { //check if valObject is not null and its value is not blank
 lblObject.innerHTML = valObject.value + ", enter your last name:";
 }
 break;
 case 3:
 validateLoginName();
 valObject = document.getElementById("newuserid"); //value from
 lblObject = document.getElementById("lblpass"); //value to
 if (valObject && valObject.value) { //check if valObject is not null and its value is not blank
 lblObject.innerHTML = valObject.value + ", enter your password:";
 }
 break;
 case 2:
// valObject = document.getElementById("company-name");
// lblObject = document.getElementById("company-domain");
// adlObject = document.getElementById("site-address2");
// validateCompany();
// if (valObject && valObject.value) {
// adlObject.innerHTML = "http://" + lblObject.value + ".deskera.com";
// }
// else
// {
// adlObject.innerHTML = "";
// }
 break;
 case 4:
 validateEmail();
 valObject = document.getElementById("fname"); //value from
 delObject = document.getElementById("deldetails"); //value to
 if (valObject && valObject.value) { //check if valObject is not null and its value is not blank
 delObject.innerHTML = "Hello " + valObject.value + ", please enter your account details.";
 }
 break;
 case 5:
 validateRePass();
 valObject = document.getElementById("newuserid"); //value from
 udlObject = document.getElementById("userdetails"); //value to
 if (valObject && valObject.value) { //check if valObject is not null and its value is not blank
 udlObject.innerHTML = "Hello " + valObject.value + ", please enter your company details.";
 }
 break;
 }
}

addEvent(window, 'load', initForm);

var highlight_array = new Array();

function initForm(){
 initializeFocus();
 browserDetect();
}

function initializeFocus(){
 fields = getElementsByClassName(document, "*", "field");
 for (i = 0; i < fields.length; i++) {
 if (fields[i].type == 'radio' || fields[i].type == 'checkbox' || fields[i].type == 'file') {
 fields[i].onclick = function(){
 clearSafariRadios();
 addClassName(this.parentNode.parentNode, "focused", true)
 };
 fields[i].onfocus = function(){
 clearSafariRadios();
 addClassName(this.parentNode.parentNode, "focused", true)
 };
 highlight_array.splice(highlight_array.length, 0, fields[i]);
 }
 if (fields[i].className.match('addr')) {
 fields[i].onfocus = function(){
 clearSafariRadios();
 addClassName(this.parentNode.parentNode.parentNode, "focused", true)
 };
 fields[i].onblur = function(){
 removeClassName(this.parentNode.parentNode.parentNode, "focused")
 };
 }
 else {
 fields[i].onfocus = function(){
 clearSafariRadios();
 addClassName(this.parentNode.parentNode.parentNode.parentNode, "focused", true)
 };
 fields[i].onblur = function(){
 removeClassName(this.parentNode.parentNode.parentNode.parentNode, "focused")
 };
 }
 }
}

function clearSafariRadios(){
 for (var i = 0; i < highlight_array.length; i++) {
 if (highlight_array[i].parentNode) {
 removeClassName(highlight_array[i].parentNode.parentNode, 'focused');
 }
 }
}

function browserDetect(){
 var detect = navigator.userAgent.toLowerCase();
 var container = document.getElementsByTagName('html');
 if (detect.indexOf('safari') + 1) {
 addClassName(container[0], 'safari', true);
 }
 if (detect.indexOf('firefox') + 1) {
 addClassName(container[0], 'firefox', true);
 }
}

function getElementsByClassName(oElm, strTagName, strClassName){
 var arrElements = (strTagName == "*" && oElm.all) ? oElm.all : oElm.getElementsByTagName(strTagName);
 var arrReturnElements = new Array();
 strClassName = strClassName.replace(/\-/g, "\\-");
 var oRegExp = new RegExp("(^|\\s)" + strClassName + "(\\s|$)");
 var oElement;
 for (var i = 0; i < arrElements.length; i++) {
 oElement = arrElements[i];
 if (oRegExp.test(oElement.className)) {
 arrReturnElements.push(oElement);
 }
 }
 return (arrReturnElements)
}

function addClassName(objElement, strClass, blnMayAlreadyExist){
 if (objElement.className) {
 var arrList = objElement.className.split(' ');
 if (blnMayAlreadyExist) {
 var strClassUpper = strClass.toUpperCase();
 for (var i = 0; i < arrList.length; i++) {
 if (arrList[i].toUpperCase() == strClassUpper) {
 arrList.splice(i, 1);
 i--;
 }
 }
 }
 arrList[arrList.length] = strClass;
 objElement.className = arrList.join(' ');
 }
 else {
 objElement.className = strClass;
 }
}

function removeClassName(objElement, strClass){
 if (objElement.className) {
 var arrList = objElement.className.split(' ');
 var strClassUpper = strClass.toUpperCase();
 for (var i = 0; i < arrList.length; i++) {
 if (arrList[i].toUpperCase() == strClassUpper) {
 arrList.splice(i, 1);
 i--;
 }
 }
 objElement.className = arrList.join(' ');
 }
}

function addEvent(obj, type, fn){
 if (obj.attachEvent) {
 obj["e" + type + fn] = fn;
 obj[type + fn] = function(){
 obj["e" + type + fn](window.event)
 };
 obj.attachEvent("on" + type, obj[type + fn]);
 }
 else {
 obj.addEventListener(type, fn, false);
 }
}
