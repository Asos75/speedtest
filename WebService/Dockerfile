FROM node:18-alpine

WORKDIR /app

COPY package*.json ./

COPY ./controllers ./controllers
COPY ./middleware ./middleware
COPY ./models ./models
COPY ./routes ./routes

COPY app.js ./

ARG MONGO_URL
ARG JWT_KEY
ARG IPNFO_KEY
ARG PORT
ARG VISION_IP
ARG AI

ENV MONGO_URL $MONGO_URL
ENV JWT_KEY $JWT_KEY
ENV IPNFO_KEY $IPNFO_KEY
ENV PORT $PORT
ENV VISION_IP $VISION_IP
ENV AI $AI

RUN npm install

EXPOSE 5000

CMD [ "npm","start" ]