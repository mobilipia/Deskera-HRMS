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
function getDocsAndCommentList (grid,Recid,valid,id)
{
    var s = grid.getSelectionModel().getSelections();
    if( s.length == 1) {
        if(valid==1){
            var updownCompE = Wtf.getCmp(id+'HRMSupdownCompo');
            var downUpPanel = Wtf.getCmp(id+"dloadpanelcenter");
            var panTitleTpl = new Wtf.XTemplate(  '' );
            panTitleTpl.overwrite(downUpPanel.body,{});

            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + 'hrms.jsp',
                url:"Performance/Goal/getComments.pf",
                params:{
                    recid:Recid,
                    flag:210
                }
            },this,
            function(res)
            {
                var resp=eval('('+res+')');
                /*  Create Comments Div   */
                DataTpl = new Wtf.XTemplate(  updownCompE.panCommTitle );
                DataTpl.append(downUpPanel.body,{});
                if(resp.commData) {
                    var commData=resp.commData;
                    if(commData.commList.length>0) {
                        DataTpl= new Wtf.XTemplate(  updownCompE.newCommContentWithPerm  );
                        for(var i2 = 0; i2 < commData.commList.length; i2++) {
                            resp.commData.commList[i2].comment = unescape(commData.commList[i2].comment);
                            DataTpl.append(downUpPanel.body,commData.commList[i2]);
                        }
                    } else {
                        var tpl= new Wtf.Template("<div class='commentTemplate'>", "<div id='{msgDiv}' class='commentTemplate1'>"+WtfGlobal.getLocaleText("hrms.common.nocommenttoshow")+"</div></div>");
                        tpl.overwrite(Wtf.getCmp(id+"dloadpanelcenter").body,'');
                    }
                } else {
                    DataTpl= new Wtf.XTemplate( updownCompE.noPerm );
                    DataTpl.append(downUpPanel.body, {});
                }
            },
            function(res)
            {
                var tpl0= new Wtf.XTemplate(  updownCompE.Failed  );
                tpl0.overwrite(downUpPanel.body,{});
            }
            );
        }
        else{           
            var tpl= new Wtf.Template("<div class='commentTemplate'>", "<div id='{msgDiv}' class='commentTemplate1'>Please select a valid record to see details.</div></div>");
            tpl.overwrite(Wtf.getCmp(id+"dloadpanelcenter").body,'');
        }
    } else{        
        var tpl1= new Wtf.Template("<div class='commentTemplate'>", "<div id='{msgDiv}' class='commentTemplate1'>"+WtfGlobal.getLocaleText("hrms.common.pleaseselectonerecordtoseedetails")+"</div></div>");
        tpl1.overwrite(Wtf.getCmp(id+"dloadpanelcenter").body,'');
    }
}

