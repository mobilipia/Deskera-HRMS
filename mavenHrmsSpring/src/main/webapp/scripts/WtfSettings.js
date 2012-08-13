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
Wtf.namespace("Wtf","Wtf.common","Wtf.account","Wtf.reportBuilder","Wtf.leavem");
Wtf.req = {
    base: "../../jspfiles/"
};
Wtf.gridEmptytext='<center><font size="4">No records to show</font></center>';
Wtf.BLANK_IMAGE_URL = "../../lib/resources/images/default/s.gif";
Wtf.DEFAULT_USER_URL = "../../images/defaultuser.png";
Wtf.ValidateMailPatt = /^([a-zA-Z0-9_\-\.+]+)@(([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$/;
Wtf.ValidateUserid = /^\w+$/;
Wtf.ValidatePhoneNo = /^([^-])(\(?\+?[0-9]*\)?)?[0-9_\- \(\)]*$/;
Wtf.ValidateUserName = /^[\w\s\'\"\.\-]+$/;
Wtf.validateHeadTitle = /^[\w\s\'\"\.\-\,\~\!\@\$\^\*\(\)\{\}\[\])]+$/;
Wtf.Week = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
Wtf.DomainPatt = /[ab]\/([^\/]*)\/(.*)/;
Wtf.validateImageFile=/^.+(.jpg|.bmp|.JPG|.gif|.GIF|.png|.PNG|.jpeg|.JPEG|.tif|.TIF|.tiff|.TIFF)$/;
Wtf.etype = {
    user: 0,
    comm: 1,
    proj: 2,
    home: 3,
    docs: 4,
    cal: 5,
    forum: 6,
    pmessage: 7,
    pplan: 8,
    adminpanel: 9,
    todo: 10,
    search: 11,
    hrms:12,
    hrmssave:13,
    hrmsaudit:14,
    hrmsmaster:15,
    hrmstime:16,
    hrmsperformance:17,
    hrmsreport:18,
    hrmscompensation:19,
    hrmsmanagecompensation:20,
    hrmsgoals:21,
    hrmsform:22,
    hrmsappraisalform:23,
    hrmsinitiate:24,
    hrmsrejectapps:25,
    hrmsaddapps:26,
    hrmsviewapps:27,
    hrmsmanageagency:28,
    hrmsrecruiter:29,
    hrmsinternaljobboard:30,
    hrmsrecruitmentagency:31,
    hrmsinternaljobmanage:32,
    hrmsexternalmanage:33,
    hrmsinternalmanage:34,
    hrmscompetencymaster:35,
    hrmsdesignation:36,
    hrmsinternaljob:37,
    hrmsjobboard:38,
    hrmsexternaljob:39,
    hrmsrecruit:40,
    hrmsviewtimesheet:41,
    hrmstimesheet:42,
    hrmskey:43,
    hrmssuccession:44,
    hrmsmygoals:45,
    hrmspreinternal:46,
    hrmsviewapp:47,
    hrmsviewrec:48,
    accreports:49,
    crm:50,
    master:51,
    acc:52,
    acccustomer:53,
    payroll:54,
    jobsearch:55,
    hrmsrecruitment:56,
    hrmsinitapp:57,
    hrmsarchive:58,
    hrmsmygoals:59,
    hrmsprofile:60,
    hrmsemployeeprofile:61,
    hrmsorganization:62,
    hrmscontactinfo:63,
    hrmspersonaldata:64,
    hrmsqualification:65,
    hrmsdocument:66,
    hrmssalaryreport:67,
    hrmsemployeelist:68,
    hrmsapplicantlist:69,
    hrmsdocuments:70,
    hrmsmypayslip:71,
    hrmsreview:72,
    hrmsappraisalreport:73,
    hrmsmyappraisalreport:74


};
Wtf.btype = {
    addbutton:1,
    editbutton:2,
    deletebutton:3,
    viewbutton:4,
    assignbutton:5,
    cancelbutton:6,
    documentbutton:7,
    submitbutton:8,
    downloadbutton:9,
    reportbutton:10,
    winicon:11,
    emailbutton:12,
    upbutton:13,
    downbutton:14,
    setmasterbutton:15
};
var bHasChanged = false;
Wtf.Payroll_Date_Authorize="authorization";
Wtf.Payroll_Date_Process="process";
Wtf.Perm = {};
Wtf.UPerm = {};
this.countryRec = new Wtf.data.Record.create([
{
    name: 'id'
},
{
    name: 'name'
}
]);
this.timezoneRec = new Wtf.data.Record.create([
{
    name: 'id'
},
{
    name: 'name'
}
]);

Wtf.countryStore = new Wtf.data.Store({
    url:Wtf.req.base+"UserManager.jsp",
    reader: new Wtf.data.KwlJsonReader({
        root: "data"
    },this.countryRec),
    baseParams:{
        mode:20
    }
});
Wtf.timezoneStore = new Wtf.data.Store({
//    url:Wtf.req.base+"UserManager.jsp",
    url:"KwlCommonTables/getAllTimeZones.do",
    reader: new Wtf.data.KwlJsonReader({
        root: "data"
    },this.timezoneRec),
    baseParams:{
        mode:16,
        common:'1'
    }
});
Wtf.comboTemplate = new Wtf.XTemplate('<tpl for="."><div wtf:qtip="{[values.hasAccess === false ? "EPF payment can be made for current and future months only" : "" ]}" class="{[values.hasAccess === false ? "x-combo-list-item disabled-record" : "x-combo-list-item"]}">',
                                                    '{name}',
                                                '</div></tpl>');
function getTopHtml(text, body,img){
    if(img===undefined) {
        img = '../../images/createuser.png';
    }
    var str =  "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
    +"<div style='float:left;height:100%;width:auto;position:relative;'>"
    +"<img src = "+img+"  class = 'adminWinImg'></img>"
    +"</div>"
    +"<div style='float:left;height:100%;width:60%;position:relative;'>"
    +"<div style='font-size:12px;font-style:bold;float:left;margin:15px 0px 0px 10px;width:100%;position:relative;'><b>"+text+"</b></div>"
    +"<div style='font-size:10px;float:left;margin:15px 0px 10px 10px;width:100%;position:relative;'>"+body+"</div>"
    +"</div>"
    +"</div>" ;
    return str;
}

function deleteHoliday(obj, admin){
    Wtf.MessageBox.confirm('Alert', 'Do you really want to delete the holiday?', function(btn){
        if(btn == "yes")
            Wtf.getCmp(admin).deleteHoliday(obj.id.substring(4));
    },
    this);
}

function cancelHoliday(){
    Wtf.get("addHoliday").dom.style.display = 'none';
}
function addHoliday(admin){
    Wtf.getCmp(admin).addHoliday();
}

Wtf.apply(Wtf.form.VTypes, {
    daterange : function(val, field) {
        var date = field.parseDate(val);

        if(!date){
            return;
        }
        if (field.startDateField && (!this.dateRangeMax || (date.getTime() != this.dateRangeMax.getTime()))) {
            var start = Wtf.getCmp(field.startDateField);
            start.setMaxValue(date);
            start.validate();
            this.dateRangeMax = date;
        }
        else if (field.endDateField && (!this.dateRangeMin || (date.getTime() != this.dateRangeMin.getTime()))) {
            var end = Wtf.getCmp(field.endDateField);
            end.setMinValue(date);
            end.validate();
            this.dateRangeMin = date;
        }
        /*
         * Always return true since we're only using this vtype to set the
         * min/max allowed values (these are tested for after the vtype test)
         */
        return true;
    },

    password : function(val, field) {
        if (field.initialPassField) {
            var pwd = Wtf.getCmp(field.initialPassField);
            return (val == pwd.getValue());
        }
        return true;
    },

 
    range:function(val, field) {
        if (field.initialPassField) {
            var pwd = Wtf.getCmp(field.initialPassField);
            return (val >= pwd.getValue());
        }
        return true;
    }
});

function msgBoxShow(choice, type){
    var strobj = [];
    switch (choice) {
        case 1:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.msgBoxShow"+choice)];
            break;
        case 2:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.msgBoxShow"+choice)];
            break;
        case 3:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.msgBoxShow"+choice)];
            break;
        case 4:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.msgBoxShow"+choice)];
            break;
        default:
            strobj = [choice[0], choice[1]];
            break;
    }
    var iconType = Wtf.MessageBox.INFO;
    if(type == 0)
        iconType = Wtf.MessageBox.WARNING;
    else if(type == 2)
        iconType = Wtf.MessageBox.ERROR;

    Wtf.MessageBox.show({
        title: strobj[0],
        msg: strobj[1],
        buttons: Wtf.MessageBox.OK,
        animEl: 'mb9',
        icon: iconType
    });
}

function getHeader(img, myTitle, description) {
    var str =  "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
    +"<div style='float:left;height:100%;width:auto;position:relative;'>"
    +"<img src = "+img+" style = 'width:40px;height:52px;margin:5px 5px 5px 5px;'></img>"
    +"</div>"
    +"<div style='float:left;height:100%;width:60%;position:relative;'>"
    +"<div style='font-size:12px;font-style:bold;float:left;margin:15px 0px 0px 10px;width:100%;position:relative;'><b>"+myTitle+"</b></div>"
    +"<div style='font-size:10px;float:left;margin:15px 0px 10px 10px;width:100%;position:relative;'>"+description+"</div>"
    +"</div>"
    +"</div>" ;
    return str;
}
function HTMLStripper(val){
    var str = Wtf.util.Format.stripTags(val);
    return str.replace(/"/g, '').trim();
}
function setDldUrl(u, type){
    if(type != undefined && type == 'print') {
        window.open(u, "mywindow","menubar=1,resizable=1,scrollbars=1");
    } else {
        document.getElementById('downloadframe').src = u;
    }
}


Wtf.comboBoxRenderer = function(combo) {
    return function(value) {
        var idx = combo.store.find(combo.valueField, value);
        if(idx == -1)
            return "";
        var rec = combo.store.getAt(idx);
        return rec.get(combo.displayField);
    };
}

//myProfile.js

Wtf.depStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:7,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});

Wtf.completedStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:5,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});

Wtf.desigStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:1,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
    	name:'id'
	},{
    	name:'name'
	}])
	),
    autoLoad : false
});

