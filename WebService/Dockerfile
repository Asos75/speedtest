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

ENV MONGO_URL $MONGO_URL
ENV JWT_KEY $JWT_KEY
ENV IPNFO_KEY $IPNFO_KEY

RUN npm install

EXPOSE 3000

CMD [ "npm","start" ]