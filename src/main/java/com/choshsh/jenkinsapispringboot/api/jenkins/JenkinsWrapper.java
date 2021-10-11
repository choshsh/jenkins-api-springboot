package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.Job;
import com.cdancy.jenkins.rest.domain.job.JobInfo;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JenkinsWrapper {

  private JenkinsClient client;
  private final long SLEEP_MILLS = 2000;

  public JenkinsWrapper() {
    connect();
  }

  public JenkinsWrapper(String cred) {
    connect();
  }

  /**
   * Jenkins 서버 연결
   */
  public void connect() {
    client = JenkinsClient.builder()
        .build();

    SystemInfo systemInfo = client.api().systemApi().systemInfo();
    if (systemInfo.jenkinsVersion().equals("-1")) {
      throw new RuntimeException("jenkins 연결 실패: 연결 정보를 확인하세요.");
    } else {
      log.info("Jenkins 연결 성공");
      log.info("Jenkins 버전 : {}", systemInfo.jenkinsVersion());
      log.info("Jenkins URL : {}", client.endPoint());
    }
  }

  /**
   * 빌드 실행
   *
   * @param jobName Job 이름
   * @param params  Job 파라미터 (null 가능)
   * @return int 큐 ID
   * @throws Exception
   */
  public int build(String jobName, Map<String, String> params) throws Exception {
    // 빌드 요청
    IntegerResponse queueId = params == null ?
        client.api().jobsApi().build(null, jobName)
        : client.api().jobsApi().buildWithParameters(null, jobName, convertParam(params));
    jenkinsErrorHandler("빌드 요청 에러", queueId.errors());

    return queueId.value();
  }

  /**
   * 큐 추적
   *
   * @param queueId 큐 ID
   * @return int 빌드번호
   */
  public int traceQueue(int queueId) throws Exception {
    QueueItem queueItem = client.api().queueApi().queueItem(queueId);
    int buildNumber;
    while (true) {
      if (queueItem.cancelled()) {
        throw new Exception("Queue item cancelled");
      }
      if (queueItem.executable() != null) {
        buildNumber = queueItem.executable().number();
        break;
      }
      try {
        Thread.sleep(SLEEP_MILLS);
        queueItem = client.api().queueApi().queueItem(queueId);
      } catch (Exception e) {
        log.error("큐 추적 에러 : {}", e.getMessage());
        throw new Exception("Queue item occurs error");
      }
    }
    return buildNumber;
  }

  /**
   * 빌드 추적
   *
   * @param jobName     Job 이름
   * @param buildNumber 빌드번호
   * @return BuildInfo
   * @throws Exception
   */
  public BuildInfo traceBuild(String jobName, int buildNumber) {
    BuildInfo buildInfo = buildInfo(jobName, buildNumber);
    try {
      while (buildInfo.result() == null) {
        Thread.sleep(SLEEP_MILLS);
        buildInfo = buildInfo(jobName, buildNumber);
      }
    } catch (Exception e) {
      log.error("빌드 추적 에러 : {}", e.getMessage());
    }
    return buildInfo;
  }

  /**
   * 빌드 실행 중인지 확인
   *
   * @param jobName     Job 이름
   * @param buildNumber 빌드번호
   * @return Boolean
   */
  public Boolean isBuild(String jobName, int buildNumber) {
    return client.api().jobsApi()
        .buildInfo(null, jobName, buildNumber).building();
  }

  /**
   * 빌드 조회
   *
   * @param jobName
   * @param buildNumber
   * @return
   */
  public BuildInfo buildInfo(String jobName, int buildNumber) {
    return client.api().jobsApi()
        .buildInfo(null, jobName, buildNumber);
  }

  /**
   * Job 마지막 빌드 번호 조회
   *
   * @param jobName Job 이름
   * @return int
   */
  public int lastBuildNumber(String jobName) {
    return client.api().jobsApi().lastBuildNumber(null, jobName);

  }

  /**
   * Job 조회
   *
   * @param jobName Job 이름
   * @return JobInfo
   */
  public JobInfo jobInfo(String jobName) {
    return client.api().jobsApi().jobInfo(null, jobName);
  }

  /**
   * Job 리스트 조회
   *
   * @return List
   */
  public List<Job> jobList() {
    return client.api().jobsApi().jobList("").jobs();
  }

  /**
   * {@link JenkinsWrapper} 에러 핸들러
   *
   * @param msg    에러 이름
   * @param errors
   */
  public void jenkinsErrorHandler(String msg, List<Error> errors) throws Exception {
    if (errors.size() > 0) {
      for (Error error : errors) {
        log.error("Exception : {}", error.exceptionName());
      }
      throw new Exception(msg);
    }
  }

  /**
   * Jenkins-rest에서 build할 때 필요한 파라미터 타입이 Map<String, List<String>>. Map<String, String>을
   * Map<String, List<String>>으로 변환
   *
   * @return Map<String, List < String>>
   */
  public Map<String, List<String>> convertParam(Map<String, String> params) {
    if (params.size() < 1) {
      return null;
    } else {
      Map<String, List<String>> result = new HashMap<>();
      params.forEach((k, v) -> {
        result.put(k, Lists.newArrayList(v));
      });
      return result;
    }
  }
}
