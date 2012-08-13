Add Following line after:<value>/masterDB/Componentmaster.hbm.xml</value> in application context

<value>/masterDB/MalaysiaFormAmanahSahamNasional.hbm.xml</value>
<value>/masterDB/MalaysiaCompanyForm.hbm.xml</value>
<value>/masterDB/MalaysiaFormTabungHaji.hbm.xml</value>
<value>/masterDB/MalaysiaFormCP21.hbm.xml</value>
<value>/masterDB/MalaysiaFormHRDLevy.hbm.xml</value>
<value>/masterDB/MalaysiaFormTP1.hbm.xml</value>
<value>/masterDB/MalaysiaFormTP2.hbm.xml</value>
<value>/masterDB/MalaysiaFormTP3.hbm.xml</value>
<value>/masterDB/MalaysiaFormCP39.hbm.xml</value>
<value>/masterDB/MalaysiaFormCP39A.hbm.xml</value>
<value>/masterDB/MalaysiaFormPCB2.hbm.xml</value>
<value>/masterDB/MalaysiaFormEA.hbm.xml</value>



Add Following line after:<mapping resource="masterDB/Componentmaster.hbm.xml"/> in hibernate.cfg

<mapping resource="masterDB/MalaysiaFormAmanahSahamNasional.hbm.xml"/>
<mapping resource="masterDB/MalaysiaCompanyForm.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormTabungHaji.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormCP21.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormHRDLevy.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormTP1.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormTP2.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormTP3.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormCP39.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormCP39A.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormPCB2.hbm.xml"/>
<mapping resource="masterDB/MalaysiaFormEA.hbm.xml"/>



web.xml:
<!--<servlet>
        <servlet-name>context</servlet-name>
        <servlet-class>
		 org.springframework.web.context.ContextLoaderServlet
	  </servlet-class>
        <load-on-startup>1</load-on-startup>
</servlet>  -->

<listener>
  	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>



DB Changes:


update malaysian_deductions set name ='Gift of phone' where description like 'Gift of fixed line telephone, mobile phone, pager or Personal Digital Assistant%';

select id FROM malaysian_deductions where name='Gift of phone';

update malaysian_deductions set name ='Gift of Mobile phone' where description='Mobile phone.' and parent ='ebfe697c-988f-102f-9377-001e4f432f82';

update malaysian_deductions set name ='Gift of Fixed line telephone' where description='Fixed line telephone.' and parent ='ebfe697c-988f-102f-9377-001e4f432f82';





