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


        <%-- JSTL foreach tag example to loop an array in jsp  --%>
	<c:forEach var="name" items="${names}">
	
		<c:set var="subjectId" value="${visutils.getPathId(name)}"/>
	    <c:set var="subject" value="${map.get(name)}"/>

		nodes.push({id: ${subjectId}, group: 'mobile', label: '${name}', value: 10, level: ${visutils.getMaxDepth(subject)}});

		<c:set var="parentId"/>
	    <c:forEach var="path" items="${subject.getPathToOrigin()}">
			<c:set var="pathId" value="${visutils.getPathId(path)}"/>
			<c:set var="parentId" value=""/>
			<c:if test="${!visutils.contains(pathId)}">
				<c:set var="paths" value="${visutils.getPathArray(path)}"/>
				<c:forEach var="absolutePath" items="${paths}">
					<c:set var="simplepath" value="${visutils.getSimplePath(absolutePath)}"/>
					<c:set var="nodeid" value="${visutils.getNodeId(simplepath, absolutePath, path)}"/>
					<%-- 				 
					apath: ${absolutePath}
					path: ${path}
					current: ${nodeid}
					parent: ${parentId}
					--%>	
					<c:if test="${!visutils.contains(nodeid)}">
						${visutils.addId(nodeid)}
						<c:if test="${simplepath.equals(\"Entry\")}">
							nodes.push({id: ${nodeid}, label: '?entry', group: 'rdftypes', value: 10, level: 0});
						</c:if>
						<c:if test="${!simplepath.equals(\"Entry\")}">
							<c:if test="${!visutils.isLast(path,absolutePath)}">
								nodes.push({id: ${nodeid}, group: 'literals', label: '', value: 1, level: "${visutils.getDepth(absolutePath)}"});
							</c:if>
						</c:if>
					</c:if>
					<c:if test="${not empty parentId}">
							<c:if test="${!visutils.isLast(path,absolutePath)}">
								${visutils.getEdge(nodeid, parentId, simplepath)}
							</c:if>
							<c:if test="${visutils.isLast(path,absolutePath)}">
								${visutils.getEdge(subjectId, parentId, simplepath)}
							</c:if>
					</c:if>
					<c:set var="parentId" value="${nodeid}"/>
				</c:forEach>
			</c:if>
		</c:forEach>
	</c:forEach>
        


        // legend
        var mygraph = document.getElementById('mygraph');
        var x = - mygraph.clientWidth / 2 + 50;
        var y = - mygraph.clientHeight / 2 + 50;
        var step = 70;

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
          hierarchicalLayout: {
              direction: "LR"
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
