import {inject} from "aurelia-framework";
import {AuthService, IAuthService} from "../../../service/authService";
/**
 * Created by mariusz on 07.01.17.
 */

@inject(AuthService)
export class VerifyVM {

    message: {text: string, type: "SUCCESS"|"FAIL"};
    private authService: IAuthService;

    constructor(authService: IAuthService) {
        this.authService = authService;
        this.message = {text: "", type: "SUCCESS"};
    }

    async activate(params, config) {
        try {
            this.message.type = "SUCCESS";
            const returned = await this.authService.verify(params.key);
            this.message.text = returned.message;
        } catch (error) {
            this.message.type = "FAIL";
            this.message.text = error;
        }
    }
}