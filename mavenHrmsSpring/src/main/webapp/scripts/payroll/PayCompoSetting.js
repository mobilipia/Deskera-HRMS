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
Wtf.PayCompoSetting=function(config){
    config.autoScroll="true";
    config.layout="fit";
    Wtf.PayCompoSetting.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.PayCompoSetting,Wtf.Panel,{
    initComponent:function(config){
        Wtf.PayCompoSetting.superclass.initComponent.call(this,config);
        this.cm=new Wtf.grid.ColumnModel([
            new Wtf.grid.CheckboxSelectionModel(),
            new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.payroll.codeid"),
                dataIndex:'code',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.type"),
                dataIndex:'type',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.Addasdefault"),
                dataIndex:'isdefault',
                sortable: true,
                renderer:function(val,a,b){
                    if(val){
                        return(WtfGlobal.getLocaleText("hrms.common.yes"));
                    }else{
                        return (WtfGlobal.getLocaleText("hrms.common.no"));
                    }
                }
            },{
                header:'<div align=\"right\">'+WtfGlobal.getLocaleText("hrms.payroll.ratein")+'</div>',
                dataIndex:'rate',
                sortable: true,
                renderer:function(val,a,b){
                    if(val==1){
                        return('<div align=\"right\">'+parseFloat(b.get('cash')).toFixed(2)+' %</div>');
                    }else{
                        return ('<div align=\"right\">-</div>');
                    }
                }
            },
            {
                header:"<div align=\"right\">"+WtfGlobal.getLocaleText({key:"hrms.payroll.amountin",params:[WtfGlobal.getCurrencySymbol()]})+"</div>",
                dataIndex:'cash',
                sortable: true,
                renderer:function(val,a,b){
                    if(b.get('rate')==0){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }else{
                        return ('<div align=\"right\">-</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.computeon"),
                dataIndex: 'computeon',
                align: 'center',
                sortable: true,
                renderer:this.rendererfun.createDelegate(this)
            }
        ]);
        this.usersRecT = new Wtf.data.Record.create([
        {
            name: 'type'
        },
        {
            name: 'cash'
        },
        {
            name: 'rate'
        },
        {
            name: 'code'
        },
        {
            name: 'id'
        },
        {
            name: 'isdefault'
        },
        {
            name: 'rangefrom'
        },
        {
            name: 'rangeto'
        },
        {
            name: 'category'
        },
        {
            name: 'depwage'
        },
        {
            name: 'depwageid'
        },
        {
            name: 'computeon'
        },
        {
            name: 'expr'
        },
        {
            name: 'comp'
        }

        ]);
        this.userdsT = new Wtf.data.Store({
            scope:this,
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:'totalcount'
            },this.usersRecT),
            baseParams : {
                tablename:'Wagemaster',
                type:'getPayComponent',
                allflag:'false'
            },
//            url: Wtf.req.base + "PayrollHandler.jsp"
            url: "Payroll/Wage/getWageMaster.py"
        });

        this.userdsT.load({
            params:{
                start:0,
                limit:15,
                grouper:'payrollcomp'
            }
        });

        this.userdsT.on("load",function(){
            if(this.payrollcombo.getValue()=='Wage')
                this.userdsT.filter('comp','wage');
            else if(this.payrollcombo.getValue()=='Deduction')
                this.userdsT.filter('comp','deduc');
            else if(this.payrollcombo.getValue()=='Employer Contribution')
                this.userdsT.filter('comp','empcontrib');
            if(this.userdsT.getCount()==0){
               this.generaltaxgrid.getView().emptyText=WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addcompo(\""+this.id+"\")'>Get started by adding a payroll components now...</a>");
               this.generaltaxgrid.getView().refresh();
            }
        },this)

        this.combodata=[['Wage','Wagemaster',WtfGlobal.getLocaleText("hrms.payroll.Wage")],['Tax','Taxmaster',WtfGlobal.getLocaleText("hrms.payroll.Tax")],['Deduction','Deductionmaster',WtfGlobal.getLocaleText("hrms.payroll.Deduction")],['Employer Contribution','EmpContribmaster',WtfGlobal.getLocaleText("hrms.payroll.EmployerContribution")]];
        this.combostore1=new Wtf.data.SimpleStore({
            fields:[{
                name:'type'
            },
            {
                name:'table'
            },{
                name:'displaytype'
            }],
            data:this.combodata
        });
        this.type="Wage";
        this.tablename="Wagemaster";

        this.payrollcombo=new Wtf.form.ComboBox({
            store:this.combostore1,
            width:200,
            displayField:'displaytype',
            forceSelection:true,
            typeAhead: true,
            valueField:'table',
            scope:this,
            mode: 'local',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.selectcomponent"),
            selectOnFocus:true,
            forceSelection:true,
            listeners:{
                scope:this,
                select:function(combo,record,index)
                {
                    this.userdsT.removeAll();
                    this.tablename=record.get('table');
                    this.type=record.get('type');
                    this.importButtonWage.hide();
                    this.importButtonDeduction.hide();
                    this.importButtonTax.hide();
                    this.importButtonEmp.hide();
                    if(this.tablename=="Wagemaster") {
                        this.userdsT.proxy.conn.url = "Payroll/Wage/getWageMaster.py";
                        //To do - Need to uncomment
//                        if(!WtfGlobal.EnableDisable(Wtf.UPerm.importf, Wtf.Perm.importf.importpaycomp)){
//                            this.importButtonWage.show();
//                        }
                    }else if(this.tablename=="Taxmaster"){
                        this.userdsT.proxy.conn.url = "Payroll/Tax/getTaxMaster.py";     
//                        if(!WtfGlobal.EnableDisable(Wtf.UPerm.importf, Wtf.Perm.importf.importpaycomp)){
//                            this.importButtonTax.show();
//                        }
                    }else if(this.tablename=="Deductionmaster"){
                        this.userdsT.proxy.conn.url = "Payroll/Deduction/getDeductionMaster.py";
//                        if(!WtfGlobal.EnableDisable(Wtf.UPerm.importf, Wtf.Perm.importf.importpaycomp)){
//                            this.importButtonDeduction.show();
//                        }
                    }else if(this.tablename=="EmpContribmaster"){
                        this.userdsT.proxy.conn.url = "Payroll/EmpContrib/getEmpContribMaster.py";
//                        if(!WtfGlobal.EnableDisable(Wtf.UPerm.importf, Wtf.Perm.importf.importpaycomp)){
//                            this.importButtonEmp.show();
//                        }
                    }
//                    this.userdsT.baseParams = {
//                        tablename:this.tablename,
//                        type:'getPayComponent'
//                    }
                    this.userdsT.load({
                        params:{
                            start:0,
                            limit:this.generaltaxgrid.pag.pageSize
                        }
                    })
                    var paysetComp = Wtf.getCmp('paysettingmainPanel');
                    if(this.type=="Tax") {
                        this.incometaxgrid.show();
                        paysetComp.doLayout();
                        Wtf.getCmp('as').doLayout();
                    } else
                    {
                        this.incometaxgrid.hide();
                        paysetComp.doLayout();
                    }
                }
            }
        });
        this.payrollcombo.setValue('Wagemaster');
        this.tablename='Wagemaster';
        
        var extraConfig = {};
//        extraConfig.url= "ACCAccount/importAccounts.do";
        var extraParams = "{\"Companydetails\":\""+companyid+"\"}";
        this.importBtnArrayWage= Wtf.importMenuArray1(this, "Wage", this.userdsT, extraParams, extraConfig);
        this.importButtonWage= Wtf.importMenuButtonA1(this.importBtnArrayWage, this, "Wage");
        this.importBtnArrayDeduction= Wtf.importMenuArray1(this, "Deduction", this.userdsT, extraParams, extraConfig);
        this.importButtonDeduction= Wtf.importMenuButtonA1(this.importBtnArrayDeduction, this, "Deduction");
        this.importBtnArrayTax= Wtf.importMenuArray1(this, "Tax", this.userdsT, extraParams, extraConfig);
        this.importButtonTax= Wtf.importMenuButtonA1(this.importBtnArrayTax, this, "Tax");
        extraParams = "{\"Companyid\":\""+companyid+"\"}";
        this.importBtnArrayEmp= Wtf.importMenuArray1(this, "Employee Contribution", this.userdsT, extraParams, extraConfig);
        this.importButtonEmp= Wtf.importMenuButtonA1(this.importBtnArrayEmp, this, "Employee Contribution");
        this.importButtonWage.hide();
        this.importButtonDeduction.hide();
        this.importButtonTax.hide();
        this.importButtonEmp.hide();
        //To do - Need to uncomment
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.importf, Wtf.Perm.importf.importpaycomp)){
//            this.importButtonWage.show();
//        }
        
        this.generaltaxgrid = new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            region:'center',
            height:350,
            loadMask:true,
            displayInfo:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:  WtfGlobal.getLocaleText("hrms.common.Searchbyname"),
            searchField:"type",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true
            },
            store: this.userdsT,
            cm: this.cm,
            border :false,
            sm: new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            tbar:['-',new Wtf.Toolbar.Button({
         		text:WtfGlobal.getLocaleText("hrms.common.reset"),
         		scope: this,
         		iconCls:'pwndRefresh',
         		handler:function(){
            		this.userdsT.load({params:{start:0,limit:this.generaltaxgrid.pag.pageSize}});
            		Wtf.getCmp("Quick"+this.generaltaxgrid.id).setValue("");
            	}
         	}),'-',
            WtfGlobal.getLocaleText("hrms.payroll.selectcomponent") +':',this.payrollcombo,'-',{
                text:WtfGlobal.getLocaleText("hrms.payroll.addnew"),
                scope:this,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.addnew.tooltip"),
                minWidth:60,
                iconCls:getButtonIconCls(Wtf.btype.addbutton),
                handler:this.addcomponent
            },
            '-',this.editcomponent=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.common.edit"),
                scope:this,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.edit.tooltip"),
                minWidth:60,
                iconCls:getButtonIconCls(Wtf.btype.editbutton),
                handler:function()
                {
                     if(this.generaltaxgrid.getSelectionModel().hasSelection()==false){
                       calMsgBoxShow(42,0);
                    } else{
                        var rec=this.generaltaxgrid.getSelectionModel().getSelected();
                    this.wageDeduWinEdit=new Wtf.analysisWindow({
                        layout:"fit",
                        modal:true,
                        title:WtfGlobal.getLocaleText("hrms.payroll.edit.component"),
                        closable:true,
                        id:"addWTDwindow",
                        closeAction:'close',
                        width:500,
                        typeimage:'../../images/payroll.gif',
                        height:450,
                        payrollcombo:this.payrollcombo,
                        iconCls:getButtonIconCls(Wtf.btype.winicon),
                        scope:this,
                        editWageId: rec.data['id'],
                        AddEdit:'Edit',
                        generaltaxgrid:this.generaltaxgrid,
                        type:this.type,
                        userdsT:this.userdsT,
                        incometaxgrid:this.incometaxgrid,
                        tablename:this.tablename
                    });
                    this.wageDeduWinEdit.on('show',function(){
                        this.wageDeduWinEdit.text1.focus(true,100);
                    },this);
                    this.wageDeduWinEdit.show();
                    
                    }

                }
            }),'-',this.deladdwtdform=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.common.delete"),
                scope:this,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.delete.tooltip"),
                minWidth:70,
                iconCls:getButtonIconCls(Wtf.btype.deletebutton),
                handler:function()
                {
                    if(!this.generaltaxgrid.getSelectionModel().hasSelection()){
                        calMsgBoxShow(42,0);
                    } else {
                        var rec=this.generaltaxgrid.getSelectionModel().getSelected();
                        for(var n = 0 ; n < this.userdsT.data.length ; n++){
                            if(this.userdsT.getAt(n).data.depwageid == rec.data['id']){
                                calMsgBoxShow(207,2);
                                return;
                            }
                        }
                        Wtf.MessageBox.show({
                                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                            msg:deleteMsgBox('component'),
                            buttons:Wtf.MessageBox.YESNO,
                            icon:Wtf.MessageBox.QUESTION,
                            scope:this,
                            fn:function(button){
                                if(button=='yes')
                                {
                                    var rec=this.generaltaxgrid.getSelectionModel().getSelected()
                                    var row=this.userdsT.indexOf(rec);
                                    this.generaltaxgrid.getSelectionModel().clearSelections();
                                    WtfGlobal.highLightRow(this.generaltaxgrid,"FF0000",5, row)
                                    calMsgBoxShow(201,4,true);
                                    var url;
                                    if(this.type=="Deduction") {
                                        url = "Payroll/Deduction/deleteMasterDeduc.py";
                                    } else if(this.type=="Wage") {
                                        url = "Payroll/Wage/deleteMasterWage.py";
                                    } else if (this.type=="Employer Contribution") {
                                        url = "Payroll/EmpContrib/deleteMasterEmpContrib.py";
                                    } else {
                                        url = "Payroll/Tax/deleteMasterTax.py";
                                    }
                                    Wtf.Ajax.requestEx({
                                        url: url,
                                        method:'post',
                                        params:{
                                            dele:'true',//Create Payslip Per Template
                                            delType:this.type,
                                            typeid:rec.data['id']
                                        }
                                    },
                                    this,
                                    function(req){
                                        var resStr = req.value.toString();
                                        if(resStr =='success'){
                                            calMsgBoxShow(9,0);
                                            //this.userdsT. removeAll();
                                            var params={
                                                start:0,
                                                limit:this.generaltaxgrid.pag.pageSize
                                            }
                                            WtfGlobal.delaytasks(this.userdsT,params);
                                            this.generaltaxgrid.doLayout();
                                        }else if(resStr =='assign'){
                                            calMsgBoxShow(10,0);
                                        }else if(resStr =='depend'){
                                            calMsgBoxShow(226,0);
                                        }
                                    },
                                    function(req){
                                        this.userdsT.reload();
                                        this.generaltaxgrid.doLayout();
                                        calMsgBoxShow(27,2);
                                    });
                                }
                            }
                        });
                    }
                }
            }),this.importButtonWage,this.importButtonDeduction,this.importButtonTax,this.importButtonEmp
            ,'-',{
                text:WtfGlobal.getLocaleText("hrms.payroll.definecpfrules"),
                scope:this,
                hidden: true,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.definecpfrules.tooltip"),
                minWidth:60,
                iconCls:getButtonIconCls(Wtf.btype.addbutton),
                handler:this.CPFRules

            }
            ]
        });

        this.cm1=new Wtf.grid.ColumnModel([
            new Wtf.grid.CheckboxSelectionModel(),
            new Wtf.grid.RowNumberer(),
           {
                header:WtfGlobal.getLocaleText("hrms.common.category"),
                dataIndex:'category',
                sortable: true
            }, {
                header:'<div align=\"right\">'+WtfGlobal.getLocaleText("hrms.payroll.ratein")+'</div>',
                dataIndex:'rate',
                sortable: true,
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\">'+parseFloat(val).toFixed(2)+'</div>');
                    }
                }

            },{
                 header:'<div align=\"right\">'+WtfGlobal.getLocaleText("hrms.payroll.salarymin")+'</div>',
                dataIndex:'rangefrom',
                sortable: true,
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            },{
                header:'<div align=\"right\">'+WtfGlobal.getLocaleText("hrms.payroll.salarymax")+'</div>',
                dataIndex:'rangeto',
                sortable: true,
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            }
            ]);

        this.incomeRec= new Wtf.data.Record.create([
        {
            name: 'rate'
        },
        {
            name: 'category'
        },
        {
            name: 'rangefrom'
        },

        {
            name: 'rangeto'
        },
        {
            name: 'id'
        }
        ]);
        this.incometaxstore=new Wtf.data.Store({
            scope:this,
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:'totalcount'
            },this.incomeRec),
