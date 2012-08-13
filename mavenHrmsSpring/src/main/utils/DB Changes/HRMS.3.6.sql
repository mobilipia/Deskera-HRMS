
alter table employercontributionmaster CHARACTER SET utf8 COLLATE utf8_general_ci;

alter table employercontributionmaster modify companyid varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci;

alter table employercontributionmaster modify empcontritype varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci;

alter table employercontributionmaster modify empcontricode varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci;

alter table employercontributionmaster modify isdefault char(1) CHARACTER SET utf8 COLLATE utf8_general_ci;

alter table employercontributionmaster modify expr longtext CHARACTER SET utf8 COLLATE utf8_general_ci;
