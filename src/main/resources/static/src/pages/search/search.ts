import User = Models.User;
import Tag = Models.Tag;
import {IUserService, UserService} from "../../service/userService";
import {ITagService, TagService} from "../../service/tagService";
import {inject} from "aurelia-dependency-injection";
import {Const} from "../../domain/const";
/**
 * Created by mariusz on 14.12.16.
 */

@inject(UserService, TagService)
export class SearchVM {

    inputText:string;
    selected:string;
    tagResult:Tag[];
    userResult:User[];
    private tagPage:number;
    private userPage:number;
    private options:string[];
    private userService:IUserService;
    private tagService:ITagService;
    private lastQueryText:string;

    constructor(userService:IUserService, tagService:ITagService) {
        this.options = ["Tag", "User"];
        this.selected = this.options[0];
        this.tagPage = 0;
        this.userPage = 0;
        this.userService = userService;
        this.tagService = tagService;
    }

    async query() {
        if (this.selected === "Tag") {
            this.tagPage = 0;
            this.tagResult = await this.tagService.findTagsByText(this.inputText, this.tagPage, Const.PAGE_SIZE);
        } else {
            this.userPage = 0;
            this.userResult = await this.userService.findUsersByUsername(this.inputText, this.userPage, Const.PAGE_SIZE);
        }
        this.lastQueryText = this.inputText;
    }

    async nextTagPage() {
        try {
            this.tagPage = ++this.tagPage;
            let nextPage = await this.tagService.findTagsByText(this.lastQueryText, this.tagPage, Const.PAGE_SIZE);
            this.tagResult = this.tagResult.concat(nextPage);
        } catch (error) {
            this.tagPage = --this.tagPage;
        }
    }

    async nextUserPage() {
        try {
            this.userPage = ++this.userPage;
            let nextPage = await this.userService.findUsersByUsername(this.lastQueryText, this.userPage, Const.PAGE_SIZE);
            this.userResult = this.userResult.concat(nextPage);
        } catch (error) {
            this.userPage = --this.userPage;
        }
    }

}