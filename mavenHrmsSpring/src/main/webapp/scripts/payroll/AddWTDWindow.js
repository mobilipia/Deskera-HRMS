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
Wtf.analysisWindow = function (config){
    Wtf.apply(this,config);
    config.resizable=false;

    Wtf.analysisWindow.superclass.constructor.call(this,{
        buttons:[
        {
            text: WtfGlobal.getLocaleText("hrms.common.Save"),
            scope:this,
            handler:function (){
                var expr = "";
                for ( var ctr in this.mchild){
                    if(this.mchild[ctr].val!=undefined){
                        expr += "("+this.bchild[ctr].val+")";
                        expr += this.cchild[ctr].val+"*";
                        expr += this.mchild[ctr].val;
                    }
                }
                if(!this.AddEditForm.form.isValid()) {
                    return ;
                } else
                {
                    if(expr == "" && this.rateunit.getValue()=='%' && this.computeon.getValue() == '3'){
                        calMsgBoxShow(225,0);
                        return;
                    }
                    if(this.radio1.getValue() && this.radiowage.getValue()){
                        for(var n = 0 ; n < this.userdsT.data.length ; n++){
                            if(this.userdsT.getAt(n).data.id == this.componentCombo.getValue()){
                                if(!this.userdsT.getAt(n).data.isdefault){
                                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.selected.payroll.component.can.not.default.because.dependent.not.default")],2,false,360);//calMsgBoxShow(["Warning","The Selected Payroll Component can not be set as default because dependent Payroll component is not default."],2,false,360);
                                    return;
                                }
                            }
                        }
                    }
                    if(this.radio1.getValue()){
                        if(this.rateunit.getValue()=="%"){
                            var con = this.computeon.getValue();
                            if(this.tablename!="Taxmaster") {
                                if(con=='1'||con=='2'||con=='0'){
                                    for(n = 0 ; n < this.userdsT.data.length ; n++){
                                      if(this.userdsT.getAt(n).data.isdefault == true){
                                          if(this.userdsT.getAt(n).data.computeon == con){
                                        	  	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.can.not.set.default.two.component.on.same.type")],2,false,360);//calMsgBoxShow(["Warning","You can not set as default two component on same type."],2,false,360);
                                                return;
                                          }
                                      }
                                    }
                                }
                            } else {
                                if(con=='2'){
                                    for(n = 0 ; n < this.userdsT.data.length ; n++){
                                      if(this.userdsT.getAt(n).data.isdefault == true){
                                          if(this.userdsT.getAt(n).data.computeon == con){
                                        	  	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.can.not.set.default.two.component.on.same.type")],2,false,360);//calMsgBoxShow(["Warning","You can not set as default two component on same type."],2,false,360);
                                                return;
                                          }
                                      }
                                    }
                                }
                            }
                        }
                    }
                    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.common.want.to.save.changes"),function(btn){//Wtf.MessageBox.confirm("Confirm","Do you want to save the changes?",function(btn){
                        if(btn!="yes") {
                            //this.close();
                        }
                        else{
                            if(this.AddEdit=="Edit") {
                                var sm=this.generaltaxgrid. getSelectionModel();
                                var rec=sm.getSelected();
                                var row=this.userdsT.indexOf(rec);
                                sm.clearSelections();
                                //WtfGlobal.highLightRow(this.generaltaxgrid ,"33CC33",5,row);
                            }
                            var url;
                            var loadUrl;
                            if(this.type=="Wage"){
                                url="Payroll/Wage/setWagesData.py";
                                loadUrl = "Payroll/Wage/getWageMaster.py"
                            } else if(this.type=="Tax"){
                                url="Payroll/Tax/setTaxData.py";
                                loadUrl = "Payroll/Tax/getTaxMaster.py";
                            } else if(this.type=="Employer Contribution"){
                                url="Payroll/EmpContrib/setEmployerContributionData.py";
                                loadUrl = "Payroll/EmpContrib/getEmpContribMaster.py";
                            } else {
                                url= "Payroll/Deduction/setDeductionData.py";
                                loadUrl = "Payroll/Deduction/getDeductionMaster.py";
                            }
                            calMsgBoxShow(200,4,true);
                            
                            var option = this.rateunit.getValue();
                            if(option=="$"){
                                option = "Amount";
                            } else if(option=="%"){
                                option = "Percent";
                            }
                            
                            this.AddEditForm.getForm().submit({
//                                url: Wtf.req.base + "PayrollHandler.jsp" ,
                                url:url,
                                params:{
                                    save:true,
                                    saveType:this.type,
                                    optiontype:this.rateunit.getValue(),
                                    option:option,
                                    isChecked:this.radio1.getValue(),
                                    Action:this.AddEdit,
                                    depwageid:this.componentCombo.getValue(),
                                    categoryid:this.categoryid==null?"":this.categoryid,
                                    expr:expr,
                                    computeon:this.computeon.getValue()
                                },
                                method:'post',
                                scope:this,
                                success:function(a,req){
                                    req=eval('('+req.response.responseText+')');
                                    if(req.data.value=='exist'){
                                        calMsgBoxShow(135,0);
                                    }
                                    if(req.data.value=='assign'){
                                        calMsgBoxShow(138,0);
                                    }
                                    if(req.data.value=='success'){
                                        if(req.data.action=='Added'){
                                            calMsgBoxShow(136,0);
                                        }
                                        if(req.data.action=='Edited'){
                                            calMsgBoxShow(137,0);
                                        }
                                        Wtf.getCmp('addWTDwindow').close();
                                        this.userdsT.proxy.conn.url = loadUrl;
                                        this.userdsT.baseParams = {
                                            allflag:false,
                                            tablename:this.tablename,
                                            type:'getPayComponent'
                                        }
                                        if(this.AddEdit=="Edit") {
                                            var params={
                                                start:this.generaltaxgrid.pag.cursor,
                                                limit:this.generaltaxgrid.pag.pageSize
                                            }
                                            WtfGlobal.delaytasks(this.userdsT,params);
                                        }else{
                                            this.userdsT.load({
                                                params:{
                                                    start:0,
                                                    limit:this.generaltaxgrid.pag.pageSize
                                                }
                                            });
                                        }
                                        this.payrollcombo.setValue(this.tablename);
                                        if(this.tablename=="Taxmaster") {
                                            this.incometaxgrid.show();
                                            Wtf.getCmp('paysettingmainPanel').doLayout();
                                        } else
                                        {
                                            this.incometaxgrid.hide();
                                            Wtf.getCmp('paysettingmainPanel').doLayout();
                                        }
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
            text:WtfGlobal.getLocaleText("hrms.common.cancel"),//"Cancel",
            handler:function (){
                Wtf.getCmp('addWTDwindow').close();
            },
            scope:this
        }
        ]
    });
}
Wtf.extend(Wtf.analysisWindow,Wtf.Window,{
    initComponent:function (){
        Wtf.analysisWindow.superclass.initComponent.call(this);
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
    afterRender:function(ct, position){
        Wtf.analysisWindow.superclass.afterRender.call(this,ct, position);
        if(this.computeon.getValue()=='3'){
            this.correct.setVisible(true);
            this.setHeight(600);
        }
    },
    selectrows : function(a,b,c){
        var arr = [];
        this.oper = [];
        this.coeff = [];
        var newrec = new this.wageRec({
                'type':"<b>"+WtfGlobal.getLocaleText("hrms.payroll.template.basic")+"</b>",
                'id':'-1',
                'comp':'<b>'+WtfGlobal.getLocaleText("hrms.payroll.template.basic")+'</b>'//'comp':'<b>Template Basic</b>'
            });
            arr.push(newrec);
            if(c.params.ss == undefined || c.params.ss ==""){
                this.wageds.insert(0,arr);
                this.ArrangeNumberer(0);
            }
            arr = [];
        if(this.computeon.getValue()=='3'){
//            if(c.params.ss != undefined){
//                this.sm.suspendEvents();
//            }
            if(this.generaltaxgrid.getSelectionModel().getSelected()!=null){
                var expr = this.generaltaxgrid.getSelectionModel().getSelected().get("expr").split("(add)");
                for(var ctr=0;ctr<expr.length;ctr++){
                    var subexpr = expr[ctr].split("(sub)");
                    for(var cnt=0;cnt<subexpr.length;cnt++){
                        var exprwthcoeff = subexpr[cnt].split("\*");
                        var ceff =1;
                        var wgid;
                        if(exprwthcoeff.length > 1) {
                            ceff = exprwthcoeff[0];
                            wgid = exprwthcoeff[1]
                        } else {
                            wgid = exprwthcoeff[0]
                        }
                        var index = this.wageds.find('id',wgid);
                        if(index>-1){
                            if(cnt==0){
                                this.oper[index] = "+";
                            }else{
                                this.oper[index] = "-";
                            }
                            arr.push(this.wageds.getAt(index));
                            this.coeff[index] = ceff;
                        }
                    }
                }
                if(arr.length>0)
                    this.sm.selectRecords(arr);
            }
        }
    },
    GetNorthPanel:function(){
    	var wintitle=this.AddEdit=="Edit"?WtfGlobal.getLocaleText("hrms.payroll.edit.component"):WtfGlobal.getLocaleText("hrms.payroll.add.component");//var wintitle=this.AddEdit=="Edit"?"Edit Component":"Add Component";
    	var windetail=this.AddEdit=="Edit"?WtfGlobal.getLocaleText("hrms.payroll.fill.up.information.to.edit.component"):WtfGlobal.getLocaleText("hrms.payroll.fill.up.information.to.add.component");//var windetail=this.AddEdit=="Edit"?'Fill up the information to edit component':'Fill up the information to add component';
        var image=this.typeimage;
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:90,
            border:false,
            bodyStyle:"backgroubodyStylend-color:white;padding:8px;border-bottom:1px solid #bfbfbf;background-color: white",
            //html:getHeader(image,wintitle,windetail)
            html: getTopHtml(wintitle,windetail,image)
        });
    },
    GetAddEditForm:function (){
        this.combodata=[[WtfGlobal.getLocaleText("hrms.payroll.Wage"),'Wagemaster'],[WtfGlobal.getLocaleText("hrms.payroll.Tax"),'Taxmaster'],[WtfGlobal.getLocaleText("hrms.payroll.Deduction"),'Deductionmaster'],[WtfGlobal.getLocaleText("hrms.payroll.EmployerContribution"),'EmpContribmaster']];
        this.combostore1=new Wtf.data.SimpleStore({
            fields:[{
                name:'type'
            },
            {
                name:'code'
            }],
            data:this.combodata
        });
        this.fieldswage=[
        {
            name:'type'
        },{
            name:'cash'
        },{
            name:'code'
        },{
            name:'id'
        },{
            name:'assigned'
        },{
            name:'rate'
        },{
            name:'amount'
        }
        ];
        this.fieldswage=Wtf.data.Record.create(this.fieldswage);
        this.readerwage=new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.fieldswage);
        this.wagestore1= new Wtf.data.Store({
            url: "Payroll/Wage/getWageMasterForComponent.py",
            method:'GET',
            reader: this.readerwage
        });
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
                items:[this.rangefrom=new Wtf.form.NumberField({
                    fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.income.slab")+" ("+WtfGlobal.getCurrencySymbol()+")",//"Income Slab ("+WtfGlobal.getCurrencySymbol()+")",
                    editable:false,
                    name:'rangefrom',
                    width:100,
                    labelWidth:100,
                    emptyText:WtfGlobal.getLocaleText("hrms.payroll.min.value"),//'Min value',
                    regex:/^[0-9]{0,10}$/
                })]
            },{
                columnWidth:.48,
                frame:false,
                border:false,
                items:[this.rangeto=new Wtf.form.NumberField({
                    width:95,
                    name:'rangeto',
                    emptyText:WtfGlobal.getLocaleText("hrms.payroll.max.value"),//'Max value',
                    regex:/^[0-9]{0,10}$/
                })]
            }
            ]
        });


        this.ctgrycombo=new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.category"),//'Category',
            store:Wtf.catgStore,
            displayField:'name',
            valueField:'id',
            name:'categoryname',
            typeAhead:true,
            width:200,
            listWidth:200,
            labelWidth:100,
            mode:'local',
            triggerAction:'all',
            emptyText:WtfGlobal.getLocaleText("hrms.common.select.category"),//'Select Category',
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

        this.editloadflag=0;
        if(!Wtf.StoreMgr.containsKey("catg")){
            Wtf.catgStore.on('load',this.setCategary,this);
            Wtf.catgStore.load();
            Wtf.StoreMgr.add("catg",Wtf.catgStore)
        }
        else{
            this.setCategary();
            this.editloadflag=1;
        }

        this.taxpanel=new Wtf.Panel({
            width:400,
            height:60,
            layout:'form',
            border:false,
            hidden:true,
            items:[this.taxrateinamount,this.ctgrycombo]
        });
        this.combo1=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.add.type")+"*",//"Add Type*",
            store:this.combostore1,
            displayField:'type',
            typeAhead: true,
            valueField:'code',
            allowBlank:false,
            name:'type',
            width:200,
            mode: 'local',
            value:this.tablename,
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.component"),//'Select component',
            selectOnFocus:true,
            listeners:
            {
                scope:this,
                Select:function(combo,record,index)
                {
                    this.tablename=record.get("code");
                    this.type=record.get("type");
                    if(this.type=="Tax"){
                        this.ctgrycombo.allowBlank=false;
                        this.rangefrom.allowBlank=false;
                        this.rangeto.allowBlank=false;
                        this.AddEditForm.doLayout();
                    } else
                    {
                        this.ctgrycombo.allowBlank=true;
                        this.rangefrom.allowBlank=true;
                        this.rangeto.allowBlank=true;
                        this.AddEditForm.doLayout();
                    }
                }
            }
        });
        this.componentCombo=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.wage.component"),//"Wage Component",
            store:this.wagestore1,
            displayField:'type',
            disabled:true,
            typeAhead: true,
            valueField:'id',
            allowBlank:false,
            name:'type',
            width:200,
            mode: 'local',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.wage.component"),//'Select Wage Component',
            selectOnFocus:true
        });
        this.text1=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.name")+"*",//"Name*",
            width:200,
            allowBlank:false,
            validator:WtfGlobal.noBlankCheck,
            maxLength:35,
            scope:this,
            name:"name"
        });
        this.text2=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.code")+"*",//"Code*",
            width:200,
            name:"code",
            maxLength:50,
            allowBlank:false,
            validator:WtfGlobal.noBlankCheck,
            displayField:'cname'
        });

        this.radio=new Wtf.Panel({
            width:400,
            layout:'form',
            height:30,
            border:false,
            items:[this.radio1=new Wtf.form.Checkbox({
                name: 'linkasdefault',
                id:'checkisdefault',
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.add.as.default"),//'Add as default',
                style:'padding-top:15px;'
            })]
        });

        this.radiosal=new Wtf.Panel({
            width:400,
            layout:'form',
            labelWidth:200,
            //height:30,
            border:false,
            items:[this.radiosalary=new Wtf.form.Radio({
                name: 'percentof',
                id:'percentofsal',
                disabled:true,
                boxLabel:WtfGlobal.getLocaleText("hrms.payroll.percent.of.salary"),//'Percent Of Salary',
                style:'padding-top:15px;padding-left:30px;',
                hideLabel:true
            })]
        });

        this.radiocomp=new Wtf.Panel({
            width:400,
            layout:'form',
          //  height:30,
            labelWidth:200,
            border:false,
            items:[this.radiowage=new Wtf.form.Radio({
                name: 'percentof',
                id:'percentofcomp',
                disabled:true,
                boxLabel:WtfGlobal.getLocaleText("hrms.payroll.percent.of.wage.component"),//'Percent Of Wage Component',
                style:'padding-top:15px;padding-left:30px;',
                hideLabel:true

            })]
        });

        this.rateunit=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.unit")+"*",//"Unit*",
            store:this.percentstore,
            displayField:'type',
            typeAhead: true,
            valueField:'code',
            allowBlank:false,
            width:200,
            labelWidth:100,
            scope:this,
            name:'option',
            mode: 'local',
            value:'%',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.percent.or.amount"),//'Select Percent or Amount',
            selectOnFocus:true,
            minValue : 0,
            listeners:
            {
                scope:this,
                Select:function(combo,record,index) {
                    if(record.get('type')=="Percent" ||record.get('type')==WtfGlobal.getLocaleText("hrms.payroll.Percent")) {
//                        this.phoneNumberExpr = /^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/;
//                        Wtf.getCmp('myratetext').regex=this.phoneNumberExpr;
                        this.myratetax.maxValue = 100;
                        this.myratetax.setValue(0);
                        Wtf.getCmp('percentofsal').enable();
                        Wtf.getCmp('percentofcomp').enable();
                        //var f = form.findField('id');
                        this.computeon.setDisabled(false);
                        this.computeon.setVisible(true);
                        this.computeon.container.up('div.x-form-item').dom.style.display = 'block';
                        this.computeon.allowBlank = false;
                        if(this.computeon.getValue()=='3'){
                            this.correct.setVisible(true);
                            this.setHeight(600);
                            this.doLayout();
                        }
                    }
                    if(record.get('type')=="Amount" ||record.get('type')==WtfGlobal.getLocaleText("hrms.payroll.Amount")) {
//                        this.phoneNumberExpr = /^[0-9]*$/;
//                        Wtf.getCmp('myratetext').regex=this.phoneNumberExpr;
                        this.myratetax.maxValue = Number.MAX_VALUE;
                        this.myratetax.setValue(0);
                        Wtf.getCmp('percentofsal').disable();
                        Wtf.getCmp('percentofcomp').disable();
                        this.componentCombo.disable();
                        this.componentCombo.setValue("");
                        this.computeon.setVisible(false);
                        this.computeon.container.up('div.x-form-item').dom.style.display = 'none';
                        this.computeon.allowBlank = true;
                        if(this.computeon.getValue()=='3'){
                            this.correct.setVisible(false);
                            this.setHeight(450);
                            this.doLayout();
                        }
                    }
                }
            }
        });

        this.cm=new Wtf.grid.ColumnModel([
            new Wtf.grid.CheckboxSelectionModel(),
            new Wtf.grid.RowNumberer(),
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.component"),//'Component',
                dataIndex:'type',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.type"),//'Type',
                dataIndex:'comp',
                sortable: true
            }
        ]);
        this.wageRec = new Wtf.data.Record.create([
        {
            name: 'type'
        },{
            name: 'id'
        },{
            name: 'comp'
        }
        ]);
        this.wageds = new Wtf.data.Store({
            scope:this,
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:'totalcount'
            },this.wageRec),
            baseParams : {
                tablename:'Wagemaster',
                type:'getPayComponent',
                allflag:'true',
                isedit: this.AddEdit,
                wageno: (this.AddEdit) ? this.editWageId : "",
                earningtype: this.type,
                computeon:"3",
                deduc:'true',
                empcont:"true"
            },
