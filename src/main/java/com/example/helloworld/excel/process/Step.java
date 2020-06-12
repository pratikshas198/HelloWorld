package com.example.helloworld.excel.process;

import com.example.helloworld.excel.exception.StepException;

public interface Step <I,O>{
    O process(I input) throws StepException;
}
