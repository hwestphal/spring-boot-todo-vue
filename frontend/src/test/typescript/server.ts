import express from "express";
import { AddressInfo } from "net";

export interface IServer {
    readonly port: number;
    close(): Promise<void>;
}

export default function(...paths: string[]) {
    const app = express();
    app.use(paths.map((p) => express.static(p)));
    // ignore missing favicon
    app.get("/favicon.ico", (req, res) => res.send(""));
    return new Promise<IServer>((resolve) => {
        const server = app.listen(0, () => {
            resolve({
                port: (server.address() as AddressInfo).port,
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
