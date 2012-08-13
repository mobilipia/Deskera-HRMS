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
Wtf.userPayCycleGrid=function(config){
    config.border=false;
    config.layout="fit";
    this.addEvents(

        "assignemployee"

        );
    Wtf.userPayCycleGrid.superclass.constructor.call(this,config);
    Wtf.apply(this, config);
}
Wtf.extend(Wtf.userPayCycleGrid,Wtf.Panel,{
    initComponent:function(config){
        this.grid = this.getGrid();
        this.designationId = "";
        this.add(this.grid);
        this.grid.on("rowclick",function(grid,rowindex,event){
            if(event.getTarget("div[class='pwndCommon gridCancel']")){
                var rec = grid.store.getAt(rowindex);
                if(rec.get("salaryflag") == "1"){
                    calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.Salaryisalreadygeneratedusingthistemplatesoyoucannotunassignemployee")], 2);
                } else {
                    grid.store.remove(rec);
                }
            }
        })
        this.on('activate', function(tp, tab){
            this.doLayout();
            
        });
        this.assignedempstore.load({params:{templateid:this.templateid,grouper:"addpayroll",firequery:'1'}});
        

    },
    setDesignationId : function(val){
        this.designationId = val;
      
    },
    setpayinterval : function(val){
        this.payinterval = val;

    },
    seteffdate : function(val){
        this.effdate = val;
        this.datearr=[];
        if(this.payinterval==3){
            for(var ctr=0;ctr<7;ctr++){
                if(val-1!=ctr)
                    this.datearr.push(ctr);
            }
        }
    },
    deleteRenderer:function(a,b,c,d){
        if(c.data.id !='-1')
            return "<div><div class='pwndCommon gridCancel' style='cursor:pointer' wtf:qtip="+WtfGlobal.getLocaleText("hrms.common.DeleteRecord")+"></div></div>";
    },
    addBlankRow : function(){
        this.assignedempstore.add(new Wtf.data.Record(['','']));
    },
    getAllEmployeeGrid:function(){
        var sm = new Wtf.grid.CheckboxSelectionModel({});
        var cm = new Wtf.grid.ColumnModel([sm,
        {
            header:WtfGlobal.getLocaleText("hrms.payroll.Employee"),
            dataIndex:'fullname'
            
        },{
            header: WtfGlobal.getLocaleText({key:"hrms.payroll.Basicsym",params:[WtfGlobal.getCurrencySymbol()]}),
            dataIndex:'basic',
            minValue: 1,
        	allowNegative:false,
        	allowBlank:false,
            editor:new Wtf.form.NumberField({
                width:155,
                maxLength: 10
            })
        },{
            header:WtfGlobal.getLocaleText("hrms.common.EffectiveFrom"),
            dataIndex:'effectivedate',
            editor:new Wtf.form.DateField({
                width:155,
                readOnly:true,
                emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
                disabledDaysText:WtfGlobal.getLocaleText("hrms.payroll.select.another.day"),
                format:'m/d/Y',
                disabledDays : this.datearr
               
            }),
            renderer:WtfGlobal.onlyDateRenderer
            
        }
        ])
        this.employeeSelectionGrid = new Wtf.grid.EditorGridPanel({
            store:this.allEmpStore,
            cm:cm,
            sm:sm,
            clicksToEdit:1,
            viewConfig:{
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.NoEmployeeisassignedtothisDesignation")),
                autoFill:true
            },
            border:false

        });
        return this.employeeSelectionGrid;
    },
    getToolbar:function(){
        this.assignEmployeeWin = "";
        this.assignEmpBtn = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.assignemployee"),
            iconCls:'pwndCommon profilebuttonIcon',
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.Pleaseselectadesignationfirstthenassignemployee"),
            handler:function(){
//                if(this.designationId != null&&this.designationId != ""&&this.payinterval != null&&this.effdate != null){
                if(this.designationId != null&&this.designationId != "" && ((this.payinterval!=null && this.payinterval!="") || (this.effdate!=null && this.effdate!=""))){
                var searchJson ='{"root":[{ "iscustom":false,"column":"ua.designationid.id","searchText":"'+this.designationId+'","columnheader":"Designation","search":"'+this.designationId+'","combosearch":"undefined","xtype":"combo"}]}'
                this.allEmpStore.on("load",function(){
                      this.quickSearchEMP.StorageChanged(this.allEmpStore);
                    this.allEmpStore.filterBy(function(record,id){
                            record.set("effectivedate",new Date());
                            return true;
                    },this);
                },this)
                this.allEmpStore.load({
                    params:{
                        "searchJson":searchJson,
                        mode:114
                    }
                    });
                this.quickSearchEMP = new Wtf.wtfQuickSearch({
                       width: 200,
                       field : "fullname",
                       emptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg")
                       });




                this.assignEmployeeWin = new Wtf.Window({
                    width:400,
                    height:400,
                    id:"assignEmplyeeWin",
                    iconCls: getButtonIconCls(Wtf.btype.winicon),
                    modal:true,
                    title:WtfGlobal.getLocaleText("hrms.payroll.SelectEmployees"),
                    layout:'border',
                    items:[{
                        region: 'north',
                        height: 90,
                        border: false,
                        bodyStyle:"backgroubodyStylend-color:white;padding:8px;border-bottom:1px solid #bfbfbf;background-color: white",
                        html: getTopHtml(WtfGlobal.getLocaleText("hrms.payroll.assignemployee"),WtfGlobal.getLocaleText("hrms.payroll.Seteffectivedateandselectemployeestobeassignedtothetemplate"),this.typeimage)
                    },{
                        border:false,
                        region:'center',
                        layout:'fit',
                        height:300,
                        tbar:[WtfGlobal.getLocaleText("hrms.common.QuickSearch")+": ",this.quickSearchEMP],
                        items:[this.getAllEmployeeGrid()]
                        }],
                    buttons:[{
                        text:WtfGlobal.getLocaleText("hrms.payroll.Assign"),
                        handler:function(){
                            var recArray = this.employeeSelectionGrid.getSelectionModel().getSelections();
                              var recJSON = "[";
                              var empNames = "";
        for(var recCnt=0;recCnt<recArray.length;recCnt++){
            var joinDate = recArray[recCnt].get("joindate");
            if(joinDate == null || joinDate== undefined || joinDate == "") {
                empNames += recArray[recCnt].get("fullname")+"<br/>";
            }
            if(recArray[recCnt].get("basic")=="" || recArray[recCnt].get("basic")<0){
            	calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.Basicisinvalid")], 2);
            	return ;
            }
            if(this.payinterval==3){
            	var day = recArray[recCnt].get("effectivedate").getDay();
            	for(var r=0; r<this.datearr.length;r++){
            		if(day==this.datearr[r]){
            			calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.Selectedeffectivedateiswrong")], 2);
                    	return ;
            		}
            	}
            }
            if(recCnt >0){
                recJSON +=",";
            }
            recJSON +="{userid:'"+recArray[recCnt].get("userid")+"',effectiveDate:'"+recArray[recCnt].get("effectivedate").format("Y-m-d")+"',basic:'"+recArray[recCnt].get("basic")+"'}";
        }
        if(empNames != "") {
            calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.Pleasesetthejoiningdateforfollowingemployeebeforeassigningtemplate")+" : "+empNames], 2);
            return;
        }
        var ename = "";
        for(var recCnt=0;recCnt<recArray.length;recCnt++){
            var joinDate = recArray[recCnt].get("joindate");
            if(new Date(joinDate).format("Y-m-d")>recArray[recCnt].get("effectivedate").format("Y-m-d")){
            	ename += "<br/>"+recArray[recCnt].get("fullname");
            }
        }
        if(ename!=""){
        	calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.Pleasesetthejoiningdateforfollowingemployeebeforeassigningtemplate")+" : "+ename], 2);
            return;
        }
        recJSON +="]";
//        var EmpTemplateClash = "Template cannot be assigned on selected Effective Date clashes with already assigned template for following employee :<br/><br/>";
        var EmpTemplateClash = WtfGlobal.getLocaleText("hrms.payroll.OnethetemplatesisalreadyassignedforselectedEffectiveDatePleaseselectanotherdateForthefollowingemployee")+" :<br/><br/>";
        var Clashfound = false;
                            Wtf.Ajax.requestEx({url:"Payroll/Template/getAssignedTemplateForEmponDate.py",
                                method:"GET",
                                params:{
                                    records:recJSON

                                    }
                            }, this, function(response){
                                var dataObj = response.data;
                                var cnt = 1;
                                if(dataObj!=null&&dataObj!=undefined){
                                    for(var i=0;i<dataObj.length;i++){
                                        var found = -1;
                                        for(var j=0;j < recArray.length;j++){
                                            if(recArray[j].get("userid") == dataObj[i].userid && dataObj[i].effectivedate == recArray[j].get("effectivedate").format("Y-m-d")){

                                                found = j;

                                            }
                                        }

                                        if(found != -1){
                                            Clashfound = true;
                                            EmpTemplateClash += "<b>"+cnt+")"+recArray[found].get("fullname")+"</b><br/>";
                                            recArray.splice(found, 1);
                                            cnt++;
                                        }
                                    }
                                }
                                if(Clashfound){
                                    Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.payroll.Errorassigningusers"),EmpTemplateClash);
                                }
                                   this.assignedempstore.add(recArray);
                                   Wtf.getCmp("assignEmplyeeWin").close();

                            }, function(){

                            })

                         
                        },
                        scope:this
                    },{
                        text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                        handler: function(){
                            Wtf.getCmp("assignEmplyeeWin").close();
                        }
                    }]
                }).show();
                }else{
                    calMsgBoxShow(223,0);
                }

            },
            scope:this
        })
        var tbar =  new Wtf.Toolbar([this.assignEmpBtn]);
        return tbar;
    },
    getRecordsJSON : function(){
        var recArray = this.assignedempgrid.store.getRange(0,this.assignedempgrid.store.getCount());
        var recJSON = "[";
        for(var recCnt=0;recCnt<recArray.length;recCnt++){
            if(recCnt >0){
                recJSON +=",";
            }
            recJSON +="{userid:'"+recArray[recCnt].get("userid")+"',effectiveDate:'"+recArray[recCnt].get("effectivedate").format("Y-m-d")+"',basic:'"+recArray[recCnt].get("basic")+"',usertemplateid:'"+recArray[recCnt].get("usertemplateid")+"'}";
        }
        recJSON +="]";
        return recJSON;
    },
    getGrid : function(){
        this.fieldsrec =  Wtf.data.Record.create([{
        	name:'usertemplateid'
        },{
            name:'fullname'
        },{
            name:'userid'
        },{
            name:'basic'
        },{
            name:'salaryflag'
        },{
            name:'effectivedate',
            type:'date',
            dateFormat:'Y-m-d'
            
        },{
            name:'joindate'
        }
        ])

        this.employeedatareader=new Wtf.data.KwlJsonReader({
            root:'data'
            
        },this.fieldsrec);

        this.allEmpStore = new Wtf.data.Store({
            url: "Common/getAllUserDetailsHrms.common",
            method:'GET',
            reader: this.employeedatareader
        });
        
        this.assignedempstore = new Wtf.data.Store({
            url: "Payroll/Template/getAssignedEmpForTemplate.py",
            method:'GET',
            reader: this.employeedatareader

        });


        this.cm = new Wtf.grid.ColumnModel([
        {
            header:WtfGlobal.getLocaleText("hrms.payroll.Employee"),
            dataIndex:'fullname',
            width:150
        },{
            header:WtfGlobal.getLocaleText({key:"hrms.payroll.Basicsym",params:[WtfGlobal.getCurrencySymbol()]}),
            dataIndex:'basic',
            width:150
        },{
            header:WtfGlobal.getLocaleText("hrms.common.EffectiveFrom"),
            dataIndex:'effectivedate',
            renderer:WtfGlobal.onlyDateRenderer,
            width:200
        },
        {
            header:WtfGlobal.getLocaleText("hrms.common.delete"),
            renderer:this.deleteRenderer.createDelegate(this)
        }
        
        ])
        this.assignedempgrid = new Wtf.grid.EditorGridPanel({
            store:this.assignedempstore,
            stripeRows: true,
            tbar:this.getToolbar(),
            id:this.id+'assignedemp',
            scope:this,
            height:440,
            clicksToEdit:1,
            title:WtfGlobal.getLocaleText("hrms.payroll.AssignedEmployees"),
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.NoEmployeesareassignedtothisSalaryTemplatePleaseclickonAssignEmployeetoselectfromthelist"))
            },
            cm:this.cm

        })

        return this.assignedempgrid;
    }
});
