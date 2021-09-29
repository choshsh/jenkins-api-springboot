package com.choshsh.jenkinsapispringboot.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocustEnv implements EnumModel {

  K("In Kubernetes"), E("External");

  private final String value;

  @Override
  public String getKey() {
    return name();
  }

  @Override
  public String getValue() {
    return value;
  }

}
