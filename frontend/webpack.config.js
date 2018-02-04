const path = require('path');

module.exports = {
    entry: './src/ts/main.ts',
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: 'ts-loader'
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.ts'],
        alias: {
            vue: 'vue/dist/vue.esm.js'
        }
    },
    output: {
        filename: 'main.js',
        path: path.resolve(__dirname, 'target/classes/static/js'),
        library: 'Main'
    },
    devtool: 'source-map'
};
