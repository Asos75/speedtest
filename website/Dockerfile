FROM node:18-alpine

WORKDIR /app

COPY package*.json ./

COPY ./public ./public
COPY ./src ./src

ARG REACT_APP_BACKEND_URL
ARG REACT_APP_FAST_SPEEDTEST_API_TOKEN
ARG REACT_APP_IPINFO_TOKEN

ENV REACT_APP_BACKEND_URL $REACT_APP_BACKEND_URL
ENV REACT_APP_FAST_SPEEDTEST_API_TOKEN $REACT_APP_FAST_SPEEDTEST_API_TOKEN
ENV REACT_APP_IPINFO_TOKEN $REACT_APP_IPINFO_TOKEN

RUN npm install --legacy-peer-deps

EXPOSE 3000

CMD [ "npm", "start" ]