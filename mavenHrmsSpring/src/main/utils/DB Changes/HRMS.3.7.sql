CREATE TABLE `component_rule` (
  `id` VARCHAR(255)  NOT NULL,
  `lowerlimit` DOUBLE  DEFAULT 0,
  `upperlimit` DOUBLE  DEFAULT 0,
  `coefficient` DOUBLE  DEFAULT 0,
  `component` VARCHAR(255)  NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY `new_fk_constraint` (`component`)
    REFERENCES `componentmaster` (`compid`)
  
)
ENGINE = InnoDB
CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `user_tax_declaration` (
  `id` VARCHAR(255)  NOT NULL,
  `component` VARCHAR(255)  NOT NULL,
  `user` VARCHAR(255)  NOT NULL,
  `value` DOUBLE  DEFAULT 0,

  PRIMARY KEY (`id`),
  CONSTRAINT `new_fk_constraint1` FOREIGN KEY `new_fk_constraint1` (`component`)
    REFERENCES `componentmaster` (`compid`),
  CONSTRAINT `new_fk_constraint2` FOREIGN KEY `new_fk_constraint2` (`user`)
    REFERENCES `users` (`userid`)
  
 
)
ENGINE = InnoDB
CHARACTER SET utf8 COLLATE utf8_general_ci;


ALTER TABLE `hrms_empprofile` ADD COLUMN `savings` DOUBLE  DEFAULT 0 AFTER `terminatedby`;

ALTER TABLE  `componentmaster` ADD COLUMN `istaxablecomponent` CHAR(1)  DEFAULT 'F' AFTER `computeon`;

alter table role drop index name;

update currency set htmlcode='CRC'  where currencyid='8';



Add following entries in applicationContext.xml after <value>/masterDB/MalaysiaFormEA.hbm.xml</value>:

<value>/masterDB/ComponentRule.hbm.xml</value>
<value>/masterDB/UserTaxDeclaration.hbm.xml</value>


Add following entries in hibernate.cfg.xml after <mapping resource="masterDB/MalaysiaFormEA.hbm.xml"/>:

<mapping resource="masterDB/ComponentRule.hbm.xml"/>
<mapping resource="masterDB/UserTaxDeclaration.hbm.xml"/>