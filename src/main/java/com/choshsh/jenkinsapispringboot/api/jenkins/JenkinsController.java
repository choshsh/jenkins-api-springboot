package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JenkinsController {

  private final JenkinsWrapper jenkinsWrapper;
  private final JenkinsService jenkinsService;
  private final JenkinsRepository jenkinsRepository;

  public JenkinsController(JenkinsWrapper jenkinsWrapper,
      JenkinsService jenkinsService,
      JenkinsRepository jenkinsRepository) {
    this.jenkinsWrapper = jenkinsWrapper;
    this.jenkinsService = jenkinsService;
    this.jenkinsRepository = jenkinsRepository;
  }

  @ApiOperation(value = "빌드 리스트 조회")
  @GetMapping("/jenkins/build")
  public List<JenkinsEntity> listJob() {
    return jenkinsService.listBuild();
  }

  @ApiOperation(value = "빌드 실행")
  @ResponseBody
  @PostMapping("/jenkins/build")
  public int build(@RequestBody JenkinsEntity jenkinsEntity) {
    return jenkinsService.build(jenkinsEntity).getBuildNumber();
  }

  @ApiOperation(value = "빌드 실행 중인지 조회")
  @GetMapping("/jenkins/{jobName}}/{buildNumber}}/status")
  public Boolean isBuild(@PathVariable("jobName") String jobName,
      @PathVariable("buildNumber") int buildNumber) {
    return jenkinsWrapper.isBuild(jobName, buildNumber);
  }

  @GetMapping("/jenkins/{jobName}}/{buildNumber}}/trace")
  public BuildInfo traceBuild(@PathVariable("jobName") String jobName,
      @PathVariable("buildNumber") int buildNumber) throws Exception {
    return jenkinsWrapper.traceBuild(jobName, buildNumber);
  }


}
