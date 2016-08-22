
/**
 * Created by mariusz on 22.08.16.
 */
///<reference path="../typings/modules/aurelia-framework/index.d.ts"/>


import {Aurelia} from "aurelia-framework";

export function configure(aurelia :Aurelia) {
    aurelia.use.standardConfiguration();
    aurelia.start().then(a => a.setRoot());
}