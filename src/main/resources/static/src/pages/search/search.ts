import User = Models.User;
import Tag = Models.Tag;
/**
 * Created by mariusz on 14.12.16.
 */


export class SearchVM {

    inputText:string;
    selected:string;
    result:User[]|Tag[];
    private options:string[];

    constructor() {
        this.options = ["Tag", "User"];
        this.selected = this.options[0];
    }

    query() {
        console.log("query");
        console.log(this.selected);
    }
}