package com.choshsh.jenkinsapispringboot;

import static org.assertj.core.api.BDDAssertions.then;

import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.Job;
import com.choshsh.jenkinsapispringboot.api.jenkins.JenkinsWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"dev"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("jenkins 서버와 job 필요")
class JenkinsRestTest {

  private final static Logger logger = Logger.getLogger("Test");
  private static JenkinsWrapper jenkinsWrapper;

  private static List<Job> jobs;
  private static String jobName;
  private static int buildNumber = 0;
  private static int queueId;
  private static BuildInfo buildInfo;

  // jenkins 서버와 연결
  @Test
  @BeforeAll
  static void CONNECT() {
    String cred = "choshsh:11b3bd881b210e2d770fab52fe6fffaa43"; // <username>:<password>
    jenkinsWrapper = new JenkinsWrapper(cred);
    then(jenkinsWrapper).isNotNull();
  }

  // job 리스트 조회
  @Test
  @Order(1)
  void JOB_LIST() {
    jobs = jenkinsWrapper.jobList();
    then(jobs.size()).isGreaterThan(0);
    jobs.forEach(job -> logger.info(job.toString()));
  }

  // 특정 job의 빌드 조회
  @Test
  @Order(2)
  void BUILD_INFO() {
    Job job = jobs.get(0);
    String jobName = job.name();
    BuildInfo buildInfo = jenkinsWrapper.buildInfo(
        jobName,
        jenkinsWrapper.lastBuildNumber(jobName)
    );
    logger.info("Job 이름 : " + jobName);
    logger.info("마지막 빌드 번호 : " + buildInfo.number());
    logger.info("마지막 빌드 설명 : " + buildInfo.description());
    logger.info("마지막 빌드 소요시간 : " + buildInfo.duration());
  }

  // 빌드
  @Test
  @Order(3)
  void BUILD() {
    jobName = "CoreDNS-Debug";

    // 파라미터 세팅
    Map<String, String> params = new HashMap<>();
    params.put("EXTERNAL_URL", "google.com");

    // 빌드 실행
    try {
      queueId = jenkinsWrapper.build(jobName, params);
    } catch (Exception e) {
      throw new RuntimeException("빌드 요청 에러");
    }

    then(queueId).isGreaterThan(-1);
    logger.info("job 이름 : " + jobName);
    logger.info("큐 ID : " + String.valueOf(queueId));
  }

  // 큐 추적
  @Test
  @Order(4)
  void TRACE_QUEUE() {
    try {
      buildNumber = jenkinsWrapper.traceQueue(queueId);
    } catch (Exception e) {
      throw new RuntimeException("빌드 큐 에러");
    }

    then(buildNumber).isGreaterThan(0);
    logger.info("빌드 번호 : " + String.valueOf(buildNumber));
  }

  // 빌드 추적
  @Test
  @Order(5)
  void TRACE_BUILD() {
    buildInfo = jenkinsWrapper.traceBuild(jobName, buildNumber);
    then(buildInfo.result()).isNotNull();
    logger.info("빌드 정보 : " + buildInfo.toString());
  }

  // 결과
  @Test
  @Order(6)
  void BUILD_RESULT() {
    String result = buildInfo.result();
    switch (result) {
      case "SUCCESS":
        logger.info("빌드 URL : " + buildInfo.url());
        logger.info("빌드 시간 : " + buildInfo.duration() / 1000 + "초");
      default:
        logger.info("빌드 결과 : " + result);
    }
  }

}

