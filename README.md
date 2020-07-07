# PageRank
검색엔진의 페이지 신뢰도 계산을 모방하여 신뢰도를 올리기위한 가장 효율적인 링크팜을 테스트 하는 프로젝트입니다.  
![spamfarm](https://user-images.githubusercontent.com/49792776/83967221-7d8e7580-a8fa-11ea-852e-83d2a3700cd5.PNG)  
링크팜의 수가 늘어남에 따라 랭크가 올라가는 것을 확인 할 수 있습니다.  
# 신뢰도 계산
한 링크에서 다음링크로 무작위로 진행하며 모든 링크를 계산합니다.
## 알고리즘 A: random walk의 한 단계를 시뮬레이션합니다.
![random_walk_algorithm](https://user-images.githubusercontent.com/49792776/86797666-e5c3a900-c0aa-11ea-9b7f-6c883d4c3105.JPG)  
## PageRank 계산
![page_rank_algorithm](https://user-images.githubusercontent.com/49792776/86797662-e4927c00-c0aa-11ea-8fda-38a423c7abf4.JPG)  
위의 알고리즘 A를 반복하여 PageRank를 계산합니다.
