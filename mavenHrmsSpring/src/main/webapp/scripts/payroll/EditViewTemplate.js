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
Wtf.EditViewTemplate=function(config){
    Wtf.form.Field.prototype.msgTarget='side',
    config.layout="fit";
    config.closable=true;
    Wtf.EditViewTemplate.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.EditViewTemplate,Wtf.Panel,{
    initComponent:function(config){
        Wtf.EditViewTemplate.superclass.initComponent.call(this,config);
        this.storeURL="null";
        this.stran=String(this.range).substring(0,String(this.range).indexOf("-",0)-1);
        this.endran=String(this.range).substring(String(this.range).indexOf("-",0)+1,String(this.range).length);

        this.PayAreaForm12=new Wtf.form.FormPanel({
            columnWidth:0.25,           
            height:200,
            labelWidth:100,
            border:false,
            bodyBorder:'true',
            scope:this,
            frame:false,
            items:[
            this.tempname =new Wtf.form.TextField({
                fieldLabel:WtfGlobal.getLocaleText("hrms.CampaignDetail.TemplateName"),
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                id:"tempName"+this.id,
                scope:this,
                width:200,
                maxLength:40,
                value:this.templatename
            })
            ]
        });
        this.PayAreaForm13=new Wtf.form.FormPanel({
            columnWidth:0.25,           
            height:200,
            labelWidth:150,
            border:false,
            hidden:true,
            bodyBorder:'true',
            scope:this,
            frame:false,
            items:[
            this.newLimitStart=new Wtf.form.NumberField({
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.range.month")+' (<span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>)',
                width:150,
                labelWidth:60,
                allowBlank:false,
                allowNegative:false,
                maxLength:15,
                id:this.id+'editsalarystartrange',
                scope:this,
                value:this.stran
            })
            ]
        });
        this.PayAreaForm14=new Wtf.form.FormPanel({
            columnWidth:0.18,           
            height:200,
            border:false,
            hidden:true,
            bodyBorder:'true',
            scope:this,
            frame:false,
            items:[
            this.newLimitEnd=new Wtf.form.NumberField({
                hideLabel:true,
                scope:this,
                maxLength:15,
                allowBlank:false,
                allowNegative:false,
                initialPassField:this.id+'editsalarystartrange',
                value:this.endran,
                vtype:'range',
                width:150
            })
            ]
        });

        this.PayAreaForm15=new Wtf.form.FormPanel({
            columnWidth:0.24,           
            height:200,
            labelWidth:120,
            border:false,
            bodyBorder:'true',
            scope:this,
            frame:false,
            items:[
            this.addgroupCombo=new Wtf.form.FnComboBox({
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.AddtoDesignation"),
                scope:this,
                allowBlank:false,
                triggerAction: 'none',
                readOnly: true,
                mode:'local',
                selectOnFocus:true,
                typeAhead:true,
                disabled: true,
                width:200,
                store:Wtf.desigStore,
                addNewFn:this.addDesignation.createDelegate(this),
                plugins: [new Wtf.common.comboAddNew({
                    handler: function(){
                        WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
                    },
                    scope: this
                })],
                listeners:{
                    scope:this,
                    select:function(combo,record,index){
                        new Wtf.form.Hidden({
                            id:'GlobalGroupId1',
                            value:record.get('id'),
                            readOnly:true
                        });
                    }
                }
                ,
                displayField:'name',
                valueField:'id'                
            })
            ]
        });
        this.wageentryform1=new Wtf.WageEntryForm({
            edittemp:'yes',
            paramstore:'1',
            region:'west',
            parentId:this.id,
            id:this.id+'editwagegrid',
            width:'33%',
            bodyStyle: 'padding:15px;background-color:white'
        });

        this.taxentryform1=new Wtf.WageTaxDeducWin({
            edittemp:'yes',
            paramstore:'1',
            parentId:this.id,
            id:this.id+'edittaxgrid',
            region:'east',
            width:'33%',
            bodyStyle: 'padding:15px;background-color:white'
        });
        this.deductionentryform1=new Wtf.DeducEntryForm({
            edittemp:'yes',
            id:this.id+'editdeducgrid',
            paramstore:'1',
            parentId:this.id,
            region:'center',
            bodyStyle: 'padding:15px;background-color:white'
        });
        this.TemplateViewForm=new Wtf.Panel({
            region:'north',
            border:false,
            layout:'column',
            scope:this,
            style:'height:12%',
            bodyStyle:'margin-left:1%;margin-top:2%',
            items:[this.PayAreaForm12,this.PayAreaForm13,this.PayAreaForm14,this.PayAreaForm15]
        });
        this.SecondPanel=new Wtf.Panel({
            layout:'border',
            region:'center',
            border:false,
            scope:this,
            style:'height:88%',            
            items:[this.wageentryform1,this.deductionentryform1,this.taxentryform1]
        });
        this.MainDataEntryPanelE=new Wtf.Panel({
            layout:'border',
            bodyStyle:'background:#FFFFFF',
            scope:this,
            border:false,
            autoScroll:true,
            items:[this.TemplateViewForm,this.SecondPanel],
            bbar:['->',{
                text: WtfGlobal.getLocaleText("hrms.common.Save"),
                scope:this,
                id: "editTemp",
                disabled:true,
                iconCls:getButtonIconCls(Wtf.btype.submitbutton),
                minWidth:50,
                handler:function(){
                    if(this.PayAreaForm12.form.isValid()&&this.PayAreaForm13.form.isValid()&&this.PayAreaForm14.form.isValid()
                        &&this.PayAreaForm15.form.isValid()){                       
//                        var a=this.calcTotalAmount(this.wageentryform1.grid.getSelectionModel().getSelections(),"cash","rate",parseFloat(this.newLimitEnd.getValue()));
//                        var b=this.calcTotalAmount(this.deductionentryform1.DeductionGridPanel.getSelectionModel().getSelections(),"cash","rate",parseFloat(this.newLimitEnd.getValue()));
//                        var c=this.calcTotalAmount(this.taxentryform1.grid.getSelectionModel().getSelections(),"cash","rate",parseFloat(this.newLimitEnd.getValue()));
                        var a=this.calcTotalAmountA(this.wageentryform1.grid.getStore(),"cash","rate",parseFloat(this.newLimitEnd.getValue()));
                        var b=this.calcTotalAmountA(this.deductionentryform1.DeductionGridPanel.getStore(),"cash","rate",parseFloat(this.newLimitEnd.getValue()));
                        var c=this.calcTotalAmountA(this.taxentryform1.grid.getStore(),"cash","rate",parseFloat(this.newLimitEnd.getValue()));
//                        if(Math.round(a)!=this.newLimitEnd.getValue()){
//                            calMsgBoxShow(["Warning","Total of earnings should be equal to max salary range <br> of template."],0);
//                            return;
//                        }
//                       if(a<b+c){
//                            calMsgBoxShow(["Warning","Taxes and deductions are more than earnings."],0);
//                            return;
//                        }else{
                    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.payroll.save.selected.component"), function(btn){
                        if(btn=="yes") {                        
                            this.saveTemplate();                        
                        }else{
                            return;
                        }
                    },this);
//                        }
                    }else{
                         calMsgBoxShow(5,0);
                    }
                }
            }]

        });
        this.add(this.MainDataEntryPanelE);
        this.doLayout();
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        this.saveTemplate=function(){
            this.mywin= new Wtf.Window({
                height:150,
                width:340,
                ckf:this,
                scope:this,
                title:'Confirm',
                resizable:false,
                border:false,
                html:'<br/><center><font size=2px>'+WtfGlobal.getLocaleText("hrms.payroll.change.template.permanently.temporary")+'<br/><br><b>'+WtfGlobal.getLocaleText("hrms.Messages.DateCannotbeRetrive")+'<br/></b></font></center>',
                layout:'Column',
                items:[
                {
                    columnWidth:0.50,
                    border:false,
                    bodyStyle:"padding-left:50%;",
                    items:[{
                        text:WtfGlobal.getLocaleText("hrms.common.Permanent"),
                        xtype:'button',
                        scope:this,
                        handler:function(){                       
                            this.permanentortempSave(1);
                            Wtf.destroy(this.mywin);
                            this.groupStore.reload();
                            this.mywin.hide();                       
                        }
                    }]
                },{
                    columnWidth:0.50,
                    border:false,
                    bodyStyle:"padding-left:3%;",
                    items:[{
                        text:WtfGlobal.getLocaleText("hrms.payroll.temporary"),
                        xtype:'button',
                        scope:this,
                        handler:function(){
                            if(this.group==this.addgroupCombo.getValue()){
                                this.permanentortempSave(2);
                                Wtf.destroy(this.mywin);
                                this.groupStore.reload();
                                this.mywin.hide();
                            }
                            else{
                                Wtf.Msg.show({
                                    msg: WtfGlobal.getLocaleText("hrms.payroll.edited.template.cannot.proceed.saving.cannot.change.temporary"),
                                    scope:this,
                                    title:WtfGlobal.getLocaleText("hrms.common.Message"),
                                    width:260,
                                    buttons: Wtf.Msg.OK,
                                    animEl: 'elId',
                                    fn:function(btn,val){
                                        if(btn=='ok'){
                                            Wtf.destroy(this.mywin);
                                            this.mywin.hide();
                                            this.addgroupCombo.focus(true);
                                        }
                                    }
                                });
                            }
                        }
                    }]
                }]
            });
            this.mywin.show();
        }
        this.permanentortempSave=function(chk){            
            if(this.PayAreaForm12.getComponent(0).getRawValue().trim().length>0 && this.PayAreaForm13.getComponent(0).getRawValue().trim().length>0 && this.PayAreaForm14.getComponent(0).getRawValue().trim().length>0 && this.PayAreaForm15.getComponent(0).getRawValue().trim().length>0){
                if(this.wageentryform1.grid.getStore().getCount()-1>0 ){
                    if(parseFloat(this.PayAreaForm13.getComponent(0).getRawValue())<=parseFloat(this.PayAreaForm14.getComponent(0).getRawValue())){

//                        this.wageentryform1.grid.getSelectionModel().deselectRow(this.wageentryform1.grid.getStore().getCount()-1);
//                        this.taxentryform1.grid.getSelectionModel().deselectRow(this.taxentryform1.grid.getStore().getCount()-1);
//                        this.deductionentryform1.DeductionGridPanel.getSelectionModel().deselectRow(this.deductionentryform1.DeductionGridPanel.getStore().getCount()-1);

                     this.frmdta="{TName:'"+this.PayAreaForm12.getComponent(0).getRawValue().trim()+"',";
                        this.frmdta+="RStart:'"+this.PayAreaForm13.getComponent(0).getRawValue()+"',";
                        this.frmdta+="GId:'"+this.addgroupCombo.getValue()+"',";
                        this.frmdta+="REnd:'"+this.PayAreaForm14.getComponent(0).getRawValue()+"'}";

                        this.saveTemplateData="{TaxDataADD:[";
//                        for(i=0;i<this.taxentryform1.grid.getSelectionModel().getSelections().length;i++){
                        for(i=0;i<this.taxentryform1.grid.getStore().getCount()-1;i++){
                            if(i>0){
                                this.saveTemplateData+=",";
                            }
                            this.saveTemplateData+="{TaxId:'"+this.taxentryform1.grid.getStore().getAt(i).get("id")+"',TaxRate:'"+this.taxentryform1.grid.getStore().getAt(i).get("cash")+"'}";
                        }
                        this.saveTemplateData+="]}";

                        this.saveTemplateData1="{WageDataADD:[";
                        for(i=0;i<this.wageentryform1.grid.getStore().getCount()-1;i++){
                            if(i>0){
                                this.saveTemplateData1+=",";
                            }
                            this.saveTemplateData1+="{WageId:'"+this.wageentryform1.grid.getStore().getAt(i).get("id")+"',WageRate:'"+this.wageentryform1.grid.getStore().getAt(i).get("cash")+"'}";
                        }
                        this.saveTemplateData1+="]}";

                        this.saveTemplateData2="{DeducDataADD:[";
                        for(i=0;i<this.deductionentryform1.DeductionGridPanel.getStore().getCount()-1;i++){
                            if(i>0){
                                this.saveTemplateData2+=",";
                            }
                            this.saveTemplateData2+="{DeducId:'"+this.deductionentryform1.DeductionGridPanel.getStore().getAt(i).get("id")+"',DeducRate:'"+this.deductionentryform1.DeductionGridPanel.getStore().getAt(i).get("cash")+"'}";
                        }
                        this.saveTemplateData2+="]}";


                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + "PayrollHandler.jsp" ,
                            url:"Payroll/Template/updateTemplateData.py",
                            method:'post',
                            params:{
                                save:'true',
                                saveType:'updatetemplatedata',
                                formdata:this.frmdta,
                                taxdata:this.saveTemplateData,
                                wagedata:this.saveTemplateData1,
                                deducdata:this.saveTemplateData2,
                                tempid:this.templateid,
                                torp:chk,
                                templatename:this.templatename
                            }
                        },
                        this,
                        function(req,res){
                            msgFlag=0;
                            if(req.value.toString()=="success"){
                                calMsgBoxShow(1,0);
                            } else{
                                calMsgBoxShow(13,0);
                            }
                        },
                        function(req,res){
                            }
                            );
                    } else{
                        calMsgBoxShow(3,0);
                    }
                } else{
                    calMsgBoxShow(4,0);
                }
            } else{
                calMsgBoxShow(5,0);
            }
            
        }
