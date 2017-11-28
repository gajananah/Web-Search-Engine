<%@page import="java.util.Map"%>
<%@ page session="true" language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to Information Retrieval</title>
<style type="text/css">
table,th,td {
	border: 1px solid black;
	align: center;
}
</style>

<script type="text/javaScript">  
function validateCrawl()
{
	
	// check for -- http://  or https://
	var depth = (document.forms[0].depthVal.value);
	var url = (document.forms[0].urlStr.value);
	var maxURL = (document.forms[0].maxURLs.value);

	var slice1 = url.trim().slice(0,7); // http://
	var slice2 = url.trim().slice(0,8); // https://


	if(slice1!="http://" && slice2!="https://"){
		alert("Seed URL is "+url);
		alert("Enter URL in proper format begining with http:// or https:// ");
		return false;
		
	}

	if(url=="http://" || url=="https://"){
		alert("Seed URL is "+url+", not in propoer format");
		return false;
	}
	

	if(url=="" || url==null){
		alert("Seed URL cannot be blank");
		return false;
	}

	if(depth=="" || isNaN(depth)==true){
		alert("Depth cannot be blank or non numeric, enter some integer or at least zero");
		return false;
	}
	
	if(isNaN(maxURL)==true || maxURL==null || maxURL=="0.0" || maxURL=="0" || maxURL==""){
		alert("Please enter a small positive integer - Max # of URLs to crawl to improve performance");
		return false;
	}
	
	document.forms[0].depthVal.value = parseInt(depth, 10);

	if(Number(document.forms[0].depthVal.value)>10)
	{
		alert("Very High Depth Entered! Re-setting to 4");
		document.forms[0].depthVal.value =4;
	}
	
	document.forms[0].maxURLs.value = parseInt(maxURL, 10);

	document.forms[0].indexSummary.value ="Please wait as crawling happens..!";
	// Reset search boxes
	document.forms[0].searchQry.value =" ";
	document.forms[0].searchSummary.value=" ";
	document.forms[0].searchReslToDisplay.value="0";

	//Reset Crawl Boxes
	document.forms[0].clusterSummary.value=" ";
	//alert("clus summ "+document.forms[0].clusterSummary.value);
	alert("Proceeding to Crawl and Index..");
	
	
	document.forms[0].submit();

}

function submitSearch()
{
	//alert("search..");
var indexStatus = document.forms[0].indexSummary.value.trim();
	
	var srch = document.forms[0].searchQry.value;
	var smry = document.forms[0].searchSummary.value;
	var noOfSearch = Number(document.forms[0].searchReslToDisplay.value);
	
	//alert("query ="+srch+" Max no of results to display ="+noOfSearch+"  index Status ="+indexStatus);

	if(isNaN(noOfSearch)==true || noOfSearch==null || noOfSearch=="0.0" || noOfSearch=="0"){
		alert("Max URLs to view as search results not provided, enter a small positive value");
		return false;
	}

	
	if(srch=="" || srch==null || srch=="%" || srch=="." || srch=="*" || srch=="*.*")
	{
		alert("Cannot enter blank or only wild card search! Please enter some valid query! ");
		return false;
	}
	

	if(indexStatus=="" || indexStatus==" " || indexStatus=="Please wait as crawling happens..!"){
		alert("Cannot Search! Since Indexing and Crawling not completed");
		return false;
	}

	
	
	{
	 document.forms[0].searchReslToDisplay.value = parseInt(noOfSearch, 10);
	 document.forms[0].searchSummary.value="Please wait as we search.....!";
	 //Reset clustering summary
	 document.forms[0].clusterSummary.value=" ";
	 alert("Proceeding to Search...");
	 
	 
	 document.forms[0].submit();
	}


	
}

