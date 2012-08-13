# Sagar A - Import functionality for Payroll components. 
mysql> CREATE TABLE `modules` (
       `id` varchar(255) NOT NULL,
       `modulename` varchar(255) default NULL,
       `pojoclasspathfull` varchar(255) default NULL,
       `primarykey_methodname` varchar(255) default NULL,
       `uniquekey_methodname` varchar(255) default NULL,
       `uniquekey_hbmname` varchar(255) default NULL,
       PRIMARY KEY  (`id`)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
Query OK, 0 rows affected (0.12 sec)

mysql> CREATE TABLE `default_header` (
       `id` varchar(255) NOT NULL,
       `defaultHeader` varchar(255) default NULL,
       `moduleName` varchar(255) default NULL,
       `flag` int(11) default '0',
       `configid` varchar(255) default NULL,
       `pojoheadername` varchar(255) default NULL,
       `recordname` varchar(255) default NULL,
       `xtype` varchar(255) default NULL,
       `pojomethodname` varchar(255) default NULL,
       `validatetype` varchar(255) default NULL,
       `maxlength` int(11) default '0',
       `ismandatory` char(1) default '0',
       `required` char(1) default '0',
       `hbmnotnull` char(1) default '0',
       `defaultvalue` varchar(255) default NULL,
       `refmodule_pojoclassname` varchar(255) default NULL,
       `refdatacolumn_hbmname` varchar(255) default NULL,
       `reffetchcolumn_hbmname` varchar(255) default NULL,
       `allowimport` char(1) default '1',
       `customflag` char(1) default '0',
       `allowmapping` char(1) default 'T',
       `dbcolumnname` varchar(255) default NULL,
       `module` varchar(255) NOT NULL,
       PRIMARY KEY  (`id`),
       KEY `FK6CE0AE4B165B5A50` (`module`),
       CONSTRAINT `FK6CE0AE4B165B5A50` FOREIGN KEY (`module`) REFERENCES `modules` (`id`)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
Query OK, 0 rows affected (0.04 sec)

mysql> CREATE TABLE `importlog` (
       `id` varchar(255) NOT NULL,
       `filename` varchar(255) default NULL,
       `storagename` varchar(255) default NULL,
       `type` varchar(255) default NULL,
       `log` varchar(255) default NULL,
       `totalrecs` int(11) default '0',
       `rejected` int(11) default '0',
       `importdate` datetime NOT NULL,
       `module` varchar(255) NOT NULL,
       `user` varchar(255) NOT NULL,
       `company` varchar(255) NOT NULL,
       PRIMARY KEY  (`id`),
       KEY `FKF392517F7E350557` (`company`),
       KEY `FKF392517F165B5A50` (`module`),
       KEY `FKF392517FE8E8F059` (`user`),
       CONSTRAINT `FKF392517F165B5A50` FOREIGN KEY (`module`) REFERENCES `modules` (`id`),
       CONSTRAINT `FKF392517F7E350557` FOREIGN KEY (`company`) REFERENCES `company` (`companyid`),
       CONSTRAINT `FKF392517FE8E8F059` FOREIGN KEY (`user`) REFERENCES `users` (`userid`)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
Query OK, 0 rows affected (0.02 sec)



#Wage
INSERT INTO modules (id,modulename,pojoclasspathfull,primarykey_methodname,uniquekey_methodname,uniquekey_hbmname) VALUES ("e1e72896-bf85-102d-b644-001e58a64cb6", "Wage", "masterDB.Wagemaster", "Wageid", "Wcode", "wcode");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb6","Code","Wcode",0,50,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb6","Unit","Rate",0,50,'T',null,null,null,null,null, "string");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb6","Name","Wagetype",0,100,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb6","Cash","Cash",0,50,'T',null,null,null,null,null, "double");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb6","Compute On","Computeon",0,100,'F',null,null,null,null,null, "string");

#Deduction
INSERT INTO modules (id,modulename,pojoclasspathfull,primarykey_methodname,uniquekey_methodname,uniquekey_hbmname) VALUES ("e1e72896-bf85-102d-b644-001e58a64cb7", "Deduction", "masterDB.Deductionmaster", "Deductionid", "Dcode", "dcode");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb7","Code","Dcode",0,50,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb7","Unit","Rate",0,50,'T',null,null,null,null,null, "string");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb7","Name","Deductiontype",0,100,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb7","Cash","Cash",0,50,'T',null,null,null,null,null, "double");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb7","Compute On","Computeon",0,100,'F',null,null,null,null,null, "string");

