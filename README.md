# Tshirt Web Service

----
## Introduction
This is a soap web service that can be used to list inventory, place an order and track an order for tshirts. It has the following methods:

1. ListInventory
2. OrderTshirt
3. TrackOrder

The following technologies are used:

1. Spring Boot
2. MySQL
3. Docker

----
## Build & Test


1. Install [Docker](https://www.docker.com/community-edition#/download) (Version 17.10+)
2. Clone this repository
3. Run the command (Make sure that the port 80 is not used by any other process in your PC/Mac): 
`docker-compose up`
4. Import the file `tshirt-service-local-soapui-project.xml` in [SOAP-UI](https://www.soapui.org/) to test the service.

You should then see an output like below in the command line:

`tshirt-service_1  | 2018-02-26 12:47:43.508  INFO 1 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8888 (http)`

---
### REST proxy

The modernized REST API is created using Anypoint Studio which can be import using the file `tshirt-service.zip`. A live demo can be found here [http://tshirt.cloudhub.io/inventory](http://tshirt.cloudhub.io/inventory). (Link may expire as the trail period for anypoint is 7 days)


