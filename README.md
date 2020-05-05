# MassGmailR
Tool for sending mass e-mails using Gmail API

To use it, first you need to authorize your gmail account, then load an .xlsx (Excel) file. 
The structure of such file is going to be following:

First you start with a template of the email, then the following rows include email addresses you want to sent the mail to, 
and the variables that are going to be unique for each receiver. As an example, it can be used to customize name of the receiver.
To mark the place where you put a variable '%s' should be used. *(If one wants to use normal '%' sign inside their message, they should type '%%' instead)
There is no restriction to the ammount of variables

EMAIL_TEMPLATE_CELL |    
--------------------------------------------------------
EMAIL_ADRESS_NO_1   | Var1_1 | Var1_2 | Var1_3 | ...
--------------------------------------------------------
EMAIL_ADRESS_NO_2   | Var2_1 | Var2_2 | Var2_3 | ...
--------------------------------------------------------
EMAIL_ADRESS_NO_3   | Var3_1 | Var3_2 | Var3_3 | ...
--------------------------------------------------------
EMAIL_ADRESS_NO_4   | Var4_1 | Var4_2 | Var4_3 | ...
--------------------------------------------------------
(and so on)...

Example:

Hello %s,                                             |
You're our %s customer! You're our top %s %% user!!   |
We'd like to thank you for your support.              |
Sincerely,                                            |
Your Agent %s                                         |
-----------------------------------------------------------------------------------------
adressNom1@domain.com                                 | Bob   | 120th   | 2  | Bobby
-----------------------------------------------------------------------------------------
addressNom2@anotherDomain.com                         | Susan | 2nd     | 1  | Rosalie 
-----------------------------------------------------------------------------------------
addressNom3@gmail.com                                 | John  | 90241st | 98 | Rosalie
-----------------------------------------------------------------------------------------

*(Missing variables will be filled with '0')

After loading the file, a list of loaded addresses should appear on the left. These are clickable, and when pushed, they show the preview of the message for the address of choice.
As an example, if you've clicked the 'addressNom2@anotherDomain.com' button, the preview should show this:

Hello Susan,                                             
You're our 2nd customer! You're our top 1 % user!!   
We'd like to thank you for your support.              
Sincerely,                                            
Your Agent Rosalie