Wtf.countryStore= new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:11
    },
    reader:new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'name'
    },{
        name:'id'
    }])
    ),
    autoLoad : false
});

Wtf.quaStore= new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:12
    },
    reader:new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'name'
    },{
        name:'id'
    }])
    ),
    autoLoad : false

});


//

// terminationWindow.js

Wtf.terStore =  new Wtf.data.Store({
//    url: Wtf.req.base + 'hrms.jsp',
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:15,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});

//
// finalScore.js

Wtf.prat =  new Wtf.data.Store({
//    url: Wtf.req.base + 'hrms.jsp',
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:13,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});

//
//goalforemployee.js

Wtf.contextstore= new Wtf.data.Store({
    url:"Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:2
    },
    reader:new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad:false
});

Wtf.priostore= new Wtf.data.Store({
    url:"Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:3
    },
    reader:new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad:false

});

Wtf.wthstore= new Wtf.data.Store({
    url:"Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:4
    },
    reader:new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad:false

});

//
//editprospect.js

Wtf.statusStore= new Wtf.data.Store({
    url:"Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:8
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad:false

});

Wtf.callbackStore= new Wtf.data.Store({
    url:"Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:9
    },
    reader:new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad:false
});

Wtf.rankStore= new Wtf.data.Store({
    url:"Common/getMasterDataField.common",
    baseParams:{
        flag:203,
        common:'1',
        configid:10
    },
    reader:new Wtf.data.KwlJsonReader1({
        root:"data",
        totalProperty:"count"
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad:false
});

//
//AddIncometaxWin.js

Wtf.catgStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common" ,
    baseParams: {
        configid:14,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});

Wtf.interviewStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common" ,
    baseParams: {
        configid:18,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});
