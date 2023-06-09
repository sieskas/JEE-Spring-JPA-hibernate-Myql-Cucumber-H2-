<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User List</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
</head>
<body>
<div class="container">
    <%-- Display success message if it exists --%>
    <% if (session.getAttribute("successMessage") != null) { %>
    <div class="alert alert-success" role="alert">
        <%= session.getAttribute("successMessage") %>
    </div>
    <% session.removeAttribute("successMessage");
    } %>
        <div class="d-flex justify-content-between align-items-center">
            <h1>User List</h1>
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button type="submit" class="btn btn-danger">Logout</button>
            </form>
        </div>
        <p>Vous êtes connecté en tant que : <%= session.getAttribute("email") %></p>
        <a href="user-create.jsp" class="btn btn-primary mb-3">Create User</a>
    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <%int i = 0;%>
        <c:forEach items="${userList}" var="user">
            <tr>
                <td><%= ++i %></td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td> <%-- New column with form and Delete button --%>
                    <form action="${pageContext.request.contextPath}/delete-user" method="post">
                        <input type="hidden" name="email" value="${user.email}" />
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<!-- Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
        integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
        integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
        crossorigin="anonymous"></script>
</body>
</html>