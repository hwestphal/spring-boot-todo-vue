import Vue from "vue";
import { reactive } from "./reactive";

describe("reactive decorator", () => {
    it("initializes class correctly", () => {
        @reactive
        class TC {
            a = 1;
            constructor(public b: number) { }
        }
        expect(TC.name).toBe("TC");
        const c = new TC(2);
        expect(c.a).toBe(1);
        expect(c.b).toBe(2);
    });

    it("preserves full interface", () => {
        const m5 = jest.fn();
        const m5super = jest.fn();
        class B {
            e = 5;
            private d = 4;

            constructor(public a: number) { }

            get dp() {
                return this.d;
            }

            set dp(v: number) {
                this.d = v;
            }

            m2() {
                this.d += 1;
            }

            protected m3() {
                this.e += 1;
            }

            protected m5() {
                m5super();
            }
        }
        @reactive
        class C extends B {
            b = 2;
            private c = 3;

            get cp() {
                return this.c;
            }

            set cp(v: number) {
                this.c = v;
            }

            m1() {
                this.c += 1;
            }

            m4() {
                this.m3();
            }

            m5() {
                super.m5();
                m5();
            }
        }
        const c = new C(1);
        expect(c.a).toBe(1);
        expect(c.b).toBe(2);
        expect(c.cp).toBe(3);
        expect(c.dp).toBe(4);
        expect(c.e).toBe(5);
        c.cp = 10;
        c.m1();
        expect(c.cp).toBe(11);
        c.dp = 20;
        c.m2();
        expect(c.dp).toBe(21);
        c.m4();
        expect(c.e).toBe(6);
        c.m5();
        expect(m5).toBeCalled();
        expect(m5super).toBeCalled();
    });

    it("makes class reactive", async () => {
        @reactive
        class C {
            a = { v: 1 };
            constructor(public b: any) { }
        }
        const obj = { v: 2 };
        const c = new C(obj);
        const watchA = jest.fn();
        const watchB = jest.fn();
        const vm = new Vue({
            inject: {
                c: {
                    default: c,
                },
            },
            watch: {
                "c.a.v": watchA,
                "c.b.v": watchB,
            },
        });
        c.a.v = 3;
        c.b.v = 4;
        await vm.$nextTick();
        expect(watchA).toBeCalledWith(3, 1);
        expect(watchB).not.toBeCalled();
    });

    it("supports reactive delegation", async () => {
        @reactive
        class B {
            a = 1;
        }
        @reactive
        class C {
            constructor(private b: B) { }
            get a() {
                return this.b.a;
            }
        }
        const i = new B();
        const c = new C(i);
        const watcher = jest.fn();
        const vm = new Vue({
            inject: {
                c: {
                    default: c,
                },
            },
            watch: {
                "c.a": watcher,
            },
        });
        i.a = 2;
        await vm.$nextTick();
        expect(watcher).toBeCalledWith(2, 1);
    });

    it("rejects reactive super class", () => {
        @reactive
        class Base { }
        expect(() => {
            @reactive
            class C extends Base { }
        }).toThrow(TypeError);
    });

    it("uses extended Vue constructor", () => {
        const m = jest.fn();
        const vueConstructor = Vue.extend({
            mixins: [
                {
                    methods: {
                        mixinMethod: m,
                    },
                },
            ],
        });
        @reactive({ vueConstructor })
        class C { }
        const c = new C();
        (c as any).mixinMethod();
        expect(m).toBeCalled();
    });
});