//
//WtfCreateUser.js

Wtf.roleStore = new Wtf.data.Store({
    url: Wtf.req.base+'UserManager.jsp',
    baseParams:{
        mode:8
    },
    reader: new Wtf.data.KwlJsonReader({
        root: 'data'
    },new Wtf.data.Record.create(
        ['roleid','rolename']
        )),
    autoLoad : false
});


Wtf.gtypeStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:21,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    },{
        name:'weightage'
    }])
    ),
    autoLoad : false
});

//Wtf.freqStore =  new Wtf.data.Store({
//    url: "Common/getMasterDataField.common",
//    baseParams: {
//        configid:22,
//        common:'1',
//        flag:203
//    },
//    reader: new Wtf.data.KwlJsonReader1({
//        root:'data'
//    },new Wtf.data.Record.create([{
//        name:'id'
//    },{
//        name:'name'
//    }])
//    ),
//    autoLoad : false
//});

Wtf.debitStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:23,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});

Wtf.paymentStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:24,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    }])
    ),
    autoLoad : false
});

Wtf.jobtypeStore =  new Wtf.data.Store({
    url: "Common/getMasterDataField.common",
    baseParams: {
        configid:25,
        common:'1',
        flag:203
    },
    reader: new Wtf.data.KwlJsonReader1({
        root:'data'
    },new Wtf.data.Record.create([{
        name:'id'
    },{
        name:'name'
    },{
        name:'weightage'
    }])
    ),
    autoLoad : false
});
function checkForJobTypeStoreLoad (){
    if(!Wtf.StoreMgr.containsKey("jobtypeStore")){
        Wtf.jobtypeStore.load();
        Wtf.StoreMgr.add("jobtypeStore",Wtf.jobtypeStore)
    }
}
//
//managerstore


