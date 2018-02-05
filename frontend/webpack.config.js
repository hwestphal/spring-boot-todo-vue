const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
    entry: './src/main/typescript/main.ts',
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: 'ts-loader'
            },
            {
                test: /\.css$/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: 'css-loader?sourceMap'
                })
            }, {
                test: /\.(woff|woff2|eot|ttf|svg)$/,
                use: {
                    loader: 'file-loader',
                    options: {
                        outputPath: 'fonts/',
                        name: '[name].[ext]',
                        publicPath: '../'
                    }
                }
            }]
    },
    plugins: [
        new ExtractTextPlugin('css/styles.css')
    ],
    resolve: {
        extensions: ['.js', '.ts'],
        alias: {
            vue: 'vue/dist/vue.esm.js'
        }
    },
    output: {
        filename: 'js/main.js',
        path: path.resolve(__dirname, 'target/classes/static'),
        library: 'Main'
    },
    devtool: 'source-map'
};