//            url: Wtf.req.base + "PayrollHandler.jsp"
            url: "Payroll/Wage/getWageMaster.py"
        });

        this.wageds.load({
            params:{
                start:0,
                limit:15
            }
        });
        this.wageds.on("load",this.selectrows,this);
        this.computedata=[['0',WtfGlobal.getLocaleText("hrms.payroll.currentdeductions")],['1',WtfGlobal.getLocaleText("hrms.payroll.currentearnings")],['2',WtfGlobal.getLocaleText("hrms.payroll.netsalary")],['3',WtfGlobal.getLocaleText("hrms.payroll.specifiedformula")]];
        this.computestore=new Wtf.data.SimpleStore({
            fields:[{
                name:'id'
            },
            {
                name:'value'
            }],
            data:this.computedata
        });

           this.computeon=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.compute.on")+"*",//"Compute on*",
            store:this.computestore,
            displayField:'value',
            typeAhead: true,
            valueField:'id',
            allowBlank:false,
            name:'type',
            width:200,
            mode: 'local',
           // value:this.tablename,
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.component"),//'Select component',
            selectOnFocus:true,
            listeners:
            {
                scope:this,
                Select:function(combo,record,index){
                    if(record.get('id')=="3"){
                        this.correct.setVisible(true);
                        this.setHeight(600);
                        this.doLayout();
                    }else{
                        this.correct.setVisible(false);
                        this.setHeight(450);
                        this.doLayout();
                    }
                        
                }
            }
        });

        this.child1 = [];
        this.bchild = [];
        this.mchild = [];
        this.cchild = [];
        this.achild = [];
        this.conContainer = document.createElement('div');
        this.conContainer.className = 'conContainer';
        this.conContainer.id = 'parentCon'
        this.child1[this.i] = document.createElement('div');
        this.child1[this.i].className = 'child1';
        this.conContainer.appendChild(this.child1[this.i]);
        this.sm = new Wtf.grid.CheckboxSelectionModel();
        this.sm.on("rowselect",this.onRowSelect,this);
        this.sm.on("rowdeselect",this.onRowDeselect,this);
        this.correct = new Wtf.Panel({
            id:'feed',
            layout:'fit',
            border:false,
            hidden:true,
            items:[{
                layout:'border',
                height:(Wtf.isIE7 || Wtf.isIE8)?210:200,
                bodyStyle:'margin-top:10px; background:transparent',
                border:false,
                //cls:'fontsize',
                layoutConfig:{labelSeparator:''},
                items:[
                     new Wtf.form.FieldSet({
                        height:(Wtf.isIE7 || Wtf.isIE8)?100:140,
                        region:'north',
                        layout:'fit',
                        id:'rules',
                        title:WtfGlobal.getLocaleText("hrms.payroll.1.select.wage.components"),//'1.Select Wage components',
                        items:[
                           this.grid = new Wtf.KwlGridPanel({
                                id:'rulegrid',
                                store:this.wageds,
                                serverSideSearch:true,
                                cm: this.cm,
                                searchLabel:" ",
                                searchLabelSeparator:" ",
                                searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.component.grid.search.msg"),//"Search by Component",
                                searchField:"type",
                                paging: false,
                                sm: this.sm,
                                viewConfig:{
                                    forceFit:true
                                }
                            })
                        ]
                    }),
                     new Wtf.form.FieldSet({
                        region:'center',
                        id:'subrules',
                        bodyStyle: 'overflow-y:scroll;',
                        height: 100,
                        title:WtfGlobal.getLocaleText("hrms.payroll.2.your.formula"),//'2. Your Formula',
                        items:[
                            new Wtf.Panel({
                                id:'addCon',
                                border:false,
                                contentEl:this.conContainer
                            })
                        ]
                    })
                ]
            }]
        });
