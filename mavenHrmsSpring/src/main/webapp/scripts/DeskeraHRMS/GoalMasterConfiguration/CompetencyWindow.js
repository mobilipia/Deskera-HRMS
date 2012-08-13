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
Wtf.competencyWindow1=function(config){
    Wtf.apply(this,config);
    Wtf.competencyWindow1.superclass.constructor.call(this,{
        buttonAlign : 'right',
        buttons :[{
            text :  WtfGlobal.getLocaleText("hrms.common.Next"),
            id:"bttnnext",
            scope: this,
            disabled:true,
            handler:function() {
            	Wtf.desigStore.removeListener("load",this.setcomboVal, this);
            	this.ids1=[];
                this.wth=[];
            	for(var i=0;i<this.selectedds.getCount();i++){
                    this.ids1.push(this.selectedds.getAt(i).get("cmptid"));
                    var cmpwth=this.selectedds.getAt(i).get("cmptwt");
                    if(cmpwth==0) {
                        calMsgBoxShow(88,2);
                        return;
                    }
                    this.wth.push(cmpwth);
                }   
            	var compSum=0;
            	for(var i=0;i<this.wth.length;i++){
            		compSum+=this.wth[i];
            	}
            	if(compSum!=100 && compSum!=0){
            		calMsgBoxShow(146,0);
            		return false;
            	}
            	this.desid=this.groupForm.findById('desigCmb').getValue();
            	Wtf.desigStore.clearFilter();
                this.winpos= new Wtf.competencyNextWindow1({
                    width:700,
                    modal:true,
                    ids1:this.ids1,
                    wth:this.wth,
                    desid:this.desid,
                    height:600,
                    id:"questioncomp",
                    title:WtfGlobal.getLocaleText("hrms.performance.AssignQuestions"),
                    resizable:false,
                    layout:'fit',
                    desigid:this.desigCmb.getValue()
                });
                this.close();
                this.winpos.show();
            }
        },{
            text :  WtfGlobal.getLocaleText("hrms.common.Save"),
            id:"bttntext",
            scope: this,
            disabled:true,
            handler:function() {
                this.createMemberList();
            }
        },{
            text :  WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope : this,
            handler : function(){
                this.close();
            }
        }]
    });
    this.addEvents({
        "savedata": true
    });
}

