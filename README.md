# jenkins-api-springboot

jenkins api를 사용하여 job 실행 및 모니터링을 하기 위한 개발한 프로젝트입니다.

아래 2가지를 사용하여 api를 구현했습니다.

- [jenkins 공식 사이트](https://www.jenkins.io/doc/book/using/remote-access-api/#RemoteaccessAPI-JavaAPIwrappers)에서  소개하는 Java API wrapper인 [jenkins-rest](https://github.com/cdancy/jenkins-rest)
- Spring Boot

## API 문서

- [https://choshsh.github.io/jenkins-api-springboot/](https://choshsh.github.io/jenkins-api-springboot/)

## 테스트 코드

- [https://github.com/choshsh/jenkins-api-springboot/blob/master/src/test/java/com/choshsh/jenkinsapispringboot/JenkinsRestTest.java](https://github.com/choshsh/jenkins-api-springboot/blob/master/src/test/java/com/choshsh/jenkinsapispringboot/JenkinsRestTest.java)

## 환경변수 설정

시스템 환경변수 값을 찾아 사용합니다.

- `JENKINS_REST_ENDPOINT` : jenkins 서버 주소
- `JENKINS_REST_CREDENTIALS` : jenkins 사용자 인증 정보

**Pod 예시**

인증 정보는 `<id>:<token>` 형식입니다. ([jenkins 사용자 token 생성하기](https://www.notion.so/89b9a9ff76ef405b82ba068b4752fb7c))

```yaml
...

env:
  - name: JENKINS_REST_ENDPOINT 
    value: "http://jenkins.choshsh.com"
  - name: JENKINS_REST_CREDENTIALS
    value: "choshsh:11b3bd881b210e2d770fab52fe6fffaa43"
```

## 빌드 시 연결 확인

```java
2021-10-12 00:43:17 - INFO --- c.c.j.api.jenkins.JenkinsWrapper         : Jenkins 연결 성공
2021-10-12 00:43:17 - INFO --- c.c.j.api.jenkins.JenkinsWrapper         : Jenkins 버전 : 2.307
2021-10-12 00:43:17 - INFO --- c.c.j.api.jenkins.JenkinsWrapper         : Jenkins URL : http://jenkins.choshsh.com
2021-10-12 00:43:18 - INFO --- o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path ''
2021-10-12 00:43:18 - INFO --- c.c.j.JenkinsApiSpringbootApplication    : Started JenkinsApiSpringbootApplication in 4.998 seconds (JVM running for 5.405)
```