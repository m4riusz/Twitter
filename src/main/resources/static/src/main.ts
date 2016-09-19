/**
 * Created by mariusz on 22.08.16.
 */

import {Aurelia} from "aurelia-framework";
import {Const} from "./domain/const";
import {Login} from "./pages/unauthorized/login/login";

export function configure(aurelia :Aurelia) {
    aurelia.use.standardConfiguration();
    aurelia.use.plugin('aurelia-dialog');
    aurelia.start().then(() => {
        let auth = aurelia.container.get(Login);
        auth.isAuthenticated()
            .then((authenticated) => {
                let root = authenticated ? Const.APP_ROOT : `pages/unauthorized/${Const.UNAUTHORIZE_ROOT}`;
                aurelia.setRoot(root);
            });

    });
}