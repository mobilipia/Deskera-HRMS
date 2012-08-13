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
Wtf.getStatutoryFormAuthorizeRenderer = function(val){

        if(val!=null){

            if(val==0){
                return WtfGlobal.getLocaleText("hrms.recruitment.pending");
            }else if(val==1){
                return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.payroll.Authorized")+'</span>';
            } else if(val==2){
                return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.payroll.Unauthorized")+'</span>';
            }
        } else if(val==undefined){
            return WtfGlobal.getLocaleText("hrms.recruitment.pending");
        }
}

Wtf.Malaysian_StatutoryForm_AmanahSahamNasional = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.amanah.saham.nasional");
Wtf.Malaysian_StatutoryForm_TabungHaji = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.tabung.haji");
Wtf.Malaysian_StatutoryForm_CP21 = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.cukai.pendapatan21");
Wtf.Malaysian_StatutoryForm_HRDLevy = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.hrd.levy");
Wtf.Malaysian_StatutoryForm_CP39 = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.cp39");
Wtf.Malaysian_StatutoryForm_CP39A = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.cp39a");
Wtf.Malaysian_StatutoryForm_PCB2 = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.pcb2");
Wtf.Malaysian_StatutoryForm_TP1 = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.tp1");
Wtf.Malaysian_StatutoryForm_TP2 = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.tp2");
Wtf.Malaysian_StatutoryForm_TP3 = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.tp3");
Wtf.Malaysian_StatutoryForm_EA = WtfGlobal.getLocaleText("hrms.payroll.malaysian.statutory.ea");

Wtf.MalaysianPayrollUser = function (config){
    Wtf.apply(this,config);
    Wtf.MalaysianPayrollUser.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.MalaysianPayrollUser, Wtf.Panel,{

    initComponent:function (){
        Wtf.MalaysianPayrollUser.superclass.initComponent.call(this);
    },

    onRender: function(config){
    	this.empGrid();
        this.pan= new Wtf.Panel({
            id: "membergridpanel" + this.id,
            autoLoad: false,
            autoScroll:true,
            paging: false,
            layout: 'fit',
            items: [this.allempGrid]
        });

        this.add(this.pan);

    	Wtf.MalaysianPayrollUser.superclass.onRender.call(this, config);
    },

    empGrid:function(){
    	var btns=this.getTopToolbarButtons();
        var bbtns=this.getBottomToolbarButtons();
    	
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.record = Wtf.data.Record.create([
        {
            name: 'userid'
        },

        {
            name: 'username'
        },

        {
            name: 'designation'
        },

        {
            name: 'designationid'
        },

        {
            name: 'department'
        },

        {
            name: 'departmentname'
        },

        {
            name: 'firstname'
        },

        {
            name: 'lastname'
        },

        {
            name: 'image'
        },

        {
            name: 'emailid'
        },

        {
            name: 'lastlogin',
            type: 'date'
        },

        {
            name: 'aboutuser'
        },

        {
            name: 'address'
        },

        {
            name:'contactnumber'
        },

        {
            name:'manager'
        },
        {
            name:'managerid'
        },

        {
            name:'salary'
        },

        {
            name:'roleid',
            mapping:'role'
        },

        {
            name:'accno'
        },

        {
            name: 'rolename'
        },

        {
            name: 'employeeid'
        },

        {
            name:'status'
        },
        {
            name:'fullname'
        },
        {
            name:'reviewer'
        },
        {
            name:'reviewerid'
        },
        {
            name:'templateid'
        },{
            name:'frequency'
        },{
            name:'companyFormStatus'
        },{
            name:'cp21Status'
        },{
            name:'tp1Status'
        },{
            name:'tp2Status'
        },{
            name:'tp3Status'
        },{
            name:'pcb2Status'
        },{
            name:'eaStatus'
        }]);


        this.empGDS =new Wtf.data.Store({
        	reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.record),
            url:"Payroll/MalaysianStatutoryForm/getUserListForMalaysia.py",
            baseParams:{
                mode:114
            }
        });
        
        this.empGDS.on('beforeload',function(store,option){
            option.params= option.params||{};
            option.params.month=this.monthCmb.getValue();
            option.params.year=this.yearCmb.getValue();
        },this);
        
        calMsgBoxShow(202,4,true);
        this.empGDS.load({
        	params:{
        		start:0,
        		limit:15
        	}
        });
        this.empGDS.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);

        this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
                dataIndex: 'employeeid',
                autoWidth : true,
                pdfwidth:50,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.UserName"),
                dataIndex: 'username',
                pdfwidth:60,
                autoWidth : true,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname',
                autoWidth : true,
                pdfwidth:100,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'departmentname',
                autoWidth : true,
                pdfwidth:60,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                autoWidth : true,
                pdfwidth:60,
                sortable: true,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.EmailAddress"),
                dataIndex: 'emailid',
                autoSize : true,
                sortable: true,
                pdfwidth:100,
                renderer: WtfGlobal.renderEmailTo,
                groupable: true
            }
            ]);
        
        this.allempGrid = new Wtf.KwlGridPanel({
            border: false,
            id:this.id+'qualifiedgr',
            store: this.empGDS,
            cm: this.cm,
            sm: this.sm2,
            loadMask:true,
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.admin.grid.search"),
            searchField:"fullname",
            tbar:btns,
            bbar:bbtns,
            serverSideSearch:true,
            viewConfig: {
                forceFit: true
            }
        });

        this.sm2.on("selectionchange", function(){
        	var selCount = this.allempGrid.getSelectionModel().getCount();
            if(selCount==1){
                this.taxDeclaration.setDisabled(false);
                this.statutoryFomrDetails.setDisabled(false);
        	}else{
                this.taxDeclaration.setDisabled(true);
                this.statutoryFomrDetails.setDisabled(true);
            }
        }, this);

    },
    getBottomToolbarButtons :function(){
        var bbtns =[];

        this.authorizebtn=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.authorize"),
     		scope: this,
     		iconCls:'pwndHRMS authorizeComponentIcon',
     		handler:function(){
        		
                var sm = this.allempGrid.getSelectionModel();
                var emparr=sm.getSelections();
                if(sm.getCount()<1){
                   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.employee.authorize.unauthorize.data")],0);
                   return;
                }
                this.compWindow=new Wtf.ApproveReportsWin({
                    iconCls:getButtonIconCls(Wtf.btype.winicon),
                    layout:'fit',
                    closable:true,
                    width:600,
                    title:WtfGlobal.getLocaleText("hrms.common.authorize"),
                    height:500,
                    border:false,
                    modal:true,
                    id:'assign_authorize_window',
                    scope:this,
                    emparr:emparr,
                    grid:this.allempGrid,
                    month:this.monthCmb.getValue(),
                    year :this.yearCmb.getValue()
                });
                this.compWindow.show();
        	}
        });
        bbtns.push('-');
        bbtns.push(this.authorizebtn);

        this.authorizeViewbtn=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.view.authorize.status"),
     		scope: this,
     		iconCls:'pwndHRMS viewbuttonIcon',
     		handler:function(){

                var sm = this.allempGrid.getSelectionModel();
                var emparr=sm.getSelections();
                if(sm.getCount()<1){
                   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.employee.authorize.unauthorize.data")],0);
                   return;
                }

                this.empSelected =new Wtf.data.Store(this.allempGrid.getStore().initialConfig);
                this.empSelected.add(emparr);


                this.compStatusWindow=new Wtf.authorizeStatusWin({
                    iconCls:getButtonIconCls(Wtf.btype.winicon),
                    layout:'fit',
                    closable:true,
                    width:760,
                    title:WtfGlobal.getLocaleText("hrms.payroll.view.statutory.form.status"),
                    height:600,
                    border:false,
                    empGDS:this.empSelected,
                    modal:true,
                    scope:this,
                    plain:true,
                    allempGrid:this.allempGrid,
                    month:this.monthCmb.getValue(),
                    year :this.yearCmb.getValue()
                });
                this.compStatusWindow.show();
        	}
        });
        bbtns.push('-');
        bbtns.push(this.authorizeViewbtn);

        return bbtns;

    },
    getTopToolbarButtons :function(){

         var bbtns =[];

         this.resetbtn=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.reset"),
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.empGDS.load({
        			params:{
        				start:0,
        				limit:this.allempGrid.pag.pageSize
        			}
        	});
        	Wtf.getCmp("Quick"+this.allempGrid.id).setValue("");
        	}
        });
        bbtns.push('-');
        bbtns.push(this.resetbtn);


        this.taxDeclaration=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.employee.tax.declaration"),
     		scope: this,
            disabled:true,
     		iconCls:"pwndHRMS incometaxuserformIcon",
     		handler:function(){

                var recData = this.allempGrid.getSelectionModel().getSelected().data

                payrollData(Wtf.getCmp("payrollUserListtabpanel"), recData.userid, recData.fullname, this);
        	}
        });

        bbtns.push('-');
        bbtns.push(this.taxDeclaration);

        this.statutoryFomrDetails=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.employee.statutory.forms.details"),
     		scope: this,
            disabled:true,
     		iconCls:"pwndHRMS incometaxformIcon",
     		handler:function(){

                var recData = this.allempGrid.getSelectionModel().getSelected().data
        		payrollUserData(Wtf.getCmp("payrollUserListtabpanel"), recData.userid, recData.fullname, this);
        	}
        });

        bbtns.push('-');
        bbtns.push(this.statutoryFomrDetails);

        this.companyStatutoryFomrDetails=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.company.statutory.forms.details"),
     		scope: this,
           iconCls:"pwndHRMS incometaxformIcon",
     		handler:function(){

                    statutoryFormCompanyDetails(Wtf.getCmp("payrollUserListtabpanel"), this);
        	}
        });

        bbtns.push('-');
        bbtns.push(this.companyStatutoryFomrDetails);


        var menubtns=[];
        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_AmanahSahamNasional,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
	        	if(this.empGDS.data.items.length>0 && !this.empGDS.data.items[0].data.companyFormStatus){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
	            	return;
	            }
	        	var url = "Payroll/MalaysianStatutoryForm/getAmanahSahamNasional.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&year="+this.yearCmb.getValue()+"&month="+this.monthCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));

        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_TabungHaji,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
	        	if(this.empGDS.data.items.length>0 && !this.empGDS.data.items[0].data.companyFormStatus){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
	            	return;
	            }
                var url = "Payroll/MalaysianStatutoryForm/getTabungHaji.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&year="+this.yearCmb.getValue()+"&month="+this.monthCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));

        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_CP21,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
                var emparr=this.allempGrid.getSelectionModel().getSelections();
                if(emparr.length>1){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.single.record.generate.form")],0);
                    return;
                }
                if(emparr.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.to.generate.form")],0);
                    return;
                }
                if(emparr.length==1 && !emparr[0].data.companyFormStatus && !emparr[0].data.cp21Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.cp21.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.companyFormStatus){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.cp21Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.cp21.statutory.form.month")],0);
                	return;
                }
                var recData = this.allempGrid.getSelectionModel().getSelected().data;
                var url = "Payroll/MalaysianStatutoryForm/getCP21.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&userid="+recData.userid+"&year="+this.yearCmb.getValue()+"&month="+this.monthCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));

        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_TP1,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
                var emparr=this.allempGrid.getSelectionModel().getSelections();
                if(emparr.length>1){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.single.record.generate.form")],0);
                    return;
                }
                if(emparr.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.to.generate.form")],0);
                    return;
                }
                if(emparr.length==1 && !emparr[0].data.companyFormStatus && !emparr[0].data.tp1Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.tp1.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.companyFormStatus){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.tp1Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.tp1.statutory.form.month")],0);
                	return;
                }
                var recData = this.allempGrid.getSelectionModel().getSelected().data;
                var url = "Payroll/MalaysianStatutoryForm/getTP1.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&userid="+recData.userid+"&month="+this.monthCmb.getValue()+"&year="+this.yearCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));

        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_TP2,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
                var emparr=this.allempGrid.getSelectionModel().getSelections();
                if(emparr.length>1){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.single.record.generate.form")],0);
                    return;
                }
                if(emparr.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.to.generate.form")],0);
                    return;
                }
                if(emparr.length==1 && !emparr[0].data.companyFormStatus && !emparr[0].data.tp2Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.tp2.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.companyFormStatus){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.tp2Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.tp2.statutory.form.month")],0);
                	return;
                }
                var recData = this.allempGrid.getSelectionModel().getSelected().data;
                var url = "Payroll/MalaysianStatutoryForm/getTP2.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&userid="+recData.userid+"&month="+this.monthCmb.getValue()+"&year="+this.yearCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));


        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_TP3,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
                var emparr=this.allempGrid.getSelectionModel().getSelections();
                if(emparr.length>1){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.single.record.generate.form")],0);
                    return;
                }
                if(emparr.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.to.generate.form")],0);
                    return;
                }
                if(emparr.length==1 && !emparr[0].data.companyFormStatus && !emparr[0].data.tp3Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.tp3.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.companyFormStatus){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.tp3Status){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.tp3.statutory.form.month")],0);
                	return;
                }
                var recData = this.allempGrid.getSelectionModel().getSelected().data;
                var url = "Payroll/MalaysianStatutoryForm/getTP3.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&userid="+recData.userid+"&month="+this.monthCmb.getValue()+"&year="+this.yearCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));

        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_HRDLevy,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
	        	if(this.empGDS.data.items.length>0 && !this.empGDS.data.items[0].data.companyFormStatus){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
	            	return;
	            }
                var url = "Payroll/MalaysianStatutoryForm/getHRDLevy.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&year="+this.yearCmb.getValue()+"&month="+this.monthCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));


        menubtns.push(
        new Wtf.Action({
        	text:Wtf.Malaysian_StatutoryForm_CP39,
        	iconCls:"pwndCommon reportbuttonIcon",
        	scope:this,
        	handler : function(){
	        	if(this.empGDS.data.items.length>0 && !this.empGDS.data.items[0].data.companyFormStatus){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
	            	return;
	            }
	        	var url = "Payroll/MalaysianStatutoryForm/getCP39.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&frequency=0"+"&month="+this.monthCmb.getValue()+"&year="+this.yearCmb.getValue()+"&frequency=0"));
	        	Wtf.get('downloadframe').dom.src = url;
        	}
        }));

        menubtns.push(
        new Wtf.Action({
        	text:Wtf.Malaysian_StatutoryForm_CP39A,
        	iconCls:"pwndCommon reportbuttonIcon",
        	scope:this,
        	handler : function(){
        		if(this.empGDS.data.items.length>0 && !this.empGDS.data.items[0].data.companyFormStatus){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
	            	return;
	            }
	        	var url = "Payroll/MalaysianStatutoryForm/getCP39A.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&frequency=0"+"&month="+this.monthCmb.getValue()+"&year="+this.yearCmb.getValue()+"&frequency=0"));
	        	Wtf.get('downloadframe').dom.src = url;
        	}
        }));

        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_PCB2,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
	            var emparr=this.allempGrid.getSelectionModel().getSelections();
	            if(emparr.length>1){
	                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.single.record.generate.form")],0);
	                return;
	            }
	            if(emparr.length==0){
	                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.to.generate.form")],0);
	                return;
	            }
	            if(emparr.length==1 && !emparr[0].data.companyFormStatus && !emparr[0].data.pcb2Status){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.pcb.statutory.form.month")],0);
	            	return;
	            }else if(emparr.length==1 && !emparr[0].data.companyFormStatus){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
	            	return;
	            }else if(emparr.length==1 && !emparr[0].data.pcb2Status){
	            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.pcb.statutory.form.month")],0);
	            	return;
	            }
                var recData = this.allempGrid.getSelectionModel().getSelected().data;
                var url = "Payroll/MalaysianStatutoryForm/getPCB2PDF.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&frequency=0"+"&userid="+recData.userid+"&month="+this.monthCmb.getValue()+"&year="+this.yearCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));
        
        
        menubtns.push(
        new Wtf.Action({
            text:Wtf.Malaysian_StatutoryForm_EA,
            iconCls:"pwndCommon reportbuttonIcon",
            scope:this,
            handler : function(){
                var emparr=this.allempGrid.getSelectionModel().getSelections();
                if(emparr.length>1){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.single.record.generate.form")],0);
                    return;
                }
                if(emparr.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.to.generate.form")],0);
                    return;
                }
                if(emparr.length==1 && !emparr[0].data.companyFormStatus && !emparr[0].data.eaStatus){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.ea.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.companyFormStatus){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.company.statutory.form.month")],0);
                	return;
                }else if(emparr.length==1 && !emparr[0].data.eaStatus){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.fill.ea.statutory.form.month")],0);
                	return;
                }
                var recData = this.allempGrid.getSelectionModel().getSelected().data;
                var url = "Payroll/MalaysianStatutoryForm/getEA.py?"+Wtf.urlEncode(Wtf.urlDecode("companyname="+companyName+"&userid="+recData.userid+"&month="+this.monthCmb.getValue()+"&year="+this.yearCmb.getValue()+"&frequency=0"));
                Wtf.get('downloadframe').dom.src = url;
            }
        }));

        this.report=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.statutory.forms"),
     		scope: this,
            iconCls:"pwndCommon reportbuttonIcon",
     		menu:menubtns
        });

        bbtns.push('-');
        bbtns.push(this.report);

        this.bankFile=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.mtd.text.file"),
     		scope: this,
           iconCls:"pwndHRMS textFileIcon",
     		handler:function(){
                    var url = "Payroll/BankInterfaceFile/getMTDDataPayrollReport.py?"+Wtf.urlEncode(Wtf.urlDecode("year="+this.yearCmb.getValue()+"&month="+this.monthCmb.getValue()+"&frequency=0"));
                    Wtf.get('downloadframe').dom.src = url;
        	}
        });

        bbtns.push('-');
        bbtns.push(this.bankFile);
        
        this.monthCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Month"),
            hiddenName: 'month',
            forceSelection:true,
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.monthStore0,
            width:80,
            typeAhead:true,
            value:new Date().getMonth()
        });
        
        this.monthCmb.on("select", function(){
        	this.empGDS.reload();
        }, this);

        bbtns.push('-');
        bbtns.push(this.monthCmb);

        this.yearCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Year"),
            hiddenName: 'year',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:Wtf.yearStore,
            width:80,
            typeAhead:true,
            value:new Date().getFullYear()
        });
        
        this.yearCmb.on("select", function(){
        	this.empGDS.reload();
        }, this);

        bbtns.push('-');
        bbtns.push(this.yearCmb);

        return bbtns;
    }

});

