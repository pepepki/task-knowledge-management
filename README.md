# Task & Knowledge Management App

Spring Boot 4 のタスク管理およびナレッジ共有プラットフォームです。
現在は **Sprint 1 (垂直貫通の開通)** ステップです。

## 🚀 プロジェクトの現状: Sprint 1 完了
- **インフラ:** Docker Compose による全環境（DB/Backend/Frontend）のコンテナ化
- **DB:** MySQL 8.0 の構築と初期データの疎通
- **Backend:** Spring Boot 4 (Java 25) による REST API の実装（一覧取得機能）
- **Frontend:** React (Vite + TypeScript) による API 連携とデータ表示

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

## 📊 設計図 (Sprint 1 時点)

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
    participant User
    participant React
    participant API
    participant DB

    User->>React: 表示
    React->>API: GET /api/tasks (JSON)
    API->>DB: SELECT * FROM task..
    DB-->>API: Success
    API-->>React: 200 OK
    React-->>User: 一覧を更新して表示

```

## 各サービスへのアクセス
- Frontend (React): http://localhost:5173
- Backend API: http://localhost:8080/api/tasks
- phpMyAdmin (DB管理): http://localhost:8081
