function validate()
{
	if (validateEmpCode() && validateEmpName() && validateExtNo() && validateEmail())
	{
		if (validateDateMonth() && validateLeaveType())
		{
			showDetails();
		}
	}
}
function validateEmpCode()
{
	var strEmpCode = frmLeaveForm.txtEmpCode.value;
	if (isNaN(frmLeaveForm.txtEmpCode.value) || isNaN(parseInt(frmLeaveForm.txtEmpCode.value)))
	{
		alert("Employee Code should be a 4 digit number >= 1000.");
		frmLeaveForm.txtEmpCode.select();
		frmLeaveForm.txtEmpCode.focus();
		return false;
	}
	var intEmpCode = parseInt(strEmpCode);
	if( intEmpCode < 1000 || strEmpCode.length > 4)	
	{
		alert("Employee Code should be a 4 digit number >= 1000.");
		frmLeaveForm.txtEmpCode.select();
		frmLeaveForm.txtEmpCode.focus();
		return false;
	}
	return true;
}
function validateEmpName()
{
	var strName = frmLeaveForm.txtEmpName.value;
	if (strName.length == 0)
	{
		alert("Please enter name here.");
		frmLeaveForm.txtEmpName.select();
		frmLeaveForm.txtEmpName.focus();
		return false;
	}
	var strRegx = /[0-9]/;
	if (strName.match(strRegx) != null)
	{
		alert("Name should not contain any number.");
		frmLeaveForm.txtEmpName.select();
		frmLeaveForm.txtEmpName.focus();
		return false;
	}
	var strUpperCaseName = strName.toUpperCase();
	if(strName != strUpperCaseName)
	{
		alert("Please Enter Name in Uppercase only.");
		frmLeaveForm.txtEmpName.select();
		frmLeaveForm.txtEmpName.focus();
		return false;
	}
	return true;
}		

function validateExtNo()
{
	var strExtn = frmLeaveForm.txtExt.value;
	if (isNaN(strExtn) || isNaN(parseInt(strExtn)))
	{
		alert ("Please enter valid extension number.");
		frmLeaveForm.txtExt.select();
		frmLeaveForm.txtExt.focus();
		return false;
	}
	var intExtn = parseInt(strExtn);
	if(intExtn > 999)
	{
		alert("Extension number should be <= 999");
		frmLeaveForm.txtExt.select();
		frmLeaveForm.txtExt.focus();
		return false;
	}
	return true;
}
function validateEmail()
{
	var strEmail = frmLeaveForm.txtEmail.value;
	var strRegx = /.+@.+/;
	var blnIsValid = true;
	if (strEmail.length == 0)
		blnIsValid = false;
	if (strEmail.indexOf('@') != strEmail.lastIndexOf('@'))
		blnIsValid = false;
	if (strEmail.indexOf('@') == -1)
		blnIsValid = false;
	if (strEmail.match(strRegx) == null)
		blnIsValid = false;
	if (strEmail.lastIndexOf('.') == strEmail.length-1)
		blnIsValid = false;
	if (strEmail.lastIndexOf('.') < strEmail.indexOf('@'))
		blnIsValid = false;
	var strSubstring = strEmail.substring(strEmail.indexOf('@'));
	if (strSubstring.match(/[0-9]+/) != null)
		blnIsValid = false;
	if (strEmail.indexOf('.') == strEmail.indexOf('@') + 1)
		blnIsValid = false;
	if (blnIsValid == false)
	{
		alert ("Please enter a valid email address.");
		frmLeaveForm.txtEmail.select();
		frmLeaveForm.txtEmail.focus();
		return false;
	}

	strEmail = frmLeaveForm.txtManagerEmail.value;
	strRegx = /.+@.+/;
	blnIsValid = true;
	if (strEmail.length == 0)
		blnIsValid = false;
	if (strEmail.indexOf('@') != strEmail.lastIndexOf('@'))
		blnIsValid = false;
	if (strEmail.indexOf('@') == -1)
		blnIsValid = false;
	if (strEmail.match(strRegx) == null)
		blnIsValid = false;
	if (strEmail.lastIndexOf('.') == strEmail.length-1)
		blnIsValid = false;
	if (strEmail.lastIndexOf('.') < strEmail.indexOf('@'))
		blnIsValid = false;
	var strSubstring = strEmail.substring(strEmail.indexOf('@'));
	if (strSubstring.match(/[0-9]+/) != null)
		blnIsValid = false;
	if (strEmail.indexOf('.') == strEmail.indexOf('@') + 1)
		blnIsValid = false;
	if (blnIsValid == false)
	{
		alert ("Please enter a valid email address of the manager.");
		frmLeaveForm.txtManagerEmail.select();
		frmLeaveForm.txtManagerEmail.focus();
		return false;
	}
	return true;
} 
function validateDateMonth()
{
	if (frmLeaveForm.lstStartDate.selectedIndex == 0)
	{
		alert ("Please select a start date.");
		frmLeaveForm.lstStartDate.focus();
		return false;
	}
	if (frmLeaveForm.lstStartMonth.selectedIndex == 0)
	{
		alert ("Please select a month.");
		frmLeaveForm.lstStartMonth.focus();
		return false;
	}
	if (frmLeaveForm.lstEndDate.selectedIndex == 0)
	{
		alert ("Please select a date.");
		frmLeaveForm.lstEndDate.focus();
		return false;
	}
	if (frmLeaveForm.lstEndMonth.selectedIndex == 0)
	{
		alert ("Please select a month.");
		frmLeaveForm.lstEndMonth.focus();
		return false;
	}
	// check for valid no of days in a selected month
	switch (frmLeaveForm.lstStartMonth.selectedIndex)
	{
		case 2 : if (frmLeaveForm.lstStartDate.selectedIndex > 28)
				 {
					alert ("Feb of 2003 has only 28 days.");
					frmLeaveForm.lstStartDate.focus();
					return false;
				 }
		case 4 :	
		case 6 :	
		case 9 : 
		case 11 : 
					if (frmLeaveForm.lstStartDate.selectedIndex > 30)
					{
						alert ("The selected month has only 30 days.");
						frmLeaveForm.lstStartDate.focus();
						return false;
					}
	}
	switch (frmLeaveForm.lstEndMonth.selectedIndex)
	{
		case 2 : if (frmLeaveForm.lstEndDate.selectedIndex > 28)
				 {
					alert ("Feb of 2003 has only 28 days.");
					frmLeaveForm.lstEndDate.focus();
					return false;
				 }
		case 4 :	
		case 6 :	
		case 9 : 
		case 11 :	if (frmLeaveForm.lstEndDate.selectedIndex > 30)
					{
						alert ("The selected month has only 30 days.");
						frmLeaveForm.lstEndDate.focus();
						return false;
					}
	}
	// check that startDate <= endDate
	if (frmLeaveForm.lstStartMonth.selectedIndex == frmLeaveForm.lstEndMonth.selectedIndex)
	{
		if (frmLeaveForm.lstStartDate.selectedIndex > frmLeaveForm.lstEndDate.selectedIndex)
		{
			alert("Start date should be <= End date");
			return false;
		}
	}
	else if (frmLeaveForm.lstStartMonth.selectedIndex > frmLeaveForm.lstEndMonth.selectedIndex)
	{
		alert("Start month should be <= End month");
		return false;
	}

	return true;
}

