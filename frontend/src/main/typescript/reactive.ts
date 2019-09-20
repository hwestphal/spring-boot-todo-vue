import Vue, { VueConstructor } from "vue";

type Constructable<T> = new (...args: any[]) => T;

interface IDecoratorOptions {
    vueConstructor?: VueConstructor;
}

class ReactiveBase { }

function decorate<T extends object>(
    ctor: Constructable<T>,
    { vueConstructor = Vue }: IDecoratorOptions = {}): Constructable<T> {

    // determine methods and computed properties (i.e. getter/setter)
    const methods: any = {};
    const computed: any = {};

    let proto = ctor.prototype;
    while (proto && proto !== Object.prototype) {
        if (proto instanceof ReactiveBase) {
            throw new TypeError("A reactive class must not have a reactive super class");
        }
        for (const key of Object.getOwnPropertyNames(proto)) {
            if (key !== "constructor" && !(key in methods || key in computed)) {
                const pd = Object.getOwnPropertyDescriptor(proto, key)!;
                if (pd.value) {
                    methods[key] = pd.value;
                } else {
                    computed[key] = {
                        get: pd.get,
                        set: pd.set,
                    };
                }
            }
        }
        proto = Object.getPrototypeOf(proto);
    }

    const name = ctor.name || /* istanbul ignore next */ ctor.toString().match(/^function\s*([^\s(]+)/)![1];

    class C extends ReactiveBase {
        constructor(...args: any[]) {
            super();

            // determine names of reactive (i.e. data) properties
            // all properties which are initialized when calling the default constructor are considered reactive
            // all others are considered to be not reactive (i.e. being constructor injected)
            const initObj = new ctor();
            const reactiveProps: string[] = [];
            for (const key in initObj) {
                if (initObj.hasOwnProperty(key) && initObj[key] !== undefined) {
                    reactiveProps.push(key);
                }
            }

            const data: any = {};
            const inject: any = {};
            const obj = new ctor(...args);
            for (const key in obj) {
                /* istanbul ignore else */
                if (obj.hasOwnProperty(key)) {
                    if (reactiveProps.indexOf(key) > -1) {
                        data[key] = obj[key];
                    } else {
                        inject[key] = {
                            default: obj[key],
                        };
                    }
                }
            }

            // return a Vue instance with the same interface as the original class
            return new vueConstructor({
                computed,
                data,
                inject,
                methods,
                name,
            });
        }
    }
    Object.defineProperty(C, "name", { value: name });

    return C as any;
}

type ClassDecorator = <T extends object>(ctor: Constructable<T>) => Constructable<T>;
export function reactive(options: IDecoratorOptions): ClassDecorator;
export function reactive<T>(ctor: Constructable<T>): Constructable<T>;
export function reactive<T extends object>(arg: IDecoratorOptions | Constructable<T>):
    ClassDecorator | Constructable<T> {
    if (typeof arg === "function") {
        return decorate(arg);
    }
    return (ctor) => decorate(ctor, arg);
}
