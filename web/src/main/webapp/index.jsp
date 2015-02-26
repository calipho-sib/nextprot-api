<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>neXtProt REST API</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<script src="js/jquery-1.11.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/handlebars-1.0.0.beta.6.js"></script>
<script type="text/javascript" src="js/jlinq.js"></script>
<script type="text/javascript" src="js/prettify.js"></script>

<!-- Le styles -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/nx-api.css" rel="stylesheet">

<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
</head>

<body>

	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
			
		    	<a class="navbar-brand" href="#">neXtProt API</a>
    		</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1" ng-controller="HelpCtrl">
				<ul class="nav navbar-nav">

					<!--  Resources  -->
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-expanded="false">Resources
							<span class="caret"></span>
					</a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="https://search.nextprot.org">Search</a></li>
							<li><a href="http://snorql.nextprot.org">Snorql</a></li>
							<li><a href="https://api.nextprot.org">API</a></li>
						</ul></li>

					<!-- Help dropdown -->
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-expanded="false">Help
							<span class="caret"></span>
					</a>
						<ul class="dropdown-menu" role="menu">
							<!-- News -->
							<li class="hide" ng-class="{'active':isActiveDoc('/pages/what-is-new')}"><a
								href="/pages/what-is-new">What's new?</a></li>
							<li class="divider hide"></li>
							<!-- FAQ -->
							<li  class="hide" ng-class="{'active':isActiveDoc('/pages/faq')}"><a
								href="/pages/faq">FAQ</a></li>
							<li class="divider hide"></li>

							<!-- Found a bug -->
							<li ng-class=""><a target="_blank"
								href="https://github.com/calipho-sib/nextprot-api/issues">Found
									a bug?<i class="icon-github"></i>
							</a></li>
						</ul></li>

					<li ng-class=""><a href="https://raw.githubusercontent.com/calipho-sib/nextprot-docs/master/pages/about.md" target="_blank">About</a></li>
					<li ng-class=""><a
						href="mailto:support@nextprot.org?subject=[neXtProt%20Search]">Contact
							us</a></li>

				</ul>
				<ul class="nav navbar-nav navbar-right">

					<!-- login button -->
					<li class="li-login" >
						<a href='' type="button" class="link btn-login">Login</a>
					</li>
					<!-- once logged in user resources -->
					<li class="dropdown li-logout" style="display:none;" >
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<span class="user"></span><span class="caret"></span>
						</a>
						<ul class="dropdown-menu" role="menu">
							<li>
								<a href='' class="btn-logout"">Logout</a>
							</li>
						</ul></li>
				</ul>
			</div>

		</div>
	</nav>

	<div class="container-fluid">
		<div class="row">
			
			<div class="col-md-2">
				<div id="maindiv" style="display:none;"></div>
				
				<div class="panel-group" id="side-accordion" aria-multiselectable="true" style="display: none;">
					
					<div class="panel panel-default">
						<div class="panel-heading" id="panel-apis">
							<h4 class="panel-title">
								<a id="panel-apis" href="#_panel-apis" data-toggle="collapse" data-parent="#side-accordion" aria-controls="_panel-apis" aria-expanded="true">API</a>
							</h4>
						</div>
						<div id="_panel-apis" class="panel-collapse collapse in" aria-labelledby="panel-apis">
							<div class="panel-body">
								<div id="apidiv"></div>
							</div>
						</div>
					</div>
					
					<div class="panel panel-default hide">
						<div class="panel-heading" id="panel-objects">
							<h4 class="panel-title">
								<a id="panel-objects" href="#_panel-objects" data-toggle="collapse" data-parent="#side-accordion" aria-controls="_panel-objects" aria-expanded="true">Objects</a>
							</h4>
						</div>
						<div id="_panel-objects" class="panel-collapse collapse" aria-labelledby="panel-objects">
							<div class="panel-body">
								<div id="objectdiv"></div>
							</div>
						</div>
					</div>
					
					<div class="panel panel-default hide">
						<div class="panel-heading" id="panel-flows">
							<h4 class="panel-title">
								<a id="panel-flows" href="#_panel-flows" data-toggle="collapse" data-parent="#side-accordion" aria-controls="_panel-flows" aria-expanded="true">Flows</a>
							</h4>
						</div>
						<div id="_panel-flows" class="panel-collapse collapse" aria-labelledby="panel-flows">
							<div class="panel-body">
								<div id="flowdiv"></div>
							</div>
						</div>
					</div>
					
				</div>
				
			</div>

			<div class="col-md-4">
				<div id="content"></div>			
			</div>
			
			<div class="col-md-6">
				<div id="testContent"></div>			
			</div>
		</div>
	</div>

	<!-- Footer
  ================================================== -->
	<footer class="container text-center small">
		<hr />
		<div class="row">
			<div class="col-lg-12">
				<div class="col-md-4">
					<ul class="nav nav-pills nav-stacked">
						<li><a href="https://raw.githubusercontent.com/calipho-sib/nextprot-docs/master/pages/legal%20disclaimer.md" target="_blank">Legal disclaimer</a></li>
					</ul>
				</div>
				<div class="col-md-4">
					<ul class="nav nav-pills nav-stacked">
						<li><a href="https://raw.githubusercontent.com/calipho-sib/nextprot-docs/master/pages/copyright.md" target="_blank">&copy; 2015 SIB</a></li>
					</ul>
				</div>
				<div class="col-md-4">
					<ul class="nav nav-pills nav-stacked">
						<li><a href="https://github.com/calipho-sib/nextprot-api"
							target="_blank"><i class="icon-github"></i>For developers</a></li>
						</a>
						</li>
					</ul>
				</div>
				<div class="col-md-3">
					<ul class="nav nav-pills nav-stacked">
					</ul>
				</div>
			</div>
		</div>
		<hr />
	</footer>


	<script id="main" type="text/x-handlebars-template">
