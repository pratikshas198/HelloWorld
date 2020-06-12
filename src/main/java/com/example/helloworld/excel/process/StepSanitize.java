package com.example.helloworld.excel.process;

import com.example.helloworld.excel.exception.StepException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.regex.Pattern;

public class StepSanitize implements Step<Row, Row>{

    @Override
    public Row process(Row input) throws StepException{
        System.out.println("step 1 : sanitize");
        Cell cell;

        cell = input.getCell(0);
        String name = cell.getStringCellValue();
        name = name.trim().replaceAll(" +"," ");//removing extra whitespaces
        cell.setCellValue(name);

        cell = input.getCell(1);
        String address = cell.getStringCellValue();
        cell.setCellValue(address.trim().replaceAll(" +"," "));//removing extra whitespaces

        cell = input.getCell(2);
        //if mobile number contains special chars , cell type is String
        //if cell type is numeric , it can be validated in next step
        if(cell.getCellTypeEnum() == CellType.STRING){
            String mobile = cell.getStringCellValue();
            mobile = mobile.replaceAll("[\\+\\- +]", "");//replaces '+','-',white space
            if(Pattern.matches("\\d+", mobile)){
                cell.setCellValue(mobile);
            }else{
                throw new StepException("cell value incorrect");
            }
        }

        cell = input.getCell(3);
        //if special char "," in salary remove it
        if(cell.getCellTypeEnum() == CellType.STRING){
            String salary = cell.getStringCellValue();
            salary = salary.replaceAll(",", "");
            if(Pattern.matches("[\\d]+.[\\d]+",salary) || Pattern.matches("[\\d]+",salary)){
                cell.setCellValue(Double.parseDouble(salary));
            }else {
                throw new StepException("cell value incorrect");
            }
        }

        return input;
    }
}
