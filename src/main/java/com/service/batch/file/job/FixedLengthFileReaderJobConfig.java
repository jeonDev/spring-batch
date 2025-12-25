package com.service.batch.file.job;

import com.service.batch.common.telegram.FixedLengthTelegramLineMapperBuilder;
import com.service.batch.common.telegram.Telegram;
import com.service.batch.file.job.telegram.FixedLengthDataTelegram;
import com.service.batch.file.job.telegram.FixedLengthHeaderTelegram;
import com.service.batch.file.job.telegram.FixedLengthTrailerTelegram;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
public class FixedLengthFileReaderJobConfig {

    private final JobRepository jobRepository;

    public FixedLengthFileReaderJobConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Bean
    public Job fixedLengthFileReaderJob() {
        return new JobBuilder("fixedLengthFileReaderJob", jobRepository)
                .start(fixedLengthFileReaderStep())
                .build();
    }

    @Bean
    @JobScope
    public Step fixedLengthFileReaderStep() {
        return new StepBuilder("fixedLengthFileReaderStep", jobRepository)
                .<Telegram, Void>chunk(1000)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(chunk -> {

                })
                .build();

    }

    private ItemReader<Telegram> itemReader() {
        var lineMapper = new FixedLengthTelegramLineMapperBuilder<>()
                .patternType("H*", FixedLengthHeaderTelegram.class)
                .patternType("D*", FixedLengthDataTelegram.class)
                .patternType("T*", FixedLengthTrailerTelegram.class)
                .build();

        return new FlatFileItemReaderBuilder<Telegram>()
                .name("fixedLengthFileReaderItemReader")
                .resource(new FileSystemResource("/Users/dev/batch/TEST_FILE"))
                .encoding("UTF-8")
                .lineMapper(lineMapper)
                .linesToSkip(0)
                .build();
    }

    private ItemProcessor<Telegram, Void> itemProcessor() {
        return item -> {

            if (item instanceof FixedLengthDataTelegram request) {
                this.dataProcessor(request);
            } else if (item instanceof FixedLengthHeaderTelegram request) {
                this.headerProcessor(request);
            } else if (item instanceof FixedLengthTrailerTelegram request) {
                this.trailerProcessor(request);
            }


            return null;
        };
    }

    private void headerProcessor(FixedLengthHeaderTelegram request) {
        log.info(request.toString());
    }

    private void dataProcessor(FixedLengthDataTelegram request) {
        log.info(request.toString());
    }

    private void trailerProcessor(FixedLengthTrailerTelegram request) {
        log.info(request.toString());
    }
}
