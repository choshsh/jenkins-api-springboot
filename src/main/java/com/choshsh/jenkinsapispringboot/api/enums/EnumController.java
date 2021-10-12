package com.choshsh.jenkinsapispringboot.api.enums;

import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnumController {

  @ApiOperation(value = "YN 코드 조회")
  @GetMapping(value = "/jenkins/code/yn")
  public List<EnumDTO> yn() {
    return Arrays
        .stream(YN.class.getEnumConstants())
        .map(EnumDTO::new)
        .collect(Collectors.toList());
  }

  @ApiOperation(value = "부하테스트 환경 코드 조회 (클러스터 외부 or 내부)")
  @GetMapping(value = "/jenkins/code/locustenv")
  public List<EnumDTO> locustenv() {
    return Arrays
        .stream(LocustEnv.class.getEnumConstants())
        .map(EnumDTO::new)
        .collect(Collectors.toList());
  }

}
