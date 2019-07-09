-- Insert SYSADMIN

INSERT INTO Users (id, email, password, matching_password, first_name, last_name, birthday_in_milliseconds, type)
VALUES (
	10001,
	'admin@ipb.com',
	--The password is 'mse2018ipb' encrypted with BCrypt @ https://www.browserling.com/tools/bcrypt 
	--('mse2018ipb' can be used for login)
	'$2a$10$n8rwMAHjSbmuI8M7ckPnNe6sp0.Vq3U2x12uHK9gExuax.geO1WCa',
	'$2a$10$n8rwMAHjSbmuI8M7ckPnNe6sp0.Vq3U2x12uHK9gExuax.geO1WCa', -- confirm password
	'SYS',
	'ADMIN',
	123456789, 
	'ADMIN'
);

-- Insert test user

INSERT INTO Users (id, email, password, matching_password, first_name, last_name, birthday_in_milliseconds, type)
VALUES (
	10002,
	'user@ipb.com',
	--The password is '123456' encrypted with BCrypt @ https://www.browserling.com/tools/bcrypt 
	--('123456' can be used for login)
	'$2a$10$HsDw5/0ENEeQB2CMRsvEweumk3cPKwW9dl5UVqPxOANXCJ//sGJiS',
	'$2a$10$HsDw5/0ENEeQB2CMRsvEweumk3cPKwW9dl5UVqPxOANXCJ//sGJiS', -- confirm password
	'Ivan',
	'Ivanov',
	123456789, 
	'USER'
);