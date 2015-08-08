function displayDate(dtToday)
{
	var strOuput = "";
	var arrDays = new Array("Sun ", "Mon ","Tue ","Wed ", 
						 "Thu ", "Fri ", "Sat ");
	var arrMonths = new Array( "Jan ","Feb ", "Mar ", "Apr ",
							"May ","Jun ","Jul ","Aug ",
							"Sep ","Oct ","Nov ", "Dec ");
	strOuput += arrDays[dtToday.getDay()];
	strOuput += arrMonths[dtToday.getMonth()];
	strOuput += dtToday.getDate();
	strOuput += " ";
	strOuput += dtToday.getHours();
	strOuput += ":";
	strOuput += dtToday.getMinutes();
	strOuput += ":";
	strOuput += dtToday.getSeconds();
	strOuput += " ";
	strOuput += dtToday.getFullYear();
	strOuput += "<BR>";
	document.write(strOuput);
}
function greet()
{
	var dtToday = new Date();
	displayDate(dtToday);
	var intHrs = dtToday.getHours();
	if (intHrs >= 0 && intHrs <= 11)
	{
		document.write("Good Morning");
		alert("Good Morning");
	}
	else if (intHrs <= 16)
	{
		document.write("Good Afternoon");
		alert("Good Afternoon");
	}
	else
	{
		document.write("Good Evening");
		alert("Good Evening");
	}
}
greet();