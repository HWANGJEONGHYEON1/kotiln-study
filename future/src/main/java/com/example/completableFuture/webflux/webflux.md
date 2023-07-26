# Webflux

## Netty
- 비동기 이벤트 기반의 어플리케이션
- HTTP 프로토콜 뿐만 아니라 다양한 프로토콜 지원
- JAVA IO, NIO, Selector 기반 적은 리소스로 높은 성능 보장
- 불필요한 메모리 copy를 최소한으로 유연하면 확장 가능한 이벤트모델 기반
- 서버와 클라이언트 모두 지원

### NIO EventLoop
- EventExecutor, TaskQueue, Selector 포함 
- EventExecutor: task를 실행하는 스레드 풀
- TaskQueue: task를 저장하는 queue, eventExecutor가 즉시 task를 수행하지않고 taskQueue에 넣은 후 처리가능
- Selector: I/O multiplexing 지원
- io task
  - NIOEventGroup을 직접생성할 수 없어 NIOEventLoopGroup사용한다.
    - 서버소케채넛을 생성하고 accept network i/o 이벤트를 이벤트루프에 등록
    - 하나의 NIOEventLoopGroup에 여러개 채널 등록 가능
    - IO 이벤트 완료시 채널의 파이프라인 실행
  
### Channel
- ChannelFuture
  - futureListener 등록/삭제 지원 -> 비동기가능
  - addListener: 작업이 완료되면 수행할 리스너등록
  - sync: 작업이 완료될 때까지 블로킹 된다.
- NioServerSockerChannel
  - netty에서는 java nio의 채널을 사용하지 않고 거의 구현
  - Channel, ServerSocketChannel 모두 자체 구현
  - AbstractChannel: ChannelPipeline을 갖는다
  - AbstractNioChannel: 내부적으로 소케체넛을 저장하고 등록할 때 java nio selector에 등록
- ChannelPipeline
  - 채널의 IO 이벤트가 준비되면 이벤트루프 파이프라인 실행
    - IO task 해당 
    - 파이프라인에서는 결과로 IO 작업을 수행
  - ChannelHandlerContext의 연속
  - 각각의 context는 LinkedList의 형태로 next, prev를 통해 이전 혹은 다음에 접근
  - inbound I/O는 next
  - outbound I/O는 prev
  - 내부
    - EventExecutor와 ChannelHandler를 포함
    - ChannelHandler는 다음 컨텍스트에 전달할 수 있고 그냥 처리도 가능하다.
  - ChannelHandlerContext에서 별도의 EventExecutor 를 지원하는 이유 ?
    - ChannelHandler에서 시간이 오래걸리는 연산을 진행한다면?
      - EventLoop 스레드에서 해당 ChannelHandler에서 블로킹
      - EventLoop에 등록된 다른 channelIO 처리 또한 블로킹
      - 해당 ChannelHandler에서는 EventLoop 스레드가 아닌 다른 스레드 풀을 사용한다면?
      - EventExecutor 있다면..?
        - Next context가 다른 스레드풀에서 동작해야하는구나 판단
        - `직접 이벤트 처리르하지 않고 executor.execute로 taskqueue에 넣고 eventLoop 스레드는 복귀.`
  
### Context
- 파이프라인 내부 어디에서든지 접근 가능한 k, v 저장소
- 특정 key의 value에 접근하고 k의 v를 수정할 수 있는 수단을 제공
- map과 유사
- 읽기 전용인 ContextView와 쓰기를 할 수 있는 Context로 구분
  - contextWrite
    - Context를 인자로 받고, Context를 반환하는 함수형 인터페이스 제공
    - Context 값을 추가하거나 변경, 삭제 가능
    - Context immutable하기 때문에 스레드 세이프하다.
- defer
  - publisher를 생성하는 Consumer를 인자로받아 publisher 생성
  - 생성된 publisher의 이벤틀르 아래로 전달

### HttpHandler
- ServerHttpRequest, ServerHttpResponse 인자로 받고 
- 응답을 돌려줘야하는 시점을 반환하는 함수형 인터페이스
