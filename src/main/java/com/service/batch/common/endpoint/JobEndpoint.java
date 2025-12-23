package com.service.batch.common.endpoint;

import com.service.batch.common.service.JobService;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobEndpoint {

    private final JobService jobService;

    public JobEndpoint(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/api/batch/{jobName}/{jobParameter}")
    public ResponseEntity<String> jobExecute(@PathVariable("jobName") String jobName,
                                             @PathVariable("jobParameter") String jobParameter) throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        jobService.execute(jobName, jobParameter);
        return ResponseEntity.ok("Ok");
    }
}
