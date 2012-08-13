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
Wtf.attributeComponent = function(config){
    Wtf.apply(this, config);
    Wtf.attributeComponent.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.attributeComponent, Wtf.Panel, {
    initComponent: function() {
        Wtf.attributeComponent.superclass.initComponent.call(this);

        this.addEvents({
            "closeform": true
        });
    },

    onRender: function(config){
        Wtf.attributeComponent.superclass.onRender.call(this, config);
        this.count = 0;
        var paramobj;
        if(this.grouper!=null&&this.grouper!=undefined){
            paramobj={
                configFlag : "true",
                configType : this.configType,
                flag:218,
                grouper:this.grouper,
                refid : this.refid
            }
        }else{
            paramobj={
                configFlag : "true",
                configType : this.configType,
                flag:218,
                refid : this.refid
            }
        }
        Wtf.Ajax.requestEx({
            //url: Wtf.req.base + "hrms.jsp",
            url: "CustomCol/getConfigData.do",
            method: 'POST',
            params: paramobj
            },this,
        function(response) {
            var responseObj = eval('('+response+')');
            if(responseObj.data !='' && responseObj.data !=null){
                this.count = responseObj.data.length;
                for(var i = 0; i < responseObj.data.length; i++) {
                    if(responseObj.data[i].configtype==0){
//                        if(responseObj.data[i].fieldname=='Target Audience' || responseObj.data[i].configid=='externelcourseurl'){
//                            this.text = new Wtf.form.TextField({
//                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
//                                id:this.id+'text'+i,
//                                name: responseObj.data[i].fieldname,
//                                value :responseObj.data[i].configdata[0],
//                                scope: this,
//                                width: this.widthValue,
//                                // msgTarget:"under",
//                                disabled:(responseObj.data[i].disable==0)?false:true,
//                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
//                            });
//                            Wtf.getCmp(this.id).add(this.text);
//                        }
//                        else{
                            this.text =new Wtf.form.TextField({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                                name: responseObj.data[i].fieldname,
                                value :responseObj.data[i].configdata[0],
                                scope: this,
                                width: this.widthValue,
                                maxLength:250,
                                // msgTarget:"under",
                                //                                  disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):true,
                                allowBlank:(responseObj.data[i].allowblank==0)?false:true,
                                disabled:userroleid==1?false:(responseObj.data[i].blockemployees==1?true:false)
                            });
                            Wtf.getCmp(this.id).add(this.text);
//                        }
                    }else if(responseObj.data[i].configtype==1){
//                        if(responseObj.data[i].configid=='coursesuppts'){
//                            Wtf.getCmp(this.id).add(new Wtf.form.NumberField({
//                                id:this.id+'text'+i,
//                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
//                                name: responseObj.data[i].fieldname,
//                                value :(!isNaN(responseObj.data[i].configdata[0]))?responseObj.data[i].configdata:0,
//                                maxLength:50,
//                                scope: this,
//                                width: this.widthValue,
//                                // msgTarget:"under",
//                                disabled:(responseObj.data[i].disable==0)?false:true,
//                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
//                            }));
//                        }else{
                            Wtf.getCmp(this.id).add(new Wtf.form.NumberField({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                                name: responseObj.data[i].fieldname,
                                value :(!isNaN(responseObj.data[i].configdata[0]))?responseObj.data[i].configdata:0,
                                maxLength:50,
                                scope: this,
                                width: this.widthValue,
                                // msgTarget:"under",
                                //                                      disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):true,
                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
                            }));
//                        }
                    }else if(responseObj.data[i].configtype==2){
//                        if(responseObj.data[i].configid=='courseassapp' || responseObj.data[i].configid=='coursescapp' || responseObj.data[i].configid=='courseallowsub' || responseObj.data[i].configid=='courseisrelate' || responseObj.data[i].configid=='courseallowcancel' || responseObj.data[i].configid=='courseallowexempt' || responseObj.data[i].configid=='courseasssch'){
//                            Wtf.getCmp(this.id).add(new Wtf.form.Checkbox({
//                                id:this.id+'text'+i,
//                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+" *":responseObj.data[i].fieldname,
//                                name: responseObj.data[i].fieldname,
//                                checked :(this.refid.length==0 &&(responseObj.data[i].configid=='courseassapp' || responseObj.data[i].configid=='coursescapp' || responseObj.data[i].configid=='courseallowsub' || responseObj.data[i].configid=='courseisrelate'))?true:responseObj.data[i].configdata[0],
//                                scope: this,
//                                // msgTarget:"under",
//                                disabled:(responseObj.data[i].disable==0)?false:true
//                            }));
//                        }else{
                            Wtf.getCmp(this.id).add(new Wtf.form.Checkbox({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+" *":responseObj.data[i].fieldname,
                                name: responseObj.data[i].fieldname,
                                checked :(this.refid.length==0)?false:responseObj.data[i].configdata[0],
                                scope: this
                            //  msgTarget:"under",
                            //                                      disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):true
                            }));
//                        }
                    }else if(responseObj.data[i].configtype==3){
                        Wtf.getCmp(this.id).add(new Wtf.form.DateField({
                            id:this.id+'text'+i,
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            name: responseObj.data[i].fieldname,
                            value:responseObj.data[i].configdata[0],
                            width: this.widthValue,
                            format:'Y-m-j',
                            //  msgTarget:"under",
                            //                                  disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):true,
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        }));
                    }else if(responseObj.data[i].configtype==4){
                        var storedata =[];
                        if(responseObj.data[i].masterdata!=null){
                            for(var ctr =0;ctr<responseObj.data[i].masterdata.length;ctr++){
                                var storerecord = [];
                                storerecord.push(responseObj.data[i].masterdata[ctr].id);
                                storerecord.push(responseObj.data[i].masterdata[ctr].data);
                                storedata.push(storerecord);
                            }
                        }
                        this.ruleTypeStore=new Wtf.data.SimpleStore({
                            fields :['id', 'name'],
                            data:storedata
                        });

                        this.ruleTypeCombo = new Wtf.form.ComboBox({
                            id:this.id+'text'+i,
                            triggerAction: 'all',
                            store:this.ruleTypeStore,
                            mode:'local',
                            width: this.widthValue,
                            listWidth:this.widthValue,
                            displayField:'name',
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            valueField:'id',
                            name:responseObj.data[i].fieldname,
                            hiddenName :responseObj.data[i].fieldname,
                            editable :false,
                            value :responseObj.data[i].configdata[0],
                            // msgTarget:"under",
                            //                                disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):(responseObj.data[i].configid == 'coursetype')? false : true,
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        });
                        Wtf.getCmp(this.id).add(this.ruleTypeCombo);
                    }else if(responseObj.data[i].configtype==5){
//                        if(responseObj.data[i].fieldname=='Outline'){
//                            Wtf.getCmp(this.id).add(new Wtf.MenuHTMLEditor({
//                                id:this.id+'text'+i,
//                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
//                                width: this.widthValue,
//                                height: 100,
//                                value :responseObj.data[i].configdata[0],
//                                name:responseObj.data[i].fieldname,
//                                //   msgTarget:"under",
//                                disabled:(responseObj.data[i].disable==0)?false:true,
//                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
//                            }));
//                        }
//                        else{
                            Wtf.getCmp(this.id).add(new Wtf.MenuHTMLEditor({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                                width: this.widthValue,
                                height: 100,
                                value :responseObj.data[i].configdata[0],
                                name:responseObj.data[i].fieldname,
                                // msgTarget:"under",
                                //                                  disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):true,
                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
                            }));
//                        }
                    }else if(responseObj.data[i].configtype==6){
                        Wtf.getCmp(this.id).add(new Wtf.form.TextArea({
                            id:this.id+'text'+i,
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            name: responseObj.data[i].fieldname,
                            value :responseObj.data[i].configdata[0],
                            scope: this,
                            width: this.widthValue,
                            //   msgTarget:"under",
                            //                                  disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):true,
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        }));
                    }else if(responseObj.data[i].configtype==7){
                        var storedata1 =[];
                        if(responseObj.data[i].masterdata!=null){
                            for(var ctr1 =0;ctr1<responseObj.data[i].masterdata.length;ctr1++){
                                var storerecord1 = [];
                                storerecord1.push(responseObj.data[i].masterdata[ctr1].id);
                                storerecord1.push(responseObj.data[i].masterdata[ctr1].data);
                                storedata1.push(storerecord1);
                            }
                        }
                        this.ruleTypeStore1=new Wtf.data.SimpleStore({
                            fields :['id', 'name'],
                            data:storedata1
                        });


                        Wtf.getCmp(this.id).add(new Wtf.common.Select(Wtf.applyIf({
                            multiSelect:true,
                            forceSelection:true
                        },{
                            id:this.id+'text'+i,
                            triggerAction: 'all',
                            store:this.ruleTypeStore1,
                            mode:'local',
                            width: this.widthValue,
                            listWidth:this.widthValue,
                            displayField:'name',
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            valueField:'id',
                            hiddenName :responseObj.data[i].fieldname,
                            name: responseObj.data[i].fieldname,
                            value :responseObj.data[i].configdata,
                            //maxLength:50,
                            scope: this,
                            //  msgTarget:"under",
                            //                                 disabled:(this.chk==1)?((responseObj.data[i].disable==0)?false:true):true,
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        })));
                    }else if(responseObj.data[i].configtype==8){
                        Wtf.getCmp(this.id).add(new Wtf.form.TextField({
                            id:this.id+'text'+i,
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            name: responseObj.data[i].fieldname,
                            //value :responseObj.data[i].configdata[0],
                            //                                  maxLength:50,
                            scope: this,
                            width: this.widthValue,
                            autoCreate : {
                                tag: "input",
                                type: "text",
                                size: "35",
                                autocomplete: "off"
                            },
                            msgTarget:"under",
                            disabled:(responseObj.data[i].disable==0)?false:true,
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true,
                            inputType : 'file'
                        }));
                    }

                }
            }
            this.fireEvent("closeform", this.id);
        }, function() {
            this.fireEvent("closeform", this.id);
        }
        );
    },
    isValidate: function() {
        var flag = 0;
        for(var i = 0; i < this.count; i++) {
            if(Wtf.getCmp(this.id+'text'+i) && Wtf.getCmp(this.id+'text'+i).validate() == false) {
                flag = 1;
                break;
            }
        }
        if(flag == 1) {
            return false;
        } else {
            return true;
        }
    }
});
