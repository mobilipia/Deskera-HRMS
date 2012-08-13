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
Wtf.AddPayrollTemplate=function(config){
    Wtf.form.Field.prototype.msgTarget='side';
    config.layout="fit";
    config.closable=true;
    Wtf.AddPayrollTemplate.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.AddPayrollTemplate,Wtf.Panel,{
    showhide:function (dd_textfield,display,labeldisplay) {
        dd_textfield.setVisible(display);
        dd_textfield.container.up('div.x-form-item').dom.style.display = labeldisplay;


        var ct = dd_textfield.el.findParent('div.x-form-item', 4, true);
        var label = ct.first('label.x-form-item-label');
        label.dom.style.display=labeldisplay;
    },
    initComponent:function(config){
        Wtf.AddPayrollTemplate.superclass.initComponent.call(this,config);

         var intervalrec = new Wtf.data.Record.create([
        {
            name: 'name'
        },{
            name:'id'
        }

        ]);
        var intervalreader= new Wtf.data.ArrayReader({
            }, intervalrec);
        var intervaldata = [[WtfGlobal.getLocaleText("hrms.payroll.onceamonth"),1],[WtfGlobal.getLocaleText("hrms.payroll.twiceamonth"),2],[WtfGlobal.getLocaleText("hrms.payroll.onceaweek"),3]]
        var intervalStore = new Wtf.data.Store({
            reader:intervalreader,
            data:intervaldata
        });
        
        this.check=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.ShowBorder"),
            name:'borderCheckBox',
            checked:this.editMode ? this.showBorder : true
        });
        
        this.payintervalCombo = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.PaymentInterval")+'*',
            mode:'local',
            displayField:'name',
            valueField:'id',
            allowBlank:false,
            disabled:(this.edit == 1)?true:false,
            name:'payinterval',
            typeAhead:true,
            hiddenName:'payinterval',
            triggerAction:'all',
            store:intervalStore,
            forceSelection:true,
            listeners:{
                scope:this,
                change:function(){
    				this.payintervalComboChange();
            	},
                select:function(){
        			this.payintervalComboChange();
                }
            }
        });
        this.payintervalCombo.on("select",function(combo,rec,index){
            this.assignedUsersPanel.setpayinterval(rec.get("id"));
        },this);
        this.monthlydata = [['1','1st'],['2','2nd'],['3','3rd'],['4','4th'],['5','5th'],['6','6th'],['7','7th'],['8','8th'],
                ['9','9th'],['10','10th'],['11','11th'],['12','12th'],['13','13th'],['14','14th'],['15','15th'],['16','16th'],['17','17th'],['18','18th'],
                ['19','19th'],['20','20th'],['21','21th'],['22','22th'],['23','23th'],['24','24th'],['25','25th'],['26','26th'],
                ['27','27th'],['28','28th'],['29','29th'],['30','30th'],['31','31th']];
        this.bimonthlydata = [['1','1st'],['16','16th']];
        this.datestore = new Wtf.data.SimpleStore({
            fields :['id', 'name'],
            data:this.monthlydata
        });
        this.daystore = new Wtf.data.SimpleStore({
            fields :['id', 'name'],
            data:[['1',WtfGlobal.getLocaleText("hrms.timesheet.sunday")],['2',WtfGlobal.getLocaleText("hrms.timesheet.monday")],['3',WtfGlobal.getLocaleText("hrms.timesheet.tuesday")],['4',WtfGlobal.getLocaleText("hrms.timesheet.wednesday")],['5',WtfGlobal.getLocaleText("hrms.timesheet.thursday")],['6',WtfGlobal.getLocaleText("hrms.timesheet.friday")],['7',WtfGlobal.getLocaleText("hrms.timesheet.saturday")]]
        });
        this.datepicker = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.Date")+"*",
            store:this.datestore,
            name:'date',
            displayField:'name',
            valueField:'id',
