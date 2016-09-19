import {inject} from "aurelia-dependency-injection";
import {DialogController} from "aurelia-dialog";

/**
 * Created by mariusz on 19.09.16.
 */

@inject(DialogController)
export class ReportModal {
    message:string;
    private dialogController:DialogController;

    constructor(dialogController:DialogController) {
        this.dialogController = dialogController;
        dialogController.settings.centerHorizontalOnly = true;
    }

    activate(message) {
        this.message = message;
    }

}