//            url: Wtf.req.base + "PayrollHandler.jsp"
            url: "Payroll/Tax/GetTaxperCatgry.py"
        });
        this.catgrec=new Wtf.data.Record.create([
        {
            name:'id'
        },

        {
            name:'name'
        }
        ]);
        this.catgStore =  new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            baseParams: {
                configid:14,
                flag:203,
                common:'1'
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.catgrec),
            autoLoad : false
        });

        this.catgStore.load();
        this.catgStore.on('load',function(){
            if(this.catgStore.getCount()>0){
                var allrec=new this.catgrec({
                    id:'0',
                    name:'All'
                });
                var c=this.catgStore.getCount();
                this.catgStore.insert(c,allrec);
                this.ctgrycombo1.setValue(this.catgStore.getAt(c).get('id'));
                this.incometaxstore.baseParams={
                     type:'GetTaxperCatgry',
                     categoryid:this.ctgrycombo1.getValue()
                }
     this.incometaxstore.load({
                        params:{
                            start:0,
                            limit:15
                        }
                   });
            }
        },this);
        this.ctgrycombo1=new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.category"),
            store:this.catgStore,
            id:'catgorycombo2',
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
                    this.incometaxstore.removeAll();
                    this.incometaxstore.baseParams={
                         type:'GetTaxperCatgry',
                         categoryid: this.ctgrycombo1.getValue()
                    }
                    this.incometaxstore.load({
                        params:{
                            start:0,
                            limit:this.incometaxgrid.pag.pageSize
                        }
                    });

                }
            }
        });


        this.incometaxgrid=new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            id:'incometaxgridpanel',
            region:'south',
            height:250,
            loadMask:true,
            displayInfo:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.incometaxgrid.searchby"),

            searchField:"rate",
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.incometaxgrid.emptytext"))
            },
            store: this.incometaxstore,
            cm: this.cm1,
            border :true,
            sm: new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            tbar:['-',
            WtfGlobal.getLocaleText("hrms.common.select.category")+':',this.ctgrycombo1,'-',{
                text:WtfGlobal.getLocaleText("hrms.payroll.addnewincometax"),
                scope:this,
                minWidth:70,
                iconCls:getButtonIconCls(Wtf.btype.addbutton),
                handler:function()
                {
                    this.usertask=new Wtf.AddIncometaxWin({
                        layout:"fit",
                        modal:true,
                        title:WtfGlobal.getLocaleText("hrms.payroll.add.component"),
                        configid:'configid',
                        parentid:'parentid',
                        closable:true,
                        id:'incometaxwin',
                        //closeAction:'hide',
                        width:500,
                        typeimage:'../../images/tax.gif',
                        height:450,
                        incometaxstore:this.incometaxstore,
                        ctgrycombo1:this.ctgrycombo1,
                        iconCls:'WinIcon',
                        gridp:this.incometaxgrid

                    });
                    this.usertask.show();
                }
            },
            '-',this.delincometax=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.common.delete"),
                scope:this,
                minWidth:60,
                iconCls:getButtonIconCls(Wtf.btype.deletebutton),
                handler:function()
                {
                    if(!this.incometaxgrid.getSelectionModel().hasSelection()) {
                        calMsgBoxShow(42,0);
                    } else {
                        Wtf.MessageBox.show({
                            title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                            msg:deleteMsgBox('record'),
                            buttons:Wtf.MessageBox.YESNO,
                            icon:Wtf.MessageBox.QUESTION,
                            scope:this,
                            fn:function(button){
                                if(button=='yes')
                                {
                                    calMsgBoxShow(201,4,true);
                                    Wtf.Ajax.requestEx({
//                                        url: Wtf.req.base + "PayrollHandler.jsp" ,
                                        url: "Payroll/Tax/deleteincomeTax.py",
                                        method:'post',
                                        params:{
                                            dele:'true',//Create Payslip Per Template
                                            delType:"IncomeTax",
                                            typeid:this.incometaxgrid.getSelectionModel().getSelected().get('id')
                                        }
                                    },
                                    this,
                                    function(req){
                                        var resObj = req.value;
                                        if(resObj =='success'){
                                            calMsgBoxShow(9,0);
                                             this.incometaxstore.baseParams={
                                                         type:'GetTaxperCatgry',
                                                         categoryid: this.ctgrycombo1.getValue()
                                                    }
                                                    this.incometaxstore.load({
                                                        params:{
                                                            start:0,
                                                            limit:this.incometaxgrid.pag.pageSize
                                                        }
                                                    });
                                        }else if(resObj =='assign'){
                                            calMsgBoxShow(10,0);
                                        }
                                    },
                                    function(req){
                                        this.incometaxstore.reload();
                                    });
                                }
                            }
                        });
                    }
                }
            })
            ]

        });

        this.paysettingmainPanel=new Wtf.Panel({
            style:'background:#FFFFFF',
            border:false,
            autoScroll:true,
            id:'paysettingmainPanel',
            scope:this,
            layout:'border',
            items:[this.generaltaxgrid]//[this.generaltaxgrid,this.incometaxgrid]
        });

        this.incometaxgrid.hide();

        this.add(this.paysettingmainPanel);
        this.doLayout();
        this.on('activate', function(tp, tab){
            this.doLayout();
        });

    },
    addcomponent:function(){
                    var wageDeduWin=new Wtf.analysisWindow({
                        layout:"fit",
                        modal:true,
                        title:WtfGlobal.getLocaleText("hrms.payroll.add.component"),
                        configid:'configid',
                        parentid:'parentid',
                        closable:true,
                        id:"addWTDwindow",
                        closeAction:'close',
                        width:500,
                        typeimage:'../../images/payroll.gif',
                        height:450,
                        payrollcombo:this.payrollcombo,
                        iconCls:getButtonIconCls(Wtf.btype.winicon),
                        scope:this,
                        userdsT:this.userdsT,
                        incometaxgrid:this.incometaxgrid,
                        type:this.type,
                        AddEdit:'Add',
                        generaltaxgrid:this.generaltaxgrid,
                        tablename:this.tablename
                    });
                   wageDeduWin.on('show',function(){
                        wageDeduWin.text1.focus(true,100);
                   },this);
                   wageDeduWin.show();
                   
                },
    addCategory:function(){
        WtfGlobal.showmasterWindow(14,this.catgStore,"Add");
    },
    CPFRules:function(){
        var mainTabId = Wtf.getCmp("as");
        var CPFComp = Wtf.getCmp("CPFSetting");
        if(CPFComp == null){
            CPFComp = new Wtf.CPFSetting({
                layout:"fit",
                title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.cpfrules.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.cpfrules")+"</div>",
                closable:true,
                border:false,
                iconCls: getTabIconCls(Wtf.etype.master),
                id:"CPFSetting"
            });
            mainTabId.add(CPFComp);
        }
        mainTabId.setActiveTab(CPFComp);
        mainTabId.doLayout();
    },
     rendererfun: function(val,met,record,row,col,store,exp){
        if(record.get("rate")==0){
            return "-"
        }
            if(val=="0"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentdeductions");
            }else if(val=="1"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentearnings");
            }else if(val=="2"){
                return WtfGlobal.getLocaleText("hrms.payroll.netsalary");
            }else if(val=="3"){
                return WtfGlobal.getLocaleText("hrms.payroll.specifiedformula");
            }
     }
});
function addcompo(Id){
    Wtf.getCmp(Id).addcomponent();
}
