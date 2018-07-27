import express from "express";

export interface IServer {
    readonly port: number;
    close(): Promise<void>;
}

export default function(...paths: string[]) {
    const app = express();
    app.use(paths.map((p) => express.static(p)));
    return new Promise<IServer>((resolve) => {
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
}
