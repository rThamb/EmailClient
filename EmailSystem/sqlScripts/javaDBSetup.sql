DROP TABLE IF EXISTS EmailSys_Attachments;
DROP TABLE IF EXISTS EmailSys_Emails; 
DROP TABLE IF EXISTS EmailSys_Folders;  
DROP TABLE IF EXISTS EmailSys_Accounts; 


CREATE TABLE EmailSys_Accounts (

	emailAddress VARCHAR(255) PRIMARY KEY, 
	userName VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE EmailSys_Folders(

	id int(5) PRIMARY KEY AUTO_INCREMENT, 
	folder_name VARCHAR(50),
	
	owner VARCHAR(255), 
	FOREIGN KEY (owner) REFERENCES EmailSys_Accounts(emailAddress) ON DELETE CASCADE
);


CREATE TABLE EmailSys_Emails(

	id int(5) PRIMARY KEY AUTO_INCREMENT, 
	person_to VARCHAR(255), 
	person_from VARCHAR(255) NOT NULL,
	cc VARCHAR(510), 
	subject VARCHAR(255) NOT NULL, 
	text LONGTEXT, 
	html LONGTEXT,
	
	receivedDate DATE,  
	
	folder_id int(5), 
	FOREIGN KEY (folder_id) REFERENCES EmailSys_Folders(id)	ON DELETE CASCADE
);



CREATE TABLE EmailSys_Attachments(

	id int(10) PRIMARY KEY AUTO_INCREMENT,
	emailID int(5),
	file_data BLOB,
	name_file VARCHAR(255), 

	FOREIGN KEY (emailID) REFERENCES EmailSys_Emails(id) ON DELETE CASCADE
);