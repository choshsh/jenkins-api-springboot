package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.cdancy.jenkins.rest.domain.job.Artifact;
import com.choshsh.jenkinsapispringboot.config.BaseColumnEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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
@Table(name = "locust")
public class JenkinsEntity extends BaseColumnEntity {

  /***
   * PK. 자동 생성된다.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /***
   * 부하 테스트 제목
   */
  @Column(nullable = false)
  private String title;

  /***
   * jenkins job 이름
   */
  @Column(nullable = false)
  private String jobName;

  /***
   * jenkins build 번호
   */
  @Column(nullable = false)
  private int buildNumber;

  @ElementCollection
  private List<String> paramKeys = new LinkedList<>();

  @ElementCollection
  private List<String> paramValues = new LinkedList<>();

  @Transient
  private Map<String, String> params;

  @Transient
  private String result;

  @Transient
  private long duration;

  @Transient
  private List<Artifact> artifacts = new ArrayList<>();

}


