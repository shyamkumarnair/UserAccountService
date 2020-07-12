# User Account Management Service
This is a Spring boot  micro service to manage users account.


Description:
-------------
This service has been developed using Springboot to manage users details. 

Moreover, once if there is any change to user details, respective event will be sent to the topic. 
All subscribers subscribed to this topic will be notified about user details changes.

Users details includes,

1. First name
2. Last name
3. Email-id
4. Password
5. Nick name
6. Country

Following operations are available,

1. Add user
2. Modify user
3. Delete user
4. Get all users
5. Get user by id
6. Get users based on (starting with) first name, last name, country, email id and nick name

Springboot Version
------------------
This service has been developed using springboot version 2.3.1.Release version

Java Version
-------------
Java 8 or above

Maven Version
--------------
This is a maven project and maven plugin is already shipped with springboot 2.3.1 version.

Storage:
--------

This service currently uses inline H2 database as storage

MQ Broker:
----------

This service currently uses Active MQ  5.15.11 version. 

Download it (http://archive.apache.org/dist/activemq/5.15.11/apache-activemq-5.15.11-bin.tar.gz)

Docker:
-------

Following version of docker was used for development. Use the same version for optimal results.

Client: Docker Engine - Community
 Version:           19.03.12

Server: Docker Engine - Community
  Version:          19.03.12
  API version:      1.40 (minimum version 1.12)


Swagger Documentation:
----------------------
Once deploed, the swagger documentation would be available on based on the port and server details.

Swagger URL : http://<Server IP/DNS Name>:<port>/swagger-ui.html
   
Default URL : http://localhost:8080/swagger-ui.html


API Docs:
---------
Api docs would be available on based on the port and server details.

API Url     : http://<Server IP/DNS Name>:<port>/v2/api-docs
   
Default URL : http://localhost:8080/v2/api-docs


Set up:
-------
This is a maven based project and developed using Java 8 and Springboot.

After cloning the source files from github, this could be directly imported to your eclipse or STS (Spring tool suite) as maven project.

Install and bring up active mq broker,

Download it (http://archive.apache.org/dist/activemq/5.15.11/apache-activemq-5.15.11-bin.tar.gz) and follow the installation steps


Set up using Docker:
--------------------

To build and run Active MQ container individually,

1) sudo docker build -f ./Dockerfile -t skp/activemq:5.15 .
2) sudo docker run --name myactivemq -d skp/activemq:5.15


To build and run User Account Management Service container individually,

1) sudo docker build  -t skp/usrmgmtsrvc:1.0 .
2) sudo docker run --name usrmgmtsrvc -p 8080:8080  skp/usrmgmtsrvc:1.0

Using Docker-compose:
---------------------

Please install docker-compose if it is not already installed. 

Please follow the documentation for installtion (https://docs.docker.com/compose/install/)

Once successfully installed, please execute the following command

1) sudo docker-compose up

      or
      
2) sudo docker-compose up -d 

To run the containers in background


How to check the service up and running
---------------------------------------

Execute the following command

curl 'http://localhost:8080/users/'

Expected result:

{"status":"OK","message":"success","result":[{"id":1,"firstName":"firstName1","lastName":"LastName1","nickName":"nickName1","p
assword":"password1","email":"email1","country":"UK"},{"id":2,"firstName":"firstName2","lastName":"LastName2","nickName":"nick
Name2","password":"password2","email":"email2","country":"UK"},{"id":3,"firstName":"firstName3","lastName":"LastName3","nickNa
me":"nickName3","password":"password3","email":"email3","country":"UK"},{"id":4,"firstName":"firstName4","lastName":"LastName4
","nickName":"nickName4","password":"password4","email":"email4","country":"UK"}]}



Future Enhancements:
--------------------

1. Implement Spring security and JWT to protect the service from unauthorized accessing
2. Implement cache to optimize the search performance
3. To replace the in-memory database H2 to a permenant persistance like Mysql or Postgres
4. To use Bcrypt for improved encrypt and decrypt of user password


Thank you.
