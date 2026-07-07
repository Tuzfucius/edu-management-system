# ============================
# Stage 1: Node 构建前端
# ============================
FROM node:20-alpine AS builder

WORKDIR /build

# 先复制依赖文件，利用 Docker 层缓存
COPY frontend/package*.json ./
RUN npm ci

# 复制源码并构建
COPY frontend/ .
RUN npm run build

# ============================
# Stage 2: Nginx 托管
# ============================
FROM nginx:alpine

# 复制构建产物到 nginx 的 /edu/ 子路径下
COPY --from=builder /build/dist /usr/share/nginx/html/edu

# 复制 nginx 配置文件
COPY docker/nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
