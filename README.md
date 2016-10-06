# shoppingList
A shopping list with a RESTful API and an Angular 1 Frontend supported by Angular-Material.
This application allows you to manage your shopping lists and share them with your friends, roommates, ..

# Requirements

* Java 8
* Maven 3

# Run the application
Clone the repository: 

**With HTTPS:**
`git clone https://github.com/Yannic92/shoppingList.git`

**With SSH:**
`git clone git@github.com:Yannic92/shoppingList.git`

Have a look at the `Configuration - Mailing` section.

```
cd shoppingList
mvn spring-boot:run
```

# Deploy the application

Have a look at the `Configuration - Mailing` section.

First package the app to a JAR-file
```
mvn clean package
```

Then move the JAR-file inside the `target` directory to the place where it should be deployed.
This JAR-file can be executed:
```
java -jar $name_of_the_jar_file
```
If you want to add custom configuration you can do this [like explained in the Spring Boot Documentation.](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html "Spring Boot Externalized Configuration")

# Configuration
## Database
By default H2 database is used. If you want to use a mysql for example the application.properties should provide the following properties:
``` properties
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/mySchema
spring.datasource.username=YourUser
spring.datasource.password=YourPassword
```
## Mailing (Required)
You have to configure the mail properties to make the shopping list work properly. Use the following properties:
``` properties
shopping-list.mail.host=Your SMTP server
shopping-list.mail.port=Your Port
shopping-list.mail.properties.mail.smtp.starttls.enable = true/false
shopping-list.mail.properties.mail.smtp.starttls.required = true/false
shopping-list.mail.properties.mail.smtp.auth = true/false
shopping-list.mail.confirmation-mail-address= email address where confirmation mails should be sent
shopping-list.mail.confirmation-mail-password=your password
```
