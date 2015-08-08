var gMenuBeforeLogin  = "" + 
"<div class=\"rootLevel\" id=\"mainMenu\">" + 
"	<ul id=\"mainMenuOne\">" + 
"		<li id=\"loginMenu\" name=\"loginMenu\" class=\"first pos1\"><a href=\"login.html\" linkindex=\"3\">Login</a></li>" + 
"		<li id=\"newUserMenu\" name=\"newUserMenu\" class=\"after pos2\"><a href=\"newUser.html\" linkindex=\"4\">New User Registration</a></li>" + 
"		<li id=\"forgotPasswordMenu\" name=\"forgotPasswordMenu\" class=\"pos3\"><a href=\"forgotPassword.html\" linkindex=\"5\">Forgot Password</a></li>" + 
"		<li id=\"contactUsMenu\" name=\"contactUsMenu\" class=\"pos4\"><a href=\"contactUs.html\" linkindex=\"6\">Contact Us</a></li>" + 
"	</ul>" + 
"</div>";

var gMenuAfterLogin  = "" + 
"<div class=\"rootLevel\" id=\"mainMenu\">" + 
"	<ul id=\"mainMenuOne\">" + 
"		<li id=\"orderSummaryMenu\" class=\"first pos1\"><a href=\"orderSummary.html\">Order Summary</a></li>" + 
"		<li id=\"createNewOrderMenu\" class=\"after pos2\"><a href=\"createNewOrder.html\">Create New Order</a></li>" + 
"		<li id=\"searchOrderMenu\" class=\"pos3\"><a href=\"searchOrder.html\">Search Order</a></li>" + 
"		<li id=\"myDetailsMenu\" class=\"pos4\"><a href=\"myDetails.html\">My Details</a></li>" + 
"		<li id=\"settingsMenu\" class=\"pos4\"><a href=\"settings.html\">Settings</a></li>" + 
"		<li id=\"contactUsMenu\" class=\"pos5\"><a href=\"#\">Contact Us</a></li>" + 
"		<li id=\"logoffMenu\" style='float:right' class=\"pos6\"><a href=\"login.html\" linkindex=\"6\">Logoff</a></li>" + 
"	</ul>" + 
"</div>";


var gHeader  = "" + 
"<table border=\"0\" style=\"border-collapse:collapse; border-style:hidden; width:100%;\">" + 
"	<tr>" + 
"		<td width=\"10%\">" + 
"			<img src=\"images/logo_white.gif\"/>" + 
"		</td>" + 
"		<td id=\"appName\" style=\"border-bottom: solid 1px black; text-align: right; width:100%\">" + 
"				Order Tracking System" + 
"		</td>" + 
"	</tr>" + 
"</table>" + 
"<br>";

var gFooter = "Order Tracking Systen - For internal use.  Copyright &#169;  2010 ABB. All rights reserved. " + 
				"Reproduction in whole or in part is prohibited without the written consent of ABB."

function initPage(pageName, isBeforeLogin) {
	if (isBeforeLogin)
	{
		document.getElementById("mainMenuOuter").innerHTML = gMenuBeforeLogin;
	} else {
		document.getElementById("mainMenuOuter").innerHTML = gMenuAfterLogin;
	}
	document.getElementById("header").innerHTML = gHeader;
	document.getElementById("footer").innerHTML = gFooter;
	pageName = pageName + "Menu";
	if (document.getElementById(pageName))
	{
		document.getElementById(pageName).className = document.getElementById(pageName).className + " pos1 selected";
	}
}


/* function to show/hide table row */
function toggleRow(rowId) {
	var row = document.getElementById(rowId);
	if (!row)
	{
		return;
	}
	if (row.style.display == '') {
		row.style.display = 'none';
	}
	else {
		row.style.display = '';
	}
}


function toggleImage(objImg) {
	if (objImg.src.indexOf('expanded') >= 0)
	{
		objImg.src = 'images/collapsed.gif';
	} else {
		objImg.src = 'images/expanded.gif';
	}
}

function toggleRows(rowIdPrefix, noOfRows) {

	for (i=1; i <= noOfRows; i++)
	{
		toggleRow(rowIdPrefix + "_" + i);
	}
}

