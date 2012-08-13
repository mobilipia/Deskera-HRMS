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
Wtf.AddEditMaster = function (config){
    Wtf.apply(this,config);
    Wtf.AddEditMaster.superclass.constructor.call(this,{
        buttons:[
                {
                    text:WtfGlobal.getLocaleText("hrms.common.Save"),
                    handler:function (){
                        this.saveProjectDetail();
                    },
                    scope:this
                },
                {
                    text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                    handler:function (){
                        this.close();
                    },
                    scope:this
                }
            ]
    });
}

Wtf.extend(Wtf.AddEditMaster,Wtf.Window,{
    initComponent:function (){
        Wtf.AddEditMaster.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetAddEditForm();

        this.mainPanel = new Wtf.Panel({
            layout:"border",
            items:[
                this.northPanel,
                this.AddEditForm
            ]
        });

        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){
        var wintitle=this.action+' Master Field';
        var windetail='';
        var image='';
        if(this.action=="Edit"){
            windetail=WtfGlobal.getLocaleText("hrms.common.Editthemasterfieldinformation");
            image='../../images/master.gif';
        } else {
            windetail=WtfGlobal.getLocaleText("hrms.common.Filluptheinformationtoaddmasterfield");
            image='../../images/master.gif';
        }
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:75,
            border:false,
            bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml(wintitle,windetail,image)
        });
    },
    GetAddEditForm:function (){
        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:15px",
//            url: Wtf.req.base + "hrms.jsp",
            url:"Common/Master/addMasterDataField.common",
            items:[
                {
                    xtype:"textfield",
                    fieldLabel:WtfGlobal.getLocaleText("hrms.EmailTemplateCmb.Name"),
                    width:200,
                    maxLength:100,
                    name:"name",
                    value:(this.action == "Edit")?this.rec.get("name"):""
                }
            ]
        });
    },
    saveProjectDetail:function (){
        if(this.AddEditForm.form.isValid()){
            this.AddEditForm.form.submit({
                params:{
                    flag:202,
                    action:this.action,
                    id:(this.action == "Edit")?this.rec.get("id"):""
                },
                success:function(){
                    Wtf.MessageBox.show({
                        title:"",
                        msg:(this.action == "Edit")? WtfGlobal.getLocaleText("hrms.common.Masterfieldeditedsuccessfully"):WtfGlobal.getLocaleText("hrms.common.Masterfieldaddedsuccessfully"),
                        icon:Wtf.MessageBox.INFO,
                        buttons:Wtf.MessageBox.OK
                    });
                    this.close();
                    this.store.load({
                        params:{
                            start:0,
                            limit:25
                        }
                    })
                },
                failure:function (){
                    Wtf.MessageBox.show({
                        title:"",
                        msg:(this.action == "Edit")?WtfGlobal.getLocaleText("hrms.common.Errorwhileeditingmasterfield"):WtfGlobal.getLocaleText("hrms.common.Errorwhileaddingmasterfield"),
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });
                },
                scope:this
            })
        }
    }
});

//---------------------------------------------------Add Master Data------------------------------------------------

Wtf.AddEditMasterData = function (config){
    Wtf.apply(this,config);
    this.save = true;
    Wtf.AddEditMasterData.superclass.constructor.call(this,{
        buttons:[
                {
                    text:WtfGlobal.getLocaleText("hrms.common.Save"),
                    id:'btnsave',
                    handler:function (){
                        this.saveProjectDetail();
                    },
                    scope:this
                },
                {
                    text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                    handler:function (){
                        this.close();
                    },
                    scope:this
                }
            ]
    });
}