function submitCluster(){

	//alert("cluster..");
	var indexStatus = document.forms[0].indexSummary.value.trim();
	var smry = document.forms[0].searchSummary.value;
	var radioSel = document.forms[0].clusterRadio.value; // LSA or kMeans
	//var lsa = Number(document.forms[0].LSAthreshold.value);
	var kmeans = parseInt(document.forms[0].kClusters.value);

	//alert("radio selected = "+radioSel);

	//alert("indexStatus ="+indexStatus+" search status "+smry);

	if(radioSel=="kMeans")
	{
		if(isNaN(kmeans)==true || kmeans==null || kmeans=="0.0" || kmeans=="0" || kmeans>10 || kmeans<0)
		{
	    alert("Num of Clusters entered = "+kmeans+" Please enter a reasonable positive integer value!");
		return false;
		}
	}


	if(indexStatus=="" || indexStatus=="Please wait as crawling happens..!" || smry=="" || smry=="Please wait as we search.....!"){
		alert("Cannot perform clustering till crawling and search is complete!");
		return false;
	}
	
	alert("Proceeding to Cluster");
	document.forms[0].clusterSummary.value="Please wait as system performs clustering.....!";
	document.forms[0].submit();
}


</script>

<style type="text/css">
table,th,td {
	border: 1px solid black;
	align: center;
}

submit {
    background-color: #4CAF50;
    border: none;
    color: red;
    padding: 15px 32px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
}
</style>
</head>
<body>



<form action="/IRP3V01/IRP3Controller"  scope="session"  method="post" >

<h3 align="center" style="color: blue;">Welcome to Information Retrieval with Clustering</h3>

