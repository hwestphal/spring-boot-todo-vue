import path from "path";

export default (...ps: string[]) => path.normalize(path.join(__dirname, "..", "..", "..", ...ps));
