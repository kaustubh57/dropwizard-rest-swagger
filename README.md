# Learning

## Commands
    - `npm install`
    - To start the project : `mvn compile exec:exec -pl learning-service`

## Dropwizard - REST - SWAGGER

### REST
    - @Path("/sample")
    - @GET
    - @POST
    - @PathParam
    - @QueryParam
    - @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    - @FormParam

### Swagger
    - http://docs.swagger.io/swagger-core/apidocs/index.html
    - @Api("/[path]") : Marks a class as a Swagger resource.
    - @ApiOperation("Sample endpoint") : Describes an operation or typically a HTTP method against a specific path.
    - @ApiParam("name") : Adds additional meta-data for operation parameters.

Project is created using - https://github.com/rayokota/generator-angular-dropwizard
