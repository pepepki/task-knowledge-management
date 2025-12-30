# Task & Knowledge Management App

Spring Boot 4 のタスク管理およびナレッジ共有プラットフォームです。
現在は **Sprint 2 (タスク登録機能の実装とUI/テストの刷新)** ステップです。

## 🚀 プロジェクトの現状: Sprint 2 完了　タスク登録機能の実装とUI/テストの刷新
### Sprint 1
- **インフラ:** Docker Compose による全環境（DB/Backend/Frontend）のコンテナ化
- **DB:** MySQL 8.0 の構築と初期データの疎通
- **Backend:** Spring Boot 4 (Java 25) による REST API の実装（一覧取得機能）
- **Frontend:** React (Vite + TypeScript) による API 連携とデータ表示

### Sprint 2
- **Backend:** 登録機能の実装とserviceレイヤーでのトランザクション機構実装
- **Frontend:** 登録機能の実装。デザイン性の向上
- **Backend/Frontend:** Unitテストの実装


## 🛠 利用技術
### Backend
- **Java 25** (最新のLTS機能を活用)
- **Spring Boot 4.0.0** (Spring Framework 7 ベース)
- **Gradle 8.x**
- **Spring Data JPA**
- **MySQL Driver**

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

## 📊 設計図 (Sprint 2 時点)

### ER図
```mermaid
erDiagram
    task {
        bigint id PK "Auto Increment"
        varchar title "NOT NULL"
        text description
        varchar status "DEFAULT 'TODO'"
    }

```

### シーケンス図
```mermaid
sequenceDiagram
    participant User as ユーザー
    participant React as React (TaskForm)
    participant Service as Spring Boot (Service)
    participant DB as MySQL

    User->>React: タイトルと説明を入力して「保存」
    React->>React: useStateで状態管理
    React->>Service: POST /api/tasks (JSON: TaskRequest record)
    Note right of Service: @Transactional開始
    Service->>DB: INSERT INTO tasks
    DB-->>Service: 保存完了
    Service-->>React: 200 OK (Task entity)
    Note right of Service: @Transactional終了
    React->>React: 一覧を再取得 (fetchTasks)
    React-->>User: テーブルに新しいタスクが表示される
```

## 各サービスへのアクセス
- Frontend (React): http://localhost:5173
- Backend API: http://localhost:8080/api/tasks
- phpMyAdmin (DB管理): http://localhost:8081
