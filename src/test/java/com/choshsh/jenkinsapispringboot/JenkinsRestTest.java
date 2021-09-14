package com.choshsh.jenkinsapispringboot;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.job.Job;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.cdancy.jenkins.rest.features.JobsApi;
import com.cdancy.jenkins.rest.features.QueueApi;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JenkinsRestTest {

    private final static Logger logger = Logger.getLogger("Test");
    private static JenkinsClient client;
    private static JobsApi jobsApi;
    private static List<Job> jobs;

    @Test
    @BeforeAll
    static void CONNECT() {
        String cred = "choshsh:11b3bd881b210e2d770fab52fe6fffaa43"; // <username>:<password>
        client = JenkinsClient.builder()
                .credentials(cred)
                .build();

        SystemInfo systemInfo = client.api().systemApi().systemInfo();
        logger.info("Jenkins 버전 : " + systemInfo.jenkinsVersion());
        logger.info("Jenkins URL : " + client.endPoint());

        then(systemInfo).isNotNull();
    }

    @Test
    @Order(1)
    void JOB_LIST() {
        jobsApi = client.api().jobsApi();
        jobs = jobsApi.jobList("").jobs();
        jobs.forEach(job -> logger.info(job.toString()));

        then(jobs).isNotNull();
    }

    @Test
    @Order(2)
    void BUILD_INFO() {
        Job job = jobs.stream().findAny().orElseGet(null);
        then(job).isNotNull();
        logger.info("Job 이름 : " + job.name());
        logger.info("마지막 빌드 번호 : " + jobsApi.lastBuildNumber(null, job.name()));
        logger.info("마지막 빌드 설명 : " + jobsApi.buildInfo(null, job.name(), jobsApi.lastBuildNumber(null, job.name())).fullDisplayName());
        logger.info("마지막 빌드 결과 : " + jobsApi.buildInfo(null, job.name(), jobsApi.lastBuildNumber(null, job.name())).result());

        logger.info("마지막 빌드 소요시간 : "
                + TimeUnit.MILLISECONDS.toSeconds(jobsApi.buildInfo(null, job.name(), jobsApi.lastBuildNumber(null, job.name())).duration())
                + "초");
    }

    @Test
    @Order(3)
    void QUEUE_LIST() {
        QueueApi queueApi = client.api().queueApi();
        queueApi.queue().forEach(queueItem -> logger.info(queueItem.toString()));
    }


//    @Test
//    @Order(99)
//    void DISCONNECT() {
//        try {
//            client.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}

