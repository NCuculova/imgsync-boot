<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="en">

<jsp:include page="fragments/head.jsp"/>
 <body>
  <jsp:include page="fragments/menu.jsp"/>
  <div class="main container-fluid">
    <div class="page-header">
        <h3>${user.email}</h3>
    </div>
    <div class="col-md-12">
        <c:forEach var="album" items="${albums}" varStatus="loop">
            <h4>${album.name}</h4>
        </c:forEach>
    </div>
  </div>
 <jsp:include page="fragments/footer.jsp"/>
 </body>
</html>