Wtf.MalaysianUserIncomeTax = function (config){
    Wtf.apply(this,config);
    Wtf.MalaysianUserIncomeTax.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.MalaysianUserIncomeTax, Wtf.Panel,{
	layout:'fit',
	closable:false,

    initComponent:function (){
        Wtf.MalaysianUserIncomeTax.superclass.initComponent.call(this);
    },

    onRender: function(config){

        var tbar = this.getTopToolbar();

        var amanahSahamNasional = this.getAmanahSahamNasional();

        var tabungHaji = this.getTabungHaji();

        var cp21 = this.getCP21();

        var hRDLevy = this.getHRDLevy();

        var pcb2 = this.getPCB2();

    	var p = new Wtf.Panel({
    	    layout:'column',
    	    bodyBorder:false,
    	    border:false,
    	    items: [{
    	        columnWidth:.32,
    	        bodyBorder:false,
        	    border:false,
                layout:'form',
                style:"padding-top:10px; padding-rigth:1px; padding-bottom:1px; padding-left:10px;",
    	        items:[amanahSahamNasional,cp21]
    	    },{
    	        columnWidth:.32,
    	        layout:'form',
                style:"padding-top:10px; padding-rigth:10px; padding-bottom:10px; padding-left:10px;",
    	        bodyBorder:false,
        	    border:false,
                items:[tabungHaji,pcb2,this.getTP1(),this.getTP2(),this.getTP3()]

    	    },{
    	        columnWidth:.33,
    	        bodyBorder:false,
                layout:'form',
                style:"padding-top:10px; padding-rigth:10px; padding-bottom:10px; padding-left:10px;",
        	    border:false,
                items:[hRDLevy, this.getCP39(), this.getCP39A(), this.getEA()]

    	    }]
    	});

    	this.formPanel= new Wtf.form.FormPanel({
    		autoWidth:true,
			autoScroll:true,
			baseParams:{
				userid:this.userid,
				year:this.year
            },
			url:"Payroll/MalaysianStatutoryForm/saveUserFormInformation.py",
			border:true,
			bodyBorder:false,
			id:'formPanel',
			items:[p]
		});
    	this.mainPanel = new Wtf.Panel({
    		layout:"fit",
    		border:false,
            tbar:tbar,
            scope:this,
    		items:[this.formPanel],
    		bbar:['->',{
    	     	id: 'Submit',
    	     	iconCls:getButtonIconCls(Wtf.btype.submitbutton),
    	        text:WtfGlobal.getLocaleText("hrms.common.submit"),
    	        handler: this.submit.createDelegate(this)
    	    }]
    	});

    	this.add(this.mainPanel);

        this.decMonthCmb.on("select", function(a,b,index){
            this.reloadData();
        },this);

        this.decYearCmb.on("select", function(a,b,index){
            this.reloadData();
        },this);

    	Wtf.MalaysianUserIncomeTax.superclass.onRender.call(this, config);
    },

    reloadData :function(){

            calMsgBoxShow(202,4,true);
            Wtf.Ajax.requestEx({
                url: "Payroll/MalaysianStatutoryForm/getUserStatutoryFormInformation.py",
                params: {
                    year:this.year,
                    userID:this.userid,
                    declarationMonth:this.decMonthCmb.getValue(),
                    declarationYear:this.decYearCmb.getValue()
                }
            }, this,
            function(response){
                var userdata = eval('('+response+')');

                this.setAmanahSahamNasional(userdata.data.userdata);
                this.setTabungHaji(userdata.data.userdata);
                this.setCP21(userdata.data.userdata);
                this.setHRDLevy(userdata.data.userdata);
                this.setPCB2(userdata.data.userdata);
                this.setTP1(userdata.data.userdata);
                this.setTP2(userdata.data.userdata);
                this.setTP3(userdata.data.userdata);
                this.setCP39(userdata.data.userdata);
                this.setCP39A(userdata.data.userdata);
                this.setEA(userdata.data.userdata);

                WtfGlobal.closeProgressbar();
            },
            function(response){

            });
    },

    submit:function(){
    	if(this.formPanel.form.isValid()){
    		this.formPanel.getForm().submit({
                params:{
                    declarationMonth : this.decMonthCmb.getValue(),
                    declarationYear : this.decYearCmb.getValue()
                },
                waitTitle:WtfGlobal.getLocaleText("hrms.common.PleaseWait"),
                waitMsg:WtfGlobal.getLocaleText("hrms.common.saving.information"),
                success:function(){
    				Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.user.information.saved.successfully"));
                    this.ownerCt.remove(this);
                    if(this.statutoryForm!=undefined){
                    	this.statutoryForm.empGDS.reload();
                    }
                },
                failure:function(f,a){
                	Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.saving.user.information"));
                },
                scope:this
            });
    	}else{

    	}
    },
    getTopToolbar : function(){

       var bbtns = [];
       this.decMonthCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Month"),
            hiddenName: 'month',
            forceSelection:true,
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.monthStore0,
            width:80,
            typeAhead:true,
            value:this.statutoryForm!=undefined?this.statutoryForm.monthCmb.getValue():new Date().getMonth()
        });

        bbtns.push('-');
        bbtns.push(WtfGlobal.getLocaleText("hrms.payroll.select.month")+': ');
        bbtns.push(this.decMonthCmb);

        this.decYearCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Year"),
            hiddenName: 'year',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:Wtf.yearStore,
            width:80,
            typeAhead:true,
            value:this.statutoryForm!=undefined?this.statutoryForm.yearCmb.getValue():new Date().getFullYear()
        });

        bbtns.push('-');
        bbtns.push(WtfGlobal.getLocaleText("hrms.payroll.select.year")+': ');
        bbtns.push(this.decYearCmb);

        return bbtns;

    },
    getFieldSet : function(arr, title, status){

        var fieldSet = new Wtf.form.FieldSet({
            title:""+title+" ("+Wtf.getStatutoryFormAuthorizeRenderer(status)+")",
            cls:status==1?"formFieldSet disableFieldSet":"formFieldSet",
            width:'90%',
            autoHeight:true,
            disabled:status==1?true:false,
            items:arr
        });

        return fieldSet;

    },
    getAmanahSahamNasional : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.asnauthorize:0;

        this.amnid=new Wtf.form.Hidden({
            name:'amnid',
            id:'amnid',
            value:this.userdata!=undefined?this.userdata.asnid:""
        });
        arr.push(this.amnid);

        this.amnStatus=new Wtf.form.Hidden({
            name:'asnauthorize',
            id:'asnauthorize',
            value:status
        });
        arr.push(this.amnStatus);

        this.amnicNumber = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.ic.no"),
            scope:this,
            name:"asnicno",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.asnicno:""
        });
        arr.push(this.amnicNumber);

        this.amnaccno = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.AccountNumber"),
            scope:this,
            maxLength:10,
            name:"asnaccno",
            value:this.userdata!=undefined?this.userdata.asnaccno:""
        });
        arr.push(this.amnaccno);

        this.amnamount = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.Amount"),
            scope:this,
            maxLength:10,
            name:"asnamount",
            value:this.userdata!=undefined?this.userdata.asnamount:0
        });
        arr.push(this.amnamount);

        this.compASN = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_AmanahSahamNasional, status )

        return this.compASN;

    },

    setAmanahSahamNasional : function(data){

        if(data!=undefined){

            this.amnid.setValue(data.asnid);
            this.amnStatus.setValue(data.asnauthorize);
            this.amnicNumber.setValue(data.asnicno);
            this.amnaccno.setValue(data.asnaccno);
            this.amnamount.setValue(data.asnamount);

            this.compASN.setTitle(Wtf.Malaysian_StatutoryForm_AmanahSahamNasional+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.asnauthorize)+")");

            if(data.asnauthorize==1){
                this.compASN.disable();
            }else {
                this.compASN.enable();
            }
        }

    },

    setTabungHaji : function(data){

        if(data!=undefined){

            this.tabunghajiid.setValue(data.tabunghajiid);
            this.tabunghajiStatus.setValue(data.tabunghajiauthorize);
            this.tabunghajiicno.setValue(data.tabunghajiicno);
            this.tabunghajiaccno.setValue(data.tabunghajiaccno);
            this.tabunghajiamount.setValue(data.tabunghajiamount);

            this.compTabungHaji.setTitle(Wtf.Malaysian_StatutoryForm_TabungHaji+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.tabunghajiauthorize)+")");
            
            if(data.tabunghajiauthorize==1){
                this.compTabungHaji.disable();
            }else {
                this.compTabungHaji.enable();
            }
                
        }

    },


    getTabungHaji : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.tabunghajiauthorize:0;
        
        this.tabunghajiid=new Wtf.form.Hidden({
            name:'tabunghajiid',
            id:'tabunghajiid',
            value:this.userdata!=undefined?this.userdata.tabunghajiid:""
        });
        arr.push(this.tabunghajiid);

        this.tabunghajiStatus=new Wtf.form.Hidden({
            name:'tabunghajiauthorize',
            id:'tabunghajiauthorize',
            value:status
        });
        arr.push(this.tabunghajiStatus);

        this.tabunghajiicno = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.ic.no"),
            scope:this,
            maxLength:10,
            name:"tabunghajiicno",
            value:this.userdata!=undefined?this.userdata.tabunghajiicno:""
        });
        arr.push(this.tabunghajiicno);

        this.tabunghajiaccno = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.AccountNumber"),
            scope:this,
            maxLength:10,
            name:"tabunghajiaccno",
            value:this.userdata!=undefined?this.userdata.tabunghajiaccno:""
        });
        arr.push(this.tabunghajiaccno);

        this.tabunghajiamount = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.Amount"),
            scope:this,
            maxLength:10,
            name:"tabunghajiamount",
            value:this.userdata!=undefined?this.userdata.tabunghajiamount:""
        });
        arr.push(this.tabunghajiamount);

        this.compTabungHaji = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_TabungHaji, status )

        return this.compTabungHaji;

    },

    setCP21 : function(data){

        if(data!=undefined){

            this.CP21amnid.setValue(data.cp21id);
            this.CP21Status.setValue(data.cp21authorize);
            this.CP21empfilerefno.setValue(data.empfilerefno);
            this.CP21expectedDateToLeave.setValue(data.datetoleave);
            this.CP21pasportno.setValue(data.passportno);
            this.CP21natureOfEmploymentCombo.setValue(data.natureofemployment);

            this.CP21departureReason.setValue(data.departurereason);
            this.CP21correspondenceAddress.setValue(data.correspondenceaddress);
            this.CP21dateofReturn.setValue(data.dateofreturn);
            this.CP21amount.setValue(data.dueamount);
            this.CP21dateofform.setValue(data.dateofform);

            this.CP21salaryFrom.setValue(data.salaryfrom);
            this.CP21salaryTo.setValue(data.salaryto);
            this.CP21salaryamount.setValue(data.salaryamount);
            this.CP21leavePayFrom.setValue(data.leavepayfrom);
            this.CP21leavePayTo.setValue(data.leavepayto);

            this.CP21leavepayamount.setValue(data.leavepayamount);
            this.CP21bonusFrom.setValue(data.bonusfrom);
            this.CP21bonusTo.setValue(data.bonusto);
            this.CP21bonusamount.setValue(data.bonusamount);
            this.CP21gratuityFrom.setValue(data.gratuityfrom);

            this.CP21gratuityTo.setValue(data.gratuityto);
            this.CP21gratuityamount.setValue(data.gratuityamount);
            this.CP21allowanceFrom.setValue(data.allowancefrom);
            this.CP21allowanceTo.setValue(data.allowanceto);
            this.CP21allowanceamount.setValue(data.allowanceamount);

            this.CP21pensionFrom.setValue(data.pensionfrom);
            this.CP21pensionTo.setValue(data.pensionto);
            this.CP21pensionamount.setValue(data.pensionamount);
            this.CP21residenceFrom.setValue(data.residencefrom);
            this.CP21residenceTo.setValue(data.residenceto);

            this.CP21residenceamount.setValue(data.residenceamount);
            this.CP21allowanceinkindFrom.setValue(data.allowanceinkindfrom);
            this.CP21allowanceinkindTo.setValue(data.allowanceinkindto);
            this.CP21allowanceinkindamount.setValue(data.allowanceinkindamount);
            this.CP21pfFrom.setValue(data.pffrom);

            this.CP21pfTo.setValue(data.pfto);
            this.CP21pfamount.setValue(data.pfamount);
            this.CP21natureOfPaymentCombo.setValue(data.natureofpayment);
            this.CP21dateofPayment.setValue(data.dateofpayment);
            this.CP21amounttobepaid.setValue(data.amounttobepaid);

            this.compCP21.setTitle(Wtf.Malaysian_StatutoryForm_CP21+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.cp21authorize)+")");

            if(data.cp21authorize==1){
                this.compCP21.disable();
            }else {
                this.compCP21.enable();
            }

        }

    },
    getCP21 : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.cp21authorize:0;
        this.CP21amnid=new Wtf.form.Hidden({
            name:'cp21id',
            id:'cp21id',
            value:this.userdata!=undefined?this.userdata.cp21id:""
        });
        arr.push(this.CP21amnid);

        this.CP21Status=new Wtf.form.Hidden({
            name:'cp21authorize',
            id:'cp21authorize',
            value:status
        });
        arr.push(this.CP21Status);

        this.CP21empfilerefno = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.employee.file.reference.no"),
            scope:this,
            width:200,
            maxLength:50,
            name:"empfilerefno",
            value:this.userdata!=undefined?this.userdata.empfilerefno:""
        });
        arr.push(this.CP21empfilerefno);

        this.CP21expectedDateToLeave = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.expected.date.to.leave"),
            format:"Y-m-d",
            name:'datetoleave',
            width:200,
            value:this.userdata!=undefined?this.userdata.datetoleave:""
        });
        arr.push(this.CP21expectedDateToLeave);

        this.CP21pasportno = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.identity.card.passport.no"),
            scope:this,
            width:200,
            maxLength:50,
            name:"passportno",
            value:this.userdata!=undefined?this.userdata.passportno:""
        });
        arr.push(this.CP21pasportno);

        this.natureOfEmployment = new Wtf.data.SimpleStore({
    		fields: ['id','name'],
    		data: [["0", WtfGlobal.getLocaleText("hrms.common.none")],
                   ["1", WtfGlobal.getLocaleText("hrms.common.Permanent")],
    	           ["2", WtfGlobal.getLocaleText("hrms.common.ad.hoc")]
    	          ]
    	});

        this.CP21natureOfEmploymentCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.nature.of.employment"),
            hiddenName: 'natureofemployment',
            name:'natureofemployment',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:this.natureOfEmployment,
            width:200,
            typeAhead:true,
            value:this.userdata!=undefined?this.userdata.natureofemployment:""

        });

    	arr.push(this.CP21natureOfEmploymentCombo);

        this.CP21departureReason = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.reason.for.departure"),
            scope:this,
            maxLength:50,
            width:200,
            name:"departurereason",
            value:this.userdata!=undefined?this.userdata.departurereason:""
        });
        arr.push(this.CP21departureReason);

        this.CP21correspondenceAddress = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.correspondence.address"),
            scope:this,
            maxLength:50,
            width:200,
            name:"correspondenceaddress",
            value:this.userdata!=undefined?this.userdata.correspondenceaddress:""
        });
        arr.push(this.CP21correspondenceAddress);

        this.CP21dateofReturn = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.probable.date.return"),
            format:"Y-m-d",
            name:'dateofreturn',
            width:200,
            value:this.userdata!=undefined?this.userdata.dateofreturn:""
        });
        arr.push(this.CP21dateofReturn);

        this.CP21amount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.amount.due.to.employee"),
            scope:this,
            maxLength:10,
            name:"dueamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.dueamount:""
        });
        arr.push(this.CP21amount);

        this.CP21dateofform = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.Date"),
            format:"Y-m-d",
            name:'dateofform',
            width:200,
            value:this.userdata!=undefined?this.userdata.dateofform:""
        });
        arr.push(this.CP21dateofform);

        var pfarr = [];

        this.CP21salaryFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.fees.wages.overtime.payfrom"),
            format:"Y-m-d",
            name:'salaryfrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.salaryfrom:""
        });
        pfarr.push(this.CP21salaryFrom);

        this.CP21salaryTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.fees.wages.overtime.payto"),
            format:"Y-m-d",
            name:'salaryto',
            width:200,
            value:this.userdata!=undefined?this.userdata.salaryto:""
        });
        pfarr.push(this.CP21salaryTo);

       this.CP21salaryamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.fees.wages.overtime.pay.amount"),
            scope:this,
            maxLength:10,
            name:"salaryamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.salaryamount:""
        });
        pfarr.push(this.CP21salaryamount);

        this.CP21leavePayFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.leave.payfrom"),
            format:"Y-m-d",
            name:'leavepayfrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.leavepayfrom:""
        });
        pfarr.push(this.CP21leavePayFrom);

        this.CP21leavePayTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.leave.pay.to"),
            format:"Y-m-d",
            name:'leavepayto',
            width:200,
            value:this.userdata!=undefined?this.userdata.leavepayto:""
        });
        pfarr.push(this.CP21leavePayTo);

        this.CP21leavepayamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.leave.pay.amount"),
            scope:this,
            maxLength:10,
            name:"leavepayamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.leavepayamount:""
        });
        pfarr.push(this.CP21leavepayamount);


        this.CP21bonusFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.commission.bonus.from"),
            format:"Y-m-d",
            name:'bonusfrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.bonusfrom:""
        });
        pfarr.push(this.CP21bonusFrom);

        this.CP21bonusTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.commission.bonus.to"),
            format:"Y-m-d",
            name:'bonusto',
            width:200,
            value:this.userdata!=undefined?this.userdata.bonusto:""
        });
        pfarr.push(this.CP21bonusTo);

        this.CP21bonusamount = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.commission.bonus.amount"),
            scope:this,
            maxLength:10,
            name:"bonusamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.bonusamount:""
        });
        pfarr.push(this.CP21bonusamount);

        this.CP21gratuityFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.gratuity.from"),
            format:"Y-m-d",
            name:'gratuityfrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.gratuityfrom:""
        });
        pfarr.push(this.CP21gratuityFrom);

        this.CP21gratuityTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.gratuity.to"),
            format:"Y-m-d",
            name:'gratuityto',
            width:200,
            value:this.userdata!=undefined?this.userdata.gratuityto:""
        });
        pfarr.push(this.CP21gratuityTo);

        this.CP21gratuityamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.gratuity.amount"),
            scope:this,
            maxLength:10,
            name:"gratuityamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.gratuityamount:""
        });
        pfarr.push(this.CP21gratuityamount);

        this.CP21allowanceFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.allowance.from"),
            format:"Y-m-d",
            name:'allowancefrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.allowancefrom:""
        });
        pfarr.push(this.CP21allowanceFrom);

        this.CP21allowanceTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.allowance.to"),
            format:"Y-m-d",
            name:'allowanceto',
            width:200,
            value:this.userdata!=undefined?this.userdata.allowanceto:""
        });
        pfarr.push(this.CP21allowanceTo);

        this.CP21allowanceamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.allowance.amount"),
            scope:this,
            maxLength:10,
            name:"allowanceamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.allowanceamount:""
        });
        pfarr.push(this.CP21allowanceamount);

        this.CP21pensionFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.pension.from"),
            format:"Y-m-d",
            name:'pensionfrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.pensionfrom:""
        });
        pfarr.push(this.CP21pensionFrom);

        this.CP21pensionTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.pension.to"),
            format:"Y-m-d",
            name:'pensionto',
            width:200,
            value:this.userdata!=undefined?this.userdata.pensionto:""
        });
        pfarr.push(this.CP21pensionTo);

        this.CP21pensionamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.pension.amount"),
            scope:this,
            maxLength:10,
            name:"pensionamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.pensionamount:""
        });
        pfarr.push(this.CP21pensionamount);

        this.CP21residenceFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.residence.provided.by.employer.from"),
            format:"Y-m-d",
            name:'residencefrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.residencefrom:""
        });
        pfarr.push(this.CP21residenceFrom);

        this.CP21residenceTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.residence.provided.by.employer.to"),
            format:"Y-m-d",
            name:'residenceto',
            width:200,
            value:this.userdata!=undefined?this.userdata.residenceto:""
        });
        pfarr.push(this.CP21residenceTo);

        this.CP21residenceamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.residence.provided.by.employer.amount"),
            scope:this,
            maxLength:10,
            name:"residenceamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.residenceamount:""
        });
        pfarr.push(this.CP21residenceamount);

        this.CP21allowanceinkindFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.allowance.kind.from"),
            format:"Y-m-d",
            name:'allowanceinkindfrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.allowanceinkindfrom:""
        });
        pfarr.push(this.CP21allowanceinkindFrom);

        this.CP21allowanceinkindTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.allowance.kind.to"),
            format:"Y-m-d",
            name:'allowanceinkindto',
            width:200,
            value:this.userdata!=undefined?this.userdata.allowanceinkindto:""
        });
        pfarr.push(this.CP21allowanceinkindTo);

        this.CP21allowanceinkindamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.allowance.kind.amount"),
            scope:this,
            maxLength:10,
            name:"allowanceinkindamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.allowanceinkindamount:""
        });
        pfarr.push(this.CP21allowanceinkindamount);

        this.CP21pfFrom = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.provident.fund.from"),
            format:"Y-m-d",
            name:'pffrom',
            width:200,
            value:this.userdata!=undefined?this.userdata.pffrom:""
        });
        pfarr.push(this.CP21pfFrom);

        this.CP21pfTo = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.provident.fund.to"),
            format:"Y-m-d",
            name:'pfto',
            width:200,
            value:this.userdata!=undefined?this.userdata.pfto:""
        });
        pfarr.push(this.CP21pfTo);

        this.CP21pfamount = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.provident.fund.amount"),
            scope:this,
            maxLength:10,
            name:"pfamount",
            width:200,
            value:this.userdata!=undefined?this.userdata.pfamount:""
        });
        pfarr.push(this.CP21pfamount);


        var pfdata = new Wtf.form.FieldSet({
            title:WtfGlobal.getLocaleText("hrms.payroll.emoluments.approved.provident.fund"),
            style:"padding-left:20px; padding-top:10px; align:center;",
            width:'90%',
            autoHeight:true,
            items:pfarr
        });
        arr.push(pfdata);

        var otherarr = [];
        this.natureOfPayment = new Wtf.data.SimpleStore({
    		fields: ['id','name'],
    		data: [["0", WtfGlobal.getLocaleText("hrms.common.none")],
                   ["1", WtfGlobal.getLocaleText("hrms.payroll.cash")],
    	           ["2", WtfGlobal.getLocaleText("hrms.payroll.cheque")]
    	          ]
    	});

        this.CP21natureOfPaymentCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.nature.of.payment"),
            hiddenName: 'natureofpayment',
            name:'natureofpayment',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:this.natureOfPayment,
            width:200,
            typeAhead:true,
            value:this.userdata!=undefined?this.userdata.natureofpayment:""

        });

    	otherarr.push(this.CP21natureOfPaymentCombo);

        this.CP21dateofPayment = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.date.of.payment"),
            format:"Y-m-d",
            name:'dateofpayment',
            width:200,
            value:this.userdata!=undefined?this.userdata.dateofpayment:""
        });
        otherarr.push(this.CP21dateofPayment);

        this.CP21amounttobepaid = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.amount.to.be.paid"),
            scope:this,
            maxLength:10,
            name:"amounttobepaid",
            width:200,
            value:this.userdata!=undefined?this.userdata.amounttobepaid:""
        });
        otherarr.push(this.CP21amounttobepaid);


        this.CP21otheramount = new Wtf.form.FieldSet({
            title:WtfGlobal.getLocaleText("hrms.payroll.other.amount.due.to.employee"),
            style:"padding-left:20px; padding-top:10px; align:center;",
            width:'90%',
            autoHeight:true,
            items:otherarr
        });
        arr.push(this.CP21otheramount);

        this.compCP21 = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_CP21, status )
        
        return this.compCP21;

    },

    getHRDLevy : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.HRDLevyauthorize:0;
        this.HRDLevyid=new Wtf.form.Hidden({
            name:'HRDLevyid',
            id:'HRDLevyid',
            value:this.userdata!=undefined?this.userdata.HRDLevyid:""
        });
        arr.push(this.HRDLevyid);

        this.HRDLevyStatus=new Wtf.form.Hidden({
            name:'HRDLevyauthorize',
            id:'HRDLevyauthorize',
            value:status
        });
        arr.push(this.HRDLevyStatus);

        this.HRDbasesalary = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.base.salary"),
            scope:this,
            name:"HRDLevybasesalary",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.HRDLevybasesalary:""
        });
        arr.push(this.HRDbasesalary);

        this.HRDothers = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.others"),
            scope:this,
            name:"HRDLevyothers",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.HRDLevyothers:""
        });
        arr.push(this.HRDothers);

        this.HRDnetsalary = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.netsalary"),
            scope:this,
            maxLength:10,
            name:"HRDLevynetsalary",
            value:this.userdata!=undefined?this.userdata.HRDLevynetsalary:""
        });
        arr.push(this.HRDnetsalary);

        this.HRDhrdlevy = new Wtf.form.NumberField({
            fieldLabel: Wtf.Malaysian_StatutoryForm_HRDLevy,
            scope:this,
            maxLength:10,
            name:"HRDLevyhrdlevy",
            value:this.userdata!=undefined?this.userdata.HRDLevyhrdlevy:""
        });
        arr.push(this.HRDhrdlevy);

        this.compHRDLevy = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_HRDLevy, status )
        
        return this.compHRDLevy;

    },

    setHRDLevy : function(data){

        if(data!=undefined){

            this.HRDLevyid.setValue(data.HRDLevyid);
            this.HRDLevyStatus.setValue(data.HRDLevyauthorize);
            this.HRDbasesalary.setValue(data.HRDLevybasesalary);
            this.HRDothers.setValue(data.HRDLevyothers);
            this.HRDnetsalary.setValue(data.HRDLevynetsalary);
            this.HRDhrdlevy.setValue(data.HRDLevyhrdlevy);

            this.compHRDLevy.setTitle(Wtf.Malaysian_StatutoryForm_HRDLevy+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.HRDLevyauthorize)+")");

            if(data.HRDLevyauthorize==1){
                this.compHRDLevy.disable();
            }else {
                this.compHRDLevy.enable();
            }
        }

    },

    setPCB2 : function(data){

        if(data!=undefined){

            this.idForPCB2.setValue(data.idForPCB2);
            this.PCB2Status.setValue(data.pcb2authorize);
            this.deductionAmountCP38ForPCB2.setValue(data.deductionAmountCP38ForPCB2);
            this.taxResitPCBForPCB2.setValue(data.taxResitPCBForPCB2);
            this.taxResitPCBDateForPCB2.setValue(data.taxResitPCBDateForPCB2);

            this.taxResitCP38ForPCB2.setValue(data.taxResitCP38ForPCB2);
            this.taxResitCP38DateForPCB2.setValue(data.taxResitCP38DateForPCB2);
            this.newIdentificationNumberForPCB2.setValue(data.newIdentificationNumberForPCB2);
            this.incomeTaxFileNumberForPCB2.setValue(data.incomeTaxFileNumberForPCB2);

            this.compPCB2.setTitle(Wtf.Malaysian_StatutoryForm_PCB2+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.pcb2authorize)+")");

            if(data.pcb2authorize==1){
                this.compPCB2.disable();
            }else {
                this.compPCB2.enable();
            }
        }

    },
    getPCB2 : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.pcb2authorize:0;

        this.idForPCB2=new Wtf.form.Hidden({
            name:'idForPCB2',
            value:this.userdata!=undefined?this.userdata.idForPCB2:""
        });
        arr.push(this.idForPCB2);

        this.PCB2Status=new Wtf.form.Hidden({
            name:'pcb2authorize',
            id:'pcb2authorize',
            value:status
        });
        arr.push(this.PCB2Status);

        this.deductionAmountCP38ForPCB2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.cp38.deduction.amount"),
            scope:this,
            maxLength:10,
            width:150,
            name:"deductionAmountCP38ForPCB2",
            value:this.userdata!=undefined?this.userdata.deductionAmountCP38ForPCB2:""
        });
        arr.push(this.deductionAmountCP38ForPCB2);

        this.taxResitPCBForPCB2 = new Wtf.form.TextField({
            fieldLabel :WtfGlobal.getLocaleText("hrms.payroll.pcb.tax.receipt.no"),
            scope : this,
            maxLength:50,
            width:150,
            name : "taxResitPCBForPCB2",
            value : this.userdata != undefined ? this.userdata.taxResitPCBForPCB2: ""
        });
        arr.push(this.taxResitPCBForPCB2);

        this.taxResitPCBDateForPCB2 = new Wtf.form.DateField({
            fieldLabel :WtfGlobal.getLocaleText("hrms.payroll.pcb.tax.receipt.date"),
            scope : this,
            width:150,
            format:"Y-m-d",
            name : "taxResitPCBDateForPCB2",
            value : this.userdata != undefined ? this.userdata.taxResitPCBDateForPCB2: ""
        });
        arr.push(this.taxResitPCBDateForPCB2);

        this.taxResitCP38ForPCB2 = new Wtf.form.TextField({
            fieldLabel :WtfGlobal.getLocaleText("hrms.payroll.cp38.tax.receipt.no"),
            scope : this,
            maxLength:50,
            width:150,
            name : "taxResitCP38ForPCB2",
            value : this.userdata != undefined ? this.userdata.taxResitCP38ForPCB2: ""
        });
        arr.push(this.taxResitCP38ForPCB2);

        this.taxResitCP38DateForPCB2 = new Wtf.form.DateField({
            fieldLabel :WtfGlobal.getLocaleText("hrms.payroll.cp38.tax.receipt.date"),
            scope : this,
            format:"Y-m-d",
            width:150,
            name : "taxResitCP38DateForPCB2",
            value : this.userdata != undefined ? this.userdata.taxResitCP38DateForPCB2: ""
        });
        arr.push(this.taxResitCP38DateForPCB2);

        this.newIdentificationNumberForPCB2 = new Wtf.form.TextField({
            fieldLabel :WtfGlobal.getLocaleText("hrms.payroll.new.identification.number"),
            scope : this,
            maxLength:50,
            width:150,
            name : "newIdentificationNumberForPCB2",
            value : this.userdata != undefined ? this.userdata.newIdentificationNumberForPCB2: ""
        });
        arr.push(this.newIdentificationNumberForPCB2);


        this.incomeTaxFileNumberForPCB2 = new Wtf.form.TextField({
            fieldLabel : WtfGlobal.getLocaleText("hrms.payroll.incometax.file.number"),
            scope : this,
            maxLength:50,
            width:150,
            name : "incomeTaxFileNumberForPCB2",
            value : this.userdata != undefined ? this.userdata.incomeTaxFileNumberForPCB2: ""
        });
        arr.push(this.incomeTaxFileNumberForPCB2);

        this.compPCB2 = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_PCB2, status )
        
        return this.compPCB2;

    },
    setTP2: function(data){

        if(data!=undefined){

            this.idForTP2.setValue(data.idForTP2);
            this.TP2Status.setValue(data.tp2authorize);
            this.oldIdentificationNumberForTP2.setValue(data.oldIdentificationNumberForTP2);
            this.newIdentificationNumberForTP2.setValue(data.newIdentificationNumberForTP2);
            this.passportNumberForTP2.setValue(data.passportNumberForTP2);
            this.armyOrPoliceNumberForTP2.setValue(data.armyOrPoliceNumberForTP2);

            this.incomeTaxLHDNNumberForTP2.setValue(data.incomeTaxLHDNNumberForTP2);
            this.carForTP2.setValue(data.carForTP2);
            this.driverForTP2.setValue(data.driverForTP2);
            this.householdItemsForTP2.setValue(data.householdItemsForTP2);
            this.entertainmentForTP2.setValue(data.entertainmentForTP2);

            this.gardenerForTP2.setValue(data.gardenerForTP2);
            this.maidForTP2.setValue(data.maidForTP2);
            this.holidayAllowanceForTP2.setValue(data.holidayAllowanceForTP2);
            this.membershipForTP2.setValue(data.membershipForTP2);

            this.compTP2.setTitle(Wtf.Malaysian_StatutoryForm_TP2+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.tp2authorize)+")");

            if(data.tp2authorize==1){
                this.compTP2.disable();
            }else {
                this.compTP2.enable();
            }
            
        }
        
    },
    setTP1: function(data){

        if(data!=undefined){

            this.idForTP1.setValue(data.idForTP1);
            this.TP1Status.setValue(data.tp1authorize);
            this.oldIdentificationNumberForTP1.setValue(data.oldIdentificationNumberForTP1);
            this.newIdentificationNumberForTP1.setValue(data.newIdentificationNumberForTP1);
            this.passportNumberForTP1.setValue(data.passportNumberForTP1);
            this.armyOrPoliceNumberForTP1.setValue(data.armyOrPoliceNumberForTP1);

            this.incomeTaxNumberForTP2.setValue(data.incomeTaxNumberForTP2);

            this.compTP1.setTitle(Wtf.Malaysian_StatutoryForm_TP1+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.tp1authorize)+")");

            if(data.tp1authorize==1){
                this.compTP1.disable();
            }else {
                this.compTP1.enable();
            }
        }

    },
    getTP1 : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.tp1authorize:0;
        this.idForTP1=new Wtf.form.Hidden({
            name:'idForTP1',
            value:this.userdata!=undefined?this.userdata.idForTP1:""
        });
        arr.push(this.idForTP1);

        this.TP1Status=new Wtf.form.Hidden({
            name:'tp1authorize',
            id:'tp1authorize',
            value:status
        });
        arr.push(this.TP1Status);

        this.oldIdentificationNumberForTP1 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.old.identification.number"),
            scope:this,
            name:"oldIdentificationNumberForTP1",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.oldIdentificationNumberForTP1:""
        });
        arr.push(this.oldIdentificationNumberForTP1);

        this.newIdentificationNumberForTP1 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.new.identification.number"),
            scope:this,
            maxLength:50,
            name:"newIdentificationNumberForTP1",
            value:this.userdata!=undefined?this.userdata.newIdentificationNumberForTP1:""
        });
        arr.push(this.newIdentificationNumberForTP1);

        this.passportNumberForTP1 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.passport.number"),
            scope:this,
            maxLength:50,
            name:"passportNumberForTP1",
            value:this.userdata!=undefined?this.userdata.passportNumberForTP1:""
        });
        arr.push(this.passportNumberForTP1);

        this.armyOrPoliceNumberForTP1 = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.army.police.number"),
            scope:this,
            name:"armyOrPoliceNumberForTP1",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.armyOrPoliceNumberForTP1:""
        });
        arr.push(this.armyOrPoliceNumberForTP1);

        this.incomeTaxNumberForTP2 = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.incometax.number"),
            scope:this,
            name:"incomeTaxNumberForTP1",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.incomeTaxNumberForTP1:""
        });
        arr.push(this.incomeTaxNumberForTP2);
        
        this.compTP1 = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_TP1, status )
       
         return this.compTP1;
    },
    getTP2 : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.tp2authorize:0;
        this.idForTP2=new Wtf.form.Hidden({
            name:'idForTP2',
            value:this.userdata!=undefined?this.userdata.idForTP2:""
        });
        arr.push(this.idForTP2);

        this.TP2Status=new Wtf.form.Hidden({
            name:'tp2authorize',
            id:'tp2authorize',
            value:status
        });
        arr.push(this.TP2Status);

        this.oldIdentificationNumberForTP2 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.old.identification.number"),
            scope:this,
            name:"oldIdentificationNumberForTP2",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.oldIdentificationNumberForTP2:""
        });
        arr.push(this.oldIdentificationNumberForTP2);

        this.newIdentificationNumberForTP2 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.new.identification.number"),
            scope:this,
            maxLength:50,
            name:"newIdentificationNumberForTP2",
            value:this.userdata!=undefined?this.userdata.newIdentificationNumberForTP2:""
        });
        arr.push(this.newIdentificationNumberForTP2);

        this.passportNumberForTP2 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.passport.number"),
            scope:this,
            maxLength:50,
            name:"passportNumberForTP2",
            value:this.userdata!=undefined?this.userdata.passportNumberForTP2:""
        });
        arr.push(this.passportNumberForTP2);

        this.armyOrPoliceNumberForTP2 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.army.police.number"),
            scope:this,
            name:"armyOrPoliceNumberForTP2",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.armyOrPoliceNumberForTP2:""
        });
        arr.push(this.armyOrPoliceNumberForTP2);

        this.incomeTaxLHDNNumberForTP2 = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.lhdn.number"),
            scope:this,
            name:"incomeTaxLHDNNumberForTP2",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.incomeTaxLHDNNumberForTP2:""
        });
        arr.push(this.incomeTaxLHDNNumberForTP2);

        this.carForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.car"),
            scope:this,
            name:"carForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.carForTP2:""
        });
        arr.push(this.carForTP2);

        this.driverForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.driver"),
            scope:this,
            name:"driverForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.driverForTP2:""
        });
        arr.push(this.driverForTP2);

        this.householdItemsForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.household.items"),
            scope:this,
            name:"householdItemsForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.householdItemsForTP2:""
        });
        arr.push(this.householdItemsForTP2);

        this.entertainmentForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.entertainment"),
            scope:this,
            name:"entertainmentForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.entertainmentForTP2:""
        });
        arr.push(this.entertainmentForTP2);

        this.gardenerForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.gardener"),
            scope:this,
            name:"gardenerForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.gardenerForTP2:""
        });
        arr.push(this.gardenerForTP2);


        this.maidForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.maid"),
            scope:this,
            name:"maidForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.maidForTP2:""
        });
        arr.push(this.maidForTP2);


        this.holidayAllowanceForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.holiday.allowance"),
            scope:this,
            name:"holidayAllowanceForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.holidayAllowanceForTP2:""
        });
        arr.push(this.holidayAllowanceForTP2);


        this.membershipForTP2 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.membership"),
            scope:this,
            name:"membershipForTP2",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.membershipForTP2:""
        });
        arr.push(this.membershipForTP2);

        this.compTP2 = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_TP2, status )
        
        return this.compTP2;
    },
    setTP3: function(data){

        if(data!=undefined){

            this.idForTP3.setValue(data.idForTP3);
            this.TP3Status.setValue(data.tp3authorize);
            this.previousEmployer1ForTP3.setValue(data.previousEmployer1ForTP3);
            this.employerReferenceNo1ForTP3.setValue(data.employerReferenceNo1ForTP3);
            this.previousEmployer2ForTP3.setValue(data.previousEmployer2ForTP3);
            this.employerReferenceNo2ForTP3.setValue(data.employerReferenceNo2ForTP3);

            this.oldIdentificationNumberForTP3.setValue(data.oldIdentificationNumberForTP3);
            this.newIdentificationNumberForTP3.setValue(data.newIdentificationNumberForTP3);
            this.armyOrPoliceNumberForTP3.setValue(data.armyOrPoliceNumberForTP3);
            this.passportNumberForTP3.setValue(data.passportNumberForTP3);
            this.incomeTaxFileNumberForTP3.setValue(data.incomeTaxFileNumberForTP3);

            this.freeSampleProductOnDiscountForTP3.setValue(data.freeSampleProductOnDiscountForTP3);
            this.employeeLongServiceAwardForTP3.setValue(data.employeeLongServiceAwardForTP3);
            this.totalContributionToKWSPForTP3.setValue(data.totalContributionToKWSPForTP3);
            this.tuitionfeesForTP3.setValue(data.tuitionfeesForTP3);
            this.contributionToPrivatePensionForTP3.setValue(data.contributionToPrivatePensionForTP3);

            this.compTP3.setTitle(Wtf.Malaysian_StatutoryForm_TP3+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.tp3authorize)+")");

            if(data.tp3authorize==1){
                this.compTP3.disable();
            }else {
                this.compTP3.enable();
            }

        }

    },
    getTP3 : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.tp3authorize:0;
        this.idForTP3=new Wtf.form.Hidden({
            name:'idForTP3',
            value:this.userdata!=undefined?this.userdata.idForTP3:""
        });
        arr.push(this.idForTP3);

        this.TP3Status=new Wtf.form.Hidden({
            name:'tp3authorize',
            id:'tp3authorize',
            value:status
        });
        arr.push(this.TP3Status);

        this.previousEmployer1ForTP3 = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.previous.employer1"),
            scope:this,
            name:"previousEmployer1ForTP3",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.previousEmployer1ForTP3:""
        });
        arr.push(this.previousEmployer1ForTP3);

        this.employerReferenceNo1ForTP3 = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.employer.reference.no1"),
            scope:this,
            maxLength:50,
            name:"employerReferenceNo1ForTP3",
            value:this.userdata!=undefined?this.userdata.employerReferenceNo1ForTP3:""
        });
        arr.push(this.employerReferenceNo1ForTP3);

        this.previousEmployer2ForTP3 = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.previous.employer2"),
            scope:this,
            name:"previousEmployer2ForTP3",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.previousEmployer2ForTP3:""
        });
        arr.push(this.previousEmployer2ForTP3);

        this.employerReferenceNo2ForTP3 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.employer.reference.no2"),
            scope:this,
            maxLength:50,
            name:"employerReferenceNo2ForTP3",
            value:this.userdata!=undefined?this.userdata.employerReferenceNo2ForTP3:""
        });
        arr.push(this.employerReferenceNo2ForTP3);

        this.oldIdentificationNumberForTP3 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.old.identification.number"),
            scope:this,
            name:"oldIdentificationNumberForTP3",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.oldIdentificationNumberForTP3:""
        });
        arr.push(this.oldIdentificationNumberForTP3);

        this.newIdentificationNumberForTP3 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.new.identification.number"),
            scope:this,
            maxLength:50,
            name:"newIdentificationNumberForTP3",
            value:this.userdata!=undefined?this.userdata.newIdentificationNumberForTP3:""
        });
        arr.push(this.newIdentificationNumberForTP3);

        this.armyOrPoliceNumberForTP3 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.army.police.number"),
            scope:this,
            name:"armyOrPoliceNumberForTP3",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.armyOrPoliceNumberForTP3:""
        });
        arr.push(this.armyOrPoliceNumberForTP3);

        this.passportNumberForTP3 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.passport.number"),
            scope:this,
            maxLength:50,
            name:"passportNumberForTP3",
            value:this.userdata!=undefined?this.userdata.passportNumberForTP3:""
        });
        arr.push(this.passportNumberForTP3);

        this.incomeTaxFileNumberForTP3 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.incometax.file.number"),
            scope:this,
            name:"incomeTaxFileNumberForTP3",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.incomeTaxFileNumberForTP3:""
        });
        arr.push(this.incomeTaxFileNumberForTP3);

        this.freeSampleProductOnDiscountForTP3 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.free.sample.product.on.discount"),
            scope:this,
            name:"freeSampleProductOnDiscountForTP3",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.freeSampleProductOnDiscountForTP3:""
        });
        arr.push(this.freeSampleProductOnDiscountForTP3);

        this.employeeLongServiceAwardForTP3 = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.employee.long.service.award"),
            scope:this,
            name:"employeeLongServiceAwardForTP3",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.employeeLongServiceAwardForTP3:""
        });
        arr.push(this.employeeLongServiceAwardForTP3);

        this.totalContributionToKWSPForTP3 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.total.contribution.to.kwsp"),
            scope:this,
            name:"totalContributionToKWSPForTP3",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.totalContributionToKWSPForTP3:""
        });
        arr.push(this.totalContributionToKWSPForTP3);

        this.tuitionfeesForTP3 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.tuition.fees"),
            scope:this,
            name:"tuitionfeesForTP3",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.tuitionfeesForTP3:""
        });
        arr.push(this.tuitionfeesForTP3);

        this.contributionToPrivatePensionForTP3 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.contribution.to.private.pension"),
            scope:this,
            name:"contributionToPrivatePensionForTP3",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.contributionToPrivatePensionForTP3:""
        });
        arr.push(this.contributionToPrivatePensionForTP3);
        
        this.totalAllowanceForTP3 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.total.allowance"),
            scope:this,
            name:"totalAllowanceForTP3",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.totalAllowanceForTP3:""
        });
        arr.push(this.totalAllowanceForTP3);
        
        this.otherAllowanceForTP3 = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.other.allowance"),
            scope:this,
            name:"otherAllowanceForTP3",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.otherAllowanceForTP3:""
        });
        arr.push(this.otherAllowanceForTP3);

        this.compTP3 = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_TP3, status )
        
        return this.compTP3;
    },

    setCP39: function(data){

        if(data!=undefined){

            this.idForCP39.setValue(data.idForCP39);
            this.CP39Status.setValue(data.cp39authorize);
            this.incomeTaxFileNumberForCP39.setValue(data.incomeTaxFileNumberForCP39);
            this.oldIdentificationNumberForCP39.setValue(data.oldIdentificationNumberForCP39);
            this.newIdentificationNumberForCP39.setValue(data.newIdentificationNumberForCP39);
            this.passportNumberForCP39.setValue(data.passportNumberForCP39);

            this.countryCodeForCP39.setValue(data.countryCodeForCP39);
            this.deductionAmountForCP38ForCP39.setValue(data.deductionAmountForCP38ForCP39);

            this.compCP39.setTitle(Wtf.Malaysian_StatutoryForm_CP39+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.cp39authorize)+")");

            if(data.cp39authorize==1){
                this.compCP39.disable();
            }else {
                this.compCP39.enable();
            }
        }

    },
    getCP39 : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.cp39authorize:0;
        this.idForCP39=new Wtf.form.Hidden({
            name:'idForCP39',
            value:this.userdata!=undefined?this.userdata.idForCP39:""
        });
        arr.push(this.idForCP39);

        this.CP39Status=new Wtf.form.Hidden({
            name:'cp39authorize',
            id:'cp39authorize',
            value:status
        });
        arr.push(this.CP39Status);

        this.incomeTaxFileNumberForCP39 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.incometax.file.number"),
            scope:this,
            name:"incomeTaxFileNumberForCP39",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.incomeTaxFileNumberForCP39:""
        });
        arr.push(this.incomeTaxFileNumberForCP39);

        this.oldIdentificationNumberForCP39 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.old.identification.number"),
            scope:this,
            maxLength:50,
            name:"oldIdentificationNumberForCP39",
            value:this.userdata!=undefined?this.userdata.oldIdentificationNumberForCP39:""
        });
        arr.push(this.oldIdentificationNumberForCP39);

        this.newIdentificationNumberForCP39 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.new.identification.number"),
            scope:this,
            name:"newIdentificationNumberForCP39",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.newIdentificationNumberForCP39:""
        });
        arr.push(this.newIdentificationNumberForCP39);

        this.passportNumberForCP39 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.passport.number"),
            scope:this,
            maxLength:50,
            name:"passportNumberForCP39",
            value:this.userdata!=undefined?this.userdata.passportNumberForCP39:""
        });
        arr.push(this.passportNumberForCP39);

        this.countryCodeForCP39 = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.country.code"),
            scope:this,
            name:"countryCodeForCP39",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.countryCodeForCP39:""
        });
        arr.push(this.countryCodeForCP39);

        this.deductionAmountForCP38ForCP39 = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.deduction.amount.for.cp38"),
            scope:this,
            name:"deductionAmountForCP38ForCP39",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.deductionAmountForCP38ForCP39:""
        });
        arr.push(this.deductionAmountForCP38ForCP39);

        this.compCP39 = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_CP39, status )
        
        return this.compCP39;
    },

    setCP39A: function(data){

        if(data!=undefined){

            this.idForCP39A.setValue(data.idForCP39A);
            this.CP39AStatus.setValue(data.cp39Aauthorize);
            this.incomeTaxFileNumberForCP39A.setValue(data.incomeTaxFileNumberForCP39A);
            this.oldIdentificationNumberForCP39A.setValue(data.oldIdentificationNumberForCP39A);
            this.newIdentificationNumberForCP39A.setValue(data.newIdentificationNumberForCP39A);
            this.passportNumberForCP39A.setValue(data.passportNumberForCP39A);

            this.countryCodeForCP39A.setValue(data.countryCodeForCP39A);
            this.deductionAmountForCP38ForCP39A.setValue(data.deductionAmountForCP38ForCP39A);

            this.compCP39A.setTitle(Wtf.Malaysian_StatutoryForm_CP39A+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.cp39Aauthorize)+")");

            if(data.cp39Aauthorize==1){
                this.compCP39A.disable();
            }else {
                this.compCP39A.enable();
            }
        }

    },
    getCP39A : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.cp39Aauthorize:0;
        this.idForCP39A=new Wtf.form.Hidden({
            name:'idForCP39A',
            value:this.userdata!=undefined?this.userdata.idForCP39A:""
        });
        arr.push(this.idForCP39A);

        this.CP39AStatus=new Wtf.form.Hidden({
            name:'cp39Aauthorize',
            id:'cp39Aauthorize',
            value:status
        });
        arr.push(this.CP39AStatus);

        this.incomeTaxFileNumberForCP39A = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.incometax.file.number"),
            scope:this,
            name:"incomeTaxFileNumberForCP39A",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.incomeTaxFileNumberForCP39A:""
        });
        arr.push(this.incomeTaxFileNumberForCP39A);

        this.oldIdentificationNumberForCP39A = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.old.identification.number"),
            scope:this,
            maxLength:50,
            name:"oldIdentificationNumberForCP39A",
            value:this.userdata!=undefined?this.userdata.oldIdentificationNumberForCP39A:""
        });
        arr.push(this.oldIdentificationNumberForCP39A);

        this.newIdentificationNumberForCP39A = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.new.identification.number"),
            scope:this,
            name:"newIdentificationNumberForCP39A",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.newIdentificationNumberForCP39A:""
        });
        arr.push(this.newIdentificationNumberForCP39A);

        this.passportNumberForCP39A = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.passport.number"),
            scope:this,
            maxLength:50,
            name:"passportNumberForCP39A",
            value:this.userdata!=undefined?this.userdata.passportNumberForCP39A:""
        });
        arr.push(this.passportNumberForCP39A);

        this.countryCodeForCP39A = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.country.code"),
            scope:this,
            name:"countryCodeForCP39A",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.countryCodeForCP39A:""
        });
        arr.push(this.countryCodeForCP39A);

        this.deductionAmountForCP38ForCP39A = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.deduction.amount.for.cp38"),
            scope:this,
            name:"deductionAmountForCP38ForCP39A",
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.deductionAmountForCP38ForCP39A:""
        });
        arr.push(this.deductionAmountForCP38ForCP39A);

        this.compCP39A = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_CP39A, status )
        
        return this.compCP39A;
    },
    setEA: function(data){

        if(data!=undefined){
            this.idForEA.setValue(data.idForEA);
            this.EAStatus.setValue(data.eaAauthorize);
            this.serialNumberForEA.setValue(data.serialNumberForEA);
            this.employerERefNumberForEA.setValue(data.employerERefNumberForEA);
            this.incomeTaxFileNumberForEA.setValue(data.incomeTaxFileNumberForEA);
            this.incomeTaxBranchForEA.setValue(data.incomeTaxBranchForEA);
            this.oldIdentificationNumberForEA.setValue(data.oldIdentificationNumberForEA);
            this.newIdentificationNumberForEA.setValue(data.newIdentificationNumberForEA);
            this.accNumberKWSPForEA.setValue(data.accNumberKWSPForEA);
            this.incomeTaxPaidByEmployerForEA.setValue(data.incomeTaxPaidByEmployerForEA);
            this.carAndPetrolForEA.setValue(data.carAndPetrolForEA);
            this.carTypeForEA.setValue(data.carTypeForEA);
            this.carYearMakeForEA.setValue(data.carYearMakeForEA);
            this.carModelForEA.setValue(data.carModelForEA);
            this.driverWagesForEA.setValue(data.driverWagesForEA);
            this.entertainmentForEA.setValue(data.entertainmentForEA);
            this.handphoneForEA.setValue(data.handphoneForEA);
            this.maidAndGardenerForEA.setValue(data.maidAndGardenerForEA);
            this.airTicketsForHolidaysForEA.setValue(data.airTicketsForHolidaysForEA);
            this.otherBenefitsForClothingAndFoodsForEA.setValue(data.otherBenefitsForClothingAndFoodsForEA);
            this.housingAddressForEA.setValue(data.housingAddressForEA);
            this.refundsFromKWSPOtherForEA.setValue(data.refundsFromKWSPOtherForEA);
            this.compensationLossWorkForEA.setValue(data.compensationLossWorkForEA);
            this.retirementPaymentForEA.setValue(data.retirementPaymentForEA);
            this.periodicalPaymentForEA.setValue(data.periodicalPaymentForEA);
            this.cp38DeductionForEA.setValue(data.cp38DeductionForEA);
            this.nameForEA.setValue(data.nameForEA);
            this.portionOfKWSPForEA.setValue(data.portionOfKWSPForEA);
            this.typeOfIncomeForEA.setValue(data.typeOfIncomeForEA);
            this.contributionKWSPForEA.setValue(data.contributionKWSPForEA);
            this.amountForEA.setValue(data.amountForEA);
            this.nonTaxableAmountForEA.setValue(data.nonTaxableAmountForEA);
            this.otherBenefitsForEA.setValue(data.otherBenefitsForEA);
            this.housingBenefitsWithFurnitureForEA.setValue(data.housingBenefitsWithFurnitureForEA);
            this.housingBenefitsWithKitchenForEA.setValue(data.housingBenefitsWithKitchenForEA);
            this.furnitureAndFittingForEA.setValue(data.furnitureAndFittingForEA);
            this.kitchenAndUtensilsForEA.setValue(data.kitchenAndUtensilsForEA);
            this.compEA.setTitle(Wtf.Malaysian_StatutoryForm_EA+" ("+Wtf.getStatutoryFormAuthorizeRenderer(data.eaAauthorize)+")");

            if(data.eaAauthorize==1){
                this.compEA.disable();
            }else {
                this.compEA.enable();
            }
        }

    },
    getEA : function(){

        var arr = [];
        var status = this.userdata!=undefined?this.userdata.eaAauthorize:0;
        this.idForEA=new Wtf.form.Hidden({
            name:'idForEA',
            value:this.userdata!=undefined?this.userdata.idForEA:""
        });
        arr.push(this.idForEA);

        this.EAStatus=new Wtf.form.Hidden({
            name:'eaAauthorize',
            id:'eaAauthorize',
            value:status
        });
        arr.push(this.EAStatus);

        this.serialNumberForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.serial.number"),
            scope:this,
            maxLength:50,
            name:"serialNumberForEA",
            value:this.userdata!=undefined?this.userdata.serialNumberForEA:""
        });
        arr.push(this.serialNumberForEA);
        
        this.employerERefNumberForEA = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.employer.e.reference.number"),
            scope:this,
            maxLength:50,
            name:"employerERefNumberForEA",
            value:this.userdata!=undefined?this.userdata.employerERefNumberForEA:""
        });
        arr.push(this.employerERefNumberForEA);
        
        this.incomeTaxFileNumberForEA = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.incometax.file.number"),
            scope:this,
            maxLength:50,
            name:"incomeTaxFileNumberForEA",
            value:this.userdata!=undefined?this.userdata.incomeTaxFileNumberForEA:""
        });
        arr.push(this.incomeTaxFileNumberForEA);
        
        this.incomeTaxBranchForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.incometax.branch"),
            scope:this,
            maxLength:50,
            name:"incomeTaxBranchForEA",
            value:this.userdata!=undefined?this.userdata.incomeTaxBranchForEA:""
        });
        arr.push(this.incomeTaxBranchForEA);

        this.oldIdentificationNumberForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.old.identification.number"),
            scope:this,
            maxLength:50,
            name:"oldIdentificationNumberForEA",
            value:this.userdata!=undefined?this.userdata.oldIdentificationNumberForEA:""
        });
        arr.push(this.oldIdentificationNumberForEA);

        this.newIdentificationNumberForEA = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.new.identification.number"),
            scope:this,
            name:"newIdentificationNumberForEA",
            maxLength:50,
            value:this.userdata!=undefined?this.userdata.newIdentificationNumberForEA:""
        });
        arr.push(this.newIdentificationNumberForEA);

        this.accNumberKWSPForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.account.number.kwsp"),
            scope:this,
            maxLength:50,
            name:"accNumberKWSPForEA",
            value:this.userdata!=undefined?this.userdata.accNumberKWSPForEA:""
        });
        arr.push(this.accNumberKWSPForEA);
        
        this.incomeTaxPaidByEmployerForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.incometax.paid.by.employer"),
            scope:this,
            maxLength:10,
            name:"incomeTaxPaidByEmployerForEA",
            value:this.userdata!=undefined?this.userdata.incomeTaxPaidByEmployerForEA:""
        });
        arr.push(this.incomeTaxPaidByEmployerForEA);
        
        
        this.carAndPetrolForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.car.and.petrol"),
            scope:this,
            maxLength:10,
            name:"carAndPetrolForEA",
            value:this.userdata!=undefined?this.userdata.carAndPetrolForEA:""
        });
        arr.push(this.carAndPetrolForEA);
        
        
        this.carTypeForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.car.type"),
            scope:this,
            maxLength:50,
            name:"carTypeForEA",
            value:this.userdata!=undefined?this.userdata.carTypeForEA:""
        });
        arr.push(this.carTypeForEA);
        
        
        this.carYearMakeForEA = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.car.year.make"),
            scope:this,
            format:"Y-m-d",
            width:150,
            name:"carYearMakeForEA",
            value:this.userdata!=undefined?this.userdata.carYearMakeForEA:""
        });
        arr.push(this.carYearMakeForEA);
        
        
        this.carModelForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.car.model"),
            scope:this,
            maxLength:50,
            name:"carModelForEA",
            value:this.userdata!=undefined?this.userdata.carModelForEA:""
        });
        arr.push(this.carModelForEA);
        
        
        this.driverWagesForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.driver.wages"),
            scope:this,
            maxLength:10,
            name:"driverWagesForEA",
            value:this.userdata!=undefined?this.userdata.driverWagesForEA:""
        });
        arr.push(this.driverWagesForEA);
        
        this.entertainmentForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.entertainment"),
            scope:this,
            maxLength:10,
            name:"entertainmentForEA",
            value:this.userdata!=undefined?this.userdata.entertainmentForEA:""
        });
        arr.push(this.entertainmentForEA);
        
        
        this.handphoneForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.handphone"),
            scope:this,
            maxLength:10,
            name:"handphoneForEA",
            value:this.userdata!=undefined?this.userdata.handphoneForEA:""
        });
        arr.push(this.handphoneForEA);
        
        
        this.maidAndGardenerForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.maid.and.gardener"),
            scope:this,
            maxLength:10,
            name:"maidAndGardenerForEA",
            value:this.userdata!=undefined?this.userdata.maidAndGardenerForEA:""
        });
        arr.push(this.maidAndGardenerForEA);
        
        this.airTicketsForHolidaysForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.air.tickets.for.holidays"),
            scope:this,
            maxLength:10,
            name:"airTicketsForHolidaysForEA",
            value:this.userdata!=undefined?this.userdata.airTicketsForHolidaysForEA:""
        });
        arr.push(this.airTicketsForHolidaysForEA);
        
        this.otherBenefitsForClothingAndFoodsForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.other.benefits.clothing.foods"),
            scope:this,
            maxLength:10,
            name:"otherBenefitsForClothingAndFoodsForEA",
            value:this.userdata!=undefined?this.userdata.otherBenefitsForClothingAndFoodsForEA:""
        });
        arr.push(this.otherBenefitsForClothingAndFoodsForEA);
        
        
        this.housingAddressForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.housing.address"),
            scope:this,
            maxLength:50,
            name:"housingAddressForEA",
            value:this.userdata!=undefined?this.userdata.housingAddressForEA:""
        });
        arr.push(this.housingAddressForEA);
        
        this.refundsFromKWSPOtherForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.refunds.from.kwsp.other"),
            scope:this,
            maxLength:10,
            name:"refundsFromKWSPOtherForEA",
            value:this.userdata!=undefined?this.userdata.refundsFromKWSPOtherForEA:""
        });
        arr.push(this.refundsFromKWSPOtherForEA);
        
        this.compensationLossWorkForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.compensation.loss.work"),
            scope:this,
            maxLength:10,
            name:"compensationLossWorkForEA",
            value:this.userdata!=undefined?this.userdata.compensationLossWorkForEA:""
        });
        arr.push(this.compensationLossWorkForEA);
        
        
        this.retirementPaymentForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.retirement.payment"),
            scope:this,
            maxLength:10,
            name:"retirementPaymentForEA",
            value:this.userdata!=undefined?this.userdata.retirementPaymentForEA:""
        });
        arr.push(this.retirementPaymentForEA);
        
        
        this.periodicalPaymentForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.periodical.payment"),
            scope:this,
            maxLength:10,
            name:"periodicalPaymentForEA",
            value:this.userdata!=undefined?this.userdata.periodicalPaymentForEA:""
        });
        arr.push(this.periodicalPaymentForEA);
        
        
        this.cp38DeductionForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.cp38.deduction"),
            scope:this,
            maxLength:10,
            name:"cp38DeductionForEA",
            value:this.userdata!=undefined?this.userdata.cp38DeductionForEA:""
        });
        arr.push(this.cp38DeductionForEA);
        
        
        this.nameForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.EmailTemplateCmb.Name"),
            scope:this,
            maxLength:50,
            name:"nameForEA",
            value:this.userdata!=undefined?this.userdata.nameForEA:""
        });
        arr.push(this.nameForEA);
        
        this.portionOfKWSPForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.portion.of.kwsp"),
            scope:this,
            maxLength:10,
            name:"portionOfKWSPForEA",
            value:this.userdata!=undefined?this.userdata.portionOfKWSPForEA:""
        });
        arr.push(this.portionOfKWSPForEA);
        
        this.typeOfIncomeForEA = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.type.of.income"),
            scope:this,
            maxLength:50,
            name:"typeOfIncomeForEA",
            value:this.userdata!=undefined?this.userdata.typeOfIncomeForEA:""
        });
        arr.push(this.typeOfIncomeForEA);
        
        this.contributionKWSPForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.contribution.on.kwsp"),
            scope:this,
            maxLength:10,
            name:"contributionKWSPForEA",
            value:this.userdata!=undefined?this.userdata.contributionKWSPForEA:""
        });
        arr.push(this.contributionKWSPForEA);
        
        this.amountForEA = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.Amount"),
            scope:this,
            maxLength:10,
            name:"amountForEA",
            value:this.userdata!=undefined?this.userdata.amountForEA:""
        });
        arr.push(this.amountForEA);
        
        this.nonTaxableAmountForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.non.taxable.amount"),
            scope:this,
            maxLength:10,
            name:"nonTaxableAmountForEA",
            value:this.userdata!=undefined?this.userdata.nonTaxableAmountForEA:""
        });
        arr.push(this.nonTaxableAmountForEA);
        
        this.otherBenefitsForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.other.benefits"),
            scope:this,
            maxLength:10,
            name:"otherBenefitsForEA",
            value:this.userdata!=undefined?this.userdata.otherBenefitsForEA:""
        });
        arr.push(this.otherBenefitsForEA);
        
        this.housingBenefitsWithFurnitureForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.housing.benefits.with.furniture"),
            scope:this,
            maxLength:10,
            name:"housingBenefitsWithFurnitureForEA",
            value:this.userdata!=undefined?this.userdata.housingBenefitsWithFurnitureForEA:""
        });
        arr.push(this.housingBenefitsWithFurnitureForEA);
        
        this.housingBenefitsWithKitchenForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.housing.benefits.with.kitchen"),
            scope:this,
            maxLength:10,
            name:"housingBenefitsWithKitchenForEA",
            value:this.userdata!=undefined?this.userdata.housingBenefitsWithKitchenForEA:""
        });
        arr.push(this.housingBenefitsWithFurnitureForEA);
        
        this.furnitureAndFittingForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.furniture.and.fitting"),
            scope:this,
            maxLength:10,
            name:"furnitureAndFittingForEA",
            value:this.userdata!=undefined?this.userdata.furnitureAndFittingForEA:""
        });
        arr.push(this.furnitureAndFittingForEA);
        
        this.kitchenAndUtensilsForEA = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.kitchen.and.utensils"),
            scope:this,
            maxLength:10,
            name:"kitchenAndUtensilsForEA",
            value:this.userdata!=undefined?this.userdata.kitchenAndUtensilsForEA:""
        });
        arr.push(this.kitchenAndUtensilsForEA);

        this.compEA = this.getFieldSet(arr,Wtf.Malaysian_StatutoryForm_EA, status )
        
        return this.compEA;
    }
});
