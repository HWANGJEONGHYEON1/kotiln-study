## Java NIO
- java 1.4 ~
- java new input/output (non x)
- 파일과 네트워크에 데이터를 읽고 쓸 수 있는 API 제공
- buffer 기반
- non-blocking 지원
- selector, channel 지원하여 높은 성능

### Java NIO vs Java IO

|         |java NIO|Java IO|
|---------|---|---|
| 데이터흐름   |양방향|단방향|
| 종류      |Channel|InputStream, OutputStream|
| 블로킹 여부  |blocking/non~ 지원|blocking만 지원|
| 데이터의 단위|buffer|byte or character|
|특이사항|selector 지원||

### 채널과 버퍼
- 데이터를 읽을 때, 적절한 크기의 버퍼를 생성하고 채널의 read() 메서드를 이용하여 buffer에 저장
- 데이터를 쓸 때, 먼저 버퍼에 데이터를 저장하고 채널의 write() 메서드를 사용하여 목적지에 전달
- clear() 메서드를 통해 초기화하여 다시사용
- 버퍼 
  - capacity: 버퍼가 저장할 수 있는 데이터의 최대크기 버퍼 생성시에 결정되며 수정 불가
  - position: 버퍼에서 현재 위치를 가르킨다. 데이터를 읽거나 쓸 때 해당위치부터 시작, 버퍼에 1byte 추가될 때마다 1증가
  - limit: 버퍼에서 데이터를 읽거나 쓸 수 있는 마지막 위치. 리밋 이후로 데이터를 읽거나 쓰기 불가
  - mark: 현재 positon 위치를 mark()로 지정할 수 있고, reset() 호출 시 position을 mark로 이동
  - 0 <= mark <= position <= limit <= capacity