## 디렉토리 구조

일단 제가 임의로 정해놓았습니다

config/: db 연결 등 설정 파일 포함하는 디렉토리

logs/: 로그 저장 디렉토리

src/: 주요 소스 코드

    controllers/: 라우트에서 요청을 처리하는 컨트롤러 함수를 포함하는 디렉토리
    models/: db 모델 스키마를 정의하는 디렉토리
    routes/: 서버 API 관련된 라우팅 정의하는 디렉토리
    middlewares/: 로그인 인증, 로깅, gpt api 등 미들웨어 들 관련 파일을 포함하는 디렉토리
    app.js: 서버의 진입점 파일

env: 환경 변수를 설정하는 파일 (process.env 로 접근 가능)

node_modules/: Node.js 모듈이 설치되는 디렉토리입니다.

package-lock.json: 설치된 모듈 의존성 

package.josn : 프로젝트 정보와 사용하는 패키지 정보

### npm 패키지(미들웨어) 목록

1. 현재 포함된 패키지 (package.json 참조) (임의로 설정한거라 필요없는 거 삭제하고 쓰면 될 것 같습니다)
- express : express.js (node js 웹 프레임워크)
- mysql2(or mysql): mysql db 연결에 사용
- dotenv: .env 파일 내 변수에 접근 가능하게 해줌
- nodemon: 개발 시 수정사항 바로 서버에 반영
- openai: gpt api
- morgan: 클라이언트랑 서버 사이 로그 관련 패키지
- cookie-parser : request에 있는 쿠키 정보 객체로 만들어줌
- express-session : 세션 관련
- bcrypt: password 해싱

2. 사용할 수 있어보이는 패키지
- jest : unit test 에 사용
- supertest : integration test에 사용
- winston: 로깅 관련, morgan이랑 같이 써도 될듯
- passport : 로그인 인증 관련 모듈 (사용안해도 무방)
- passport-local, passport- jwt, passport-kakao, passport-google : 위에 거랑 연동해서 소셜 연동 로그인 등 다양한 방식 로그인 구현 가능
- prettier : js code formatter
- pm2 : 프로세스 매니저
- 보안 관련? : sanitize-html 등등
