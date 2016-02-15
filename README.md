# Learning

## Commands to start project
    - `npm install`
    - copy (do not rename) sample-learning.yml to learning.yml in the same directory.
    - Change learning.yml to match your environment.
    - From the *root* learning directory compile : `mvn clean compile` 
    - To start the project : `mvn exec:exec -pl learning-service`
    - Access project using : `http://localhost:8787`
    - Access swagger UI : `http://localhost:8787/learning/swagger`

## Dropwizard - REST - SWAGGER - shiro

### REST
    - @Path("/sample")
    - @GET
    - @POST
    - @PathParam
    - @QueryParam
    - @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    - @FormParam
    - @Produces
    - @Consumes

### Swagger
    - http://docs.swagger.io/swagger-core/apidocs/index.html
    - @Api("/[path]") : Marks a class as a Swagger resource.
    - @ApiOperation("Sample endpoint") : Describes an operation or typically a HTTP method against a specific path.
    - @ApiParam("name") : Adds additional meta-data for operation parameters.

## shiro
    - authcBasic
![shiro authcBasic file](./docs/images/shiro-authcBasic-file.png)
![shiro authcBasic](./docs/images/shiro-authcBasic.png)

## CAS
    - Integrate dw-shiro-cas
        - https://github.com/javajack/dw-shiro-bundle
        - http://clearthehaze.com/2014/09/dropwizard-ssl/
    - Create and import SSL certificate
        - keytool -genkey -alias selfsigned -keyalg RSA -keystore keystore.jks -keysize 2048
        - keytool -export -alias selfsigned -file selfsigned.crt -keystore keystore.jks
        - keytool -import -trustcacerts -alias selfsigned -file selfsigned.crt -keystore <path_to_java>/jre/lib/security/cacerts

Project is created using - https://github.com/rayokota/generator-angular-dropwizard
