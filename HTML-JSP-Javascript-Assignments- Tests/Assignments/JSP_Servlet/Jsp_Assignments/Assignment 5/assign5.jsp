<%@ taglib uri = "myLib.tld" prefix ="sql" %>

<%-- open a database connection --%>
<sql:connection  id="conn1" url="jdbc:oracle:thin:@192.168.12.16:1521:oracle8i"  driver="oracle.jdbc.driver.OracleDriver" user="scott" password="tiger" />

<table border="1">
<sql:statement id = "stat1" conn = "conn1">
	<sql:query>
		select  * from employee
	</sql:query>

<%-- loop through the rows of the query --%>
	<sql:resultSet id = "rs">
	<tr>
		<td><sql:getColumn position = "1" /></td>
		<td><sql:getColumn position = "2" /></td>
		<td><sql:getColumn position = "3" /></td>
	</tr>
	</sql:resultSet>
</sql:statement>
</table>
<sql:closeConnection conn = "conn1" />
