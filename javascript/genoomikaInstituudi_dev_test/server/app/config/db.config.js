module.exports = {
    HOST: 'db',
    USER: 'postgres',
    PASSWORD: 'postgres',
    DB: 'postgres',
    dialect: 'postgres',
    pool: {
        max: 5,
        mix: 0,
        acquire: 30000,
        idle: 10000
    },
    logging: false
}