<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="en">

<jsp:include page="fragments/head.jsp"/>
 <body>
  <jsp:include page="fragments/menu.jsp"/>
  <div class="main container-fluid">
    <div class="page-header">
        <h3>Login</h3>
    </div>

    <div class="col-md-12">
        <form class="form-horizontal" action="/login" method="post">
            <fieldset>
                <div class="form-group">
                    <label for="inputEmail" class="col-lg-2 control-label">Email</label>
                    <div class="col-lg-10">
                        <input type="email" name="username" class="form-control" id="inputEmail" placeholder="Enter your email...">
                    </div>
                </div>
                <div class="form-group">
                    <label for="inputPassword" class="col-lg-2 control-label">Password</label>
                    <div class="col-lg-10">
                        <input type="password" name="password" class="form-control" id="inputPassword" placeholder="Enter your password...">
                    </div>
                </div>
                <input type="submit" class="btn btn-primary" id="submit">
                		<c:if test="${not empty param.authentication_error}">
                			<h1>Woops!</h1>
                			<p class="error">Your login attempt was not successful.</p>
                		</c:if>
            </fieldset>
        </form>
    </div>
  </div>
 <jsp:include page="fragments/footer.jsp"/>
 </body>
</html>