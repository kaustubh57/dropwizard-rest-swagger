package com.learning.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/loginCheck")
@Api("/loginCheck")
@Produces({MediaType.TEXT_PLAIN})
public class ShiroLoginCheck {

    @GET
    @ApiOperation("Sample login check")
    public String isLoggedIn()
    {
        final Subject s = SecurityUtils.getSubject();
        if (s != null && s.isAuthenticated()) {
            return String.format("Logged in as '%s'.", s.getPrincipal());
        } else {
            return "Not logged in.";
        }
    }
}
