const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
    entry: {
        main: './src/main/typescript/main.ts'
    },
    module: {
        rules: [{
            test: /\.ts$/,
            use: {
                loader: 'ts-loader',
                options: {
                    appendTsSuffixTo: [/\.vue$/]
                }
            }
        }, {
            test: /\.vue$/,
            use: {
                loader: 'vue-loader',
                options: {
                    loaders: {
                        css: ExtractTextPlugin.extract({
                            use: 'css-loader?sourceMap',
                            fallback: 'vue-style-loader'
                        }),
                        scss: ExtractTextPlugin.extract({
                            use: ['css-loader?sourceMap', 'sass-loader?sourceMap'],
                            fallback: 'vue-style-loader'
                        })
                    }
                }
            }
        }, {
            test: /\.s?css$/,
            use: ExtractTextPlugin.extract({
                use: ['css-loader?sourceMap', 'sass-loader?sourceMap'],
                fallback: 'style-loader'
            })
        }, {
            test: /\.(woff|woff2|eot|ttf)$/,
            use: {
                loader: 'file-loader',
                options: {
                    outputPath: 'fonts/',
                    name: '[name].[ext]',
                    publicPath: '../fonts'
                }
            }
        }]
    },
    plugins: [
        new ExtractTextPlugin('css/[name].css')
    ],
    resolve: {
        extensions: ['.js', '.ts'],
    },
    output: {
        filename: 'js/[name].js',
        path: path.resolve(__dirname, 'target/classes/static'),
        library: '__[name]'
    },
    devtool: 'source-map'
};
