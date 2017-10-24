<%--
  Created by IntelliJ IDEA.
  User: Roman from Ryasne-2
  Date: 24.10.2017
  Time: 23:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Main Logs</title>
  </head>
  <body>
  <table border="2">
    <tr>
      <td>Id</td>
      <td>Ip</td>
      <td>Message</td>
      <td>DateTime</td>
      <td>Count</td>
    </tr>
    <%@ page import="java.sql.*" %>
    <%@ page import="com.microsoft.sqlserver.jdbc.*" %>
    <%
      try
      {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        String connectionString = "jdbc:sqlserver://RZR2PC\\RZR2SQL;"
                        + "database=DbForJava;"
                        + "user=sa;"
                        + "password=sa0123Roma;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";

        Connection conn = DriverManager.getConnection(connectionString);

        Statement stmt = conn.createStatement();

        String query = "SELECT * FROM Logs";

        ResultSet rs = stmt.executeQuery(query);

        while(rs.next())
        {

    %>
    <tr>
      <td><%= rs.getInt("Id") %></td>
      <td><%= rs.getString("Ip") %></td>
      <td><%= rs.getString("Message") %></td>
      <td><%= rs.getString("Date") %></td>
      <td><%= rs.getString("Count") %></td>
    </tr>
    <%

      }
    %>
  </table>
  <%
      rs.close();
      stmt.close();
      conn.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  %>
  </body>
</html>
