package com.service.batch.database;

import com.service.batch.database.domain.DatabaseData;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
public class DatabaseJobConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;

    public DatabaseJobConfig(JobRepository jobRepository,
                             EntityManagerFactory entityManagerFactory,
                             PlatformTransactionManager transactionManager
    ) {
        this.jobRepository = jobRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job databaseJob() {
        return new JobBuilder("databaseJob", jobRepository)
                .start(databaseStep())
                .build();
    }

    @Bean
    @JobScope
    public Step databaseStep() {
        return new StepBuilder("databaseStep", jobRepository)
                .<DatabaseData, DatabaseData>chunk(10)
                .transactionManager(transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<DatabaseData> itemReader() {
        return new JpaPagingItemReaderBuilder<DatabaseData>()
                .name("databaseStepItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT DD FROM DatabaseData DD")
                .transacted(false)
                .build();
    }

    private ItemProcessor<DatabaseData, DatabaseData> itemProcessor() {
        return item -> {
            item.updateDataA();

            return item;
        };
    }

    private ItemWriter<DatabaseData> itemWriter() {
        return new JpaItemWriterBuilder<DatabaseData>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }


}
