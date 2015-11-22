FROM node:5.0.0
MAINTAINER Brian Hurlow <brian@brianhurlow.com> (@bhurlow)
ADD . /app
WORKDIR /app
ENV DEBUG debug,info
RUN npm install
