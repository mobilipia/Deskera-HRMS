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
Wtf.TempEmpMaster=function(config){
    Wtf.form.Field.prototype.msgTarget='side',
    config.layout="fit";
   // config.title='Employee Management';
    Wtf.TempEmpMaster.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.TempEmpMaster,Wtf.Panel,{
	id:'PayrollManagementTempEmpMaster',
    showhide:function (dd_textfield,display,labeldisplay) {
        dd_textfield.setVisible(display);
        dd_textfield.container.up('div.x-form-item').dom.style.display = labeldisplay;


        var ct = dd_textfield.el.findParent('div.x-form-item', 4, true);
        var label = ct.first('label.x-form-item-label');
        label.dom.style.display=labeldisplay;
    },
    onRender: function(config){
        Wtf.TempEmpMaster.superclass.onRender.call(this, config);
        this.quickSearchEmpField.on("render",function(){
            this.quickSearchEmpField.el.dom.onkeyup = this.onKeyUpEvent.createDelegate(this);
        },this);
    },
    initComponent:function(config){
        Wtf.TempEmpMaster.superclass.initComponent.call(this,config);
        this.lastlimitoption = 15;
        this.cnamedata = [
        [WtfGlobal.getLocaleText("hrms.January")],
        [WtfGlobal.getLocaleText("hrms.February")],
        [WtfGlobal.getLocaleText("hrms.March")],
        [WtfGlobal.getLocaleText("hrms.April")],
        [WtfGlobal.getLocaleText("hrms.May")],
        [WtfGlobal.getLocaleText("hrms.June")],
        [WtfGlobal.getLocaleText("hrms.July")],
        [WtfGlobal.getLocaleText("hrms.August")],
        [WtfGlobal.getLocaleText("hrms.September")],
        [WtfGlobal.getLocaleText("hrms.October")],
        [WtfGlobal.getLocaleText("hrms.November")],
        [WtfGlobal.getLocaleText("hrms.December")]
        ];
        this.monthcombostore=new Wtf.data.SimpleStore({
            fields:[{
                name:'cname'
            }]
        });
        this.monthcombostore.loadData(this.cnamedata);
        this.MonthName= new Wtf.form.ComboBox({
            store:this.monthcombostore,
            displayField:'cname',
            mode: 'local',
            scope:this,
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.CName.Select"),
            selectOnFocus:true,
            width:120,
            minWidth:110,
            height:200,
            triggerAction :'all',
            listeners :{
                scope:this,
                select:function(MonthName,rec,i){
                    this.month=this.monthcombostore.getAt(i).get('cname');
                }
            }
        });
        this.usersRecG=new Wtf.data.Record.create([
        {
            name:'TempID'
        },{
            name:'id'
        },{
            name:'GroupName'
        },
        {
            name:'TempName'
        },
        {
            name:'payinterval'
        },
        {
            name:'effdate'
        }
        ]);
        this.groupuserds =  new Wtf.data.GroupingStore({
            url : "Payroll/Template/getPayProcessData.py",
            baseParams: {
                PayProc:'get'
            },
            reader: new Wtf.data.KwlJsonReader({
                root:'data',
                totalProperty:'totalcount'
            },this.usersRecG),
            autoLoad : false,
            sortInfo:{
                field: 'TempName',
                direction: "DSC"
            },
            groupField:'GroupName'
        });
        this.paycyclerec=new Wtf.data.Record.create([
        {
            name:'TempID'
        },{
            name:'paycyclestart'
        },{
            name:'paycycleend'
        },{
            name:'paycycleactualend'
        },{
            name:'paycycleshowstart'
        },{
            name:'paycycleshowend'
        },{
            name:'paycycletemplate'
        }
        ]);
        this.groupuserds.on("load", function(a,b,c){
            this.lastlimitoption = c.params.limit;
        }, this);
        this.emptempds =  new Wtf.data.GroupingStore({
            url:"Payroll/Template/getuserpaycycle.py",
             reader: new Wtf.data.KwlJsonReader({
                root:'data',
                totalProperty:'totalcount'
            }, this.paycyclerec)
        });
        this.on("render",function(){
            this.groupuserds.load({
                params:{
                    start:0,
                    limit:15
                }
            });
        });

//        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.rowNo=new Wtf.grid.RowNumberer();
        this.paycyclesm = new Wtf.grid.CheckboxSelectionModel({
        	singleSelect:true
        });
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
                singleSelect:true,
                scope:this,
                listeners:{
                    scope:this,
                    selectionchange :function(sm,index,record){
        				if(this.selectionModel.getCount()==0){
        					if(this.emptempdriven.getValue()){
        						this.emptempds.removeAll();
        						this.payintervalgrid.getView().emptyText = WtfGlobal.emptyGridRenderer( WtfGlobal.getLocaleText("hrms.common.SelecttheTemplate"));
        						this.payintervalgrid.getView().refresh();
        						this.quickSearchEmpField.disable();
        		                this.dateComp.disable();
        					}else{
        						this.emptempds.removeAll();
        						this.payintervalgrid.getView().emptyText = WtfGlobal.emptyGridRenderer( WtfGlobal.getLocaleText("hrms.common.SelecttheEmployee"));
        						this.payintervalgrid.getView().refresh();
        					}
        					return;
        				}
                        var grid = sm.grid;
                        var cell = grid.view.getHeaderCell(1);
                        var hd = Wtf.fly(cell).child('.x-grid3-hd-checker');

                        if (sm.getCount() === grid.getStore().getCount()) {
                            hd.addClass('x-grid3-hd-checker-on');
                        } else {
                            hd.removeClass('x-grid3-hd-checker-on');
                        }
                    }}});
        this.gridcm= new Wtf.grid.ColumnModel([this.rowNo,this.selectionModel,
        {
            id:'GroupName',
            header: WtfGlobal.getLocaleText("hrms.payroll.payrolltemplates"),
            width: 10,
            sortable: true,
            hidden:true,
            dataIndex: 'GroupName'
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.payrolltemplates"),
            width: 10,
            sortable: true,
            dataIndex: 'TempName'
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.payinterval"),
            width: 10,
            sortable: true,
            dataIndex: 'payinterval',
            renderer:function(val){
                if(val==1)
                    return WtfGlobal.getLocaleText("hrms.payroll.onceamonth");
                else if(val==3)
                    return WtfGlobal.getLocaleText("hrms.payroll.twiceamonth");
                else if(val==2)
                    return WtfGlobal.getLocaleText("hrms.payroll.onceaweek");
            }
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.PayDate"),
            width: 10,
            sortable: true,
            dataIndex: 'effdate',
            renderer:function(val,meta,rec){
                if(rec.get("payinterval")==3){
                    if(val==1)
                        return WtfGlobal.getLocaleText("hrms.Sunday");
                    else if(val==2)
                        return WtfGlobal.getLocaleText("hrms.Monday");
                    else if(val==3)
                        return  WtfGlobal.getLocaleText("hrms.Tuesday");
                    else if(val==4)
                        return WtfGlobal.getLocaleText("hrms.Wednesday");
                    else if(val==5)
                        return WtfGlobal.getLocaleText("hrms.Thursday");
                    else if(val==6)
                        return WtfGlobal.getLocaleText("hrms.Friday");
                    else if(val==7)
                        return WtfGlobal.getLocaleText("hrms.Saturday");

                }else{
                    return val;
                }
            }
        }],this);

       this.paycyclecm= new Wtf.grid.ColumnModel([this.rowNo,this.selectionModel,
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.PayCycleStart"),
            sortable: false,
            dataIndex: 'paycycleshowstart'
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.PayCycleEnd"),
            sortable: false,
            dataIndex: 'paycycleshowend'
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Template"),
            sortable: false,
            dataIndex: 'paycycletemplate'
        }],this);

        var tbartempbtns=[];

        this.grouptempgrid = new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.search"),
            searchField:"GroupName",
            serverSideSearch:true,
            view: new Wtf.grid.GroupingView({
                forceFit: true,
                showGroupName:false,
                groupTextTpl: '{text}',
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.NoTemplateaddedtillnow"))
            }),
            store: this.groupuserds,
            cm: this.gridcm,
            scope:this,
            border :true,
            sm:this.selectionModel
        });

        this.selectionModel.on('selectionchange',this.calculatepaycycle,this);

        this.payintervalgrid = new Wtf.grid.GridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            loadMask:true,
            view: new Wtf.grid.GroupingView({
                forceFit: true,
                showGroupName:false,
                groupTextTpl: '{text}',
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.Notemplateassignedtothisemployee"))
            }),
            store: this.emptempds,
            cm: this.paycyclecm,
            scope:this,
            border :true,
            sm:this.paycyclesm,
            tbar:tbartempbtns
        });
        this.paycyclesm.on('selectionchange',this.getemployees,this);

        var emptemprec = new Wtf.data.Record.create([
            {
                name: 'name'
            },{
                name:'id'
            }

        ]);

        this.emptempdriven = new Wtf.form.Radio({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.TemplateDriven"),
            id:'tempdriven'+this.id,
            bodyStyle:"float:left",
            checked:true,
            name:'emptemp'
        });

        this.empdriven = new Wtf.form.Radio({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.EmployeeDriven"),
            id:'empdriven'+this.id,
            bodyStyle:"float:left",
            name:'emptemp'
        });
        this.quickSearchEmpField = new Wtf.form.TextField({
            anchor : '99%',
            width: 150,
            emptyText: WtfGlobal.getLocaleText("hrms.common.grid.search.msg")
        });
        this.timer = new Wtf.util.DelayedTask(this.onKeyUpEvent),
        this.empdriven.on("check",function(a,b){
        	this.empgrid.emptext="";
        	this.empgrid.quickSearchTF.setValue("");
        	if(b){
                this.getemployeelist();
                this.quickSearchEmpField.disable();
                this.dateComp.disable();
                this.grouptempgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.NoEmployeeaddedtillnow"));
                this.paycyclecm.setHidden(4,false);
            }
        },this);
       this.emptempdriven.on("check",function(a,b){
    	   this.grouptempgrid.emptext="";
       	   this.grouptempgrid.quickSearchTF.setValue("");
    	   if(b){
                this.getTemplatelist();
                this.extrausergrid.hide();
                Wtf.getCmp('gridcontainerpanel').doLayout();
                Wtf.getCmp('as').doLayout();
                this.quickSearchEmpField.enable();
                this.dateComp.enable();
                this.grouptempgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.NoTemplateaddedtillnow"));
                this.paycyclecm.setHidden(4,true);
           }
        },this);
        this.empdriven.on("check",function(a,b){
        	if(this.empdriven.rendered && this.emptempdriven.rendered){
                this.empdriven.onClick();
                this.emptempdriven.onClick();
                this.emptempds.removeAll();
                this.payintervalgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.SelecttheEmployee"));
                this.payintervalgrid.getView().refresh();
            }
        },this);
        this.emptempdriven.on("check",function(a,b){
        	if(this.emptempdriven.rendered && this.empdriven.rendered){
                this.emptempdriven.onClick();
                this.empdriven.onClick();
                this.emptempds.removeAll();
                this.payintervalgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.SelecttheTemplate"));
                this.payintervalgrid.getView().refresh();
                this.quickSearchEmpField.disable();
                this.dateComp.disable();
            }
        },this);

        this.emptempfield=new Wtf.Panel({
            frame:false,
            border:false,
            layout:'column',
            items:[
            {
                frame:false,
                columnWidth:0.5,
                border:false,
                layout:'form',
                items:[this.emptempdriven]
            },
            {
                frame:false,
                border:false,
                columnWidth:0.5,
                layout:'form',
                items:[this.empdriven]
            }]
        });

        var intervalrec = new Wtf.data.Record.create([
            {
                name: 'name'
            },{
                name:'id'
            }

        ]);

        var intervalreader= new Wtf.data.ArrayReader({
            }, intervalrec);
        var intervaldata = [[WtfGlobal.getLocaleText("hrms.payroll.onceamonth"),1],[WtfGlobal.getLocaleText("hrms.payroll.onceaweek"),3]]
        var intervalStore = new Wtf.data.Store({
            reader:intervalreader,
            data:intervaldata
        });

         this.payintervalCombo = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.PaymentInterval"),
            mode:'local',
            displayField:'name',
            valueField:'id',
            allowBlank:false,
            name:'payinterval',
            typeAhead:true,
            hiddenName:'payinterval',
            triggerAction:'all',
            store:intervalStore,
            listeners:{
                scope:this,
                select:function(){
                    if(this.payintervalCombo.getValue()==1){
                        this.showhide(this.datepicker,true,"block");
                        this.showhide(this.daypicker,false,"none");
                    }else if(this.payintervalCombo.getValue()==3){
                        this.showhide(this.datepicker,false,"none");
                        this.showhide(this.daypicker,true,"block");
                    }
                }
            }
        });
        this.datestore = new Wtf.data.SimpleStore({
            fields :['id', 'name'],
            data:[['1','1st'],['2','2nd'],['3','3rd'],['4','4th'],['5','5th'],['6','6th'],['7','7th'],['8','8th'],
                ['9','9th'],['10','10th'],['11','11th'],['12','12th'],['13','13th'],['14','14th'],['15','15th'],['16','16th'],['17','17th'],['18','18th'],
                ['19','19th'],['20','20th'],['21','21th'],['22','22th'],['23','23th'],['24','24th'],['25','25th'],['26','26th'],
                ['27','27th'],['28','28th'],['29','29th'],['30','30th'],['31','31th']]
        });
        this.daystore = new Wtf.data.SimpleStore({
            fields :['id', 'name'],
            data:[ ['1',WtfGlobal.getLocaleText("hrms.Sunday")],
                    ['2',WtfGlobal.getLocaleText("hrms.Monday")],
                    ['3',WtfGlobal.getLocaleText("hrms.Tuesday")],
                    ['4',WtfGlobal.getLocaleText("hrms.Wednesday")],
                    ['5',WtfGlobal.getLocaleText("hrms.Thursday")],
                    ['6',WtfGlobal.getLocaleText("hrms.Friday")],
                    ['7',WtfGlobal.getLocaleText("hrms.Saturday")]]
        });
        this.datepicker = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.Date"),
            store:this.datestore,
            name:'date',
            displayField:'name',
            valueField:'id',
            mode:'local',
            triggerAction:'all',
            hidden:true,
            hideLabel:true
        })

        this.daypicker = new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.day"),
            store:this.daystore,
            name:'date',
            displayField:'name',
            valueField:'id',
            mode:'local',
            triggerAction:'all',
            hidden:true,
            hideLabel:true
        })
        this.filternorth = new Wtf.Panel({
            layout:'form',
            labelWidth:120,
            bodyStyle:'padding:10px;',
            border:false,
            items:[this.emptempfield,/*this.payintervalCombo,*/this.datepicker,this.daypicker]
        })

        this.filterpanel = new Wtf.Panel({
            region:'west',
            width:400,
            layout:'border',
            bodyStyle:"background-color: rgb(241, 241, 241)",
            border:false,
            items:[{
                region:'north',
                layout:'fit',
                border:false,
                height:40,
                items:this.filternorth
            },{
                region:'center',
                layout:'fit',
                border:false,
                items:this.grouptempgrid
            },{
                region:'south',
                layout:'fit',
                border:false,
                split:true,
                height:350,
                items:this.payintervalgrid
            }]
        })
        this.interjobRecord=Wtf.data.Record.create([
        {
            name:'EName'
        },
        {
            name: 'Wage'
        },
        {
            name: 'AccNo'
        },
        {
            name: 'Tax'
        },
        {
            name: 'Deduc'
        },{
            name: 'Salary'
        },
        {
            name: 'FixedSal'
        },
        {
            name:'empid'
        },
        {
            name:'design'
        },
        {
            name:'tempid'
        },
        {
            name:'showborder'
        },
        {
            name:'generated'
        },
        {
            name:'paycycleend'
        },
        {
            name:'mappingid'
        },
        {
        	name:'salaryGenerated'
        }
        ]);
        this.empstore = new Wtf.data.Store({
            scope:this,
            id:'empStore',
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.interjobRecord),
            url: "Emp/getEmpListPerGroupid.py"
        });
        this.emptempstore = new Wtf.data.Store({
            scope:this,
            id:'emptempStore',
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.interjobRecord)
        });

        this.empstore.on("load",function(){
            //if(msgFlag==1){
                WtfGlobal.closeProgressbar();
            //}
            var paysetComp = Wtf.getCmp('gridcontainerpanel');
            if(this.empdriven.getValue()){
                this.emptempstore.removeAll();
                this.extrausergrid.show();
                paysetComp.doLayout();
                Wtf.getCmp('as').doLayout();
                for(var i=0;i<this.empstore.data.length;i++){
                     if(this.empstore.getAt(i).data.empid == this.selectedEmp){

                     } else {
                          this.emptempstore.add(this.empstore.getAt(i));
                          this.empstore.remove(this.empstore.getAt(i));
                          i--;
                     }
                  }
            }
        },this);
        this.empstore.on("loadexception",function(){
            //if(msgFlag==1){
                WtfGlobal.closeProgressbar();
           // }
        },this);

        this.empsm=new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false,
            scope:this,
            listeners:{
                scope:this,
                rowselect :function(sm2,index,record){
                    this.TempId=this.empstore.getAt(index).get("tempid");
                    this.ename=this.empstore.getAt(index).get("EName");
                    this.accno=this.empstore.getAt(index).get("AccNo");
                    this.salary=this.empstore.getAt(index).get("Wage");
                    this.tax=this.empstore.getAt(index).get("Tax");
                    this.empid=this.empstore.getAt(index).get("empid");
                    this.deduc=this.empstore.getAt(index).get("Deduc");
                    this.currency=this.empstore.getAt(index).get("tempid");
                    this.fixedsal=this.empstore.getAt(index).get("FixedSal");
                    this.design=this.empstore.getAt(index).get("design");
                    this.selectedEmpGrid=this.empgrid;
                },
                rowdeselect:function(selectionmodal,index,record){
                },
                selectionchange :function(sm,index,record){
                        var grid = sm.grid;
                        var cell = grid.view.getHeaderCell(0);
                        var hd = Wtf.fly(cell).child('.x-grid3-hd-checker');

                        if (sm.getCount() === grid.getStore().getCount()) {
                            hd.addClass('x-grid3-hd-checker-on');
                        } else {
                            hd.removeClass('x-grid3-hd-checker-on');
                        }
                    }
            }
        });
        this.emptempsm=new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false,
            scope:this,
            listeners:{
                scope:this,
                rowselect :function(sm2,index,record){
                    this.TempId=this.emptempstore.getAt(index).get("tempid");
                    this.ename=this.emptempstore.getAt(index).get("EName");
                    this.accno=this.emptempstore.getAt(index).get("AccNo");
                    this.salary=this.emptempstore.getAt(index).get("Wage");
                    this.tax=this.emptempstore.getAt(index).get("Tax");
                    this.empid=this.emptempstore.getAt(index).get("empid");
                    this.deduc=this.emptempstore.getAt(index).get("Deduc");
                    this.currency=this.emptempstore.getAt(index).get("tempid");
                    this.fixedsal=this.emptempstore.getAt(index).get("FixedSal");
                    this.design=this.emptempstore.getAt(index).get("design");
                    this.selectedEmpGrid=this.extrausergrid;
                },
                rowdeselect:function(selectionmodal,index,record){
                },
                selectionchange :function(sm,index,record){

                        var grid = sm.grid;
                        var cell = grid.view.getHeaderCell(0);
                        var hd = Wtf.fly(cell).child('.x-grid3-hd-checker');

                        if (sm.getCount() === grid.getStore().getCount()) {
                            hd.addClass('x-grid3-hd-checker-on');
                        } else {
                            hd.removeClass('x-grid3-hd-checker-on');
                        }
                    }
            }
        });
        this.cm = new Wtf.grid.ColumnModel(
            [
            this.empsm,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'EName',
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.Earnings"),
                dataIndex: 'Wage',
                sortable: true,
                align:'right',
                groupable: true,
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.Deductions"),
                dataIndex: 'Deduc',
                sortable: true,
                align:'right',
                groupable: true,
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.Taxes"),
                dataIndex: 'Tax',
                sortable: true,
                align:'right',
                groupable: true,
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.netpay"),
                dataIndex: 'Salary',
                scope:this,
                sortable: true,
                align:'right',
                groupable: true,
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.common.delete"),
                width: 7,
                align: 'center',
                renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                    return "<div style='width:30px;' class='pwndCommon deletecolIcon' >&nbsp;</div>";
                }
            }
            ]);

        this.fromdateempG=new Wtf.form.DateField({
            width:135,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getFirstDateOfMonth(),
            maxValue:new Date().clearTime(true)
        });
        this.todateempG= new Wtf.form.DateField({
            width:135,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            disabled:true,
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getLastDateOfMonth()
        });
        this.dateComp=new Wtf.form.DateField({
            width:155,
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.SelectDate")
        });
        tbartempbtns.push(this.quickSearchEmpField,'-',this.dateComp);
        this.fromdateempG.on('change',function(){
            var myDate=new Date();
            myDate=this.fromdateempG.getValue();
            this.todateempG.setValue(myDate.add(Date.MONTH,+0).getLastDateOfMonth());
        },this);
        this.mypdf=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip"),
            scope:this,
            disabled : true,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip.tooltip2"),
            handler : function(){
                var rec=this.selectedEmpGrid.getSelectionModel().getSelections();
                var docname=rec[0].get('EName')+"_"+this.paycyclestart+"";
                this.exportReport(1, "pdf", docname,rec[0].get('empid'),companyName,rec);
                this.empid=null;
            }
        });
        this.myprint=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip"),
            scope:this,
            disabled : true,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip.tooltip"),
            handler : function(){
                var rec=this.selectedEmpGrid.getSelectionModel().getSelections();
                var docname=rec[0].get('EName')+"_"+this.paycyclestart+"";
                this.exportReport(1, "print", docname,rec[0].get('empid'),companyName,rec);
                this.empid=null;
            }
        });
        this.empsaldetails=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails"),
            scope:this,
            minWidth:90,
            disabled : true,
            iconCls:getButtonIconCls(Wtf.btype.reportbutton),
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails.tooltip2"),
            handler:function(){
                this.arr=this.empgrid.getSelections();
                this.arrExtraUser=this.extrausergrid.getSelections();
                this.arr1=this.payintervalgrid.getSelections();
                if(this.arr.length==0 && this.arrExtraUser.length==0)
                {
                    calMsgBoxShow(21,0);
                }
                else
                {
                    if(this.arr.length==0){
                        var arr = this.arrExtraUser;
                    } else {
                        var arr = this.arr;
                    }
                    this.TempId=arr[0].get("tempid");
                    this.showBorder=arr[0].get("showborder");
                    this.ename=arr[0].get("EName");
                    this.accno=arr[0].get("AccNo");
                    this.salary=arr[0].get("Wage");
                    this.tax=arr[0].get("Tax");
                    this.empid=arr[0].get("empid");
                    this.deduc=arr[0].get("Deduc");
                    this.currency=arr[0].get("tempid");
                    this.fixedsal=arr[0].get("FixedSal");
                    this.design=arr[0].get("design");
                    var generated=arr[0].get("generated");
                    this.mainTabId = Wtf.getCmp("as");
                    var sdate = new Date(this.arr1[0].get("paycyclestart").replace(new RegExp("-","g"),"/"));
                    var edate = new Date(this.arr1[0].get("paycycleend").replace(new RegExp("-","g"),"/"));
                    this.payslip = Wtf.getCmp(this.empid+"payslipTab"+sdate);
                    if(this.payslip == null){
                        this.payslip = new Wtf.EmpPayslip({
                            layout:"fit",
                            title: WtfGlobal.getLocaleText({key:"hrms.payroll.employeespayslip",params:[arr[0].get('EName')]}),
                            closable:true,
                            border:false,
                            iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                            id:this.empid+"payslipTab"+sdate,
                            TempId:this.TempId,
                            showBorder:this.showBorder,
                            ename:this.ename,
                            mappingid:arr[0].get('mappingid'),
                            accno:this.accno,
                            salary:this.salary,
                            tax:this.tax,
                            empid:this.empid,
                            deduc:this.deduc,
                            cursymbol:this.currency,
                            fixedsal:this.fixedsal,
                            generated:generated,
                            netSalary:arr[0].get("Salary"),
                            salaryGenerated:arr[0].get("salaryGenerated"),
                            design:this.design,
                            stdate1:this.fromdateempG.getValue(),
                            enddate1:this.todateempG.getValue(),
                            fromdateempG:this.fromdateempG,
                            todateempG:this.todateempG,
                            emptempstore: this.emptempstore,
                            empstore: this.empstore,
                            stdate:new Date(this.arr1[0].get("paycyclestart").replace(new RegExp("-","g"),"/")),
                            enddate:new Date(this.arr1[0].get("paycycleend").replace(new RegExp("-","g"),"/")),
                            paycyclestart:this.paycyclestart,
                            paycycleend:(arr[0].get('paycycleend')=="")?this.paycycleend:arr[0].get('paycycleend'),
                            ischanged:(arr[0].get('paycycleend')=="")?0:1,
                            arr:arr
                        });
                        this.mainTabId.add(this.payslip);
                        this.payslip.on('gridload',function(){
                            calMsgBoxShow(202,4,true);
                            this.empstore.load();
                        },this);
                    }
                    this.mainTabId.setActiveTab(this.payslip);
                    this.mainTabId.doLayout();
                }
            }

        });
        var empbtns=[];
        empbtns.push('->','-',this.myprint,'-',this.mypdf,'-',this.empsaldetails);
        var tbarempbtns=[];
        var genSalaryText = WtfGlobal.getLocaleText("hrms.payroll.GenerateSalary");
        if(Wtf.cmpPref.approvesalary!=undefined && Wtf.cmpPref.approvesalary===true){
            genSalaryText = WtfGlobal.getLocaleText("hrms.payroll.send.for.authorization");
        }
        tbarempbtns.push('->',this.gensalbtn=new Wtf.Button({
                text:genSalaryText,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.GenerateSalary.tooltip"),
                scope:this,
                minWidth:100,
                disabled:true,
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                handler:function(){
        			this.arr=this.empgrid.getSelectionModel().getSelections();
        			this.extraarr=this.extrausergrid.getSelectionModel().getSelections();
		        	if(isStandAlone){
		        		this.getUnpaidleaves();
		        	}else{
		        		calMsgBoxShow(202,4,true);
			           	var json = new Array();
			            for(var i=0; i<this.arr.length; i++){
			            	var data = {};
			            	data['userid'] = this.arr[i].data.empid;
			            	data['fromDate'] = this.paycyclestart;
			            	data['toDate'] = this.paycycleend;
			            	json.push(data);
			            }
			            
			            for(var i=0; i<this.extraarr.length; i++){
			            	var data = {};
			            	data['userid'] = this.extraarr[i].data.empid;
			            	data['fromDate'] = this.paycyclestart;
			            	data['toDate'] = this.paycycleend;
			            	json.push(data);
			            }
			            
			            Wtf.Ajax.requestEx({
			                url: "Emp/getLeavesFromEleaves.py" ,
			                scope:this,
			                method:'post',
			                params:{
			            		jsondata:Wtf.encode(json)
			                }
			            },this,
			            function(request){
			            	var req = eval('('+request+')');
			            	this.getUnpaidleaves(req);			            	
			            	WtfGlobal.closeProgressbar();
			            },
			            function(response){
			            	WtfGlobal.closeProgressbar();
			            });
		        	}
        		}
            }));
         this.extrausergrid=new Wtf.grid.GridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            id:'incometaxgridpanel',
            region:'south',
            height:250,
            loadMask:true,
            displayInfo:true,
            searchField:"rate",
            viewConfig: {
                forceFit: true
            },
            store: this.emptempstore,
            cm: this.cm,
            border :true,
            sm:this.emptempsm,
            tbar:[WtfGlobal.getLocaleText("hrms.payroll.OthersEmployeefortheselectedpaycycle")]
        });
        this.empgrid= new Wtf.KwlGridPanel({
            border: true,
            region:'center',
            store: this.empstore,
            cm: this.cm,
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            loadMask:true,
            displayInfo:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            searchField:"EName",
            serverSideSearch:true,
            paging:false,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.SelectthePayCycle"))
            },
            sm:this.empsm ,
            width:700,
            tbar:tbarempbtns
        });
        this.gridcontainerpanel = new Wtf.Panel({
            region:'center',
            layout: 'border',
            id: 'gridcontainerpanel',
            bbar:empbtns,
            items:[this.empgrid, this.extrausergrid]
        })
        this.extrausergrid.hide();
        this.empsm.on("selectionchange",function(){
            this.customEnableDisable(empbtns, this.empgrid, [6], []);
            this.customEnableDisable(tbarempbtns, this.empgrid, [], [1]);
            if(this.empsm.hasSelection()){
            var rec=this.empsm.getSelections();
            var rec1=this.emptempsm.getSelections();
            if((rec.length+rec1.length)>1){
               this.mypdf.disable();
               this.myprint.disable();
            }else{
                if(rec[0].get('generated')=='1'){
                    this.mypdf.enable();
                    this.myprint.enable();
                }else{
                    this.mypdf.disable();
                    this.myprint.disable();
                }
            }
            }else{
                 this.mypdf.disable();
                 this.myprint.disable();
            }
        },this);
        this.emptempsm.on("selectionchange",function(){
            this.customEnableDisable(empbtns, this.extrausergrid, [6], []);
            this.customEnableDisable(tbarempbtns, this.extrausergrid, [], [1]);

            if(this.emptempsm.hasSelection()){
            var rec1=this.empsm.getSelections();
            var rec=this.emptempsm.getSelections();
            if((rec.length+rec1.length)>1){
               this.mypdf.disable();
               this.myprint.disable();
            }else{
                if(rec[0].get('generated')=='1'){
                    this.mypdf.enable();
                    this.myprint.enable();
                }else{
                    this.mypdf.disable();
                    this.myprint.disable();
                }
            }
            }else{
                 this.mypdf.disable();
                 this.myprint.disable();
            }
        },this);
        this.empgrid.on("cellclick", this.deletePayslip, this);
        this.extrausergrid.on("cellclick", this.deletePayslip, this);
        this.MainDataEntryPanelT=new Wtf.Panel({
            title:this.tempName,
            border:false,
            layout:'border',
            bodyStyle:'background:white',
            scope:this,
            items:[this.filterpanel, this.gridcontainerpanel]//[this.AddEditForm,this.pan],
        });
        this.add(this.MainDataEntryPanelT);
        this.doLayout();
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        this.fromdateempG.on('change',function(){
            var select=this.grouptempgrid.getSelectionModel();
            if(select.hasSelection()){
             calMsgBoxShow(202,4,true);
             var rec=select.getSelections();

            this.tempids=[];
            for(i=0;i<this.templates.length;i++){
                this.tempids.push(this.templates[i].get('TempID'));
            }
            this.empstore.baseParams={
                groupid:this.tempids,
                stdate:this.fromdateempG.getRawValue(),
                enddate:this.todateempG.getRawValue()
            }
            this.empstore.load();
        }
        },this);
        this.dateComp.on('change',function(){
            this.calculatepaycycle();
        }, this);
    },
    customEnableDisable:function(btnArr,grid,singleSelectArr,multiSelectArr){
        var multi = !(this.extrausergrid.getSelectionModel().hasSelection() || this.empgrid.getSelectionModel().hasSelection());
        var single = ((this.extrausergrid.getSelectionModel().getCount()+this.empgrid.getSelectionModel().getCount())!=1);
        for(var i=0;i<multiSelectArr.length;i++)
            btnArr[multiSelectArr[i]].setDisabled(multi);
        for(i=0;i<singleSelectArr.length;i++)
            btnArr[singleSelectArr[i]].setDisabled(single);
    },
    onKeyUpEvent: function(e){
        if (this.quickSearchEmpField.getValue() != "") {
            this.timer.cancel();
            this.timer.delay(1000,this.onKeyUpHandler,this);
        }
    },
    onKeyUpHandler: function(){
        var chdate = this.emptempds.getAt(0).data.paycyclestart;
            var re=this.grouptempgrid.getSelectionModel().getSelections();
            Wtf.Ajax.requestEx({
                url: "Emp/getEmpCycle.py",
                scope:this,
                method:'post',
                params:{
                    cyclestartdate:chdate,
                    cycleenddate:chdate,
                    ss:this.quickSearchEmpField.getValue(),
                    groupid:re[0].get('TempID')
                }
            },this,
            function(req){
                var resp=req.effdate;
                if(resp!=""){

                    var respdate = new Date(resp.replace(new RegExp("-","g"),"/"))
                    for(var k=this.emptempds.data.length-1; k>=0;k--){
                        var griddate = new Date(this.emptempds.getAt(k).data.paycyclestart.replace(new RegExp("-","g"),"/"));
                        if(respdate <= griddate){
                            this.paycyclesm.selectRow(k);
                            break;
                        }
                    }
                }
            },
            function(req){

            });
    },
    getempsalaries: function(){
            if(this.arr.length==0 && this.extraarr.length==0)
            {
                calMsgBoxShow(19,0);
            }
            else{
                this.jsondata = "";
                var vflag=0;
                for(i=0;i<this.arr.length;i++)
                {
                    var paycycleend = this.paycycleend;
                    var ischanged = 0;
                    if(this.arr[i].get('paycycleend')!=null&&this.arr[i].get('paycycleend')!=undefined&&this.arr[i].get('paycycleend')!=""){
                        paycycleend = this.arr[i].get('paycycleend');
                        ischanged = 1;
                    }
                    this.jsondata += "{'EName':'" + this.arr[i].get('EName')+ "',";
                    this.jsondata += "'AccNo':'" +this.arr[i].get('AccNo')+ "',";
                    this.jsondata += "'Wage':'" +this.arr[i].get('Wage')+ "',";
                    this.jsondata += "'FixedSal':'" +this.arr[i].get('FixedSal')+ "',";
                    this.jsondata += "'Tax':'" +this.arr[i].get('Tax')+ "',";
                    this.jsondata += "'Deduc':'" +this.arr[i].get('Deduc')+ "',";
                    this.jsondata += "'mappingid':'" +this.arr[i].get('mappingid')+ "',";
                    this.jsondata += "'empid':'" +this.arr[i].get('empid')+ "',";
                    this.jsondata+= "'design':'" +this.arr[i].get('design')+ "',";
                    this.jsondata+= "'paycyclestart':'" +this.paycyclestart+ "',";
                    this.jsondata+= "'paycycleend':'" +paycycleend+ "',";
                    this.jsondata+= "'ischanged':'" +ischanged+ "',";
                    this.jsondata+= "'template':'" +this.arr[i].get('tempid')+ "'},";
                    if(this.arr[i].get('Salary')<0){
                        vflag=1;
                        break;
                    }
                }
                for(i=0;i<this.extraarr.length;i++){
                    paycycleend = this.paycycleend;
                    ischanged = 0;
                    if(this.extraarr[i].get('paycycleend')!=null&&this.extraarr[i].get('paycycleend')!=undefined&&this.extraarr[i].get('paycycleend')!=""){
                        paycycleend = this.extraarr[i].get('paycycleend');
                        ischanged = 1;
                    }
                    this.jsondata += "{'EName':'" + this.extraarr[i].get('EName')+ "',";
                    this.jsondata += "'AccNo':'" +this.extraarr[i].get('AccNo')+ "',";
                    this.jsondata += "'Wage':'" +this.extraarr[i].get('Wage')+ "',";
                    this.jsondata += "'FixedSal':'" +this.extraarr[i].get('FixedSal')+ "',";
                    this.jsondata += "'Tax':'" +this.extraarr[i].get('Tax')+ "',";
                    this.jsondata += "'Deduc':'" +this.extraarr[i].get('Deduc')+ "',";
                    this.jsondata += "'mappingid':'" +this.extraarr[i].get('mappingid')+ "',";
                    this.jsondata += "'empid':'" +this.extraarr[i].get('empid')+ "',";
                    this.jsondata+= "'design':'" +this.extraarr[i].get('design')+ "',";
                    this.jsondata+= "'paycyclestart':'" +this.paycyclestart+ "',";
                    this.jsondata+= "'paycycleend':'" +paycycleend+ "',";
                    this.jsondata+= "'ischanged':'" +ischanged+ "',";
                    this.jsondata+= "'template':'" +this.extraarr[i].get('tempid')+ "'},";
                    if(this.extraarr[i].get('Salary')<0){
                        vflag=1;
                        break;
                    }
                }
                this.trmLen1 =this.jsondata.length - 1;
                this.jsondata = this.jsondata.substr(0,this.trmLen1);
                if(vflag==0){
                    calMsgBoxShow(202,4,true);
                    Wtf.Ajax.requestEx({
                        url: "Emp/setPayrollforTemp.py" ,
                        scope:this,
                        method:'post',
                        params:{
                            save:'true',
                            saveType:'PayHistoryforTemp',//Create Payslip Per Template
                            jsondata:this.jsondata,
                            TempId:this.TempId,
                            stdate:this.fromdateempG.getRawValue(),
                            enddate:this.todateempG.getRawValue(),
                            action:1,//added for add/delete functionality
                            paycyclestart:this.paycyclestart,
                            paycycleend:this.paycycleend
                        }
                    },this,
                    function(req){
                        var resp=req.value.toString();
                        if(resp=="success"){
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),req.msg.toString()],0,false,250);
                            this.emptempstore.removeAll();
                            this.extrausergrid.selModel.clearSelections();
                            this.empstore.load();
                        }else if(resp=="failure"){
                            Wtf.MessageBox.alert( WtfGlobal.getLocaleText("hrms.common.warning"),req.msg.toString());
                        }

                    },
                    function(req){

                        }
                        );
                }else{
                    calMsgBoxShow(157,0);
                }
            }
    },
    getemployeelist:function(){
        this.groupuserds.proxy.conn.url="Payroll/Template/getuserlist.py";
        this.grouptempgrid.getColumnModel().setHidden(4, true);
        this.grouptempgrid.getColumnModel().setHidden(5, true);
        this.grouptempgrid.getColumnModel().setColumnHeader(3,  WtfGlobal.getLocaleText("hrms.common.employee.name"));
        this.groupuserds.reload({params:{
                    start:0,
                    limit:this.lastlimitoption
                }});
    },
    getTemplatelist:function(){
        this.groupuserds.proxy.conn.url="Payroll/Template/getPayProcessData.py";
        this.grouptempgrid.getColumnModel().setHidden(4, false);
        this.grouptempgrid.getColumnModel().setHidden(5, false);
        this.grouptempgrid.getColumnModel().setColumnHeader(3, WtfGlobal.getLocaleText("hrms.payroll.payrolltemplates"));
        this.groupuserds.reload({params:{
                    start:0,
                    limit:this.lastlimitoption
                }});
    },
    calculatepaycycle:function(){
    	if(this.emptempdriven.getValue() && this.selectionModel.getCount()>0){
    		this.quickSearchEmpField.enable();
    		this.dateComp.enable();
    	}
        this.payintervalgrid.selModel.clearSelections();
        this.empstore.removeAll();
        this.emptempstore.removeAll();
        this.payintervalgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.Notemplateassignedtothisemployee"));
        if(this.selectionModel.getSelections().length>0){
            if(this.emptempdriven.getValue()){
            	var selected = this.selectionModel.getSelected();
                this.emptempds.reload({params:{
                	templateid:selected.get('TempID'),
                	isTemplateDriven: true
                }});
            }else if(this.empdriven.getValue()){
                var selected = this.selectionModel.getSelected();
                this.selectedEmp = selected.get('id');
                this.emptempds.reload({params:{
                    userid:selected.get('id')
                }});
            }
        }
    },
    getemployees: function(){
            if(this.grouptempgrid.selModel.getCount()>0){
                if(this.payintervalgrid.selModel.getCount()>0){
                    this.empgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.Noemployeeassignedfortheselectedtemplateforthisduration"));
                    this.templates=this.payintervalgrid.selModel.getSelections();
                    this.tempids=[];
                    for(i=0;i<this.templates.length;i++){
                        this.tempids.push(this.templates[i].get('TempID'));
                    }
                    this.paycyclestart = "";
                    this.paycycleend = "";
                    if(this.paycyclesm.getCount()>0){
                        var selected = this.paycyclesm.getSelected();
                        this.paycyclestart = selected.get("paycyclestart");
                        this.paycycleend = selected.get("paycycleend");
                        this.paycycleactualend = selected.get("paycycleactualend");
                        }
                    if(this.paycyclestart!=""&&this.paycycleend!=""){
                        this.empstore.removeAll();
                        this.emptempstore.removeAll();
                        this.empstore.baseParams={
                            groupid:this.tempids,
                            stdate:this.fromdateempG.getRawValue(),
                            enddate:this.todateempG.getRawValue(),
                            paycyclestart:this.paycyclestart,
                            paycycleend:this.paycycleend
                        }
                        this.empstore.load();
                    }
                } else {
                    this.empstore.removeAll();
                    this.emptempstore.removeAll();
                    this.empgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.SelectthePayCycle"));
                    this.empgrid.getView().refresh();
                }
        } else {
            this.empgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.SelectthePayCycle"));
            this.empgrid.getView().refresh();
        }
    },
    exportReport: function(flag, exportType, docName,empid,cname,rec){
       var paycycleend = this.paycycleend;
       if(rec[0].get('paycycleend')!=null&&rec[0].get('paycycleend')!=undefined&&rec[0].get('paycycleend')!=""){
            paycycleend = rec[0].get('paycycleend');
       }
       var selIds = "";
        var colHeader = "{\"data\": []}";
        if (flag==1){
            colHeader = "{\"data\": [\"No\",\"From\", \"To\", \"Duration\", \"Reason\", "+
        "\"Type Of Leave\", \"Paid\", \"LPW\", \"Employee Signature\", \"Approver Signature\", \"Balance\"]}";
        }
        
        var controller = "";
        if(exportType=='print'){
        	controller = "Payroll/Date/Salary/printHTML.py?";
        }else{
        	controller = "Payroll/Date/Salary/exportPDF.py?";
        }
        
        var url =  controller +"&flag=" + flag +
        "&colHeader=" + colHeader+
        "&userIDs="+ selIds +
        "&reportname="+ docName +
        "&exporttype=" + exportType+
        "&empid="+empid+
        "&cname="+cname+
        "&showborder="+rec[0].get('showborder')+
        "&stdate="+this.paycyclestart+
        "&flagpdf="+"datewise"+
        "&cdomain="+subDomain+
        "&enddate="+paycycleend;
        setDldUrl(url, exportType);
    },
    deletePayslip: function(gd, ri, ci, e){
    if(ci==6) {
        var rec=gd.getSelectionModel().getSelections();
        var created=rec[0].get('generated');
        var paycycleend=(rec[0].get('paycycleend')=="")?this.paycycleend:rec[0].get('paycycleend');
        Wtf.MessageBox.show({
            title: WtfGlobal.getLocaleText("hrms.common.confirm"),
            msg:WtfGlobal.getLocaleText("hrms.payroll.Areyousureyouwanttodeletethesalarydetailsoftheemployee"),
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='no')
                {
                    return;
                } else {
                	var empid= new Array();
                    empid.push(rec[0].get('empid'));
                    
                    Wtf.Ajax.requestEx({
                        url: "Emp/deletePayslipDetails.py" ,
                        method:'post',
                        params:{
                            empid:Wtf.encode(empid),
                            enddate:(this.empdriven.getValue())?this.paycycleactualend:this.paycycleend,
                            startdate:this.paycyclestart
                        }
                    },
                    this,
                    function(response){
                        var res=eval('('+response+')');
                        var msg = res.msg;
                        if((res.msg == "No salary details found for this duration." ||res.msg == WtfGlobal.getLocaleText("hrms.payroll.Nosalarydetailsfoundforthisduration") ) && created == "1" && !this.empdriven.getValue()){
                            msg = WtfGlobal.getLocaleText("hrms.payroll.PleasedeletepayslipfromEmployeeDriven");
                        }
                        Wtf.Msg.show({
                            title:WtfGlobal.getLocaleText("hrms.common.success"),
                            msg: ""+msg+"",
                            scope:this,
                            width:300,
                            buttons: Wtf.Msg.OK,
                            fn: function(btn,value){
                                this.mypdf.disable();
                                this.myprint.disable();
                                calMsgBoxShow(202,4,true);
                                this.emptempstore.removeAll();
                                this.extrausergrid.selModel.clearSelections();
                                this.empstore.load();
                            },
                            animEl: 'elId',
                            icon:Wtf.MessageBox.INFO
                        });
                    },
                    function(req,res){
                        calMsgBoxShow(27,1);
                    }
                    );
                }
            }
        });
    }
},

