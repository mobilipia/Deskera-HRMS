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
Wtf.AddIncometaxWin = function (config){
    Wtf.apply(this,config);
    config.resizable=false;
    Wtf.AddIncometaxWin.superclass.constructor.call(this,{
        buttons:[
        {
            text: WtfGlobal.getLocaleText("hrms.common.Save"),
            scope:this,
            handler:function (){
                if(!this.AddEditForm.form.isValid()) {
                    return ;
                } else
                {
                    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.timesheet.save.data"), WtfGlobal.getLocaleText("hrms.common.want.to.save.changes"), function(btn){
                        if(btn!="yes") {
                            this.close();
                        }
                        else{
                            calMsgBoxShow(200,4,true);
                            this.AddEditForm.getForm().submit({
//                                url: Wtf.req.base + "PayrollHandler.jsp" ,
                                url:"Payroll/Tax/setNewIncometax.py",
                                params:{
                                    save:true,
                                    saveType:"AddIncometax",
                                    catgryid:this.ctgrycombo.getValue()
                                },
                                method:'post',
                                scope:this,
                                success:function(a,req){
                                    req=eval('('+req.response.responseText+')');
                                    if(req.data.value=='exist'){
                                        calMsgBoxShow(135,0);
                                    }
                                    if(req.data.value=='success' || req.data.value==WtfGlobal.getLocaleText("hrms.common.success")){
                                        Wtf.getCmp('incometaxwin').close();
                                        calMsgBoxShow(136,0);
                                        this.incometaxstore.load({
                                            params:{
                                                type:'GetTaxperCatgry',
                                                categoryid:this.ctgrycombo.getValue(),
                                                start:0,
                                                limit:this.gridp.pag.pageSize
                                            }
                                        });
                                    }
                                },
                                failure:function(req,res){
                                }
                            });
                        }
                    },this);
                }
            }
        }, 
        {
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler:function (){               
                this.close();
            },
            scope:this
        }
        ]
    });
}  
Wtf.extend(Wtf.AddIncometaxWin,Wtf.Window,{
    initComponent:function (){
        Wtf.AddIncometaxWin.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetAddEditForm();
        this.mainPanel = new Wtf.Panel({
            layout:"border",
            border:false,
            items:[
            this.northPanel,
            this.AddEditForm
            ]
        });
        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){
        var wintitle=WtfGlobal.getLocaleText("hrms.payroll.add.income.tax");
        var windetail=WtfGlobal.getLocaleText("hrms.payroll.fill.information.add.income.tax");
        var image=this.typeimage;
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:90,
            border:false,
            bodyStyle:"backgroubodyStylend-color:white;padding:8px;border-bottom:1px solid #bfbfbf;background-color: white",
            html: getTopHtml(wintitle,windetail,image)
        });
    },
    GetAddEditForm:function (){
        this.percent=[[WtfGlobal.getLocaleText("hrms.payroll.Percent"),'%'],[WtfGlobal.getLocaleText("hrms.payroll.Amount"),'$']];
        this.percentstore=new Wtf.data.SimpleStore({
            fields:[{
                name:'type'
            },
            {
                name:'code'
            }],
            data:this.percent
        });
        this.taxrateinamount=new Wtf.Panel({
            width:400,
            frame:false,
            border:false,
            layout:'column',
            items:[
            {
                columnWidth:.52,
                frame:false,
                border:false,
                layout:'form',
                items:[{
                    xtype:"numberfield",
                    fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.income.slab"),
                    allowBlank:false,
                    maxLength:10,
                    editable:false,
                    name:'rangefrom',
                    width:100,
                    labelWidth:100
                }]
            },{
                columnWidth:.48,
                frame:false,
                border:false,
                items:[this.myrate=new Wtf.form.NumberField({
                    width:95,
                    maxLength:10,
                    name:'rangeto'
                })]
            }
            ]
        });

        
        this.ctgrycombo=new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.Masters.Category")+'*',
            store:Wtf.catgStore,
            id:'catgorycombo3',
            displayField:'name',
            typeAhead: true,
            valueField:'id',
            name:'categoryname',
            allowBlank:false,
            width:200,
            labelWidth:200,
            mode: 'local',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.common.select.category"),
            selectOnFocus:true,
            addNewFn:this.addCategory.createDelegate(this),
            listeners:
            {
                scope:this,
                Select:function(combo,record,index)
                {
                    this.categoryid=record.get('id');
                }
            }
        });

        if(!Wtf.StoreMgr.containsKey("catg")){
            Wtf.catgStore.on('load',this.setCategary,this);
            Wtf.catgStore.load();
            Wtf.StoreMgr.add("catg",Wtf.catgStore)
        }else{
            this.setCategary();
        }


        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            scope:this,
            defaults:{
                xtype:'numberfield',
                width:200,
                allowBlank:false
            },
            bodyStyle:"background-color:#f1f1f1;padding:15px",
            items:[
            this.ctgrycombo,
            {
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.Rate")+"*",
                scope:this,
                name:"rate",
                regex:/^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/
            },
            {
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.min")+"*",
                name:"rangefrom",
                id:'incometaxmin',
                displayField:'cname',
                regex:/^\d{0,10}$/
            },
            {
                labelWidth:100,
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.max")+"*",
                name:'rangeto',
                scope:this,
                vtype:'range',
                regex:/^\d{0,10}$/,
                initialPassField: 'incometaxmin'
            }]
        });
    },
    addCategory:function(){
        WtfGlobal.showmasterWindow(14,Wtf.catgStore,"Add");
    },
    setCategary:function(){
        if(Wtf.catgStore.getCount()>0){
            this.ctgrycombo.setValue(Wtf.catgStore.getAt(Wtf.catgStore.getCount()-1).get('id'));
        }
    }
});
