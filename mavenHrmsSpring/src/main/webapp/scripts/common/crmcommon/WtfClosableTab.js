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
Wtf.ux.ClosableTabPanel=function(config){
    this.isClosable = true;
    Wtf.ux.ClosableTabPanel.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.ux.ClosableTabPanel, Wtf.Panel, {
    closeMsg:WtfGlobal.getLocaleText("hrms.common.data.filled.still.unsaved")+"<br>"+WtfGlobal.getLocaleText("hrms.common.exit.without.saving"),
    initComponent:function(config){
        Wtf.ux.ClosableTabPanel.superclass.initComponent.call(this, config);
        this.on('closeTemplate', this.closeTemplate, this);
        this.on('beforeclose', this.askToClose, this);
    },

    askToClose:function(){
        // write some function here if required, to check or to set isClosable true/false
        if(this.closeWindow==true)
            this.isClosable=true;
        else if(Wtf.getCmp("templatehtmleditor"+this.templateid)!=undefined && Wtf.getCmp('template_name_txt'+this.templateid)!=undefined && Wtf.getCmp('template_subject'+this.templateid)!=undefined)
            if(Wtf.getCmp("templatehtmleditor"+this.templateid).getValue().trim()!="" || Wtf.getCmp('template_name_txt'+this.templateid).getValue().trim()!="" || Wtf.getCmp('template_subject'+this.templateid).getValue().trim()!="")
                this.isClosable=false;
        if(this.isClosable !== true){
            Wtf.MessageBox.show({
                title:this.title,
                msg:this.closeMsg,
                buttons:Wtf.MessageBox.YESNO,
                animEl:'mb9',
                fn:function(btn){
                    if(btn!="yes")return;
                    this.ownerCt.remove(this);
                },
                scope:this,
                icon: Wtf.MessageBox.QUESTION
            });
        }
        return this.isClosable;
    },
    closeTemplate: function(){
        this.ownerCt.remove(this);
    }

});
