ARG VARIANT=16-bullseye
FROM mcr.microsoft.com/vscode/devcontainers/javascript-node:0-${VARIANT}

RUN yarn install
RUN yarn add express cors pg pg-hstore sequelize

EXPOSE 5000