//        this.grid.on("render",this.selectCheckboxes,this);
        this.myratetax=new Wtf.form.NumberField({
            width:200,
            labelWidth:100,
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.value")+"*",//"Value*",
            name:'rate',
            maxLength:10,
            scope:this,
            allowNegative:false,
            value:0,
            id:"myratetext",
            allowBlank:false,
            listeners:
            {
                scope:this,
                focus:function(){
                    if(this.rateunit.getValue()=="%"){
//                        this.phoneNumberExpr = /^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/;
//                        Wtf.getCmp('myratetext').regex=this.phoneNumberExpr;
                        this.myratetax.maxValue = 100;
                    }if(this.rateunit.getValue()=="$") {
//                        this.phoneNumberExpr = /^[0-9]*$/;
//                        Wtf.getCmp('myratetext').regex='';
                        this.myratetax.maxValue = Number.MAX_VALUE;
                    }
                }
            }
        })
        this.typeid=new Wtf.form.Hidden({
            name:'typeid',
            scope:this,
            allowBlank:true,
            hideLabel:true,
            hidden:true
        });
        this.typeid.hide();

        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            scope:this,
            labelWidth:200,
            bodyStyle:"background-color:#f1f1f1;padding:15px",
            items:[
            this.combo1,this.text1,
            this.text2,
            this.rateunit,/*this.radiosal,this.radiocomp,this.componentCombo,*/ this.computeon,this.correct,this.myratetax,this.radio,this.typeid
            ]
        });
        if(this.type=="Tax") {
            this.ctgrycombo.allowBlank=false;
            this.rangefrom.allowBlank=false;
            this.rangeto.allowBlank=false;
//            Wtf.getCmp('addWTDwindow').setSize(500,450);
        }
        if(this.AddEdit=="Edit") {
            var recData = this.generaltaxgrid.getSelectionModel().getSelected().data;
            if(this.type=="Tax"){
                this.ctgrycombo.allowBlank=false;
                this.rangefrom.allowBlank=false;
                this.rangeto.allowBlank=false;
//                Wtf.getCmp('addWTDwindow').setSize(400,400);
                this.AddEditForm.doLayout();
                this.rangefrom.setValue(recData.rangefrom);
                this.rangeto.setValue(recData.rangeto);

                Wtf.catgStore.on("load",function(){
                    this.ctgrycombo.setValue(recData.category);
                },this);

                if(this.editloadflag==1){
                    this.ctgrycombo.setValue(recData.category);
                }

            }else{
                this.ctgrycombo.allowBlank=true;
                this.rangefrom.allowBlank=true;
                this.rangeto.allowBlank=true;

            }
            this.text1.setValue(recData.type);
            this.text2.setValue(recData.code);
            this.typeid.setValue(recData.id);
            if(recData.rate==0) {
                this.ratecash=recData.cash;
                this.rateunit.setValue("$");
                this.computeon.setDisabled(true);
            } else{
                if(recData.depwage==""){
                    Wtf.getCmp("percentofsal").enable();
                    Wtf.getCmp("percentofcomp").enable();
                    Wtf.getCmp("percentofsal").checked=true;
                } else {
                    Wtf.getCmp("percentofcomp").enable();
                    Wtf.getCmp("percentofsal").enable();
                    Wtf.getCmp("percentofcomp").checked=true;
                    //this.componentCombo.setValue(recData.depwageid);
                }
                this.ratecash=recData.cash;
                this.rateunit.setValue("%");
            }
            this.myratetax.setValue(this.ratecash);
            this.combo1.setValue(this.tablename);
            this.combo1.disable();
            this.radio1.setValue(recData.isdefault);
            this.computeon.setValue(recData.computeon);
            this.myratetax.on("change",this.validatePercent,this);
        }
        Wtf.getCmp("percentofcomp").on("check",function(a,b){
                this.componentCombo.setDisabled(!b);
        },this);
        Wtf.getCmp("percentofsal").on("check",function(a,b){
                this.componentCombo.setValue("");
        },this);
        Wtf.getCmp("percentofsal").on("check",function(a,b){
            if(Wtf.getCmp("percentofsal").rendered && Wtf.getCmp("percentofcomp").rendered){
                Wtf.getCmp("percentofsal").onClick();
                Wtf.getCmp("percentofcomp").onClick();
            }
        },this);
        Wtf.getCmp("percentofcomp").on("check",function(a,b){
            if(Wtf.getCmp("percentofsal").rendered && Wtf.getCmp("percentofcomp").rendered){
                Wtf.getCmp("percentofsal").onClick();
                Wtf.getCmp("percentofcomp").onClick();
            }
        },this);