getUnpaidleaves : function(req){
	

    
    var alertstr = WtfGlobal.getLocaleText("hrms.payroll.PayCycleenddatedoesnotmatchwiththeselctedPaycycle")+".<br><ol>"
    var toalert = false;
    var count = 1;
    for(var i=0;i<this.arr.length;i++) {
        if(this.arr[i].get('paycycleend')!=null&&this.arr[i].get('paycycleend')!=undefined&&this.arr[i].get('paycycleend')!=""){
            toalert = true;
            alertstr += "<li><b>"+count+". "+this.arr[i].get('EName') +"</b>: "+WtfGlobal.getLocaleText({key:"hrms.payroll.Paycyclextoy",params:[this.paycyclestart,this.arr[i].get('paycycleend')]})+"</li>";
            count++;
        }
    }
    for(i=0;i<this.extraarr.length;i++) {
        if(this.extraarr[i].get('paycycleend')!=null&&this.extraarr[i].get('paycycleend')!=undefined&&this.extraarr[i].get('paycycleend')!=""){
            toalert = true;
            alertstr += "<li><b>"+count+". "+this.extraarr[i].get('EName') +"</b>:"+WtfGlobal.getLocaleText({key:"hrms.payroll.Paycyclextoy",params:[this.paycyclestart,this.extraarr[i].get('paycycleend')]})+"</li>";
            count++;
        }
    }
    alertstr += WtfGlobal.getLocaleText("hrms.common.Doyouwanttocontinue")+"</ol>";
	
    var i=0;
    for( ; i<this.arr.length; i++){
    	if(req!=undefined && req.data!=undefined && req.data[i]!=undefined && req.data[i]!=""){
    		this.arr[i].data.unpaidleave=req.data[i].unpaidleave;
    	}else{
    		this.arr[i].data.unpaidleave=0;
    	}
    }
    
    for(var j=0; j<this.extraarr.length; j++){
    	if(req!=undefined && req.data!=undefined && req.data[i+j]!=undefined && req.data[i+j]!=""){
    		this.extraarr[j].data.unpaidleave=req.data[i+j].unpaidleave;
    	}else{
    		this.extraarr[j].data.unpaidleave=0;
    	}
    }
    
	if(toalert){
        Wtf.MessageBox.show({
            title: WtfGlobal.getLocaleText("hrms.common.alert"),
            msg:alertstr,
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            width:600,
            fn:function(button){
                if(button=='no'){
                    return;
                }else{
                	
                	
                    //this.getempsalaries();
                    var adjustLeaveWin = new Wtf.leavem.LeaveAdjustWindow({
                            title: WtfGlobal.getLocaleText("hrms.payroll.Unpaidleaves"),
                            height: 400,
                            width: 450,
                            modal: true,
                            resizable: false,
                            paycycleend: this.paycycleend,
                            emptempstore: this.emptempstore,
                            empstore: this.empstore,
                            paycyclestart: this.paycyclestart,
                            TempId:this.TempId,
                            fromdateempG: this.fromdateempG,
                            todateempG: this.todateempG,
                            layout: 'fit',
                            iconCls : getTabIconCls(Wtf.etype.iconwin),
                            bodyStyle: "background-color: #f1f1f1;",
                            arr: this.arr,
                            extraarr: this.extraarr
                        });
                        adjustLeaveWin.show();
                }
            }
        });
    }else{
        var adjustLeaveWin = new Wtf.leavem.LeaveAdjustWindow({
            title: WtfGlobal.getLocaleText("hrms.payroll.Unpaidleaves"),
            height: 400,
            width: 450,
            modal: true,
            resizable: false,
            paycycleend: this.paycycleend,
            emptempstore: this.emptempstore,
            empstore: this.empstore,
            paycyclestart: this.paycyclestart,
            TempId:this.TempId,
            fromdateempG: this.fromdateempG,
            todateempG: this.todateempG,
            layout: 'fit',
            iconCls : getTabIconCls(Wtf.etype.iconwin),
            bodyStyle: "background-color: #f1f1f1;",
            arr: this.arr,
            extraarr: this.extraarr
        });
        adjustLeaveWin.show();
    }
    
	}
});

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Wtf.leavem.LeaveAdjustWindow = function(config) {
    Wtf.apply(this, config);
    Wtf.leavem.LeaveAdjustWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.leavem.LeaveAdjustWindow, Wtf.Window, {
    initComponent: function() {
        Wtf.leavem.LeaveAdjustWindow.superclass.initComponent.call(this);

    },
    onRender: function(config) {
            Wtf.leavem.LeaveAdjustWindow.superclass.onRender.call(this, config);
            this.createLeavetypeAdjGrid();
            var formItems = Array();

            this.adjLeavesPanel	 = new Wtf.Panel({
			frame: true,
			border: false,
                        scope:this,
			layout: 'fit',
			items: [{
                                border:false,
                                layout:'border',
				items: [{
					region : 'north',
                                        height : 80,
                                        border : false,
                                        bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
					html: getTopHtml( WtfGlobal.getLocaleText("hrms.payroll.Unpaidleaves"),WtfGlobal.getLocaleText("hrms.payroll.Fillthenoofunpaidleavesforrespectiveusers"))
                                    },{
					border:false,
					region:'center',
					bodyStyle : 'background:#f1f1f1;font-size:10px;',
					layout:'fit',
					items: [this.typeGrid]
                                    }
                                ]
			}],
			buttonAlign: 'right',
			buttons:[{
				text: WtfGlobal.getLocaleText("hrms.common.submit"),
				handler: function(){
					this.getempsalaries();
				},
				scope: this
			},{
				text: WtfGlobal.getLocaleText("hrms.common.cancel"),
				handler: function(){
					this.close();
				},
				scope: this
			}]
        });
        this.add(this.adjLeavesPanel);
   },
   getempsalaries: function(){
            var approveSalFlag = Wtf.cmpPref.approvesalary?2:3;
            if(this.arr.length==0 && this.extraarr.length==0){
                calMsgBoxShow(19,0);
            } else {
                this.jsondata = "";
                var vflag=0;
                for(var i=0;i<this.arr.length;i++){
                    var paycycleend = this.paycycleend;
                    var ischanged = 0;
                    if(this.arr[i].get('paycycleend')!=null&&this.arr[i].get('paycycleend')!=undefined&&this.arr[i].get('paycycleend')!=""){
                        paycycleend = this.arr[i].get('paycycleend');
                        ischanged = 1;
                    }
                    this.jsondata += "{'EName':'" + this.arr[i].get('EName')+ "',";
                    this.jsondata += "'AccNo':'" +this.arr[i].get('AccNo')+ "',";
                    this.jsondata += "'Wage':'" +this.arr[i].get('Wage')+ "',";
                    this.jsondata += "'FixedSal':'" +this.arr[i].get('FixedSal')+ "',";
                    this.jsondata += "'Tax':'" +this.arr[i].get('Tax')+ "',";
                    this.jsondata += "'Deduc':'" +this.arr[i].get('Deduc')+ "',";
                    this.jsondata += "'mappingid':'" +this.arr[i].get('mappingid')+ "',";
                    this.jsondata += "'empid':'" +this.arr[i].get('empid')+ "',";
                    this.jsondata += "'design':'" +this.arr[i].get('design')+ "',";
                    this.jsondata += "'netSalary':'" +this.arr[i].get('Salary')+ "',";
                    this.jsondata += "'salaryGenerated':'" +this.arr[i].get('salaryGenerated')+ "',";
                    this.jsondata+= "'salarystatus':'" +approveSalFlag+ "',";
                    this.jsondata += "'paycyclestart':'" +this.paycyclestart+ "',";
                    this.jsondata += "'paycycleend':'" +paycycleend+ "',";
                    this.jsondata += "'ischanged':'" +ischanged+ "',";
                    if(this.arr[i].get('tempinterval') == "1"){
                        if(this.LeavetypeAdjStore.getAt(i).data.leaveAdj > 30){
                            vflag=1;
                            break;
                        }
                    } else if(this.arr[i].get('tempinterval') == "2"){
                        if(this.LeavetypeAdjStore.getAt(i).data.leaveAdj > 15){
                            vflag=1;
                            break;
                        }
                    } else if(this.arr[i].get('tempinterval') == "3"){
                        if(this.LeavetypeAdjStore.getAt(i).data.leaveAdj > 7){
                            vflag=1;
                            break;
                        }
                    }
                    this.jsondata += "'unpaidleaves':'" +this.LeavetypeAdjStore.getAt(i).data.leaveAdj+ "',";
                    this.jsondata += "'template':'" +this.arr[i].get('tempid')+ "'},";
                    if(this.arr[i].get('Salary')<0){
                        vflag=1;
                        break;
                    }
                }
                var totalleavecount = this.arr.length;
                for(i=0;i<this.extraarr.length;i++){
                    paycycleend = this.paycycleend;
                    ischanged = 0;
                    if(this.extraarr[i].get('paycycleend')!=null&&this.extraarr[i].get('paycycleend')!=undefined&&this.extraarr[i].get('paycycleend')!=""){
                        paycycleend = this.extraarr[i].get('paycycleend');
                        ischanged = 1;
                    }
                    this.jsondata += "{'EName':'" + this.extraarr[i].get('EName')+ "',";
                    this.jsondata += "'AccNo':'" +this.extraarr[i].get('AccNo')+ "',";
                    this.jsondata += "'Wage':'" +this.extraarr[i].get('Wage')+ "',";
                    this.jsondata += "'FixedSal':'" +this.extraarr[i].get('FixedSal')+ "',";
                    this.jsondata += "'Tax':'" +this.extraarr[i].get('Tax')+ "',";
                    this.jsondata += "'Deduc':'" +this.extraarr[i].get('Deduc')+ "',";
                    this.jsondata += "'mappingid':'" +this.extraarr[i].get('mappingid')+ "',";
                    this.jsondata += "'empid':'" +this.extraarr[i].get('empid')+ "',";
                    this.jsondata+= "'design':'" +this.extraarr[i].get('design')+ "',";
                    this.jsondata+= "'paycyclestart':'" +this.paycyclestart+ "',";
                    this.jsondata+= "'paycycleend':'" +paycycleend+ "',";
                    this.jsondata+= "'ischanged':'" +ischanged+ "',";
                    if(this.extraarr[i].get('tempinterval') == "1"){
                        if(this.LeavetypeAdjStore.getAt(i).data.leaveAdj > 30 || this.LeavetypeAdjStore.getAt(i).data.leaveAdj < 0){
                            vflag=1;
                            break;
                        }
                    } else if(this.extraarr[i].get('tempinterval') == "2"){
                        if(this.LeavetypeAdjStore.getAt(i).data.leaveAdj > 15){
                            vflag=1;
                            break;
                        }
                    } else if(this.extraarr[i].get('tempinterval') == "3"){
                        if(this.LeavetypeAdjStore.getAt(i).data.leaveAdj > 7){
                            vflag=1;
                            break;
                        }
                    }
                    this.jsondata += "'unpaidleaves':'" +this.LeavetypeAdjStore.getAt(totalleavecount+i).data.leaveAdj+ "',";
                    this.jsondata+= "'template':'" +this.extraarr[i].get('tempid')+ "'},";
                    if(this.extraarr[i].get('Salary')<0){
                        vflag=1;
                        break;
                    }
                }
                this.trmLen1 =this.jsondata.length - 1;
                this.jsondata = this.jsondata.substr(0,this.trmLen1);
                if(vflag==0){
                    calMsgBoxShow(202,4,true);
                    Wtf.Ajax.requestEx({
                        url: "Emp/setPayrollforTemp.py" ,
                        scope:this,
                        method:'post',
                        params:{
                            save:'true',
                            saveType:'PayHistoryforTemp',//Create Payslip Per Template
                            jsondata:this.jsondata,
                            TempId:this.TempId,
                            stdate:this.fromdateempG.getRawValue(),
                            enddate:this.todateempG.getRawValue(),
                            action:1,//added for add/delete functionality
                            paycyclestart:this.paycyclestart,
                            paycycleend:this.paycycleend
                        }
                    },this,
                    function(req){
                        var resp=req.value.toString();
                        if(resp=="success"){
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),req.msg.toString()],0,false,250);
                            this.emptempstore.removeAll();
                            Wtf.getCmp("incometaxgridpanel").selModel.clearSelections();
                            this.empstore.load();
                        }else if(resp=="failure"){
                            Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.warning"),req.msg.toString());
                        }
                        this.close();
                    },
                    function(req){

                        }
                        );
                }else{
                    calMsgBoxShow(157,0);
                }
            }
    },
   createLeavetypeAdjGrid: function(){
	this.createLeavetypeAdjStore(this.leavetypeid, this.leavetype, this.leaveadj);

        this.LeavetypeAdjStore = new Wtf.data.SimpleStore({
		   fields: ['ename', 'leaveAdj'],
		   data: this.storeData
       });

       this.cm = new Wtf.grid.ColumnModel([
		   {
                        header:  WtfGlobal.getLocaleText("hrms.common.employee.name"),
                        dataIndex: 'ename'
		   },{
                        header: WtfGlobal.getLocaleText("hrms.payroll.Unpaidleaves"),
                        dataIndex: 'leaveAdj',
                        align: 'right',
                        editor: new Wtf.form.NumberField({
                                allowBlank: false,
                                maxValue: 30
                        })
		   }
	   ]);

       this.LeavetypeAdjStore.on('load',function(){
           Wtf.MsgClose();
       },this);

       this.LeavetypeAdjStore.on('beforeload',function(){
           msgBoxShow(35, 4, 5, true);
       },this);

       this.LeavetypeAdjStore.on('loadexception',function(){
           Wtf.MsgClose();
       },this);

       this.sm2 = new Wtf.grid.RowSelectionModel({width:25,singleSelect:true});

		this.typeGrid = new Wtf.grid.EditorGridPanel({
			store: this.LeavetypeAdjStore,
			cm: this.cm,
			sm:this.sm2,
			loadMask : true,
			layout:'fit',
			viewConfig: {
				forceFit: true
			},
			clicksToEdit: 1
		});
   },

   createLeavetypeAdjStore: function(leavetypeId, leaveType, leaveAdj){
	   var outerArray = new Array();
	   for(var i=0; i<this.arr.length; i++){
			var innerArray = new Array();
			innerArray.push(this.arr[i].data.EName);
			innerArray.push(this.arr[i].data.unpaidleave);
			outerArray.push(innerArray);
	   }
	   for(var i=0; i<this.extraarr.length; i++){
			innerArray = new Array();
			innerArray.push(this.extraarr[i].data.EName);
			innerArray.push(this.extraarr[i].data.unpaidleave);
			outerArray.push(innerArray);
	   }
	   this.storeData = outerArray;
   },

   submitLeaveAdj: function(formPanel, winObj){
        var remTrailingComma = false;
        var jsonData = "{'root': [";
        for(var cnt =0 ;cnt < this.LeavetypeAdjStore.getCount(); cnt++) {
			var rec = this.LeavetypeAdjStore.getAt(cnt);
			if(rec.dirty){
				remTrailingComma = true;
				jsonData += this.getJsonFromRecord(rec) + ",";
			}
		}
		if(remTrailingComma)
			jsonData = jsonData.substr(0, jsonData.length - 1);
		 jsonData += "]}"

		Wtf.Ajax.requestEx({
			method: 'POST',
                        url: Wtf.req.leavejsp+'leavemanager.jsp?',
			params: {
					flag: 23,
					jsondata: jsonData,
					userid: this.userid
				}
			},
			this,
			function(result, req){
				this.gridstore.reload();
				this.close();
                                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.Leaveadjustmentisdonesuccessfully")], 1,1);
			},
			function(result, req){
                                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.Problemoccuredwhileconnectingtoserver")], 0,1);
				this.close();
			}
		);
   },

   getJsonFromRecord : function(record) {
		var jsonData = '{"id":"'+record.data['id']+'", "leavetype":"'+record.data["leavetype"]+'", "leaveadj": "'+record.data['leaveAdj']+'"}';
		return jsonData;
	}
});