<p />
<p />
<p />
        <!-- Part 1 -->
		<table align="center" style="width: 75%">
			<tr>
				<th>Phase 1 : Crawling and Indexing</th>
			</tr>
						
			<table align="center" style="width: 75%">
			<tr>
			<td>Enter Seed URL*</td>
			
			<td><input size="40	" type="text"   name="urlStr" value="<%= session.getAttribute("urlStr")==null?"":session.getAttribute("urlStr")%>" /></td>
			</tr>
			
			<tr>
			<td>Enter Depth*</td>
			<td><input size="2" type="text"  onkeypress='return event.charCode >= 48 && event.charCode <= 57' name="depthVal" value="<%= session.getAttribute("depthVal")==null?"":session.getAttribute("depthVal")%>" /></td>
			</tr>
			
			<tr>
			<td>Max. URLs to Crawl*</td>
			<td><input size="2" type="text" onkeypress='return event.charCode >= 48 && event.charCode <= 57'  name="maxURLs" value="<%= session.getAttribute("maxURLs")==null?"":session.getAttribute("maxURLs")%>" /></td>
			</tr>
			
			
			</table>
			
			<table align="center" style="width: 75%">
			<tr align="center">
			<td>
			<input type="submit" id="crwl" border="" name="btn" onclick="return validateCrawl();" value="Start Crawling" />
			</td>
			</tr>
			</table>
			
			
			<table align="center" style="width: 75%">
			<tr>
			<td>Crawl & Index Summary</td>
			<td><input size="65" type="text" readonly="readonly" name="indexSummary" value="<%=session.getAttribute("indexSummary")==null?"":session.getAttribute("indexSummary")%>" /></td>
			</tr>
			</table>
		
		</table>
		<p />
		<p />
		<p />
		<p />


        <!-- Part 2 -->
		<table align="center" style="width: 75%">
			<tr>
				<th>Phase 2 : Search Text & Most Representative Image</th>
			</tr>
			
			<table align="center" style="width: 75%">
			<tr>
			<td>Enter Search Term*</td>
			
			<td><input size="40	" type="text"  name="searchQry" value="<%= session.getAttribute("searchQry")==null?"":session.getAttribute("searchQry")%>" /></td>
			</tr>
			
			<tr>
			<td>Max. No of Results to Display*</td>
			
			<td><input size="2" type="text" onkeypress='return event.charCode >= 48 && event.charCode <= 57'  name="searchReslToDisplay" value="<%= session.getAttribute("searchReslToDisplay")==null?"":session.getAttribute("searchReslToDisplay")%>" /></td>
			</tr>
			</table>
			
			<table align="center" style="width: 75%">
			<tr align="center">
			<td>
			<input type="submit" name="btn" onclick="return submitSearch();"  value="Search" />
			</td>
			</tr>
			
			<table align="center" style="width: 75%">
			<tr>
			<td>Search Summary</td>
			<td><input size="65	" type="text" readonly="readonly" name="searchSummary" value="<%=session.getAttribute("searchSummary")==null?"":session.getAttribute("searchSummary")%>" /></td>
			</tr>
			</table>
			
			</table>
			
			
		    <!-- Results Table -->
		    <table align="center" style="width: 75%">
			<tr>
			<td><b>Rank</b></td>
			<td><b>Score</b></td>
			<td><b>URL</b></td>
			<td><b>Representative Image</b></td>	
			</tr>
			
			<c:forEach var="results" items="${listofMaps}">
	        <tr> 
	        
	        <td><c:out  value="${results['rank']}"></c:out> </td>  
 	        <td><c:out  value="${results['score']}"></c:out> </td>
	        <td><c:out  value="${results['url']}"></c:out> </td>
	        <td><img    src="${pageContext.request.contextPath}/pics/TempCrawl${results['imagepath']}" width="110" > </td>
	        
	        
	        </tr>
	       
	        
            </c:forEach>
			</table>
		</table>
		<p />
		<p />

		<table align="center" style="width: 75%">
			<tr>
				<th>Phase 3 : Clustering</th>
			</tr>
			
			<table align="center" style="width: 75%">
					
			
			<tr>
			 
			<td>
			
			<input type="radio"  name="clusterRadio" value="kMeans"  checked="checked" />"Spherical" KMeans with Instance Normalization<br>
			<p/>
			Enter # of Clusters (A typical value = 3 for K-Means) * <input type="text" onkeypress='return event.charCode >= 48 && event.charCode <= 57' size="2" value="<%=session.getAttribute("kClusters")==null?"":session.getAttribute("kClusters")%>"  name="kClusters" /> <br>
			</td>
			</tr>
			</table>
			
			<table align="center" style="width: 75%">
			<tr align="center">
			<td>
			<input type="submit" name="btn" onclick="return submitCluster();" value="Cluster" />
			</td>
			</tr>
			
			<table align="center" style="width: 75%">
			<tr>
			<td>Clustering Summary</td>
			<td><input size="65" type="text" readonly="readonly" name="clusterSummary" value="<%=session.getAttribute("clusterSummary")==null?"":session.getAttribute("clusterSummary")%>" /></td>
			</tr>
			</table>
			
			<!-- Results Table -->
			
		    <table align="center" style="width: 75%">
			<tr>
			
			<td><b>Clusters</b></td>
			<td><b>URLs</b></td>
			<td><b>No of URLs</b></td>
			<td><b>Images per Cluster</b></td>
			</tr>
			
			<c:forEach var="clusResults" items="${listofClusters}">
	        <tr> 
	        
	        <td><c:out value="${clusResults['clusterNum'].clusterNum}"></c:out> </td> 
 	        <td><c:out value="${clusResults['clusterURLs'].clusterURLs}"></c:out> </td> 
 	        <td><c:out value="${clusResults['numURLs'].numURLs}"></c:out> </td> 
 	        
 	        <td>
 	        <c:forEach var="myImages" items="${clusResults['arrayListofURLsPerCluster'].arrayListofURLsPerCluster}">
 	         <img src="${pageContext.request.contextPath}/pics/TempCrawl${myImages}" width="60" >    
 	        </c:forEach>
 	        </td>
 	     
	        </tr>
	        
            </c:forEach>
            
			</table>
		
		</table>
		
		<p />
		<p />

</form>
</body>
</html>