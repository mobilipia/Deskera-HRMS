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
Wtf.CPFSetting=function(config){
    config.autoScroll="true";
    config.layout="fit";
    Wtf.CPFSetting.superclass.constructor.call(this,config);
};
Wtf.extend(Wtf.CPFSetting,Wtf.Panel,{
    initComponent:function(config){
        this.fixedAmt="";
        Wtf.CPFSetting.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.CPFSetting.superclass.onRender.call(this,config);
        this.gridfields=[
        {
            name:'Country'
        },
        {
            name:'Fixamount'
        },
        {
            name:'percentofsalary'
        },
        {
            name:'AgeRange'
        },
        {
            name:'SalRange'
        }
        ];
        this.cmGroupGrid=new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                id:'country',
                header: WtfGlobal.getLocaleText("hrms.common.nationality"),
              //  width: 10,
                sortable: true,
                dataIndex: 'Country'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.Amount"),
               // width: 10,
                sortable: true,
                dataIndex: 'Fixamount'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.percentage.of.salary"),
              //  width: 10,
                sortable: true,
                align:'right'
                //dataIndex: 'percentofsalary',
            },

            {
                header: WtfGlobal.getLocaleText("hrms.common.age.range"),
              //  width: 10,
                sortable: true,
                align:'right',
                dataIndex: 'AgeRange'
              //  renderer:WtfGlobal.numericRenderer
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.salary.range"),
             //   width: 10,
                sortable: true,
                align:'right',
                dataIndex: 'SalRange',
                renderer:function(val){
                    var symbol=WtfGlobal.getCurrencySymbol();
                    var myScripts = new Array(2)
                    myScripts=val.split("-");
                    var range=symbol+" "+WtfGlobal.currencyRenderer2(myScripts[0])+" - "+symbol+" "+WtfGlobal.currencyRenderer2(myScripts[1]);
                    if(val!=null){
                        return('<div class="currency" style="font-family:Lucida Sans Unicode;">'+range+'</div>');
                    }

                }
            }
            ]);
        this.reader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalcount'
        },this.gridfields);
        this.child1 = [];
        this.bchild = [];
        this.mchild = [];
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
        this.cm = new Wtf.grid.ColumnModel([this.sm,
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.component"),
            dataIndex: 'cmptname',
            width: 300
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.value"),
            dataIndex: 'cmptvalue',
            align: 'right',
            renderer:WtfGlobal.numericRenderer,
            editor: new Wtf.form.NumberField({
                allowBlank: false,
                allowDecimals:false,
                allowNegative:false,
                maxValue:100,
                minValue:1,
                validator:WtfGlobal.noBlankCheck
            })
        }
        ]);
        this.selectedRec = new Wtf.data.Record.create([
        {
            name:'cmptname'
        },{
            name:'cmptvalue'
        },{
            name:'cmptprintvalue'
        }
        ]);
        this.data1 = {'data':[{cmptname:'Fixed Amount', cmptvalue:'0', cmptprintvalue:'$'},{cmptname:'Percent of Salary', cmptvalue:'0', cmptprintvalue:'% of Salary'},{cmptname:'Percent of ( Diff. between Salary and Fixed Amount )', cmptvalue:'0', cmptprintvalue:'% of Diff. between Salary and $'}]};
        this.componentds=new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root:'data'
            },this.selectedRec)
        });
        this.groupGridStore=new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root:'data'
            },this.selectedRec)
        });

        this.componentds.loadData(this.data1);
        this.form = new Wtf.form.FormPanel({
            url: Wtf.req.tmjsp+'travelManager.jsp?flag=5',
            waitMsgTarget: true,
            method : 'POST',
            autoScroll:true,
            scope:this,
            frame : false,
            fileUpload:true,
            border:false,
            style:"padding-top:10px;padding-bottom:0px;",
            bodyStyle :'margin-left:20px;margin-right:20px;',
            items:[{
                    border: false,
                    id : 'formattach',
                    layout:'column',
                    autoWidth:true,
                    bodyStyle:'padding:0px;font-size:11px;margin-top:1px;',
                    items: [{
                            border:false,
                            columnWidth:.20,
                            //labelWidth: 110,
                          //  bodyStyle:'margin-right:10px;',
                            layout:'form',
                            items:[{
                                xtype:'fieldset' ,
                                title:WtfGlobal.getLocaleText("hrms.common.nationality"),
                                scope:this,
                                height:70,
                               // labelWidth: 110,
                                anchor:'93%',
                             //   bodyStyle:"padding-left:15px;margin-right:15px;",
                                items:[{
                                    border:false,
                                   // bodyStyle:'margin-left:10px;',
                                    layout:'form',
                                    items:[
                                        this.ExpenseCmb = new Wtf.form.ComboBox({
                                              //  fieldLabel:'Expense Type*',
                                                hideLabel:true,
                                                store:  Wtf.countryStore,
                                                mode:'local',
                                                border: false,
                                                editable: true,
                                                readOnly:true,
                                                typeAhead:true,
                                                forceSelection:true,
                                                hiddenName :'expensetypeid',
                                                valueField:'id',
                                                allowBlank: false,
                                                width: 150,
                                                anchor:'93%',
                                                displayField:'name',
                                               // addNewDisplay:'Add new Expense...',
                                                triggerAction: 'all',
                                                //            allowBlank: false,
                                                emptyText:WtfGlobal.getLocaleText("hrms.common.select.country"),
                                                scope:this
                                        })
                                    ]
                               }]
                            }]
                        },
                        {
                           border:false,
                           columnWidth:.38,
                           bodyStyle:'margin-left:10px;padding-right:10px;',
                           layout:'form',
                           items:[{
                                xtype:'fieldset' ,
                                title:WtfGlobal.getLocaleText("hrms.common.age.range"),
                                scope:this,
                                height:70,
                                bodyStyle:"padding-left:30px;",
                                items:[{
                                    border:false,
                                   // bodyStyle:'margin-left:10px;',
                                    layout:'column',
                                    items:[{
                                                xtype:'panel',
                                                columnWidth:0.45,
                                                border:false,
                                              //  bodyStyle:'padding-left:10px;',
                                                layout:'form',
                                                items:[
                                                this.startrange=new Wtf.form.NumberField({
                                                   // fieldLabel:' Salary Range/Month (<span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>)',
                                                    allowBlank:false,
                                                    hideLabel:true,
                                                    allowNegative:false,
                                                    value:0,
                                                //    id:this.id+'salarystartrange',
                                                    maxLength:10,
                                                    scope:this
                                                })
                                                ]
                                            },
                                            {
                                                xtype:'panel',
                                                columnWidth:0.45,
                                                border:false,
                                                layout:'form',
                                                items:[
                                                this.endrange=new Wtf.form.NumberField({
                                                    allowBlank:false,
                                                    maxLength:10,
                                                    hideLabel:true,
                                                    value:0,
                                                    allowNegative:false,
                                                    scope:this,
                                                    //vtype:'range',
                                                    initialPassField:this.id+'salarystartrange'
                                                })
                                                ]
                                            }
                                    ]
                                }]
                           }]
                        },
                        {
                           border:false,
                           columnWidth:.38,
                           bodyStyle:'padding-left:10px;',
                           layout:'form',
                           items:[{
                                xtype:'fieldset' ,
                                title:WtfGlobal.getLocaleText("hrms.payroll.salary.range.month")+' (<span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>)',
                                scope:this,
                                height:70,
                                bodyStyle:"padding-left:30px;",
                                items:[{
                                    border:false,
                                   // bodyStyle:'margin-left:10px;',
                                    layout:'column',
                                    items:[
                                        {
                                                xtype:'panel',
                                                columnWidth:0.45,
                                                border:false,
                                              //  bodyStyle:'padding-left:10px;',
                                                layout:'form',
                                                items:[
                                                this.startrange=new Wtf.form.NumberField({
                                                   // fieldLabel:' Salary Range/Month (<span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>)',
                                                    allowBlank:false,
                                                    hideLabel:true,
                                                    allowNegative:false,
                                                    value:0,
                                                  //  id:this.id+'salarystartrange',
                                                    maxLength:10,
                                                    scope:this
                                                })
                                                ]
                                            },
                                            {
                                                xtype:'panel',
                                                columnWidth:0.45,
                                                border:false,
                                                layout:'form',
                                                items:[
                                                this.endrange=new Wtf.form.NumberField({
                                                    allowBlank:false,
                                                    maxLength:10,
                                                    hideLabel:true,
                                                    value:0,
                                                    allowNegative:false,
                                                    scope:this,
                                                   // vtype:'range',
                                                    initialPassField:this.id+'salarystartrange'
                                                })
                                                ]
                                            }
                                    ]
                                }]
                          }]
                        }]
                 },{
                       border:false,
                       // bodyStyle:'margin-left:10px;',
                       layout:'column',
                       items:[{
                       border:false,
                       bodyStyle:'padding-right:10px;',
                       layout:'form',
                       columnWidth:0.48,
                       items:[{
                            xtype:'fieldset' ,
                            title:WtfGlobal.getLocaleText("hrms.payroll.components.header"),
                           // anchor:'93%',
                            scope:this,
                            height:150,
                            //autoHeight: true,
                            //width:450,
                            // bodyStyle:"width:250px;",
                            items:[{
                                border:false,
                                // bodyStyle:'margin-left:10px;',
                                layout:'fit',
                               // labelWidth:195,
                                items:[
                                   this.grid = new Wtf.grid.EditorGridPanel({
                                        id:'rulegrid',
                                        width:400,
                                        height:80,
                                        store:this.componentds,
                                        clicksToEdit: 1,
                                        cm: this.cm,
                                        sm: this.sm,
                                        viewConfig:{
                                            forceFit:true
                                        }
                                    })
                                ]
//                                items:[
//                                    this.fixamt=new Wtf.form.NumberField({
//                                                    allowBlank:false,
//                                                    maxLength:10,
//                                                    fieldLabel:'Fixed Amount('+WtfGlobal.getCurrencySymbol()+')',
//                                                    value:0,
//                                                    allowNegative:false,
//                                                    scope:this,
//                                                   // vtype:'range',
//                                                    initialPassField:this.id+'salarystartrange'
//                                                }),
//                                    this.percent=new Wtf.form.NumberField({
//                                                    allowBlank:false,
//                                                    maxLength:10,
//                                                    fieldLabel:'% of salary per month',
//                                                    value:0,
//                                                    allowNegative:false,
//                                                    scope:this,
//                                                   // vtype:'range',
//                                                    initialPassField:this.id+'salarystartrange'
//                                                }),
//                               {
//                                    border:false,
//                                   // bodyStyle:'margin-left:10px;',
//                                    layout:'column',
//                                    items:[{
//                                                xtype:'panel',
//                                                columnWidth:0.07,
//                                                border:false,
//                                                bodyStyle:'padding-right:1px;',
//                                                layout:'form',
//                                                items:[
//                                                    this.mixpercent=new Wtf.form.NumberField({
//                                                       // fieldLabel:' Salary Range/Month (<span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>)',
//                                                        allowBlank:false,
//                                                        hideLabel:true,
//                                                        allowNegative:false,
//                                                        value:0,
//                                                        anchor:"90%",
//                                                       // width:30,
//                                                      //  id:this.id+'salarystartrange',
//                                                        maxLength:10,
//                                                        scope:this
//                                                    })
//                                                ]
//                                            },
//                                            {
//                                                xtype:'panel',
//                                                columnWidth:0.65,
//                                                border:false,
//                                                layout:'form',
//                                                bodyStyle:'padding-right:3px;padding-right:2px;',
//                                                labelWidth:270,
//                                                items:[
//                                                this.minusamt=new Wtf.form.NumberField({
//                                                    allowBlank:false,
//                                                    maxLength:10,
//                                                    fieldLabel:'% of difference between salary per month and <span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>',
//                                                    value:0,
//                                                    allowNegative:false,
//                                                    //width:45,
//                                                    anchor:"90%",
//                                                    labelSeparator:"",
//                                                    scope:this,
//                                                   // vtype:'range',
//                                                    initialPassField:this.id+'salarystartrange'
//                                                })
//                                                ]
//                                            }
//                                    ]}
//                                ]
                            },
                 {
                      xtype:'panel',
                      border:false,
                      html:'<div style=\"color:SeaGreen;font-size:11px;padding-left:25px;\">* '+WtfGlobal.getLocaleText("hrms.payroll.select.components.generate.cpf")+'</div>'
                 }]
                        }
                    ]},{
                        border:false,
                        bodyStyle:'padding-left:10px;',
                       layout:'form',
                       //style:'margin-left:33px;',
                       columnWidth:0.48,
                       items:[{
                            xtype:'fieldset' ,
                            title:WtfGlobal.getLocaleText("hrms.payroll.generated.rule"),
                            scope:this,
                            height:150,
                            //width:450,
                            // bodyStyle:"width:250px;",
                            items:[
                                    new Wtf.Panel({
                                        id:'addCon',
                                        border:false,
                                        contentEl:this.conContainer
                                    })
//                                this.calculationTemplatePanel = new Wtf.Panel({
//                                    border:false,
//                                    bodyStyle:'margin-bottom:2px;margin-left:2px;margin-right:2px;margin-top:2px;'
//                                })
                            ]}
                       ]
                    }]
                 }/*,
                 {
                      xtype:'panel',
                      border:false,
                      html:'<div style=\"color:SeaGreen;font-size:12px;padding-left:25px;\">* The total CPF amount is the accumulation of all component values</div>'
                 }*/]
        });
        this.grid1 = new Wtf.grid.GridPanel({
            border: false,
            layout:'fit',
            clicksToEdit : 1,
            store: this.groupGridStore,
            //store: this.addstore,
            height:350,
            frame:false,
            //            width:1200,
            autoScroll:true,
            cm: this.cmGroupGrid,
            viewConfig: {
                forceFit: true
            }
            //sm: this.sm2
        });
        this.cpfFormGridPanel = new Wtf.Panel({
            layout:'border',
            //            height:800,
            border:true,
            frame:false,
            bodyStyle:"background:rgb(241,241,241);",
            items:[{
                region:'north',
                height:300,
                border:true,
                layout:'fit',
                frame:false,
                split: true,
                autoScroll:true,
                style: 'padding:3px 25px 3px 25px;background:rgb(241,241,241);',//top right bottom left
                items:[this.form],
                bbar:[
                this.submitBtn=new Wtf.Button({
                    anchor : '90%',
                    iconCls: 'pwnd showSaveAsDraftIcon',
                    text:  WtfGlobal.getLocaleText("hrms.common.submit"),
                    handler: this.addToGrid,
                    scope:this
                })]
            },{
                region:'center',
                border:true,
                frame:false,
                layout:'fit',
                autoScroll:true,
                title:'Predefined Rules',
                style: 'padding:0px 25px 0px 25px;background:rgb(241,241,241);',
                items:[this.grid1]
            }]
        });
        this.cpfFormGridPanelRender = new Wtf.Panel({
            layout:'fit',
            border:false,
            frame:false,
            bodyStyle:"background:rgb(241,241,241);",
            items:this.cpfFormGridPanel
        });
        if(!Wtf.StoreMgr.containsKey("country")){
            Wtf.countryStore.load();
            Wtf.StoreMgr.add("country",Wtf.countryStore)
        }
        this.grid1.doLayout();
        this.cpfFormGridPanel.doLayout();
        this.add(this.cpfFormGridPanelRender);
    },
    openNewRoleWindow:function(sel,row,rec){
        this.roleNameField = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.enter.fixed.amount.calculate.difference")+'($)*',
            allowBlank: false,
            allowDecimals:false,
            allowNegative:false,
            maxValue:100,
            minValue:1,
            validator:WtfGlobal.noBlankCheck
        });
        this.groupForm = new Wtf.FormPanel({
                labelWidth: 300,
                labelAlign : 'left',
                border:false,
                bodyStyle:'padding:15px 5px 0',
                layout : 'form',
                anchor : '95%',
                defaultType: 'textfield',
                buttonAlign :'right',
                items: [this.roleNameField]
            });
        this.win=new Wtf.Window({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            title:WtfGlobal.getLocaleText("hrms.common.Role"),
            height:120,
            width:500,
            id: 'roleWin',
            modal:true,
            resizable:false,
            scope:this,
            items:[this.groupForm],
            buttonAlign :'right',
            buttons: [
            {
                text:  WtfGlobal.getLocaleText("hrms.common.Save"),
                scope:this,
                handler:function(){
                    if(!this.roleNameField.isValid()){
                        return;
                    }
                    this.fixedAmt = this.roleNameField.getValue();
                    Wtf.getCmp("roleWin").close();
                    this.onRowSelectFun(sel,row,rec);
                }
            },
            {
                text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                handler: function(){
                    Wtf.getCmp("roleWin").close();
                }
            }
            ]
        });
        this.win.show();
    },
    onRowSelect:function(sel,row,rec){
        if(row == 2){
            this.openNewRoleWindow(sel,row,rec);
        } else {
            this.onRowSelectFun(sel,row,rec);
        }
    },
    onRowSelectFun:function(sel,row,rec){
            this.child1[row] = document.createElement('div');
            this.child1[row].id = "con"+row;

            this.mchild[row] = document.createElement('div');
            this.mchild[row].className = 'mchild';
            this.mchild[row].style.cssFloat = "left";
            this.mchild[row].val = rec.get("id");
            this.mchild[row].id = "mchild"+row;

            this.bchild[row] = document.createElement('div');
            this.bchild[row].id = "addsub" + row;
            this.bchild[row].className="x-form-item";
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
            if(row == 2){
                this.mchild[row].innerHTML = rec.get("cmptvalue")+' '+rec.get("cmptprintvalue")+' '+this.fixedAmt;
            } else {
                this.mchild[row].innerHTML = rec.get("cmptvalue")+' '+rec.get("cmptprintvalue");
            }
            this.mchild[row].className="x-form-item";
            this.bchild[row].onclick = this.showRuleWin.createDelegate(this);
            
            this.child1[row].appendChild(this.bchild[row]);//operator
            this.child1[row].appendChild(this.mchild[row]);//component
            this.conContainer.appendChild(this.child1[row]);
    },
    onRowDeselect:function(sel,row,rec){
      document.getElementById('parentCon').removeChild(document.getElementById("con"+row));
      this.mchild.splice(row,1);
      this.bchild.splice(row,1);
    },
     showRuleWin:function(e){
        this.updateelement = e.currentTarget;
        var top = new Wtf.Panel({
            frame:true,
            items: [{
                layout:'form',
                items:[{
                    layout:'column',
                    items: [{
                        html:WtfGlobal.getLocaleText("hrms.payroll.ApplyRuleIf")+':<br><br>'
                    }]
                },{
                layout: 'column',
                fieldWidth:0,
                items: [
                    new Wtf.form.Radio({
                        name:'cond',
                        id:'add',
                        checked : (this.updateelement.val =='add')?true:false,
                        boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Addition")
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
                        boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Subtraction")
                    })
                ]
                }]
            },{
                layout:'column',
                items:[{
                    layout:'form',
                    buttons:[{
                        text:'OK',
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
                        text: WtfGlobal.getLocaleText("hrms.common.cancel"),
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
            title:'And/Or',
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
    getCalculationDetailTemplate:function(config){
        var bookTplMarkupFull =
        ['<div style="color: DarkSlateGray;">',
        '<table style="margin:7px">',
        '<tr><td>{percentamt}% of the employee\'s total wages for the month and</td></tr>',
        '<tr><td>{curr}{fixamt} and</td></tr>',
        '<tr><td>{mixpercent}% of the difference between the employee\'s total wages for the month and {curr}{minusamt}</td></tr>',
        '</table>',
        '</div>'];
        var bookTpl = new Wtf.Template(bookTplMarkupFull);
        var data={
            percentamt:this.percent.getValue(),
            curr:WtfGlobal.getCurrencySymbol(),
            fixamt:this.fixamt.getValue(),
            mixpercent:this.mixpercent.getValue(),
            minusamt:this.minusamt.getValue()
        };
        bookTpl.overwrite(this.calculationTemplatePanel.body, data);
    }
});
