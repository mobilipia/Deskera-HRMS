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
Wtf.createapplicantForm=function(config){
    Wtf.apply(this, config);
    Wtf.createapplicantForm.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.createapplicantForm,Wtf.Panel,
{
    initComponent:function(config)
    {
        Wtf.createapplicantForm.superclass.initComponent.call(this,config);


        this.chkbox=new Wtf.form.Checkbox({
            boxLabel:WtfGlobal.getLocaleText("hrms.recruitment.Ihavereadagreetothelegaldisclaimer"),
            style:"margin-left:40%",
            listeners:{
                check:this.show_submit,
                scope:this
            }
        });

        this.submit=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.Submitresume"),
            disabled:true,
            scope:this,
            handler:this.submitFormValue
        });

        this.pan7=new Wtf.Panel({
            frame:true,
            bodyStyle : 'background:white',
            buttonAlign:"center" ,
            autoHeight:true,
            items:[{
                html:"<center> <font color='red'>"+WtfGlobal.getLocaleText("hrms.recruitment.Disclaimer")+"</font></center><hr/></br>"
            },
            {
                html:"&nbsp "+WtfGlobal.getLocaleText("hrms.recruitment.Disclaimer.Undertaking")
            },
            this.chkbox
            ],
            buttons:[this.submit]
        });
        this.getallpanels();
    },
    getallpanels: function(){
          this.PersonalattrPanel = new Wtf.Panel({
            title:WtfGlobal.getLocaleText("hrms.recruitment.PersonalInformation"),
            widthValue: 215,
            layout:'form',
            frame:true,
            border :false,
            refid:this.profId,
            formtype : "Personal",
            id:"Personal" + this.profId,
            fetchmaster:true,
            chk:1,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px;'
        });

        this.ContactattrPanel = new Wtf.Panel({
            title:WtfGlobal.getLocaleText("hrms.recruitment.ContactInformation"),
            widthValue: 215,
            layout:'form',
            frame:true,
            border :false,
            refid:this.profId,
            formtype : "Contact",
            id:"Contact" + this.profId,
            fetchmaster:true,
            chk:1,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px;'
        });

        this.AcademicattrPanel = new Wtf.Panel({
            title:WtfGlobal.getLocaleText("hrms.recruitment.AcademicInformation"),
            widthValue: 215,
            layout:'form',
            frame:true,
            border :false,
            refid:this.profId,
            formtype : "Academic",
            fetchmaster:true,
            id:"Academic" + this.profId,
            chk:1,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px;'
        });

        this.WorkattrPanel = new Wtf.Panel({
            title:WtfGlobal.getLocaleText("hrms.recruitment.WorkExperienceDetailsFullTime"),
            widthValue: 215,
            layout:'form',
            frame:true,
            border :false,
            refid:this.profId,
            formtype : "Work",
            id:"Work" + this.profId,
            fetchmaster:true,
            chk:1,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px;'
        });

        this.OtherattrPanel = new Wtf.Panel({
            title:WtfGlobal.getLocaleText("hrms.recruitment.OtherInformation"),
            widthValue: 215,
            layout:'form',
            frame:true,
            border :false,
            id:"other" + this.profId,
            refid:this.profId,
            formtype : "other",
            fetchmaster:true,
            chk:1,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px;'
        });
        this.recinfo=new Wtf.FormPanel({
            autoHeight:true,
            url: "Rec/Job/saveConfigRecruitment.rec",
            frame:true,
            fileUpload:true,
            border :false,
            labelWidth:150,
            items:[this.PersonalattrPanel,this.ContactattrPanel,this.AcademicattrPanel,this.WorkattrPanel,this.OtherattrPanel]
        });
    },
    onRender:function(config)
    {
        this.add(this.recinfo);
        this.add(this.pan7);
        this.renderconfigfields();
        
        Wtf.createapplicantForm.superclass.onRender.call(this,config);
    },
    show_submit:function(){
        if(this.chkbox.checked==true)
            this.submit.enable();
        else
            this.submit.disable();
    },
   
    submitFormValue:function(){
        var validform = this.ValidateForm();
        
        if(validform){
            var recinfo = this.recinfo.getForm().getValues();
            recinfo.update=true;
            recinfo.profileid=this.profId;
            calMsgBoxShow(200,4,true);
                this.recinfo.getForm().submit({
                    scope:this,
                    params:{
                        Id:this.profId
                    },
                    success:function(){
                        if(msgFlag==1){
                            WtfGlobal.closeProgressbar();
                        }
                        calMsgBoxShow(206,0);
                        Wtf.getCmp("recruitmentmanage").remove(this);
                    },
                    failure:function(){
                        calMsgBoxShow(27,1);
                    }
                });
        }
        else{
            calMsgBoxShow(5,0);
        }
    },
    ValidateForm:function(){
        for(var i = 0; i < this.fieldsdata.length; i++) {
                if(!Wtf.getCmp(this.id+'text'+i).isValid())
                   return false;
        }
        return true;
    },    
    renderconfigfields : function(){
        this.fetchmaster = true;
        this.count = 0;
        this.widthValue =215;

        Wtf.Ajax.requestEx({
            url: "Rec/Job/getConfigRecruitment.rec",
            method: 'POST',
            params: {
                visible:"True",
                fetchmaster:this.fetchmaster,
                refid : this.profId,
                formtype : "All"
            }
            },this,
        function(response) {
            var responseObj = eval('('+response+')');
            if(responseObj.data !='' && responseObj.data !=null){
                this.count = responseObj.data.length;
                this.fieldsdata = responseObj.data;
                for(var i = 0; i < responseObj.data.length; i++) {
                    if(responseObj.data[i].configtype==0){
                            this.text =new Wtf.form.TextField({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                                name: "Col" + responseObj.data[i].colnum,
                                value :responseObj.data[i].configdata[0],
                                width: this.widthValue,
                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
                            });
                            Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(this.text);
                    }else if(responseObj.data[i].configtype==7){
                            this.text =new Wtf.form.TextField({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                                name: "Col" + responseObj.data[i].colnum,
                                value :responseObj.data[i].configdata[0],
                                width: this.widthValue,
                                allowBlank:(responseObj.data[i].allowblank==0)?false:true,
                                vtype :'email'
                            });
                            Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(this.text);
                    }else if(responseObj.data[i].configtype==6){
                            Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(new Wtf.form.NumberField({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                                name: "Col" + responseObj.data[i].colnum,
                                value :(!isNaN(responseObj.data[i].configdata[0]) && responseObj.data[i].configdata[0]!=undefined)?responseObj.data[i].configdata[0]:0,
                                maxLength:10,
                                scope: this,
                                width: this.widthValue,
                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
                            }));
                    }else if(responseObj.data[i].configtype==22){
                            Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(new Wtf.form.Checkbox({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+" *":responseObj.data[i].fieldname,
                                name: "Col" + responseObj.data[i].colnum,
                                checked :(this.profId.length==0)?false:responseObj.data[i].configdata[0],
                                scope: this
                            }));
                    }else if(responseObj.data[i].configtype==2){
                        Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(new Wtf.form.DateField({
                            id:this.id+'text'+i,
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            name: "Col" + responseObj.data[i].colnum,
                            value:responseObj.data[i].configdata[0],
                            width: this.widthValue,
                            format:'Y-m-j',
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        }));
                    }else if(responseObj.data[i].configtype==3){
                        var storedata =[];
                        if(responseObj.data[i].data!=null){
                            for(var ctr =0;ctr<responseObj.data[i].data.length;ctr++){
                                var storerecord = [];
                                storerecord.push(responseObj.data[i].data[ctr].masterid);
                                storerecord.push(responseObj.data[i].data[ctr].masterdata);
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
                            name: "Col" + responseObj.data[i].colnum,
                            hiddenName :"Col" + responseObj.data[i].colnum,
                            editable :false,
                            value :responseObj.data[i].configdata[0],
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        });
                        Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(this.ruleTypeCombo);
                    }else if(responseObj.data[i].configtype==55){

                            Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(new Wtf.MenuHTMLEditor({
                                id:this.id+'text'+i,
                                fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                                width: this.widthValue,
                                height: 100,
                                value :responseObj.data[i].configdata[0],
                                name:responseObj.data[i].fieldname,
                                allowBlank:(responseObj.data[i].allowblank==0)?false:true
                            }));
                    }else if(responseObj.data[i].configtype==4){
                        Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(new Wtf.form.TextArea({
                            id:this.id+'text'+i,
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            name: "Col" + responseObj.data[i].colnum,
                            value :responseObj.data[i].configdata[0],
                            scope: this,
                            width: this.widthValue,
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        }));
                    }else if(responseObj.data[i].configtype==77){
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


                        Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(new Wtf.common.Select(Wtf.applyIf({
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
                            name: "Col" + responseObj.data[i].colnum,
                            value :responseObj.data[i].configdata,
                            scope: this,
                            allowBlank:(responseObj.data[i].allowblank==0)?false:true
                        })));
                    }else if(responseObj.data[i].configtype==5){
                        Wtf.getCmp(responseObj.data[i].formtype + this.profId).add(new Wtf.common.helpTextField({
                            id:this.id+'text'+i,
                            fieldLabel: (responseObj.data[i].allowblank==0)?responseObj.data[i].fieldname+"*":responseObj.data[i].fieldname,
                            name: "Col" + responseObj.data[i].colnum,
                            fileName :(responseObj.data[i].configdata[0]==undefined)?"":"<span><a href='javascript:void(0)' title="+ WtfGlobal.getLocaleText("hrms.common.Download")+" onclick='setDldUrl(\"Common/Document/downloadDocuments.common?url=" + responseObj.data[i].configdata[0] + "&mailattch=true&dtype=attachment&applicant=applicant\")'><span class='pwndHRMS resumeIcon' style='cursor:pointer;padding-left:20px;margin-top:-20px;' title="+ WtfGlobal.getLocaleText("hrms.common.Clicktodownloaddocument")+" ></span></a></span>",
                            scope: this,
                            width: this.widthValue,
                            autoCreate : {
                                tag: "input",
                                type: "text",
                                size: "13",
                                autocomplete: "off"
                            },
                            msgTarget:"under",
                            allowBlank:(responseObj.data[i].configdata[0]==undefined && responseObj.data[i].allowblank==0)?false:true,
                            inputType : 'file'
                        }));
                    }

                }
                 this.recinfo.doLayout();
            }
        }, function() {
        }
        );
    }
}); 
