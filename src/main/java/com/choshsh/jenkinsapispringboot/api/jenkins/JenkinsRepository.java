package com.choshsh.jenkinsapispringboot.api.jenkins;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JenkinsRepository extends CrudRepository<JenkinsEntity, Long> {

  Iterable<JenkinsEntity> findAllByOrderByRegDateDesc();

}
