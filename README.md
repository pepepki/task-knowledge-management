# Task & Knowledge Management App

Spring Boot 4 のタスク管理およびナレッジ共有プラットフォームです。
現在は **Sprint 7 (Redisによるキャッシュ処理の実装)** ステップです。

## 🛠 利用技術
### Backend
- **Java 25** 
- **Spring Boot 4.0.0** (Spring Framework 7 ベース)
- **Gradle 8.x**
- **Spring Data JPA**
- **MySQL Driver**
- **Redis**

### Frontend
- **React 18+**
- **Vite**
- **TypeScript**
- **Axios** (API通信)

### Infrastructure
- **Docker / Docker Compose**
- **MySQL 8.0**

## 🔐 セキュリティと環境設定
本プロジェクトでは、DBのユーザー名やパスワードなどの機密情報を保護するため、**環境変数 (.env)** を利用しています。

- `.env` ファイルは Git 管理から除外（`.gitignore`）されています。
  - DB_PASSWORD=パスワード となる`.env` ファイルをプロジェクト直下に作成してください。
- 各コンテナの設定は `docker-compose.yml` を通じて `.env` から注入されます。

## 📊 設計図 (Sprint 6 時点)

### ER図
```mermaid
erDiagram
    USER ||--o{ TASK : "owns (作成者)"
    USER ||--o{ TASK : "is assigned to (担当者)"
    
    USER {
        bigint id PK "自動採番 (BIGINT)"
        string username "ユニーク制約 / NotNull"
        string password "NotNull"
    }

    TASK {
        bigint id PK "自動採番 (BIGINT)"
        string title "NotNull"
        string description "Nullable"
        string status "TODO/ASSIGN_WAITING/PROGRESS/IN_REVIEW/DONE"
        bigint user_id FK "作成者 (Owner)"
        igint assignee_id FK "担当者 (Assignee) - Nullable"
    }

```

### 画面遷移図
```mermaid
stateDiagram-v2
    direction LR
    [*] --> サインアップ
    サインアップ --> ログイン: アカウント作成
    ログイン --> タスク一覧: 認証成功
    タスク一覧 --> ログイン: ログアウト
    
    state タスク一覧 {
        direction TB
        一覧表示 --> 新規作成
        新規作成 --> 一覧表示
        一覧表示 --> ステータス更新
    }

```

### シーケンス図
基本機能
```mermaid
sequenceDiagram
    autonumber
    actor User as ユーザー
    participant Front as フロントエンド (React)
    participant Auth as 認証フィルタ (Spring Security)
    participant Service as TaskService
    participant DB as データベース (MySQL)

    Note over User, DB: 【タスク一覧の表示】
    User->>Front: ページにアクセス
    Front->>Auth: GET /api/tasks (Basic認証情報)
    Auth->>Auth: ユーザーを特定 (Principal)
    Auth->>Service: getTasksByUsername(username)
    Service->>DB: findByUser(user)
    DB-->>Service: そのユーザーのタスクのみ返却
    Service-->>Front: JSON形式のリスト
    Front-->>User: テーブル形式で表示

    Note over User, DB: 【ステータス更新 (ガード処理あり)】
    User->>Front: 「完了」ボタンをクリック
    Front->>Auth: PATCH /api/tasks/{id}/toggle
    Auth->>Auth: ユーザーを特定 (Principal)
    Auth->>Service: toggleTaskStatus(id, username)
    
    Service->>DB: findById(id)
    DB-->>Service: Task情報 (所有者を含む)
    
    alt 所有者が一致する場合
        Service->>DB: save(更新されたTask)
        DB-->>Service: 成功
        Service-->>Front: 200 OK
        Front-->>User: 打ち消し線を表示
    else 所有者が一致しない場合 (不正アクセス)
        Service-->>Front: 403 Forbidden (AccessDeniedException)
        Front-->>User: 「権限がありません」と表示
    end

```

Redis部分の抜粋
```mermaid
sequenceDiagram
    participant Front as Frontend (React)
    participant Back as Backend (Spring Boot)
    participant Redis as Redis (Cache)
    participant DB as DB (MySQL)

    Front->>Back: GET /api/tasks (タスク取得)
    Back->>Redis: キャッシュがあるか確認
    
    alt キャッシュあり (Cache Hit)
        Redis-->>Back: タスクリストを返却
    else キャッシュなし (Cache Miss)
        Back->>DB: データベースから取得
        DB-->>Back: タスクリスト
        Back->>Redis: データを保存 (TTL設定)
    end
    
    Back-->>Front: JSONを返却

```

## 各サービスへのアクセス
- Frontend (React): http://localhost:5173
- Backend API: http://localhost:8080/api/tasks
- phpMyAdmin (DB管理): http://localhost:8081

## 🚀 プロジェクトの現状: Sprint 7 完了　Redisによるキャッシュ処理の実装
### Sprint 1
- **インフラ:** Docker Compose による全環境（DB/Backend/Frontend）のコンテナ化
- **DB:** MySQL 8.0 の構築と初期データの疎通
- **Backend:** Spring Boot 4 (Java 25) による REST API の実装（一覧取得機能）
- **Frontend:** React (Vite + TypeScript) による API 連携とデータ表示

### Sprint 2
- **Backend:** 登録機能の実装とserviceレイヤーでのトランザクション機構実装
- **Frontend:** 登録機能の実装。デザイン性の向上
- **Backend/Frontend:** Unitテストの実装

### Sprint 3
- **Backend:** 編集・削除機能の実装
- **Frontend:** 編集・削除機能の実装

### Sprint 4
- **Backend:** Spring Securityによる認証機能実装(簡易的にBasic認証)
- **Frontend:** ユーザー登録、ログイン機能実装　※まだタスクとの関連付けは未実装

### Sprint 5
- **Backend:** ユーザー別にタスクのCRUDを行うように実装

### Sprint 6　他ユーザーへのタスクのアサイン
- **Backend:** 担当者の設定、タスク登録後の担当者変更機能実装
- **Frontend:** 担当者の設定、タスク登録後の担当者変更機能実装

### Sprint 7
- **Backend:** Redisによるキャッシュ処理の実装

### Sprint 8(予定)　タスク期限日の設定

### Sprint 9(予定)　個別タスクの詳細画面表示