//        this.permanentortempSave=function(chk){
//            if(this.PayAreaForm12.getComponent(0).getRawValue().trim().length>0 && this.PayAreaForm13.getComponent(0).getRawValue().trim().length>0 && this.PayAreaForm14.getComponent(0).getRawValue().trim().length>0 && this.PayAreaForm15.getComponent(0).getRawValue().trim().length>0){
//                if(this.wageentryform1.grid.getSelectionModel().getSelections().length>0 ){
//                    if(parseFloat(this.PayAreaForm13.getComponent(0).getRawValue())<=parseFloat(this.PayAreaForm14.getComponent(0).getRawValue())){
//
//                        this.wageentryform1.grid.getSelectionModel().deselectRow(this.wageentryform1.grid.getStore().getCount()-1);
//                        this.taxentryform1.grid.getSelectionModel().deselectRow(this.taxentryform1.grid.getStore().getCount()-1);
//                        this.deductionentryform1.DeductionGridPanel.getSelectionModel().deselectRow(this.deductionentryform1.DeductionGridPanel.getStore().getCount()-1);
//
//                     this.frmdta="{TName:'"+this.PayAreaForm12.getComponent(0).getRawValue().trim()+"',";
//                        this.frmdta+="RStart:'"+this.PayAreaForm13.getComponent(0).getRawValue()+"',";
//                        this.frmdta+="GId:'"+this.PayAreaForm13.getComponent(0).getRawValue()+"',";
//                        this.frmdta+="REnd:'"+this.PayAreaForm14.getComponent(0).getRawValue()+"'}";
//
//                        this.saveTemplateData="{TaxDataADD:[";
//                        for(i=0;i<this.taxentryform1.grid.getSelectionModel().getSelections().length;i++){
//                            if(i>0){
//                                this.saveTemplateData+=",";
//                            }
//                            this.saveTemplateData+="{TaxId:'"+this.taxentryform1.grid.getSelectionModel().getSelections()[i].get("id")+"',TaxRate:'"+this.taxentryform1.grid.getSelectionModel().getSelections()[i].get("cash")+"'}";
//                        }
//                        this.saveTemplateData+="]}";
//
//                        this.saveTemplateData1="{WageDataADD:[";
//                        for(i=0;i<this.wageentryform1.grid.getSelectionModel().getSelections().length;i++){
//                            if(i>0){
//                                this.saveTemplateData1+=",";
//                            }
//                            this.saveTemplateData1+="{WageId:'"+this.wageentryform1.grid.getSelectionModel().getSelections()[i].get("id")+"',WageRate:'"+this.wageentryform1.grid.getSelectionModel().getSelections()[i].get("cash")+"'}";
//                        }
//                        this.saveTemplateData1+="]}";
//
//                        this.saveTemplateData2="{DeducDataADD:[";
//                        for(i=0;i<this.deductionentryform1.DeductionGridPanel.getSelectionModel().getSelections().length;i++){
//                            if(i>0){
//                                this.saveTemplateData2+=",";
//                            }
//                            this.saveTemplateData2+="{DeducId:'"+this.deductionentryform1.DeductionGridPanel.getSelectionModel().getSelections()[i].get("id")+"',DeducRate:'"+this.deductionentryform1.DeductionGridPanel.getSelectionModel().getSelections()[i].get("cash")+"'}";
//                        }
//                        this.saveTemplateData2+="]}";
//
//
//                        Wtf.Ajax.requestEx({
////                            url: Wtf.req.base + "PayrollHandler.jsp" ,
//                            url:"Payroll/Template/updateTemplateData.py",
//                            method:'post',
//                            params:{
//                                save:'true',
//                                saveType:'updatetemplatedata',
//                                formdata:this.frmdta,
//                                taxdata:this.saveTemplateData,
//                                wagedata:this.saveTemplateData1,
//                                deducdata:this.saveTemplateData2,
//                                tempid:this.templateid,
//                                torp:chk,
//                                templatename:this.templatename
//                            }
//                        },
//                        this,
//                        function(req,res){
//                            msgFlag=0;
//                            if(req.value.toString()=="success"){
//                                calMsgBoxShow(1,0);
//                            } else{
//                                calMsgBoxShow(13,0);
//                            }
//                        },
//                        function(req,res){
//                            }
//                            );
//                    } else{
//                        calMsgBoxShow(3,0);
//                    }
//                } else{
//                    calMsgBoxShow(4,0);
//                }
//            } else{
//                calMsgBoxShow(5,0);
//            }
//
//        }
        new Wtf.form.Hidden({
            id:'GlobalCompanyName1',
            value:'companyid',
            readOnly:true
        });

        if(this.deductionentryform1.storededuc.getCount()>0){
            this.deductionentryform1.storededuc.removeAll();
        }
        this.deductionentryform1.storededucforgrid.proxy.conn.url="Payroll/Deduction/getDeductionData.py";
        this.deductionentryform1.storededucforgrid.load({
            params:"type=TDeduction&cname=aa&TempId="+this.templateid
        });
        if(this.taxentryform1.storetax.getCount()>0){
            this.taxentryform1.storetax.removeAll();
        }

        this.taxentryform1.storetaxforgrid.proxy.conn.url="Payroll/Tax/getTaxData.py";
        this.taxentryform1.storetaxforgrid.load({
            params:"type=TTax&cname=aa&TempId="+this.templateid
        });
        if(this.wageentryform1.storewage.getCount()>0){
            this.wageentryform1.storewage.removeAll();
        }

        this.wageentryform1.storewageforgrid.proxy.conn.url="Payroll/Wage/getWagesData.py";
        this.wageentryform1.storewageforgrid.load({
            params:"type=TWages&cname=aa&TempId="+this.templateid
        });
        this.doLayout();

        if(!Wtf.StoreMgr.containsKey("desig")){            
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
            Wtf.desigStore.on('load',this.onloadfunc,this);
        }
        else{
            this.onloadfunc();
        }
        this.wageentryform1.grid.on("validateedit",function(e){
            Wtf.getCmp("editTemp").enable();
        },this);
        this.taxentryform1.grid.on("validateedit",function(e){
            Wtf.getCmp("editTemp").enable();
        },this);
        this.deductionentryform1.DeductionGridPanel.on("validateedit",function(e){
            Wtf.getCmp("editTemp").enable();
        },this);
        Wtf.getCmp("tempName"+this.id).on("change",function(e){
            Wtf.getCmp("editTemp").enable();
        },this);

    },    
    onloadfunc : function(){        
        this.k=0;        
        for(this.k=0;this.k<Wtf.desigStore.getCount();this.k++){
            if(Wtf.desigStore.getAt(this.k).get('name')==this.group){
                new Wtf.form.Hidden({
                    id:'GlobalGroupId1',
                    value:Wtf.desigStore.getAt(this.k).get('id'),
                    readOnly:true
                });
            }
            this.addgroupCombo.setValue(this.group);            
        }
    },
    addDesignation:function(){
        WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
    },
    calcTotalAmount:function(rs,amountField,typeField, baseSalary){
        var total=0;
        for(var i=0;i<rs.length;i++){
            var rec=rs[i];
            var val=rec.data[amountField]*1;
            if(rec.data[typeField]=="1"){
                val=baseSalary/100*val;
            }
            total+=val;
        }
        return total;
    },
    calcTotalAmountA:function(rs,amountField,typeField, baseSalary){
        var total=0;
        for(var i=0;i<rs.getCount();i++){
            var rec=rs.getAt(i);
            var val=rec.data[amountField]*1;
            if(rec.data[typeField]=="1"){
                val=baseSalary/100*val;
            }
            total+=val;
        }
        return total;
    }
});
