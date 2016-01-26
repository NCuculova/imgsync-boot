# imgsync-boot
Simple service for storing and organizing photos using Spring Boot and OAuth2  


DemoGallery is Android mobile app in a role of client for the OAuth2 provider spring-boot app(demo project).
The user can sing in/up from the mobile app using his credentials (username, password) or he can choose Facebook login.
Providing correct credentials means that authorization server implemented in the web app will issue you with an access token.
Than the mobile app allows access to all protected images stored on the server on behalf of the user.
