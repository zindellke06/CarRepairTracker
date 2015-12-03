<%-- 
    Document   : displayRecords
    Created on : Nov 3, 2015, 4:52:40 PM
    Author     : John Phillips
--%>

<%@page import="java.util.List, model.Car"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>KZ Car Repairs</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="mystyle.css">
    </head>
    <body>
        <h1><a href="home.html">KZ Car Repairs</a></h1>
        <h2>Car Report</h2>
        <%
            List<Car> mydata = (List<Car>) request.getAttribute("mydata");
            out.println("<table>");
            for (Car car : mydata) {
                out.println(car.inHTMLRowFormat());
            }
            out.println("</table>");
        %>
    </body>
</html>
