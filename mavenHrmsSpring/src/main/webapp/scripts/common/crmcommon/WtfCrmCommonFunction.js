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

Wtf.deleteGlobal = function (EditorGrid,EditorStore,title,keyid,valueid,table,msg1,msg2,msg3,treeid)
    {
        var s=EditorGrid.getSelectionModel().getSelections();
        var duration = 60;
        EditorGrid.getSelectionModel().clearSelections();
        if(s.length>0){
            var tob=Wtf.getCmp('tree');
            var moduleNode=tob.getNodeById(treeid);
            Wtf.onlyhighLightRecordLoop(EditorGrid, "FF0000", duration, s, title);
            Wtf.highLightTreeNodeLoop("FF0000", duration, s, tob, moduleNode, valueid);

            Wtf.MessageBox.show({

                title: WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg: WtfGlobal.getLocaleText({key:"hrms.Messages.Deleteselected",params:[title]})+"<br><br><b>"+WtfGlobal.getLocaleText("hrms.Messages.DateCannotbeRetrive"),
                buttons: Wtf.MessageBox.OKCANCEL,
                animEl: 'upbtn',
                icon: Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(bt){
                    if(bt=="ok"){
                        var jsondata="";
                        for(var i=0;i<s.length;i++)
                        {
                            if(s[i].get("validflag") != -1 ) {
                                jsondata+="{'"+keyid+"':'" + s[i].get(valueid) + "'},";
                            }
                        }
                        var trmLen = jsondata.length - 1;
                        var finalStr = jsondata.substr(0,trmLen);
                        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.Deletingdata"));
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + 'crm.jsp',
                            params:{
                                jsondata:finalStr,
                                table:table,
                                flag:41
                            }
                        },
                        this,
                        function(res)
                            {
                                this.delayFunction=new Wtf.util.DelayedTask(function(){
                                    Wtf.removeSelectedTreeNode(tob, moduleNode, valueid, s);
                                    Wtf.removeSelectedRows(EditorGrid, s);
                                    EditorStore.load({params:{start:0, limit: EditorGrid.getBottomToolbar().pageSize}});
                                },this);

                                Wtf.onlyhighLightRecordLoop(EditorGrid, "ffffff", duration, s, title, true);
                                Wtf.highLightTreeNodeLoop("ffffff", duration, s, tob, moduleNode, valueid);

                                Wtf.onlyhighLightRecordLoop(EditorGrid, "FF0000", 3, s, title);
                                Wtf.highLightTreeNodeLoop("FF0000", 3, s, tob, moduleNode, valueid);

                                this.delayFunction.delay(1800);
                                if(title=="Lead"||title=="Opportunity"||title=="Account"||title=="Activity")
                                {
                                    bHasChanged = true;
                                }
                                Wtf.updateProgress();
                                ResponseAlert(msg1);
                            },
                        function()
                        {
                            Wtf.updateProgress();
                            ResponseAlert(msg2);
                        }
                        )

                    } else{
                        var rows =[];
                        Wtf.highLightTreeNodeLoop("ffffff", duration+5, s, tob, moduleNode, valueid);
                        Wtf.onlyhighLightRecordLoop(EditorGrid, "ffffff", duration+1, s, title, true);
                        for(var i=0; i<s.length; i++) {
                            var ri=EditorStore.indexOf(s[i]);
                            rows.push(ri);
                        }
                        EditorGrid.getSelectionModel().selectRows(rows);
                    }
                }
            });
        }
        else {
        Wtf.updateProgress();
        ResponseAlert(msg3);
        }
    }


Wtf.commonWaitMsgBox = function(msg) {
        Wtf.MessageBox.show({
            msg: msg,
            width:290,
            wait:true,
            title:WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow200"),
            waitConfig: {interval:200}
        });
}

Wtf.updateProgress =function() {
    Wtf.MessageBox.hide();
}

Wtf.PayrollSetDatevalue =function(obj) {
	
	var returnObject={};
	
	var frequency = eval(obj.frequencyStoreCmb.getValue());
	var selectedYear = obj.yearCmb.getRawValue();
	var selectedMonth = eval(obj.monthCmb.getValue());
	var month = new Array("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
	var startdt;
	var enddt;
	if(frequency==0){//Once a month
		startdt = Date.parseDate(selectedYear+"-"+month[selectedMonth-1]+"-01", "Y-m-d");
		enddt = startdt.add(month[selectedMonth-1],+0).getLastDateOfMonth();
    } else if(frequency==1){//Once a week
    	var date = new Date(selectedYear+'-01-01');
    	startdt = new Date(date.clone().setDate(date.getDate()+((selectedMonth-1)*7)));
		enddt = new Date(date.clone().setDate(date.getDate()+6+((selectedMonth-1)*7)));
    } else if(frequency==2){//Twice a month
    	var startDates = new Array("16", "01");
    	var endDate = "15";
    	startdt = Date.parseDate(selectedYear+"-"+month[parseInt((selectedMonth-1)/2)]+"-"+startDates[selectedMonth%2], "Y-m-d");
    	if(selectedMonth%2==0){
    		endDate = startdt.add(month[parseInt((selectedMonth-1)/2)],+0).getLastDateOfMonth().format('d');
    	}
    	enddt = Date.parseDate(selectedYear+"-"+month[parseInt((selectedMonth-1)/2)]+"-"+endDate, "Y-m-d");	
    }
	returnObject={
			startdt:startdt,
			enddt:enddt
	}
	return returnObject
   // this.startdate.setValue(startdt);
   // this.enddate.setValue(enddt);
}
