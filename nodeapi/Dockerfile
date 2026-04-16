#Build stage for nodeapi
FROM node:16-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install --only=production
COPY ./ ./
EXPOSE 5000
CMD ["node", "server.js"]
