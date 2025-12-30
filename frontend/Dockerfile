FROM node:20-slim

WORKDIR /app

# 設定ファイルを先にコピー（ビルド高速化のため）
COPY package*.json ./
RUN npm install

# ソースコードをコピー
COPY . .

# Viteのデフォルトポートを開放
EXPOSE 5173

# コンテナ起動時に開発サーバーを立ち上げる
# --host を付けないとDocker外部からアクセスできません
CMD ["npm", "run", "dev", "--", "--host"]