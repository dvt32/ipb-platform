# How to use REST services

1. Create / Read / Update / Delete user(s)

a) Create user: HTTP POST request to "http://localhost:8190/users". Example request body (JSON):

{
    "email": "test@ipb.com",
    "password": "123456",
    "matchingPassword": "123456",
    "firstName": "Ivan",
    "lastName": "Ivanov",
    "birthdayInMilliseconds": 123456789,
    "type": "USER"
}

b) Read user(s): 

HTTP GET request to http://localhost:8190/users and http://localhost:8190/users/{userId}

c) Update user: 

HTTP PUT request to http://localhost:8190/users/{userId} with updated user data request body (JSON)

d) Delete user: 

HTTP DELETE request to http://localhost:8190/users/{userId}

README.md

e) Change user privileges

- User to Admin -> HTTP POST request to http://localhost:8190/users/make-user-admin with request param "email" OR "id" (email or ID of an existing user)

- Admin to User -> HTTP POST request to http://localhost:8190/users/make-user-non-admin with request param "email" OR "id" (email or ID of an existing user)