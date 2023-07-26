## 동기 비동기
동기 : caller는 callee에 관심이 있음, caller는 결과를 이용해서 action을 수행
비동기 : caller는 callee의 결과에 관심이 없다 callee는 결과이용해서 callback을 수행
동기 블로킹: 어플리케이션은 커널 IO 작업을 완료할 때 까지 기다린다. 그 후 결과를 가지고 작업을 수행
동기 논블로킹: 어플리케이션은 주기적으로 IO작업이 완료되었는지 확인한다. 중간중간 자기의 일을 하면고 작업이 완료되면 그 때 본인의 일을 수행
비동기 논블로킹: 어플리케이션은 IO 작업의 요청을 보내고, 본인의 일을 한다. 작업이 완료되면 커널은 시그널을 보내거나 콜백을 호출

## CompletableFuture
method reference

```java
var target = new Person("F");
Consumer<String> staticPrint = MethodReferenceExample::print;

Stream.of("a", "b", "c")  
        .map(Person::new) // 생성자
        .filter(target::compareTo) // 메서드 
        .map(Person::getName) // 인스턴스 
        .forEach(staticPrint); // 스태틱
```

Future
- 비동기작업을 수행
- 해당 작업이 완료되면 결과를 반환하는 인터페이스
- isDone(): running 중이 아니면 무조건 true
- isCanceled(): task가 명시적으로 취소된 경우 true
- get(): 결과를 구할 때까지 thread가 계속 block, 무한 루프나 오랜 시간이 걸린다면 스레드가 블록 상태
  - get(long timeout, TimeUnit unit) : 결과를 타임아웃 동안만 블록, 응답이 반환되지 않으면 에러 반환
- cancel(boolean mayInterruptIfRunning) 
  - future 작업 취소, 취소할 수 없는 상황이라면 false
  - 이미 취소된 경우, mayInterruptIfRunning false라면 취소가능 
- 한계
  - cancel을 제외하고 외부에서 컨트롤하기 어려움
  - 반환된 결과를 get으로 접근해야하기 때문에 비동기로 처리하기 어렵다.
  - 완료되거나 에러가 발생했는지 구분이 어렵다.

CompletableStage
- 비동기 작업을 수행
- 50개에 가까운 연산자들을 활용하여 비동기 일을 실행하고 chaining을 이용한 조합 가능
- 에러를 처리하기 위한 콜백 제공
- 해당 작업이 완료되면 결과를 처리하거나 다른 CompletableStage를 연결
- ForkJoinPool - thread pool
  - CompletableFuture는 내부적으로 비동기
  - 할당된 cpu 코어 - 1
  - 데몬 스레드, main 스레드가 종료되면 즉각 종료됨
  - Task를 fork를 통해서 subtask로 나누고 스레드 풀에서 steal work 알고리즘을 이용하여 균등하게 처리 join을 통해서 결과를 생성
- thenAccept[Apply]
  - Consumer를 파라미터로 받는다. 이전 task로 값을 받지만 넘기진 않음 다음 task에게 null이 전달
  - 값을 받아서 action만 수행하는 경우 유용
  - then (사용 자제)
    - Stage의 상태에 따라
      - isDone true : then을 호출한 caller 스레드에서 action 실행
      - isDone false: then이 호출된 callee 스레드에서 action 실행
  - thenAsync
    - thread pool에 있는 스레드에서 action 실행

CompletableFuture
- supplyAsync
  - Supplier : 아무런 값이 없는상태에서 값을 만들어 내려줌 CompletableFuture<Object>
- runAsync
  - Runnable : CompletableFuture<Void>
- complete
  - CompletableFuture가 완료되지 않았다면 주언진 값으로 채움
  - complete에 의해 상태가 바뀌었다면 true, 아니라면 false
- allOf
  - 여러 CompletableFuture를 모아 하나의 CompletableFuture를 리턴
  - 모든 CompletableFuture가 완료되면 상태가 done
  - Void를 반환하므로 각각의 값에 get으로 접근해야함
- 상태
  - isCompleteExceptionally : exception에 의헤 complete가 되었는지
  - canceled
  - completed
- 한계
  - 지연로딩 제공지 않음 => 함수를 호출 시 즉시 작업 실행
  - 지속적으로 생성되는 데이터를 처리하기 어려움 => 데이터를 반환하고 나면 다시 다른 값을 전달하기 어려움

ExecutorService
- 스레드 풀을 이용하여 비동기적으로 작업을 실행하고 관리
- 별도의 스레드를 생성하고 관리하지 않아도 되므로, 간결하게 유지가능
- 스레드풀을 사용하여 효율적으로 관리
- execute: Runnable 인터페이스를 구현하여 작업을 스레드 풀에서 비동기적으로 실행
- submit: Callable 구현하여 비동기적으로 실행하고, 해당 작업의 결과를 Future<T>로 반환
- shutdown: ExecutorService를 종료.

Executors
- newSingleThreadExecutor: 단일 스레드로 구성된 스레드풀 생성, 한번에 하나의 작업만 실행
- newFixedThreadPool: 고정된 크기의 스레드 풀 생성
- newCachedThreadPool: 사용 가능한 스레드가 있다면 재사용. 일정 시간동안 사용하지 않으면 회수, 없다면 새로 생성하여 작업을 처리 (예측가능해야함)
- newScheduledThreadPool: 스케줄링 기능을 갖춘 고정크기의 스레드풀을 생성, 주기적이거나 지연이 발생하는 작업을 실행 
- newWorkStealingPool: work steal 알고리즘을 사용하는 ForkJoinPool 생성

## 일반적인 프로그래밍 패러다임
- stream을 이용한 흐름
  - 구성 요소는 서로 비동기적으로 메시지를 주고 받으며, 독립적인 실행을 보장
    - caller는 collect를 통해 값을 조회해야한다. caller와 callee는 동기적으로 동작한다.
  - 메시지 큐를 생성하고 배압을 적용하여 부하를 관리하고 흐릉을 제어한다. -> stream이 메시지 큐의 역할을 하지만, 부하를 관리할 수 없다.
- Reactive stream
  - 구성요소는 서로 비동기적으로 메시지를 주고 받으며, 독립적인 실행을 보장
    - callee는 publisher를 반환하고 caller는 subscriber를 등록한다. 이 과정에서 caller와 callee는 비동기적으로 동작
  - 메시지 큐를 생성하고 배압을 적용하여 부하를 관리하고 흐름을 제어한다.
    - publisher는 메시지 큐를 생성해서 부하를 관리하고 흐름을 제어한다. back-pressure를 조절할 수 있는 수단을 제공
- event-driven vs message-driven
  - message: event, command, query 등 다양한 형태를 수용
  - message-driven: 메시지를 비동기적으로, 가능하다면 배압을 적용해서 전달하는 형태에 집중
  - event-driven: 메시지의 형태를 이벤트로 제한
  - completion, error 심지어 값 까지도 이벤트의 형태로 전달

## 리엑트 스트림 구조
- Publisher : 데이터 혹은 이벤트를 제공
- Subscriber : 데이터 혹은 이벤트를 제공받음
- Subscription: 데이터 흐름을 조절 


