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
Wtf.AddJobs = function(config) {
	this.submitbtn = new Wtf.Button({
        text:  WtfGlobal.getLocaleText("hrms.common.submit"),
        minWidth:70,
        handler: this.sendjobSaveRequest,
        scope:this
    });
	
    Wtf.apply(this,{
        buttonAlign :'right',
        width:430,
        height:490,
        buttons: [this.submitbtn, {
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    }, config);
    Wtf.AddJobs.superclass.constructor.call(this, config);

};

Wtf.extend(Wtf.AddJobs, Wtf.Window, {
    initComponent: function() {
        Wtf.AddJobs.superclass.initComponent.call(this);
    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.AddJobs.superclass.onRender.call(this, config);
        
        this.internaljobtype=  new Wtf.data.SimpleStore({
            fields:['jobtype', 'jobtypename'],
            data: [['Both', WtfGlobal.getLocaleText("hrms.common.Both")],['External', WtfGlobal.getLocaleText("hrms.common.External")],['Internal', WtfGlobal.getLocaleText("hrms.common.Internal")]]
        });

        this.deptrec=new Wtf.data.Record.create([
        {
            name:'id'
        },

        {
            name:'name'
        }
        ]);

        this.posCmb = new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.PositionName")+"*",
            store:Wtf.desigStore,
            mode:'local',
            hiddenName :'name',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.recruitment.Selectaposition"),
            typeAhead:true,
            forceSelection:true,
            allowBlank:false,
            width:200,
            addNewFn:this.addDesignation.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
                },
                scope: this
            })]
        });

        if(!Wtf.StoreMgr.containsKey("desig")){
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }

        this.deptCmb = new Wtf.form.FnComboBox({
            fieldLabel:	  WtfGlobal.getLocaleText("hrms.common.department")+'*',
            store:Wtf.depStore,
            mode:'local',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Selectadepartment"),
            typeAhead:true,
            forceSelection:true,
            allowBlank:false,
            width:200,
            addNewFn:this.addDepartment.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    WtfGlobal.showmasterWindow(7,Wtf.depStore,"Add");
                },
                scope: this
            })]
        });


        if(!Wtf.StoreMgr.containsKey("dep")){
            Wtf.depStore.load();
            Wtf.StoreMgr.add("dep",Wtf.depStore)
        }

        this.managerCmb = new Wtf.form.ComboBox({
            store:Wtf.managerStore,
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.approving.manager")+'*',
            mode:'local',
            valueField: 'userid',
            displayField:'username',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Selectamanager"),
            typeAhead:true,
            forceSelection:true,
            allowBlank:false,
            width:200
        });

        if(!Wtf.StoreMgr.containsKey("manager")){
            Wtf.managerStore.on('load',function(){
                this.loadRecord();
            },this);
            Wtf.managerStore.load();
            Wtf.StoreMgr.add("manager",Wtf.managerStore)
        }else{
            this.loadRecord();
        }

        this.startd = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.start.date")+'*',
            width : 200,
            name:'startdate',
            value:new Date(),
            minValue:new Date().clearTime(true),
            allowBlank:false,
            format:'m/d/Y'
        });

        this.endd = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.end.date")+'*',
            width : 200,
            value:new Date(),
            minValue:new Date().clearTime(true),
            name:'enddate',
            allowBlank:false,
            format:'m/d/Y'
        });
        this.startd.on('blur', function(field){
            if(field.getValue()>this.endd.getValue())
                this.endd.markInvalid(WtfGlobal.getLocaleText({key:"hrms.recruitment.Thedateinthisfieldbeequaltooraftermessage",params:[this.startd.getValue().format('m/d/Y')]}));
        }, this);
        this.endd.on('blur', function(field){
            if(field.getValue()<this.startd.getValue())
                field.markInvalid(WtfGlobal.getLocaleText({key:"hrms.recruitment.Thedateinthisfieldbeequaltooraftermessage",params:[this.startd.getValue().format('m/d/Y')]}));
        }, this);

        this.posdetail = new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.PositionDetails"),
            name:'details',
            width:200,
            maxLength:512
        });        
        this.posidformat= new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.JobIDFormat"),
            name:'jobidformat',
            regex:/^[a-zA-Z0-9]{0,}$/,
            width:200,
            maxLength:255
        });
        this.posid= new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.job.id"),
            name:'jobid',
            width:200,
            disabled:true,
            maxLength:255
        });
        this.jobtype = new Wtf.form.ComboBox({ 
            store:this.internaljobtype,
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.job.type")+'*',
            mode:'local',
            hiddenName :'jobtype',
            valueField: 'jobtype',
            displayField:'jobtypename',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.recruitment.Selectthejobtype"),
            typeAhead:true,
            forceSelection:true,
            allowBlank:false,
            width:200
        });

        this.nopos= new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.no.of.vacancies")+'*',
            name:'nopos',
            allowDecimals:false,
            width:200,
            allowBlank:false,
            minValue:1,
            //            value:1,
            maxLength:5
        });

        this.IneternalJobForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 0px 10px 10px;margin-top:3%',
            autoScroll:false,
            labelWidth :119,
            layoutConfig: {
                deferredRender: false
            },
            items:[this.posidformat, this.posid,
            this.posCmb,this.nopos,this.posdetail, this.deptCmb, this.managerCmb,
            this.startd,this.endd,this.jobtype
            ]
        })

        this.addjobpanel= new Wtf.Panel({            
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 70,
                    border : false,
                    cls : 'panelstyleClass1',
                    html: getTopHtml(WtfGlobal.getLocaleText("hrms.recruitment.JobPosition"), WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"),"../../images/addjobpos.jpg")
                },{
                    border:false,
                    region:'center',
                    cls : 'panelstyleClass2',
                    layout:"fit",
                    items: [
                    this.IneternalJobForm
                    ]
                }]
            }]
        });
        
        this.add(this.addjobpanel);
        if(this.editflag=='true')
        {
            this.posCmb.disable();
            this.posCmb.setValue(this.posname);
            this.posdetail.setValue(this.posdetails);
            this.deptCmb.setValue(this.department);
            this.managerCmb.setValue(this.apprmanager);
            this.startd.setValue(this.startdate);
            this.endd.setValue(this.enddate);
            this.jobtype.setValue(this.jobstatus);
        }
    },
    sendjobSaveRequest:function(){
        if(!this.IneternalJobForm.getForm().isValid())
        {
            return;
        }
        else
        {
            if(this.startd.getValue().format('Ymd')>this.endd.getValue().format('Ymd')||this.endd.getValue().format('Ymd')<new Date().format('Ymd')){
                this.endd.markInvalid(WtfGlobal.getLocaleText({key:"hrms.recruitment.Thedateinthisfieldbeequaltooraftermessage",params:[this.startd.getValue().format('m/d/Y')]}));
                return;
            }
            else{
            	this.submitbtn.setDisabled(true);
                calMsgBoxShow(200,4,true);
                Wtf.Ajax.requestEx({
//                    url: Wtf.req.base + 'hrms.jsp?flag=8',
                    url: 'Rec/Job/InternalJobpositions.rec?flag=8',
                    params:{
                        startdate:this.startd.getValue().format("m/d/Y"),
                        enddate:this.endd.getValue().format("m/d/Y"),
                        position:this.posCmb.getValue(),
                        details:this.posdetail.getValue().trim(),
                        department:this.deptCmb.getValue(),
                        manager:this.managerCmb.getValue(),
                        jobtype:this.jobtype.getValue(),
                        posid:this.positionid,
                        jobid:this.posid.getValue(),
                        nopos:this.nopos.getValue(),
                        jobidformat:this.posidformat.getValue()
                    }
                },this,
                function(response){
                    var res=eval('('+response+')');                    
                    this.close();
                    this.ds.load({
                        params:{
                            start:this.grids.pag.cursor,
                            limit:this.grids.pag.pageSize,
                            ss: Wtf.getCmp("Quick"+this.grids.id).getValue()
                        }
                    });
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),""+res.message+""],0);
                },

                function(){
                    calMsgBoxShow(52,1);
                })
            }
        }
    },  
    afterEditFunction1:function(e){
        if(this.startd.getValue().format('Ymd')<new Date().format('Ymd')){
            calMsgBoxShow(108,0);
            this.startd.setValue('');
        }
    },

    loadRecord:function(){
        Wtf.Ajax.requestEx({
            //url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/getJobidFormat.rec",
            params: {
                flag:209
            }
        },
        this,
        function(req,res){
            var resp=eval('('+req+')');
            var values=resp.data[0].maxempid;
            this.posidformat.setValue(resp.data[0].jobidformat);
            this.posid.setValue(values);
        },
        function(req,res){
            });
    },
    addDesignation:function(){
        WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
    },
    addDepartment:function(){
        WtfGlobal.showmasterWindow(7,Wtf.depStore,"Add");
    }
});
