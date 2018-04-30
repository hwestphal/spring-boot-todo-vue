const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const { VueLoaderPlugin } = require('vue-loader');

module.exports = function (env, args = {}) {
    const prod = args.mode === 'production';
    return {
        mode: 'development',
        devtool: prod ? 'source-map' : 'eval-source-map',
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
                use: 'vue-loader'
            }, {
                test: /\.s?css$/,
                oneOf: [
                    {
                        resourceQuery: /module/,
                        use: [
                            prod ? MiniCssExtractPlugin.loader : 'vue-style-loader',
                            {
                                loader: 'css-loader',
                                options: {
                                    modules: true,
                                    localIdentName: '[local]_[hash:base64:8]',
                                    sourceMap: true
                                }
                            },
                            {
                                loader: 'sass-loader',
                                options: {
                                    sourceMap: true
                                }
                            }
                        ]
                    },
                    {
                        use: [
                            prod ? MiniCssExtractPlugin.loader : 'vue-style-loader',
                            {
                                loader: 'css-loader',
                                options: {
                                    sourceMap: true
                                }
                            },
                            {
                                loader: 'sass-loader',
                                options: {
                                    sourceMap: true
                                }
                            }
                        ]
                    }
                ]
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
            new VueLoaderPlugin(),
            new MiniCssExtractPlugin({
                filename: 'css/[name].css'
            })
        ],
        resolve: {
            extensions: ['.js', '.ts'],
        },
        output: {
            filename: 'js/[name].js',
            path: path.resolve(__dirname, 'target/classes/static'),
            library: '__[name]'
        }
    };
}
