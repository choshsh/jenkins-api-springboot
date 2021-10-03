package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.choshsh.jenkinsapispringboot.utils.GithubFileTree;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class JenkinsController {

  private final JenkinsWrapper jenkinsWrapper;
  private final JenkinsService jenkinsService;
  private final JenkinsRepository jenkinsRepository;
  private ObjectMapper mapper = new ObjectMapper();
  private final RestTemplate restTemplate = new RestTemplate();
  @Value("${pyscriptURL}")
  private String pyscriptURL;

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

  @ApiOperation(value = "빌드 조회")
  @GetMapping("/jenkins/build/{id}")
  public JenkinsEntity infoJob(@PathVariable("id") Long id) {
    return jenkinsService.infoBuild(id);
  }

  @ApiOperation(value = "빌드 실행")
  @ResponseBody
  @PostMapping("/jenkins/build")
  public JenkinsEntity build(@RequestBody JenkinsEntity jenkinsEntity) {
    return jenkinsService.build(jenkinsEntity);
  }

  @ApiOperation(value = "빌드 실행 중인지 조회")
  @GetMapping("/jenkins/{jobName}}/{buildNumber}}/status")
  public Boolean isBuild(@PathVariable("jobName") String jobName,
      @PathVariable("buildNumber") int buildNumber) {
    return jenkinsWrapper.isBuild(jobName, buildNumber);
  }

  @ApiOperation(value = "빌드 추적")
  @GetMapping("/jenkins/{jobName}}/{buildNumber}}/trace")
  public BuildInfo traceBuild(@PathVariable("jobName") String jobName,
      @PathVariable("buildNumber") int buildNumber) {
    return jenkinsWrapper.traceBuild(jobName, buildNumber);
  }

  @ApiOperation(value = "GitHub에서 부하테스트 파이썬 스크립트 리스트 조회")
  @GetMapping("/jenkins/pyscript")
  public List<String> pyscriptList() {
    String searchString = "script/loadtest/";

    GithubFileTree data = restTemplate.getForObject(pyscriptURL, GithubFileTree.class);
    if (data == null) {
      return null;
    } else {
      return data.getTree()
          .stream()
          .filter(item -> item.get("path").indexOf(searchString) > -1)
          .map(item -> item.get("path"))
          .collect(Collectors.toList());
    }
  }

}
