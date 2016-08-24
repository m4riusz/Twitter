/**
 * Created by mariusz on 22.08.16.
 */

import {AuthService} from "./authService";
import {Aurelia} from "aurelia-framework";
import {Const} from "./const";

export function configure(aurelia :Aurelia) {
    aurelia.use.standardConfiguration();

    aurelia.start().then(() => {
        var auth = aurelia.container.get(AuthService);
        let root = auth.isAuthenticated() ? Const.APP_ROOT : Const.UNAUTHORIZE_ROOT;
        aurelia.setRoot(root);
    });
}