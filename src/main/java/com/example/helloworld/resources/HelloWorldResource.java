package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.HelloWorldConfiguration;
import com.example.helloworld.api.Saying;
import com.example.helloworld.excel.ProcessExcel;
import com.example.helloworld.excel.dao.PersonDAO;
import com.example.helloworld.excel.exception.ExcelProcessingException;
import io.dropwizard.hibernate.HibernateBundle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private final PersonDAO personDAO;
    private final HibernateBundle<HelloWorldConfiguration> hibernate;

    public HelloWorldResource(String template, String defaultName, PersonDAO dao, HibernateBundle<HelloWorldConfiguration> hibernate) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
        this.personDAO = dao;
        this.hibernate = hibernate;
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }

    @GET
    @Path("/excel")
    @Timed
    public Response processExcel(@QueryParam("name") Optional<String> name) throws ExcelProcessingException{
        final String value = String.format(template, name.orElse(defaultName));

        ProcessExcel pe = new ProcessExcel(personDAO,hibernate);
        pe.startProcess();

        if(!pe.getErrorMessages().isEmpty()) {
            final ExcelProcessingException exception = new ExcelProcessingException(404, pe.getErrorMessages());
            throw exception;
        }

        return Response.ok("ok").build();
    }
}