Wtf.managerStore = new Wtf.data.Store({
    //url:  Wtf.req.base + 'UserManager.jsp',
    url:  "Common/getManagers.common",
    baseParams: {
        common:'1',
        mode: 22
    },
    reader: new Wtf.data.KwlJsonReader({
        root: 'data',
        totalProperty:'count'
    },new Wtf.data.Record.create([{
        name:'userid'
    },{
        name:'username'
    },{
        name:'name'
    },{
        name:'designation'
    },{
        name:'department'
    }])
    ),
    autoLoad : false

});

Wtf.adminStore = new Wtf.data.Store({
    //url:  Wtf.req.base + 'UserManager.jsp',
    url:  "Common/getAdmins.common",
    baseParams: {
        common:'1',
        mode: 22
    },
    reader: new Wtf.data.KwlJsonReader({
        root: 'data',
        totalProperty:'count'
    },new Wtf.data.Record.create([{
        name:'userid'
    },{
        name:'username'
    },{
        name:'name'
    },{
        name:'designation'
    },{
        name:'department'
    }])
    ),
    autoLoad : false

});

//managerstore


Wtf.reportingToStore = new Wtf.data.Store({
    //url:  Wtf.req.base + 'UserManager.jsp',
    url:  "Common/getReportingTo.common",
    baseParams: {
        common:'1',
        mode: 22
    },
    reader: new Wtf.data.KwlJsonReader({
        root: 'data',
        totalProperty:'count'
    },new Wtf.data.Record.create([{
        name:'userid'
    },{
        name:'username'
    },{
        name:'name'
    },{
        name:'designation'
    },{
        name:'department'
    }])
    ),
    autoLoad : false

});

//
//Wtfupdateprofile.js

Wtf.dfStore=new Wtf.data.Store({
//    url: Wtf.req.base+'UserManager.jsp',
    url:"KwlCommonTables/getAllDateFormats.do",
    baseParams:{
        common:'1',
        mode:32
    },
    reader: new Wtf.data.KwlJsonReader({
        root: "data"
    },new Wtf.data.Record.create ([{
        name:'formatid'
    },{
        name:'name'
    }])
    ),
    autoLoad : false

});

Wtf.employeeStore = new Wtf.data.Store({
    url:  Wtf.req.base + 'hrms.jsp',
    baseParams: {
        flag: 211
    },
    reader: new Wtf.data.KwlJsonReader1({
        root: 'data',
        totalProperty:'count'
    },new Wtf.data.Record.create([{
        name:'userid'
    },{
        name:'username'
    },{
        name:'designation'
    },{
        name:'departmentname'
    }])
    ),
    autoLoad : false
});


Wtf.frequencyStore = new Wtf.data.SimpleStore({
	id    : 'monStore',
	fields: ['id','name'],
	data: [["0",WtfGlobal.getLocaleText("hrms.payroll.Monthly")],
           ["1",WtfGlobal.getLocaleText("hrms.payroll.Weekly")],
           ["2",WtfGlobal.getLocaleText("hrms.payroll.twice.month")]
          ]
});

Wtf.onceMonthRec = [["1",WtfGlobal.getLocaleText("hrms.January")],
            ["2",WtfGlobal.getLocaleText("hrms.February")],
            ["3",WtfGlobal.getLocaleText("hrms.March")],
            ["4",WtfGlobal.getLocaleText("hrms.April")],
            ["5",WtfGlobal.getLocaleText("hrms.May")],
            ["6",WtfGlobal.getLocaleText("hrms.June")],
            ["7",WtfGlobal.getLocaleText("hrms.July")],
            ["8",WtfGlobal.getLocaleText("hrms.August")],
            ["9",WtfGlobal.getLocaleText("hrms.September")],
            ["10",WtfGlobal.getLocaleText("hrms.October")],
            ["11",WtfGlobal.getLocaleText("hrms.November")],
            ["12",WtfGlobal.getLocaleText("hrms.December")]
           ];

Wtf.monthStore = new Wtf.data.SimpleStore({
    id    : 'monStore',
    fields: ['id','name'],
    data: Wtf.onceMonthRec
});

