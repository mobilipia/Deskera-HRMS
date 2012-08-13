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
Wtf.ux.TextField = function(config) {
    Wtf.apply(this,config);
    Wtf.ux.TextField.superclass.constructor.call(this);
}

Wtf.extend(Wtf.ux.TextField, Wtf.form.TextField, {
    initComponent:function(config){
        Wtf.ux.TextField.superclass.initComponent.call(this,config);
        this.on('change',this.mychange,this);
    },
    mychange:function(field,newval){
        var retVal = Wtf.util.Format.stripTags(newval).trim();
        field.setValue(retVal);
        return retVal;
    }
});

Wtf.reg('striptextfield',Wtf.ux.TextField);

// Kuldeep Singh : Extended textfield to remove more than one spaces between two strings
Wtf.ux.ExtTextField = function(config) {
    Wtf.apply(this,config);
    Wtf.ux.ExtTextField.superclass.constructor.call(this);
}

Wtf.extend(Wtf.ux.ExtTextField, Wtf.form.TextField, {
    initComponent:function(config){
        Wtf.ux.ExtTextField.superclass.initComponent.call(this,config);
        this.on('change',this.mychange,this);
    },
    mychange:function(field,newval){
        var retVal = Wtf.util.Format.stripTags(newval).trim();
        retVal = WtfGlobal.replaceAll(retVal,"\\s+"," ");
        field.setValue(retVal);
        return retVal;
    }
});

Wtf.reg('extstriptextfield',Wtf.ux.ExtTextField);

// Kuldeep Singh : Extended TextArea to strip html tags in TextArea.
Wtf.ux.TextArea = function(config) {
    Wtf.apply(this,config);
    Wtf.ux.TextArea.superclass.constructor.call(this);
}

Wtf.extend(Wtf.ux.TextArea, Wtf.form.TextArea, {
    initComponent:function(config){
        Wtf.ux.TextArea.superclass.initComponent.call(this,config);
        this.on('change',this.mychange,this);
    },
    mychange:function(field,newval){
        var retVal = Wtf.util.Format.stripTags(newval).trim();
        retVal = WtfGlobal.replaceAll(retVal,"\\s+"," ");
        field.setValue(retVal);
        return retVal;
    }
});

Wtf.reg('striptextarea',Wtf.ux.TextArea);
