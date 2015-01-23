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
			<h1>Register Client</h1>
			
			
			<form action="register" method="post" class="form-horizontal col-md-4" role="form">
			
				<div class="form-group">
					<label for="inputId">Id</label>
					<input type="text" class="form-control" id="inputId" name="client_id" placeholder="Id"/>
				</div>
				
				<div class="form-group">
					<label for="inputSecret">Secret</label>
					<input type="text" class="form-control" id="inputSecret" name="client_secret" placeholder="Secret"/>
				</div>
			
				<div class="form-group">
					<label for="inputName">Name</label>
					<input type="text" class="form-control" id="inputName" name="name" placeholder="Name"/>
				</div>
				<div class="form-group">
					<label for="inputDescription">Description</label>
					<input type="text" class="form-control" id="inputDescription" name="description" placeholder="Description"/>
				</div>
				
				<div class="form-group">
					<label for="inputResourceIds">Resource Ids</label>
					<input type="text" class="form-control" id="inputResourceIds" name="resource_ids" placeholder="Resource Ids"/>
				</div>
				
				<div class="form-group">
					<label for="inputScopes">Scopes</label>
					<input type="text" class="form-control" id="inputScopes" name="scopes" placeholder="Scopes"/>
				</div>
				
				<div class="form-group">
					<label for="inputGrantTypes">Grants</label>
					<input type="text" class="form-control" id="inputGrantTypes" name="grant_types" placeholder="Grant Types"/>
				</div>
				
				<div class="form-group">
					<label for="inputAuthorities">Authorities</label>
					<input type="text" class="form-control" id="inputAuthorities" name="authorities" placeholder="Authorities"/>
				</div>
				
				<button class="btn btn-primary" type="submit">Register</button>
				<button class="btn" type="submit">Cancel</button> 
			</form>
		</div>
	</body>

</html>