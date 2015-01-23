<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Graph | Images</title>

<style type="text/css">
body {
	font: 10pt arial;
	padding: 0;
	margin: 0;
	overflow: hidden;
}

#mygraph {
	width: 100%;
	height: 100%;
	box-sizing: border-box;
}
</style>

<script type="text/javascript" src="http://visjs.org/dist/vis.js"></script>

<script type="text/javascript">
    var nodes = null;
    var edges = null;
    var graph = null;

    var LENGTH_MAIN = 350,
        LENGTH_main_rdftypes = 150,
        LENGTH_SUB = 50,
        WIDTH_SCALE = 2,
        GREEN = 'green',
        RED = '#C5000B',
        ORANGE = 'orange',
    //GRAY = '#666666',
        GRAY = 'gray',
        BLACK = '#2B1B17';

    // Called when the Visualization API is loaded.
    function draw() {
      // Create a data table with nodes.
      nodes = [];

      // Create a data table with links.
      edges = [];


  	<c:forEach var="name" items="${names}">
    <c:set var="subject" value="${map.get(name)}"/>
		<c:if test="${!visutils.contains(subject.getId())}">
			${visutils.addId(subject.getId())}
			nodes.push({id: ${subject.getId()}, label: '${subject.getTypeName()}', group: 'main_rdftypes', value: ${subject.getInstanceCount()}});
		</c:if>
	</c:forEach>


        <%-- JSTL foreach tag example to loop an array in jsp  --%>
	<c:forEach var="name" items="${names}">
    <c:set var="subject" value="${map.get(name)}"/>
		<c:if test="${!visutils.contains(subject.getId())}">
			${visutils.addId(subject.getId())}
			nodes.push({id: ${subject.getId()}, label: '${subject.getTypeName()}', group: 'main_rdftypes', value: ${subject.getInstanceCount()}});
		</c:if>

		<c:forEach var="triple" items="${subject.getTriples()}">
		<c:set var="object" value="${map.get(triple.getObjectType())}"/>
		<c:if test="${not empty object}">
			<c:if test="${!visutils.contains(object.getId())}">
				${visutils.addId(object.getId())}
				nodes.push({id: ${object.getId()}, label: '${object.getTypeName()}', group: 'rdftypes', value: 5});
			</c:if>
			edges.push({from: ${subject.getId()}, to: ${object.getId()}, length: LENGTH_MAIN, width: WIDTH_SCALE, label: '${triple.getPredicate()}'});
		</c:if>

		<%-- case of blank nodes and literals --%>
		
		<c:if test="${empty object}">
			<c:set var="tripleId" value="${visutils.getTripleId(triple)}"/>
			<c:if test="${!visutils.contains(tripleId)}">
				${visutils.addId(object.getId())}
				<c:if test="${!triple.getObjectType().equals(\"BlankNodeType\")}">
					nodes.push({id: ${tripleId}, label: '${triple.getPredicate()}', group: 'literals', value: 5});
				</c:if>
				<c:if test="${triple.getObjectType().equals(\"BlankNodeType\")}">
					nodes.push({id: ${tripleId}, label: '${triple.getPredicate()}', group: 'blank_nodes', value: 5});
				</c:if>
			</c:if>
			edges.push({from: ${subject.getId()}, to: ${tripleId}, length: LENGTH_MAIN/20, width: WIDTH_SCALE/2, label: ''});
		</c:if>

		
		</c:forEach>
	</c:forEach>
        


        // legend
        var mygraph = document.getElementById('mygraph');
        var x = - mygraph.clientWidth / 2 + 50;
        var y = - mygraph.clientHeight / 2 + 50;
        var step = 70;
        nodes.push({id: 1000, x: x, y: y, label: 'Main Types', group: 'main_rdftypes', value: 1});
        nodes.push({id: 1001, x: x, y: y + step, label: 'Derived Types', group: 'rdftypes', value: 1});
        nodes.push({id: 1002, x: x, y: y + 2 * step, label: 'Blank Nodes', group: 'blank_nodes', value: 1});
        nodes.push({id: 1003, x: x, y: y + 3 * step, label: 'Literals', group: 'literals', value: 1});

        // create a graph
        var container = document.getElementById('mygraph');
        var data = {
          nodes: nodes,
          edges: edges
        };
        var options = {
          stabilize: true,   // stabilize positions before displaying
          nodes: {
            radiusMin: 16,
            radiusMax: 32,
            fontColor: BLACK
          },
          edges: {
            color: BLACK
          },
          groups: {
            'blank_nodes': {
              shape: 'triangle',
              color: '#FF9900' // orange
            },
            literals: {
              shape: 'dot',
              color: "#2B7CE9" // blue
            },
            mobile: {
              shape: 'dot',
              color: "#5A1E5C" // purple
            },
            main_rdftypes: {
              shape: 'square',
              color: "#C5000B" // red
            },
            rdftypes: {
              shape: 'square',
              color: "#109618" // green
            }
          }
        };
        graph = new vis.Graph(container, data, options);
      }
    </script>
</head>

<body onload="draw()">

	<div id="mygraph">
		<div class="graph-frame"
			style="position: relative; overflow: hidden; width: 100%; height: 100%;">
			<canvas style="position: relative; width: 100%; height: 100%;"></canvas>
		</div>
	</div>


</body>
</html>
