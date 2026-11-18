# 🤝 Contributing Guide

인계점 Backend 리포지토리의 협업 규칙입니다.

## 🤝 Branch Strategy & Conventions

### 📌 브랜치 이름 규칙
`분류/기능-요약` 형식을 사용하며, **소문자와 하이픈(-)** 만 사용합니다. (Kebab Case)
* 예시: `feature/email-sender` (O) / `feature/emailSender` (X)

| 브랜치명 | 설명 |
|---|---|
| `main` | 배포 가능한 안정화 상태 (직접 Push 금지) |
| `develop` | 다음 버전을 위한 통합 개발 브랜치 |
| `feature/...` | 새로운 기능 개발 |
| `fix/...` | 버그 수정 |
| `refactor/...` | 기능 변화 없는 코드 구조 및 로직 개선 |

### 📌 커밋 메시지 규칙
`[Type] 제목` 형식을 사용하며, 제목은 50자 이내의 **명사형 종결**을 권장합니다.
* (선택) 상세 내용: 무엇을, 왜 변경했는지 본문에 추가 설명

| 태그(Type) | 의미 (Description) | 사용 예시 |
|---|---|---|
| `[feat]` | 새로운 기능 추가 | `[feat] 소셜 로그인 API 구현` |
| `[edit]` | 버그 또는 기존 기능 수정 | `[edit] 결제 금액 계산 오류 수정` |
| `[docs]` | 문서 작업 (README 등) | `[docs] API 명세서 업데이트` |
| `[style]` | 코드 포맷팅 (로직 변경 X) | `[style] 코드 들여쓰기 및 세미콜론 정리` |
| `[refactor]` | 코드 리팩토링 (기능 변경 X) | `[refactor] 중복되는 유틸 함수 통합` |
| `[test]` | 테스트 코드 추가/수정 | `[test] 회원가입 서비스 유닛 테스트 작성` |
| `[chore]` | 빌드, 패키지 매니저 등 | `[chore] .gitignore 파일 수정` |

---

## 🔄 협업 워크플로우 (The Loop)

**[1. 최신화] → [2. 브랜치 생성] → [3. 작업/저장] → [4. 업로드] → [5. PR 및 리뷰] → [6. 병합(완료)]**

### 1. 작업 시작 전 최신화 (Sync)
로컬의 `develop` 브랜치를 원격 저장소의 최신 상태로 동기화합니다.
```bash
git checkout develop
git pull origin develop
```
### 2. 작업 브랜치 생성 (Start)
develop에서 내가 작업할 전용 공간을 만듭니다.

```Bash
git checkout -b feature/작업명
```
### 3. 작업 및 커밋 (Coding)
규칙에 맞춰 코드를 작성하고 커밋합니다.

```Bash
git add .
git commit -m "[feat] 핵심 작업 내용 요약"
```

### 4. 깃허브로 업로드 (Push)
내 로컬 브랜치를 원격 저장소로 보냅니다.

```Bash
git push origin feature/작업명
```

### 5. PR 생성 및 리뷰 (Pull Request)
- GitHub 저장소에 접속하여 Compare & pull request 버튼을 클릭합니다.

- 🚨 주의: Base 브랜치가 main이 아닌 **develop**인지 반드시 확인합니다.

- 제목과 작업 내용을 작성하고, 리뷰어(Reviewers)를 지정한 뒤 PR을 생성합니다.

- 피드백을 받아 코드를 수정했다면 다시 git add → git commit → git push를 진행합니다. (기존 PR에 자동 반영됨)

### 6. 작업 종료 및 청소 (Clean up)
PR이 승인(Approve)되어 develop에 병합되면, 다음 작업을 위해 로컬 환경을 정리합니다.

```Bash
git checkout develop
git pull origin develop
git branch -d feature/작업명  # (선택) 다 쓴 브랜치 삭제
```
