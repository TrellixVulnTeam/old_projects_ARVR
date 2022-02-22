module.exports = (sequelize, Sequelize) => {
    const Data = sequelize.define('data', {
        code: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        department: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        visitTime: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        firstName: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        lastName: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        email: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        idCode: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        birthDate: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        gender: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        },
        age: {
            type: Sequelize.STRING,
            allowNull: false,
            unique: "user_entrie"
        }
    })

    return Data
}