function validateLeaveType()
{
	if (frmLeaveForm.lstLeaveType.options.selectedIndex == 0)
	{
		if (frmLeaveForm.txtAddress.value.length == 0)
		{
			alert("Please enter contact address.");
			frmLeaveForm.txtAddress.select();
			frmLeaveForm.txtAddress.focus();
			return false;
		}
	}
	return true;
}
function activateAddressField()
{
	var intLeaveType = frmLeaveForm.lstLeaveType.options.selectedIndex;
	if(intLeaveType == 0)
		frmLeaveForm.txtAddress.disabled = false;
	else
	{	
		frmLeaveForm.txtAddress.value = "";
		frmLeaveForm.txtAddress.disabled = true;
	}
}

function showDetails()
{
	newWindow = window.open("","","width=500,height=500,top=0,left=0")

	var strMessage = 
		"<B>Employee Code :</B>"+ frmLeaveForm.txtEmpCode.value + "<BR>" +
		"<B>Employee Name :</B>"+ frmLeaveForm.txtEmpName.value + "<BR>" +
		"<B>Employee Group:</B>"+ frmLeaveForm.lstGroup.options[frmLeaveForm.lstGroup.options.selectedIndex].text + "<BR>" +
		"<B>Extension Number :</B>"+ frmLeaveForm.txtExt.value + "<BR>" +
		"<B>Email-id :</B>" + frmLeaveForm.txtEmail.value + "<BR>" +

		"<B>Leave Starting Date :</B>"  + frmLeaveForm.lstStartDate.options[frmLeaveForm.lstStartDate.selectedIndex].text
										+ "/" +
										frmLeaveForm.lstStartMonth.options[frmLeaveForm.lstStartMonth.selectedIndex].text
								 + "/" +
								 "2003" + "<BR>" +
		"<B>End Leaving Date :</B>" +	frmLeaveForm.lstEndDate.options[frmLeaveForm.lstEndDate.selectedIndex].text
									+ "/" + 
									 frmLeaveForm.lstEndMonth.options[frmLeaveForm.lstEndMonth.selectedIndex].text
									 + "/" + 
									 "2003" + "<BR>" + 
		"<B>Type of Leave :</B>" + frmLeaveForm.lstLeaveType.options[frmLeaveForm.lstLeaveType.options.selectedIndex].text + "<BR>";
		if (frmLeaveForm.lstLeaveType.options[frmLeaveForm.lstLeaveType.options.selectedIndex].text == "PL")
		{
			strMessage += "<B>Contact Address : </B>" +	frmLeaveForm.txtAddress.value + "<BR>";
		}
		strMessage += "<B>Email id of Manager :</B>" + frmLeaveForm.txtManagerEmail.value + "<BR>";

	newWindow.document.write(strMessage);
	newWindow.document.close();
}

function employee(code, name, group, extn, email, startDate, endDate, leaveType, address, managerEmail)
{
	this.code = code;
	this.name = name;
	this.group = group;
	this.extn = extn;
	this.email= email;
	this.startDate = startDate;
	this.endDate = endDate;
	this.leaveType = leaveType;
	this.address = address;
	this.managerEmail = managerEmail;
	this.showInfo = showInfo();
}
