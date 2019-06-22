# How to use REST services

1. Login: 

HTTP POST request to http://localhost:8190/login 
with body params "username" and "password" 
where the username is the user's email

2. Logout: 

HTTP GET request to http://localhost:8190/logout

3. Change password (after login): 

HTTP POST request to http://localhost:8190/profile/change-password 
with request params "oldPassword", "newPassword" and "matchingNewPassword"

4. Forgot / Reset password

a) HTTP POST request http://localhost:8190/users/forgot-password 
with request param "email" (the user's email)
(Example: http://localhost:8190/users/forgot-password?email=test@test.com)

b) Then an e-mail is sent to the specified address with a password reset link.
Use that link and send a POST request to with an added request param "newPassword" to the URL.
(Example: http://localhost:8190/users/reset-password?token=7a6761ac-453e-451a-ab5e-8cda55f84ee3)

5. Change user privileges

a) User to Admin -> HTTP POST request to http://localhost:8190/users/make-user-admin with request param "email" OR "id" (email or ID of an existing user)

b) Admin to User -> HTTP POST request to http://localhost:8190/users/make-user-non-admin with request param "email" OR "id" (email or ID of an existing user)

Note: only a logged-in admin can perform this operation

6. View / Edit / Delete Profile (after login)

View: HTTP GET request to http://localhost:8190/profile/get-data (returns user data in JSON format, but for security reasons with a blank password)
Edit: HTTP PUT request to http://localhost:8190/profile/edit-data with request body like shown below
Delete: HTTP DELETE request to http://localhost:8190/profile (logs out user and deletes him)

---

Insert user by sending POST request to "http://localhost:8190/users". Example request body (JSON):

{
    "email": "test@ipb.com",
    "password": "123456",
    "matchingPassword": "123456",
    "firstName": "Ivan",
    "lastName: "Ivanov",
    "birthdayInMilliseconds": 123456789,
    "type": "USER"
}

Protected resource for testing (accessible only after login): http://localhost:8190/catalog