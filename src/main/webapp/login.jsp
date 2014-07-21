<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="shortcut icon" href="../../assets/ico/favicon.ico">

<title>Signin Template for Bootstrap</title>

<!-- Bootstrap core CSS -->
<link type="text/css" rel="stylesheet"
	href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.0/css/bootstrap.css" />

<!-- Custom styles for this template -->
<link type="text/css" rel="stylesheet"
	href="http://getbootstrap.com/examples/signin/signin.css" />

<!-- Just for debugging purposes. Don't actually copy this line! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>

	<div class="container">
		
<%-- 		<form class="form-signin" role="form"action="<c:url value="/login.do"/>" method="post"> --%>
		<form class="form-signin" role="form" action="/nextprot-api/login.do" method="post">
			<img class="nomargin padding-bottom" src="http://nextprot.org/db/images/blueflat/np.png">
			<h2 class="form-signin-heading">Please sign in</h2>
			<!--         <input type="email" class="form-control" placeholder="Email address" required autofocus> -->
			<input name="j_username" type="text" class="form-control"
				placeholder="Username" required autofocus value="mario"> <input
				name="j_password" type="password" class="form-control"
				placeholder="Password" required value="123"> <label
				class="checkbox"> <input type="checkbox" value="remember-me">
				Remember me
			</label>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Sign
				in</button>
		</form>

	</div>
	<!-- /container -->


	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
</body>
</html>
