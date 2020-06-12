package com.example.helloworld.excel.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelProcessingExceptionMapper implements ExceptionMapper<ExcelProcessingException> {
    @Override
    public Response toResponse(ExcelProcessingException e) {

        Response response;
        if(!e.getErrorMessages().isEmpty()){
            Map<String, List<String>> errorMap = new HashMap<>() ;
           errorMap.put("errors",e.getErrorMessages());
           response =  Response.status(e.getCode())
                    .entity(errorMap)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        }else {
            Map<String,String> errorMap = new HashMap<>() ;
            errorMap.put("errors",e.getMessage());
            response =  Response.status(e.getCode())
                    .entity(errorMap)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        }

        return response;
    }
}
