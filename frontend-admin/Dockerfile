# ---- Build Stage ----
FROM node:22-alpine AS builder
WORKDIR /app

# Copy package files first for better layer caching
COPY package.json package-lock.json ./
RUN npm config set registry https://registry.npmmirror.com && npm ci

# Copy source and build
COPY . .
# Use the docker env for Docker image build (VITE_API_BASE_URL=http://localhost:8000)
RUN npm run build:docker

# ---- Run Stage ----
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 6173

CMD ["nginx", "-g", "daemon off;"]
