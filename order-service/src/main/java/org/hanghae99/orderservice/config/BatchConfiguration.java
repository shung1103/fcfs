//package org.hanghae99.orderservice.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//@Slf4j
//@RequiredArgsConstructor
//public class BatchConfiguration {
//
//    @Bean
//    public Job job(JobRepository jobRepository, Step step) {
//        return new JobBuilder("job", jobRepository)
//                .start(step).build();
//    }
//    @Bean
//    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new StepBuilder("step", jobRepository)
//                .tasklet((contribution, chunkContext) -> {
//                    System.out.println("Hello Batch");
//                    return RepeatStatus.FINISHED;
//                }, transactionManager).build();
//    }
//}
