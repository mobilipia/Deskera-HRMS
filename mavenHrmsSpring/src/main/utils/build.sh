#Linux build script for INCEIF project-path is as per code in /home/ajay/NetBeansProjects/INCEIFG/INCEIF/

echo "Minifying Process Starting............................."
echo "Merging css files............................."

export codepath="/home/krawler/Desktop/shs_projects/HrmsPayrollSpring_maven/mavenHrmsSpring/src/main"
cd $codepath/utils
cd FileMerge
echo '---------'
pwd
echo '---------'
php mergefilescss.php css.txt 3
echo "Merging js files............................."
echo '---------'
pwd
echo '---------'
php mergefilesjs.php js.txt 3
cp $codepath/webapp/scripts/HQLHrms.js $codepath/webapp/scripts/hrmstemp.js
cp $codepath/webapp/style/HQLHrms.css $codepath/webapp/style/hrmstemp.css
php mergefiles.php payroll.txt 3
cp $codepath/webapp/scripts/HQLPayroll.js $codepath/webapp/scripts/payrolltemp.js
php mergefilesjs.php dashboardjs.txt 3
cp $codepath/webapp/scripts/HQLPayroll.js $codepath/webapp/scripts/dashboard-ex-temp.js

#cp $codepath/web/WEB-INF/web.sso.xml $codepath/web/WEB-INF/web.xml
#cp $codepath/src/java/hibernate.sso.cfg.xml $codepath/src/java/hibernate.cfg.xml



#cp project_ex.js projecttemp.js

cd
mkdir tempyui
cd tempyui
echo "Compressing js files............................."
java -jar $codepath/utils/YUICompressor/yuicompressor-2.2.5.jar --charset UTF-8 -o $codepath/webapp/scripts/HQLHrms.js $codepath/webapp/scripts/hrmstemp.js 2>&1
java -jar $codepath/utils/YUICompressor/yuicompressor-2.2.5.jar --charset UTF-8 -o $codepath/webapp/scripts/HQLPayroll.js $codepath/webapp/scripts/payrolltemp.js 2>&1
java -jar $codepath/utils/YUICompressor/yuicompressor-2.2.5.jar --charset UTF-8 -o $codepath/webapp/scripts/dashboard-ex.js $codepath/webapp/scripts/dashboard-ex-temp.js 2>&1
#java -jar /home/krawler/NetBeansProjects/HQLHrmsProject/utils/YUICompressor/yuicompressor-2.2.5.jar --charset UTF-8 "/home/krawler/NetBeansProjects/HQLHrmsProject/web/scripts/project.js" "/home/krawler/NetBeansProjects/HQLHrmsProject/utils/FileMerge/projecttemp.js"
echo "Compressing css files............................."
java -jar $codepath/utils/YUICompressor/yuicompressor-2.2.5.jar --charset UTF-8 -o $codepath/webapp/style/HQLHrms.css $codepath/webapp/style/hrmstemp.css 2>&1
echo "Removing temporary files............................."
rm $codepath/webapp/scripts/hrmstemp.js
rm $codepath/webapp/scripts/payrolltemp.js
rm $codepath/webapp/style/hrmstemp.css
rm $codepath/webapp/scripts/dashboard-ex-temp.js

cd
rm -rf tempyui
echo "Minifying Process Completed............................."
