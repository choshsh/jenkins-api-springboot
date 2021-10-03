package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.cdancy.jenkins.rest.domain.job.Artifact;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JenkinsService {

  private final JenkinsWrapper jenkinsWrapper;
  private final JenkinsRepository jenkinsRepository;

  public JenkinsService(JenkinsWrapper jenkinsWrapper,
      JenkinsRepository jenkinsRepository) {
    this.jenkinsWrapper = jenkinsWrapper;
    this.jenkinsRepository = jenkinsRepository;
  }

  /**
   * 빌드 리스트 조회
   *
   * @return List
   */
  public List<JenkinsEntity> listBuild() {
    List<JenkinsEntity> list = new ArrayList<>();

    jenkinsRepository.findAllByOrderByRegDateDesc().forEach(jenkinsEntity -> {
      BuildInfo buildInfo = jenkinsWrapper.buildInfo(jenkinsEntity.getJobName(),
          jenkinsEntity.getBuildNumber());
      jenkinsEntity.setTimestamp(buildInfo.timestamp());
      jenkinsEntity.setResult(buildInfo.result());
      jenkinsEntity.setDuration(buildInfo.duration());
      jenkinsEntity.setArtifacts(
          buildInfo.artifacts().stream().map(Artifact::fileName)
              .collect(Collectors.toList()));
      list.add(jenkinsEntity);
    });

    return list;
  }

  /**
   * 빌드 조회
   *
   * @return JenkinsEntity
   */
  public JenkinsEntity infoBuild(Long id) {
    Optional<JenkinsEntity> jenkinsEntityOptional = jenkinsRepository.findById(id);
    if (!jenkinsEntityOptional.isPresent()) {
      return null;
    } else {
      JenkinsEntity jenkinsEntity = jenkinsEntityOptional.get();

      BuildInfo buildInfo = jenkinsWrapper.buildInfo(jenkinsEntity.getJobName(),
          jenkinsEntity.getBuildNumber());
      jenkinsEntity.setTimestamp(buildInfo.timestamp());
      jenkinsEntity.setResult(buildInfo.result());
      jenkinsEntity.setDuration(buildInfo.duration());
      jenkinsEntity.setArtifacts(
          buildInfo.artifacts().stream().map(Artifact::fileName)
              .collect(Collectors.toList()));
      return jenkinsEntity;
    }
  }

  /**
   * 빌드 실행
   *
   * @param jenkinsEntity
   * @return JenkinsEntity
   */
  public JenkinsEntity build(JenkinsEntity jenkinsEntity) {
    int queueId = jenkinsWrapper.build(jenkinsEntity.getJobName(),
        jenkinsEntity.getParams());
    jenkinsEntity.setBuildNumber(jenkinsWrapper.traceQueue(queueId));
    return save(jenkinsEntity);
  }

  /**
   * 간략한 정보만 DB에 저장. 이 데이터를 기반으로 jenkins-api 조회.
   *
   * @param jenkinsEntity
   * @return JenkinsEntity
   */
  public JenkinsEntity save(JenkinsEntity jenkinsEntity) {
    if (!jenkinsEntity.getParams().isEmpty()) {
      jenkinsEntity.setParamKeys(
          jenkinsEntity.getParams().keySet().stream().collect(Collectors.toList()));
      jenkinsEntity.setParamValues(
          jenkinsEntity.getParams().values().stream().collect(Collectors.toList()));
    }
    return jenkinsRepository.save(jenkinsEntity);
  }

}
