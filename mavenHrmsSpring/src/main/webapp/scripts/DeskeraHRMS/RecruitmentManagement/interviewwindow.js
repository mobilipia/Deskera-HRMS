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
Wtf.interview = function(config) {
    Wtf.apply(this ,{
        buttonAlign :'right',
        height:380,
        width:410,
        buttons: [
        {
            text: WtfGlobal.getLocaleText("hrms.common.Save"),
            handler: this.sendinterviewSave,
            scope:this,
            disabled:true
        },
        {
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    },config);
    Wtf.interview.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.interview, Wtf.Window, {
    initComponent: function() {
        Wtf.interview.superclass.initComponent.call(this);
    },
    loadAllStores:function(){

    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.interview.superclass.onRender.call(this, config);
        //  this.loadMask = new Wtf.LoadMask(this.el.dom, Wtf.apply(this.empProfile));
        this.interviewdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.Date")+'*',
            name:'interviewdt',
            width:200,
            allowBlank:false,
            format:'m/d/Y',
            minValue:new Date().clearTime(true),
            value:new Date()
        });
        this.interviewtime = new Wtf.form.TimeField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.InterviewTime")+'*',
            name:'interviewtime',
            width:200,
            allowBlank:false,
            typeAhead:true,
            minValue:new Date(new Date().format("M d, Y")+" 8:00:00 AM"),
            maxValue:new Date(new Date().add(Date.DAY, 1).format("M d, Y")+" 7:45:00 AM")
        });
        this.interviewplace = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.Location")+'*',
            name:'interviewplace',
            maxLength:255,
            width:200,
            validator:WtfGlobal.noBlankCheck,
            allowBlank:false
        });

        this.interviewdetails = new Wtf.form.TextArea({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.Comment"),
            name:'interviewcomment',
            width:200,
            maxLength:255,
            height:50
        });
        this.userrec=new Wtf.data.Record.create([{
            name:'userid'
        },{
            name:'fullname'
        }]);
        this.empnameStore =  new Wtf.data.Store({
//            url: Wtf.req.base + "UserManager.jsp",
//            url: "Rec/Job/getAllUserDetails.rec",
            url:"Common/getAllUserDetailsHrms.common",
            baseParams: {
                userid : this.apcntid,
                grouper: 'a',
                combo : true
                
            },
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            },this.userrec),
            autoLoad : false
        });
        this.empnameStore.load({params:{grouper:'a'}});
        this.empId=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.contact.person")+'*',
            labelWidth:110,
            mode:"local",
            hiddenName:'contactperson',
            store:this.empnameStore,
            allowBlank:false,
            displayField: 'fullname',
            valueField:'userid',
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead:true,
            width:200,
            forceSelection:true
        });

        this.RecruiterRecord = Wtf.data.Record.create([
        {
            name:'fname'
        },
        {
            name:'rid'
        },
        {
            name:'lname'
        },
        {
            name:'fullname'
        }

        ]);

        this.RecruiterReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.RecruiterRecord);

        this.RecruiterStore= new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/getRecruiter.rec",
            reader:this.RecruiterReader,
            baseParams:{
                flag:126,
                grouper: 'a',
                firequery: '1'
                
            }
        });
        this.RecruiterStore.load();

        this.recruitercombo = new Wtf.form.ComboBox({
            store:  this.RecruiterStore,
            typeAhead:true,
            fieldLabel: WtfGlobal.getLocaleText("hrms.Dashboard.AssignInterviewer")+'*',
            name: 'recruiter',
            displayField: 'fullname',
            hiddenName:'rid',
            valueField:'rid',
            mode: 'local',
            width:200,
            triggerAction: 'all',
            allowBlank: false
        })

        this.check=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.SendMail"),
            name:'mail',
            checked:true
        });

        this.MSComboconfig = {
            store:  this.RecruiterStore,
            typeAhead:true,
            fieldLabel: WtfGlobal.getLocaleText("hrms.Dashboard.AssignInterviewer")+'*',
            name: 'recruiter',
            displayField: 'fullname',
            hiddenName:'rid',
            valueField:'rid',
            emptyText:WtfGlobal.getLocaleText("hrms.recruitment.Nointerviewerassignforinterview"),
            mode: 'local',
            width:200,
            triggerAction: 'all',
            allowBlank: false
        },
        this.nonmonBen = new Wtf.common.Select(Wtf.applyIf({
            multiSelect:true,
            labelSeparator:'',
            forceSelection:false
        },this.MSComboconfig));

        this.interviewform = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            id:'interviewform',
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            labelWidth :110,
            layoutConfig: {
                deferredRender: false
            },
            items:[
            this.interviewdate, this.interviewtime, this.interviewplace, this.nonmonBen,this.empId,this.interviewdetails,this.check
            ]
        })

        this.headingType=WtfGlobal.getLocaleText("hrms.recruitment.schedule.interview");
        this.interviewpanel= new Wtf.Panel({            
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 70,
                    border : false,
                    cls : 'panelstyleClass1',
                    html:this.isview?getTopHtml(this.headingType,"","../../images/interview-schedule.gif"):getTopHtml(this.headingType, WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"),"../../images/interview-schedule.gif")
                },{
                    border:false,
                    region:'center',
                    cls : 'panelstyleClass2',
                    layout:"fit",
                    items: [
                    this.interviewform
                    ]
                }]
            }]
        });
        this.add(this.interviewpanel);
        this.RecruiterStore.on("load",function(){
            var index = this.RecruiterStore.find('rid',this.apcntid);
            if(index!=-1){
                var rec = this.RecruiterStore.getAt(index);
                this.RecruiterStore.remove(rec);
            }
            this.buttons[0].enable();
        },this);
        this.empnameStore.on('load',function(){
            var index = this.empnameStore.find('userid',this.apcntid);
            if(index!=-1){
                var rec = this.empnameStore.getAt(index);
                this.empnameStore.remove(rec);
            }
        },this);
    }, 
 
    sendinterviewSave:function(){
        if(!this.interviewform.form.isValid()){
            return ;
        }else{
            if(this.reason=='forallapps')
            {
                this.arr=Wtf.getCmp(this.appid+'allappsviewgr').getSelectionModel().getSelections();
                this.gridst=Wtf.getCmp(this.appid+'allappsviewgr').getStore();
            }
            else{
                this.arr=Wtf.getCmp(this.appid+'rejectedgr').getSelectionModel().getSelections();
                this.gridst=Wtf.getCmp(this.appid+'rejectedgr').getStore();
            }
            this.ids=[];
            this.cname=[];
            //this.appgrid.getSelectionModel().clearSelections();
            for(var i=0;i<this.arr.length;i++){
                this.ids.push(this.arr[i].get('id'));
                this.cname.push(this.arr[i].get('cname'));
                //var rec=this.gridst.indexOf(this.arr[i]);
               // WtfGlobal.highLightRow(this.appgrid,"33CC33",5, rec);
            }
            var  param={
                flag:39,
                ids:this.ids,
                employeetype:this.employeetype,
                cname:this.cname
            };
            calMsgBoxShow(200,4,true);
            calMsgBoxShow(202,4,true);
            this.interviewform.form.submit({
//                url: Wtf.req.base + 'hrms.jsp',
                url: "Rec/Job/scheduleinterview.rec",
                params:param,
                scope: this,
                success:  function(){
                    var params={
                        start:0,
                        limit:this.appgrid.pag.pageSize
                    }
                    WtfGlobal.delaytasks(this.gridst,params); 
                    this.close();
                    calMsgBoxShow(58,0);
                    if(msgFlag==1)
                        WtfGlobal.closeProgressbar();
                },
                failure:function(){
                    calMsgBoxShow(59,1);
                    if(msgFlag==1)
                        WtfGlobal.closeProgressbar();
                }
            })
        
        }
    }

});








