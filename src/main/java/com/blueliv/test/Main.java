/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test;

import com.blueliv.test.exception.InvalidDataLineException;
import com.blueliv.test.exception.InvalidFilterTypeException;
import com.blueliv.test.exception.UnknownFormatLineException;
import com.blueliv.test.model.DataLine;
import com.blueliv.test.model.DataLineFormat;
import com.blueliv.test.model.FilterType;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import lombok.extern.java.Log;

/**
 *
 * @author bruno
 */
@Log
public class Main {

    private static BlockingQueue<String> DATA_QUEUE;
    private static int DATA_QUEUE_INITIAL_SIZE = 100;
    private static int FILTERED_VALUE_SET_INITIAL_SIZE = 100;
    private static int DATA_PROCESSOR_COUNT = 1;
    private static final String POISON = "EXIT";

    public static void main(String[] args) {

	String dataProcessorCount = System.getProperty("data.processor.count");
	String dataQueueInitialSize = System.getProperty("data.queue.initialsize");
	String filteredValueSetInitialSize = System.getProperty("filtered.value.set.initial.size");

	if (dataProcessorCount != null) {
	    try {
		DATA_PROCESSOR_COUNT = Integer.parseInt(dataProcessorCount);
	    } catch (NumberFormatException ex) {
		//use default value
	    }
	}

	if (dataQueueInitialSize != null) {
	    try {
		DATA_QUEUE_INITIAL_SIZE = Integer.parseInt(dataQueueInitialSize);
	    } catch (NumberFormatException ex) {
		//use default value
	    }
	}

	if (filteredValueSetInitialSize != null) {
	    try {
		FILTERED_VALUE_SET_INITIAL_SIZE = Integer.parseInt(filteredValueSetInitialSize);
	    } catch (NumberFormatException ex) {
		//use default value
	    }
	}

	DATA_QUEUE = new LinkedBlockingQueue<String>(DATA_QUEUE_INITIAL_SIZE);

	try {

	    String path;
	    FilterType filterType;
	    String filterValue;

	    if (args.length != 3) {
		System.out.println("Invalid command error. You have to use: \n"+	
			"'java -jar application.jar input.txt CITY [CITY_NAME]' or \n"+ 
			"'java -jar application.jar input.txt ID [PERSON_ID]' or \n");
		return;
	    }

	    path = args[0];
	    filterType = FilterType.parse(args[1]);
	    filterValue = args[2];

	    FileInputStream fio = new FileInputStream(path);

	    ExecutorService dataLoaderPool = Executors.newFixedThreadPool(1);
	    CountDownLatch dataLoaderPoolLatch = new CountDownLatch(1);
	    dataLoaderPool.submit(new DataLoader(dataLoaderPoolLatch, fio));

	    ExecutorService dataProcessorPool = Executors.newFixedThreadPool(DATA_PROCESSOR_COUNT);
	    CountDownLatch dataProcessorPoolLatch = new CountDownLatch(DATA_PROCESSOR_COUNT);
	    for (int i = 0; i < DATA_PROCESSOR_COUNT; i++) {
		dataProcessorPool.submit(new DataProcessor(dataProcessorPoolLatch, filterType, filterValue));
	    }

	    dataProcessorPoolLatch.await();
	    dataLoaderPoolLatch.await();
	    
	    dataProcessorPool.shutdown();
	    dataLoaderPool.shutdown();

	} catch (InvalidFilterTypeException ex) {
	    log.log(Level.SEVERE, null, ex);
	} catch (InterruptedException ex) {
	    log.log(Level.SEVERE, null, ex);
	} catch (FileNotFoundException ex) {
	    log.log(Level.SEVERE, null, ex);
	}
    }

    static class DataLoader implements Runnable {

	private final FileInputStream fio;
	private final CountDownLatch latch;

	public DataLoader(CountDownLatch latch, FileInputStream fio) {
	    this.latch = latch;
	    this.fio = fio;
	}

	@Override
	public void run() {

	    DataLineFormat format = DataLineFormat.F1;
	    Scanner sc = null;
	    try {
		sc = new Scanner(fio, "UTF-8");
		while (sc.hasNextLine()) {
		    String line = sc.nextLine();
		    if (line.startsWith("F")) {
			try {
			    format = DataLineFormat.parse(line);
			} catch (UnknownFormatLineException ex) {
			   //  log.log(Level.SEVERE, null, ex); // ignore error and continue
			}
		    } else if (line.startsWith("D")) {
			line = line.replaceFirst("D ", format.name + format.separator);
			DATA_QUEUE.put(line);
		    }
		}
		if (sc.ioException() != null) {
		    throw sc.ioException();
		}

		//put POISON
		for (int k = 0; k < DATA_PROCESSOR_COUNT; k++) {
		    DATA_QUEUE.put(POISON);
		}

	    } catch (FileNotFoundException ex) {
		log.log(Level.SEVERE, null, ex);
	    } catch (IOException ex) {
		log.log(Level.SEVERE, null, ex);
	    } catch (InterruptedException ex) {
		Thread.currentThread().interrupt();
	    } finally {
		if (fio != null) {
		    try {
			fio.close();
		    } catch (IOException ex) {
			log.log(Level.SEVERE, null, ex);
		    }
		}
		if (sc != null) {
		    sc.close();
		}
		latch.countDown();
	    }
	}

    }

    static class DataProcessor implements Runnable {

	private static final Set<String> FILTERED_VALUE_SET = ConcurrentHashMap.newKeySet(FILTERED_VALUE_SET_INITIAL_SIZE);
	private static final Semaphore DATA_PROCESSOR_MUTEX = new Semaphore(1);

	private final CountDownLatch latch;
	private final FilterType filterType;
	private final String filterValue;

	DataProcessor(CountDownLatch latch, FilterType filterType, String filterValue) {
	    this.filterType = filterType;
	    this.filterValue = filterValue;
	    this.latch = latch;
	}

	@Override
	public void run() {

	    try {
		// wait(); //wait dataloader signal
		while (true) {

		    try {
			String line = DATA_QUEUE.take();

			if (POISON.equals(line)) {
			    break;
			}

//		    System.out.println("-> Process: "+ line +" DATA_QUEUE.size() "+ DATA_QUEUE.size());
			DataLine data = DataLine.parse(line);

			if (filterType.equals(FilterType.CITY)) {
			    DATA_PROCESSOR_MUTEX.acquire();
			    if (data.getCityName().equals(filterValue) && !FILTERED_VALUE_SET.contains(data.getPersonId())) {
				System.out.println(data.getPersonCompleteName() + "," + data.getPersonId());
				FILTERED_VALUE_SET.add(data.getPersonId());
			    }
			    DATA_PROCESSOR_MUTEX.release();
			} else {
			    DATA_PROCESSOR_MUTEX.acquire();
			    if (data.getPersonId().equals(filterValue) && !FILTERED_VALUE_SET.contains(data.getCityName())) {
				System.out.println(data.getCityName());
				FILTERED_VALUE_SET.add(data.getCityName());
			    }
			    DATA_PROCESSOR_MUTEX.release();
			}

		    } catch (InvalidDataLineException ex) {
			//log.log(Level.SEVERE, ex.getMessage(), ex); Ignore Line
		    }

		}
	    } catch (InterruptedException ex) {
		Thread.currentThread().interrupt();
	    } finally {
		latch.countDown();
	    }

	}

    }
}
