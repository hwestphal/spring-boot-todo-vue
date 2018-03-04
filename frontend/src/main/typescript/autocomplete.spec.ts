import { mount } from "@vue/test-utils";
import Awesomplete = require("awesomplete");
import Autocomplete from "./autocomplete.vue";

function autocomplete(value?: string) {
    return mount(Autocomplete, {
        attachToDocument: true,
        propsData: {
            value,
        },
    });
}

describe("autocomplete", () => {
    it("emits input event on @input", () => {
        const value = "a value";
        const wrapper = autocomplete(value);
        wrapper.find("input").trigger("input");
        expect(wrapper.emitted().input[0]).toEqual([value]);
    });

    it("emits enter event on @keyup.enter", () => {
        const wrapper = autocomplete();
        wrapper.find("input").trigger("keyup.enter");
        expect(wrapper.emitted().enter).toHaveLength(1);
    });

    it("updates awesomplete list", () => {
        const spy = jest.spyOn(Awesomplete.prototype, "list", "set");
        const wrapper = autocomplete();
        expect(spy).toHaveBeenCalledWith([]);
        const list = ["entry"];
        wrapper.setProps({ list });
        expect(spy).toHaveBeenCalledWith(list);
    });

    it("calls update and enter on awesomplete event", () => {
        const wrapper = autocomplete();
        const vm = wrapper.vm;
        const update = jest.spyOn(vm, "update");
        const enter = jest.spyOn(vm, "enter");
        wrapper.trigger("awesomplete-selectcomplete");
        expect(update).toHaveBeenCalled();
        expect(enter).toHaveBeenCalled();
    });
});
