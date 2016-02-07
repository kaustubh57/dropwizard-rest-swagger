package com.learning.resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.learning.config.LearningConfiguration;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import io.dropwizard.logging.FileAppenderFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ReversedLinesFileReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;

import com.wordnik.swagger.annotations.Api;

@Singleton
@Path("/logs")
@Api("/logs")
@Slf4j
public class LogResource {

    @Inject
    private LearningConfiguration configuration;

    @ApiOperation(value = "Get last n lines of log from the log file",
                  response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200,
                                         message = "Successful request") })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public String get(
                    @QueryParam("lines")
                    final LongParam lines) throws IOException {

        FileAppenderFactory types = (FileAppenderFactory) configuration.getLoggingFactory().getAppenders().get(1);

        File file = new File(types.getCurrentLogFilename());
        long numberOfLines = 2000;
        long counter = 0L;
        String line;
        if (lines != null) {
            numberOfLines = lines.get();
        }
        StringBuilder logString = new StringBuilder();
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file)) {
            while (((line = reader.readLine()) != null) && counter < numberOfLines) {
                logString.insert(0, "\n" + line);
                counter++;
            }
        } catch (IOException ioe) {
            throw ioe;
        }

        return logString.toString();
    }
}
