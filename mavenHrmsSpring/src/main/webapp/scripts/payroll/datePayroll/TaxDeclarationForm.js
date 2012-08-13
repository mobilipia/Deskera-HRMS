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

/*
 * Window Add new items in Advance request
 *
 */

Wtf.MyTaxDeclarationForm = function(config) {
    Wtf.apply(this, config);
    Wtf.MyTaxDeclarationForm.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.MyTaxDeclarationForm, Wtf.Window, {
    onRender: function(config) {
        Wtf.MyTaxDeclarationForm.superclass.onRender.call(this, config);
        
        var formArr = this.getDeclarationForm();
      
        this.MytaxDecPanel= new Wtf.Panel({
            border: false,
            layout:'fit',
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 70,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html:getTopHtml(this.title, WtfGlobal.getLocaleText("hrms.common.fill.following.fields"))
                },{
                    border:false,
                    region:'center',
                    cls:'windowstyle',
                    layout:"fit",
                    bodyStyle:"background-color:#f1f1f1;padding:15px",
                    items: [
                    this.MytaxDecForm = new Wtf.form.FormPanel({
                        url:'Payroll/Date/saveUserIncomeTaxDeclaration.py',
                        waitMsgTarget: true,
                        method : 'POST',
                        border : false,
                        autoScroll:true,
                        bodyStyle : 'font-size:10px;padding:10px 20px;',
                        lableWidth :50,
                        layoutConfig: {
                            deferredRender: false
                        },
                        defaults:{
                            width: 200,
                            msgTarget: 'side'
                        },
                        defaultType:'textfield',
                        items:formArr
                    })]
                }]
            }],
            buttonAlign :'right',
            buttons: [
            {
                text: (this.action == "Edit")?WtfGlobal.getLocaleText("hrms.activityList.edit"):WtfGlobal.getLocaleText("hrms.common.submit"),
                id:'Item-submit-btn',
                handler: this.saveCostCenterRequest,
                scope:this
            },
            {
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                handler: function(){
                         this.close();
                },
                scope:this
            }
            ]
        });
        
        this.add(this.MytaxDecPanel);
        
    },
    
    getDeclarationForm: function(){
        
        var arr = [];
        
        var savings = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.savings")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.savings.tooltip")),
            scope:this,
            name:"savings",
            allowNegative:false,
            value:this.components.data.savings!=undefined?this.components.data.savings:0
        });
        arr.push(savings);
        
        for(var i=0; i< this.components.count; i++){
            
            var fieldname = this.components.data.taxablecomponents[i].name;
            var value = this.components.data.taxablecomponents[i].value;
            var compid = this.components.data.taxablecomponents[i].compid;
            
            var components = new Wtf.form.NumberField({
                fieldLabel: fieldname+WtfGlobal.addLabelHelp(fieldname),
                scope:this,
                name:compid,
                allowNegative:false,
                value:value!=undefined?value:0
            });
            
            arr.push(components);
            
        }
        
        return arr;

    },
    saveCostCenterRequest: function(){
        if(this.MytaxDecForm.form.isValid()){
    		this.MytaxDecForm.getForm().submit({
                waitMsg:WtfGlobal.getLocaleText("hrms.payroll.saving.incometax.information"),
                params:{
                    userid:this.userid,
                    year:this.year
                },
                success:function(){
                        Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.user.incometax.saved.successfully")); 
                        this.close();
                },
                failure:function(f,a){
                	Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.error.saving.user.incometax"));
                },
                scope:this
            });
    	}else{
    		ResponseAlert(152);
    	}
    }
});
