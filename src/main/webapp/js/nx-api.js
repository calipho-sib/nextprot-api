var model;

function checkURLExistence() {
	var value = "http://" + window.location.hostname + ":8080/nextprot-api/jsondoc";
	if (value.trim() == '') {
		alert("Please insert a valid URL");
		return false;
	} else {
		return fetchdoc(value);
	}
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

String.prototype.endsWith = function(suffix) {
	return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

function printResponse(data, res, url, time) {

	var now = new Date().getTime();
	if (res.responseXML != null) {
		var xml = formatXML(res.responseText);
		$("#response").text(xml);
	} else {
		if (url.endsWith("ttl")) {
			$("#response").text(res.responseText);
		} else {
			var json = JSON.stringify(data, undefined, 2);
			$("#response").text(JSON.stringify(data, undefined, 2));
		}
	}

	prettyPrint();
	$("#timeElapsed").text((now - time) + " ms");
	$("#responseStatus").text(res.status);
	$("#responseHeaders").text(res.getAllResponseHeaders());
	$("#requestURL").html(
			"<a href='" + url + "' target='_blank'>" + url + "</a>");
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
		if (node.match(/.+<\/\w[^>]*>$/)) {
			indent = 0;
		} else if (node.match(/^<\/\w/)) {
			if (pad != 0) {
				pad -= 1;
			}
		} else if (node.match(/^<\w[^>]*[^\/]>.*$/)) {
			indent = 1;
		} else {
			indent = 0;
		}

		var padding = '';
		for ( var i = 0; i < pad; i++) {
			padding += '  ';
		}

		formatted += padding + node + '\r\n';
		pad += indent;
	});

	return formatted;
}

function fetchdoc(jsondocurl) {
	$.ajax({
		url : jsondocurl,
		type: 'GET',
		dataType: 'json',
		contentType: "application/json; charset=utf-8",
		success : function(data) {
			model = data;
			var main = Handlebars.compile($("#main").html());
			var mainHTML = main(data);
			$("#maindiv").html(mainHTML);
			$("#maindiv").show();
			
			var apis = Handlebars.compile($("#apis").html());
			var apisHTML = apis(data);
			$("#apidiv").html(apisHTML);
			$("#apidiv").show();
			
			$("#apidiv a").each(function() {
				$(this).click(function() {
					var api = jlinq.from(data.apis).equals("jsondocId", this.id).first();
					var methods = Handlebars.compile($("#methods").html());
					var methodsHTML = methods(api);
					$("#content").html(methodsHTML);
					$("#content").show();
					$("#apiName").text(api.name);
					$("#apiDescription").text(api.description);
					$("#testContent").hide();
					
					$('#content a[rel="method"]').each(function() {
						$(this).click(function() {
							var method = jlinq.from(api.methods).equals("jsondocId", this.id).first();
							var test = Handlebars.compile($("#test").html());
							var testHTML = test(method);
							$("#testContent").html(testHTML);
							$("#testContent").show();
							
							$("#produces input:first").attr("checked", "checked");
							
							$("#testButton").click(function() {
								var headers = new Object();
								$("#headers input").each(function() {
									headers[this.name] = $(this).val();
								});
								
								headers["Accept"] = $("#produces input:checked").val();
								
								var replacedPath = method.path;
								var tempReplacedPath = replacedPath; // this is to handle more than one parameter on the url
								$("#pathparameters input").each(function() {
									tempReplacedPath = replacedPath.replace("{"+this.name+"}", $(this).val());
									replacedPath = tempReplacedPath;
								});

								$("#queryparameters input").each(function() {
									tempReplacedPath = replacedPath.replace("{"+this.name+"}", $(this).val());
									replacedPath = tempReplacedPath;
								});
								
								$('#testButton').button('loading');
								
								var res = $.ajax({
									url : model.basePath + replacedPath,
									type: method.verb,
									data: $("#inputJson").val(),
									headers: headers,
									contentType: $("#consumes input:checked").val(),
									success : function(data) {
										printResponse(data, res, this.url);
									},
									error: function(data) {
										printResponse(data, res, this.url);
									}
								});
								
							});
							
						});
					});
				});
			});
			
			var objects = Handlebars.compile($("#objects").html());
			var objectsHTML = objects(data);
			$("#objectdiv").html(objectsHTML);
			$("#objectdiv").show();
			
			$("#objectdiv a").each(function() {
				$(this).click(function() {
					var o = jlinq.from(data.objects).equals("jsondocId", this.id).first();
					var object = Handlebars.compile($("#object").html());
					var objectHTML = object(o);
					$("#content").html(objectHTML);
					$("#content").show();
					
					$("#testContent").hide();
				});
			});

		},
		error: function(msg) {
			alert("Error " + msg);
		}
	});
}
$(document).ready(function() {

    var widget = new Auth0Widget({
	    domain: 'nextprot.auth0.com',
	    clientID: '7vS32LzPoIR1Y0JKahOvUCgGbn94AcFW',
	    callbackURL: location.href,
        callbackOnLocationHash: true
    });


    
    $('.btn-login').click(function(e) {
      e.preventDefault();
      widget.signin({ popup: true, scope : 'openid profile'} , null, function(err, profile, token) {
        if (err) {
          // Error callback
          console.log("There was an error");
          alert("There was an error with the log in");
        } else {

        	// Save the JWT token.
            localStorage.setItem('userToken', token);
            localStorage.setItem('nickname', profile.nickname);
            
            // Success calback
        	
        	/*
          widget.getClient().getDelegationToken('IckaP4QRfGSRGuVZfP9VJBUdlXtgcS4o', token,
            function(err, thirdPartyApiToken) {
              localStorage.setItem('thirdPartyApiToken', thirdPartyApiToken.id_token);
              console.log("Third party token", thirdPartyApiToken.id_token);
            });
        	*/
        	setUserState();
          
        }
      });
    });

    $('.btn-logout').click(function(e) {
    	localStorage.removeItem('userToken');
        localStorage.removeItem('nickname');
        setAnonymousState();
    });
    
    function getJSONDOCAccordingToUser(){
        checkURLExistence();
    }
    
    function setUserState(){
        $('.btn-logout').show();
        $('.btn-login').hide();
        $('.login-box').hide();
        $('.logged-in-box').show();
        $('.nickname').text("Welcome " + localStorage.getItem('nickname'));
        getJSONDOCAccordingToUser();
    }

    function setAnonymousState(){
        $('.btn-login').show();
    	$('.nickname').text("You are anonymous");
        $('.btn-login').show();
        $('.btn-logout').hide();
        getJSONDOCAccordingToUser();
    }

    
    $('.btn-api').click(function(e) {
        // Just call your API here. The header will be sent
        $.ajax({
          url: 'http://localhost:3001/secured/ping',
          method: 'GET'
        }).then(function(data, textStatus, jqXHR) {
          alert("The request to the secured enpoint was successfull");
        }, function() {
          alert("You need to download the server seed and start it to call this API");
        });
      });

    $.ajaxSetup({
  	  'beforeSend': function(xhr) {
  	    if (localStorage.getItem('userToken')) {
  	      xhr.setRequestHeader('Authorization',
  	            'Bearer ' + localStorage.getItem('userToken'));
  	    }
  	  }
  	});

    //Sets the states initially
    if(localStorage.getItem('userToken')){
    	setUserState();
    }else {
    	setAnonymousState();
    }
    


});