#Tax
INSERT INTO modules (id,modulename,pojoclasspathfull,primarykey_methodname,uniquekey_methodname,uniquekey_hbmname) VALUES ("e1e72896-bf85-102d-b644-001e58a64cb8", "Tax", "masterDB.Taxmaster", "Taxid", "Tcode", "tcode");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb8","Code","Tcode",0,50,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb8","Unit","Rate",0,50,'T',null,null,null,null,null, "string");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb8","Name","Taxtype",0,100,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb8","Cash","Cash",0,50,'T',null,null,null,null,null, "double");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb8","Compute On","Computeon",0,100,'F',null,null,null,null,null, "string");

#Employee Contribution
INSERT INTO modules (id,modulename,pojoclasspathfull,primarykey_methodname,uniquekey_methodname,uniquekey_hbmname) VALUES ("e1e72896-bf85-102d-b644-001e58a64cb9", "Employee Contribution", "masterDB.EmployerContribution", "Id", "Empcontricode", "empcontricode");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb9","Code","Empcontricode",0,50,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb9","Unit","Rate",0,50,'T',null,null,null,null,null, "string");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb9","Name","Empcontritype",0,100,'T',null,null,null,null,null);
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb9","Cash","Cash",0,50,'T',null,null,null,null,null, "double");
INSERT INTO default_header(id,module,defaultHeader,pojomethodname,flag,maxlength,ismandatory,refmodule_pojoclassname,refdatacolumn_hbmname,reffetchcolumn_hbmname,xtype,defaultvalue, validatetype) 
VALUES (UUID(),"e1e72896-bf85-102d-b644-001e58a64cb9","Compute On","Computeon",0,100,'F',null,null,null,null,null, "string");

update default_header set defaultHeader = 'Code ID' where defaultHeader = 'Code';
update default_header set defaultHeader = 'Amount' where defaultHeader = 'Cash';

# Changes in web.xml for import functionality
<servlet>
    <servlet-name>bind</servlet-name>
    <servlet-class>org.mortbay.cometd.continuation.ContinuationCometdServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>bind</servlet-name>
    <url-pattern>/bind/*</url-pattern>
</servlet-mapping>

# - Please add these 3 entries after ConfigMaster.hbm.xml
# Changes in applicationContext.xml for Import functionality -

    <value>/com/krawler/common/admin/Modules.hbm.xml</value>
    <value>/com/krawler/common/admin/DefaultHeader.hbm.xml</value>
    <value>/com/krawler/common/admin/ImportLog.hbm.xml</value>

# Replace following line in applicationContext.xml -
<prop key="hibernate.dialect">com.krawler.hql.payroll.ExHibernate.ExMySQLInnoDBDialect</prop>

# Changes in hibernate.cfg.xml for Import functionality  -

    <mapping resource="com/krawler/common/admin/Modules.hbm.xml"/>
    <mapping resource="com/krawler/common/admin/DefaultHeader.hbm.xml"/>
    <mapping resource="com/krawler/common/admin/ImportLog.hbm.xml"/>   



#Import Permissions
insert into featurelist (featureid, featurename, displayfeaturename, moduleid) values ('ff808081236584940123ee85ee141103','importf','Import',null);
insert into activitylist (activityid,activityname,displayactivityname,feature) values ('ff8080812365849401236587da7e0004','viewimportlog','View Import Log','ff808081236584940123ee85ee141103');
insert into activitylist (activityid,activityname,displayactivityname,feature) values ('ff8080812365849401236587da7e0005','importpaycomp','Import Payroll Components','ff808081236584940123ee85ee141103');
insert into userpermission values ('ff808081236584940123ee85ee141103','1',3);

delete from activitylist where feature = 'ff808081236584940123ee85ee141103';
delete from userpermission where feature = 'ff808081236584940123ee85ee141103';
delete from featurelist where featureid = 'ff808081236584940123ee85ee141103';

# Changes in web.xml for notification functionality(ASHWIN) add on line no 18
<context-param>
        <param-name>hrmsURL</param-name>
        <param-value>http://192.168.0.86:8080/hrms/</param-value>
</context-param>

alter table users modify column address varchar(300);

    
    
#Kuldeep
update CompanyPreferences set approvesalary='F';
insert into auditaction values ("60","Salary authorized","4");
insert into auditaction values ("61","Salary unauthorized","4");
insert into auditaction values ("62","Salary deleted","4");