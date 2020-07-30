<%--
  Created by IntelliJ IDEA.
  User: alex
  Date: 08.04.20
  Time: 14:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>user</title>
  </head>
  <body>
    <p>
      <% out.println(request.getAttribute("name")); %>
    </p>
  </body>
</html>
