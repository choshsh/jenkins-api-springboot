package com.choshsh.jenkinsapispringboot.api.jenkins;

import com.choshsh.jenkinsapispringboot.api.enums.LocustEnv;
import com.choshsh.jenkinsapispringboot.config.BaseColumnEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

/***
 * Locust 엔티티
 *
 * @author choshsh (cho911115@gmail.com)
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Table(name = "locust")
public class LocustEntity extends BaseColumnEntity {

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
    private String name;

    /***
     * jenkins job 이름
     */
    @Column(nullable = false)
    private String jobName;

    /***
     * jenkins build 번호
     */
    @Column(nullable = false)
    private String buildNumber;

    /***
     * 테스트 대상 환경 (쿠버네티스 클러스터 내부 or 외부)
     */
    @Column(nullable = false)
    private LocustEnv env;

    /***
     * 부하 테스트 대상
     */
    @Column(nullable = false)
    private String target;

    /***
     * 부하 테스트 스크립트
     */
    private String scriptUrl;

    /***
     * 설명
     */
    private String comment;

}


