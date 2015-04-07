# QRReader

이 코드는 단순한 프로토타입을 위한 코드입니다.
사용에 주의하십시오.

이 소스로 컴파일된 apk 파일은 아래 링크에서 받으실 수 있습니다. (기준 커밋:  0cd9d04)

https://github.com/WindSekirun/QRReader/blob/master/app/app-release.apk

이 소스는 아래의 라이브러리를 사용하고 있습니다.

* https://github.com/zxing/zxing
* https://github.com/dlazaro66/QRCodeReaderView

이 소스는 현재 아래의 기능을 수행할 수 있습니다.

* 학번으로 QR코드 생성
* QR코드 내용 읽기
* 서버로의 저장을 위해 인식한 QR코드를 SQLite 형태로 저장

이 소스는 아래의 기능을 포함할 예정입니다.

* 서버에서 기본 데이터 수집
* 서버에서 수집한 기본 데이터와 SQLite를 대조해 최종적으로 서버에 보낼 Hashmap 구현
* Hashmap를 서버로 보내 날짜별로 테이블이 생성, 값이 넣어지도록 구현.

이 소스는 아래의 소스를 사용하고 있습니다.

* NaraeAsync, Made by WindSekirun
* NaraePreference, Made by WindSekirun

이 앱의 스크린샷 [더보기: https://github.com/WindSekirun/QRReader/tree/master/screenshot ]
![alt tag](https://raw.githubusercontent.com/WindSekirun/QRReader/master/screenshot/2.png)

Licensed by MIT License, Copyright 2015 WindSekirun. 
You can see license text at https://github.com/WindSekirun/QRReader/blob/master/License.md 