Wtf.extend(Wtf.competencyWindow1, Wtf.Window, {
    group_id:"",
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender:function(config){
        Wtf.competencyWindow1.superclass.onRender.call(this,config);
        
        this.desigCmb = new Wtf.form.FnComboBox({
            store:Wtf.desigStore,
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.designation")+'*',
            mode:'local',
            id:'desigCmb',          
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            allowBlank:false,
            typeAhead:true,
            width:200,
            addNewFn:this.addDesignation.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
                },
                scope: this
            })],
            listeners:{
                scope:this,
                select:function(combo,record,index)
                {
                    this.availableds.load({
                        params:{
                            flag:115,
                            desig:this.desigCmb.getValue()
                        }
                    });
                    this.availablegrid.doLayout();
                    this.selectedds.load({
                        params:{
                            flag:116,
                            desig:this.desigCmb.getValue()
                        }
                    });
                    this.selectedgrid.doLayout();
                }
            }
        });
        this.availableRec = new Wtf.data.Record.create([
        {
            name:'cmptid'
        },

        {
            name:'cmptname'
        },
        {
            name:'cmptwt'
        }
        ]);
        
        this.availableds = new Wtf.data.Store({
            url: "Performance/Competency/getCompetencyAvailable.pf",
            reader: new Wtf.data.KwlJsonReader1({
                root:'data',
                totalProperty: 'count'
            },this.availableRec),
            fields :['cmptid', 'cmptname','cmptwt']
        });


        this.availablesm = new Wtf.grid.CheckboxSelectionModel();
        this.ratingRadio = new Wtf.form.Radio({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.Quantitative"),
            id:'Quantitative'+this.id,
            bodyStyle:"float:left",
            checked:true,
            name:'emptemp'
        });

        this.quesRadio = new Wtf.form.Radio({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.Qualitative"),
            id:'Qualitative'+this.id,
            bodyStyle:"float:left",
            name:'emptemp'
        });

        this.availablecm = new Wtf.grid.ColumnModel([this.availablesm,
        {
            header: WtfGlobal.getLocaleText("hrms.performance.competency"),
            dataIndex: 'cmptname',
            autoWidth: true,
            sortable: true,
            groupable: true
        }
        ]);
        this.quickSearchEmp = new Wtf.wtfQuickSearch({
            width: 150,
            field:"cmptname",
            emptyText:WtfGlobal.getLocaleText("hrms.performance.manage.competency.grid.search.msg")
        });
        this.emptempfield=new Wtf.Panel({
            frame:false,
            border:false,
            width:350,
            layout:'column',
            id:"mypanel",
            bodyStyle:"margin-top:10px;",
            items:[
            {
                frame:false,
                columnWidth:0.5,
                border:false,
                layout:'form',
                items:[this.ratingRadio]
            },
            {
                frame:false,
                border:false,
                columnWidth:0.5,
                layout:'form',
                items:[this.quesRadio]
            }]
        });

        this.ratingRadio.on("check",function(a,b){
            if(b){
                Wtf.getCmp('bttnnext').disable();
                Wtf.getCmp('bttntext').enable();
            }
        },this);
        this.quesRadio.on("check",function(a,b){
           if(b){
                Wtf.getCmp('bttnnext').enable();
                Wtf.getCmp('bttntext').disable();
           }
        },this);
        this.ratingRadio.on("check",function(a,b){
            if(this.ratingRadio.rendered && this.quesRadio.rendered){
                this.ratingRadio.onClick();
                this.quesRadio.onClick();
            }
        },this);
        this.quesRadio.on("check",function(a,b){
            if(this.ratingRadio.rendered && this.quesRadio.rendered){
                this.ratingRadio.onClick();
                this.quesRadio.onClick();
            }
        },this);

        this.availablegrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store: this.availableds,
            cm: this.availablecm,
            border:false,
            id:this.id+'availablegrid',
            sm : this.availablesm,
            autoScroll:true,
            viewConfig: {
                forceFit: true,
                autoFill:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.NoCompetencytoassign"))
            },
            tbar : [this.quickSearchEmp]
        });

        this.availableds.on("load",this.empSearch,this);
        
        this.selectedRec = new Wtf.data.Record.create([
        {
            name:'cmptid'
        },

        {
            name:'cmptname'
        },
        {
            name:'cmptwt'
        }
        ]);

        this.selectedds = new Wtf.data.Store({
            url: "Performance/Competency/getCompetencyAssigned.pf",
            reader: new Wtf.data.KwlJsonReader1({
                root:'data',
                totalProperty: 'count'
            },this.selectedRec),
            autoLoad : false
        });
        this.selectedsm = new Wtf.grid.CheckboxSelectionModel();
        this.selectedcm = new Wtf.grid.ColumnModel([this.selectedsm,
        {
            header:  WtfGlobal.getLocaleText("hrms.performance.competency"),
            dataIndex: 'cmptname',
            autoWidth: true,
            sortable: true,
            summaryRenderer:WtfGlobal.total,
            groupable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.performance.weightage"),
            dataIndex: 'cmptwt',
            autoWidth: true,
            summaryType:'sum',
            renderer:WtfGlobal.numericRenderer,
            editor: new Wtf.form.NumberField({
                allowBlank: false,
                allowDecimals:false,
                allowNegative:false,
                maxValue:100,
                minValue:1,
                validator:WtfGlobal.noBlankCheck
            }),
            sortable: true

        }
        ]);
        this.quickSearchAssgEmp = new Wtf.wtfQuickSearch({
            width: 150,
            field:"cmptname",
            emptyText:WtfGlobal.getLocaleText("hrms.performance.manage.competency.grid.search.msg")
        });
        this.selectedgrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store: this.selectedds,
            cm: this.selectedcm,
            sm : this.selectedsm,
            autoScroll:true,
            border:false,
            plugins:new Wtf.ux.grid.GridSummary(),
            clicksToEdit : 1,
            viewConfig: {
                forceFit: true
            },
            tbar : [this.quickSearchAssgEmp]
        });
        this.selectedds.on("load",this.empAssgSearch,this);
        
        if(!Wtf.StoreMgr.containsKey("desig")){
        	Wtf.desigStore.on('load',this.setcomboVal,this),
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }else{
            this.setcomboVal();
        }

        this.movetoright = document.createElement('img');
        this.movetoright.src = "../../images/arrowright.gif";
        this.movetoright.style.width = "24px";
        this.movetoright.style.height = "24px";
        this.movetoright.style.margin = "5px 0px 5px 0px";
        this.movetoright.onclick = this.movetorightclicked.createDelegate(this,[]);

        this.movetoleft = document.createElement('img');
        this.movetoleft.src = "../../images/arrowleft.gif";
        this.movetoleft.style.width = "24px";
        this.movetoleft.style.height = "24px";
        this.movetoleft.style.margin = "5px 0px 5px 0px";
        this.movetoleft.onclick = this.movetoleftclicked.createDelegate(this,[]);

        this.centerdiv = document.createElement("div");
        this.centerdiv.appendChild(this.movetoright);
        this.centerdiv.appendChild(this.movetoleft);
        this.centerdiv.style.padding = "135px 10px 135px 10px";
        var id="1";
        var msg1="";
        var head="";
        if(this.text1=='edit')
        {
            this.bttntext= WtfGlobal.getLocaleText("hrms.common.Update");
            var flag=9;
            id=this.group_id;
            msg1=WtfGlobal.getLocaleText("hrms.performance.group.updated.successfully");
            head=WtfGlobal.getLocaleText("hrms.performance.edit.group");
            this.imgsrc = 'edit-group.gif';
        }
        else
        {
            this.bttntext= WtfGlobal.getLocaleText("hrms.common.Create");
            var flag=6;
            id=this.group_id;
            msg1=WtfGlobal.getLocaleText("hrms.performance.group.added.successfully");
            head=WtfGlobal.getLocaleText("hrms.performance.create.new.group");
            this.imgsrc = 'add-group.gif';
        }
        this.assignTeamPanel = new Wtf.Panel({
            layout : 'border',
            items :[{
                region : 'north',
                height : 80,
                border : false,
                cls : 'panelstyleClass1',
                html : getTopHtml(WtfGlobal.getLocaleText("hrms.performance.competency.management"),WtfGlobal.getLocaleText("hrms.performance.Selectdesignationforassigningcompetencies"), "../../images/compentencymanagement.jpg")
            },{
                region : 'center',
                border : false,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 20px 20px;',
                layout : 'fit',
                items : [{
                    border : false,
                    bodyStyle : 'background:transparent;',
                    layout : 'border',
                    items : [{
                        region : 'north',
                        border : false,
                        height : 70,
                        layout : 'border',
                        bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 20px 20px;',
                        items:[{
                            region:'center',
                            border:false,
                            layout : 'fit',
                            width:400,
                            items:[this.groupForm=new Wtf.form.FormPanel({
                                labelWidth:80,
                                bodyStyle:"mar",
                                bodyStyle:"padding-left:160px;",
                                border:false,
                                items:[ this.desigCmb,this.emptempfield,{
                                    xtype:"hidden",
                                    id:"h1",
                                    name:"user_ids"
                                }
                                ]
                            }) ]
                        }
                        ]
                    },
                    {
                        region : 'west',
                        border : false,
                        width : 300,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title : WtfGlobal.getLocaleText("hrms.performance.competencies"),
                            border : false,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.availablegrid
                        }]
                    },{
                        region : 'center',
                        border : false,
                        contentEl : this.centerdiv
                    },{
                        region : 'east',
                        border : false,
                        width : 300,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title :  WtfGlobal.getLocaleText("hrms.performance.AssignedCompetencies"),
                            border : false,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.selectedgrid
                        }]
                    }]
                }]
            }]
        });

        
        this.add(this.assignTeamPanel);

    },
    setcomboVal: function(){
        if(Wtf.desigStore.getCount()!=0){
            if(this.desname!=""){
                this.desigCmb.setValue(this.desigid);
            }else{
                var id=Wtf.desigStore.getAt(Wtf.desigStore.getCount()-1).get('id');
                this.desigCmb.setValue(id);
                this.desigid=id;
            }
            this.availableds.load({
                params:{
                    flag:115,
                    grouper:'assigncomp',
                    desig:this.desigCmb.getValue()
                }
            });
            this.availablegrid.doLayout();
            this.selectedds.load({
                params:{
                    flag:116,
                    grouper:'assigncomp',
                    firequery:'1',
                    desig:this.desigid
                }
            });
            this.selectedds.on("load",this.empAssgSearch,this);
            this.selectedgrid.doLayout();
        }
        else
            this.desigCmb.setValue(WtfGlobal.getLocaleText("hrms.performance.Nodesignationavailable"));
    },
    createMemberList: function(){
        this.ids1=[];
        this.wth=[];
        var sum=0;
        for(var i=0;i<this.selectedds.getCount();i++){
            this.ids1.push(this.selectedds.getAt(i).get("cmptid"));
            var cmpwth=this.selectedds.getAt(i).get("cmptwt");
            
            if(cmpwth!=parseInt(cmpwth)){
            	calMsgBoxShow(233,2);
                return;
            }
            
            if(cmpwth==0) {
                calMsgBoxShow(88,2);
                return;
            }
            this.wth.push(cmpwth);
            sum = sum +parseFloat(cmpwth);
        }       
        if(sum==100 ) {
            if(this.desigCmb.getValue()!='')
            {
                calMsgBoxShow(200,4,true);
                Wtf.Ajax.requestEx({
                    url:"Performance/Competency/assignCompetency.pf",
                    params: {
                        flag:104,
                        item_ids:this.ids1,
                        wth:this.wth,
                        desid:this.groupForm.findById('desigCmb').getValue()
                    }
                }, this,
                function(response){
                    this.fireEvent('savedata',this);
                    calMsgBoxShow(36,0);
                    this.close();
                },
                function(response){
                    calMsgBoxShow(27,1);
                    this.close();
                })
            }
            else{

                calMsgBoxShow(37,0);
            }
        }
        else{
            calMsgBoxShow(146,0);
        }
    }, 
    empSearch: function(store, rec, opt) {
        this.quickSearchEmp.StorageChanged(store);
    },
    empAssgSearch: function(store, rec, opt) {
        this.quickSearchAssgEmp.StorageChanged(store);
        if(this.selectedds.getCount()>0)
            Wtf.getCmp('bttntext').enable();
        else
            Wtf.getCmp('bttntext').disable();
    },

    movetorightclicked : function() {
        var selected = this.availablesm.getSelections();
        if(selected.length>0){
            this.selectedds.add(selected);
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.availableds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            Wtf.getCmp('bttntext').enable();
        else
            Wtf.getCmp('bttntext').disable();
    },

    movetoleftclicked : function() {

        var selected = this.selectedsm.getSelections();
        if(selected.length>0){
            this.availableds.add(selected);
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.selectedds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            Wtf.getCmp('bttntext').enable();
        else
            Wtf.getCmp('bttntext').disable();
    },
    addDesignation:function(){
        WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
    }

});
function adddesgcomp(Id){
    var grid=Wtf.getCmp(Id);
    var desg=Wtf.getCmp('desigCmb').getValue();
    var addcomp=new Wtf.managecompetencyWindow({
        width:390,
        modal:true,
        height:265,
        title:WtfGlobal.getLocaleText("hrms.performance.competency"),
        resizable:false,
        layout:'fit',
        wintitle:WtfGlobal.getLocaleText("hrms.performance.add.competency"),
        editflag:false,
        datastore:grid.getStore(),
        cleargrid:grid,
        desig:desg,
        action:"add"
    });
    addcomp.show();
}


