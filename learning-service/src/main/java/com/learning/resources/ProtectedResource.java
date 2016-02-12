package com.learning.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/protected")
@Api("/protected")
@Produces(MediaType.TEXT_PLAIN)
public class ProtectedResource {

    /**
     * The SecurityContext's principal will be set by Shiro if its filter intercepted the call.
     */
    @GET
    @ApiOperation("Sample protected")
    public String showSecret(@Context SecurityContext context) {
        if (context.getUserPrincipal()!=null)
        {
            return String.format("Hey there, %s. I know you!",
                context.getUserPrincipal().getName());
        }
        return "Access denied.";
    }
}
