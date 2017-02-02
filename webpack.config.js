'use strict';

const path = require('path');
const WebpackUglifyJsPlugin = require('webpack-uglify-js-plugin');

const defaultConfig =  {
    entry: {
        '/js/ShoppingList': path.resolve(__dirname + '/src/main/js/ShoppingList.js'),
        '/ServiceWorkers': path.resolve(__dirname + '/src/main/js/serviceworkers/cache.sw.js')
    },
    output: {
        path: path.resolve(__dirname + '/target/generated-resources/public'),
        filename: '[name].js'
    },
    devtool: 'source-map',
    context: __dirname,
    module: {
        loaders: [
            {
                test: /js$/,
                include: path.resolve (__dirname, 'src', 'main', 'js'),
                loaders: ['babel']
            }
        ]
    },
    plugins: []
};

const minimizedConfig = {
    entry: {
        ShoppingList: path.resolve(__dirname + '/src/main/js/ShoppingList.js')
    },
    output: {
        path: path.resolve(__dirname + '/target/generated-resources/public/js'),
        filename: '[name].min.js'
    },
    context: __dirname,
    module: {
        loaders: [
            {
                test: /js$/,
                include: path.resolve (__dirname, 'src', 'main', 'js'),
                loaders: ['ng-annotate', 'babel']
            }
        ]
    },
    plugins: [new WebpackUglifyJsPlugin({
        cacheFolder: path.resolve(__dirname, 'public/cached_uglify/'),
        minimize: true,
        sourceMap: false,
        output: {
            comments: false
        },
        compressor: {
            warnings: false
        }
    })]
};

module.exports =[minimizedConfig, defaultConfig];