//        this.wagestore1.load({
//            params:{
//                allflag:'true',
//                type:'Wages',
//                cname:'aa',
//                wageid:(this.AddEdit=="Edit")?recData.id:""
//            }
//        });
//        this.wagestore1.on("load",function(){
//            if(this.AddEdit=="Edit") {
//                this.componentCombo.setValue(recData.depwageid);
//            }
//        },this)
    },
    ArrangeNumberer: function(currentRow) {                // use currentRow as no. from which you want to change numbering
        var plannerView = this.grid.getView();                      // get Grid View
        var length = this.wageds.getCount();              // get store count or no. of records upto which you want to change numberer
        for (var i = currentRow; i < length; i++)
            plannerView.getCell(i, 1).firstChild.innerHTML = i + 1;
    },
    onRowDeselect:function(sel,row,rec){
      document.getElementById('parentCon').removeChild(document.getElementById("con"+row));
      this.mchild.splice(row,1);
      this.cchild.splice(row,1);
      this.bchild.splice(row,1);
      this.child1[row]=undefined;
    },
     onRowSelect:function(sel,row,rec){
    	if(this.child1[row]==undefined){
            this.child1[row] = document.createElement('div');
            this.child1[row].id = "con"+row;

            this.mchild[row] = document.createElement('div');
            this.mchild[row].className = 'mchild';
            if(Wtf.isIE7 || Wtf.isIE8)
            	this.mchild[row].style.display = "inline";
            else
            	this.mchild[row].style.cssFloat = "left";
            this.mchild[row].val = rec.get("id");
            this.mchild[row].id = "mchild"+row;

            this.cchild[row] = document.createElement('div');
            this.cchild[row].id = "coeff" + row;
            this.cchild[row].className="x-form-item";
            if(Wtf.isIE7 || Wtf.isIE8)
            	this.cchild[row].style.display = "inline"; 
            else
            	this.cchild[row].style.cssFloat = "left";
            if(this.coeff!=null&&this.coeff!=undefined&&this.coeff.length>0){
                this.cchild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+this.coeff[row]+")</a>&nbsp;";
                this.cchild[row].val = this.coeff[row];
            } else {
                this.cchild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+1+")</a>&nbsp;";
                this.cchild[row].val = 1;
            }
            this.bchild[row] = document.createElement('div');
            this.bchild[row].id = "addsub" + row;
            this.bchild[row].className="x-form-item";
            if(Wtf.isIE7 || Wtf.isIE8)
            	this.bchild[row].style.display = "inline";
            else
            	this.bchild[row].style.cssFloat = "left";
            if(this.oper!=null&&this.oper!=undefined&&this.oper.length>0){
                if(this.oper[row]=="+")
                    this.bchild[row].val = "add";
                else if(this.oper[row]=="-")
                    this.bchild[row].val = "sub";
                else{
                    this.bchild[row].val = "add";
                    this.oper[row] = "+";
                }
                this.bchild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+this.oper[row]+")</a>&nbsp;";
            }else{
                this.bchild[row].val = 'add';
                this.bchild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>(+)</a>&nbsp;";
            }
            this.mchild[row].innerHTML = rec.get("type");
            this.mchild[row].className="x-form-item";
            if(Wtf.isIE7 || Wtf.isIE8){
            	this.bchild[row].onclick = this.showRuleWin.createDelegate(this, [this.bchild[row]], false);
            	this.cchild[row].onclick = this.showCoeffWin.createDelegate(this, [this.cchild[row]], false);
            }else{
            	this.bchild[row].onclick = this.showRuleWin.createDelegate(this);
            	this.cchild[row].onclick = this.showCoeffWin.createDelegate(this);
            }
            
            this.child1[row].appendChild(this.bchild[row]);//operator
            this.child1[row].appendChild(this.cchild[row]);
            this.child1[row].appendChild(this.mchild[row]);//component
            this.conContainer.appendChild(this.child1[row]);
    	}
    },
    invRadiobttn:function(obj,chk){
        if(obj.id=="add")
            Wtf.getCmp("sub").checked =false;
        if(obj.id=="sub")
            Wtf.getCmp("add").checked =false;
    },
    showCoeffWin:function(e){
        if(Wtf.isIE7 || Wtf.isIE8)
    		this.updateelement = e;
        else
        	this.updateelement = e.currentTarget;
        var top = new Wtf.Panel({
            frame:true,
            items: [{
                layout:'form',
                items:[{
                    layout:'column',
                    items: [{
                        html:WtfGlobal.getLocaleText("hrms.payroll.coefficient")+':<br><br>'//'Coefficient:<br><br>'
                    }]
                },{
                layout: 'column',
                fieldWidth:0,
                items: [
                    new Wtf.form.NumberField({
                        name:'coeff',
                        id:'coefffield',
                        value : this.updateelement.val,
                        allowNegative : false,
                        minValue : 0,
                        maxValue : 100,
                        decimalPrecision : 4
                    })
                ]
                }]
            },{
                layout:'column',
                items:[{
                    layout:'form',
                    buttons:[{
                        text:WtfGlobal.getLocaleText("hrms.common.ok"),//'OK',
                        scope:this,
                        handler:function(){
                             var val = Wtf.getCmp("coefffield").value;
                             if(!Wtf.getCmp("coefffield").validateValue(val)){
                            	 return ;
                             }
                             this.updateelement.innerHTML="&nbsp;<a href=javascript:void(0)>"+val+"</a>&nbsp;";
                             this.updateelement.val = val;
                             win.close();
                        }
                    },{
                        text:WtfGlobal.getLocaleText("hrms.common.cancel"),//'Cancel',
                        handler:function(){
                            win.close();
                        }
                    }]
                }]
            }]
        });

//        Wtf.getCmp("add").on("check",this.invRadiobttn,this);
//	Wtf.getCmp("sub").on("check",this.invRadiobttn,this);
        var win = new Wtf.Window({
            title:WtfGlobal.getLocaleText("hrms.payroll.coefficient"),//'Coefficient',
            closable:true,
            width:200,
            iconCls:'winicon',
            resizable:false,
            autoDestroy:true,
            modal:true,
            border:false ,
            id:'coefficientWindow',
            items: [top]
        });
        win.show();
        //win.on("render",this.selectRadiobttn,this);
    },
    showRuleWin:function(e){
        if(Wtf.isIE7 || Wtf.isIE8)
    		this.updateelement = e;
        else
        	this.updateelement = e.currentTarget;
        var top = new Wtf.Panel({
            frame:true,
            items: [{
                layout:'form',
                items:[{
                    layout:'column',
                    items: [{
                        html:WtfGlobal.getLocaleText("hrms.payroll.ApplyRuleIf")+':<br><br>'//'Apply Rule If:<br><br>'
                    }]
                },{
                layout: 'column',
                fieldWidth:0,
                items: [
                    new Wtf.form.Radio({
                        name:'cond',
                        id:'add',
                        checked : (this.updateelement.val =='add')?true:false,
                        boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Addition")//'Addition'
                    })
                ]
                },{
                layout:'column',
                fieldWidth:0,
                items: [
                    new Wtf.form.Radio({
                        name:'cond',
                        id:'sub',
                        checked : (this.updateelement.val =='sub')?true:false,
                        boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Subtraction")//'Subtraction'
                    })
                ]
                }]
            },{
                layout:'column',
                items:[{
                    layout:'form',
                    buttons:[{
                        text:WtfGlobal.getLocaleText("hrms.common.ok"),//'OK',
                        scope:this,
                        handler:function(){
                             if(Wtf.getCmp("add").checked == true){
                                this.rad = 0;
                            }else if(Wtf.getCmp("sub").checked == true){
                                this.rad = 1;
                            }
                            if(this.rad==1){
                                this.updateelement.innerHTML="&nbsp;<a href=javascript:void(0)>(-)</a>&nbsp;";
                                this.updateelement.val = 'sub';
                            }else{
                                this.updateelement.innerHTML="&nbsp;<a href=javascript:void(0)>(+)</a>&nbsp;";
                                this.updateelement.val = 'add';
                            }
                            win.close();
                        }
                    },{
                        text:WtfGlobal.getLocaleText("hrms.common.cancel"),//'Cancel',
                        handler:function(){
                            win.close();
                        }
                    }]
                }]
            }]
        });

        Wtf.getCmp("add").on("check",this.invRadiobttn,this);
	Wtf.getCmp("sub").on("check",this.invRadiobttn,this);
        var win = new Wtf.Window({
            title:WtfGlobal.getLocaleText("hrms.payroll.andor"),//'And/Or',
            closable:true,
            width:200,
            iconCls:'winicon',
            resizable:false,
            autoDestroy:true,
            modal:true,
            border:false ,
            id:'conditionWindow',
            items: [top]
        });
        win.show();
        //win.on("render",this.selectRadiobttn,this);
    },
    addCategory:function() {
    	 WtfGlobal.showmasterWindow(14,Wtf.catgStore,"Add");//WtfGlobal.showmasterWindow(14,Wtf.catgStore,"Add");
    },
    setCategary:function(){
        if(Wtf.catgStore.getCount()>0){
            this.ctgrycombo.setValue(Wtf.catgStore.getAt(Wtf.catgStore.getCount()-1).get('id'));
        }
    },
    validatePercent:function(){
        if(this.rateunit.getRawValue()=="Percent"&&this.myratetax.getValue()>100){
            this.myratetax.setValue("");
            calMsgBoxShow(166,3);
        }
    }
});

