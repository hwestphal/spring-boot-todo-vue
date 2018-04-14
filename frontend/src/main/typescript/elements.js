// import only used components to reduce code size
// this is written is ES5 Javascript, so no Babel configuration is needed

require("element-ui/packages/theme-chalk/src/button.scss");
exports.Button = require("element-ui/lib/button").default;

require("element-ui/packages/theme-chalk/src/button-group.scss");
exports.ButtonGroup = require("element-ui/lib/button-group").default;

require("element-ui/packages/theme-chalk/src/col.scss");
exports.Col = require("element-ui/lib/col").default;

require("element-ui/packages/theme-chalk/src/input.scss");

require("element-ui/packages/theme-chalk/src/row.scss");
exports.Row = require("element-ui/lib/row").default;
