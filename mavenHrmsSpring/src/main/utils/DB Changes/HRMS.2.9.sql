ALTER TABLE componentmaster DROP FOREIGN KEY `FKFADFD59F5FE81E95`;

ALTER TABLE componentmaster DROP INDEX `FKFADFD59F5FE81E95`;

ALTER TABLE payrollhistory modify jobtitleid  varchar(255);

alter table componentmaster modify column percentamount double;

alter table componentmaster drop column percentamount;


###Config Changes:
Add Following line after:<value>/masterDB/Componentmaster.hbm.xml</value> in application context


<value>/masterDB/SpecifiedComponents.hbm.xml</value>
<value>/masterDB/MalaysianDeduction.hbm.xml</value>
<value>/masterDB/MalaysianTaxSlab.hbm.xml</value>
<value>/masterDB/MalaysianUserIncomeTaxInfo.hbm.xml</value>
<value>/masterDB/MalaysianUserTaxComponent.hbm.xml</value>
<value>/masterDB/MalaysianUserTaxBenefits.hbm.xml</value>
<value>/masterDB/MalaysianUserTaxComponentHistory.hbm.xml</value>


Add Following line after:<mapping resource="masterDB/Componentmaster.hbm.xml"/> in hibernate.cfg
<mapping resource="masterDB/SpecifiedComponents.hbm.xml"/>
<mapping resource="masterDB/MalaysianDeduction.hbm.xml"/>
<mapping resource="masterDB/MalaysianTaxSlab.hbm.xml"/>
<mapping resource="masterDB/MalaysianUserIncomeTaxInfo.hbm.xml"/>
<mapping resource="masterDB/MalaysianUserTaxComponent.hbm.xml"/>
<mapping resource="masterDB/MalaysianUserTaxBenefits.hbm.xml"/>
<mapping resource="masterDB/MalaysianUserTaxComponentHistory.hbm.xml"/>

