package com.exmeple.batch.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
/**
 * JobExecutionListener
 *
 */
public class JobResultListener implements JobExecutionListener {
	 
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Called ####  beforeJob().");
    }
 
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Called ####  afterJob().");
    }
}