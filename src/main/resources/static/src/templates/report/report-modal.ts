import {inject} from "aurelia-dependency-injection";
import {DialogController} from "aurelia-dialog";
import {Const} from "../../domain/const";

/**
 * Created by mariusz on 19.09.16.
 */

@inject(DialogController)
export class ReportModal {
    message:string;
    maxLength:number;
    private dialogController:DialogController;
    private categories:Object[];
    private selectedCategory:Object;

    constructor(dialogController:DialogController) {
        this.dialogController = dialogController;
        this.categories = [
            {id: 0, text: "Verbal abuse"},
            {id: 1, text: "Hate speech"},
            {id: 2, text: "Pornography"},
            {id: 3, text: "Advertisement"},
            {id: 4, text: "Spam or flood"},
            {id: 5, text: "Wrong tags"},
            {id: 6, text: "Other"}
        ];
        this.selectedCategory = this.categories[0];
        dialogController.settings.centerHorizontalOnly = true;
    }

    activate() {
        this.message = '';
        this.maxLength = Const.REPORT_MESSAGE_MAX_LENGTH;
    }

}