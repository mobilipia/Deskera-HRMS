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
Wtf.editprospect = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        height:375,
        width:410,
        buttons: [
        {
            anchor : '90%',
            text:  WtfGlobal.getLocaleText("hrms.common.Save"),
            handler: this.sendSaveRequest,
            scope:this,
            disabled:true
        },
        {
            anchor : '90%',
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }
        ]
    }, config);
    Wtf.editprospect.superclass.constructor.call(this, config);

};

Wtf.extend(Wtf.editprospect, Wtf.Window, {
   
    initComponent: function() {
        Wtf.editprospect.superclass.initComponent.call(this);
    },
    loadAllStores:function(){

    },
    onRender: function(config) {
        Wtf.editprospect.superclass.onRender.call(this, config);
        this.addEvents({
        'editpload':true
    });
        this.joiningdate = new Wtf.form.DateField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.DateofJoining"),
            name:'joiningdate',
            width:200,
            disabled:true,
            allowBlank:true,
            format:'m/d/Y'
        });

        
        
        this.statusComboData();
        
        this.statuscombo = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.AssignStatus")+'*',
            store:  this.combostore,
            allowBlank:false,
            typeAhead:true,
            name: 'status',
            displayField: 'name',
            valueField:'id',
            value:this.editval,
            mode: 'local',
            width:200,
            triggerAction: 'all',
            listeners:{
                scope:this,
                select:function()
                {
                    this.statusChange(this.statuscombo.getValue());
                }
            }
        });

        if(!Wtf.StoreMgr.containsKey("status")){
            Wtf.statusStore.load();
            Wtf.StoreMgr.add("status",Wtf.statusStore)
        }

        if(this.editval=='Selected')
        {
            this.joiningdate.value=new Date();
        }

        this.callbackcombo = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.RequestCallback"),
            width:200,
            store:  Wtf.callbackStore,
            typeAhead:true,
            hiddenName: 'callback',
            valueField:'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all'
        });

        this.setCallback();

        this.rankcombo = new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.rank"),
            width:200,
            store:Wtf.rankStore,
            typeAhead:true,
            hiddenName: 'rank',
            valueField:'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            addNewFn:this.addRank.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    WtfGlobal.showmasterWindow(10,Wtf.rankStore,"Add");
                },
                scope: this
            })]
        });

        if(!Wtf.StoreMgr.containsKey("rank")){
            Wtf.rankStore.on("load",function(){
                this.setRank();
                this.buttons[0].enable();
            },this);
            Wtf.rankStore.load();
            Wtf.StoreMgr.add("rank",Wtf.rankStore)
        }
        else{
            this.setRank();
            this.buttons[0].enable();
        }

        this.comment = new Wtf.form.TextArea({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.Comment"),
            name:'statuscomment',
            width:200,
            maxLength:255,
            height:50
        });
        this.check=new Wtf.form.Checkbox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.SendMail"),
            name:'mail',
            checked:false,
            disabled:true
        });

        this.headingType= WtfGlobal.getLocaleText("hrms.common.edit.prospect");
        this.newExpense= new Wtf.Panel({
            frame:true,
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
                    html:this.isview?getTopHtml(this.headingType,""):getTopHtml(this.headingType, WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"))
                },{
                    border:false,
                    region:'center',
                    cls : 'panelstyleClass2',
                    layout:"fit",
                    items: [
                    this.editprospectform = new Wtf.form.FormPanel({                        
                        waitMsgTarget: true,        
                        border : false,
                        bodyStyle : 'font-size:10px;padding:10px 10px;margin-top:5%;',
                        autoScroll:false,
                        labelWidth:120,
                        layoutConfig: {
                            deferredRender: false
                        },
                        items:[this.statuscombo,this.rankcombo,this.callbackcombo,this.joiningdate,this.comment]
                    })]
                }]
            }]
        });
        this.add( this.newExpense);
                
        this.statuscombo.on('select',function(a,b,c){
            if(c==2 || c==1){
                this.check.enable();
                this.check.setValue(true);
            }else{
                this.check.setValue(false);
                this.check.disable();
            }
        },this);
    },  
    
    statusComboData : function(){
        var mainArray=new Array();

        for (var i=0;i<Wtf.statusStore.getTotalCount();i++) {
            var tmpArray=new Array();
            
            tmpArray.push(Wtf.statusStore.data.items[i].data.name);
            
            var displayName = WtfGlobal.replaceAll(Wtf.statusStore.data.items[i].data.name," ","");
            tmpArray.push(WtfGlobal.getLocaleText("hrms.recruitment.status."+displayName));
            
            mainArray.push(tmpArray)
            
        }

        var myData = mainArray;

        this.combostore = new Wtf.data.SimpleStore({
            fields: [
            {
                name: 'id'
            },

            {
                name: 'name'
            }
            ]
        });
        this.combostore.loadData(myData);  
    },
    
    sendSaveRequest:function(){
        var appgrid="";
        if(this.rejected){
            appgrid=Wtf.getCmp(this.appid+'rejectedgr');
            this.arr=appgrid.getSelectionModel().getSelections();
            this.gridst=appgrid.getStore();
            this.selectedflag=true;
        }
        else if(this.selected){
            appgrid=Wtf.getCmp(this.appid+'qualifiedgr');
            this.arr=appgrid.getSelectionModel().getSelections();
            this.gridst=appgrid.getStore();
            this.selectedflag=false;
        }
        else{
            appgrid=Wtf.getCmp(this.appid+'allappsviewgr');
            this.arr=appgrid.getSelectionModel().getSelections();
            this.gridst=appgrid.getStore();
            this.selectedflag=true;
        }
        this.ids=[]
        appgrid.getSelectionModel().clearSelections();
        for(i=0;i<this.arr.length;i++)
        {
            var rec=this.gridst.indexOf(this.arr[i]);
            WtfGlobal.highLightRow(appgrid,"33CC33",5, rec);
            this.ids.push(this.arr[i].get('id'));
        }

        if(! this.editprospectform.form.isValid())
        {
            return ;
        }else{
            this.flag=true;
            this.ids=[];
            this.cnames=[];
            this.positionids=[];
            this.applicantids=[];
            if(this.statuscombo.getValue()=="Selected"){
                this.ids1={};
                for(var i=0;i<this.arr.length;i++)
                {
                    var data=this.arr[i].data;
                    var idx=data.JobId;
                    if(!this.ids1[idx])
                        this.ids1[idx]=1;
                    else
                        this.ids1[idx]+=1;
                }
                for(i=0;i<this.arr.length;i++)
                {
                    data=this.arr[i].data;
                    idx=data.JobId;
                    if((parseInt(data.vacancy)-parseInt(data.filled))<this.ids1[idx]){
                        this.flag=false;
                        break;
                    }
                }
            }
            if(this.flag){
                for(i=0;i<this.arr.length;i++)
                {
                    this.ids.push(this.arr[i].get('id'));
                    this.cnames.push(this.arr[i].get('cname'));
                    this.positionids.push(this.arr[i].get('posid'));
                    this.applicantids.push(this.arr[i].get('apcntid'));
                }
                var  param={
                    flag:44,
                    selected:this.statuscombo.getValue(),
                    ids:this.ids,
                    cnames:this.cnames,
                    positionids:this.positionids,
                    applicantids:this.applicantids,
                    employeetype:this.employeetype,
                    changeselected:this.selectedflag
                };
                var status=this.statuscombo.getValue();
                calMsgBoxShow(200,4,true);
                this.editprospectform.form.submit({
//                    url: Wtf.req.base + 'hrms.jsp',
                    url: "Rec/Job/editProspect.rec",
                    params:param,
                    scope: this,
                    success:  function(req, resp){
                        var params={
                            start:0,
                            limit:appgrid.pag.pageSize
                        }                     
                            this.gridst.load({
                                params:params
                            });                                                   
                        this.fireEvent('editpload',status);                       
                        this.close();
                    	if(resp.result.msg==""){
                    		calMsgBoxShow(56,0);
                    	}else{
                    		calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText({key:"hrms.recruitment.Joiningdatemustbegreaterthanapplieddateforaction",params:[resp.result.msg]})],2);
                        }                       
                    }, 
                    failure:function(){
                    	this.close();
                        calMsgBoxShow(57,1);
                    }
                })
            }else{
                calMsgBoxShow(150,0);
                this.close();
                appgrid.getSelectionModel().clearSelections();
            }
        }
    }, 
    statusChange:function(status){
        if(status=="Selected"){
            this.joiningdate.setValue(new Date());
            this.joiningdate.enable();
            this.callbackcombo.disable();
        }
        else{
            this.joiningdate.setValue('');
            this.joiningdate.disable();
            this.callbackcombo.enable();
        }
    },
    setRank:function(){
        if(Wtf.rankStore.getCount()>0){
            this.rankcombo.setValue(Wtf.rankStore.getAt(Wtf.rankStore.getCount()-1).get('id'));
        }        
    },
    setCallback:function(){
        if(Wtf.callbackStore.getCount()>0){
            this.callbackcombo.setValue(Wtf.callbackStore.getAt(0).get('id'));
        }
    },
    addRank:function(){
        WtfGlobal.showmasterWindow(10,Wtf.rankStore, "Add");
    }
});
