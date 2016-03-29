package com.learning.resources;

import com.learning.model.HTTPMethod;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Created by kaustubh on 3/28/16.
 */
@Path("/httpmethod")
@Api("/httpmethod")
@Slf4j
public class HTTPMethodResource {

    @ApiOperation(value = "Create a http method", response = HTTPMethod.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful request")})
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public HTTPMethod create(final HTTPMethod entity) {
        entity.setId(99999L);
        entity.setDate(new Date().toString());
        return entity;
    }

    @ApiOperation(value = "Get a http method", response = HTTPMethod.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful request")})
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public HTTPMethod get(@PathParam("id") final LongParam id) {
        HTTPMethod getMethod = new HTTPMethod();
        getMethod.setId(id.get());
        getMethod.setName("GET method");
        getMethod.setDate(new Date().toString());
        return getMethod;
    }

    @ApiOperation(value = "Delete a http method by id")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Successful request")})
    @DELETE
    @Path("{id}")
    @UnitOfWork
    public HTTPMethod delete(@PathParam("id") final LongParam id) {
        HTTPMethod deleteMethod = new HTTPMethod();
        deleteMethod.setId(id.get());
        deleteMethod.setName("DELETE method");
        deleteMethod.setDate(new Date().toString());
        return deleteMethod;
    }

    @ApiOperation(value = "Update the http method", response = HTTPMethod.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful request")})
    @PUT
    @Path("{id}")
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public HTTPMethod update(@PathParam("id") final LongParam id, final HTTPMethod entity) {
        HTTPMethod putMethod = new HTTPMethod();
        putMethod.setId(id.get());
        putMethod.setName("PUT method");
        putMethod.setDate(new Date().toString());
        return putMethod;
    }

}