Wtf.competencyNextWindow1=function(config){
    Wtf.apply(this,config);
    Wtf.competencyNextWindow1.superclass.constructor.call(this,{
        buttonAlign : 'right',
        buttons :[{
            text :  WtfGlobal.getLocaleText("hrms.common.back"),
            id:this.id+"bttnback",
            scope: this,
            handler:function() {
            	Wtf.desigStore.removeListener("load",this.setcomboVal1, this);
            	Wtf.desigStore.removeListener("load",this.setAllValue, this);
                this.windowpos= new Wtf.competencyWindow1({
                    width:700,
                    modal:true,
                    height:600,
                    title:WtfGlobal.getLocaleText("hrms.performance.assign.competency"),
                    resizable:false,
                    layout:'fit',
                    desname:"designame",
                    desigid:this.desigid
                });
                this.windowpos.show();
                this.close();
            }
        },{
            text :  WtfGlobal.getLocaleText("hrms.common.Save"),
            id:this.id+"bttntext",
            scope: this,
            handler:function() {
                this.saveQuestionCompetency();
            }
        },{
            text :  WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope : this,
            handler : function(){
                this.close();
            }
        }]
    });
    this.addEvents({
        "savedata": true
    });
}

Wtf.extend(Wtf.competencyNextWindow1, Wtf.Window, {
    group_id:"",
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender:function(config){
        Wtf.competencyNextWindow1.superclass.onRender.call(this,config);

        this.desigCmb = new Wtf.form.FnComboBox({
            store:Wtf.desigStore,
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.designation")+'*',
            mode:'local',
            id:this.id+'desigNewCmb',
            valueField: 'id',
            displayField:'name',
            disabled:true,
            triggerAction: 'all',
            allowBlank:false,
            typeAhead:true,
            value:this.desigid,
            width:200
        });
        
        this.on("close",function(){
        	Wtf.desigStore.remove(this.def);
        }, this);
        
        Wtf.desigStore.on("load",this.setAllValue, this);

        this.addbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.AddQuestion"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.AddQuestion.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:70,
            scope:this,
            handler:this.insertgoal
        });
        this.deletebutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.DeleteQuestions"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.DeleteQuestions.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:70,
            scope:this,
            disabled:true,
            handler:this.deletequestions
        });
        var empgoalbtnstbar=new Array();

        empgoalbtnstbar.push(this.addbutton);
        empgoalbtnstbar.push(this.deletebutton);
        
        this.ratingRadio = new Wtf.form.Radio({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.Quantitative"),
            id:'Quantitative'+this.id,
            bodyStyle:"float:left",
            disabled:true,
            name:'emptemp'
        });

        this.quesRadio = new Wtf.form.Radio({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.Qualitative"),
            id:'Qualitative'+this.id,
            bodyStyle:"float:left",
            checked:true,
            disabled:true,
            name:'emptemp'
        });

    
        this.emptempfield=new Wtf.Panel({
            frame:false,
            border:false,
            layout:'column',
            width:350,
            id:this.id +"mypanel",
            bodyStyle:"margin-top:10px;",
            items:[
            {
                frame:false,
                columnWidth:0.5,
                border:false,
                layout:'form',
                items:[this.ratingRadio]
            },
            {
                frame:false,
                border:false,
                columnWidth:0.5,
                layout:'form',
                items:[this.quesRadio]
            }]
        });

        this.widthstore = new Wtf.data.SimpleStore({
            fields:['id','value'],
            data: [
            ['50',WtfGlobal.getLocaleText("hrms.performance.Half")],
            ['100',WtfGlobal.getLocaleText("hrms.performance.Full")]
            ]
        });

        this.widthCombo=new Wtf.form.ComboBox({
            store:this.widthstore,
            displayField: 'value',
            valueField:'id',
            value:'50',
            forceSelection: true,
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            width:200
        });

        this.text1=new Wtf.form.TextField({
            allowBlank:false,
            validator:WtfGlobal.noBlankCheck,
            maxLength:1000
        });
        
        Wtf.desigRec = new Wtf.data.Record.create([{
    		name:'id'
		},{
    		name:'name'
		}]);
        
        this.def = new Wtf.desigRec({
            id:"0",
            name:'All'
        });
        Wtf.desigStore.add(this.def);
        
        this.wthcombo= new Wtf.form.FnComboBox({
            store:Wtf.desigStore,
            displayField:'name',
            scope:this,
            selectOnFocus:true,
            width:200,
            allowBlank:false,
            typeAhead:true,
            valueField:'id',
            mode:'local',
            hiddenName:"id",
            height:200,
            triggerAction :'all',
            addNewFn:this.addDesignation.createDelegate(this)
        });
        this.isVisible = new Wtf.grid.CheckColumn({
        	header:  WtfGlobal.getLocaleText("hrms.common.Visible"),
            dataIndex: 'isVisible',
        	width: 45
        });
        this.selectedquestion = new Wtf.grid.CheckboxSelectionModel();
        this.cm1=new Wtf.grid.ColumnModel([
            this.selectedquestion,
            {
                header: WtfGlobal.getLocaleText("hrms.performance.question"),
                sortable: true,
                editor: this.text1,
                dataIndex: 'qdesc',
                hideable:false,
                renderer:function(val){
            		return WtfGlobal.commentRenderer(val);
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.NoofAnswers"),
                sortable: true,
                editor:  new Wtf.form.NumberField({
                                allowBlank: false,
                                allowNegative: false,
                                allowDecimals : false,
                                validationDelay:0,
                                maxValue: 5
                        }),
                dataIndex: 'qans',
                hideable:false
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.OrderofAppearance"),
                sortable: true,
                editor:  new Wtf.form.NumberField({
                                allowBlank: false,
                                allowNegative: false,
                                allowDecimals : false,
                                validationDelay:0,
                                maxValue: 100,
                                minValue:1
                        }),
                dataIndex: 'qorder',
                hideable:false
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.Width"),
                sortable: true,
                editor:  this.widthCombo,
                dataIndex: 'qtype',
                hideable:false,
                renderer: Wtf.ux.comboBoxRenderer(this.widthCombo)
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.AppraiserDesignation"),
                sortable:true,
                editor:this.wthcombo,
                dataIndex:'qdes',
                renderer: Wtf.ux.comboBoxRenderer(this.wthcombo)
            },
            this.isVisible]);
        this.goalRecord=Wtf.data.Record.create([
        {
            name:'qdescription'
        },
        {
            name:'qdesc'
        },
        {
            name:'qans'
        },
        {
            name:'qdes'
        },
        {
            name:'qtype'
        },
        {
            name:'qorder'
        },
        {
            name:'isVisible'
        }]);
        this.goalReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.goalRecord);
        this.goalstore= new Wtf.data.Store({
            url:"Performance/Competency/getCompetencyQuestion.pf",
            reader:this.goalReader,
            baseParams:{
                flag:29
            }
        });
        this.quesgrid = new Wtf.grid.EditorGridPanel({
            store: this.goalstore,
            sm:this.selectedquestion,
            height:100,
            autoScroll :true,
            border:false,
            nopaging:true,
            id:'quesGrid'+this.id,
            scope:this,
            plugins:[this.isVisible],
            clicksToEdit :1,
            serverSideSearch:false,
            displayInfo:true,
            viewConfig: {
                forceFit: true,
                emptyText:Wtf.gridEmptytext
            },
            tbar:empgoalbtnstbar,
            cm: this.cm1
        });
        
        this.selectedquestion.on("selectionchange",function(){
        	if(this.selectedquestion.getCount()>0)
        		this.deletebutton.setDisabled(false);
        	else
        		this.deletebutton.setDisabled(true);
        },this);
        
        this.quesgrid.on("validateedit",function(e){
            if(e.field == "qdesc"){
                if(e.value.length > 1000){
                    return false;
                }
            }
        }, this);
        
        if(!Wtf.StoreMgr.containsKey("desig")){
            Wtf.desigStore.on('load',this.setcomboVal1,this),
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }else{
            this.setcomboVal1();
        }

        this.movetoright = document.createElement('img');
        this.movetoright.src = "../../images/arrowright.gif";
        this.movetoright.style.width = "24px";
        this.movetoright.style.height = "24px";
        this.movetoright.style.margin = "5px 0px 5px 0px";
        this.movetoright.onclick = this.movetorightclicked.createDelegate(this,[]);

        this.movetoleft = document.createElement('img');
        this.movetoleft.src = "../../images/arrowleft.gif";
        this.movetoleft.style.width = "24px";
        this.movetoleft.style.height = "24px";
        this.movetoleft.style.margin = "5px 0px 5px 0px";
        this.movetoleft.onclick = this.movetoleftclicked.createDelegate(this,[]);

        this.centerdiv = document.createElement("div");
        this.centerdiv.appendChild(this.movetoright);
        this.centerdiv.appendChild(this.movetoleft);
        this.centerdiv.style.padding = "135px 10px 135px 10px";
        var id="1";
        var msg1="";
        var head="";
        if(this.text1=='edit')
        {
            this.bttntext= WtfGlobal.getLocaleText("hrms.common.Update");
            var flag=9;
            id=this.group_id;
            msg1=WtfGlobal.getLocaleText("hrms.performance.group.updated.successfully");
            head=WtfGlobal.getLocaleText("hrms.performance.edit.group");
            this.imgsrc = 'edit-group.gif';
        }
        else
        {
            this.bttntext= WtfGlobal.getLocaleText("hrms.common.Create");
            var flag=6;
            id=this.group_id;
            msg1=WtfGlobal.getLocaleText("hrms.performance.group.added.successfully");
            head=WtfGlobal.getLocaleText("hrms.performance.create.new.group");
            this.imgsrc = 'add-group.gif';
        }
        this.assignTeamPanel = new Wtf.Panel({
            layout : 'border',
            items :[{
                region : 'north',
                height : 80,
                border : false,
                cls : 'panelstyleClass1',
                html : getTopHtml(WtfGlobal.getLocaleText("hrms.performance.competency.management"),WtfGlobal.getLocaleText("hrms.performance.Addquestionsforselecteddesignation"), "../../images/compentencymanagement.jpg")
            },{
                region : 'center',
                border : false,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 20px 20px;',
                layout : 'fit',
                items : [{
                    border : false,
                    bodyStyle : 'background:transparent;',
                    layout : 'border',
                    items : [{
                        region : 'north',
                        border : false,
                        height : 70,
                        layout : 'border',
                        bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 20px 20px;',
                        items:[{
                            region:'center',
                            border:false,
                            layout : 'fit',
                            width:400,
                            items:[this.groupForm=new Wtf.form.FormPanel({
                                labelWidth:80,
                                bodyStyle:"padding-left:160px;",
                                border:false,
                                items:[ this.desigCmb,this.emptempfield,{
                                    xtype:"hidden",
                                    id:"h1",
                                    name:"user_ids"
                                }
                                ]
                            }) ]
                        }
                        ]
                    },
                    {
                        region : 'center',
                        border : false,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title : WtfGlobal.getLocaleText("hrms.performance.Questions"),
                            border : false,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.quesgrid
                        }]
                    }]
                }]
            }]
        });

        this.goalstore.load({
                params:{
                    desig:this.desigid
                }
            });
        this.add(this.assignTeamPanel);

    },
    insertgoal:function(){

        this.p=new this.goalRecord({
            qdescription:'',
            qdesc:'',
            qans:'',
            qdes:'',
            qtype:'100',
            qorder:'',
            isVisible:''
        });
        this.quesgrid.stopEditing();

        this.c=this.goalstore .getCount();

        this.goalstore.insert(this.c,this.p);

    },
    deletequestions:function(){
    	Wtf.MessageBox.show({
    		title: WtfGlobal.getLocaleText("hrms.common.confirm"),
            msg:deleteMsgBox('question'),
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                	this.delkey=this.selectedquestion.getSelections();
                    this.ids=[];
                    this.selectedquestion.clearSelections();
                    var store=this.quesgrid.getStore();
                    if(this.delkey.length>0)
                    {
                    	for(var i=0;i<this.delkey.length;i++){
                            var rec=this.goalstore.indexOf(this.delkey[i]);
                            WtfGlobal.highLightRow(this.quesgrid,"FF0000",5, rec)
                            if(this.delkey[i].get('qdescription'))
                            {
                                this.ids.push(this.delkey[i].get('qdescription'));
                            }
                            else{
                                store.remove(this.delkey[i]);
                            }
                        }
                        if(this.ids.length>0)
                        {
                            calMsgBoxShow(201,4,true);
                            Wtf.Ajax.requestEx({
                            	url:"Performance/Competency/deleteCompetencyQuestion.pf",
                                params:{
                                    flag:45,
                                    ids:this.ids,
                                    desigid:this.desigid
                                }
                            },this,
                            function(response){
                            	var myObject = eval('(' + response + ')');
                            	if(myObject.success==true){
                            		calMsgBoxShow(189,0);
                            	}else{
                            		calMsgBoxShow(228,2);
                            	}
                            	this.goalstore.load({
                        			params:{
                        				desig:this.desigid
                        			}
                        		});
                        		//WtfGlobal.delaytasks(this.goalstore);
                            },
                            function(response){
                                calMsgBoxShow(228,1);
                            });
                        }
                    }else{
                    	calMsgBoxShow(227,0);
                    }
                }
            }
        });
    },
    setcomboVal1: function(){
        if(Wtf.desigStore.getCount()!=0){
            if(this.desname!=""){
                this.desigCmb.setValue(this.desigid);
            }else{
                var id=Wtf.desigStore.getAt(Wtf.desigStore.getCount()-1).get('id');
                this.desigCmb.setValue(id);
                this.desigid=id;
            }
           
        }
        else
            this.desigCmb.setValue(WtfGlobal.getLocaleText("hrms.performance.Nodesignationavailable"));
    },
    setAllValue: function(){
    	if(Wtf.desigStore.indexOf(this.def)==-1)
    		Wtf.desigStore.add(this.def);
    },
    saveQuestionCompetency:function(){
        var jsondata = new Array();
        var record;
        var bFields="";
        var vflag=1;
        var columnArr = ["qdescription", "qans", "qdes"];
        var columnArrName = ["Question", "No.of Ans", "Designation"];
        var records = this.goalstore.data;
        if(records.length==0){
        	this.close();
        }else{
            if(vflag==1){
                for(var i=0;i<records.length ;i++){
                    record=records.items[i].data;
                    if(record.qdesc == ""){
                        calMsgBoxShow(186,2);
                        return;
                    }
                    if(record.qorder == ""){
                        calMsgBoxShow(188,2);
                        return;
                    }
                    if(record.qans == ""){
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.performance.Noofanswerscannotbeempty")],2);
                        return;
                    }
                    for(var j=0;j<records.length;j++) {
                        var temprec1 = records.items[j].data;
                        if(temprec1.qorder == record.qorder && i != j) {
                               calMsgBoxShow(187,2);
                               return;
                        }
                    }
                    for(var k=0;k<this.goalstore.getCount();k++) {
                        var temprec2 = this.goalstore.getAt(k);
                       if(temprec2.get('qorder') == record.qorder && temprec2.get('qdescription') != record.qdescription)
                           {
                               calMsgBoxShow(187,2);
                               return;
                           }
                    }
                    
                    jsondata.push({
                		qdescription:record.qdesc,
                        qid:record.qdescription,
                        qans:record.qans,
                        qorder:record.qorder,
                        qtype:record.qtype,
                        qdes:record.qdes,
                        isVisible:record.isVisible});
                }

                calMsgBoxShow(200,4,true);
                Wtf.desigStore.clearFilter();
                Wtf.Ajax.requestEx({
                    url:"Performance/Competency/insertQuestion.pf",
                    params: {
                        jsondata:Wtf.encode(jsondata),
                        desigid:this.desigid
                    }
                }, this,
                function(response){
                    this.close();
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.performance.Questionaddedsuccessfully")],0);
                },
                function(response)
                {
                    calMsgBoxShow(229,1);
                }),
                Wtf.Ajax.requestEx({
                    url:"Performance/Competency/assignCompetency.pf",
                    params: {
                        flag:104,
                        item_ids:this.ids1,
                        wth:this.wth,
                        desid:this.desid
                    }
                }, this,
                function(response){
                    this.fireEvent('savedata',this);
                    this.close();
                },
                function(response){
                    calMsgBoxShow(27,1);
                    this.close();
                })
            }
        }
    },
    empSearch: function(store, rec, opt) {
        this.quickSearchEmp.StorageChanged(store);
    },
    empAssgSearch: function(store, rec, opt) {
        this.quickSearchAssgEmp.StorageChanged(store);
        if(this.selectedds.getCount()>0)
            Wtf.getCmp('bttntext').enable();
        else
            Wtf.getCmp('bttntext').disable();
    },

    movetorightclicked : function() {
        var selected = this.availablesm.getSelections();
        if(selected.length>0){
            this.selectedds.add(selected);
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.availableds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            Wtf.getCmp('bttntext').enable();
        else
            Wtf.getCmp('bttntext').disable();
    },

    movetoleftclicked : function() {

        var selected = this.selectedsm.getSelections();
        if(selected.length>0){
            this.availableds.add(selected);
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.selectedds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            Wtf.getCmp('bttntext').enable();
        else
            Wtf.getCmp('bttntext').disable();
    },
    addDesignation:function(){
        WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
    }

});

//-------------- CheckColumn Plugin -------------------------//
Wtf.grid.CheckColumn = function(config){
 Wtf.apply(this, config);
 if(!this.id){
     this.id = Wtf.id();
 }
 this.renderer = this.renderer.createDelegate(this);
};

Wtf.grid.CheckColumn.prototype ={
 init : function(grid){
     this.grid = grid;
     this.grid.on('render', function(){
         var view = this.grid.getView();
         view.mainBody.on('mousedown', this.onMouseDown, this);
     }, this);
 },

 onMouseDown : function(e, t){
     if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
         e.stopEvent();
         var index = this.grid.getView().findRowIndex(t);
         var record = this.grid.store.getAt(index);
         record.set(this.dataIndex, !record.data[this.dataIndex]);
     }
 },

 renderer : function(v, p, record){
     p.css += ' x-grid3-check-col-td';
     return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
 }
};
