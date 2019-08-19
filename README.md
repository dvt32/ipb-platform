# How to use REST services

### 1. Login: 

HTTP POST request to http://localhost:8190/login with body params "username" and "password" where the username is the user's email

---

### 2. Logout: 

HTTP GET request to http://localhost:8190/logout

---

### 3. CRUD user operations (general)

a) Create user: HTTP POST request to "http://localhost:8190/users". Example request body (JSON):

{
    "email": "test@ipb.com",
    "password": "123456",
    "matchingPassword": "123456",
    "firstName": "Ivan",
    "lastName": "Ivanov",
    "birthday": "1969-01-01",
    "type": "USER"
}

b) Read user(s): 

HTTP GET request to http://localhost:8190/users and http://localhost:8190/users/{userId}

c) Update user: 

HTTP PUT request to http://localhost:8190/users/{userId} with updated user data request body (JSON)

d) Delete user: 

HTTP DELETE request to http://localhost:8190/users/{userId}

e) Change user privileges

- User to Admin -> HTTP POST request to http://localhost:8190/users/make-user-admin with request param "email" OR "id" (email or ID of an existing user)

- Admin to User -> HTTP POST request to http://localhost:8190/users/make-user-non-admin with request param "email" OR "id" (email or ID of an existing user)

Note: only a logged-in admin can perform this operation

---

### 4. CRUD user operations (for currently logged-in user)

a) Change password: HTTP POST request to http://localhost:8190/profile/change-password with request params "oldPassword", "newPassword" and "matchingNewPassword"

b) Read user data: HTTP GET request to http://localhost:8190/profile/get-data (returns user data in JSON format, but for security reasons with a blank password) 

c) Update user data: HTTP PUT request to http://localhost:8190/profile/edit-data with request body like shown above

d) Delete user data: HTTP DELETE request to http://localhost:8190/profile (logs out user and deletes him)

---

### 5. Forgot / Reset password

a) HTTP POST request http://localhost:8190/users/forgot-password with request param "email" (the user's email) (Example: http://localhost:8190/users/forgot-password?email=test@test.com)

b) Then an e-mail is sent to the specified address with a password reset link (if such a user exists). 
Use that link and send a POST request to with an added request param "newPassword" and "matchingNewPassword" to the URL. 

(Example: http://localhost:8190/users/reset-password?token=7a6761ac-453e-451a-ab5e-8cda55f84ee3&newPassword=1234567&matchingNewPassword=1234567)

---

### 6. Basic Contact Form (Text Message + 1 Attached File w/ Max Size 1 MB)

Note: files are stored in the "files" folder (contained in the project's main directory)

a) Send submission: HTTP POST request to http://localhost:8190/contact. Example JSON request body:

{
	"message": "I noticed an error on your page..."
}

Note: creating a submission returns a response with a location header containing the newly created submission's ID, which can be used to attach a file to that submission afterwards.

b) Attach file to an existing submission: HTTP POST request to http://localhost:8190/contact/{submissionId}/file with form-data attribute "file" which has the value of a file

c) Retrieve attached file for an existing submission: HTTP GET request to http://localhost:8190/contact/{submissionId}/file

d) Get list of all submissions' data (as JSON array): HTTP GET request to http://localhost:8190/contact/all

e) Get specific submission data (as JSON): HTTP GET request to http://localhost:8190/contact/{submissionId}

f) Delete specific submission: HTTP DELETE request to http://localhost:8190/contact/{submissionId}

---

### 7. Extended Contact Form (Suggest Objects/Events & Accept/Reject Them)

a) Suggest object: HTTP POST request to http://localhost:8190/contact/object-suggestions. Example JSON request body:

{ 
	"name": "My Suggested Object",
	"description": "My suggested object's description...",
	"type": "LANDMARK"
}

b) Suggest event: HTTP POST request to http://localhost:8190/contact/event-suggestions. Example JSON request body:

{ 
	"name": "My Suggested Event",
	"description": "My suggested event's description...",
	"startDate": "10-10-1995 01:00",
	"endDate": "10-10-1996 03:37",
	"cityId": 1
}

Note: "cityId" refers to the ID of an existing city in the database
Note 2: The date format is "dd-MM-yyyy hh:mm".

c) Accept object suggestion: HTTP POST request to http://localhost:8190/contact/object-suggestions/{id}/accept (where "id" refers to an existing object suggestion's id)

d) Accept event suggestion: HTTP POST request to http://localhost:8190/contact/event-suggestions/{id}/accept (where "id" refers to an existing event suggestion's id)

e) Reject object suggestion: HTTP POST request to http://localhost:8190/contact/object-suggestions/{id}/reject (where "id" refers to an existing object suggestion's id)

f) Reject object suggestion w/ Message: HTTP POST request to http://localhost:8190/contact/object-suggestions/{id}/reject?message=Your rejection message here (where "id" refers to an existing object suggestion's id and "message" is the request param rejection message to be included in the notification e-mail)

g) Reject event suggestion: HTTP POST request to http://localhost:8190/contact/event-suggestions/{id}/reject (where "id" refers to an existing event suggestion's id)

h) Reject event suggestion w/ Message: HTTP POST request to http://localhost:8190/contact/event-suggestions/{id}/reject?message=Your rejection message here (where "id" refers to an existing event suggestion's id and "message" is the request param rejection message to be included in the notification e-mail)

i) Retrieve specific object suggestion's data: HTTP GET request to http://localhost:8190/contact/object-suggestions/{id} (where "id" refers to an existing object suggestion's id)

j) Retrieve specific event suggestion's data: HTTP GET request to http://localhost:8190/contact/event-suggestions/{id} (where "id" refers to an existing event suggestion's id)

k) Retrieve all object suggestions' data: HTTP GET request to http://localhost:8190/contact/object-suggestions/all

l) Retrieve all event suggestions' data: HTTP GET request to http://localhost:8190/contact/event-suggestions/all

---

Postman request collection: https://www.getpostman.com/collections/26f5476168d5c306ce2d