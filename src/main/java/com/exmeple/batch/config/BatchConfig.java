package com.exmeple.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.exmeple.batch.task.MyTaskOne;
import com.exmeple.batch.task.MyTaskThree;
import com.exmeple.batch.task.MyTaskTow;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	// Step 1
	@Bean
	public Step stepOne() {
		return steps.get("stepOne")
				.tasklet(new MyTaskOne())
				//configure StepExecutionListener
				.listener(new StepResultListener())
				.build();
	}

	// Step 2
	@Bean
	public Step stepTow() {
		return steps.get("stepTow")
				.tasklet(new MyTaskTow())
				//configure StepExecutionListener
				.listener(new StepResultListener())
				.build();
	}

	// Step 3
	@Bean
	public Step stepThree() {
		return steps.get("stepThree")
				//configure StepExecutionListener
				.listener(new StepResultListener())
				.tasklet(new MyTaskThree())
				.build();
	}

	@Bean
	public Job jobDemo() {
		return jobs.get("demoJob")
				.incrementer(new RunIdIncrementer())
				//configure JobExecutionListener
				.listener(new JobResultListener())
				.start(stepOne())
				.next(stepTow())
				.next(stepThree())
				.build();
	}

}
