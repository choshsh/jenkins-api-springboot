package com.choshsh.jenkinsapispringboot.config;

import com.choshsh.jenkinsapispringboot.api.enums.YN;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 엔티티에 extends하면 자동으로 생성되고 업데이트된다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseColumnEntity {

  /**
   * 등록시간
   */
  @CreatedDate
  @Column(updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime regDate;

  /**
   * 수정시간
   */
  @LastModifiedDate
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime modDate;

  /**
   * 삭제시간
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime delDate;

  /**
   * 삭제여부
   */
  @Enumerated(EnumType.STRING)
  @Column(length = 1)
  private final YN isDel = YN.N;

}
