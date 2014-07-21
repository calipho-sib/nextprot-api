<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>neXtProt OAuth</title>
		<link type="text/css" rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.0/css/bootstrap.css"/>
	</head>
	<body role="document">
		<div class="container">
			<h1>Register User</h1>
			
			
			<form action="register" method="post" class="form-horizontal col-md-4" role="form">
			
				<div class="form-group">
					<label for="inputUsername">Username</label>
					<input type="text" class="form-control" id="inputUsername" name="username" placeholder="Username"/>
				</div>
				
				<div class="form-group">
					<label for="inputPassword">Password</label>
					<input type="test" class="form-control" id="inputPassword" name="password" placeholder="Password"/>
				</div>
			
				<button class="btn btn-primary" type="submit">Register</button>
				<button class="btn" type="submit">Cancel</button> 
			</form>
		</div>
	</body>

</html>