Wtf.addpayCmpWin = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        width:430,
        height:325,
        buttons: [
        {
            text: WtfGlobal.getLocaleText("hrms.common.Save"),//'Save',
            handler: this.saveRequest,
            scope:this
        },
        {
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),//'Cancel',
            handler: function(){
                this.close();
            },
            scope:this
        }]
    }, config);
    Wtf.addpayCmpWin.superclass.constructor.call(this, config);

};

Wtf.extend(Wtf.addpayCmpWin, Wtf.Window, {
    initComponent: function() {
        Wtf.addpayCmpWin.superclass.initComponent.call(this);
    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.addpayCmpWin.superclass.onRender.call(this, config);
        this.perValue=[[WtfGlobal.getLocaleText("hrms.payroll.Percent"),'%'],[WtfGlobal.getLocaleText("hrms.payroll.Amount"),'$']];
        this.percentSt=new Wtf.data.SimpleStore({
            fields:[{
                name:'type'
            },
            {
                name:'code'
            }],
            data:this.perValue
        });
        this.text1=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.name"),//"Name*",
            width:200,
            allowBlank:false,
            validator:WtfGlobal.noBlankCheck,
            maxLength:35,
            scope:this,
            name:"name"
        });
        this.text2=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.code")+"*",//"Code*",
            width:200,
            name:"code",
            validator:WtfGlobal.noBlankCheck,
            maxLength:50,
            allowBlank:false,
            displayField:'cname'
        });
         this.rateunit=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.unit"),//"Unit*",
            store:this.percentSt,
            displayField:'type',
            typeAhead: true,
            valueField:'code',
            allowBlank:false,
            disabled: true,
            width:200,
            labelWidth:100,
            scope:this,
            name:'option',
            mode: 'local',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.percent.or.amount"),//'Select Percent or Amount',
            selectOnFocus:true,
            listeners:
            {
                scope:this,
                Select:function(combo,record,index) {
                    if(record.get('type')=="Percent" ||record.get('type')==WtfGlobal.getLocaleText("hrms.payroll.Percent")) {
                        this.phoneNumberExpr = /^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/;
                        Wtf.getCmp(this.id+"myratetext").regex=this.phoneNumberExpr;
                        Wtf.getCmp(this.id+"myratetext").setValue(0);
                    }
                    if(record.get('type')=="Amount" || record.get('type')==WtfGlobal.getLocaleText("hrms.payroll.Amount")) {
                        this.phoneNumberExpr = /^[0-9]*$/;
                        Wtf.getCmp(this.id+"myratetext").regex=this.phoneNumberExpr;
                        Wtf.getCmp(this.id+"myratetext").setValue(0);
                    }
                }
            }
