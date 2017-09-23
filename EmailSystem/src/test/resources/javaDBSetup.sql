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
	
	receivedDate TIMESTAMP,  
	
	folder_id int(5), 
	FOREIGN KEY (folder_id) REFERENCES EmailSys_Folders(id)	ON DELETE CASCADE
);



CREATE TABLE EmailSys_Attachments(

	id int(10) PRIMARY KEY AUTO_INCREMENT,
	emailID int(5),
	file_data MEDIUMBLOB,
	name_file VARCHAR(255), 

	FOREIGN KEY (emailID) REFERENCES EmailSys_Emails(id) ON DELETE CASCADE
);



insert into emailsys_accounts values("sp33dycow86@gmail.com", "Jim", "Tester11");
insert into emailsys_folders(folder_name,owner) values("sent","sp33dycow86@gmail.com");
insert into emailsys_folders(folder_name,owner) values("extras","sp33dycow86@gmail.com");
INSERT INTO EmailSys_Emails(person_to, person_from, cc, subject, text, html, folder_id)
VALUES('hjs@gmail.com', 'sp33dycow86@gmail.com', '', 'hello', 'I am someone', '',1);