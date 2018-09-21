const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const { VueLoaderPlugin } = require("vue-loader");

module.exports = function(env, args = {}) {
    const prod = args.mode === "production";
    return {
        devtool: prod ? "source-map" : "eval-source-map",
        entry: {
            main: "./src/main/typescript/main.ts",
            polyfills: "./src/main/typescript/polyfills.ts",
        },
        mode: "development",
        module: {
            rules: [{
                exclude: /node_modules/,
                test: /\.(j|t)s$/,
                use: {
                    loader: "ts-loader",
                    options: {
                        appendTsSuffixTo: [/\.vue$/],
                        transpileOnly: true,
                    },
                },
            }, {
                test: /\.vue$/,
                use: "vue-loader",
            }, {
                oneOf: [
                    {
                        resourceQuery: /module/,
                        use: [
                            prod ? MiniCssExtractPlugin.loader : "vue-style-loader",
                            {
                                loader: "css-loader",
                                options: {
                                    localIdentName: "[local]_[hash:base64:8]",
                                    modules: true,
                                    sourceMap: true,
                                },
                            },
                            {
                                loader: "sass-loader",
                                options: {
                                    sourceMap: true,
                                },
                            },
                        ],
                    },
                    {
                        use: [
                            prod ? MiniCssExtractPlugin.loader : "vue-style-loader",
                            {
                                loader: "css-loader",
                                options: {
                                    sourceMap: true,
                                },
                            },
                            {
                                loader: "sass-loader",
                                options: {
                                    sourceMap: true,
                                },
                            },
                        ],
                    },
                ],
                test: /\.s?css$/,
            }, {
                test: /\.(woff|woff2|eot|ttf)$/,
                use: {
                    loader: "file-loader",
                    options: {
                        name: "[name].[ext]",
                        outputPath: "fonts/",
                        publicPath: "../fonts",
                    },
                },
            }],
        },
        output: {
            filename: "js/[name].js",
            library: "__[name]",
            path: path.resolve(__dirname, "target/classes/static"),
        },
        plugins: [
            new VueLoaderPlugin(),
            new MiniCssExtractPlugin({
                filename: "css/[name].css",
            }),
        ],
        resolve: {
            "alias": {
                "@Generated": path.resolve(__dirname, "target/generated-sources"),
                "portable-fetch": "empty-module",
            },
            extensions: [".js", ".ts"],
        },
    };
};
