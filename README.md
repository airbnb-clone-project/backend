# 백엔드

## 서버 구성 정보

### MySQL

- MySQL 8.0
- USERNAME: airbnb
- PASSWORD: airbnb!@#
- DATABASE: airbnb

### MongoDB

- MongoDB 8.0 - rc
- USERNAME: airbnb
- PASSWORD: airbnb!!

## 프로젝트 git submodule 을 최신화 불가능한 경우

```bash
git submodule update --remote 수행하여 원격을 최신화해야함 -> 이후 프로젝트 재 실행한다.
 java/com/airbnb_clone/config/submodule/SubmodulePreInitializer.java 참고
```

## Docker Compose 사용시 트러블 슈팅

1. DB 연결 실패
   - Docker Compose 를 down 시킨 후 다시 up 시켜도 DB 에러가 발생하는 경우, 컨테이너 외부의 볼륨이 삭제되지 않아서 발생하는 문제입니다.

   ```text
   volumes:
       mysql-data:
       mongo-data:
   ```
   - 위와 같이 volumes 가 컨테이너 내부가 아닌, 호스트에 마운트 되어 있기에, docker-compose down -v 옵션으로 볼륨까지 삭제시켜야합니다.
