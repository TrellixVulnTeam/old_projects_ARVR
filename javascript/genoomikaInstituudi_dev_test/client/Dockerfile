FROM node:lts-alpine

RUN yarn global add http-server

WORKDIR /client

COPY package*.json ./

RUN yarn install

COPY . .

RUN yarn build

EXPOSE 8000

CMD ["http-server", "dist", "-p", "8000", "--proxy", "http://localhost:8000?", "dist"]