//            listeners:
//            {
//                scope:this,
//                Select:function(combo,record,index) {
//                    if(record.get('type')=="Percent") {
////                        this.phoneNumberExpr = /^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/;
////                        Wtf.getCmp('myratetext').regex=this.phoneNumberExpr;
//                        this.myratetax.maxValue = 100;
//                        this.myratetax.setValue(0);
//                        Wtf.getCmp('percentofsal').enable();
//                        Wtf.getCmp('percentofcomp').enable();
//                    }
//                    if(record.get('type')=="Amount") {
////                        this.phoneNumberExpr = /^[0-9]*$/;
////                        Wtf.getCmp('myratetext').regex=this.phoneNumberExpr;
//                        this.myratetax.maxValue = Number.MAX_VALUE;
//                        this.myratetax.setValue(0);
//                        Wtf.getCmp('percentofsal').disable();
//                        Wtf.getCmp('percentofcomp').disable();
//                        this.componentCombo.disable();
//                        this.componentCombo.setValue("");
//                    }
//                }
//            }
        });
          this.myratetax=new Wtf.form.NumberField({
            width:200,
            labelWidth:100,
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.value"),//"Value*",
            name:'rate',
            value:0,
            maxLength:10,
            allowNegative:false,
            scope:this,
            id:this.id+"myratetext",
            allowBlank:false,
            listeners:
            {
                scope:this,
                focus:function(){
                    if(this.rateunit.getValue()=="%"){
                        this.phoneNumberExpr = /^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/;
                        Wtf.getCmp(this.id+"myratetext").regex=this.phoneNumberExpr;
                    }if(this.rateunit.getValue()=="$") {
                        this.phoneNumberExpr = /^[0-9]*$/;
                        Wtf.getCmp(this.id+"myratetext").regex=this.phoneNumberExpr;
                    }
                }
            }
        });
         this.chk1=new Wtf.form.Checkbox({
                name: 'linkasdefault',
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.AddtoPayrollMaster"),//'Add to Payroll Master',
                style:'padding-top:15px;'
            });
        
         this.typeid=new Wtf.form.Hidden({
            name:'typeid',
            value:this.type
        });
        this.addpayform = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder',
            style: "background: transparent;padding-left:20px;padding-top: 20px;padding-right: 0px;",
            autoScroll:false,
            labelWidth :119,
            layoutConfig: {
                deferredRender: false
            },
            items:[this.text1,this.text2,this.rateunit,this.myratetax,this.typeid
            ]
        })

        this.addpaypanel= new Wtf.Panel({
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region :'north',
                    height : 80,
                    border : false,
                    cls : 'panelstyleClass1',
                    html: getTopHtml(WtfGlobal.getLocaleText({key:"hrms.payroll.Addpayrollgrid",params:[this.type]}), WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"),"../../images/payroll.gif")
                },{
                    border:false,
                    region:'center',
                    cls : 'formstyleClass2',
                    layout:"fit",
                    items: [
                    this.addpayform
                    ]
                }]
            }]
        });
        this.rateunit.setValue('$');
        this.add(this.addpaypanel);
    },
    saveRequest:function(){
        if(! this.addpayform.form.isValid()){
            return ;
        }else{
//            if(this.chk1.getValue()){
//
//            }
            var amt=0;
            var rate="-";
            var type="";
            if(this.rateunit.getValue()=='%'){
                amt=parseFloat(this.myratetax.getValue())*(parseFloat(this.salary)/100);
                rate=this.myratetax.getValue();
                type=1;
            }else{
                amt=parseFloat(this.myratetax.getValue());
                type=0;
            }
            var rec=new this.gridrec({
                    Type:this.text1.getValue(),
                    Rate:rate,
                    computeon:"-",
                    amount:amt,
                    amtot:'',
                    ratetype:type
            });
            this.gridst.insert(this.gridst.getCount(),rec);
            var maxtot=0;
            for(var i=0; i <this.gridst.getCount(); i++){
             maxtot+=parseFloat(this.gridst.getAt(i).get("amount"));
          }
            this.grid.total.setValue(WtfGlobal.currencyRenderer2(maxtot));
            Wtf.getCmp(this.empid+'payslipTab'+this.stdate).setTotalSal();
            this.close();
        }
    }
});

