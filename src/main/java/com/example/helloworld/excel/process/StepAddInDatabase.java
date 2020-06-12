package com.example.helloworld.excel.process;

import com.example.helloworld.excel.dao.PersonDAO;
import com.example.helloworld.excel.entity.Person;
import com.example.helloworld.excel.exception.StepException;
import io.dropwizard.hibernate.UnitOfWork;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class StepAddInDatabase implements Step<Row,String>{

    private final PersonDAO personDAO;

    public StepAddInDatabase(PersonDAO dao){
        this.personDAO = dao;
    }

    @UnitOfWork
    @Override
    public String process(Row input) throws StepException {
        int id;
        try {
            String name = input.getCell(0).getStringCellValue();
            String address = input.getCell(1).getStringCellValue();
            int mobile = (int) input.getCell(2).getNumericCellValue();
            double salary = (double) input.getCell(3).getNumericCellValue();

            Person person = new Person(name, address, mobile, salary);
            System.out.println(person.toString());

            id = personDAO.create(person);
        }catch (Exception e){
            throw new StepException("Error while database insertion");
        }

        return "returned from step 3 : addInDatabase - " + id;
    }
}