update malaysian_deductions set uniquecode=1  where name="Children";
update malaysian_deductions set uniquecode=2  where name="Spouse";
update malaysian_deductions set uniquecode=3  where name="Individual";
update malaysian_deductions set uniquecode=4  where name="Disabled Individual";
update malaysian_deductions set uniquecode=5  where name="Disabled Spouse";
update malaysian_deductions set uniquecode=6  where name="Disabled children studying in diploma or higher level (in Malaysia)/degree or its equivalent (outside Malaysia)";
update malaysian_deductions set uniquecode=7  where name="Disabled children";
update malaysian_deductions set uniquecode=8  where name="Under the age of 18 years";
update malaysian_deductions set uniquecode=9  where name="Above 18 years and receiving full-time instruction at diploma level onwards in an institution of higher education in Malaysia";
update malaysian_deductions set uniquecode=10  where name="Above 18 years and receiving full-time instruction at degree level onwards in an institution of higher education outside Malaysia";
update malaysian_deductions set uniquecode=11  where name="Interest on Housing Loan";
update malaysian_deductions set uniquecode=12  where name="Higher Education Fees (Self)";
update malaysian_deductions set uniquecode=13  where name="Education and medical insurance premium";
update malaysian_deductions set uniquecode=14  where name="Contribution to a Private Retirement Scheme and Payment of Deferred Annuity";
update malaysian_deductions set uniquecode=15  where name="Complete medical examination for self, spouse or child";
update malaysian_deductions set uniquecode=16  where name="Medical Expenses on Serious Diseases";
update malaysian_deductions set uniquecode=17  where name="Medical Treatment, Special Needs or Carer Expenses of Parents";
update malaysian_deductions set uniquecode=18  where name="Payment of Alimony to Former Wife";
update malaysian_deductions set uniquecode=19  where name="Purchase of Books/Magazines/Journals/Similar Publications";
update malaysian_deductions set uniquecode=20  where name="Purchase of Personal Computer";
update malaysian_deductions set uniquecode=21  where name="Purchase of Sports Equipment";
update malaysian_deductions set uniquecode=22  where name="Subscription Fee of Internet Broadband";
update malaysian_deductions set uniquecode=23  where name="Basic supporting equipment for disabled self, spouse, child or parent";
update malaysian_deductions set uniquecode=24 where name="Net Deposit in Skim Simpanan Pendidikan Nasional (SSPN)";
update malaysian_deductions set uniquecode=25 where name="Principle amount for Education loan";
update malaysian_deductions set uniquecode=26 where name="Pager and Personal Digital Assistant (PDA)";
update malaysian_deductions set uniquecode=27 where name="Mobile phone";
update malaysian_deductions set uniquecode=28 where name="Fixed line telephone";
update malaysian_deductions set uniquecode=29 where name="Subscription of broadband";
update malaysian_deductions set uniquecode=30 where name="Principle amount for Car loan";
update malaysian_deductions set uniquecode=31 where name="Monthly bills for subscription of broadband";
update malaysian_deductions set uniquecode=32 where name="Pager or Personal Digital Assistant (PDA)";
update malaysian_deductions set uniquecode=33 where name="Gift of Mobile phone";
update malaysian_deductions set uniquecode=34 where name="Gift of Fixed line telephone";
update malaysian_deductions set uniquecode=35 where name="Subsidised interest for housing education or car loan";
update malaysian_deductions set uniquecode=36 where name="Employer contribution of subsidised interest for Car";
update malaysian_deductions set uniquecode=37 where name="Principle amount for Housing loan";
update malaysian_deductions set uniquecode=38 where name="Child care allowance in respect of children up to 12 years of age";
update malaysian_deductions set uniquecode=39 where name="Gift of new personal computer";
update malaysian_deductions set uniquecode=40 where name="Meal allowance";
update malaysian_deductions set uniquecode=41 where name="Parking rate and parking allowance";
update malaysian_deductions set uniquecode=42 where name="Perquisite";
update malaysian_deductions set uniquecode=43 where name="Travelling Allowance";
update malaysian_deductions set uniquecode=44 where name="Employer contribution of subsidised interest for Housing";
update malaysian_deductions set uniquecode=45 where name="Employer contribution of subsidised interest for Education";
update malaysian_deductions set uniquecode=46 where name="Gift of phone";





update malaysian_deductions set description='The purchase of any supporting equipment for one\'s own use, if he/she is a disabled person or for the use of his/her spouse, child or parent, who is a disabled person may be claimed but limited to a maximum of RM5,000.00 in a basis year. Basic supporting equipment includes haemodialysis machine, wheel chair, artificial leg and hearing aid but exclude optical lenses and spectacles.' where name='Basic supporting equipment for disabled self, spouse, child or parent';


update malaysian_deductions set description='Medical Treatment, Special Needs or Carer Expenses of Parents
Medical treatment, special needs and carer for parents are limited to RM5,000.00 in a basis year. Medical expenses which qualify for deductions includes:                                           
i. medical care and treatment provided by a nursing home; and
ii. dental treatment limited to tooth extraction, filling, scaling and cleaning but not including cosmetic dental treatment.
The claim must be supported by a certified medical practitioner registered with the Malaysian Medical Council that the medical conditions of the parents require medical treatment or special needs or carer.
The parents shall be resident in Malaysia. The medical treatment and care services are provided in Malaysia.
In the case of carer, shall be proved by a written certification, receipt or copy of carer\'s work permit. \'Carer\' shall not include that individual, husband, wife or the child of that individual.' where name='Medical Treatment, Special Needs or Carer Expenses of Parents';


update malaysian_deductions set description='Net Deposit in Skim Simpanan Pendidikan Nasional (SSPN) | Amount deposited in SSPN by an individual for his children\'s education is deductible up to a maximum of RM3,000.00 per year. The deduction is limited to the net amount deposited in that basis year only.' where name='Net Deposit in Skim Simpanan Pendidikan Nasional (SSPN)';





update malaysian_deductions set name='Number of disabled children studying in diploma or higher level (in Malaysia)/degree or its equivalent (outside Malaysia)' where uniquecode = 6;

update malaysian_deductions set name='Number of disabled children' where uniquecode = 7;

update malaysian_deductions set name='Number of children under the age of 18 years' where uniquecode = 8;

update malaysian_deductions set name='Number of children above 18 years and receiving full-time instruction at diploma level onwards in an institution of higher education in Malaysia' where uniquecode = 9;

update malaysian_deductions set name='Number of children above 18 years and receiving full-time instruction at degree level onwards in an institution of higher education outside Malaysia' where uniquecode = 10;
