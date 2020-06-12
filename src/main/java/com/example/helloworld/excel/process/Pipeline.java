package com.example.helloworld.excel.process;

import com.example.helloworld.excel.exception.StepException;

public class Pipeline <I,O>{

    private final Step<I, O> current;

    public Pipeline(Step<I, O> current) {
        this.current = current;
    }

    public <NewO> Pipeline<I, NewO> pipe(Step<O, NewO> next) {
        return new Pipeline<>(input -> next.process(current.process(input)));
    }

    public O execute(I input) throws StepException {
        return current.process(input);
    }
}