Wtf.extend(Wtf.AddEditMasterData,Wtf.Window,{
    initComponent:function (){
        Wtf.AddEditMasterData.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetAddEditForm();

        this.mainPanel = new Wtf.Panel({
            layout:"border",
            items:[
                this.northPanel,
                this.AddEditForm
            ]
        });
        
        this.add(this.mainPanel);
    },
    GetMasterDataArray :function(){
        var masterdataarray = [WtfGlobal.getLocaleText("hrms.common.designation"),
                          WtfGlobal.getLocaleText("hrms.performance.context"),
                          WtfGlobal.getLocaleText("hrms.performance.priority"),
                          WtfGlobal.getLocaleText("hrms.performance.goal.weightage"),
                          WtfGlobal.getLocaleText("hrms.common.Completed"),
                          WtfGlobal.getLocaleText("hrms.common.Attributes"),
                          WtfGlobal.getLocaleText("hrms.common.department"),
                          WtfGlobal.getLocaleText("hrms.common.Subfield"),
                          WtfGlobal.getLocaleText("hrms.common.Subfield"),
                          WtfGlobal.getLocaleText("hrms.recruitment.rank"),
                          WtfGlobal.getLocaleText("hrms.common.Subfield"),
                          WtfGlobal.getLocaleText("hrms.common.Qualifications"),
                          WtfGlobal.getLocaleText("hrms.common.Performance"),
                          WtfGlobal.getLocaleText("hrms.common.category"),
                          WtfGlobal.getLocaleText("hrms.common.TerminationCause"),
                          WtfGlobal.getLocaleText("hrms.Masters.Currency"),
                          WtfGlobal.getLocaleText("hrms.common.CustomFields"),
                          WtfGlobal.getLocaleText("hrms.common.Subfield"),
                          WtfGlobal.getLocaleText("hrms.common.InterviewLocation"),"",
                          WtfGlobal.getLocaleText("hrms.masterconf.component.subtype"),
                          WtfGlobal.getLocaleText("hrms.masterconf.frequency"), 
                          WtfGlobal.getLocaleText("hrms.masterconf.payment.type")];
        
        return masterdataarray;
    },
    
    GetMasterDataArrayValue : function(configid, val){
        
        var masterdata = this.GetMasterDataArray();
        
        if(masterdata[configid]!=undefined){
            val = masterdata[configid]
        }
        
        return val;
    },
    
    GetNorthPanel:function (){
        
        var wintitle=WtfGlobal.getLocaleText("hrms.common.add")+' '+this.GetMasterDataArrayValue(this.configid-1,WtfGlobal.getLocaleText("hrms.common.Subfield"));
        var windetail='';
        var image='';
        if(this.action=="Edit"){
            windetail=WtfGlobal.getLocaleText("hrms.common.Editthesubfieldinformation");
            image='../../images/master.gif';
        } else {
            windetail=WtfGlobal.getLocaleText("hrms.common.Filluptheinformationtoaddsubfield");
            image='../../images/master.gif';
        }
        
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:75,
            border:false,
            bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml(wintitle,windetail,image)
        });
    },
    GetAddEditForm:function (){
        var formfieldtype;
        if(this.configid==4 || this.configid==5){
            formfieldtype=Wtf.form.NumberField;
        }else{
            formfieldtype=Wtf.ux.TextField;
        }
        if(this.configid==21) {//Component Sub type
            
            var subTypeArr =[];
            subTypeArr.push([WtfGlobal.getLocaleText("hrms.common.Earning"),'1']);
            subTypeArr.push([WtfGlobal.getLocaleText("hrms.payroll.Deduction"),'2']);
            subTypeArr.push([WtfGlobal.getLocaleText("hrms.payroll.Tax"),'3']);
            subTypeArr.push([WtfGlobal.getLocaleText("hrms.payroll.additional.remuneration"),'4']);
            if(!isMalaysianCompany){
            	subTypeArr.push([WtfGlobal.getLocaleText("hrms.payroll.income.tax.component.type"),'5']);
            }
            subTypeArr.push([WtfGlobal.getLocaleText("hrms.payroll.EmployerContribution"),'0']);
            
            this.subtype=subTypeArr;
            this.subtypestore=new Wtf.data.SimpleStore({
                fields:[{
                    name:'type'
                },
                {
                    name:'code'
                }],
                data:this.subtype
            });
            this.weightageField=new Wtf.form.ComboBox({
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.type")+"*",
                store:this.subtypestore,
                displayField:'type',
                typeAhead: true,
                valueField:'code',
                allowBlank:false,
                width:200,
                labelWidth:100,
                scope:this,
                hiddenName:'wt',
                name:'wt',
                value:(this.action == "Edit")?this.rec.get("weightage"):1,
                mode: 'local',
                triggerAction: 'all',
                emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.type"),
                selectOnFocus:true
            });
        }else if(this.configid==25) {//Timesheet Job
            var reg=/^(([0-1][0-9]|[2][0-3]):([0-5][0-9]))|(([2][4]:[0][0]))|(([0-1][0-9]|[2][0-3]):([0-6][0]))$/;
             this.weightageField=new Wtf.ux.TextField({
                regex:reg,
                fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.hours")+"*"+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.timesheet.enterhours.format")),
                width:200,
                allowBlank:false,
                name:"wt",
                maxLength:5,
                value:(this.action == "Edit")?this.rec.get("weightage"):"00:00"
            });
        } else {
            this.weightageField=new Wtf.form.NumberField({
//                    xtype:"textfield",
                xtype:"numberfield",
                fieldLabel:WtfGlobal.getLocaleText("hrms.performance.heirarchy"),
                width:200,
                allowDecimals:false,
                maxLength:10,
                minValue : 0,
                id:this.id+'wtfield',
                name:"wt",
                value:(this.action == "Edit")?this.rec.get("weightage"):0
            })
        }
        
        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:15px",
