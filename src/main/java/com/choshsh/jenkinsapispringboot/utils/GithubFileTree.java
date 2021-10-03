package com.choshsh.jenkinsapispringboot.utils;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GithubFileTree {

  private List<Map<String, String>> tree;

}
