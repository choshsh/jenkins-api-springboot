package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import java.util.ArrayList;
import java.util.List;
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

  public List<JenkinsEntity> listJob() {
    List<JenkinsEntity> list = new ArrayList<>();
    jenkinsRepository.findAll().forEach(jenkinsEntity -> {
      BuildInfo buildInfo = jenkinsWrapper.buildInfo(jenkinsEntity.getJobName(),
          jenkinsEntity.getBuildNumber());
      jenkinsEntity.setResult(buildInfo.result());
      jenkinsEntity.setDuration(buildInfo.duration());
      jenkinsEntity.setArtifacts(buildInfo.artifacts());
    });
    return list;
  }

  public JenkinsEntity build(JenkinsEntity jenkinsEntity) {
    int queueId = jenkinsWrapper.build(jenkinsEntity.getJobName(),
        jenkinsEntity.getParams());
    jenkinsEntity.setBuildNumber(jenkinsWrapper.traceQueue(queueId));
    return save(jenkinsEntity);
  }

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
