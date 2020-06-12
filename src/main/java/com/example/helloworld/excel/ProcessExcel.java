package com.example.helloworld.excel;

import com.example.helloworld.HelloWorldConfiguration;
import com.example.helloworld.excel.dao.PersonDAO;
import com.example.helloworld.excel.exception.StepException;
import com.example.helloworld.excel.process.Pipeline;
import com.example.helloworld.excel.process.StepAddInDatabase;
import com.example.helloworld.excel.process.StepSanitize;
import com.example.helloworld.excel.process.StepValidate;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessExcel implements Runnable{

    private static final int CONSUMER_COUNT = 3;
    private final static BlockingQueue<Row> rowsReadQueue = new ArrayBlockingQueue<Row>(30);

    private boolean isConsumer = false;
    private static boolean producerIsDone = false;

    private final PersonDAO personDAO;
    private final HibernateBundle<HelloWorldConfiguration> hibernate;

    private List<String> errorMessages = new ArrayList<String>();

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public ProcessExcel(PersonDAO dao,HibernateBundle<HelloWorldConfiguration> hibernate){
        this.personDAO = dao;
        this.hibernate = hibernate;
    }

    public ProcessExcel(boolean consumer, PersonDAO personDAO, HibernateBundle<HelloWorldConfiguration> hibernate) {
        this.isConsumer = consumer;
        this.personDAO = personDAO;
        this.hibernate = hibernate;
    }

    public void startProcess(){

        ExecutorService producerPool = Executors.newFixedThreadPool(1);
        producerPool.submit(new ProcessExcel(false, personDAO,hibernate)); // run method is called

        // create a pool of consumer threads to parse the lines read
        ExecutorService consumerPool = Executors.newFixedThreadPool(CONSUMER_COUNT);
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumerPool.submit(new ProcessExcel(true, personDAO,hibernate)); // run method is called
        }

        producerPool.shutdown();
        consumerPool.shutdown();
    }

    @Override
    public void run() {
        if (isConsumer) {
            consume();
        } else {
            readFile(); //produce data by reading a file
        }
    }

    private void readFile() {
        try
        {
            File file = new File("C:/Users/Pratiksha Shekhawat/Desktop/sample.xlsx");
            FileInputStream fis = new FileInputStream(file);

            XSSFWorkbook workBook = new XSSFWorkbook (fis);
            XSSFSheet sheet = workBook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowsReadQueue.put(row);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        producerIsDone = true; // signal consumer
        System.out.println(Thread.currentThread().getName() + " producer is done");
    }

    private void consume() {
        try {

            StepAddInDatabase addInDatabaseStep = new UnitOfWorkAwareProxyFactory(hibernate)
                    .create(StepAddInDatabase.class, PersonDAO.class, personDAO);

            Pipeline<Row,String> pipeline = new Pipeline<>(new StepSanitize())
                    .pipe(new StepValidate())
                    .pipe(addInDatabaseStep);
            while (!producerIsDone || (producerIsDone && !rowsReadQueue.isEmpty())) {
                Row rowToProcess = rowsReadQueue.take();
                try {
                    System.out.println(pipeline.execute(rowToProcess));
                }catch (Exception e){
                    errorMessages.add(e.getMessage());
                } catch (StepException e) {
                    errorMessages.add(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " consumer is done");
    }

}
