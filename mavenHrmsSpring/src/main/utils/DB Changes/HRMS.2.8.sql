Entry in applicationContext.xml after line - <value>/masterDB/Template.hbm.xml</value>
<value>/com/krawler/common/admin/CostCenter.hbm.xml</value>
<value>/masterDB/Componentmaster.hbm.xml</value>
<value>/masterDB/PayrollHistory.hbm.xml</value>
<value>/masterDB/ComponentResourceMapping.hbm.xml</value>
<value>/masterDB/ComponentResourceMappingHistory.hbm.xml</value>

Entry in hibernate.cfg.xml after line - <mapping resource="masterDB/Template.hbm.xml"/>
<mapping resource="com/krawler/common/admin/CostCenter.hbm.xml"/>
<mapping resource="masterDB/Componentmaster.hbm.xml"/>
<mapping resource="masterDB/PayrollHistory.hbm.xml"/>
<mapping resource="masterDB/ComponentResourceMapping.hbm.xml"/>
<mapping resource="masterDB/ComponentResourceMappingHistory.hbm.xml"/>


ALTER TABLE CompanyPreferences MODIFY payrollbase varchar(255) Default 'Template';
update CompanyPreferences set payrollbase = 'Template';
ALTER TABLE CompanyPreferences MODIFY timesheetjob char(1) Default 'T';
update CompanyPreferences set timesheetjob = 'T';
INSERT INTO `master` VALUES (20,'Cost Center','a4792363-b0e1-4b67-992b-2851234d5ea6','F'),(21,'Component Sub Type','a4792363-b0e1-4b67-992b-2851234d5ea6','F'),(22,'Frequency','a4792363-b0e1-4b67-992b-2851234d5ea6','F'),(24,'Payment Type','a4792363-b0e1-4b67-992b-2851234d5ea6','F');
INSERT INTO `master` VALUES (25,'Timesheet Job','a4792363-b0e1-4b67-992b-2851234d5ea6','F');

INSERT INTO `MasterData` VALUES ('ff80808133ceee3a0133cf04db290010',24,'Default',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290111',22,'Periodically',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290112',22,'Monthly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290113',22,'Daily',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290114',22,'Hourly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290115',22,'Quarterly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290116',22,'Half-yearly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290117',22,'Yearly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290118',22,'4-weekly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290119',22,'Once-only',NULL,0,0,NULL),('ff80808133ceee3a0133cf04db290120',22,'Bi-weekly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04fe800011',24,'Monthly',NULL,0,0,NULL),('ff80808133ceee3a0133cf04fe800013',24,'Yearly',NULL,0,0,NULL),('ff80808133ceee3a0133cf0520aa0012',24,'Quarterly',NULL,0,0,NULL);

