const dbConfig = require('../config/db.config')
const Sequelize = require('sequelize')

const sequelize = new Sequelize(dbConfig.DB, dbConfig.USER, dbConfig.PASSWORD, {
    host: dbConfig.HOST,
    dialect: dbConfig.dialect,
    operatorsAliases: false,

    pool: {
        max: dbConfig.pool.max,
        min: dbConfig.pool.min,
        // acquire: dbConfig.pool.acquire,
        // idle: dbConfig.pool.idle
    },
    logging: dbConfig.logging
})

const db = {}

db.Sequelize = Sequelize
db.sequelize = sequelize

db.data = require('./data.model.js')(sequelize, Sequelize)

module.exports = db