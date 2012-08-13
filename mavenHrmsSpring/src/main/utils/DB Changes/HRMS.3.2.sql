Config Changes:

applicationContext:

Add Following line of code in applicationComtext after <value>/com/krawler/hrms/timesheet/timesheet.hbm.xml</value>:

<value>/com/krawler/hrms/timesheet/TimesheetTimer.hbm.xml</value>


hibernate.cfg.xml:

Add Following line of code in hibernate.cfg.xml after <mapping resource="com/krawler/hrms/timesheet/timesheet.hbm.xml"/>:

<mapping resource="com/krawler/hrms/timesheet/TimesheetTimer.hbm.xml"/>

# Execute below queries for Audit Trail entries in Organization Chart

insert into auditgroup  values('20', 'Organization');
insert into auditaction values('71', 'Node assigned','20');
insert into auditaction values('72', 'Node removed','20');


update hrms_empprofile set reportto = null where userid in (select creator from company where creator is not null);