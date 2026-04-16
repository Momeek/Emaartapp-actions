#build for client 
FROM node:16-alpine AS builder
WORKDIR /app
 COPY package*.json ./
 
 #install dependency
 RUN npm install 
 #Copy source code and build
 COPY ./ ./
 RUN npm run build 

 #Production build
 FROM nginx:alpine
 WORKDIR /app
 COPY --from=builder /app/dist/client /usr/share/nginx/html

 
 EXPOSE 80
 CMD ["nginx", "-g","daemon off;"]
