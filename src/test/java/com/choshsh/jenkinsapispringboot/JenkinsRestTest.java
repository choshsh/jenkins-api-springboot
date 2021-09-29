package com.choshsh.jenkinsapispringboot;

import static org.assertj.core.api.BDDAssertions.then;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.Job;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.cdancy.jenkins.rest.features.JobsApi;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

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
    logger.info("마지막 빌드 설명 : " + jobsApi.buildInfo(null, job.name(),
        jobsApi.lastBuildNumber(null, job.name())).fullDisplayName());
    logger.info("마지막 빌드 결과 : " + jobsApi.buildInfo(null, job.name(),
        jobsApi.lastBuildNumber(null, job.name())).result());

    logger.info("마지막 빌드 소요시간 : "
        + TimeUnit.MILLISECONDS.toSeconds(
        jobsApi.buildInfo(null, job.name(), jobsApi.lastBuildNumber(null, job.name())).duration())
        + "초");
  }

  @Test
  @Order(3)
  void BUILD_JOB() throws Exception {
    String jobName = "CoreDNS Debug";

    // 빌드
    Map<String, List<String>> params = new HashMap<>();
    params.put("EXTERNAL_URL", Lists.newArrayList("google.com"));
    IntegerResponse queueId = jobsApi.buildWithParameters(null, jobName, params);
    if (queueId.errors().size() > 0) {
      for (Error error : queueId.errors()) {
        System.out.println(error);
      }
      throw new RuntimeException("빌드 에러");
    }

    // 큐 체크
    QueueItem queueItem = client.api().queueApi().queueItem(queueId.value());

    while (true) {
      if (queueItem.cancelled()) {
        throw new RuntimeException("Queue item cancelled");
      }

      if (queueItem.executable() != null) {
        System.out.println(queueItem.executable().number());
        break;
      }

      Thread.sleep(2000);
      queueItem = client.api().queueApi().queueItem(queueId.value());
    }

    // 빌드 정보 조회
    System.out.println(queueItem.toString());
    BuildInfo buildInfo = client.api().jobsApi()
        .buildInfo(null, jobName, queueItem.executable().number());
    while (buildInfo.result() == null) {
      Thread.sleep(2000);
      buildInfo = client.api().jobsApi().buildInfo(null, jobName, queueItem.executable().number());
      System.out.println(buildInfo.actions().toString());
      System.out.println(buildInfo.building());
    }

    System.out.println(buildInfo.displayName());
    System.out.println(buildInfo.number());
    System.out.println(buildInfo.duration() / 1000);
    System.out.println(buildInfo.actions().toString());
    System.out.println(buildInfo.result());

    then(buildInfo.result()).isEqualTo("SUCCESS");
  }

}

