Add following line in web.xml after context-param of hrmsURL:

<context-param>
	<param-name>eleaveURL</param-name>
    <param-value>http://192.168.0.198:8080/eleave/</param-value>
</context-param>