//            allowBlank:false,
            mode:'local',
            triggerAction:'all',
            disabled:(this.edit == 1)?true:false,
            forceSelection:true
        })
        this.datepicker.on("select",function(combo,rec,index){
            this.assignedUsersPanel.seteffdate(rec.get("id"));
        },this);
        
        this.daypicker = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Day")+"*",
            store:this.daystore,
            name:'date',
            displayField:'name',
            valueField:'id',
            mode:'local',
            triggerAction:'all',
            hidden:true,
            hideLabel:true,
            disabled:(this.edit == 1)?true:false,
            forceSelection:true
        })
        this.daypicker.on("select",function(combo,rec,index){
            this.assignedUsersPanel.seteffdate(rec.get("id"));
        },this);



        this.designcombo=new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.AddtoDesignation")+'*',
            labelWidth:200,
            scope:this,
            allowBlank:false,
            store:Wtf.desigStore,
            triggerAction: 'all',
            disabled:(this.edit == 1)?true:false,
            typeAhead:true,            
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.Selectdesignation"),
            displayField:'name',
            valueField:'id',
            hiddenName:"id",
            name:'categoryname',
            mode:'local',
            forceSelection:true,
            width:200,
            addNewFn:this.addDesignation.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
            		if(!this.isDesgDisabled){
            			WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
            			this.designcombo.collapse();
            		}
                },
                scope: this
            })],
            listeners:{
                scope:this,
                select:function(combo,record,index){
                    new Wtf.form.Hidden({
                        id:this.id+this.globname+'GlobalGroupId',
                        value:record.get('id'),
                        readOnly:true
                    });
                }
            }
        });
        
        this.designcombo.on("disable",function(){
        	this.isDesgDisabled = true;
        },this);

        this.tempformPanel=new Wtf.form.FormPanel({
	  		border:false,
	  		frame:false,
	  		bodyStyle:"background-color:#FFFFFF;padding:20px 20px 20px 20px;",
	        region:'north',
            scope:this,
            style:'height:15%',
            items:[{
            	xtype:'panel',
            	frame:false,
            	border:false,
            	layout:'column', 
            	items:[{
            		columnWidth:.33,
            		frame:false,
            		border:false,
            		layout:'form',
            		items:[
            		       this.tempname=new Wtf.form.TextField({
            		    	   fieldLabel:WtfGlobal.getLocaleText("hrms.CampaignDetail.TemplateName")+'*',
            		    	   allowBlank:false,
            		    	   validator:WtfGlobal.noBlankCheck,
            		    	   scope:this,
            		    	   //value:this.editMode ? this.templatename : '',
            		           maxLength:50
                	      }),this.check
                	]
                },{
                	xtype:'panel',
                	columnWidth:0.25,
                	border:false,
                	hidden:true,
                	bodyStyle:'padding-left:10px;',
                	layout:'form',
                	items:[
                	       this.startrange=new Wtf.form.NumberField({
                	    	   fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.range.month")+' (<span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>)',
                	    	   allowBlank:false,
                	    	   allowNegative:false,
                	    	   value:0,
                	    	   id:this.id+'salarystartrange',
                	    	   maxLength:10,
                	    	   scope:this
                	       })
                    ]
                },{
                	xtype:'panel',
                	columnWidth:0.15,
                	border:false,
                	hidden:true,
                	layout:'form',
                	items:[
                	       this.endrange=new Wtf.form.NumberField({
                	    	   allowBlank:false,
                	    	   maxLength:10,
                	    	   value:0,
                	    	   allowNegative:false,
                	    	   scope:this,
                	    	   vtype:'range',
                	    	   initialPassField:this.id+'salarystartrange',
                	    	   hideLabel:true
                	       })
                    ]
                },{
                	columnWidth:.33,
            		frame:false,
            		border:false,
            		layout:'form',
                	items:[
                	       this.designcombo
                	]
                },{
                	columnWidth:.33,
                	frame:false,
                	border:false,
                	layout:'form',
                	items:[this.payintervalCombo,this.datepicker,this.daypicker]
                }]}]
        });  
        if(this.editMode){
        	this.tempname.setValue(this.templatename);
        }
        this.wageentryform=new Wtf.WageEntryForm({
            inv:this.endrange.getValue(),
            paramstore:'0',
            bodyStyle: 'padding:10px;background-color:white',
            region:'center',
            parentId:this.id,
            id:this.id+'addwage'
        });

        this.empcontribform=new Wtf.EmployerContributionForm({
            inv:this.endrange.getValue(),
            paramstore:'0',
            bodyStyle: 'padding:10px;background-color:white',
            region:'south',
            parentId:this.id,
            height:250,
            id:this.id+'addempcontrib'
        });

        this.taxentryform=new Wtf.WageTaxDeducWin({
            paramstore:'0',
            bodyStyle: 'padding:10px;background-color:white',
            region:'south',
            parentId:this.id,
            height:250,

            id:this.id+'addtax'
           
        });
               
        this.deductionentryform=new Wtf.DeducEntryForm({
            paramstore:'0',
            bodyStyle: 'padding:10px;background-color:white',
            region:'center',
            parentId:this.id,
            id:this.id+'adddeduc'
            
        });
             this.assignedUsersPanel = new Wtf.userPayCycleGrid({
                 region:'east',
                 width:'33%',
                 bodyStyle: 'padding:10px;background-color:white',
                 border:false,
                 templateid:this.templateid
                 
             });

        this.SecondPanelA=new Wtf.Panel({
            layout:'border',
            region:'center',
            border:false,
            scope:this,
            style:'height:88%',
            items:[new Wtf.Panel({border:false,split:false,width:'34%',layout:'border',region:'west',items:[this.wageentryform,this.empcontribform]}),
                new Wtf.Panel({border:false,split:true,width:'33%',layout:'border',region:'center',items:[this.deductionentryform,this.taxentryform]}),this.assignedUsersPanel]
        });
        this.designcombo.on("select",function(combo,rec,index){
            this.assignedUsersPanel.setDesignationId(rec.get("id"));
        },this);

        this.MainDataEntryPanelA=new Wtf.Panel({
            layout:'border',
            bodyStyle:'background:#FFFFFF',
            scope:this,
            border:false,
            autoScroll:true,
            items:[this.tempformPanel,this.SecondPanelA],
            bbar:['->',
                {
                    text:WtfGlobal.getLocaleText("hrms.payroll.SaveandClose"),
                    scope:this,
                    id: "editTemp1",
                    iconCls:"pwndCommon submitnexitbuttonIcon",
                    minWidth:40,
                    handler:function(){
                        this.saveTemplateA(true);
                    }
                },'-',{
                    text:WtfGlobal.getLocaleText("hrms.common.Save"),
                    scope:this,
                    id: "editTemp",
                    iconCls:getButtonIconCls(Wtf.btype.submitbutton),
                    minWidth:40,
                    handler:function(){
                        this.saveTemplateA(false);
                    }
                }]
        });
        this.add(this.MainDataEntryPanelA);
        this.doLayout();
        this.on('activate', function(tp1, tab1){
            this.doLayout();
        });
        if(this.editMode){
            new Wtf.form.Hidden({
            id:'GlobalCompanyName1',
            value:'companyid',
            readOnly:true
        });

        if(this.deductionentryform.storededuc.getCount()>0){
            this.deductionentryform.storededuc.removeAll();
        }
        this.deductionentryform.storededucforgrid.proxy.conn.url="Payroll/Deduction/getDeductionData.py?cname=aa&TempId="+this.templateid;
        this.deductionentryform.storededucforgrid.load({params:{
                type:'TDeduction'
        }});
        if(this.taxentryform.storetax.getCount()>0){
            this.taxentryform.storetax.removeAll();
        }

        this.taxentryform.storetaxforgrid.proxy.conn.url="Payroll/Tax/getTaxData.py?cname=aa&TempId="+this.templateid;
        this.taxentryform.storetaxforgrid.load({params:{
                type:'TTax'
        }});
        if(this.wageentryform.storewage.getCount()>0){
            this.wageentryform.storewage.removeAll();
        }

        this.wageentryform.storewageforgrid.proxy.conn.url="Payroll/Wage/getWagesData.py?cname=aa&TempId="+this.templateid;
        this.wageentryform.storewageforgrid.load({params:{
                type:'TWages'
        }});

        if(this.empcontribform.storewage.getCount()>0){
            this.empcontribform.storewage.removeAll();
        }

        this.empcontribform.storewageforgrid.proxy.conn.url="Payroll/EmpContrib/getEmployerContribData.py?cname=aa&TempId="+this.templateid;
        this.empcontribform.storewageforgrid.load({params:{
                type:'TEC'
        }});
        
        this.doLayout();

        

        
       
        this.wageentryform.grid.on("validateedit",function(e){
            Wtf.getCmp("editTemp").enable();
            Wtf.getCmp("editTemp1").enable();
        },this);
        this.taxentryform.grid.on("validateedit",function(e){
            Wtf.getCmp("editTemp").enable();
            Wtf.getCmp("editTemp1").enable();
        },this);
        this.deductionentryform.DeductionGridPanel.on("validateedit",function(e){
            Wtf.getCmp("editTemp").enable();
            Wtf.getCmp("editTemp").enable();
        },this);
    }
        this.saveTemplateA=function(closeorno){
            if(this.payintervalCombo.getValue()==1||this.payintervalCombo.getValue()==2) {
                if(this.datepicker.getValue()=="") {
                    calMsgBoxShow(5,0);
                    return;
                }
            } else {
                if(this.daypicker.getValue()=="") {
                    calMsgBoxShow(5,0);
                    return;
                }
            }
            if(this.tempformPanel.form.isValid()){
                var _assignedEmployees = this.assignedUsersPanel.getRecordsJSON();
               // this.wageentryform.grid.getSelectionModel().deselectRow(this.wageentryform.grid.getStore().getCount()-1);
                //this.taxentryform.grid.getSelectionModel().deselectRow(this.taxentryform.grid.getStore().getCount()-1);
                //this.deductionentryform.DeductionGridPanel.getSelectionModel().deselectRow(this.deductionentryform.DeductionGridPanel.getStore().getCount()-1);

               // var wageslength=this.wageentryform.grid.getSelectionModel().getSelections().length;
               //var taxslength=this.taxentryform.grid.getSelectionModel().getSelections().length;
               // var dedslength=this.deductionentryform.DeductionGridPanel.getSelectionModel().getSelections().length;
                var wageslength=this.wageentryform.grid.getStore().getCount() - 1;
                var taxslength=this.taxentryform.grid.getStore().getCount() - 1;
                var dedslength=this.deductionentryform.DeductionGridPanel.getStore().getCount() - 1;
                var eclength=this.empcontribform.grid.getStore().getCount() - 1;

                if( wageslength>0 ){
                    if(parseFloat(this.startrange.getValue())<=parseFloat(this.endrange.getValue())){
                        //var a=this.calcTotalAmount(this.wageentryform.grid.getSelectionModel().getSelections(),"cash","rate",parseFloat(this.endrange.getValue()));
                        //var b=this.calcTotalAmount(this.deductionentryform.DeductionGridPanel.getSelectionModel().getSelections(),"cash","rate",parseFloat(this.endrange.getValue()));
                        var a=this.calcTotalAmountA(this.wageentryform.grid.getStore(),"cash","rate",parseFloat(this.endrange.getValue()));
                        var b=this.calcTotalAmountA(this.deductionentryform.DeductionGridPanel.getStore(),"cash","rate",parseFloat(this.endrange.getValue()));
                        var c=this.calcTotalAmountA(this.taxentryform.grid.getStore(),"cash","rate",parseFloat(this.endrange.getValue()));
//                        if(Math.round(a)!=this.endrange.getValue()){
//                            calMsgBoxShow(["Warning","Total of earnings should be equal to max salary range <br> of template."],0);
//                            return;
//                        } else {
//
//                        }
//                        if(a<b+c){
//                            calMsgBoxShow(["Warning","Taxes and deductions are more than earnings."],0);
//                            return;
//                        }

                        this.frmdta="{TName:'"+this.tempname.getValue()+"',";
                        this.frmdta+="RStart:'"+this.startrange.getValue()+"',";
                        this.frmdta+="REnd:'"+this.endrange.getValue()+"',";
                        this.frmdta+="payInterval:'"+this.payintervalCombo.getValue()+"',";
                        if(this.payintervalCombo.getValue()==1)
                            this.frmdta+="effdate:'"+this.datepicker.getValue()+"',";
                        else if(this.payintervalCombo.getValue()==3)
                            this.frmdta+="effdate:'"+this.daypicker.getValue()+"',";
                        else if(this.payintervalCombo.getValue()==2)
                            this.frmdta+="effdate:'"+this.datepicker.getValue()+"',";
                        this.frmdta+="showborder:'"+this.check.getValue()+"',";
                        this.frmdta+="GId:'"+this.designcombo.getValue()+"'}";
                        this.saveTemplateData="{TaxDataADD:[";
                        for(i=0;i<taxslength;i++){
                            if(i>0){
                                this.saveTemplateData+=",";
                            }
                            //this.saveTemplateData+="{TaxId:'"+this.taxentryform.grid.getSelectionModel().getSelections()[i].get("id")+"',TaxRate:'"+this.taxentryform.grid.getSelectionModel().getSelections()[i].get("cash")+"'}";
                            this.saveTemplateData+="{TaxId:'"+this.taxentryform.grid.getStore().getAt(i).get("id")+"',TaxRate:'"+this.taxentryform.grid.getStore().getAt(i).get("cash")+"'}";
                        }
                        this.saveTemplateData+="]}";
                        //this.convertToPercent(this.wageentryform.grid.getStore(),"cash","rate",parseFloat(this.endrange.getValue()));
                        this.saveTemplateData1="{WageDataADD:[";

                        for(i=0;i<wageslength;i++){
                            if(i>0){
                                this.saveTemplateData1+=",";
                            }
                            if(this.wageentryform.grid.getStore().getAt(i).get("id") == -1){
                                calMsgBoxShow(28,0);
                                return;
                            }
                            //this.saveTemplateData1+="{WageId:'"+this.wageentryform.grid.getSelectionModel().getSelections()[i].get("id")+"',WageRate:'"+this.wageentryform.grid.getSelectionModel().getSelections()[i].get("cash")+"'}";
                            this.saveTemplateData1+="{WageId:'"+this.wageentryform.grid.getStore().getAt(i).get("id")+"',WageRate:'"+this.wageentryform.grid.getStore().getAt(i).get("cash")+"'}";
                        }
                        this.saveTemplateData1+="]}";

                        this.saveTemplateData2="{DeducDataADD:[";
                        for(i=0;i<dedslength;i++){
                            if(i>0){
                                this.saveTemplateData2+=",";
                            }
                            if(this.deductionentryform.DeductionGridPanel.getStore().getAt(i).get("id") == -1){
                                calMsgBoxShow(28,0);
                                return;
                            }
                            //this.saveTemplateData2+="{DeducId:'"+this.deductionentryform.DeductionGridPanel.getSelectionModel().getSelections()[i].get("id")+"',DeducRate:'"+this.deductionentryform.DeductionGridPanel.getSelectionModel().getSelections()[i].get("cash")+"'}";
                            this.saveTemplateData2+="{DeducId:'"+this.deductionentryform.DeductionGridPanel.getStore().getAt(i).get("id")+"',DeducRate:'"+this.deductionentryform.DeductionGridPanel.getStore().getAt(i).get("cash")+"'}";
                        }
                        this.saveTemplateData2+="]}";

                        this.saveTemplateData3="{ECDataADD:[";

                        for(i=0;i<eclength;i++){
                            if(i>0){
                                this.saveTemplateData3+=",";
                            }
                            if(this.empcontribform.grid.getStore().getAt(i).get("id") == -1){
                                calMsgBoxShow(28,0);
                                return;
                            }
                            //this.saveTemplateData1+="{WageId:'"+this.wageentryform.grid.getSelectionModel().getSelections()[i].get("id")+"',WageRate:'"+this.wageentryform.grid.getSelectionModel().getSelections()[i].get("cash")+"'}";
                            this.saveTemplateData3+="{Id:'"+this.empcontribform.grid.getStore().getAt(i).get("id")+"',ECRate:'"+this.empcontribform.grid.getStore().getAt(i).get("cash")+"'}";
                        }
                        this.saveTemplateData3+="]}";

                        var url="";
                        if(this.edit==1)
                            url="Payroll/Template/updateTemplateData.py";
                        else
                            url="Payroll/Template/setTemplateData.py"

                        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.common.want.to.save.changes"), function(btn){
                            if(btn=='yes'){
                                Wtf.Ajax.requestEx({
                                    url: url,
                                    method:'post',
                                    params:{
                                        save:'true',
                                        saveType:'templatedata',
                                        formdata:this.frmdta,
                                        taxdata:this.saveTemplateData,
                                        wagedata:this.saveTemplateData1,
                                        deducdata:this.saveTemplateData2,
                                        ecdata:this.saveTemplateData3,
                                        assignedEmployees:_assignedEmployees,
                                        torp:1,
                                        tempid:this.templateid,
                                        tempname:this.tempname.getValue(),
                                        templatename:this.templatename
                                    }
                                },this,
                                function(req){
                                    if(req.value=="success"){
                                        msgFlag=0;
                                        calMsgBoxShow(1,0);
                                        this.groupGridStore.reload();
                                        if(closeorno) {
                                            Wtf.getCmp('as').remove(this.id, true);
                                        }
                                    }
                                    else if(req.value=="Exist"){
                                        calMsgBoxShow(2,0);
                                    }
                                },
                                function(req){
                                    }
                                    );
                            }else{
                                return;
                            }
                        
                        },this);
                    }
                    else{
                        calMsgBoxShow(3,0);
                    }
                }
                else{
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.Pleaseinsertearnings")],0);
                }
            }
            else{
                calMsgBoxShow(5,0);
            }
        }
        this.wageentryform.storewage.on('load',function(){
            if(!Wtf.StoreMgr.containsKey("desig")){
                Wtf.desigStore.load();
                Wtf.StoreMgr.add("desig",Wtf.desigStore)
                Wtf.desigStore.on("load",function(){

                        this.onloadfunc();
                },this);
            }else{
                        this.onloadfunc();
            }
        },this)
    },
   onloadfunc : function(){
       if(this.editMode){
        this.k=0;
        for(this.k=0;this.k<Wtf.desigStore.getCount();this.k++){
            if(Wtf.desigStore.getAt(this.k).get('name')==this.group){
                new Wtf.form.Hidden({
                    id:'GlobalGroupId',
                    value:Wtf.desigStore.getAt(this.k).get('id'),
                    readOnly:true
                });
            }
        }
        this.designcombo.setValue(this.group);
        if(this.payinterval != ""){
            this.payintervalCombo.setValue(this.payinterval);
            this.payintervalCombo.fireEvent('change');
            if(this.payintervalCombo.getValue()==1)
                this.datepicker.setValue(this.effdate)
            else if(this.payintervalCombo.getValue()==3)
                this.daypicker.setValue(this.effdate)
            else
                this.datepicker.setValue(this.effdate)
        }
        
            this.assignedUsersPanel.setDesignationId(this.group);
            this.assignedUsersPanel.setpayinterval(this.payinterval);
            this.assignedUsersPanel.seteffdate(this.effdate);
       }
    },
    calcTotalAmount:function(rs,amountField,typeField, baseSalary){
        var total=0;
        for(var i=0;i<rs.length;i++){
            var rec=rs[i];
            var val=rec.data[amountField]*1;
            if(rec.data[typeField]=="1"){
                val=(baseSalary/100)*val;
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
                val=(baseSalary/100)*val;
            }
            total+=val;
        }
        return total;
    },
    convertToPercent:function(rs,amountField,typeField,baseSalary){
        var total=0;
        for(var i=0;i<rs.getCount();i++){
            var rec=rs.getAt(i);
            var val = 0;
           // var val=rec.data[amountField]*1;
            if(rec.data[typeField]=="0"){
                val=(rec.data[amountField]/baseSalary)*100;
                rec.data[amountField]=val;
            }
        }
    },

    addDesignation:function(){
        WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
    },
    setDesignation:function(){
        if(Wtf.desigStore.getCount()>0){
            this.designcombo.setValue(Wtf.desigStore.getAt(Wtf.desigStore.getCount()-1).get('id'));
        }
    },
    payintervalComboChange:function(){
    	if(this.payintervalCombo.getValue()==1){
            this.showhide(this.datepicker,true,"block");
            this.showhide(this.daypicker,false,"none");
            this.datepicker.setValue("");
            this.datestore.removeAll();
            this.datestore.loadData(this.monthlydata);
        }else if(this.payintervalCombo.getValue()==3){
            this.showhide(this.datepicker,false,"none");
            this.showhide(this.daypicker,true,"block");
        }else if(this.payintervalCombo.getValue()==2){
            this.showhide(this.datepicker,true,"block");
            this.showhide(this.daypicker,false,"none");
            this.datepicker.setValue("");
            this.datestore.removeAll();
            this.datestore.loadData(this.bimonthlydata);
        }
    }
});