Wtf.monthRec0 = [["0", WtfGlobal.getLocaleText("hrms.January")],
                ["1", WtfGlobal.getLocaleText("hrms.February")],
                ["2", WtfGlobal.getLocaleText("hrms.March")],
                ["3", WtfGlobal.getLocaleText("hrms.April")],
                ["4", WtfGlobal.getLocaleText("hrms.May")],
                ["5", WtfGlobal.getLocaleText("hrms.June")],
                ["6", WtfGlobal.getLocaleText("hrms.July")],
                ["7", WtfGlobal.getLocaleText("hrms.August")],
                ["8", WtfGlobal.getLocaleText("hrms.September")],
                ["9", WtfGlobal.getLocaleText("hrms.October")],
                ["10",WtfGlobal.getLocaleText("hrms.November")],
                ["11",WtfGlobal.getLocaleText("hrms.December")]
                ];

Wtf.monthStore0 = new Wtf.data.SimpleStore({
    id    : 'monStore0',
    fields: ['id','name'],
    data: Wtf.monthRec0
});


Wtf.onceWeekRec = new Array();
for(var i=0; i<52; i++){
	Wtf.onceWeekRec[i] = new Array((i+1), (i+1));
}

Wtf.twiceMonthRec = new Array();
for(var i=0; i<24; i++){
	Wtf.twiceMonthRec[i] = new Array((i+1), (i+1));
}

Wtf.yearRec = new Array();
for(var i=0; i<50; i++){
	Wtf.yearRec[i] = new Array((i+2001), (i+2001));
}

Wtf.yearStore = new Wtf.data.SimpleStore({
    id    : 'monStore',
    fields: ['id','name'],
    data: Wtf.yearRec
});


Wtf.payrollStatusCombobox = function(module){

    var statusRecord=[];
    if(module=="Generate"){
        statusRecord.push(["0",Wtf.Payroll_Status_Renderer_NOT_STARTED]);
        statusRecord.push(["1",Wtf.Payroll_Status_Renderer_ENTERED]);
    }
    if(module=="Generate"|| module=="Authorize" ){

        statusRecord.push(["2",Wtf.Payroll_Status_Renderer_CALCULATED]);

    }
    if(module=="Process"|| module=="Generate"|| module=="Authorize" ){
        statusRecord.push(["3",Wtf.Payroll_Status_Renderer_AUTHORIZED]);
    }

    if(module=="Generate"|| module=="Authorize" ){
        statusRecord.push(["4",Wtf.Payroll_Status_Renderer_UNAUTHORIZED]);
    }
    if(module=="Generate"|| module=="Process"){
        statusRecord.push(["5",Wtf.Payroll_Status_Renderer_PROCESSED_TRIAL]);
        statusRecord.push(["6",Wtf.Payroll_Status_Renderer_PROCESSED_FINAL]);
    }
    var defvalue=0;
    if(module=="Authorize"){
        defvalue=2;
    }
    if(module=="Process"){
        defvalue=3;
    }
    
    
    var statusStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data: statusRecord
    });

    var statusComobBox = new Wtf.form.ComboBox({
        triggerAction:"all",
        fieldLabel:'Frequency',
        hiddenName: 'frequency',
        mode:"local",
        valueField:'id',
        displayField:'name',
        forceSelection:true,
        store:statusStore,
        width:150,
        typeAhead:true,
        value:defvalue
    });

    return statusComobBox;
    
}


Wtf.common.helpTextField = function(conf){
    Wtf.apply(this, conf);
    Wtf.common.helpTextField.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.common.helpTextField, Wtf.form.TextField, {
    onRender: function(conf){
        Wtf.common.helpTextField.superclass.onRender.call(this, conf);
        if(this.helpText !== undefined){
            this.helpDiv = document.createElement("span");
            this.helpDiv.className = "helpText";
            this.helpDiv.innerHTML = this.helpText;
            this.el.dom.parentNode.appendChild(this.helpDiv);
            this.el.dom.style.width = "auto";
        }
            if (this.fileName != '' && this.fileName){
                this.FileNameDiv = document.createElement("span");
                this.FileNameDiv.id = "filename"+this.name;
                this.FileNameDiv.className = "filenameText";
                this.FileNameDiv.innerHTML = this.fileName;
                this.el.dom.parentNode.appendChild(this.FileNameDiv);
                this.el.dom.style.width = "auto";
            }
    },
    setFilename: function(text) {
        if(this.FileNameDiv !== undefined){
            this.FileNameDiv.innerHTML ='Current File : '+ text;
            this.fileName=text;
        }
    },
    setHellpText: function(text) {
        if(this.helpDiv !== undefined){
            this.helpDiv.innerHTML = text;
            this.helpText=text;
        }
    },
    getHelpText: function(){
        return this.helpText;
    },
    getFilename: function(){
        return this.fileName;
    }
})
