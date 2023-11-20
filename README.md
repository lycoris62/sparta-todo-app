# sparta-todo-app

> 개인적인 일이 있어 과제를 늦게 시작해 제대로 완료하지 못했습니다.
>       
> [참고할 점]    
> * JWT 필터가 1개 입니다. 강의와 달리 로그인 및 회원가입은 AuthController가 하고 있습니다. 필터에서는 shouldNotFilter 메서드를 이용하여 통과시켰습니다.
> * 디렉토리는 크게 `domain`과 `global`로 나뉩니다.
> * `domain` 에서는 `인증(auth)`, `할일카드(todocard)`, `댓글(comment)` 도메인으로 나뉘고, 그 안에서 컨트롤러, 서비스, 레포지토리 등 나뉩니다.
> * `global` 에서는 전반적으로 쓰이는 `security`나 `error` 처리를 맡고 있습니다. 
> * 선택 요구사항이나, 저번 리뷰때 알려주셨던 mapStruct는 공부하고 적용할 시간이 없었습니다..
> * ERD나 API 명세서 역시 작성할 시간이 없었습니다..
> * User 와 TodoCard 는 1:N 관계이며, User는 필수로 있어야 하며 TodoCard 는 0개 이상입니다.
> * TodoCard 와 Commant 는 1:N 관계이며, TodoCard는 필수로 있어야 하며 Comment 는 0개 이상입니다.
> * User 와 Comment 는 1:N 관계이며, User는 필수로 있어야 하며 Comment 는 0개 이상입니다.
> * 유저나 할일카드의 삭제는 요구사항에 없었고, 수정에 영향받는 것은 없었으므로, Cascade 설정은 하지 않았습니다.
> * 관계 설정을 제대로 한 것이 맞는지 모르겠습니다..
> * API 명세 중에 `할일카드 완료` 부분의 끝 `/finish`을 쓰는게 안 맞는 것 같은데 더 나은 방법이 있을까요?
> * API 명세 중에 `댓글 작성` 부분에서 `/{todoCardId}` 부분을 쓰는게 맞을까요? 더 나은 방법이 궁금합니다. 



## 디렉토리 구조 
```
├── TodoappApplication.java
├── domain
│   ├── auth
│   │   ├── controller
│   │   │   └── AuthController.java
│   │   ├── dto
│   │   │   └── AuthRequestDto.java
│   │   ├── entity
│   │   │   ├── User.java
│   │   │   └── UserRoleEnum.java
│   │   ├── repository
│   │   │   └── UserRepository.java
│   │   └── service
│   │       └── AuthService.java
│   ├── comment
│   │   ├── controller
│   │   │   └── CommentController.java
│   │   ├── dto
│   │   │   ├── request
│   │   │   │   ├── CommentCreateRequestDto.java
│   │   │   │   └── CommentEditRequestDto.java
│   │   │   └── response
│   │   │       └── CommentResponseDto.java
│   │   ├── entity
│   │   │   └── Comment.java
│   │   ├── repository
│   │   │   └── CommentRepository.java
│   │   └── service
│   │       └── CommentService.java
│   ├── model
│   │   └── BaseEntity.java
│   └── todocard
│       ├── controller
│       │   └── TodoController.java
│       ├── dto
│       │   ├── request
│       │   │   ├── TodoCardCreateRequestDto.java
│       │   │   └── TodoCardEditRequestDto.java
│       │   └── response
│       │       ├── TodoCardDetailResponseDto.java
│       │       └── TodoCardSimpleResponseDto.java
│       ├── entity
│       │   └── TodoCard.java
│       ├── repository
│       │   └── TodoCardRepository.java
│       └── service
│           └── TodoCardService.java
└── global
    ├── config
    │   └── security
    │       ├── CustomUserDetails.java
    │       ├── CustomUserDetailsService.java
    │       ├── ExceptionHandleFilter.java
    │       ├── WebSecurityConfig.java
    │       └── jwt
    │           ├── JwtFilter.java
    │           └── JwtUtil.java
    └── error
        ├── ErrorCode.java
        ├── ErrorResponse.java
        ├── GlobalExceptionHandler.java
        └── exception
            ├── AccessDeniedException.java
            ├── DuplicateUsernameException.java
            ├── InvalidTokenException.java
            └── UserNotFoundException.java
```
위 디렉토리 구조는 [스프링 입문 과제 - 익명게시판](https://github.com/lycoris62/sparta-anonymous-board) 레포의 리드미에 개략적으로 설명되어 있습니다. 

## API 명세 
![스크린샷 2023-11-20 오후 7 47 52](https://github.com/lycoris62/sparta-todo-app/assets/55584664/06b4b56d-c060-4aab-994c-1d8f5253a6fc)




