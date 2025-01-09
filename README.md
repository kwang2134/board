# **게시판 프로젝트**

자바 스프링 mvc 백엔드 학습을 위해 진행한 프로젝트입니다.

## **목차**

- [프로젝트 소개](#프로젝트-소개)
  - [기능 설명](#기능-설명)
  - [사용 기술](#사용-기술)
- [프로젝트 구조](#프로젝트-구조)
  - [패키지](#프로젝트-구조) 
  - [DB](#db-구조)
- [개발 일지](#개발-일지)
  - [개요](#개요)
  - [설계](#설계)
    - 유스케이스
    - 아키텍처
    - 데이터베이스
    - 클래스
    - 프론트엔드
  - [개발](#개발)
    - 백엔드
    - 프론트엔드
- [프로젝트 결과](#프로젝트-결과)
  - 결과물
  - 후기

## **프로젝트 소개**

본 프로젝트는 자바 스프링을 사용한 백엔드 구축 학습을 위해 진행된 프로젝트로 기술 학습 후 실제 적용을 목적으로 수행하였습니다. 

### **기능 설명**

프로젝트는 웹에서 사용할 수 있는 게시판 서비스를 제작한 것으로 기본적인 CRUD 기능과 추천 및 추가적인 기능을 제공합니다. 프로젝트의 특징으로 회원과 비회원 모두에게 게시글 및 댓글에 대한 CRUD 기능을 제공합니다.

**주요 기능**

- 게시판: CRUD, 추천, 이미지 포함
- 댓글: CRUD
- 회원: 로그인, 회원가입, 수정

### **사용 기술**

**주요 프레임워크/ 라이브러리**
- Java 17
- SpringBoot 3.4.0
- SpringDataJpa 3.4.0
- Spring Security 6.4.1
- Querydsl 5.0.0

**기타 의존성**
- Lombok
- Validation
- docker 26.1.3

**Build Tool**
- Gradle

**DataBase**
- MySQL 8.0.39 (서비스)
- h2(개발)
- redis 7.4.1

**프론트 엔드**
- Thymeleaf
- TypeScript(vanilla)
- Html/Css

**개발 환경**
- IntelliJ
- vs code

**디자인 Tool** 
- Figma

## **프로젝트 구조**

### **패키지 구조**
<details>
  <summary>패키지</summary>
  <pre>
├─java
│  └─com
│      └─kwang
│          └─board
│              │  BoardApplication.java
│              │  TestInit.java
│              │
│              ├─comment
│              │  ├─adapters
│              │  │  ├─controller
│              │  │  │      CommentController.java
│              │  │  │      CommentFormController.java
│              │  │  │
│              │  │  └─mapper
│              │  │          CommentMapper.java
│              │  │
│              │  ├─application
│              │  │  ├─dto
│              │  │  │      CommentDTO.java
│              │  │  │      CommentUpdateDTO.java
│              │  │  │
│              │  │  └─service
│              │  │          CommentService.java
│              │  │
│              │  ├─domain
│              │  │  ├─model
│              │  │  │      Comment.java
│              │  │  │
│              │  │  └─repository
│              │  │          CommentRepository.java
│              │  │
│              │  └─usecase
│              │          CommentCrudUseCase.java
│              │
│              ├─global
│              │  ├─aop
│              │  │  ├─annotation
│              │  │  │      SavePostRequest.java
│              │  │  │
│              │  │  └─aspect
│              │  │          PostSessionAspect.java
│              │  │
│              │  ├─config
│              │  │      QuerydslConfig.java
│              │  │      RedisConfig.java
│              │  │      WebConfig.java
│              │  │
│              │  ├─controller
│              │  │      GlobalControllerAdvice.java
│              │  │
│              │  ├─domain
│              │  │      BaseEntity.java
│              │  │
│              │  ├─exception
│              │  │  │  BaseException.java
│              │  │  │  CustomErrorController.java
│              │  │  │
│              │  │  └─exceptions
│              │  │      │  GlobalExceptionHandler.java
│              │  │      │  UnauthorizedAccessException.java
│              │  │      │
│              │  │      ├─comment
│              │  │      │      CommentNotFoundException.java
│              │  │      │
│              │  │      ├─photo
│              │  │      │      FileUploadException.java
│              │  │      │
│              │  │      ├─post
│              │  │      │      AlreadyNotRecommendedException.java
│              │  │      │      AlreadyRecommendedException.java
│              │  │      │      PostNotFoundException.java
│              │  │      │
│              │  │      └─user
│              │  │              InvalidLoginException.java
│              │  │              UserNotFoundException.java
│              │  │
│              │  └─validation
│              │          ValidationGroups.java
│              │
│              ├─photo
│              │  ├─adapters
│              │  │  ├─controller
│              │  │  │      PhotoApiController.java
│              │  │  │
│              │  │  └─mapper
│              │  ├─application
│              │  │  ├─dto
│              │  │  └─service
│              │  │          PhotoService.java
│              │  │
│              │  ├─domain
│              │  │  ├─model
│              │  │  │      Photo.java
│              │  │  │
│              │  │  └─repository
│              │  │          PhotoRepository.java
│              │  │
│              │  └─usecase
│              │          PhotoCrudUseCase.java
│              │
│              ├─post
│              │  ├─adapters
│              │  │  ├─controller
│              │  │  │      PostApiController.java
│              │  │  │      PostController.java
│              │  │  │      PostFormController.java
│              │  │  │
│              │  │  ├─mapper
│              │  │  │      PostMapper.java
│              │  │  │
│              │  │  └─persistence
│              │  │          PostQueryRepositoryImpl.java
│              │  │
│              │  ├─application
│              │  │  ├─dto
│              │  │  │      PostDTO.java
│              │  │  │      PostSearchCond.java
│              │  │  │      PostUpdateDTO.java
│              │  │  │      
│              │  │  └─service
│              │  │          PostService.java
│              │  │
│              │  ├─domain
│              │  │  ├─model
│              │  │  │      Post.java
│              │  │  │      PostType.java
│              │  │  │
│              │  │  └─repository
│              │  │          PostQueryRepository.java
│              │  │          PostRepository.java
│              │  │
│              │  └─usecase
│              │          PostCrudUseCase.java
│              │          RecommendPostUseCase.java
│              │
│              └─user
│                  ├─adapters
│                  │  ├─controller
│                  │  │  ├─admin
│                  │  │  │      AdminApiController.java
│                  │  │  │      AdminFormController.java
│                  │  │  │
│                  │  │  └─user
│                  │  │          UserApiController.java
│                  │  │          UserController.java
│                  │  │          UserFormController.java
│                  │  │
│                  │  ├─mapper
│                  │  │      AdminMapper.java
│                  │  │      UserMapper.java
│                  │  │
│                  │  ├─persistence
│                  │  └─security
│                  │      │  WebSecurityConfig.java
│                  │      │
│                  │      ├─handler
│                  │      │  ├─admin
│                  │      │  │      AdminAuthFailureHandler.java
│                  │      │  │      AdminAuthSuccessHandler.java
│                  │      │  │
│                  │      │  └─user
│                  │      │          UserAuthFailureHandler.java
│                  │      │          UserAuthSuccessHandler.java
│                  │      │
│                  │      └─userdetails
│                  │              CustomUserDetails.java
│                  │              CustomUserDetailsService.java
│                  │
│                  ├─application
│                  │  ├─dto
│                  │  │  ├─admin
│                  │  │  │      AdminDTO.java
│                  │  │  │
│                  │  │  └─user
│                  │  │          UserDTO.java
│                  │  │          UserUpdateDTO.java
│                  │  │
│                  │  └─service
│                  │          UserService.java
│                  │
│                  ├─domain
│                  │  ├─model
│                  │  │      Role.java
│                  │  │      User.java
│                  │  │
│                  │  └─repository
│                  │          UserRepository.java
│                  │
│                  └─usecase
│                          LoginUseCase.java
│                          UserCrudUseCase.java
│
├─resources
│  │  application-docker.properties
│  │  application-local.properties
│  │  application.properties
│  │
│  ├─static
│  │  ├─css
│  │  │  ├─admin
│  │  │  │      login.css
│  │  │  │      manage.css
│  │  │  │
│  │  │  ├─comment
│  │  │  │  └─fragments
│  │  │  │          comment-edit.css
│  │  │  │          comments-fragment.css
│  │  │  │
│  │  │  ├─common
│  │  │  │      common.css
│  │  │  │
│  │  │  ├─error
│  │  │  │      client-error.css
│  │  │  │      not-found.css
│  │  │  │      server-error.css
│  │  │  │
│  │  │  ├─footer
│  │  │  │      footer.css
│  │  │  │
│  │  │  ├─header
│  │  │  │      header.css
│  │  │  │
│  │  │  ├─post
│  │  │  │  │  edit.css
│  │  │  │  │  list.css
│  │  │  │  │  view.css
│  │  │  │  │  write.css
│  │  │  │  │
│  │  │  │  └─fragments
│  │  │  │          posts-fragment.css
│  │  │  │
│  │  │  └─user
│  │  │      │  login.css
│  │  │      │  mypage.css
│  │  │      │  signup.css
│  │  │      │
│  │  │      └─fragments
│  │  │              edit.css
│  │  │              info.css
│  │  │              posts.css
│  │  │
│  │  ├─images
│  │  └─js
│  │      ├─admin
│  │      │      manage.js
│  │      │
│  │      ├─post
│  │      │      edit.js
│  │      │      list.js
│  │      │      view.js
│  │      │      write.js
│  │      │
│  │      └─user
│  │              login.js
│  │              mypage.js
│  │              signup.js
│  │
│  └─templates
│      ├─admin
│      │      login-form.html
│      │      manage-form.html
│      │
│      ├─error
│      │      client-error.html
│      │      not-found.html
│      │      server-error.html
│      │
│      ├─fragments
│      │  ├─comment
│      │  │      comment-edit.html
│      │  │      comments-fragment.html
│      │  │
│      │  ├─footer
│      │  │      footer.html
│      │  │
│      │  ├─header
│      │  │      header.html
│      │  │
│      │  ├─post
│      │  │      posts-fragment.html
│      │  │
│      │  └─user
│      │          edit.html
│      │          info.html
│      │          posts.html
│      │
│      ├─layout
│      │      base.html
│      │
│      ├─posts
│      │      edit-form.html
│      │      list-form.html
│      │      view-form.html
│      │      write-form.html
│      │
│      └─user
│              login-form.html
│              mypage.html
│              signup-form.html
│
└─typescript
    ├─admin
    │      manage.ts
    │
    ├─post
    │      edit.ts
    │      list.ts
    │      view.ts
    │      write.ts
    │
    └─user
            login.ts
            mypage.ts
            signup.ts
   </pre>
</details>

### **DB 구조**

데이터베이스 구조 이미지

## **개발 일지**
프로젝트를 진행하며 작성했던 개발 일지입니다.

### **개요**
- [게시판 프로젝트 개요](https://kwang2134.tistory.com/98)

### **설계**
- 유스케이스
  - [설계 - 유스케이스](https://kwang2134.tistory.com/102)
- 아키텍처
  - [설계 - 아키텍처[Hexagonal] & 패키지](https://kwang2134.tistory.com/105)
- 데이터베이스
  - [설계 - 데이터베이스](https://kwang2134.tistory.com/112)
- 클래스
  - [설계 - 도메인](https://kwang2134.tistory.com/117)
  - [설계 - 유스케이스](https://kwang2134.tistory.com/123)
  - [설계 - Application](https://kwang2134.tistory.com/137)
  - [설계 - Adapters(Controller 제외)](https://kwang2134.tistory.com/148)
  - [설계 - Adapters(Controller)](https://kwang2134.tistory.com/166)
- 프론트엔드
  - [설계 - 프론트엔드(Post)](https://kwang2134.tistory.com/173)
  - [설계 - 프론트엔드(User)](https://kwang2134.tistory.com/176)
- 설계서 최종
  - [설계서 - 최종](https://kwang2134.tistory.com/179)

### **개발**
- 백엔드(Backend)
  - [개발 - Domain](https://kwang2134.tistory.com/185)
  - [개발 - UseCase & DTO](https://kwang2134.tistory.com/186)
  - [개발 - Application(UserService)](https://kwang2134.tistory.com/188)
  - [개발 - Application(PostService)](https://kwang2134.tistory.com/189)
  - [개발 - Application(Comment, Photo)](https://kwang2134.tistory.com/190)
  - [개발 - 단위 테스트(domain, service)](https://kwang2134.tistory.com/191)
  - [개발 - Adapters(User)](https://kwang2134.tistory.com/193)
  - [개발 - Adapters(Post)](https://kwang2134.tistory.com/195)
  - [개발 - Adapters(Comment, Photo)](https://kwang2134.tistory.com/197)
  - [개발 - 통합 테스트](https://kwang2134.tistory.com/202)
  - [개발 - 수정](https://kwang2134.tistory.com/217)
- 프론트엔드(Frontend)
  - [개발 - 프론트엔드(User, Admin)](https://kwang2134.tistory.com/218)
  - [개발 - 프론트엔드(Post)](https://kwang2134.tistory.com/221)

## **프로젝트 결과**

### **결과물**
<details>
  <summary>회원</summary>

**로그인**
로그인 이미지

**회원가입**
회원가입 이미지

**마이페이지**
마이페이지 이미지

</details>

<details>
  <summary>게시글</summary>

**게시글 목록(일반)**
일반 이미지

**게시글 목록(공지)**
공지 이미지

**게시글 목록(인기)**
인기 이미지

**게시글 작성(비회원)**
비회원 작성

**게시글 작성(회원)**
회원 작성

**게시글 작성(매니저)**
매니저 작성

**게시글 조회**
조회 이미지

**게시글 조회(이미지 포함)**
이미지 포함

**게시글 수정**
수정 이미지
</details>

<details>
  <summary>관리자</summary>

**관리자 로그인**
로그인

**회원 관리**
관리
</details>

<details>
  <summary>에러</summary>

**404**
404

**500**
500
</details>

동작 영상
- [동작 영상](https://kwang2134.tistory.com/224)

### **후기**

웹 개발을 공부하면서, 실제로 서비스를 만들어보고 싶은 주제가 있었습니다. **"이런 사이트가 있었으면 좋겠다"** 는 생각을 직접 구현해보고 싶다는 마음에 프로젝트를 시작하게 되었습니다. 구현하고 싶었던 서비스는 복잡했고 잘 만들고 싶은 생각이 있었기에 연습 겸 배운 내용을 적용하고 연습하는 과정이 필요하다고 생각했습니다. 그리하여 게시판이라는 프로젝트가 시작되었고 연습이지만 체계적인 과정으로 개발을 진행해 보고 싶었기에 기록하며 설계를 진행했습니다. 볼품없지만 간단하게 디자인까지 마친 뒤 개발을 시작했습니다.

설계가 엉망이었다는 것은 개발에 들어가니 알 수 있었습니다. **"백 번 듣는 것보다 한 번 보는 게 낫다"** 는 말이 있는 것처럼 백 번 보는 것보다 한 번 해보는 게 낫다는 것을 확실하게 느낄 수 있었습니다. 이렇게 하면 되지 않을까? 이거는 이렇게 하면 될 것 같은데? 하며 설계했던 내용들이 실제로는 전혀 다르게 동작하고 있었고 디버깅하며 오류를 수정할수록 방향을 다시 고쳐 잡을 수 있었습니다. 프로젝트가 안정화되면서 조금씩 욕심이 생겼습니다.

예를 들어, **추천 기능** 을 구상할 때는 단순히 기능 구현에만 초점을 맞추고 제약 사항을 고려하지 않았습니다. 하지만 개발 도중, 제약 없이 추천 기능이 구현되면 한 사용자가 여러 번 추천할 수 있어 인기 글 탭의 의미가 없어질 수 있다는 점을 깨달았습니다. 이에 대한 해결책으로 **Redis** 를 사용하기로 했습니다. Redis는 NoSql 기반의 key-value 데이터베이스라는 것만 알고 있는 상태에서 추천하는 경우 해당 게시글과 사용자의 정보로 특정한 키를 만들어 추천 여부를 Redis에 저장하고 24시간이 지나면 자동으로 내역이 사라지게 구현했습니다.

**Docker** 도 이번 프로젝트에선 사용할 계획이 없었습니다. 로컬 pc 한 대로만 개발을 진행하고 완성된 결과물도 클라우드 서비스로 실행할 것도 아니고 개발에 사용했던 pc로 띄워볼 예정이었습니다. 그러나 중간에 다른 환경에서 개발해야 할 일이 생겼고 계획보다 의존성도 많아지고 다른 pc에선 잠깐씩만 개발할 것이라 많은 의존성을 설치하기 곤란한 상황에서 Docker가 생각났습니다. 프로젝트 환경을 컨테이너화시켜 어떤 환경에서도 동일하게 개발을 진행할 수 있다는 게 제가 알고 있던 Docker의 기능이고 상황과 맞아떨어지는 것 같아 Docker 의존성도 추가하게 되었습니다.

프론트엔드의 경우에는 지식이 거의 없다 싶어 생성형 AI의 도움을 많이 받아 제작했습니다. thymeleaf에 대해선 어느 정도 이해를 하고 했다 해도 css나 그 외의 부분은 거의 AI가 담당했다고 봐도 될 것 같습니다. 언어로는 JavaScript 대신 TypeScript를 사용하였습니다. 이전 코딩테스트를 위해 JavaScript를 학습했었습니다. JavaScript의 타입이 동적으로 코드 실행 시 지정되는 부분이 너무 익숙하지 않았고 코드 수정 시 복잡하고 이해하기 힘들었던 경험이 있어 제대로 알아보지도 않은 채 TypeScript 사용을 결정했습니다. 그러나 TypeScript로 백엔드를 구현하는 것이 아니라 간단한 프로젝트의 프론트 단에서 사용되는 수준으로는 JavaScript와의 큰 차이를 느낄 수 없었습니다. 자바스크립트와 거의 90퍼센트 이상 동일한 코드지만 TypeScript로 작성했기에 빌드 과정에서 npm을 통해 js 코드로 변환되는 과정을 거쳐야 했고 실제로 서비스할 프로그램이었다면 불필요한 과정만 덕지덕지 붙어 수행되는 꼴이 되어버렸습니다.

초기 구상보다 많이 복잡해지긴 했지만, 결과적으로 원하는 기능들을 구현했습니다. 백엔드 개발을 목표로 하고 있기에 자바 코드에 중점을 두고 시작한 프로젝트였지만 백엔드를 깊이 이해하기 위해서는 프론트엔드에 대한 이해도 필수적임을 깨달았습니다. 그리고 제대로 학습되지 않은 기술 (redis, docker)에 대해 의존성이 생겨버렸습니다. 학습을 목적으로 한 프로젝트였기에 여러 기술을 체험해 볼 수 있는 기회라고 생각하고 사용했지만, 추가적인 외부 기술들을 얕게 공부하는 것보다 스프링 프레임워크에 대해 더 깊게 공부하는 것이 중요하다고 느꼈습니다. 추천 기능만 해도 **"과연 redis를 도입하지 않고 깔끔하게 해결할 방법이 없었을까?"** 라는 의문이 들기도 하고 오류가 발생했을 때나 원인을 더 명확하게 파악하기 위해선 메인 기술에 대해 더 자세히 알아야 할 것 같았습니다. 한 번 해봤으니 다음 프로젝트를 진행할 땐 더 쉽게 방향을 잡고 나아갈 수 있을 것 같습니다.

읽어주셔서 감사합니다.