<blockquote>
  <p style="text-transform: uppercase;">API info</span></p>
  <small>Version: {{version}}</small>
</blockquote>
</script>

<script id="apis" type="text/x-handlebars-template">
{{#eachInMap apis}}
	<ul class="list-unstyled">
		{{#if key}}
		<li style="text-transform: uppercase;">{{key}}</li>
		{{/if}}
		{{#each value}}
			<li><a href="#" id="{{jsondocId}}" rel="api">{{name}}</a></li>
		{{/each}}
	</ul>
{{/eachInMap}}	
</script>

<script id="objects" type="text/x-handlebars-template">
{{#eachInMap objects}}
	<ul class="list-unstyled">
		{{#if key}}
		<li style="text-transform: uppercase;">{{key}}</li>
		{{/if}}
		{{#each value}}
			<li><a href="#" id="{{jsondocId}}" rel="object">{{name}}</a></li>
		{{/each}}
	</ul>
{{/eachInMap}}
</script>

<script id="flows" type="text/x-handlebars-template">
{{#eachInMap flows}}
	<ul class="list-unstyled">
		{{#if key}}
		<li style="text-transform: uppercase;">{{key}}</li>
		{{/if}}
		{{#each value}}
			<li><a href="#" id="{{jsondocId}}" rel="flow">{{name}}</a></li>
		{{/each}}
	</ul>
{{/eachInMap}}
</script>

<script id="methods" type="text/x-handlebars-template">
<blockquote>
  <p style="text-transform: uppercase;"><span id="apiName">{{name}}</span></p>
  <small><span id="apiDescription">{{description}}</span></small>
  <small><span id="apiSupportedVersions"></span></small>
</blockquote>

{{#if preconditions}}
	<div class="alert alert-info border-radius-none">
		<p><strong>Preconditions: </strong></p>
		<ul class="list-unstyled">
		{{#each preconditions}}
			<li>{{this}}</li>
		{{/each}}
		</ul>
	</div>
{{/if}}

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
	{{#methods}}
	<div class="panel panel-default">
		<div class="panel-heading" id="{{jsondocId}}">
			<h4 class="panel-title">
				<span class="label pull-right {{verb}}">{{verb}}</span>
				<a id="{{jsondocId}}" href="#_{{jsondocId}}" rel="method" data-toggle="collapse" data-parent="#accordion" aria-controls="_{{jsondocId}}" aria-expanded="true">{{path}}</a>
			</h4>
		</div>
		<div id="_{{jsondocId}}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="{{jsondocId}}">
			<div class="panel-body">
				{{#if jsondocerrors}}
					<div class="alert alert-danger border-radius-none">
						<p><strong>The following errors prevent a correct functionality of the playground and do not provide enough documentation data for API users:</strong></p>
						<ul class="list-unstyled">
						{{#each jsondocerrors}} <li>- {{this}}</li> {{/each}}
						</ul>
					</div>
				{{/if}}
				<table class="table table-condensed table-bordered">
					<tr>
						<th>Path</th>
						<td><code>{{path}}</code></td>
					</tr>
					{{#if supportedversions}}
						<tr>
							<td>Since version</td>
							<td>{{supportedversions.since}}</td>
						</tr>
						{{#if supportedversions.until}}
							<tr>
								<td>Until version</td>
								<td>{{supportedversions.until}}</td>
							</tr>
						{{/if}}	
					{{/if}}
					
					{{#if description}}
					<tr>
						<th>Description</th>
						<td>{{description}}</td>
					</tr>
					{{/if}}

					{{#if auth}}
						<tr>
							<th>Auth</th>
							<td>{{auth.type}}, Roles: {{auth.roles}}</td>
						</tr>
					{{/if}}

					{{#if produces}}
						<tr>
							<th colspan=2>Produces</th>
						</tr>
						<tr>
							<td colspan=2>
								{{#each produces}} <code>{{this}}</code> {{/each}}
							</td>
						</tr>
					{{/if}}
					{{#if consumes}}
						<tr>
							<th colspan=2>Consumes</th>
						</tr>
						<tr>
							<td colspan=2>
								{{#each consumes}} <code>{{this}}</code> {{/each}}
							</td>
						</tr>
					{{/if}}
					{{#if headers}}
						<tr>
							<th colspan=2>Headers</th>
						</tr>
						{{#each headers}}
							{{#if this.description}}
							<tr>
								<td><code>{{this.name}}</code></td>
								<td>{{this.description}}</td>
							</tr>
							{{/if}}
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
							{{#if this.description}}
							<tr>
								<td></td>
								<td>Description: {{this.description}}</td>
							</tr>
							{{/if}}
							<tr>
								<td></td>
								<td>Type: <code>{{this.jsondocType.oneLineText}}</code></td>
							</tr>
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
							{{#if this.description}}
							<tr>
								<td></td>
								<td>Description: {{this.description}}</td>
							</tr>
							{{/if}}
							<tr>
								<td></td>
								<td>Type: <code>{{this.jsondocType.oneLineText}}</code></td>
							</tr>
							{{#if this.format}}
							<tr>
								<td></td>
								<td>Format: {{this.format}}</td>
							</tr>
							{{/if}}
							{{#if this.defaultvalue}}
							<tr>
								<td></td>
								<td>Default value: {{this.defaultvalue}}</td>
							</tr>
							{{/if}}
						{{/each}}
					{{/if}}
					{{#if bodyobject}}
						<tr>
							<th colspan=2>Body object</th>
						</tr>
						<tr>
							<td colspan=2><code>{{bodyobject.jsondocType.oneLineText}}</code></td>
						</tr>
					{{/if}}
					{{#if responsestatuscode}}
						<tr>
							<th colspan=2>Response status code</th>
						</tr>
						<tr>
							<td colspan=2><code>{{responsestatuscode}}</code></td>
						</tr>
					{{/if}}
					{{#if response}}
						<tr>
							<th colspan=2>Response object</th>
						</tr>
						<tr>
							<td colspan=2><code>{{response.jsondocType.oneLineText}}</code></td>
						</tr>
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
				{{#if jsondocwarnings}}
					<div class="alert alert-warning alert-dismissible border-radius-none">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<p><strong>Warnings that may prevent a correct playground functionality:</strong></p>
						<ul class="list-unstyled">
						{{#each jsondocwarnings}} <li>- {{this}}</li> {{/each}}
						</ul>
					</div>
				{{/if}}
				{{#if jsondochints}}
					<div class="alert alert-info alert-dismissible border-radius-none">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<p><strong>Hints to provide a better understanding of your API:</strong></p>
						<ul class="list-unstyled">
						{{#each jsondochints}} <li>- {{this}}</li> {{/each}}
						</ul>
					</div>
				{{/if}}
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

<div class="row">
	{{#if auth}}
		{{#equal auth.type "BASIC_AUTH"}}
			<div class="col-md-12">
			</div>
		{{/equal}}
	{{/if}}

	{{#if headers}}
	<div class="col-md-12">
		<div id="headers">
			<h4>Headers</h4>
			{{#headers}}
				<div class="form-group">
					<label for="i_{{name}}">{{name}}</label>					
					{{#compare allowedvalues.length 1 operator="=="}}
						<input type="text" class="form-control" name="{{name}}" placeholder="{{name}}" value="{{allowedvalues}}">
					{{/compare}}
					{{#compare allowedvalues.length 1 operator="!="}}
						<input type="text" class="form-control" name="{{name}}" placeholder="{{name}}">
					{{/compare}}
				</div>
			{{/headers}}
		</div>
	</div>
	{{/if}}

	{{#if produces}}
		<div class="col-md-6" style="margin-left:0px">		
		<div id="produces" class="playground-spacer">
		<h4>Accept</h4>	
		{{#produces}}
			<label><input type="radio" name="produces" value="{{this}}"> {{this}}</label><br/>
		{{/produces}}
		</div>
		</div>
	{{/if}}

	{{#if bodyobject}}
	{{#if consumes}}
		<div class="col-md-6" style="margin-left:0px">		
		<div id="consumes" class="playground-spacer">
		<h4>Content type</h4>	
		{{#consumes}}
			<label><input type="radio" name="consumes" value="{{this}}"> {{this}}</label>
		{{/consumes}}
		</div>
		</div>
	{{/if}}
	{{/if}}

	{{#if pathparameters}}
	<div class="col-md-12">
		<div id="pathparameters" class="playground-spacer">
			<h4>Path parameters</h4>
			<div id="pathparametererrors" class="alert alert-danger" style="display:none">
				<strong>Validation errors:</strong>
				<ul class="list-unstyled"></ul>
			</div>
			{{#pathparameters}}
				<div class="form-group">
					<label class="control-label" for="i_{{name}}">{{name}}</label>
					<input type="text" class="form-control" id="i_{{name}}" name="{{name}}" placeholder="{{name}}" value="{{allowedvalues}}">
				</div>
			{{/pathparameters}}
		</div>
	</div>
	{{/if}}

	{{#if queryparameters}}
	<div class="col-md-12">
		<div id="queryparameters" class="playground-spacer">
			<h4>Query parameters</h4>
			{{#queryparameters}}
				<div class="form-group">
					<label for="i_{{name}}">{{name}}</label>
					<input type="text" class="form-control" id="i_{{name}}" name="{{name}}" placeholder="{{name}}">
				</div>
			{{/queryparameters}}
		</div>
	</div>
	{{/if}}

	{{#if bodyobject}}
	<div class="col-md-12">
		<div id="bodyobject" class="playground-spacer">
			<h4>Body object</h4>
			<textarea class="form-control" id="inputJson" rows=10 />
		</div>
	</div>
	{{/if}}

	<div class="col-md-12 playground-spacer">
		<button class="btn btn-primary col-md-12" id="testButton" data-loading-text="Loading...">Submit</button>
	</div>
</div>
</div>

<div class="row">
<div class="col-md-12">
<div class="tabbable" id="resInfo" style="display:none; margin-top: 20px;">
	<ul class="nav nav-tabs">
  		<li class="active"><a href="#tab1" data-toggle="tab">Response text</a></li>
  		<li><a href="#tab2" data-toggle="tab">Response info</a></li>
  		<li><a href="#tab3" data-toggle="tab">Request info</a></li>
	</ul>
	<div class="tab-content" style="margin-top: 20px">
    	<div class="tab-pane active" id="tab1">
    		<pre id="response" class="prettyprint">
			</pre>
   		</div>
    	<div class="tab-pane" id="tab2">
			<h5 style="padding:0px">Response code</p>
      		<pre id="responseStatus" class="prettyprint">
			</pre>
			<h5 style="padding:0px">Response headers</p>
      		<pre id="responseHeaders" class="prettyprint">
			</pre>
    	</div>
		<div class="tab-pane" id="tab3">
      		<h5 style="padding:0px">Request URL</p>
      		<pre id="requestURL" class="prettyprint">
			</pre>
    	</div>
	</div>
</div>
</div>

</script>

<script id="object" type="text/x-handlebars-template">
<table class="table table-condensed table-striped table-bordered" style="table-layout: fixed;">
	<tr><th style="width:18%;">Name</th><td><code>{{name}}</code></td></tr>
	{{#if description}}
		<tr><th>Description</th><td>{{description}}</td></tr>
	{{/if}}
	{{#if supportedversions}}
		<tr>
			<td>Since version</td>
			<td>{{supportedversions.since}}</td>
		</tr>
		{{#if supportedversions.until}}
			<tr>
				<td>Until version</td>
				<td>{{supportedversions.until}}</td>	
			</tr>
		{{/if}}	
	{{/if}}
	{{#if allowedvalues}}
		<tr><td></td><td>Allowed values: {{allowedvalues}}</td></tr>
	{{/if}}
	{{#if fields}}
	<tr><th colspan=2>Fields</th></tr>
		{{#each fields}}
			<tr><td><code>{{name}}</code></td><td>{{description}}</td></tr>
			<tr><td></td><td>Required: {{required}}</td></tr>
			<tr><td></td><td>Type: <code>{{jsondocType.oneLineText}}</code></td></tr>
			{{#if format}}
				<tr><td></td><td>Format: {{format}}</td></tr>
			{{/if}}
			{{#if allowedvalues}}
				<tr><td></td><td>Allowed values: {{allowedvalues}}</td></tr>
			{{/if}}
			{{#if supportedversions}}
				<tr><td></td><td>Since version {{supportedversions.since}}</td></tr>
				{{#if supportedversions.until}}
					<tr><td></td><td>Until version {{supportedversions.until}}</td></tr>
				{{/if}}
			{{/if}}
		{{/each}}
	{{/if}}
</table>
{{#if jsondochints}}
	<div class="alert alert-info alert-dismissible border-radius-none">
		<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		<p><strong>Hints to provide a better understanding of your API:</strong></p>
		<ul class="list-unstyled">
		{{#each jsondochints}} <li>- {{this}}</li> {{/each}}
		</ul>
	</div>
{{/if}}
</script>

<script id="objecttemplate" type="text/x-handlebars-template">
<pre class="prettyprint">
{{#json jsondocTemplate}}
{{/json}}
</pre>
</script>

<script>
	var model;
	var jsondoc = JSON.stringify('_JSONDOC_OFFLINE_PLACEHOLDER_');
	
	$(document).ready(function() {
		// This is to check if the '_JSONDOC_OFFLINE_PLACEHOLDER_' has been replaced with content coming from
		// the result of the jsondoc-maven-plugin
		if(jsondoc.split('_').length != 5) {
			$("#jsondocfetch").hide();
			$("#getDocButton").hide();
			buildFromJSONDoc($.parseJSON(jsondoc));
		}
	});
	
	Handlebars.registerHelper('compare', function(lvalue, rvalue, options) {
	    if (arguments.length < 3)
	        throw new Error("Handlerbars Helper 'compare' needs 2 parameters");

	    operator = options.hash.operator || "==";

	    var operators = {
	        '==':       function(l,r) { return l == r; },
	        '===':      function(l,r) { return l === r; },
	        '!=':       function(l,r) { return l != r; },
	        '<':        function(l,r) { return l < r; },
	        '>':        function(l,r) { return l > r; },
	        '<=':       function(l,r) { return l <= r; },
	        '>=':       function(l,r) { return l >= r; },
	        'typeof':   function(l,r) { return typeof l == r; }
	    }

	    if (!operators[operator])
	        throw new Error("Handlerbars Helper 'compare' doesn't know the operator "+operator);

	    var result = operators[operator](lvalue,rvalue);

	    if( result ) {
	        return options.fn(this);
	    } else {
	        return options.inverse(this);
	    }

	});

	Handlebars.registerHelper('equal', function(lvalue, rvalue, options) {
		if(lvalue!=rvalue) {
			return options.inverse(this);
		} else {
			return options.fn(this);
		}
	});

	Handlebars.registerHelper('eachInMap', function ( map, block ) {
		var out = '';
		Object.keys( map ).map(function( prop ) {
			out += block.fn( {key: prop, value: map[ prop ]} );
		});
		return out;
	} );
	
	function replacer(key, value) {
	    if (value == null) return undefined;
	    else return value;
	}
	
	Handlebars.registerHelper('json', function(context) {
	    return JSON.stringify(context, replacer, 2);
	});
	
	function checkURLExistence() {
		fetchdoc(window.location.href.replace("#", "") + '/jsondoc');
	}
	
	$("#jsondocfetch").keypress(function(event) {
		if (event.which == 13) {
			checkURLExistence();
			return false;
		}
	});
	
	$("#getDocButton").click(function() {
		checkURLExistence();
		return false;
	});

	function fillBasicAuthFields() {
		$("#basicAuthPassword").val($("#basicAuthSelect").val());
		$("#basicAuthUsername").val($("#basicAuthSelect").find(":selected").text());
	}
	
	function printResponse(data, res, url) {
		if(res.responseXML != null) {
			$("#response").text(formatXML(res.responseText));
		} else if (url.endsWith("ttl")) {
			$("#response").text(res.responseText);
		} else {
			$("#response").text(JSON.stringify(data, undefined, 2));
		}
		
		$("#responseStatus").text(res.status);
		$("#responseHeaders").text(res.getAllResponseHeaders());
		$("#requestURL").html("<a href='" + url + "' target='_blank'>" + url + "</a>");
		$('#testButton').button('reset');
		$("#resInfo").show();
	}
	
	function formatXML(xml) {
	    var formatted = '';
	    var reg = /(>)(<)(\/*)/g;
	    xml = xml.replace(reg, '$1\r\n$2$3');
	    var pad = 0;
	    jQuery.each(xml.split('\r\n'), function(index, node) {
	        var indent = 0;
	        if (node.match( /.+<\/\w[^>]*>$/ )) {
	            indent = 0;
	        } else if (node.match( /^<\/\w/ )) {
	            if (pad != 0) {
	                pad -= 1;
	            }
	        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
	            indent = 1;
	        } else {
	            indent = 0;
	        }

	        var padding = '';
	        for (var i = 0; i < pad; i++) {
	            padding += '  ';
	        }

	        formatted += padding + node + '\r\n';
	        pad += indent;
	    });

	    return formatted;
	}
	
	function buildMethodsContent(items) {
		$('#content a[rel="method"]').each(function() {
			$(this).click(function() {
				var method = jlinq.from(items).equals("jsondocId", this.id).first();
				var test = Handlebars.compile($("#test").html());
				var testHTML = test(method);
				$("#testContent").html(testHTML);
				$("#testContent").show();

				// if bodyobject is not empty then put jsondocTemplate into textarea
				if(method.bodyobject) {
					$("#inputJson").text(JSON.stringify(method.bodyobject.jsondocTemplate, undefined, 2));	
				}
				
				$("#produces input:first").attr("checked", "checked");
				$("#consumes input:first").attr("checked", "checked");
				
				$("#testButton").click(function() {
					var headers = new Object();
					$("#headers input").each(function() {
						headers[this.name] = $(this).val();
					});
					
					headers["Accept"] = $("#produces input:checked").val();

					if(method.auth) {
						if(method.auth.type == "BASIC_AUTH") {
							headers["Authorization"] = "Basic " + window.btoa($('#basicAuthUsername').val() + ":" + $('#basicAuthPassword').val());
						}
					}
					
					var replacedPath = method.path;
					var tempReplacedPath = replacedPath; // this is to handle more than one parameter on the url
					
					var validationErrors = [];
					$('#pathparametererrors').hide();
					$('#pathparametererrors ul').empty();
					
					$("#pathparameters input").each(function() {
						$('#' + this.id).parent().removeClass('has-error');
						
						if($(this).val()) {
							tempReplacedPath = replacedPath.replace("{"+this.name+"}", $(this).val());
							replacedPath = tempReplacedPath;	
						} else {
							validationErrors.push(this.name + ' must not be empty');
							$('#' + this.id).parent().addClass('has-error');
						}
					});
					
					if(validationErrors.length > 0) {
						for (var k=0; k<validationErrors.length; k++) {
							$('#pathparametererrors ul').append($('<li/>').text(validationErrors[k]));
							
						}
						$('#pathparametererrors').show();
						validationErrors = [];
						return;
					}

					$("#queryparameters input").each(function() {
						tempReplacedPath = replacedPath.replace("{"+this.name+"}", $(this).val());
						replacedPath = tempReplacedPath;
					});
					
					$('#testButton').button('loading');
					
					var suffix = "xml";
					if (headers["Accept"] == "application/json")
						suffix = "json";
					if (headers["Accept"] == "text/turtle")
						suffix = "ttl";
					
					var res = $.ajax({
						url : window.location.href.replace("#", "") + replacedPath + "." + suffix,
						type: method.verb,
						data: $("#inputJson").val(),
						headers: headers,
						contentType: $("#consumes input:checked").val(),
						success : function(data) {
							printResponse(data, res, this.url);
						},
						error: function(data) {
							printResponse(data, res, this.url);
							
							var errorMsg;
							if (res.status == 0) {
								errorMsg="The API is not accessible";
							} else if (res.status == 401 || (status == 403)) {
								errorMsg="You are not authorized to access the resource. Please login or review your privileges.";
				            } else if (res.status == 404) {
				            	errorMsg="URL not found";
				            } else if (res.status >= 500) {
				            	errorMsg="Some error occured: " + res.statusText;
				            }
							alert("Error: " + errorMsg);
						}
					});
					
				});
				
			});
		});
	}
	
	function buildFromJSONDoc(data) {
		model = data;
		var main = Handlebars.compile($("#main").html());
		var mainHTML = main(data);
		$("#maindiv").html(mainHTML);
		
		var apis = Handlebars.compile($("#apis").html());
		var apisHTML = apis(data);
		$("#apidiv").html(apisHTML);
		
		// this builds an plain array out of the apis map, that makes selecting with jlinq much easier
		var plainApis = [];
		$.each(data.apis, function(i, v) {
			$.each(v, function(j, p) {
				plainApis.push(p);	
			});
		});
		
		$("#apidiv a").each(function() {
			$(this).click(function() {
				var api = jlinq.from(plainApis).equals("jsondocId", this.id).first();
				var methods = Handlebars.compile($("#methods").html());
				var methodsHTML = methods(api);
				$("#content").html(methodsHTML);
				$("#content").show();
				if(api.supportedversions) {
					$("#apiSupportedVersions").text("Since version: " + api.supportedversions.since);
					if(api.supportedversions.until) {
						$("#apiSupportedVersions").text($("#apiSupportedVersions").text() + " - Until version: " + api.supportedversions.until);
					}
				}
				$("#testContent").hide();
				
				buildMethodsContent(api.methods);
			});
		});
		
		var objects = Handlebars.compile($("#objects").html());
		var objectsHTML = objects(data);
		$("#objectdiv").html(objectsHTML);
		
		// this builds an plain array out of the objects map, that makes selecting with jlinq much easier
		var plainObjects = [];
		$.each(data.objects, function(i, v) {
			$.each(v, function(j, p) {
				plainObjects.push(p);	
			});
		});
		
		$("#objectdiv a").each(function() {
			$(this).click(function() {
				var o = jlinq.from(plainObjects).equals("jsondocId", this.id).first();
				var object = Handlebars.compile($("#object").html());
				var objectHTML = object(o);
				$("#content").html(objectHTML);
				$("#content").show();
				$("#testContent").hide();
				
				var objecttemplate = Handlebars.compile($("#objecttemplate").html());
				var objecttemplateHTML = objecttemplate(o);
				$("#testContent").html(objecttemplateHTML);
				$("#testContent").show();
			});
		});
		
		var flows = Handlebars.compile($("#flows").html());
		var flowsHTML = flows(data);
		$("#flowdiv").html(flowsHTML);
		
		// this builds an plain array out of the flows map, that makes selecting with jlinq much easier
		var plainFlows = [];
		$.each(data.flows, function(i, v) {
			$.each(v, function(j, p) {
				plainFlows.push(p);	
			});
		});
		
		$("#flowdiv a").each(function() {
			$(this).click(function() {
				var flow = jlinq.from(plainFlows).equals("jsondocId", this.id).first();
				var methods = Handlebars.compile($("#methods").html());
				var methodsHTML = methods(flow);
				$("#content").html(methodsHTML);
				$("#content").show();
				$("#testContent").hide();
				
				buildMethodsContent(flow.methods);
			});
		});
		

		// display sidebar
		$('#maindiv').show();
		$('#side-accordion').show();
	}
	
	function fetchdoc(jsondocurl) {
		console.log("Fetching doc for " + jsondocurl);
		$.ajax({
			url : jsondocurl,
			type: 'GET',
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			success : function(data) {
				buildFromJSONDoc(data);
			},
			error: function(msg) {
				alert("Error " + msg);
			}
		});
	}

    function buildHref(resource) {

        var hostname=window.location.hostname;

        var regexp = /(alpha|dev|build)-(api|search|snorql)\.nextprot\.org/g;
        var match = regexp.exec(hostname);

        if (match != null) {
            var machine = match[1]

            if (machine == "build") {

                if (resource == "search") {

                    machine = "alpha";
                }
                else if (resource.match("search|snorql")) {

                    machine = "alpha";
                }
            }

            return "http://" + machine + "-" + resource + ".nextprot.org"
        }
    }

    function updateResourcesHrefs() {

        if (! window.location.protocol.match(/^https$/)) {

            $("a[href^='https://search.nextprot.org']").attr("href", buildHref("search"));
            $("a[href^='http://snorql.nextprot.org']").attr("href", buildHref("snorql"));
            $("a[href^='https://api.nextprot.org']").attr("href", buildHref("api"));
        }
    }

    updateResourcesHrefs();

</script>

<!-- Auth0 lock script -->
<script src="js/lock-7.0.min.js"></script>
<script src="js/jquery.cookie.js"></script>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<script>
	var lock = null;

	$(document).ready(function() {
		lock = new Auth0Lock('7vS32LzPoIR1Y0JKahOvUCgGbn94AcFW', 'nextprot.auth0.com');
	
   		var userProfile;
		
   		if ($.cookie("authUserProfile") && $.cookie("authUserToken")) {
   			// If there is already a cookie 
   			// Update login text (set to user email) 
			$('.li-login').hide();
			$('.li-logout').show();
			
			// Save the profile
			userProfile = JSON.parse($.cookie("authUserProfile"));

   			// Update login text (set to user email) 
			if (userProfile.name) {
				$('.user').text(userProfile.name);
			} else {
				$('.user').text(userProfile.email);						
			}
   		}
   		
   		$('.btn-login').click(function(e) {
   			// When click on "Login"
			e.preventDefault();
			var options = {popup: true, icon:'img/np.png', authParams: {
                scope: 'openid email name picture'
            }};
			lock.show(options, function(err, profile, token) {
				if (!err) {
					// Success calback
					// Save cookies
					$.cookie("authUserProfile", JSON.stringify(profile));
					$.cookie("authUserToken", token);

					// Save the profile
					userProfile = profile;
					
		   			// Update login text (set to user email) 
					$('.li-login').hide();
					$('.li-logout').show();
					if (userProfile.name) {
						$('.user').text(userProfile.name);
					} else {
						$('.user').text(userProfile.email);						
					}
					
					checkURLExistence();
				}
			});
		});
   		
   		$('.btn-logout').click(function(e) {
   			// When click on "Logout"
   			// Remove cookies
			$.removeCookie("authUserProfile");
			$.removeCookie("authUserToken");
			
			// Remove the profile
	   		userProfile = null;

   			window.location.reload();
			
   			// Update login text (remove user email) 
   			$('.li-logout').hide();
			$('.li-login').show();

			checkURLExistence();
   		});

		$.ajaxSetup({
			'beforeSend': function(xhr) {
				if ($.cookie("authUserToken")) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + $.cookie("authUserToken"));
				}
			}
		});
		
		checkURLExistence();
	});
</script>

</body>
</html>