package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.choshsh.jenkinsapispringboot.api.enums.LocustEnv;
import com.choshsh.jenkinsapispringboot.config.BaseColumnEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/***
 * Locust 엔티티
 *
 * @author choshsh (cho911115@gmail.com)
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Table(name = "jenkins")
public class JenkinsEntity extends BaseColumnEntity {

  /**
   * PK. 자동 생성된다.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 부하 테스트 제목
   */
  @Column(nullable = false)
  private String title;

  /**
   * jenkins job 이름
   */
  @Column(nullable = false)
  private String jobName;

  /**
   * jenkins build 번호
   */
  @Column(nullable = false)
  private int buildNumber;

  /**
   * 대상환경
   */
  @Enumerated(EnumType.STRING)
  private LocustEnv locustEnv;

  /**
   * 빌드 파라미터 key
   */
  @ElementCollection
  private List<String> paramKeys = new LinkedList<>();

  /**
   * 빌드 파라미터 value
   */
  @ElementCollection
  private List<String> paramValues = new LinkedList<>();

  @Transient
  private Map<String, String> params;

  @Transient
  private String result;

  @Transient
  private long duration;

  @Transient
  private List<String> artifacts = new ArrayList<>();

  @Transient
  private Long timestamp;

  @Transient
  private String locustEnvName;



}


