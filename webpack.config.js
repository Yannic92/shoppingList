'use strict';

const path = require('path');

module.exports = {
    entry: {
        ShoppingList: path.resolve(__dirname + '/src/main/js/ShoppingList.js')
    },
    output: {
        path: path.resolve(__dirname + '/target/generated-resources/public/js'),
        filename: '[name].js'
    },
    devtool: 'source-map',
    module: {
        loaders: [
            {
                test: /js$/,
                include: path.resolve (__dirname, 'src', 'main', 'js'),
                loader: 'babel'
            }
        ]
    },
    plugins: [
    ]
};