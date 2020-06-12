package com.example.helloworld.excel.process;

import com.example.helloworld.excel.exception.StepException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.regex.Pattern;

public class StepValidate implements Step<Row,Row>{

    @Override
    public Row process(Row input) throws StepException {
        System.out.println("Step 2 : validate");
        Cell cell;

        /*
            to do validation for name and address
         */

        cell = input.getCell(2);
        String mobile="";
        if(cell.getCellTypeEnum() == CellType.NUMERIC){
            double temp = cell.getNumericCellValue();
            mobile = String.valueOf(temp);
        }else if(cell.getCellTypeEnum() == CellType.STRING){
            mobile = cell.getStringCellValue();
        }
        if(mobile.length() == 12 && mobile.substring(0,2).equals("91")){
            mobile = mobile.substring(2);
            double number = Double.parseDouble(mobile);
            if(number >= 6000000000.0 && number <= 9999999999.0){
                cell.setCellValue(number); //note: setCellValue doesn't have int set method
            }else{
                throw new StepException("cell value not valid");
            }
        }

        cell = input.getCell(3);
        if(cell.getCellTypeEnum() == CellType.NUMERIC){
            Double salary = cell.getNumericCellValue();
            if(salary > 0){
                cell.setCellValue(salary);
            }else{
                throw new StepException("cell value not valid");
            }
        }

        return input;
    }
}
