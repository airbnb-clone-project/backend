SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS USER_FOLLOW;
DROP TABLE IF EXISTS SOCIAL_USER;
DROP TABLE IF EXISTS NOTIFICATION;
DROP TABLE IF EXISTS PIN;
DROP TABLE IF EXISTS PIN_LIKE;
DROP TABLE IF EXISTS COMMENT;
DROP TABLE IF EXISTS COMMENT_LIKE;
DROP TABLE IF EXISTS BOARD;
DROP TABLE IF EXISTS PIN_TAG;
DROP TABLE IF EXISTS TAG;
DROP TABLE IF EXISTS `USER`;
DROP TABLE IF EXISTS REFRSH_TOKEN;



CREATE TABLE IF NOT EXISTS REFRSH_TOKEN
(
    NO              BIGINT PRIMARY KEY COMMENT '사용자 고유번호',
    USERNAME           VARCHAR(255) UNIQUE NOT NULL COMMENT '이메일',
    REFRESH_TOKEN   VARCHAR(1024) NOT NULL COMMENT '리프레시 토큰',
    EXPIRATION DATETIME NOT NULL COMMENT '토큰 유효기간',

    CREATED_AT      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS USER
(
    NO              BIGINT PRIMARY KEY COMMENT '사용자 고유번호',
    USERNAME           VARCHAR(255) UNIQUE NOT NULL COMMENT '이메일',
    IS_SOCIAL       BOOLEAN             NOT NULL COMMENT '소셜 아이디 여부',
    PASSWORD        VARCHAR(1024)   COMMENT '비밀번호'
    BIRTHDAY        DATE    NOT NULL '생년월일'

    REFRESH_TOKEN   VARCHAR(1024) COMMENT '리프레시 토큰',
    EXPIRATION DATETIME COMMENT '토큰 유효기간',

    FIRST_NAME       VARCHAR(255)    COMMENT '이름, 유저 닉네임',
    LAST_NAME        VARCHAR(255)    COMMENT '성, 유저 닉네임',
    PROFILE_IMG_URL VARCHAR(2048)       NOT NULL COMMENT '프로필 이미지 URL',
    SPOKEN_LANGUAGE        VARCHAR(255)        NOT NULL,
    COUNTRY         VARCHAR(255)        NOT NULL,

    CREATED_AT      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS USER_FOLLOW
(
    NO          BIGINT PRIMARY KEY COMMENT '팔로우 관계 고유번호',
    FOLLOWER_NO BIGINT  NOT NULL COMMENT '팔로워 사용자 번호',
    FOLLOWED_NO BIGINT  NOT NULL COMMENT '팔로우된 사용자 번호',
    IS_ACTIVE   BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성 상태',

    CREATED_AT  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP COMMENT '팔로우 시작 시간',
    UPDATED_AT  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '상태 변경 시간'
);

CREATE TABLE IF NOT EXISTS SOCIAL_USER
(
    NO          BIGINT PRIMARY KEY COMMENT '소셜 회원 고유번호',
    USER_NO     BIGINT      NOT NULL COMMENT '사용자 고유번호 (FK)',
    PROVIDER    VARCHAR(20) NOT NULL COMMENT '소셜로그인 공급자(Google ..)',
    CREATED_AT  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS NOTIFICATION
(
    NO             BIGINT PRIMARY KEY COMMENT '알림정보 고유번호',
    USER_NO        BIGINT       NOT NULL COMMENT '사용자 번호 고유번호',
    TYPE           VARCHAR(30)  NOT NULL COMMENT '알림 타입',
    RELARED_PIN_NO BIGINT       NOT NULL COMMENT '알림과 연관된 핀고유번호(FK)',
    IS_READ        BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '알림 읽음 여부',
    NAME           VARCHAR(512) NOT NULL COMMENT '알림 제목',
    CONTENT        VARCHAR(1024) COMMENT '알림 내용',
    CREATED_AT     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS PIN
(
    NO                 BIGINT PRIMARY KEY COMMENT '핀 고유번호',
    IMG_URL            VARCHAR(1024) NOT NULL COMMENT '핀 이미지 URL',
    TITLE              VARCHAR(255) COMMENT '핀 제목',
    DESCRIPTION        TEXT COMMENT '핀 설명',
    LINK               VARCHAR(2000) COMMENT '링크 URL',
    BOARD_NO           BIGINT        NOT NULL COMMENT '보드 종류(FK)',
    IS_COMMENT_ALLOWED BOOLEAN       NOT NULL DEFAULT TRUE COMMENT '댓글 달기 허용여부',
    LIKE_COUNT         INT                    DEFAULT 0 COMMENT '좋아요 수',
    CREATED_AT         TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT         TIMESTAMP              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS PIN_LIKE
(
    NO            BIGINT PRIMARY KEY COMMENT '좋아요 고유번호',
    TARGET_PIN_NO BIGINT NOT NULL COMMENT '좋아요 타겟 게시물',
    LIKER         BIGINT NOT NULL COMMENT '좋아요 누른 사용자 번호',
    EMOJI_NO      INT    NOT NULL COMMENT '0: 좋은 아이디어에요, 1: 좋아요, 2: 감사합니다, 3: 우와, 4: 재미있어요',
    CREATED_AT    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS COMMENT
(
    NO                BIGINT PRIMARY KEY COMMENT '댓글 고유번호',
    TARGET_PIN_NO     BIGINT NOT NULL COMMENT '게시물 핀',
    COMMENTER_NO      BIGINT NOT NULL COMMENT '댓글 작성자 번호',
    PARENT_COMMENT_NO BIGINT COMMENT '부모 댓글 번호',
    DEPTH             INT    NOT NULL COMMENT '댓글 깊이',
    CONTENT           TEXT   NOT NULL COMMENT '댓글 내용',
    LIKE_COUNT        INT       DEFAULT 0 COMMENT '좋아요 카운트수',
    CREATED_AT        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS COMMENT_LIKE
(
    NO                BIGINT PRIMARY KEY COMMENT '댓글 좋아요 고유번호',
    TARGET_COMMENT_NO BIGINT NOT NULL COMMENT '좋아요 타겟 댓글',
    LIKER             BIGINT NOT NULL COMMENT '좋아요 누른 사용자 번호',
    EMOJI_NO          INT    NOT NULL COMMENT '0: 좋아요, 1: 유용함',
    CREATED_AT        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS BOARD
(
    NO         BIGINT PRIMARY KEY COMMENT '보드 고유번호',
    NAME       VARCHAR(255) UNIQUE NOT NULL COMMENT '보드 이름',
    USER_NO    BIGINT COMMENT '유저고유번호 (FK)',
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS PIN_TAG
(
    NO     BIGINT PRIMARY KEY COMMENT '핀 - 태그 고유번호',
    PIN_NO BIGINT NOT NULL COMMENT '연관 핀 번호',
    TAG_NO BIGINT NOT NULL COMMENT '연관 태그 번호'
);

CREATE TABLE IF NOT EXISTS TAG
(
    NO         BIGINT PRIMARY KEY COMMENT '태그 고유번호',
    NAME       VARCHAR(255) NOT NULL COMMENT '태그 이름',
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX USER_FOLLOW_INDEX_0 ON USER_FOLLOW (FOLLOWER_NO, FOLLOWED_NO);

CREATE INDEX COMMENT_INDEX_1 ON COMMENT (TARGET_PIN_NO, PARENT_COMMENT_NO);

ALTER TABLE COMMENT
    ADD FOREIGN KEY (COMMENTER_NO) REFERENCES USER (NO);

ALTER TABLE COMMENT
    ADD FOREIGN KEY (TARGET_PIN_NO) REFERENCES PIN (NO);

ALTER TABLE USER_FOLLOW
    ADD FOREIGN KEY (FOLLOWED_NO) REFERENCES USER (NO);

ALTER TABLE USER_FOLLOW
    ADD FOREIGN KEY (FOLLOWER_NO) REFERENCES USER (NO);

ALTER TABLE SOCIAL_USER
    ADD FOREIGN KEY (USER_NO) REFERENCES USER (NO);

ALTER TABLE PIN
    ADD FOREIGN KEY (BOARD_NO) REFERENCES BOARD (NO);

ALTER TABLE PIN_TAG
    ADD FOREIGN KEY (PIN_NO) REFERENCES PIN (NO);

ALTER TABLE PIN_TAG
    ADD FOREIGN KEY (TAG_NO) REFERENCES TAG (NO);

ALTER TABLE NOTIFICATION
    ADD FOREIGN KEY (USER_NO) REFERENCES USER (NO);

ALTER TABLE PIN_LIKE
    ADD FOREIGN KEY (TARGET_PIN_NO) REFERENCES PIN (NO);

ALTER TABLE PIN_LIKE
    ADD FOREIGN KEY (LIKER) REFERENCES USER (NO);

ALTER TABLE COMMENT_LIKE
    ADD FOREIGN KEY (TARGET_COMMENT_NO) REFERENCES COMMENT (NO);

ALTER TABLE COMMENT_LIKE
    ADD FOREIGN KEY (LIKER) REFERENCES USER (NO);

ALTER TABLE BOARD
    ADD FOREIGN KEY (USER_NO) REFERENCES USER (NO);

ALTER TABLE NOTIFICATION
    ADD FOREIGN KEY (RELARED_PIN_NO) REFERENCES PIN (NO);

SET FOREIGN_KEY_CHECKS = 1;