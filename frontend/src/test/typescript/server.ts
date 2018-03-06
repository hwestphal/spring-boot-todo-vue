import express from "express";

export interface IServer {
    readonly port: number;
    close(): Promise<void>;
}

export default (...paths: string[]): Promise<IServer> => {
    const app = express();
    paths.forEach((p) => {
        app.use(express.static(p));
    });
    return new Promise((resolve) => {
        const server = app.listen(0, () => {
            resolve({
                port: server.address().port,
                close() {
                    return new Promise((res) => {
                        server.close(() => {
                            res();
                        });
                    });
                },
            });
        });
    });
};
