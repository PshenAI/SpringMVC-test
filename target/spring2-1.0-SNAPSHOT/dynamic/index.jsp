<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Prog.academy</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    </head>

    <body>
        <div class="container">
           <h3><img height="50" width="55" src="<c:url value="/static/logo.png"/>"/><a href="/">Contacts List</a></h3>

            <nav class="navbar navbar-default">
                <div class="container-fluid">
                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul id="groupList" class="nav navbar-nav">
                            <li><button type="button" id="add_contact" class="btn btn-default navbar-btn">Add Contact</button></li>
                            <li><button type="button" id="add_group" class="btn btn-default navbar-btn">Add Group</button></li>
                            <li><button type="button" id="delete_contact" class="btn btn-default navbar-btn">Delete Contact</button></li>
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Groups <span class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <li><a href="/">Default</a></li>
                                    <c:forEach items="${groups}" var="group">
                                        <li><a href="/group/${group.id}">${group.name}</a></li>
                                    </c:forEach>
                                </ul>
                            </li>
                        </ul>
                        <form class="navbar-form navbar-left" role="search" action="/search" method="post">
                            <div class="form-group">
                                <input type="text" class="form-control" name="pattern" placeholder="Search">
                            </div>
                            <button type="submit" class="btn btn-default">Submit</button>
                        </form>
                        <!-- Trigger the modal with a button -->
                        <button type="button" class="btn btn-primary navbar-btn" data-toggle="modal" data-target="#myModal">Download contacts</button>
                        <!-- Modal -->
                        <div id="myModal" class="modal fade" role="dialog">
                            <div class="modal-dialog">
                                <!-- Modal content-->
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                                        <h4 class="modal-title">Download contacts</h4>
                                    </div>
                                    <div class="modal-body">
                                        <p>All</p>
                                        <li><a href="/contact/download?type=1&group=0">JSON</a></li>
                                        <li><a href="/contact/download?type=2&group=0">XML</a></li>
                                        <c:forEach items="${groups}" var="group">
                                            <hr/>
                                            <p>${group.name}</p>
                                            <li><a href="/contact/download?type=1&group=${group.id}">JSON</a></li>
                                            <li><a href="/contact/download?type=2&group=${group.id}">XML</a></li>
                                            <hr/>
                                        </c:forEach>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <ul id="deletionList" class="nav navbar-nav">
                            <li class="dropdown">
                                <button type="button" id="delete_group" class="btn btn-danger navbar-btn dropdown-toggle" aria-haspopup="true"
                                        data-toggle="dropdown" aria-expanded="false">Delete group</button>
                                <ul class="dropdown-menu">
                                    <li><a href="/group/${group.id}">${group.name}</a></li>
                                    <c:forEach items="${groups}" var="group">
                                        <li><a href="/deleteGroup?groupId=${group.id}">Delete ${group.name}</a></li>
                                    </c:forEach>
                                </ul>
                            </li>
                        </ul>
                    </div><!-- /.navbar-collapse -->
                </div><!-- /.container-fluid -->
            </nav>

            <table class="table table-striped">
                <thead>
                <tr>
                    <td></td>
                    <td><b>Name</b></td>
                    <td><b>Surname</b></td>
                    <td><b>Phone</b></td>
                    <td><b>E-mail</b></td>
                    <td><b>Group</b></td>
                </tr>
                </thead>
                <c:forEach items="${contacts}" var="contact">
                    <tr>
                        <td><input type="checkbox" name="toDelete[]" value="${contact.id}" id="checkbox_${contact.id}"/></td>
                        <td>${contact.name}</td>
                        <td>${contact.surname}</td>
                        <td>${contact.phone}</td>
                        <td>${contact.email}</td>
                        <c:choose>
                            <c:when test="${contact.group ne null}">
                                <td>${contact.group.name}</td>
                            </c:when>
                            <c:otherwise>
                                <td>Default</td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </table>

            <nav aria-label="Page navigation">
                <ul class="pagination">
                    <c:forEach var="i" begin="1" end="${pages}">
                        <li><a href="/?page=<c:out value="${i - 1}"/>"><c:out value="${i}"/></a></li>
                    </c:forEach>
                </ul>
            </nav>
        </div>

        <script>
            $('.dropdown-toggle').dropdown();

            $('#add_contact').click(function(){
                window.location.href='/contact_add_page';
            });

            $('#add_group').click(function(){
                window.location.href='/group_add_page';
            });

            $('#delete_contact').click(function(){
                var data = { 'toDelete[]' : []};
                $(":checked").each(function() {
                    data['toDelete[]'].push($(this).val());
                });
                $.post("/contact/delete", data, function(data, status) {
                    window.location.reload();
                });
            });
        </script>
    </body>
</html>