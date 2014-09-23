<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<html>
<head>
<meta charset="utf-8">
<title>neXtProt REST API</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/handlebars-1.0.0.beta.6.js"></script>
<script type="text/javascript" src="js/jlinq.js"></script>
<script type="text/javascript" src="js/prettify.js"></script>
<script src="js/bootstrap-button.js"></script>
<script src="js/nx-api.js"></script>

<script src="//cdn.auth0.com/w2/auth0-widget-5.2.9.min.js"></script>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

<!-- Le styles -->
<link href="css/nx-api.css" rel="stylesheet">
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet">
<link href="css/nx-api.css" rel="stylesheet">
<link href="css/bootstrap-responsive.min.css" rel="stylesheet">


<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

</head>

<body class="profile-<% out.print(System.getProperty("spring.profiles.active")); %>">

	<div class="navbar navbar-fixed-top navbar-inverse">
		<div class="navbar-inner">
			<div class="container-fluid profile-header">
				<a class="brand" href="#">neXtProt API</a> 
				<a class="btn btn-primary btn-small btn-login pull-right">SignIn</a> 
				<a class="btn btn-primary btn-small btn-logout pull-right">Logout</a>
				<div align="center">
					<span class="nickname"></span>
					<span class="devenv"></span>
				</div>
			</div>
		</div>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">

			<div class="span2">
				<div id="maindiv" style="display: none;"></div>
				<div class="well sidebar-nav" id="apidiv" style="display: none;"></div>
				<div class="well sidebar-nav" id="xmlschemadiv" style>
					<ul class="nav nav-list">
						<li class="nav-header">XML Schema</li>
						<li><a href="http://crick:8080/nextprotExport.xsd"
							target="_blank">nextprotExport.xsd</a></li>
					</ul>
				</div>
				<div class="well sidebar-nav" id="rdfschemadiv" style>
					<ul class="nav nav-list">
						<li class="nav-header">RDF namespaces</li>
						<li><a href="http://localhost:8080/rdfNamespaces.ttl"
							target="_blank">rdfNamespaces.ttl</a></li>
					</ul>
				</div>
				<div class="well sidebar-nav" id="objectdiv" style="display: none;"></div>
			</div>

			<div class="span4">
				<div id="content"></div>
			</div>

			<div class="span6">
				<div id="testContent"></div>
			</div>
		</div>
	</div>

	<script id="main" type="text/x-handlebars-template">
<blockquote>
  <p style="text-transform: uppercase;">API info</span></p>
  <small>Version: {{version}}</small>
</blockquote>
</script>

	<script id="apis" type="text/x-handlebars-template">
