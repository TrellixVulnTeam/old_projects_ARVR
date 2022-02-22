FROM node:lts-alpine

WORKDIR /server

COPY package*.json ./

RUN yarn install

COPY . .

EXPOSE 5000
CMD ["yarn", "start"]
