package com.exmeple.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.exmeple.batch.model.Employee;
import com.exmeple.batch.task.MyTaskOne;
import com.exmeple.batch.task.MyTaskThree;
import com.exmeple.batch.task.MyTaskTow;
import com.exmeple.batch.utils.ConsoleItemWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

//	@Value("/input/inputData.csv")
	private Resource inputResource=new ClassPathResource("input/inputData.csv");

	// Step 1
	@Bean
	public Step stepOne() {
		return steps.get("stepOne").tasklet(new MyTaskOne())
		// configure StepExecutionListener
//				.listener(new StepResultListener())
				// configure ItemReadListener
				.listener(new StepItemReadListener()).build();
	}

	// Step 2
	@Bean
	public Step stepTow() {
		return steps.get("stepTow").tasklet(new MyTaskTow())
				// configure StepExecutionListener
				.listener(new StepResultListener())
				// configure ItemWriteListener
				.listener(new StepItemWriteListener()).build();
	}

	// Step 3
	@Bean
	public Step stepThree() {
		return steps.get("stepThree")
				// configure StepExecutionListener
				.listener(new StepResultListener()).tasklet(new MyTaskThree()).build();
	}	

//	@Bean
//	public Job jobDemo() {
//		return jobs.get("demoJob").incrementer(new RunIdIncrementer())
//				// configure JobExecutionListener
//				.listener(new JobResultListener()).start(stepOne()).next(stepTow()).next(stepThree()).build();
//	}
	
	
    @Bean
    public Step stepReaderWriter() {
        return steps
                .get("step1")
                .<Employee, Employee>chunk(1)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    //the prossecor
    @Bean
    public ItemProcessor<Employee, Employee> processor() {
        return new ValidationProcessor();
    }
    //The reader 
    @Bean
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> itemReader = new FlatFileItemReader<Employee>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }
    //The mapper
    @Bean
    public LineMapper<Employee> lineMapper() {
        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[] { "id", "firstName", "lastName" });
        lineTokenizer.setIncludedFields(new int[] { 0, 1, 2 });
        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<Employee>();
        fieldSetMapper.setTargetType(Employee.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
 
    @Bean
    public ConsoleItemWriter<Employee> writer() {
        return new ConsoleItemWriter<Employee>();
    }
    
    //the job
    @Bean
    public Job readCSVFilesJob() {
        return jobs
                .get("readCSVFilesJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(stepReaderWriter())
                .next(stepOne())
                .build();
    }
	

}
