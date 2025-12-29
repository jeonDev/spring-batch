package com.service.batch.file.job;

import com.service.batch.common.telegram.Telegram;
import com.service.batch.database.domain.DatabaseData;
import com.service.batch.file.job.telegram.WriterDataTelegram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
public class DbToFixedLengthFileWriterJobConfig {

    private final JobRepository jobRepository;
    private final DataSource dataSource;

    public DbToFixedLengthFileWriterJobConfig(JobRepository jobRepository,
                                              DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.dataSource = dataSource;
    }

    @Bean
    public Job dbToFixedLengthFileWriterJob() throws Exception {
        return new JobBuilder("dbToFixedLengthFileWriterJob", jobRepository)
                .start(dbToFixedLengthFileWriterStep())
                .build();
    }

    @Bean
    public Step dbToFixedLengthFileWriterStep() throws Exception {
        return new StepBuilder("dbToFixedLengthFileWriterStep", jobRepository)
                .<DatabaseData, Telegram>chunk(1000)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<DatabaseData> itemReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<DatabaseData>()
                .dataSource(dataSource)
                .name("jdbcDatabaseStepItemReader")
                .selectClause("id, DATA_A, DATA_B")
                .fromClause("FROM DATABASE_DATA")
                .whereClause("WHERE 1=1")
                .sortKeys(Map.of("id", Order.DESCENDING))
                .rowMapper((rs, rowNum) ->
                        DatabaseData.builder()
                                .id(rs.getLong(1))
                                .dataA(rs.getString(2))
                                .dataB(rs.getString(3))
                                .build()
                )
                .build();
    }

    private ItemProcessor<DatabaseData, Telegram> itemProcessor() {
        return item -> {
            // TODO: Header / Trailer
            return new WriterDataTelegram(item.getDataA(), item.getDataB());
        };
    }

    private ItemWriter<Telegram> itemWriter() {
        // TODO: File Writer
        return null;
    }
}
