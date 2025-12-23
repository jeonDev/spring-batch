package com.service.batch.common.service;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class JobService {
    private final JobOperator jobOperator;
    private final ApplicationContext applicationContext;

    public JobService(JobOperator jobOperator, ApplicationContext applicationContext) {
        this.jobOperator = jobOperator;
        this.applicationContext = applicationContext;
    }

    public void execute(String jobName, String jobParameter) throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        Job job = applicationContext.getBean(jobName, Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("date", jobParameter, String.class)
                .toJobParameters();
        jobOperator.start(job, jobParameters);
    }
}