<ul class="nav nav-list">
	<li class="nav-header">APIs</li>
	{{#apis}}
		<li><a href="#" id="{{jsondocId}}" rel="api">{{name}} <span class="role">{{role}}</span></a></li>
	{{/apis}}
</ul>
</script>

	<script id="objects" type="text/x-handlebars-template">
<ul class="nav nav-list">
	<li class="nav-header">Objects</li>
	{{#objects}}
		<li><a href="#" id="{{jsondocId}}" rel="object">{{name}}</a></li>
	{{/objects}}
</ul>
</script>

	<script id="methods" type="text/x-handlebars-template">
<blockquote>
  <p style="text-transform: uppercase;"><span id="apiName"></span></p>
  <small><span id="apiDescription"></span></cite></small>
</blockquote>

<div class="accordion" id="accordion">
	{{#methods}}
	<div class="accordion-group">
		<div class="accordion-heading">
			<span class="label pull-right {{verb}}" style="margin-right: 5px; margin-top: 8px;">{{verb}}</span>
			<a href="#_{{jsondocId}}" id="{{jsondocId}}" rel="method" data-parent="#accordion" data-toggle="collapse" class="accordion-toggle">{{path}}</a>
		</div>
		<div class="accordion-body collapse" id="_{{jsondocId}}">
			<div class="accordion-inner">
				<table class="table table-condensed table-striped table-bordered">
					<tr>
						<th style="width:15%;">Path</th>
						<td><code>{{path}}</code></td>
					</tr>
					<tr>
						<th>Description</th>
						<td>{{description}}</td>
					</tr>
					<tr>
						<th>Method</th>
						<td><span class="label {{verb}}">{{verb}}</span></td>
					</tr>
					{{#if produces}}
						<tr>
							<th colspan=2>Produces</th>
						</tr>
						{{#each produces}}
							<tr>
								<td colspan=2><code>{{this}}</code></td>
							</tr>
						{{/each}}
					{{/if}}
					{{#if consumes}}
						<tr>
							<th colspan=2>Consumes</th>
						</tr>
						{{#each consumes}}
							<tr>
								<td colspan=2><code>{{this}}</code></td>
							</tr>
						{{/each}}
					{{/if}}
					{{#if headers}}
						<tr>
							<th colspan=2>Headers</th>
						</tr>
						{{#each headers}}
							<tr>
								<td><code>{{this.name}}</code></td>
								<td>{{this.description}}</td>
							</tr>
						{{/each}}
					{{/if}}
					{{#if pathparameters}}
						<tr>
							<th colspan=2>Path parameters</th>
						</tr>
						{{#each pathparameters}}
							<tr>
								<td><code>{{this.name}}</code></td>
								<td>Required: {{this.required}}</td>
								
							</tr>
							<tr>
								<td></td>
								<td>Type: {{this.type}}</td>
							</tr>
							{{#if this.description}}
							<tr>
								<td></td>
								<td>Description: {{this.description}}</td>
							</tr>
							{{/if}}
							{{#if this.allowedvalues}}
							<tr>
								<td></td>
								<td>Allowed values: {{this.allowedvalues}}</td>
							</tr>
							{{/if}}
							{{#if this.format}}
							<tr>
								<td></td>
								<td>Format: {{this.format}}</td>
							</tr>
							{{/if}}
						{{/each}}
					{{/if}}
					{{#if queryparameters}}
						<tr>
							<th colspan=2>Query parameters</th>
						</tr>
						{{#each queryparameters}}
							<tr>
								<td><code>{{this.name}}</code></td>
								<td>Required: {{this.required}}</td>
								
							</tr>
							<tr>
								<td></td>
								<td>Type: {{this.type}}</td>
							</tr>
							{{#if this.description}}
							<tr>
								<td></td>
								<td>Description: {{this.description}}</td>
							</tr>
							{{/if}}
							{{#if this.allowedvalues}}
							<tr>
								<td></td>
								<td>Allowed values: {{this.allowedvalues}}</td>
							</tr>
							{{/if}}
							{{#if this.format}}
							<tr>
								<td></td>
								<td>Format: {{this.format}}</td>
							</tr>
							{{/if}}
						{{/each}}
					{{/if}}
					{{#if bodyobject}}
						<tr>
							<th colspan=2>Body object</th>
						</tr>
						<tr>
							<td>Object</td>
							<td><code>{{bodyobject.object}}</code></td>
						</tr>
						<tr>
							<td>Multiple</td>
							<td>{{bodyobject.multiple}}</td>
						</tr>
						{{#if bodyobject.map}}
							<tr>
								<td>Map key</td>
								<td><code>{{bodyobject.mapKeyObject}}</code></td>
							</tr>
							<tr>
								<td>Map value</td>
								<td><code>{{bodyobject.mapValueObject}}</code></td>
							</tr>
						{{/if}}
					{{/if}}
					{{#if response}}
						<tr>
							<th colspan=2>Response object</th>
						</tr>
						<tr>
							<td>Object</td>
							<td><code>{{response.object}}</code></td>
						</tr>
						<tr>
							<td>Multiple</td>
							<td>{{response.multiple}}</td>
						</tr>
						{{#if response.map}}
							<tr>
								<td>Map key</td>
								<td><code>{{response.mapKeyObject}}</code></td>
							</tr>
							<tr>
								<td>Map value</td>
								<td><code>{{response.mapValueObject}}</code></td>
							</tr>
						{{/if}}
					{{/if}}
					{{#if apierrors}}
						<tr>
							<th colspan=2>Errors</th>
						</tr>
						{{#each apierrors}}
							<tr>
								<td><code>{{this.code}}</code></td>
								<td>{{this.description}}</td>
							</tr>
						{{/each}}
					{{/if}}
				</table>
			</div>
		</div>
	</div>
	{{/methods}}
</div>

</script>

	<script id="test" type="text/x-handlebars-template">
<blockquote>
  <p style="text-transform: uppercase;">Playground</span></p>
  <small>{{path}}</small>
</blockquote>

<div class="row-fluid">

	{{#if headers}}	
	<div class="span12">
		<div id="headers">
			<h4>Headers</h4>
			{{#headers}}
				<div class="input-prepend">
					<span style="text-align:left;" class="add-on span4">{{name}}</span><input type="text" class="span8" name="{{name}}" placeholder="{{name}}">
				</div>
			{{/headers}}
		</div>
	</div>
	{{/if}}

	{{#if produces}}
		<div class="span6" style="margin-left:0px">		
		<div id="produces" class="playground-spacer">
		<h4>Accept</h4>	
		{{#produces}}
			<label class="radio"><input type="radio" name="produces" value="{{this}}">{{this}}</label>
		{{/produces}}
		</div>
		</div>
	{{/if}}

	{{#if bodyobject}}
	{{#if consumes}}
		<div class="span6" style="margin-left:0px">		
		<div id="consumes" class="playground-spacer">
		<h4>Content type</h4>	
		{{#consumes}}
			<label class="radio"><input type="radio" name="consumes" value="{{this}}">{{this}}</label>
		{{/consumes}}
		</div>
		</div>
	{{/if}}
	{{/if}}

	{{#if pathparameters}}
	<div class="span12" style="margin-left:0px">
		<div id="pathparameters" class="playground-spacer">
			<h4>Path parameters</h4>
			{{#pathparameters}}
				<div class="input-prepend">
					<span style="text-align:left;" class="add-on span4">{{name}}</span><input type="text" class="span8" name="{{name}}" placeholder="{{name}}">
				</div>
			{{/pathparameters}}
		</div>
	</div>
	{{/if}}

	{{#if queryparameters}}
	<div class="span12" style="margin-left:0px">
		<div id="queryparameters" class="playground-spacer">
			<h4>Query parameters</h4>
			{{#queryparameters}}
				<div class="input-prepend">
					<span style="text-align:left;" class="add-on span4">{{name}}</span><input type="text" class="span8" name="{{name}}" value="{{this.allowedvalues}}" placeholder="{{name}}">
				</div>
			{{/queryparameters}}
		</div>
	</div>
	{{/if}}

	{{#if bodyobject}}
	<div class="span12" style="margin-left:0px">
		<div id="bodyobject" class="playground-spacer">
			<h4>Body object</h4>
			<textarea class="span12" id="inputJson" rows=10 />
		</div>
	</div>
	{{/if}}

	<div class="span12" style="margin-left:0px">
		<div class="form-actions">
			<button class="btn btn-primary" id="testButton" data-loading-text="Loading...">Submit</button>
		</div>
	</div>

</div>

<div class="tabbable" id="resInfo" style="display:none;">
	<ul class="nav nav-tabs">
  		<li class="active"><a href="#tab1" data-toggle="tab">Response text</a></li>
  		<li><a href="#tab2" data-toggle="tab">Response info</a></li>
  		<li><a href="#tab3" data-toggle="tab">Request info</a></li>
	</ul>
	<div class="tab-content">
    	<div class="tab-pane active" id="tab1">
    		<pre id="response">
			</pre>
   		</div>
    	<div class="tab-pane" id="tab2">
			<p class="nav-header" style="padding:0px">Response code</p>
      		<pre id="responseStatus">
			</pre>
			<p class="nav-header" style="padding:0px">Response headers</p>
      		<pre id="responseHeaders">
			</pre>
    	</div>
		<div class="tab-pane" id="tab3">
      		<p class="nav-header" style="padding:0px">Request URL</p>
      		<pre id="requestURL">
			</pre>
    	</div>
	</div>
</div>

</script>

	<script id="object" type="text/x-handlebars-template">
<table class=" table-condensed table-striped table-bordered">
	<tr><th style="width:15%;">Name</th><td><code>{{name}}</code></td></tr>
	{{#if description}}
		<tr><th>Description</th><td>{{description}}</td></tr>
	{{/if}}
	{{#if fields}}
	<tr><th colspan=2>Fields</th></tr>
		{{#each fields}}
			<tr><td><code>{{name}}</code></td><td>{{description}}</td></tr>
			<tr><td></td><td>Type: {{type}}</td></tr>
			<tr><td></td><td>Multiple: {{multiple}}</td></tr>
			{{#if map}}
				{{#if this.mapKeyObject}}
				<tr>	
					<td></td>
					<td>Map key: {{this.mapKeyObject}}</td>
				</tr>
				<tr>
					<td></td>
					<td>Map value: {{this.mapValueObject}}</td>
				</tr>
				{{/if}}
			{{/if}}
		{{/each}}
	{{/if}}
</table>
</script>
</body>
</html>