//            url: Wtf.req.base + "hrms.jsp",
            url:"Common/Master/addMasterDataField.common",
            items:[
                this.name=new formfieldtype({
//                    xtype:"textfield",
                    fieldLabel:this.GetMasterDataArrayValue(this.configid-1,WtfGlobal.getLocaleText("hrms.common.AddSubField.Name"))+"*",
                    width:200,
                    allowDecimals:false,
                    allowNegative:false,
                    maxLength:100,
                    maxValue:100,
                    minValue:1,
                    id:this.id+'namefield',
                    name:"name",
                    value:(this.action == "Edit")?this.rec.get("name"):(this.configid==4 || this.configid==5)?undefined:""
                }),
                this.weightageField
            ]
        });
    },
    saveProjectDetail:function (){
             this.formtext=Wtf.getCmp(this.id+'namefield').getValue();
             var vflag=1;
          
          for(var i=0;i<this.store.getCount();i++)
               {
                   if(this.store.getAt(i).get('name')== this.formtext)
                       {
                           if(this.rec){
                               if(this.store.getAt(i).get('id')!=this.rec.get('id')){
                                    vflag=0;
                                }
                            } else {
                                vflag=0;
                            }
                       }
               }
               if(vflag==0 || this.formtext=="")
                 {
                        Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.common.error"),
                        msg:WtfGlobal.getLocaleText("hrms.common.field.blank.data.already.present"),
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });
                   }
                   else{
        if(this.AddEditForm.form.isValid() && this.save){
           var saveelement = Wtf.get('btnsave');
           saveelement.findParent('table').className = "x-btn-wrap x-btn x-item-disabled";
           this.save = false;
           this.AddEditForm.form.submit({              
                params:{
                    flag:204,
                    configid:this.configid,
                    action:this.action,
                    id:(this.action == "Edit")?this.rec.get("id"):""
                },
                success:function(){
                    msgFlag=0;
                    if(this.action == "Edit"){
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText({key:"hrms.common.mdeditedsuccessfully",params:[this.GetMasterDataArrayValue(this.configid-1, "Sub Field")]})]);
                    }else{
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText({key:"hrms.common.mdaddedsuccessfully",params:[this.GetMasterDataArrayValue(this.configid-1, "Sub Field")]})]);
                    }
                    this.close();
                    this.store.load({
                        params:{
                            configid:this.configid
                        }
                    });
                    this.loadMasterStores();
                },
                failure:function (){
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.common.status"),
                        msg:(this.action == "Edit")?WtfGlobal.getLocaleText("hrms.common.ErrorwhileeditingSubfield"):WtfGlobal.getLocaleText("hrms.common.ErrorwhileaddingSubfield"),
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });
                    this.save = true;
                    saveelement.findParent('table').className = "x-btn-wrap x-btn";
                },
                scope:this
            })
        }//end
                   }
    },

    loadMasterStores : function() {
        switch(this.configid) {
            case 1 :
                Wtf.desigStore.removeListener("load");
                Wtf.desigStore.load();
                Wtf.StoreMgr.add("desig",Wtf.desigStore);
                break;
            case 2 :
                Wtf.contextstore.removeListener("load");
                Wtf.contextstore.load();
                Wtf.StoreMgr.add("context",Wtf.contextstore);
                break;
            case 3 :
                Wtf.priostore.removeListener("load");
                Wtf.priostore.load();
                Wtf.StoreMgr.add("prio",Wtf.priostore);
                break;
            case 4 :
                Wtf.wthstore.removeListener("load");
                Wtf.wthstore.load();
                Wtf.StoreMgr.add("wth",Wtf.wthstore);
                break;
            case 5 :
            	Wtf.completedStore.removeListener("load");
            	Wtf.completedStore.load();
                Wtf.StoreMgr.add("comp",Wtf.completedStore);
                break;
            case 7 :
                Wtf.depStore.removeListener("load");
                Wtf.depStore.load();
                Wtf.StoreMgr.add("dep",Wtf.depStore);
                break;
            case 10 :
                Wtf.rankStore.removeListener("load");
                Wtf.rankStore.load();
                Wtf.StoreMgr.add("rank",Wtf.rankStore);
                break;
            case 12:
                Wtf.quaStore.removeListener("load");
                Wtf.quaStore.load();
                Wtf.StoreMgr.add("qua",Wtf.quaStore);
                break;
            case 14:
                Wtf.catgStore.removeListener("load");
                Wtf.catgStore.load();
                Wtf.StoreMgr.add("catg",Wtf.catgStore);
                break;
            case 15:
                Wtf.terStore.removeListener("load");
                Wtf.terStore.load();
                Wtf.StoreMgr.add("ter",Wtf.terStore);
                break;
        }
    }
});

