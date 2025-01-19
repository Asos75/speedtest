FROM node:18-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

ARG API_ENDPOINT
ARG MQTT_BROKER_URL

ENV API_ENDPOINT=${API_ENDPOINT}
ENV MQTT_BROKER_URL=${MQTT_BROKER_URL}

CMD ["node", "app.js"]
